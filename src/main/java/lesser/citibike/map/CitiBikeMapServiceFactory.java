package lesser.citibike.map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CitiBikeMapServiceFactory {
    private CitiBikeMapService createLambdaService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://zobqqgur7viqtdzeze7jsocdna0qabdt.lambda-url.us-east-2.on.aws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(CitiBikeMapService.class);
    }
}
