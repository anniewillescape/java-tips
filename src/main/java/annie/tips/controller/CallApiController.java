package annie.tips.controller;

import annie.tips.service.CallApiService;
import annie.tips.util.AppUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CallApiController {

  @NonNull
  private final CallApiService callApiService;

  @NonNull
  private final AppUtil appUtil;


  @GetMapping("call-mock-api/{status}")
  public String callMockApi(@PathVariable(name = "status", required = false) String status) {
    
    if (StringUtils.isBlank(status) || !appUtil.canParseToInt(status)) {
      return callApiService.callMyMockApi();
    }
    return callApiService.callMyMockApiWithResponseCode(Integer.parseInt(status));
  }

}
