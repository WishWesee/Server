package depth.main.wishwesee.domain.map.controller;
import depth.main.wishwesee.domain.map.dto.response.MapLocationRes;
import depth.main.wishwesee.domain.map.service.MapService;
import depth.main.wishwesee.global.payload.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MapSearch", description = "MapSearch API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/map")
public class MapController {
    private final MapService mapService;
    @Operation(summary = "장소 검색", description = "지정된 이름으로 장소를 검색하고, 해당 장소의 이름, 주소, 지도 링크, 위도 및 경도를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MapLocationRes.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @Parameter(description = "검색할 장소 이름", example = "명지대학교") @RequestParam String name
    ) {
        return mapService.searchPlaces(name);
    }
}