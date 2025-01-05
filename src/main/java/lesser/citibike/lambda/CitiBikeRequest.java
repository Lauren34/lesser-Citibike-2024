package lesser.citibike.lambda;

public class CitiBikeRequest {
    public Location from;
    public Location to;

    public CitiBikeRequest(Location from, Location to) {
        this.from = from;
        this.to = to;
    }
}
