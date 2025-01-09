package lesser.citibike.map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CitiBikeMapServiceFactory {
    public CitiBikeMapService createLambdaService() {
        return new Retrofit.Builder()
                .baseUrl("https://zobqqgur7viqtdzeze7jsocdna0qabdt.lambda-url.us-east-2.on.aws/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(CitiBikeMapService.class);
    }
}
