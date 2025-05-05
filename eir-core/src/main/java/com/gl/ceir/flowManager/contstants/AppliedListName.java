package com.gl.ceir.flowManager.contstants;

public enum AppliedListName {

    BLockedlist(0),
    Trackedlist(1),
    Exceptionlist(2),
    Blockedtaclist(3),
    Allowedtaclist(4),
    NA(5);


    int code;
    AppliedListName(int code){
        this.code = code;
    }

    public int getCode(){
        return this.code;
    }
}
