import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import org.springframework.web.util.UriComponentsBuilder;

public class UriEncodeTest {
    public static void main(String[] args) throws Exception {
        String serviceKey =
                "tK1kA1Dogdw9us7Rq5f/e2BxTOxuWO1J7Fi7vqgN/YI5lBDrLfmhYaV/j2KdjmdSjQMO24NLKD68AW6VcCRxBA==";

        URI uri =
                UriComponentsBuilder.fromHttpUrl("https://apis.data.go.kr/B551011/KorService2")
                        .path("/detailImage2")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("contentId", 1277679L)
                        .queryParam("imageYN", "Y")
                        .queryParam("numOfRows", 3)
                        .queryParam("pageNo", 1)
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", "TraceK")
                        .queryParam("_type", "json")
                        .build()
                        .toUri();

        System.out.println("built URI=" + uri);

        HttpClient client =
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("status=" + response.statusCode());
        String body = response.body();
        System.out.println("body(prefix)=" + body.substring(0, Math.min(300, body.length())));
    }
}
