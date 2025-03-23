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
import java.text.Collator;
import java.util.*;

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
            // 입력값 검증
            if (name == null || name.trim().length() < 2 || name.matches(".*[^a-zA-Z0-9가-힣\\s].*")) {
                return ResponseEntity.badRequest().body("올바른 검색어를 입력하세요.");
            }

            // 1차 검색 (원본 입력)
            List<MapLocationRes> result = searchFromNaverAPI(name);
            if (!result.isEmpty()) {
                return ResponseEntity.ok(result);
            }

            // 2차 검색 (공백 제거 후 재검색)
            String cleanedName = name.replaceAll("\\s+", ""); // 모든 공백 제거
            if (!cleanedName.equals(name)) { // 공백 제거로 변화가 있을 때만 재검색
                List<MapLocationRes> cleanedResult = searchFromNaverAPI(cleanedName);
                return ResponseEntity.ok(cleanedResult);
            }

            // 결과 없음
            return ResponseEntity.ok(Collections.emptyList());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("장소 검색 중 오류 발생: " + e.getMessage());
        }
    }

    private List<MapLocationRes> searchFromNaverAPI(String query) throws Exception {
        List<MapLocationRes> places = new ArrayList<>();
        String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
        int start = 1;
        int display = 5;

        String apiUrlWithParams = localApiUrl + "?query=" + encodedQuery + "&display=" + display + "&start=" + start;

        URL url = new URL(apiUrlWithParams);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("X-Naver-Client-Id", localClientId);
        connection.setRequestProperty("X-Naver-Client-Secret", localClientSecret);

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Local API 호출 실패 - 응답 코드: " + connection.getResponseCode());
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
            String location = place.getString("title").replaceAll("<.*?>", "");
            String address = place.optString("roadAddress", "");

            double latitude = Double.parseDouble(place.getString("mapy")) / 1e6;
            double longitude = Double.parseDouble(place.getString("mapx")) / 1e6;

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

        // 가나다 순 정렬
        Collator collator = Collator.getInstance(Locale.KOREA);
        places.sort((a, b) -> collator.compare(a.getLocation(), b.getLocation()));

        return places;
    }


}
