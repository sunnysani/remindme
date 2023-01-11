package com.lawtk.lawtkschedulerbe.util;

import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

public abstract class AbstractEntity<T extends AbstractEntity, ID> implements Persistable<ID> {
    @Transient
    boolean isNew;

    protected AbstractEntity(){

    }

    protected AbstractEntity(boolean isNew){
        this.isNew = isNew;
    }

    @Override
    public boolean isNew(){
        return isNew;
    }
}
