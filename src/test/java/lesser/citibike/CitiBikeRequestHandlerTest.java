package lesser.citibike;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import lesser.citibike.lambda.CitiBikeRequestHandler;
import lesser.citibike.lambda.CitiBikeResponse;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class CitiBikeRequestHandlerTest {

    @Test
    void handleRequestTest() throws Exception {
        // given
        String requestJson = Files.readString(Path.of("src/test/resources/request.json"));
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody(requestJson);

        Context mockContext = mock(Context.class);

        CitiBikeRequestHandler requestHandler = new CitiBikeRequestHandler();

        // when
        String responseJson = requestHandler.handleRequest(event, mockContext);

        CitiBikeResponse response = new com.google.gson.Gson().fromJson(responseJson, CitiBikeResponse.class);

        // then
        assertEquals("Lenox Ave & W 146 St", response.start.name);
        assertEquals("Berry St & N 8 St", response.end.name);
    }
}
