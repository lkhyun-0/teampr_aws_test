let map, service, infowindow;

// 지도 초기화 및 현재 위치 기반 검색
function initMap() {
    const defaultCenter = new google.maps.LatLng(37.5665, 126.9780);
    map = new google.maps.Map(document.getElementById("map"), {
        center: defaultCenter,
        zoom: 15
    });

    // 현재 위치를 받아 초기 검색 진행
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            position => {
                const userLatLng = new google.maps.LatLng(
                    position.coords.latitude,
                    position.coords.longitude
                );
                map.setCenter(userLatLng);
                addMarker(userLatLng, "내 위치");
                searchHospitals(userLatLng);
            },
            error => {
                console.error("위치 정보를 가져올 수 없습니다.", error);
            },
            {enableHighAccuracy: true, timeout: 10000, maximumAge: 0}
        );
    } else {
        alert("위치 정보를 사용할 수 없습니다.");
    }
}

// Marker 추가 함수 (아이콘 옵션이 있을 경우 설정)
function addMarker(position, title, iconUrl = null) {
    const markerOptions = {position, map, title};
    if (iconUrl) {
        markerOptions.icon = {
            url: iconUrl,
            scaledSize: new google.maps.Size(40, 40),
            origin: new google.maps.Point(0, 0),
            anchor: new google.maps.Point(20, 40)
        };
    }
    return new google.maps.Marker(markerOptions);
}

// 지정된 위치를 중심으로 주변 병원 검색
function searchHospitals(location) {
    // 검색 결과 초기화 (리스트 등)
    document.querySelector('.search-list').innerHTML = "";

    const request = {location, radius: 2000, type: "hospital"};
    service = new google.maps.places.PlacesService(map);
    service.nearbySearch(request, (results, status) => {
        if (status !== google.maps.places.PlacesServiceStatus.OK) {
            console.error("Places API 오류:", status);
            return;
        }
        results.forEach(place => {
            if (!place.geometry?.location) return;

            // 병원 마커 추가 (아이콘 경로 지정)
            const marker = addMarker(place.geometry.location, place.name, "/images/hospital.png");
            marker.addListener("click", () => {
                infowindow = new google.maps.InfoWindow({
                    content: `<div><strong>${place.name}</strong><br>${place.vicinity}</div>`
                });
                infowindow.open(map, marker);
            });

            // 병원 상세정보를 받아 리스트에 추가
            getPlaceDetails(place.place_id, details => {
                let contentHTML = `<strong>${details ? details.name : place.name}</strong><br>`;
                contentHTML += `주소: ${details ? details.adr_address : place.vicinity}`;
                if (details?.formatted_phone_number) {
                    contentHTML += `<br>전화번호: ${details.formatted_phone_number}`;
                }
                const listItem = document.createElement('div');
                listItem.className = 'list-item';
                listItem.innerHTML = contentHTML;
                document.querySelector('.search-list').appendChild(listItem);
            });
        });
    });


}

// Place Details API 호출
function getPlaceDetails(placeId, callback) {
    const request = {
        placeId,
        fields: ['name', 'adr_address', 'formatted_phone_number', 'business_status']
    };
    service.getDetails(request, (placeDetails, status) => {
        if (status === google.maps.places.PlacesServiceStatus.OK) {
            callback(placeDetails);
        } else {
            console.error('Place details 요청 오류:', status);
            callback(null);
        }
    });
}

// 주소를 좌표로 변환하고 해당 위치에서 병원 검색 수행
function geocodeAddress(address) {
    const geocoder = new google.maps.Geocoder();
    geocoder.geocode({address}, (results, status) => {
        if (status === google.maps.GeocoderStatus.OK) {
            const newLocation = results[0].geometry.location;
            updateMapAndSearch(newLocation);
        } else {
            alert("검색한 위치를 찾을 수 없습니다: " + status);
        }
    });
}

// 지도 중심 이동 및 검색 실행
function updateMapAndSearch(newLocation) {
    map.setCenter(newLocation);
    addMarker(newLocation, "검색한 위치");
    searchHospitals(newLocation);
}

// 검색창 이벤트 리스너
document.querySelector('.search-form form').addEventListener('submit', event => {
    event.preventDefault();
    const input = event.target.querySelector('input[type="text"]')
    const address = input.value.trim();
    if (address) {
        geocodeAddress(address);
    }
    input.value = "";
});
