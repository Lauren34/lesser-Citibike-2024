package lesser.citibike.map;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Set;

public class RoutePainter implements Painter<JXMapViewer> {
    private final List<GeoPosition> track;
    private final Set<WaypointAdapter> waypoints; // Waypoints to display

    public RoutePainter(List<GeoPosition> track, Set<WaypointAdapter> waypoints) {
        this.track = track;
        this.waypoints = waypoints;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
        g = (Graphics2D) g.create();
        Rectangle viewportBounds = map.getViewportBounds();
        g.translate(-viewportBounds.getX(), -viewportBounds.getY());

        // Draw the route
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(3));
        Point2D lastPoint = null;
        for (GeoPosition position : track) {
            Point2D currentPoint = map.getTileFactory().geoToPixel(position, map.getZoom());
            if (lastPoint != null) {
                g.drawLine((int) lastPoint.getX(), (int) lastPoint.getY(),
                        (int) currentPoint.getX(), (int) currentPoint.getY());
            }
            lastPoint = currentPoint;
        }

        // Draw the waypoints
        for (WaypointAdapter waypoint : waypoints) {
            GeoPosition position = waypoint.getPosition();
            Point2D point = map.getTileFactory().geoToPixel(position, map.getZoom());

            // Draw the waypoint marker
            g.setColor(Color.BLUE);
            g.fillOval((int) point.getX() - 5, (int) point.getY() - 5, 10, 10);

            // Draw the waypoint label
            g.setColor(Color.BLACK);
            g.drawString(waypoint.getLabel(), (int) point.getX() + 5, (int) point.getY() - 5);
        }

        g.dispose();
    }
}
