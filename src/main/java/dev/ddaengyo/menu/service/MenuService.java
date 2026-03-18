package dev.ddaengyo.menu.service;

import dev.ddaengyo.entity.Menu;
import dev.ddaengyo.menu.controller.MenuController;
import dev.ddaengyo.menu.dto.MenuRequest;
import dev.ddaengyo.menu.dto.MenuResponse;
import dev.ddaengyo.menu.respository.MenuRepository;
import dev.ddaengyo.repository.StoreRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    public CollectionModel<EntityModel<MenuResponse>> getMenus(Long storeId) {
        List<EntityModel<MenuResponse>> menus = menuRepository.findAllByStore_StoreId(storeId)
                .stream()
                .map(menu -> {
                    MenuResponse response = MenuResponse.from(menu);
                    return EntityModel.of(response);
                })
                .toList();

        return CollectionModel.of(menus,
                linkTo(methodOn(MenuController.class).getMenus(storeId)).withSelfRel(),
                Link.of("/api/store/" + storeId + "/menu/{menuId}").withRel("menu"));
    }

    public EntityModel<MenuResponse> getMenu(Long storeId, Long menuId) {
        Menu menu = menuRepository.findByMenuIdAndStore_StoreId(menuId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));

        MenuResponse response = MenuResponse.from(menu);

        return EntityModel.of(response,
                linkTo(methodOn(MenuController.class).getMenu(storeId, menuId)).withSelfRel(),
                linkTo(methodOn(MenuController.class).getMenus(storeId)).withRel("menus")
        );
    }

    @Transactional
    public EntityModel<MenuResponse> createMenu(Long storeId, MenuRequest request) {
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
        MenuResponse response = MenuResponse.from(saved);

        return EntityModel.of(response,
                linkTo(methodOn(MenuController.class).getMenu(storeId, saved.getMenuId())).withSelfRel(),
                linkTo(methodOn(MenuController.class).getMenus(storeId)).withRel("menus")
        );
    }

    public EntityModel<MenuResponse> updateMenu(Long storeId, Long menuId, @Valid MenuRequest request) {
        Menu menu = menuRepository.findByMenuIdAndStore_StoreId(menuId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));

        Menu updated = menu.updateMenu(request);
        MenuResponse response = MenuResponse.from(updated);

        return EntityModel.of(response,
                linkTo(methodOn(MenuController.class).getMenu(storeId, updated.getMenuId())).withSelfRel(),
                linkTo(methodOn(MenuController.class).getMenus(storeId)).withRel("menus")
        );

    }

    public void deleteMenu(Long storeId, Long menuId) {
        Menu menu = menuRepository.findByMenuIdAndStore_StoreId(menuId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));

        menuRepository.deleteByMenuIdAndStore_StoreId(menuId, storeId);
    }
}
