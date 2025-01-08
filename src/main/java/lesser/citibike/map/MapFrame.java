package lesser.citibike.map;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MapFrame extends JFrame {
    private final JXMapViewer mapViewer = new JXMapViewer();
    private final MapController controller;
    private final JTextArea coordinatesArea = new JTextArea(2, 40);

    public MapFrame(MapController controller) {
        this.controller = controller;
        controller.setCoordinatesArea(coordinatesArea);
        createAndShowGui();
    }

    private void createAndShowGui() {
        DefaultTileFactory tileFactory = new DefaultTileFactory(new OSMTileFactoryInfo());
        mapViewer.setTileFactory(tileFactory);

        GeoPosition nyc = new GeoPosition(40.730610, -73.935242);
        mapViewer.setZoom(10);
        mapViewer.setAddressLocation(nyc);

        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GeoPosition position = mapViewer.convertPointToGeoPosition(e.getPoint());
                System.out.println("Clicked position: " + position.getLatitude() + ", " + position.getLongitude());

                if (controller.getWaypoints().isEmpty()) {
                    controller.setStartPoint(position);
                } else if (controller.getWaypoints().size() == 1) {
                    controller.setEndPoint(position, mapViewer);
                } else {
                    JOptionPane.showMessageDialog(null, "Both locations already set! Use 'Clear' to reset.");
                }
            }
        });

        JPanel controlPanel = new JPanel();
        JButton findRouteButton = new JButton("Map");
        JButton clearButton = new JButton("Clear");

        findRouteButton.addActionListener(e -> controller.calculateAndDrawRoute(mapViewer));

        clearButton.addActionListener(e -> {
            controller.clearPoints();
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
}
