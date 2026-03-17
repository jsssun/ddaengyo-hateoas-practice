package dev.ddaengyo.repository;

import dev.ddaengyo.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByStoreStoreId(Long storeId);  // store.storeId로 탐색
}