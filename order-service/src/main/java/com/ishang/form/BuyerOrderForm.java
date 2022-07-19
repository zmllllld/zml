package com.ishang.form;


import lombok.Data;

import java.util.List;

@Data
public class BuyerOrderForm {
    private String name;
    private String phone;
    private String address;
    private Integer id;
    private List<BuyerOrderDetailForm> items;
}
