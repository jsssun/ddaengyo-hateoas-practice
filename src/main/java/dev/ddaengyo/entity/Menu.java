package dev.ddaengyo.entity;

import dev.ddaengyo.menu.dto.MenuRequest;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Menu extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    private Boolean popularity;

    @Transactional
    public Menu updateMenu(@Valid MenuRequest request) {
        this.category = request.getCategory();
        this.name = request.getName();
        this.price = request.getPrice();
        this.popularity = request.getPopularity();
        return this;
    }
}