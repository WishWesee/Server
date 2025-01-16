package depth.main.wishwesee.domain.map.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "지도 검색 응답 DTO")
public class MapLocationRes {
    @Schema(description = "장소명")
    private String location;

    @Schema(description = "주소")
    private String address;

    @Schema(description = "지도링크")
    private String mapLink;

    @Schema(description = "위도")
    private double latitude;

    @Schema(description = "경도")
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
