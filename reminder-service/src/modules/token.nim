import 
    jwt, json, tables, jester, logging,
    ../commons/[settings]

proc verify*(token: string): bool =
    try:
        let jwtToken = token.toJWT()
        {.cast(gcsafe).}:
            result = jwtToken.verify(settings.SECRET, HS256)
    except InvalidToken:
        result = false

proc getClaimString*(token: JWT, key: string, default = ""): string =
    result = default
    try:
        result = $token.claims[key].node.str
    except:
        {.cast(gcsafe).}:
            settings.LOGGER.log lvlWarn, "Failed to get claim \"", key, "\""

export toJWT
