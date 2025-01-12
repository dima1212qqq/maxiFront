package ru.dovakun.dovapay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cascade;

import java.util.List;

@Entity
@Data
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Category extends BaseGoods{
    private String name;
    private String imageUrl;
    @ManyToOne(fetch = FetchType.EAGER)
    private Category parent = null;
    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Product> products = null;

    private Boolean isActive = true;

}


