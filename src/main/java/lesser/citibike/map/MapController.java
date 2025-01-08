package lesser.citibike.map;

import io.reactivex.rxjava3.core.Single;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapController {
    private final CitiBikeMapService service;
    private GeoPosition fromLocation;
    private GeoPosition toLocation;
    private final List<GeoPosition> routePoints = new ArrayList<>();
    private final Set<WaypointAdapter> waypoints = new HashSet<>();
    private JTextArea coordinatesArea;

    public MapController(CitiBikeMapService service) {
        this.service = service;
    }

    public void setCoordinatesArea(JTextArea coordinatesArea) {
        this.coordinatesArea = coordinatesArea;
    }

    public void setStartPoint(GeoPosition position) {
        if (fromLocation == null) {
            fromLocation = position;
            addCoordinateToTextArea("Start", position);
            addWaypoint(position, "Start");
        } else {
            JOptionPane.showMessageDialog(null, "Start location already set. Clear to reset.");
        }
    }

    public void setEndPoint(GeoPosition position, JXMapViewer mapViewer) {
        if (toLocation == null) {
            toLocation = position;
            addCoordinateToTextArea("Destination", position);
            addWaypoint(position, "Destination");

            routePoints.clear();
            routePoints.add(fromLocation);
            routePoints.add(toLocation);

            drawRoute(mapViewer);
        } else {
            JOptionPane.showMessageDialog(null, "Destination location already set. Clear to reset.");
        }
    }

    public void clearPoints() {
        fromLocation = null;
        toLocation = null;
        routePoints.clear();
        waypoints.clear();
        if (coordinatesArea != null) coordinatesArea.setText("");
    }

    public void calculateAndDrawRoute(JXMapViewer mapViewer) {
        if (fromLocation == null || toLocation == null) {
            JOptionPane.showMessageDialog(null, "Please set both start and destination points.");
            return;
        }

        getRoute()
                .subscribe(route -> {
                    routePoints.clear();
                    routePoints.addAll(route);
                    drawRoute(mapViewer);
                }, throwable -> JOptionPane.showMessageDialog(null,
                        "Error fetching route: " + throwable.getMessage()));
    }

    public Single<List<GeoPosition>> getRoute() {
        CitiBikeMapRequest request = new CitiBikeMapRequest(
                new MapLocation(fromLocation.getLatitude(), fromLocation.getLongitude()),
                new MapLocation(toLocation.getLatitude(), toLocation.getLongitude())
        );

        return service.getRoute(request)
                .map(responseBody -> List.of(
                        new GeoPosition(responseBody.from.lat, responseBody.from.lon),
                        new GeoPosition(responseBody.start.lat, responseBody.start.lon),
                        new GeoPosition(responseBody.end.lat, responseBody.end.lon),
                        new GeoPosition(responseBody.to.lat, responseBody.to.lon)
                ));
    }

    private void drawRoute(JXMapViewer mapViewer) {
        RoutePainter routePainter = new RoutePainter(routePoints);

        mapViewer.setOverlayPainter(routePainter);

        mapViewer.zoomToBestFit(new HashSet<>(routePoints), 0.7);
    }

    private void addCoordinateToTextArea(String label, GeoPosition position) {
        if (coordinatesArea != null) {
            coordinatesArea.append(label + ": " + position.getLatitude() + ", " + position.getLongitude() + "\n");
        }
    }

    private void addWaypoint(GeoPosition position, String label) {
        waypoints.add(new WaypointAdapter(position, label));
    }

    public Set<WaypointAdapter> getWaypoints() {
        return waypoints;
    }
}
