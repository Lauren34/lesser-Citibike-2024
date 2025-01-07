package lesser.citibike.map;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class RoutePainter implements Painter<JXMapViewer> {
    private final List<GeoPosition> track;

    public RoutePainter(List<GeoPosition> track) {
        this.track = track;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
        g = (Graphics2D) g.create();
        Rectangle viewportBounds = map.getViewportBounds();
        g.translate(-viewportBounds.getX(), -viewportBounds.getY());

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

        g.dispose();
    }
}
