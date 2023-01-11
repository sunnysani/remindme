import 
    norm/[model, postgres],
    std/[times, marshal, logging],

    .. / commons / [settings]

settings.LOGGER.log lvlInfo, "Initializing db"

type
    Reminder* = ref object of Model
        activity_name*: string
        remind_time*: DateTime
        username*: string

    Cron* = ref object of Model
        reminder*: Reminder
        dispatched*: bool
        dismissed*: bool

    ReminderDTO* = object
        activity_name*: string
        remind_time*: string
        username*: string
        id*: int64

    NotificationDTO* = object
        `type`: string
        notification_time: string
        is_read: bool
        name: string

    DataDto*[T] = object
        data*: T

proc newReminder*(activityName: string = "", remindTime: DateTime = now(), username: string = ""): Reminder =
    Reminder(activity_name: activityName, remind_time: remindTime, username: username)

proc newCron*(reminder: Reminder = newReminder()): Cron =
    Cron(reminder: reminder, dispatched: false, dismissed: false)

proc newDataDTO*[T](data: T): DataDto[T] =
    result.data = data

proc toReminder*(dto: ReminderDTO): Reminder =
    new result
    result.activity_name = dto.activity_name
    try:
        result.remind_time = parse(dto.remind_time, "yyyy-MM-dd'T'HH:mm:ss'.'fff'Z'", utc())
    except TimeParseError:
        result.remind_time = parse(dto.remind_time, "yyyy-MM-dd'T'HH:mm:sszzz")
    result.username = dto.username

proc toReminderDTO*(reminder: Reminder): ReminderDTO = 
    ReminderDTO(
        activity_name: reminder.activity_name,
        remind_time: $reminder.remind_time.toTime,
        username: reminder.username,
        id: reminder.id
    )

proc toNotificationDTO*(reminder: Reminder): NotificationDTO =
    NotificationDTO(
        `type`: "Reminder",
        notification_time: $reminder.remind_time.toTime,
        is_read: false,
        name: reminder.activity_name
    )

proc `$$`*(x: Reminder): string =
    return $$x.toReminderDTO

proc `$$`*(x: seq[Reminder]): string =
    var arr = newSeq[ReminderDTO](len(x))
    for i, r in arr:
        arr[i] = x[i].toReminderDTO
    return $$arr

proc `$$`*(x: DataDto[seq[Reminder]]): string =
    var arr = newSeq[ReminderDTO](len(x.data))
    for i, r in arr:
        arr[i] = x.data[i].toReminderDTO
    return $$newDataDTO(arr)

proc init*() =
    withDb:
        db.createTables(Cron(reminder: Reminder()))

