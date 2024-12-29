package lesser.citibike;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CitiBikeHelperTest {

    @Test
    void findStationStatus() {
        // given
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        CitiBikeHelper helper = new CitiBikeHelper(service);
        String stationId = "816e50eb-dc4b-47dc-b773-154e2020cb0d";

        // when
        StatusResponse.StationStatus status = helper.findStationStatus(stationId).blockingGet();

        // then
        assertNotNull(status, "Status should not be null");
        assertEquals(stationId, status.station_id, "Station ID should match");
        assertTrue(status.num_bikes_available >= 0, "Number of bikes available should be >= 0");
        assertTrue(status.num_docks_available >= 0, "Number of docks available should be >= 0");
    }

    @Test
    void findClosestStationWithBikes() {
        // given
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        CitiBikeHelper helper = new CitiBikeHelper(service);
        double lat = 40.767;
        double lon = -73.971;

        // when
        StationsResponse.Station closestStation = helper.findClosestStationWithBikes(lat, lon).blockingGet();

        // then
        assertNotNull(closestStation, "Station should not be null");
        assertTrue(closestStation.lat > 0, "Latitude should be valid");
        assertTrue(closestStation.lon < 0, "Longitude should be valid");
    }

    @Test
    void findClosestStationWithDocks() {
        // given
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        CitiBikeHelper helper = new CitiBikeHelper(service);
        double lat = 40.767; // Central Park coordinates
        double lon = -73.971;

        // when
        StationsResponse.Station closestStation = helper.findClosestStationWithDocks(lat, lon).blockingGet();

        // then
        assertNotNull(closestStation, "Station should not be null");
        assertTrue(closestStation.lat > 0, "Latitude should be valid");
        assertTrue(closestStation.lon < 0, "Longitude should be valid");
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}