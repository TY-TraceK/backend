import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpVersionTest {
    public static void main(String[] args) throws Exception {
        String url =
                "https://apis.data.go.kr/B551011/KorService2/detailImage2?serviceKey="
                        + "tK1kA1Dogdw9us7Rq5f%2Fe2BxTOxuWO1J7Fi7vqgN%2FYI5lBDrLfmhYaV%2Fj2KdjmdSjQMO24NLKD68AW6VcCRxBA%3D%3D"
                        + "&contentId=1277679&imageYN=Y&numOfRows=3&pageNo=1&MobileOS=ETC&MobileApp=TraceK&_type=json";

        System.out.println("=== Default HttpClient (HTTP/2 preferred) ===");
        run(url, HttpClient.Version.HTTP_2);

        System.out.println();
        System.out.println("=== Forced HTTP/1.1 ===");
        run(url, HttpClient.Version.HTTP_1_1);
    }

    private static void run(String url, HttpClient.Version version) {
        try {
            HttpClient client =
                    HttpClient.newBuilder()
                            .version(version)
                            .connectTimeout(Duration.ofSeconds(5))
                            .build();
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("status=" + response.statusCode());
            System.out.println("negotiated version=" + response.version());
            String body = response.body();
            System.out.println(
                    "body(prefix)=" + body.substring(0, Math.min(200, body.length())));
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e);
        }
    }
}
