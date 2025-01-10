package lesser.citibike.map;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CitiBikeMapService {
    @POST("/")
    Single<CitiBikeMapResponse> getRoute(@Body CitiBikeMapRequest request);
}
