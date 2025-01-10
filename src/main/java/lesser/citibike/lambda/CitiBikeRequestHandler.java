package lesser.citibike.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import lesser.citibike.*;

public class CitiBikeRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, String> {

    @Override
    public String handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        context.getLogger().log("Received request: " + event.getBody());

        Gson gson = new Gson();
        CitiBikeRequest request;

        // Parse the request body
        try {
            request = gson.fromJson(event.getBody(), CitiBikeRequest.class);
            if (request.from == null || request.to == null) {
                throw new IllegalArgumentException("Request must include 'from' and 'to' locations.");
            }
        } catch (Exception e) {
            context.getLogger().log("Error parsing request: " + e.getMessage());
            return gson.toJson(new ErrorResponse("Invalid request format: " + e.getMessage()));
        }

        // Initialize service and cache
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        StationsCache stationsCache = new StationsCache();

        // Fetch stations data
        StationsResponse stationsResponse;
        try {
            stationsResponse = stationsCache.getStations();
        } catch (Exception e) {
            context.getLogger().log("Error retrieving station data: " + e.getMessage());
            return gson.toJson(new ErrorResponse("Failed to retrieve station data."));
        }

        // Fetch status data
        StatusResponse statusResponse;
        try {
            statusResponse = service.getStationStatus().blockingGet();
        } catch (Exception e) {
            context.getLogger().log("Error retrieving station status: " + e.getMessage());
            return gson.toJson(new ErrorResponse("Failed to retrieve station status."));
        }

        // Find closest stations
        CitiBikeHelper helper = new CitiBikeHelper(stationsResponse, statusResponse);
        StationsResponse.Station startStation;
        StationsResponse.Station endStation;
        try {
            startStation = helper.findClosestStationWithBikes(request.from.lat, request.from.lon);
            endStation = helper.findClosestStationWithDocks(request.to.lat, request.to.lon);
        } catch (Exception e) {
            context.getLogger().log("Error finding closest stations: " + e.getMessage());
            return gson.toJson(new ErrorResponse("Failed to find suitable stations."));
        }

        // Construct response
        CitiBikeResponse response = new CitiBikeResponse(
                new Location(request.from.lat, request.from.lon),
                new StationLocation(startStation),
                new StationLocation(endStation),
                new Location(request.to.lat, request.to.lon)
        );

        context.getLogger().log("Constructed response: " + gson.toJson(response));
        return gson.toJson(response);
    }

    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}
