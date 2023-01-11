import
    std / [logging, httpclient, json, times, strtabs, strformat],
    jester,
    ../commons/[settings]

type
    LogType* = enum
        INFO = 1,
        WARNING = 2,
        ERROR = 3

    LogContent[T] = ref object of RootObj
        time: string
        service: string
        `type`: int
        host: string
        user: string
        `method`: string
        path: string
        code: int
        requestBody: string
        response: T

var logger = settings.LOGGER

proc newClient*(targetUrl: string): AsyncHttpClient =
    result = newAsyncHttpClient(
        useragent="Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 RuxitSynthetic/1.0 v3561250663517519541 t6557865812607695948 ath5ee645e0 altpriv cvcv=2 smf=0",
        headers=newHttpHeaders(@[("Content-Type", "application/json")])
    )

proc newLogContent*[T](`type`: LogType, path: string, `method`: HttpMethod, code: HttpCode, host = "", user = "", requestBody = "", response: T = ""): LogContent[T] =
    new result
    result.time = $now()
    result.service = settings.SERVICE_NAME
    result.`type` = `type`.int
    result.host = host
    result.user = user
    if user == "":
        result.user = "guest"
    result.`method` = $`method`
    result.path = path
    result.code = code.int
    result.requestBody = requestBody
    result.response = response


func toLevel*(`type`: LogType): Level =
    case `type`:
        of WARNING: lvlWarn
        of ERROR: lvlError
        of INFO: lvlInfo

func toLogType*(level: Level): LogType = 
    case level:
        of lvlWarn: WARNING
        of lvlError: ERROR
        else: INFO

proc logRequest*[T](level: Level, req: Request, httpCode: HttpCode, user: string, response: T) {.async.} =
    {.cast(gcsafe).}:
        var url = settings.LOG_URL
    if unlikely(url == ""):
        {.cast(gcsafe).}:
            logger.log lvlError,("Log Request: Cannot log, LOG_URL is not set!")
        return
    var client = newClient(url)
    var content = newLogContent(level.toLogType, req.path, req.reqMethod, httpCode, req.host, user, req.body, response)
    var reqFut = client.request(url, HttpPost, $ %content)
    yield reqFut
    if reqFut.failed:
        var err = reqFut.readError
        {.cast(gcsafe).}:
            logger.log lvlError,(&"Log Request: Failed to send log to {url}. {err.msg}")
    client.close()
    
    var respCode = reqFut.read.code.int
    {.cast(gcsafe).}:
        logger.log lvlDebug,(&"HTTP Logger: {content.`method`} {content.path} {content.host} {content.user} {content.code} -> {respCode}")
