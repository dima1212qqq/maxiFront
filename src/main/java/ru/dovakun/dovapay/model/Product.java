package ru.dovakun.dovapay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Product extends BaseGoods{
    private String name;
    private String imageUrl;
    @ManyToOne
    private Category category;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Feature> features;
    private Boolean isActive = true;
}

