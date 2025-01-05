package lesser.citibike.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import lesser.citibike.*;

import java.util.Map;

public class CitiBikeRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, String> {

    @Override
    public String handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        Gson gson = new Gson();
        CitiBikeRequest request = gson.fromJson(event.getBody(), CitiBikeRequest.class);

        try {
            CitiBikeService service = new CitiBikeServiceFactory().getService();
            StationsResponse stationsResponse = service.getStations().blockingGet();
            StatusResponse statusResponse = service.getStationStatus().blockingGet();
            CitiBikeHelper helper = new CitiBikeHelper(stationsResponse, statusResponse);

            StationsResponse.Station startStation = helper.findClosestStationWithBikes(request.from.lat, request.from.lon);
            StationsResponse.Station endStation = helper.findClosestStationWithDocks(request.to.lat, request.to.lon);

            CitiBikeResponse response = new CitiBikeResponse(
                    new Location(request.from.lat, request.from.lon),
                    new StationLocation(startStation),
                    new StationLocation(endStation),
                    new Location(request.to.lat, request.to.lon)
            );

            return gson.toJson(response);

        } catch (Exception e) {
            context.getLogger().log("Error occurred: " + e.getMessage());
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }
}
