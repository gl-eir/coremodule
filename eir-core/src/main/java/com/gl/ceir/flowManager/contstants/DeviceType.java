package com.gl.ceir.flowManager.contstants;

public enum DeviceType {

    Handheld(0, "Handheld"),
    Mobile_Phone_Feature_phone(1, "Mobile Phone/Feature phone"),
    Mobile_Test_Platform(2, "Mobile Test Platform"),
    Vehicle(3, "Vehicle"),
    Portable_include_PDA(4, "Portable(include PDA)"),
    Module(5, "Module"),
    IoT_Device(6, "IoT Device"),
    WLAN_Router(7, "WLAN Router"),
    Modem(8, "Modem"),
    Tablet(9, "Tablet"),
    Smartphone(10, "Smartphone"),
    Connected_Computer(11, "Connected Computer"),
    e_Book(12, "e-Book"),

    Wearable(13, "Wearable"),

    Device_for_Automatic_Processing_Data_APD(14, "Device for the Automatic Processing of Data (APD)"),
    Others(100, "Others");


    int code;
    String name;

    DeviceType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public static DeviceType findByName(String name) {
        for (DeviceType deviceType : DeviceType.values()) {
            if (deviceType.getName().equalsIgnoreCase(name))
                return deviceType;
        }
        return DeviceType.Others;
    }
}
