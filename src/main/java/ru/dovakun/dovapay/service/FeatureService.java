package ru.dovakun.dovapay.service;

import org.springframework.stereotype.Service;
import ru.dovakun.dovapay.model.Feature;
import ru.dovakun.dovapay.model.Product;
import ru.dovakun.dovapay.repo.FeatureRepo;

import java.util.List;

@Service
public class FeatureService {
    private final FeatureRepo featureRepo;

    public FeatureService(FeatureRepo featureRepo) {
        this.featureRepo = featureRepo;
    }
    public List<Feature> getFeaturesByProduct(Product product) {
        return featureRepo.findByProduct(product);
    }
    public void save(Feature feature) {
        featureRepo.save(feature);
    }
    public void delete(Feature feature) {
        feature.setIsActive(false);
        featureRepo.save(feature);
    }
}
