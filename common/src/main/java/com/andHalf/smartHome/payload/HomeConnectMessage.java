package com.andHalf.smartHome.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeConnectMessage {

    String clientId;
    String piId;
    String messageId;
    Date messageGenerationTime = new Date();
    Action action;
    Object commandPayLoad;


}
