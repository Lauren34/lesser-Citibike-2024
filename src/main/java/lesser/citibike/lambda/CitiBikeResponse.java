package lesser.citibike.lambda;

public class CitiBikeResponse {
    public Location from;
    public StationLocation start;
    public StationLocation end;
    public Location to;

    public CitiBikeResponse(Location from, StationLocation start, StationLocation end, Location to) {
        this.from = from;
        this.start = start;
        this.end = end;
        this.to = to;
    }
}