import 
    std / [times, asyncdispatch, sets, logging, net, marshal, strformat],
    norm / [postgres],
    .. / commons / [settings],
    db, stomp

const REFRESH_DURATION = initDuration(milliseconds=settings.REFRESH_INTERVAL_MS)

var logger = settings.LOGGER

var stompClient = newStompClient(newSocket(), settings.RABBIT_STOMP)
var workerChannel: Channel[db.Cron]
var shouldProcess = true
var worker: Thread[void]
var threadDispatchFn: proc (cron: db.Cron) {.async.}

proc workerProc() {.thread.} =
    workerChannel.open()
    var dispatchedReminders = initHashSet[int64]()
    const MAX_BEAT = initDuration(seconds=settings.MAX_BEAT)
    const ZERO = initDuration(0)

    proc reminderProc(c: db.Cron) {.async.} =
        var cron = c
        var targetTime = cron.reminder.remind_time.toTime
        
        while true:
            var currentTime = now().toTime
            var nextBeat = min(targetTime - currentTime, MAX_BEAT)

            if nextBeat < ZERO:
                {.cast(gcsafe).}:
                    stompClient.send("/queue/" & cron.reminder.username, $$c.reminder.toNotificationDTO, headers = @[
                        ("durable","false"),
                        ("exclusive", "false"),
                        ("auto-delete", "true")
                    ])
                logger.log lvlInfo, &"Reminder {cron.id} sent to {cron.reminder.username} for time {targetTime} at ", now()
                dispatchedReminders.excl cron.id
                var reminder = cron.reminder
                withDb:
                    db.delete(cron)
                    db.delete(reminder)
                break
            
            await sleepAsync nextBeat.inMilliseconds.int
    
    proc tryDispatch(cron: db.Cron) {.async.} =
        var targetTime = cron.reminder.remind_time
        var nextDispatch = (now() + REFRESH_DURATION)

        if nextDispatch < targetTime:
            logger.log lvlDebug, &"CRON: Won't dispatch cron {cron.id}, will dispatch next cycle"
            return

        if dispatchedReminders.contains cron.id:
            logger.log lvlDebug, &"CRON: Cron {cron.id} already dispatched"
            return

        logger.log lvlDebug, &"CRON: Dispatched cron {cron.id}"
        dispatchedReminders.incl cron.id
        asyncCheck reminderProc cron

    {.cast(gcsafe).}:
        threadDispatchFn = tryDispatch

    while shouldProcess:
        waitFor sleepAsync 30000

    workerChannel.close()
createThread(worker, workerProc)

proc tryDispatch*(cron: db.Cron) {.async.} =
    {.cast(gcsafe).}:
        logger.log lvlDebug,("Dispatching cron ", cron.id)
        if threadDispatchFn != nil:
            await threadDispatchFn(cron)

proc dispatcher() {.async.} =
    while shouldProcess:
        logger.log lvlDebug,("Another dispatcher loop at ", now())
        var crons = @[newCron()]
        withDb:
            db.select(crons, "reminder.remind_time <= $1", now() + REFRESH_DURATION)
        for cron in crons:
            asyncCheck tryDispatch cron
        await sleepAsync settings.REFRESH_INTERVAL_MS

proc init*() =
    logger.log lvlInfo,("Connecting to RabbitMQ")
    stompClient.connect

    logger.log lvlInfo,("Initializing cron")
    asyncCheck dispatcher()