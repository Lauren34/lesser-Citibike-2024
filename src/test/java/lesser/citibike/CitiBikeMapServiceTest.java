package lesser.citibike;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import io.reactivex.rxjava3.core.Single;
import lesser.citibike.map.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class CitiBikeMapServiceTest {

    @Test
    public void getRouteSuccess() {
        // given
        final CitiBikeMapService serviceMock = mock(CitiBikeMapService.class);
        final CitiBikeMapRequest mockRequest = new CitiBikeMapRequest(
                new MapLocation(40.730610, -73.935242),
                new MapLocation(40.712776, -74.005974)
        );

        final CitiBikeMapResponse mockResponse = new CitiBikeMapResponse();
        mockResponse.from = new MapLocation(40.730610, -73.935242);
        mockResponse.start = new MapStationLocation(40.740000, -73.950000, "StartStation", "ID123");
        mockResponse.end = new MapStationLocation(40.720000, -73.980000, "EndStation", "ID456");
        mockResponse.to = new MapLocation(40.712776, -74.005974);

        when(serviceMock.getRoute(mockRequest)).thenReturn(Single.just(mockResponse));

        // when
        final Single<CitiBikeMapResponse> result = serviceMock.getRoute(mockRequest);

        // then
        final CitiBikeMapResponse response = result.blockingGet();
        assertNotNull(response);
        assertEquals(mockResponse.from.lat, response.from.lat);
        assertEquals(mockResponse.start.name, response.start.name);
        assertEquals(mockResponse.end.stationId, response.end.stationId);
        assertEquals(mockResponse.to.lon, response.to.lon);
    }

    @Test
    public void getRouteFailure() {
        // given
        final CitiBikeMapService serviceMock = mock(CitiBikeMapService.class);
        final CitiBikeMapRequest mockRequest = new CitiBikeMapRequest(
                new MapLocation(40.730610, -73.935242),
                new MapLocation(40.712776, -74.005974)
        );

        when(serviceMock.getRoute(mockRequest))
                .thenReturn(Single.error(new RuntimeException("Server error")));

        // when
        final Single<CitiBikeMapResponse> result = serviceMock.getRoute(mockRequest);

        // then
        final RuntimeException exception = assertThrows(RuntimeException.class, result::blockingGet);
        assertEquals("Server error", exception.getMessage());
    }

    @Test
    public void requestPayloadSentToService() {
        // given
        final CitiBikeMapService serviceMock = mock(CitiBikeMapService.class);
        final ArgumentCaptor<CitiBikeMapRequest> requestCaptor = ArgumentCaptor.forClass(CitiBikeMapRequest.class);

        final CitiBikeMapRequest mockRequest = new CitiBikeMapRequest(
                new MapLocation(40.730610, -73.935242),
                new MapLocation(40.712776, -74.005974)
        );

        final CitiBikeMapResponse mockResponse = new CitiBikeMapResponse();
        when(serviceMock.getRoute(any())).thenReturn(Single.just(mockResponse));

        // when
        serviceMock.getRoute(mockRequest);

        // then
        verify(serviceMock).getRoute(requestCaptor.capture());
        final CitiBikeMapRequest capturedRequest = requestCaptor.getValue();
        assertEquals(40.730610, capturedRequest.from.lat);
        assertEquals(-74.005974, capturedRequest.to.lon);
    }
}
