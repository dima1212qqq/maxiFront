package ru.dovakun.dovapay.service;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dovakun.dovapay.model.Category;
import ru.dovakun.dovapay.model.Product;
import ru.dovakun.dovapay.repo.FeatureRepo;
import ru.dovakun.dovapay.repo.ProductRepo;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepo productRepo;
    private final FeatureRepo featureRepo;

    public ProductService(ProductRepo productRepo, FeatureRepo featureRepo) {
        this.productRepo = productRepo;
        this.featureRepo = featureRepo;
    }

    public List<Product> findByCategoryIsNull() {
        return productRepo.findByCategoryIsNull();
    }

    public void save(Product product) {
        productRepo.save(product);
    }

    @Transactional
    public void delete(Product product) {
        product.setIsActive(false);
        productRepo.save(product);
    }

    public List<Product> findByCategory(Category category) {
        return productRepo.findByCategory(category);
    }
}
