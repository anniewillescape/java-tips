package annie.tips.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Service
public class CallApiService {

  @NonNull
  private final RestTemplate restTemplate;

  public String callMyMockApi(){

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

}
