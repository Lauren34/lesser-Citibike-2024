package lesser.citibike.map;

import io.reactivex.rxjava3.core.Single;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.List;

public class MapController {
    private final CitiBikeMapService service;

    public MapController(CitiBikeMapService service) {
        this.service = service;
    }

    public Single<List<GeoPosition>> findRoute(GeoPosition from, GeoPosition to) {
        CitiBikeMapRequest request = new CitiBikeMapRequest(
                new MapLocation(from.getLatitude(), from.getLongitude()),
                new MapLocation(to.getLatitude(), to.getLongitude())
        );

        return service.getRoute(request)
                .map(responseBody -> List.of(
                        new GeoPosition(responseBody.from.lat, responseBody.from.lon),
                        new GeoPosition(responseBody.start.lat, responseBody.start.lon),
                        new GeoPosition(responseBody.end.lat, responseBody.end.lon),
                        new GeoPosition(responseBody.to.lat, responseBody.to.lon)
                ));
    }
}
