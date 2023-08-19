package annie.tips.controller;

import annie.tips.service.CallApiWithRestTemplateService;
import annie.tips.service.CallApiWithWebClientService;
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
  private final CallApiWithRestTemplateService callApiWithRestTemplateService;

  @NonNull
  private final CallApiWithWebClientService callApiWithWebClientService;

  @NonNull
  private final AppUtil appUtil;


  @GetMapping("call-mock-api/rest-template/{status}")
  public String callMockApiWithRestTemplate(
      @PathVariable(name = "status", required = false) String status) {

    if (StringUtils.isBlank(status) || !appUtil.canParseToInt(status)) {
      return callApiWithRestTemplateService.callMyMockApi();
    }
    return callApiWithRestTemplateService.callMyMockApiWithResponseCode(Integer.parseInt(status));
  }

  @GetMapping("call-mock-api/web-client/{status}")
  public String callMockApiWithWebClient(
      @PathVariable(name = "status", required = false) String status) {

    if (StringUtils.isBlank(status) || !appUtil.canParseToInt(status)) {
      return callApiWithWebClientService.callMyMockApi();
    }
    return callApiWithWebClientService.callMyMockApiWithResponseCode(Integer.parseInt(status));
  }

}
