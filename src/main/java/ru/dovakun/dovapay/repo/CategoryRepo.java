package ru.dovakun.dovapay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.dovakun.dovapay.model.Category;

import java.util.List;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
    @Query("select c from Category c where c.parent is null and c.isActive = true")
    List<Category> findByParentIsNull();

    @Query("select c from Category c where c.parent.id = :id")
    List<Category> findByParent(@Param("id") Long id);

    List<Category> findByParent(Category parent);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Category c where c.id = :id")
    void deleteByCategory(Long id);
}
