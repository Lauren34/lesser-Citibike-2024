package lesser.citibike.lambda;

import lesser.citibike.StationsResponse;

public class StationLocation {
    public double lat;
    public double lon;
    public String name;
    public String stationId;

    public StationLocation(StationsResponse.Station station) {
        this.lat = station.lat;
        this.lon = station.lon;
        this.name = station.name;
        this.stationId = station.station_id;
    }
}
