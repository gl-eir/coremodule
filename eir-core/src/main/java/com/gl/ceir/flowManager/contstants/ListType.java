package com.gl.ceir.flowManager.contstants;

public enum ListType {
    TRACKED_LIST("tracked_list.csv"), BLOCKED_LIST("blocked_list.csv"),
    EXCEPTION_LIST("exception_list.csv"),
    BLOCKED_TAC("blocked_tac.csv"),
    ALLOWED_TAC("allowed_tac.csv");
    private String filename;

    ListType(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return this.filename;
    }
}
