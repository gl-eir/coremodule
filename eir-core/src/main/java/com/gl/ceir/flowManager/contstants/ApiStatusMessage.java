package com.gl.ceir.flowManager.contstants;

public enum ApiStatusMessage {

    added(201, "Added Successfully"),
    duplicate(202, "Duplicate Data"),
    deleted(203, "Deleted Successfully"),
    notFound(204, "Not Found"),
    notValidRequest(205, "Not a valid request"),
    notDeleted(206, "Not Able To Delete"),
    notAdded(207, "Not able to Add"),
    updated(208, "Updated Successfully"),
    notUpdated(208, "Not Updated");

    int code;
    String msg;

    ApiStatusMessage(int code, String name) {
        this.code = code;
        this.msg = name;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
