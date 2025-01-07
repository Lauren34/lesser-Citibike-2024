package lesser.citibike.map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main {
    public static void main(String[] args) {
        CitiBikeMapService service = new Retrofit.Builder()
                .baseUrl("https://zobqqgur7viqtdzeze7jsocdna0qabdt.lambda-url.us-east-2.on.aws/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(CitiBikeMapService.class);

        MapController controller = new MapController(service);

        new MapFrame(controller);
    }
}
