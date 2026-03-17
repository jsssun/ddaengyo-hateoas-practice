package dev.ddaengyo.menu.respository;

import dev.ddaengyo.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    /// 가게 ID로 전체 메뉴 조회
    List<Menu> findAllByStore_StoreId(Long storeId);

    /// 가게 ID + 메뉴 ID로 단건 조회 (다른 가게 메뉴 접근 방지)
    Optional<Menu> findByMenuIdAndStore_StoreId(Long menuId, Long storeId);

    /// 가게 ID + 메뉴 ID로 메뉴 삭제
    void deleteByMenuIdAndStore_StoreId(Long menuId, Long storeId);
}
