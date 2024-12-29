package lesser.citibike;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.HashMap;
import java.util.Map;

public class CitiBikeHelper {
    private final CitiBikeService service;

    public CitiBikeHelper(CitiBikeService service) {
        this.service = service;
    }

    public Single<StatusResponse.StationStatus> findStationStatus(String stationId) {
        return service.getStationStatus()
                .map(response -> {
                    for (StatusResponse.StationStatus status : response.data.stations) {
                        if (status.station_id.equals(stationId)) {
                            return status;
                        }
                    }
                    throw new Exception("Station not found");
                })
                .subscribeOn(Schedulers.io());
    }

    public Single<StationsResponse.Station> findClosestStationWithBikes(double lat, double lon) {
        return Single.zip(
                service.getStations().subscribeOn(Schedulers.io()),
                service.getStationStatus().subscribeOn(Schedulers.io()),
                (stationsResponse, statusResponse) -> {
                    Map<String, StatusResponse.StationStatus> statusMap = buildStatusMap(statusResponse);
                    return findClosestStation(stationsResponse, lat, lon, statusMap, true);
                });
    }

    public Single<StationsResponse.Station> findClosestStationWithDocks(double lat, double lon) {
        return Single.zip(
                service.getStations().subscribeOn(Schedulers.io()),
                service.getStationStatus().subscribeOn(Schedulers.io()),
                (stationsResponse, statusResponse) -> {
                    Map<String, StatusResponse.StationStatus> statusMap = buildStatusMap(statusResponse);
                    return findClosestStation(stationsResponse, lat, lon, statusMap, false);
                });
    }

    private Map<String, StatusResponse.StationStatus> buildStatusMap(StatusResponse statusResponse) {
        Map<String, StatusResponse.StationStatus> statusMap = new HashMap<>();
        for (StatusResponse.StationStatus status : statusResponse.data.stations) {
            statusMap.put(status.station_id, status);
        }
        return statusMap;
    }

    private StationsResponse.Station findClosestStation(
            StationsResponse stationsResponse,
            double lat,
            double lon,
            Map<String, StatusResponse.StationStatus> statusMap,
            boolean checkBikes
    ) throws Exception {
        StationsResponse.Station closestStation = null;
        double closestDistance = Double.MAX_VALUE;

        for (StationsResponse.Station station : stationsResponse.data.stations) {
            StatusResponse.StationStatus status = statusMap.get(station.station_id);
            boolean hasResources = checkBikes ? status.num_bikes_available > 0 : status.num_docks_available > 0;

            if (status != null && hasResources) {
                double distance = calculateDistance(lat, lon, station.lat, station.lon);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestStation = station;
                }
            }
        }

        if (closestStation == null) {
            throw new Exception("No station found with required resources");
        }

        return closestStation;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}