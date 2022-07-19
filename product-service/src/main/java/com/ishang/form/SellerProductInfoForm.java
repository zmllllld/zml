package com.ishang.form;


import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SellerProductInfoForm {
    private Integer categoryType;
    private String productDescription;
    private String productIcon;
    private String productName;
    private BigDecimal productPrice;
    private Integer productStatus;
    private Integer productStock;
}
