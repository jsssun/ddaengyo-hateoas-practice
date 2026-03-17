package dev.ddaengyo.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "메뉴 등록/수정 요청 DTO")
public class MenuRequest {

    @NotBlank(message = "카테고리는 필수입니다.")
    @Schema(description = "메뉴 카테고리", example = "후라이드")
    private String category;

    @NotBlank(message = "메뉴명은 필수입니다.")
    @Schema(description = "메뉴명", example = "황금올리브 후라이드")
    private String name;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    @Schema(description = "가격", example = "18000")
    private Integer price;

    @Schema(description = "인기 메뉴 여부", example = "true")
    private Boolean popularity;
}