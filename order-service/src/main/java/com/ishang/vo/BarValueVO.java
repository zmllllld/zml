package com.ishang.vo;

import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Map;

@Data
public class BarValueVO {
    private Integer value;
    private Map<String,String> itemStyle;

}
