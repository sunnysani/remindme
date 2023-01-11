import 
    jester,
    json,
    norm/[model, postgres],
    std/[macros, options, strutils, strformat, sugar, logging, httpcore, marshal, asyncdispatch, times , nativesockets],

    commons/[settings],
    modules/[db, token, cron, httpLogger]
import nimpy

type 
    #[  
        Override RouteError from Jester to access exc and data
        Apparently when naming this to original "RouteError", 
        `errorHandler` failed to be registered to Jester.
    ]#
    MyRouteError = object
        case kind: RouteErrorKind
        of RouteException:
            exc: ref Exception
        of RouteCode:
            data: ResponseData

    DataDTO*[T] = object
        data*: T

    MessageDTO* = object
        message*: string
    
    GetUserFnType* = proc(token: string): string

var logger = settings.LOGGER
var grcpClient {.threadvar.}: PyObject

func newBaseDTO*[T](data: T): DataDTO[T] =
    result.data = data

func newMessageDTO*(message: string): MessageDTO =
    result.message = message

proc getToken(request: Request): string =
    var bearerToken = request.headers.getOrDefault("Authorization").string
    removePrefix(bearerToken, "Bearer ")
    return bearerToken


proc getUsername(request: Request): string =
    var token = request.getToken()
    try:
        if grcpClient.isNil:
            var pySys = pyImport("sys")
            pySys.path = settings.PYTHON_PATH
            grcpClient = pyImport("client")
        result = grcpClient.getUsername(token).to(string)
    except Exception:
        {.cast(gcsafe).}:
            logger.log(lvlError, getCurrentExceptionMsg())
    result = token.toJWT.getClaimString "username"


#[
    Macro for sending json response.
    Must be used in jester routes.
]#
template jsonResp[T](httpCode: HttpCode, data: T) =
    var rr = $$data
    discard httpLogger.logRequest(lvlInfo, request, httpCode, request.getUsername, rr)
    jester.resp httpCode, rr, "application/json"

template resp(httpCode: HttpCode) =
    asyncCheck httpLogger.logRequest(lvlInfo, request, httpCode, request.getUsername, "")
    jester.resp httpCode

#[
    Macro to check Content-Type header from `type`.
    "Unsupported Media-Type" is sent if assertion fails.
    Must be used in jester routes.
]#
template assertContentType(`type`: string) =
    var found = false
    for val in seq[string](headers(request).getOrDefault("Content-Type")):
        if val.toLowerAscii.contains(`type`.toLowerAscii):
            found = true
        break
    if not found:
        resp Http415


#[
  Error handler to override Jester's defaultErrorFilter and wrap
  responses in BaseResponse.
]#
proc errorHandler(request: Request, err: RouteError): Future[ResponseData] {.async.} =
    var error = cast[MyRouteError](err) # Prevent compiler crying
    case error.kind:
    of RouteException:
        var traceback = getStackTrace(error.exc)
        var errorMsg = error.exc.msg
        if errorMsg.len == 0: errorMsg = "(empty)"
        asyncCheck httpLogger.logRequest(lvlError, request, HTTP500, request.getUsername, errorMsg)
        {.cast(gcsafe).}:
            logger.log(lvlError, traceback & errorMsg)
        return (
            TCActionSend, 
            Http500, 
            some(@({"Content-Type": "application/json"})), 
            $$newMessageDTO("Internal Server Error"), 
            true
        )
    of RouteCode:
        if error.data.matched:
            return error.data
        asyncCheck httpLogger.logRequest(lvlInfo, request, error.data.code, request.getUsername, error.data.content)
        return (
            TCActionSend, 
            error.data.code, 
            some(@({"Content-Type": "application/json"})), 
            $$newMessageDTO(($error.data.code)[4 .. ^1]),
            true
        )


