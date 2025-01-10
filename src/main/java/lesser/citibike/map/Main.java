package lesser.citibike.map;

public class Main {
    public static void main(String[] args) {
        CitiBikeMapService service = new CitiBikeMapServiceFactory().createLambdaService();
        MapController controller = new MapController(service);
        new MapFrame(controller);
    }
}
