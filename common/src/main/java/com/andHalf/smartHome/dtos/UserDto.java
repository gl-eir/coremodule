package com.andHalf.smartHome.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    private String userId;

    private String emailId;

    private String firstName;

    private String lastName;

    private String status;

    private String mobileNo;
}
