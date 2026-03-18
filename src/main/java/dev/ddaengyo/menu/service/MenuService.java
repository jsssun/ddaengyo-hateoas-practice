package dev.ddaengyo.menu.service;

import dev.ddaengyo.entity.Menu;
import dev.ddaengyo.menu.dto.MenuRequest;
import dev.ddaengyo.menu.dto.MenuResponse;
import dev.ddaengyo.menu.respository.MenuRepository;
import dev.ddaengyo.repository.StoreRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    public List<MenuResponse> getMenus(Long storeId) {
        return menuRepository.findAllByStore_StoreId(storeId)
                .stream()
                .map(MenuResponse::from)
                .toList();
    }

    public MenuResponse getMenu(Long storeId, Long menuId) {
        Menu menu = menuRepository.findByMenuIdAndStore_StoreId(menuId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));

        return MenuResponse.from(menu);
    }

    @Transactional
    public MenuResponse createMenu(Long storeId, MenuRequest request) {
        var store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        Menu menu = Menu.builder()
                .store(store)
                .category(request.getCategory())
                .name(request.getName())
                .price(request.getPrice())
                .popularity(request.getPopularity())
                .build();

        Menu saved = menuRepository.save(menu);
        return MenuResponse.from(saved);
    }

    public MenuResponse updateMenu(Long storeId, Long menuId, @Valid MenuRequest request) {
        Menu menu = menuRepository.findByMenuIdAndStore_StoreId(menuId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));

        Menu updated = menu.updateMenu(request);

        return MenuResponse.from(updated);
    }

    public void deleteMenu(Long storeId, Long menuId) {
        menuRepository.findByMenuIdAndStore_StoreId(menuId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));

        menuRepository.deleteByMenuIdAndStore_StoreId(menuId, storeId);
    }
}
