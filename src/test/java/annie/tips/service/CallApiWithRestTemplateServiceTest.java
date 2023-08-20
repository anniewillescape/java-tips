package annie.tips.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import annie.tips.config.RetryTemplateConfig;
import annie.tips.util.AppUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

class CallApiWithRestTemplateServiceTest {

  @InjectMocks
  private CallApiWithRestTemplateService target;

  @Mock
  private RestTemplate restTemplate;

  @Spy
  private RetryTemplate retryTemplate = new RetryTemplateConfig().retryTemplate();

  @Spy
  private AppUtil appUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  class testCallMyMockApiWithResponseCode_validation {

    @Test
    public void success() {
      when(restTemplate.exchange(any(), eq(String.class)))
          .thenReturn(ResponseEntity.ok("success"));

      assertEquals("success", target.callMyMockApiWithResponseCode(200));
    }
    @Test
    public void failed() {
      assertEquals("input string is invalid", target.callMyMockApiWithResponseCode(999));
    }
  }

  @Nested
  public class callMyMockApiWithResponseCode_retry{

    @Test
    void success() {

      ArgumentCaptor<RequestEntity<String>> entityCaptor = ArgumentCaptor.forClass(RequestEntity.class);
      when(restTemplate.exchange(any(), eq(String.class)))
          .thenReturn(ResponseEntity.ok("success"));

      assertEquals("success", target.callMyMockApiWithResponseCode(200));
      verify(restTemplate, times(1)).exchange(entityCaptor.capture(), eq(String.class));
    }

    @Test
    void successAfter1Fail() {

      ArgumentCaptor<RequestEntity<String>> entityCaptor = ArgumentCaptor.forClass(RequestEntity.class);
      when(restTemplate.exchange(any(), eq(String.class)))
          .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE))
          .thenReturn(ResponseEntity.ok("success"));

      assertEquals("success", target.callMyMockApiWithResponseCode(200));
      verify(restTemplate, times(2)).exchange(entityCaptor.capture(), eq(String.class));
    }

    @Test
    void successAfter3Fail() {

      ArgumentCaptor<RequestEntity<String>> entityCaptor = ArgumentCaptor.forClass(RequestEntity.class);
      when(restTemplate.exchange(any(), eq(String.class)))
          .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE))
          .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE))
          .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE))
          .thenReturn(ResponseEntity.ok("success"));

      assertEquals("success", target.callMyMockApiWithResponseCode(200));
      verify(restTemplate, times(4)).exchange(entityCaptor.capture(), eq(String.class));
    }

    @Test
    void fail_successAfter4Fail() {

      ArgumentCaptor<RequestEntity<String>> entityCaptor = ArgumentCaptor.forClass(RequestEntity.class);
      when(restTemplate.exchange(any(), eq(String.class)))
          .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE))
          .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE))
          .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE))
          .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE))
          .thenReturn(ResponseEntity.ok("success"));

      assertThrows(HttpServerErrorException.class, () -> target.callMyMockApiWithResponseCode(200));
      verify(restTemplate, times(4)).exchange(entityCaptor.capture(), eq(String.class));
    }

    @Test
    void fail_allRequest() {

      ArgumentCaptor<RequestEntity<String>> entityCaptor = ArgumentCaptor.forClass(RequestEntity.class);
      when(restTemplate.exchange(any(), eq(String.class)))
          .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE))
          .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE))
          .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE))
          .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE))
          .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE));

      assertThrows(HttpServerErrorException.class, () -> target.callMyMockApiWithResponseCode(200));
      verify(restTemplate, times(4)).exchange(entityCaptor.capture(), eq(String.class));
    }
  }

}