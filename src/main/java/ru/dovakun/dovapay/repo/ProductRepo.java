package ru.dovakun.dovapay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.dovakun.dovapay.model.Category;
import ru.dovakun.dovapay.model.Product;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    @Query("select p from Product p where p.category is null and p.isActive = true")
    List<Product> findByCategoryIsNull();

    List<Product> findByCategory(Category category);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Product p where p.id = :id")
    void deleteByProduct(Long id);
}
