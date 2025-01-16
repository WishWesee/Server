package depth.main.wishwesee.domain.map.service;

import depth.main.wishwesee.domain.map.dto.response.MapLocationRes;
import lombok.RequiredArgsConstructor;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapService {

    @Value("${naver.local.client-id}")
    private String localClientId;

    @Value("${naver.local.client-secret}")
    private String localClientSecret;

    @Value("${naver.local.api-url}")
    private String localApiUrl;

    public ResponseEntity<?> searchPlaces(String name) {
        try {
            List<MapLocationRes> places = new ArrayList<>();
            String encodedQuery = java.net.URLEncoder.encode(name, "UTF-8");
            String apiUrlWithParams = localApiUrl + "?query=" + encodedQuery + "&display=5&start=1";

            // API 요청
            URL url = new URL(apiUrlWithParams);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Naver-Client-Id", localClientId);
            connection.setRequestProperty("X-Naver-Client-Secret", localClientSecret);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Local API 호출 실패 - 응답 코드: " + responseCode);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray itemsArray = jsonResponse.getJSONArray("items");

            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject place = itemsArray.getJSONObject(i);

                // HTML 태그 제거
                String location = place.getString("title").replaceAll("<.*?>", "");
                String address = place.getString("roadAddress");

                // 위도와 경도 변환
                double latitude = Double.parseDouble(place.getString("mapy")) / 1e6;
                double longitude = Double.parseDouble(place.getString("mapx")) / 1e6;

                // 주소 기반 네이버 지도 링크 생성
                String encodedAddress = java.net.URLEncoder.encode(address, "UTF-8");
                String mapLink = "https://map.naver.com/v5/search/" + encodedAddress;

                places.add(MapLocationRes.builder()
                        .location(location)
                        .address(address)
                        .mapLink(mapLink)
                        .latitude(latitude)
                        .longitude(longitude)
                        .build());
            }
            return ResponseEntity.ok(places);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("장소 검색 중 오류 발생: " + e.getMessage());
        }
    }
}