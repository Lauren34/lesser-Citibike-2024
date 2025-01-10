package lesser.citibike.map;

public class CitiBikeMapRequest {
    public MapLocation from;
    public MapLocation to;

    public CitiBikeMapRequest(MapLocation from, MapLocation to) {
        this.from = from;
        this.to = to;
    }
}
