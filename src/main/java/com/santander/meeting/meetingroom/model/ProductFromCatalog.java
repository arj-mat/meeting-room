package com.santander.microsservicos.shopcart.external.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductFromCatalog {
    private Long id;
    private String name;
    private String category;
    private String description;
    private String thumbnail;
    private List<String> images;
    private float price;
    private boolean available;
}
