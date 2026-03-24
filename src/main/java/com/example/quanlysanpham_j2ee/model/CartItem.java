package com.example.quanlysanpham_j2ee.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private int productId;
    private String name;
    private long price;
    private int quantity;
}
