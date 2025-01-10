package lesser.citibike.lambda;

import com.google.gson.Gson;
import lesser.citibike.*;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;

public class StationsCache {

    private static final String BUCKET_NAME = "lesser.citibike";
    private static final String FILE_KEY = "station_information.json";

    private final S3Client s3Client;
    private final CitiBikeService bikeService;
    private final Gson gson;

    private StationsResponse stationsData;
    private Instant lastUpdated;

    public StationsCache() {
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        this.bikeService = new CitiBikeServiceFactory().getService();
        this.gson = new Gson();
    }

    public synchronized StationsResponse getStations() {
        if (isCacheValid()) {
            return stationsData;
        }

        if (isS3DataRecent()) {
            loadStationsFromS3();
        } else {
            fetchAndStoreLatestStations();
        }

        return stationsData;
    }

    private boolean isCacheValid() {
        return stationsData != null
                && lastUpdated != null
                && Duration.between(lastUpdated, Instant.now()).toMinutes() <= 60;
    }

    private boolean isS3DataRecent() {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(FILE_KEY)
                    .build();

            HeadObjectResponse headResponse = s3Client.headObject(headRequest);
            lastUpdated = headResponse.lastModified();
            return Duration.between(lastUpdated, Instant.now()).toMinutes() <= 60;
        } catch (Exception e) {
            return false;
        }
    }

    private void loadStationsFromS3() {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(FILE_KEY)
                    .build();

            InputStream inputStream = s3Client.getObject(getRequest);
            stationsData = gson.fromJson(new InputStreamReader(inputStream), StationsResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error loading data from S3: " + e.getMessage(), e);
        }
    }

    private void fetchAndStoreLatestStations() {
        try {
            stationsData = bikeService.getStations().blockingGet();

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(FILE_KEY)
                    .build();

            String jsonContent = gson.toJson(stationsData);
            s3Client.putObject(putRequest, RequestBody.fromString(jsonContent));

            lastUpdated = Instant.now();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching and storing latest station data: " + e.getMessage(), e);
        }
    }
}
