package dev.ddaengyo.menu.dto;

import dev.ddaengyo.entity.Menu;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuResponse {

    private Long menuId;
    private Long storeId;
    private String category;
    private String name;
    private Integer price;
    private Boolean popularity;

    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .menuId(menu.getMenuId())
                .storeId(menu.getStore().getStoreId())
                .category(menu.getCategory())
                .name(menu.getName())
                .price(menu.getPrice())
                .popularity(menu.getPopularity())
                .build();
    }
}
