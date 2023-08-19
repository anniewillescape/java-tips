package annie.tips.service;

import annie.tips.util.AppUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
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

    try {
      var res = restTemplate.exchange(
          // use httpbin (see how to start httpbin on README.md)
          "http://0.0.0.0:80/get",
          HttpMethod.GET,
          null,
          String.class
      );
      return res.getBody();
    }catch(Exception e){
      log.warn(e.getMessage());
      throw e;
    }
  }

  public String callMyMockApiWithResponseCode(int httpStatus) {

    log.info("callMyMockApiWithResponseCode httpStatus: {}", httpStatus);

    if (!appUtil.isValidHttpCode(httpStatus)) {
      return "input string is invalid";
    }

    String res;
    try {
      res = retryTemplate.execute(context ->
          restTemplate.exchange(
              // use httpbin (see how to start httpbin on README.md)
              "http://0.0.0.0:80/status/" + httpStatus,
              HttpMethod.GET,
              null,
              String.class
          ).getBody());
    } catch (Exception e) {
      log.warn(e.getMessage());
      throw e;
    }

    return res;
  }

}
