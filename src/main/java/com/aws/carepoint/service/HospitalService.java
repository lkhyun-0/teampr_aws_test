package com.aws.carepoint.service;

import com.aws.carepoint.domain.Hospital;
import com.aws.carepoint.mapper.HospitalMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HospitalService {
    private final HospitalMapper hospitalMapper;

    @Value("${google.api.key}")
    private String googleApiKey; // application.properties에서 API KEY 설정

    public HospitalService(HospitalMapper hospitalMapper) {
        this.hospitalMapper = hospitalMapper;
    }

    // Google Places API에서 병원 가져와서 저장
    public void fetchAndSaveHospitals(String query) {
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json"
                + "?query=" + query
                + "&type=hospital"
                + "&language=ko"
                + "&key=" + googleApiKey;

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray results = jsonResponse.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {
            JSONObject place = results.getJSONObject(i);

            String name = place.getString("name");
            String address = place.optString("formatted_address", "");
            String placeId = place.getString("place_id");
            JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
            double lat = location.getDouble("lat");
            double lng = location.getDouble("lng");

            // DB에서 중복 확인
            Hospital existingHospital = hospitalMapper.findByPlaceId(placeId);
            if (existingHospital == null) {
                Hospital hospital = new Hospital();
                hospital.setName(name);
                hospital.setAddress(address);
                hospital.setLatitude(lat);
                hospital.setLongitude(lng);
                hospital.setPlaceId(placeId);
                hospitalMapper.insertHospital(hospital);
            }
        }
    }
}
