package ru.dovakun.dovapay.model;

import lombok.Data;

@Data
public class ProductCartDto {
    private Product product;
    private Feature feature;
}
