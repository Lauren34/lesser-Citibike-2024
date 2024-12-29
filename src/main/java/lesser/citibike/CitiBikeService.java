package lesser.citibike;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CitiBikeService {
    @GET("station_information.json")
    Single<StationsResponse> getStations();

    @GET("station_status.json")
    Single<StatusResponse> getStationStatus();
}