# Jester's routing declaration
router myrouter:



    get "/api/reminder/all":
        # Get Reminders
        var reminders = @[newReminder()]
        var username = request.getUsername
        try:
            withDb:
                db.select(reminders, "username = $1", username)
        except NotFoundError:
            jsonResp Http200, newSeq[Reminder]()
        except ValueError:
            resp Http400
        jsonResp Http200, newDataDTO reminders



    get "/api/reminder/@id":
        # Get Reminder
        var reminder = newReminder()
        try:
            var id = parseInt(@"id").int64
            withDb:
                db.select(reminder, "id = $1", id)
        except NotFoundError:
            var msg = fmt"""Reminder not with id {@"id"} found"""
            jsonResp Http404, newMessageDto msg
        except ValueError:
            var msg = fmt"""Cannot parse id {@"id"}"""
            jsonResp Http400, newMessageDTO msg

        # Check if authorized
        var username = request.getUsername
        if reminder.username != username:
            resp Http403

        jsonResp Http200, reminder



    post "/api/reminder":
        assertContentType "application/json"

        # Parse DTO
        var reminder: Reminder
        try:
            reminder = request.body.to[:ReminderDTO].toReminder    
        except TimeParseError:
            jsonResp Http400, newMessageDTO "Unknown time format"
        except:
            resp Http400

        # Set username
        reminder.username = request.getUsername

        # Save reminder
        {.cast(gcsafe).}: # Cheeky hack
            withDb:
                db.insert(reminder)

        #Create cron
        var cron = newCron reminder
        {.cast(gcsafe).}: # Cheeky hack
            withDb:
                db.insert(cron)

        asyncCheck tryDispatch cron

        jsonResp Http201, reminder



    put "/api/reminder/@id":
        assertContentType "application/json"

        # Get Reminder
        var reminder = newReminder()
        try:
            let id = parseInt(@"id").int64
            withDb:
                db.select(reminder, "id = $1", id)
        except NotFoundError:
            jsonResp Http404, newMessageDto "Reminder not found"
        except ValueError:
            var msg = fmt"""Cannot parse id {@"id"}"""
            jsonResp Http400, newMessageDTO msg
            
        # Parse DTO
        var reminderInput: Reminder
        try:
            reminderInput = request.body.to[:ReminderDTO].toReminder
        except TimeParseError:
            jsonResp Http400, newMessageDTO "Unknown time format"
        except:
            resp Http400

        # Check if authorized
        var username = request.getUsername
        if reminder.username != username:
            resp Http403
            
        reminder.activity_name = reminderInput.activity_name
        reminder.remind_time = reminderInput.remind_time
        {.cast(gcsafe).}: # Cheeky hack
            withDb:
                db.insert(reminder)

        var cron = @[newCron reminder]
        try:
            withDb:
                db.selectOneToMany reminder, cron, "reminder"
        except NotFoundError:
            {.cast(gcsafe).}: 
                withDb:
                    db.insert(cron[0])
            
        asyncCheck tryDispatch cron[0]

        jsonResp Http200, reminder



    delete "/api/reminder/@id":

        # Get reminder
        var cron = newCron()
        try:
            let id = parseInt(@"id").int
            withDb:
                db.select(cron, "reminder.id = $1", id)
        except NotFoundError:
            let msg = fmt"""Reminder not with id {@"id"} found"""
            jsonResp Http404, newMessageDto msg
        except ValueError:
            resp Http400

        # Check if authorized
        var username = request.getUsername
        if cron.reminder.username != username:
            resp Http403
        
        var reminder = cron.reminder
        {.cast(gcsafe).}:
            withDb:
                db.delete(cron)
                db.delete(reminder)
        resp Http204



proc jwtFilter(request: Request): Future[ResponseData] {.gcsafe.} = 
    var resultResp = (
        TCActionSend, 
        Http200, 
        some(@({"Content-Type": "application/json"})), 
        "",
        true
    )
    var user = ""
    block check:

        var bearerToken = string request.headers.getOrDefault("Authorization")
        if bearerToken == "":
            resultResp[1] = Http401
            resultResp[3] = $$newMessageDTO "No authentication token provided"
            break check

        bearerToken.removePrefix "Bearer "

        try:
            if not bearerToken.verify():
                resultResp[1] = Http401
                resultResp[3] = $$newMessageDTO "Invalid token"
                break check 
        except:
            resultResp[1] = Http401
            resultResp[3] = $$newMessageDTO "Token malformed"
            break check
        

        try:
            var jwt = bearerToken.toJWT()
            user = jwt.claims["username"].node.str
            var exp = jwt.claims["exp"].node.num
            if exp < getTime().toUnix:
                resultResp[1] = Http401
                resultResp[3] = $$newMessageDTO "Token expired"
                break check
        except:
            {.cast(gcsafe).}:
                logger.log(lvlWarn, getCurrentExceptionMsg())
            resultResp[1] = Http401
            resultResp[3] = $$newMessageDTO "Invalid token"
            break check

        resultResp[4] = false

    if resultResp[4]:
        asyncCheck httpLogger.logRequest(lvlInfo, request, resultResp[1], user, resultResp[3])

    new result
    result.complete resultResp


# Entrypoint if run as standalone binary
proc main(port: int = settings.PORT) =
    db.init()
    cron.init()

    let jesterSetting = newSettings nativesockets.Port(port)
    # jesterSetting.numThreads = numThreads
    var jester = initJester jesterSetting
    jester.register jwtFilter
    jester.register myrouter
    jester.register errorHandler
    jester.serve()
  

when(isMainModule):
    
    import cligen; dispatch(main) 
