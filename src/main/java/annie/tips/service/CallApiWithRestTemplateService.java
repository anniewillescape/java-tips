package annie.tips.service;

import annie.tips.util.AppUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Service
public class CallApiWithRestTemplateService {

  @NonNull
  private final RestTemplate restTemplate;

  @NonNull
  private final RetryTemplate retryTemplate;

  @NonNull
  private final AppUtil appUtil;


  public String callMyMockApi(){

    log.info("callMyMockApi");

    ResponseEntity<String> res;

    try {
      res = restTemplate.exchange(
          // use httpbin (see how to start httpbin on README.md)
          "http://0.0.0.0:80/get",
          HttpMethod.GET,
          null,
          String.class
      );
    }catch(Exception e){
      log.warn(e.getMessage());
      throw e;
    }

    return res.getStatusCode().is2xxSuccessful() ? res.getBody() : null;
  }

  public String callMyMockApiWithResponseCode(int httpStatus) {

    log.info("callMyMockApiWithResponseCode httpStatus: {}", httpStatus);

    if (!appUtil.isValidHttpCode(httpStatus)) {
      return "input string is invalid";
    }

    // use httpbin (see how to start httpbin on README.md)
    var request = RequestEntity
        .get("http://0.0.0.0:80/status/" + httpStatus)
        .build();

    ResponseEntity<String> res;
    try {
      res = retryTemplate.execute(context ->
          restTemplate.exchange(
              request,
              String.class
          ));
    } catch (Exception e) {
      log.warn(e.getMessage());
      throw e;
    }

    return res.getStatusCode().is2xxSuccessful() ? res.getBody() : null;
  }

}
