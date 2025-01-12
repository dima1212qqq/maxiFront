package ru.dovakun.dovapay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.dovakun.dovapay.model.Feature;
import ru.dovakun.dovapay.model.Product;

import java.util.List;

@Repository
public interface FeatureRepo extends JpaRepository<Feature, Long> {
    @Query("select f from Feature f where f.product = :product and f.isActive = true")
    List<Feature> findByProduct(Product product);

    void deleteByProduct(Product product);
}
