package depth.main.wishwesee.domain.map.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "지도 검색 응답 DTO")
public class MapLocationRes {
    @Schema(description = "장소명", example = "명지대학교", type = "string")
    private String location;

    @Schema(description = "주소", example = "서울특별시 서대문구 명지대길 116", type = "string")
    private String address;

    @Schema(description = "지도링크", example = "https://map.naver.com/v5/search/명지대학교", type = "string")
    private String mapLink;

    @Schema(description = "위도", example = "37.582218", type = "number")
    private double latitude;

    @Schema(description = "경도", example = "127.001739", type = "number")
    private double longitude;


    @Builder
    MapLocationRes(String location, String address, String mapLink, double latitude, double longitude){
        this.location = location;
        this.address = address;
        this.mapLink = mapLink;
        this.latitude = latitude;
        this.longitude = longitude;
    }


}
