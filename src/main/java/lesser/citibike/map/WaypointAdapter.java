package lesser.citibike.map;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

public class WaypointAdapter extends DefaultWaypoint {
    private final String label;

    public WaypointAdapter(GeoPosition position, String label) {
        super(position);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
