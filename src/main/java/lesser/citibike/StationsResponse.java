package lesser.citibike;

import java.util.List;

public class StationsResponse {
    public Data data;

    public static class Data {
        public Station [] stations;
    }

    public static class Station {
        public String station_id;
        public String name;
        public double lat;
        public double lon;
    }
}
