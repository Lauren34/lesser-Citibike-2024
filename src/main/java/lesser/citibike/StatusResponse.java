package lesser.citibike;

import java.util.List;

public class StatusResponse {
    public Data data;

    public static class Data {
        public StationStatus [] stations;
    }

    public static class StationStatus {
        public String station_id;
        public int num_bikes_available;
        public int num_docks_available;
    }
}