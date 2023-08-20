package annie.tips.service;

import annie.tips.util.AppUtil;
import java.time.Duration;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

@Slf4j
@RequiredArgsConstructor
@Service
public class CallApiWithWebClientService {

  private static final int DEFAULT_RETRY_COUNT = 3;
  private static final int DEFAULT_RETRY_INTERVAL = 150;

  @NonNull
  private final AppUtil appUtil;
  private WebClient webClient;

  @PostConstruct
  private void createWebClient() {
    webClient = WebClient.builder()
        .baseUrl("http://0.0.0.0:80")
        .build();
  }

  public String callMyMockApi() {

    log.info("callMyMockApi");

    var res = webClient
        .get()
        .uri("/get")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(String.class)
        .blockOptional();

    log.debug(res.toString());
    return res.toString();
  }

  public String callMyMockApiWithResponseCode(int httpStatus) {

    log.info("callMyMockApiWithResponseCode httpStatus: {}", httpStatus);

    if (!appUtil.isValidHttpCode(httpStatus)) {
      return "input http status is invalid";
    }

    Optional<String> res;
    try {
      res = webClient
          .get()
          .uri("/status/" + httpStatus)
          .accept(MediaType.APPLICATION_JSON)
          .retrieve()
          .bodyToMono(String.class)
          .retryWhen(Retry.backoff(DEFAULT_RETRY_COUNT, Duration.ofMillis(DEFAULT_RETRY_INTERVAL))
              .filter(e -> {
                if (e instanceof WebClientResponseException) {
                  return ((WebClientResponseException) e).getStatusCode().value()
                      == HttpStatus.SERVICE_UNAVAILABLE.value();
                }
                return false;
              }))
          .blockOptional();
    } catch (Exception e) {
      throw new RuntimeException("failed to call api.", e);
    }

    return Optional.of(res).orElse(Optional.of("")).toString();
  }

}
