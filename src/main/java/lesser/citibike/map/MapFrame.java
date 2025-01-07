package lesser.citibike.map;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapFrame extends JFrame {
    private final JXMapViewer mapViewer = new JXMapViewer();
    private final MapController controller;
    private GeoPosition fromLocation;
    private GeoPosition toLocation;

    private final JTextArea coordinatesArea = new JTextArea(2, 40);
    private final List<GeoPosition> routePoints = new ArrayList<>();
    private final Set<Waypoint> waypoints = new HashSet<>();

    public MapFrame(MapController controller) {
        this.controller = controller;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        GeoPosition nyc = new GeoPosition(40.730610, -73.935242);
        mapViewer.setZoom(10);
        mapViewer.setAddressLocation(nyc);

        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GeoPosition position = mapViewer.convertPointToGeoPosition(e.getPoint());
                if (fromLocation == null) {
                    fromLocation = position;
                    coordinatesArea.append("Start: " + position.getLatitude() + ", " + position.getLongitude() + "\n");
                    addWaypoint(position, "Start");
                } else if (toLocation == null) {
                    toLocation = position;
                    coordinatesArea.append("Destination: " + position.getLatitude() + ", " + position.getLongitude() + "\n");
                    addWaypoint(position, "Destination");
                } else {
                    JOptionPane.showMessageDialog(null, "Both locations already set! Use 'Clear' to reset.");
                }
            }
        });

        JPanel controlPanel = new JPanel();
        JButton findRouteButton = new JButton("Map");
        JButton clearButton = new JButton("Clear");

        findRouteButton.addActionListener(e -> {
            if (fromLocation == null || toLocation == null) {
                JOptionPane.showMessageDialog(this, "Please set both start and destination points.");
            } else {
                calculateAndDrawRoute();
            }
        });

        clearButton.addActionListener(e -> {
            fromLocation = null;
            toLocation = null;
            routePoints.clear();
            waypoints.clear();
            coordinatesArea.setText("");
            mapViewer.setOverlayPainter(null);
        });

        controlPanel.add(findRouteButton);
        controlPanel.add(clearButton);

        setLayout(new BorderLayout());
        add(mapViewer, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(new JScrollPane(coordinatesArea), BorderLayout.NORTH);

        setTitle("BikeMap");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void addWaypoint(GeoPosition position, String label) {
        Waypoint waypoint = new WaypointAdapter(position, label);
        waypoints.add(waypoint);

        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(waypoints);
        mapViewer.setOverlayPainter(waypointPainter);
    }

    private void calculateAndDrawRoute() {
        controller.findRoute(fromLocation, toLocation)
                .subscribe(route -> {
                    routePoints.clear();
                    routePoints.addAll(route);
                    drawRoute();
                }, throwable -> JOptionPane.showMessageDialog(this, "Error fetching route: " + throwable.getMessage()));
    }

    private void drawRoute() {
        RoutePainter routePainter = new RoutePainter(routePoints);

        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(waypoints);

        mapViewer.setOverlayPainter(routePainter);
        mapViewer.setOverlayPainter(waypointPainter);

        mapViewer.zoomToBestFit(new HashSet<>(routePoints), 0.7);
    }
}
