package com.ishang.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserVO {
    private Integer userId;
    private String mobile;
    private String password;
    private String token;
}
