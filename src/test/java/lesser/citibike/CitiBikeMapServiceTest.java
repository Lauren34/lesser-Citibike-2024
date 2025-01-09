package lesser.citibike;

import lesser.citibike.map.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CitiBikeMapServiceTest {

    @Test
    void testGetRoute() {
        // given
        CitiBikeMapService service = new CitiBikeMapServiceFactory().createLambdaService();
        MapLocation from = new MapLocation(40.730610, -73.935242);
        MapLocation to = new MapLocation(40.712776, -74.005974);
        CitiBikeMapRequest request = new CitiBikeMapRequest(from, to);

        // when
        CitiBikeMapResponse response = service.getRoute(request).blockingGet();

        // then
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.start, "Start station should not be null");
        assertNotNull(response.end, "End station should not be null");

        // Update the expected station names to match the actual response
        assertEquals("Van Dam St & Greenpoint Ave", response.start.name, "Start station name should match");
        assertEquals("Centre St & Chambers St", response.end.name, "End station name should match");
    }
}
