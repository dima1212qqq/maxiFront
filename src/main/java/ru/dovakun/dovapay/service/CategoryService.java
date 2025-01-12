package ru.dovakun.dovapay.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dovakun.dovapay.model.Category;
import ru.dovakun.dovapay.model.Feature;
import ru.dovakun.dovapay.model.Product;
import ru.dovakun.dovapay.repo.CategoryRepo;
import ru.dovakun.dovapay.repo.FeatureRepo;
import ru.dovakun.dovapay.repo.ProductRepo;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;
    private final FeatureRepo featureRepo;

    public CategoryService(CategoryRepo categoryRepo, ProductRepo productRepo, FeatureRepo featureRepo) {
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
        this.featureRepo = featureRepo;
    }
    public List<Category> findByParentId(Long parentId) {
        return categoryRepo.findByParent(parentId);
    }
    public List<Category> findCategoryWhereParentIsNull(){
        return categoryRepo.findByParentIsNull();
    }
    public void save(Category category){
        categoryRepo.saveAndFlush(category);
    }
    @Transactional
    public void delete(Category category) {
        category.setIsActive(false);
        categoryRepo.save(category);
    }
}
