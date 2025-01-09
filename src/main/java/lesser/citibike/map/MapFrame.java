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
    private final JTextField coordinatesField = new JTextField();

    public MapFrame(MapController controller) {
        this.controller = controller;
        controller.setCoordinatesField(coordinatesField);
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
                controller.handleMapClick(position, mapViewer);
            }
        });

        JPanel controlPanel = new JPanel();
        JButton findRouteButton = new JButton("Map");
        JButton clearButton = new JButton("Clear");

        findRouteButton.addActionListener(e -> controller.calculateAndDrawRoute(mapViewer));
        clearButton.addActionListener(e -> {
            controller.clearPoints(mapViewer);
            coordinatesField.setText("");
        });

        controlPanel.add(findRouteButton);
        controlPanel.add(clearButton);

        setLayout(new BorderLayout());
        add(mapViewer, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(coordinatesField, BorderLayout.NORTH);

        coordinatesField.setEditable(false);
        coordinatesField.setHorizontalAlignment(SwingConstants.LEFT);

        setTitle("BikeMap");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
