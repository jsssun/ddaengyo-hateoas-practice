package dev.ddaengyo.menu.controller;

import dev.ddaengyo.menu.dto.MenuRequest;
import dev.ddaengyo.menu.dto.MenuResponse;
import dev.ddaengyo.menu.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/store/{storeId}/menu")
@RequiredArgsConstructor
@Tag(name = "Menu", description = "메뉴 API")
public class MenuController {

    private final MenuService menuService;

    /// ### 메뉴 목록 조회
    /// 가게의 전체 메뉴를 조회합니다.
    ///
    /// - 인증 없이 조회 가능
    /// - `200` 조회 성공
    /// - `404` 가게를 찾을 수 없음
    ///
    /// @param storeId 가게 ID
    /// @return 메뉴 목록 (HATEOAS)
    @Operation(summary = "메뉴 목록 조회", description = "가게의 전체 메뉴를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<MenuResponse>>> getMenus(
            @Parameter(description = "가게 ID") @PathVariable Long storeId) {

        List<EntityModel<MenuResponse>> menus = menuService.getMenus(storeId)
                .stream()
                .map(EntityModel::of)
                .toList();

        CollectionModel<EntityModel<MenuResponse>> model = CollectionModel.of(menus,
                linkTo(methodOn(MenuController.class).getMenus(storeId)).withSelfRel(),
                Link.of("/api/store/" + storeId + "/menu/{menuId}").withRel("menu")
        );

        return ResponseEntity.ok(model);
    }

    /// ### 메뉴 단건 조회
    /// 특정 메뉴 하나를 조회합니다.
    ///
    /// - 인증 없이 조회 가능
    /// - `200` 조회 성공
    /// - `404` 메뉴를 찾을 수 없음
    ///
    /// @param storeId 가게 ID
    /// @param menuId  메뉴 ID
    /// @return 메뉴 단건 (HATEOAS)
    @Operation(summary = "메뉴 단건 조회", description = "특정 메뉴를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    @GetMapping("/{menuId}")
    public ResponseEntity<EntityModel<MenuResponse>> getMenu(
            @Parameter(description = "가게 ID") @PathVariable Long storeId,
            @Parameter(description = "메뉴 ID") @PathVariable Long menuId) {

        MenuResponse response = menuService.getMenu(storeId, menuId);

        EntityModel<MenuResponse> model = EntityModel.of(response,
                linkTo(methodOn(MenuController.class).getMenu(storeId, menuId)).withSelfRel(),
                linkTo(methodOn(MenuController.class).getMenus(storeId)).withRel("menus")
        );

        return ResponseEntity.ok(model);
    }

    /// ### 메뉴 등록
    /// 새로운 메뉴를 등록합니다.
    ///
    /// - `OWNER` 권한 필요
    /// - `201` 등록 성공
    /// - `403` 권한 없음
    ///
    /// @param storeId 가게 ID
    /// @param request 메뉴 등록 요청 DTO
    /// @return 등록된 메뉴 (HATEOAS)
    @Operation(summary = "메뉴 등록", description = "OWNER만 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ResponseEntity<EntityModel<MenuResponse>> createMenu(
            @Parameter(description = "가게 ID") @PathVariable Long storeId,
            @RequestBody @Valid MenuRequest request) {

        MenuResponse response = menuService.createMenu(storeId, request);

        EntityModel<MenuResponse> model = EntityModel.of(response,
                linkTo(methodOn(MenuController.class).getMenu(storeId, response.getMenuId())).withSelfRel(),
                linkTo(methodOn(MenuController.class).getMenus(storeId)).withRel("menus")
        );

        return ResponseEntity
                .created(URI.create("/api/store/" + storeId + "/menu/" + response.getMenuId()))
                .body(model);
    }

    /// ### 메뉴 수정
    /// 기존 메뉴 정보를 수정합니다.
    ///
    /// - `OWNER` 권한 필요
    /// - `200` 수정 성공
    /// - `403` 권한 없음
    /// - `404` 메뉴를 찾을 수 없음
    ///
    /// @param storeId 가게 ID
    /// @param menuId  메뉴 ID
    /// @param request 메뉴 수정 요청 DTO
    /// @return 수정된 메뉴 (HATEOAS)
    @Operation(summary = "메뉴 수정", description = "OWNER만 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{menuId}")
    public ResponseEntity<EntityModel<MenuResponse>> updateMenu(
            @Parameter(description = "가게 ID") @PathVariable Long storeId,
            @Parameter(description = "메뉴 ID") @PathVariable Long menuId,
            @RequestBody @Valid MenuRequest request) {

        MenuResponse response = menuService.updateMenu(storeId, menuId, request);

        EntityModel<MenuResponse> model = EntityModel.of(response,
                linkTo(methodOn(MenuController.class).getMenu(storeId, menuId)).withSelfRel(),
                linkTo(methodOn(MenuController.class).getMenus(storeId)).withRel("menus")
        );

        return ResponseEntity.ok(model);
    }

    /// ### 메뉴 삭제
    /// 메뉴를 삭제합니다.
    ///
    /// - `OWNER` 권한 필요
    /// - `204` 삭제 성공
    /// - `403` 권한 없음
    /// - `404` 메뉴를 찾을 수 없음
    ///
    /// @param storeId 가게 ID
    /// @param menuId  메뉴 ID
    @Operation(summary = "메뉴 삭제", description = "OWNER만 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{menuId}")
    public ResponseEntity<Void> deleteMenu(
            @Parameter(description = "가게 ID") @PathVariable Long storeId,
            @Parameter(description = "메뉴 ID") @PathVariable Long menuId) {
        menuService.deleteMenu(storeId, menuId);
        return ResponseEntity.noContent().build();
    }
}