import
    std / [os, uri, logging, strutils],
    dotenv

from norm/postgres import dbHostEnv, dbUserEnv, dbPassEnv, dbNameEnv

let LOG_FILTER* = when defined(release): lvlInfo else: lvlDebug
setLogFilter(LOG_FILTER)
let LOGGER* = logging.newConsoleLogger(LOG_FILTER)

const SERVICE_NAME* = "reminder-service"
const REFRESH_INTERVAL_MS* = 20 * 60 * 60 * 1000
const MAX_BEAT* = 5 * 60

if fileExists(".env"):
    dotenv.load()

var SECRET*  = getEnv("JWT_SECRET", "django-insecure-ym2(qz)(f0dx#v49-w-7+%h3r@m1%07hhze93n*7znl4f5!q(h")
var LOG_URL* = getEnv("LOG_URL")
var RABBIT_STOMP* = getEnv("RABBIT_STOMP")
var PORT*    = 8080
try: PORT = getEnv("PORT").parseInt
except: discard

var DB_HOST* = getEnv("DATABASE_HOST", "localhost:5432")
var DB_USER* = getEnv("DATABASE_USER", "postgres")
var DB_PASS* = getEnv("DATABASE_PASS", "postgres")
var DB_NAME* = getEnv("DATABASE_NAME", "postgres")
var DB_URL*  = getEnv("DATABASE_URL")
if DB_URL != "":
    try:
        var u = parseUri(DB_URL)
        DB_HOST = u.hostname & ":" & u.port
        DB_USER = u.username
        DB_PASS = u.password
        DB_NAME = u.path[1..^1]
    except:
        LOGGER.log(lvlError, "Cannot parse DATABASE_URL")

putEnv(dbHostEnv, DB_HOST)
putEnv(dbUserEnv, DB_USER)
putEnv(dbPassEnv, DB_PASS)
putEnv(dbNameEnv, DB_NAME)

const PYTHON_PATH* = @["", "/usr/lib/python38.zip", "/usr/lib/python3.8", "/usr/lib/python3.8/lib-dynload", "/home/ubuntu/.local/lib/python3.8/site-packages", "/usr/local/lib/python3.8/dist-packages", "/usr/lib/python3/dist-packages"]