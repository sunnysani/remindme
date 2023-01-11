# Package

version       = "0.1.0"
author        = "i.gede97@ui.ac.id"
description   = "RemindMe! Reminder Service"
license       = "MIT"
srcDir        = "src"
bin           = @["reminder"]
binDir        = "bin"


# Dependencies

requires "nim >= 1.6.6"
requires "jester"
requires "norm"
requires "dotenv"
requires "jwt"
requires "cligen"
requires "stomp"
requires "nimpy"

task buildProd, "Build for production":
    exec("nimble -d:release build")

task buildDebug, "Build for production":
    exec("nimble -d:debug -d:normDebug --debuginfo:on build")

task runProd, "Run in production mode":
    exec("nimble -d:release run")

task runDebug, "Run in debug mode":
    exec("nimble -d:debug -d:normDebug --debuginfo:on run")