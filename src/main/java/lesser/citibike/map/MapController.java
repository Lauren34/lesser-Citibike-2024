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
    private JTextField coordinatesField;

    public MapController(CitiBikeMapService service) {
        this.service = service;
    }

    public void setCoordinatesField(JTextField coordinatesField) {
        this.coordinatesField = coordinatesField;
    }

    public void handleMapClick(GeoPosition position, JXMapViewer mapViewer) {
        if (waypoints.isEmpty()) {
            setStartPoint(position);
        } else if (waypoints.size() == 1) {
            setEndPoint(position, mapViewer);
        } else {
            JOptionPane.showMessageDialog(null, "Both locations already set! Use 'Clear' to reset.");
        }
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

    private Single<List<GeoPosition>> getRoute() {
        CitiBikeMapRequest request = new CitiBikeMapRequest(
                new MapLocation(fromLocation.getLatitude(), fromLocation.getLongitude()),
                new MapLocation(toLocation.getLatitude(), toLocation.getLongitude())
        );

        return service.getRoute(request)
                .map(responseBody -> {
                    List<GeoPosition> positions = new ArrayList<>();
                    positions.add(new GeoPosition(responseBody.from.lat, responseBody.from.lon));
                    positions.add(new GeoPosition(responseBody.start.lat, responseBody.start.lon));
                    positions.add(new GeoPosition(responseBody.end.lat, responseBody.end.lon));
                    positions.add(new GeoPosition(responseBody.to.lat, responseBody.to.lon));
                    return positions;
                });
    }

    private void drawRoute(JXMapViewer mapViewer) {
        RoutePainter routePainter = new RoutePainter(routePoints, waypoints);
        mapViewer.setOverlayPainter(routePainter);
        mapViewer.zoomToBestFit(new HashSet<>(routePoints), 0.7);
    }

    public void setStartPoint(GeoPosition position) {
        if (fromLocation == null) {
            fromLocation = position;
            addWaypoint(position, "Start");
            updateCoordinatesField();
        } else {
            JOptionPane.showMessageDialog(null, "Start location already set. Clear to reset.");
        }
    }

    public void setEndPoint(GeoPosition position, JXMapViewer mapViewer) {
        if (toLocation == null) {
            toLocation = position;
            addWaypoint(position, "Destination");

            routePoints.clear();
            routePoints.add(fromLocation);
            routePoints.add(toLocation);

            drawRoute(mapViewer);
            updateCoordinatesField();
        } else {
            JOptionPane.showMessageDialog(null, "Destination location already set. Clear to reset.");
        }
    }

    public void clearPoints(JXMapViewer mapViewer) {
        fromLocation = null;
        toLocation = null;
        routePoints.clear();
        waypoints.clear();
        if (coordinatesField != null) {
            coordinatesField.setText("");
        }
        mapViewer.setOverlayPainter(null);
    }

    private void addWaypoint(GeoPosition position, String label) {
        waypoints.add(new WaypointAdapter(position, label));
    }

    public Set<WaypointAdapter> getWaypoints() {
        return waypoints;
    }

    private void updateCoordinatesField() {
        if (coordinatesField != null) {
            StringBuilder coordinatesText = new StringBuilder();
            for (WaypointAdapter waypoint : waypoints) {
                coordinatesText.append(waypoint.getLabel()).append(": ")
                        .append(waypoint.getPosition().getLatitude()).append(", ")
                        .append(waypoint.getPosition().getLongitude()).append(" | ");
            }
            if (coordinatesText.length() > 0) {
                coordinatesText.setLength(coordinatesText.length() - 3); // Remove trailing " | "
            }
            coordinatesField.setText(coordinatesText.toString());
        }
    }
}
