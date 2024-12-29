package lesser.citibike;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CitiBikeServiceTest {

    @Test
    void getStations() {
        // given
        CitiBikeService service = new CitiBikeServiceFactory().getService();

        // when
        StationsResponse response = service.getStations().blockingGet();

        // then
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.data, "Data should not be null");
        assertTrue(response.data.stations.length > 0, "Stations list should not be empty");
        assertNotNull(response.data.stations[0].name, "Station name should not be null");
        assertNotNull(response.data.stations[0].station_id, "Station ID should not be null");
    }

    @Test
    void getStationStatus() {
        // given
        CitiBikeService service = new CitiBikeServiceFactory().getService();

        // when
        StatusResponse response = service.getStationStatus().blockingGet();

        // then
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.data, "Data should not be null");
        assertTrue(response.data.stations.length > 0, "Stations list should not be empty");
        assertNotNull(response.data.stations[0].station_id, "Station ID should not be null");
        assertTrue(response.data.stations[0].num_bikes_available >= 0, "Number of bikes available should be >= 0");
        assertTrue(response.data.stations[0].num_docks_available >= 0, "Number of docks available should be >= 0");
    }

    @Test
    void findClosestStationWithBikes() {
        // given
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        CitiBikeHelper helper = new CitiBikeHelper(service);
        double lat = 40.767;
        double lon = -73.993;

        // when
        StationsResponse.Station closestStation = helper.findClosestStationWithBikes(lat, lon).blockingGet();

        // then
        assertNotNull(closestStation, "Closest station should not be null");
        assertNotNull(closestStation.station_id, "Station ID should not be null");
        assertTrue(closestStation.lat > 0, "Latitude should be valid");
        assertTrue(closestStation.lon < 0, "Longitude should be valid");
    }

    @Test
    void findClosestStationWithDocks() {
        // given
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        CitiBikeHelper helper = new CitiBikeHelper(service);
        double lat = 40.767;
        double lon = -73.993;

        // when
        StationsResponse.Station closestStation = helper.findClosestStationWithDocks(lat, lon).blockingGet();

        // then
        assertNotNull(closestStation, "Closest station should not be null");
        assertNotNull(closestStation.station_id, "Station ID should not be null");
        assertTrue(closestStation.lat > 0, "Latitude should be valid");
        assertTrue(closestStation.lon < 0, "Longitude should be valid");
    }
}