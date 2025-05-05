package com.andHalf.smartHome.payload;

import lombok.Data;

import java.util.List;

@Data
public class GetStateRequest {

    List<String> pinNos;
    String state;
}
