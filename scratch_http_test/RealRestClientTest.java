import org.springframework.web.client.RestClient;

public class RealRestClientTest {
    public static void main(String[] args) {
        String serviceKey =
                "tK1kA1Dogdw9us7Rq5f/e2BxTOxuWO1J7Fi7vqgN/YI5lBDrLfmhYaV/j2KdjmdSjQMO24NLKD68AW6VcCRxBA==";

        RestClient client =
                RestClient.builder()
                        .baseUrl("https://apis.data.go.kr/B551011/KorService2")
                        .requestInterceptor(
                                (request, requestBody, execution) -> {
                                    System.out.println("ACTUAL SENT URI=" + request.getURI());
                                    return execution.execute(request, requestBody);
                                })
                        .build();

        String body =
                client.get()
                        .uri(
                                uriBuilder ->
                                        uriBuilder
                                                .path("/detailImage2")
                                                .queryParam("serviceKey", serviceKey)
                                                .queryParam("contentId", 1277679L)
                                                .queryParam("imageYN", "Y")
                                                .queryParam("numOfRows", 3)
                                                .queryParam("pageNo", 1)
                                                .queryParam("MobileOS", "ETC")
                                                .queryParam("MobileApp", "TraceK")
                                                .queryParam("_type", "json")
                                                .build())
                        .retrieve()
                        .body(String.class);

        System.out.println("body=" + body);
    }
}
