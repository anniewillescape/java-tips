package annie.tips.controller;

import annie.tips.service.CallApiService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CallApiController {

  @NonNull
  private final CallApiService callApiService;

  @GetMapping("call-mock-api")
  public String callMockApi(){
    return callApiService.callMyMockApi();
  }

}
