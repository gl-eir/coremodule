package com.gl.ceir.flowManager.contstants;

public enum StatusValue {

    whitelist(0, "WHITELISTED"),
    blacklist(1,"BLACKLISTED"),
    greylist(2, "GREYLISTED"),
    unknown(7,"NA");



    int code;
    String name;
    StatusValue(int code, String name){
        this.code = code;
        this.name = name;
    }

    public int getCode(){
        return this.code;
    }
    public String getName() { return this.name; }
}
