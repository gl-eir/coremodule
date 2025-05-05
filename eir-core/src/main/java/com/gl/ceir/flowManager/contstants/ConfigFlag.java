package com.gl.ceir.flowManager.contstants;

public enum ConfigFlag {
    eir_workflow_global,
    eir_devicetype_global,
    eir_blockedlist,
    eir_trackedlist,
    eir_exceptionlist,
    eir_blockedtaclist,
    eir_allowedtaclist,
    eir_foreign_imsi_check_enable,
    eir_null_imei_response_status,

    eir_imei_length_response_status,
    eir_imei_not_numeric_response_status,
    eir_imei_all_zeros_response_status,

    eir_imei_all_check_pass_response_status,

    eir_tac_not_in_gsma_response,
    eir_enable_imei_length_validation,
    eir_enable_imei_allzero_validation,
    eir_enable_imei_numeric_validation,

    // Device Types
    Handheld,
    Mobile_Phone_Feature_phone,
    Mobile_Test_Platform,
    Vehicle,
    Portable_include_PDA,
    Module,
    IoT_Device,
    WLAN_Router,
    Modem,
    Tablet,
    Smartphone,
    Connected_Computer,
    e_Book,
    Wearable,
    Device_for_Automatic_Processing_Data_APD,
    Others;
}
