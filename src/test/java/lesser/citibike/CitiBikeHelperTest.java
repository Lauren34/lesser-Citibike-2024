package lesser.citibike;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CitiBikeHelperTest {

    @Test
    void findStationStatus() {
        // given
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        StationsResponse stationsResponse = service.getStations().blockingGet();
        StatusResponse statusResponse = service.getStationStatus().blockingGet();
        CitiBikeHelper helper = new CitiBikeHelper(stationsResponse, statusResponse);
        String stationId = "816e50eb-dc4b-47dc-b773-154e2020cb0d";

        // when
        StatusResponse.StationStatus status = helper.findStationStatus(stationId);

        // then
        assertNotNull(status, "Status should not be null");
        assertEquals(stationId, status.station_id, "Station ID should match");
        assertTrue(status.num_bikes_available >= 0, "Number of bikes available should be >= 0");
        assertTrue(status.num_docks_available >= 0, "Number of docks available should be >= 0");
    }

    @Test
    void findClosestStationWithBikes() throws Exception {
        // given
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        StationsResponse stationsResponse = service.getStations().blockingGet();
        StatusResponse statusResponse = service.getStationStatus().blockingGet();
        CitiBikeHelper helper = new CitiBikeHelper(stationsResponse, statusResponse);
        double lat = 40.767;
        double lon = -73.971;

        // when
        StationsResponse.Station closestStation = helper.findClosestStationWithBikes(lat, lon);

        // then
        assertNotNull(closestStation, "Station should not be null");
        assertTrue(closestStation.lat > 0, "Latitude should be valid");
        assertTrue(closestStation.lon < 0, "Longitude should be valid");
    }

    @Test
    void findClosestStationWithDocks() throws Exception {
        // given
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        StationsResponse stationsResponse = service.getStations().blockingGet();
        StatusResponse statusResponse = service.getStationStatus().blockingGet();
        CitiBikeHelper helper = new CitiBikeHelper(stationsResponse, statusResponse);
        double lat = 40.767;
        double lon = -73.971;

        // when
        StationsResponse.Station closestStation = helper.findClosestStationWithDocks(lat, lon);

        // then
        assertNotNull(closestStation, "Station should not be null");
        assertTrue(closestStation.lat > 0, "Latitude should be valid");
        assertTrue(closestStation.lon < 0, "Longitude should be valid");
    }
}
