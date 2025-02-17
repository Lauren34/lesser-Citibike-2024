package lesser.citibike;

import java.util.List;

public class StationsResponse {
    public Data data;

    public static class Data {
        public Station[] stations;
    }

    public static class Station {
        //CHECKSTYLE:OFF
        public String station_id;
        //CHECKSTYLE:ON
        public String name;
        public double lat;
        public double lon;

    }
}
