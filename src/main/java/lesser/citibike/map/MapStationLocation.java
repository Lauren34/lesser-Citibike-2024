package lesser.citibike.map;

public class MapStationLocation {
    public double lat;
    public double lon;
    public String name;
    public String stationId;

    public MapStationLocation(double lat, double lon, String name, String stationId) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.stationId = stationId;
    }
}
