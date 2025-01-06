package lesser.citibike.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import lesser.citibike.*;

public class CitiBikeRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, String> {

    @Override
    public String handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        Gson gson = new Gson();
        CitiBikeRequest request = gson.fromJson(event.getBody(), CitiBikeRequest.class);

        CitiBikeService service = new CitiBikeServiceFactory().getService();
        StationsResponse stationsResponse = service.getStations().blockingGet();
        StatusResponse statusResponse = service.getStationStatus().blockingGet();
        CitiBikeHelper helper = new CitiBikeHelper(
                stationsResponse,
                statusResponse
        );

        StationsResponse.Station startStation = null;
        try {
            startStation = helper.findClosestStationWithBikes(
                    request.from.lat,
                    request.from.lon
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StationsResponse.Station endStation = null;
        try {
            endStation = helper.findClosestStationWithDocks(
                    request.to.lat,
                    request.to.lon
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CitiBikeResponse response = new CitiBikeResponse(
                new Location(request.from.lat, request.from.lon),
                new StationLocation(startStation),
                new StationLocation(endStation),
                new Location(request.to.lat, request.to.lon)
        );

        return gson.toJson(response);
    }
}
