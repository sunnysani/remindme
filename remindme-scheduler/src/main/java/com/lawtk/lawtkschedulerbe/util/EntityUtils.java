package com.lawtk.lawtkschedulerbe.util;

public interface EntityUtils {
    static <T extends AbstractEntity> T persistable(T abstractEntity, boolean isNew){
        abstractEntity.isNew = isNew;
        return abstractEntity;
    }

}
