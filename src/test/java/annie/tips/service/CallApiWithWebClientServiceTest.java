package annie.tips.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import annie.tips.util.AppUtil;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;


class CallApiWithWebClientServiceTest {

  @InjectMocks
  private CallApiWithWebClientService target;

  @Spy
  private AppUtil appUtil;

  public static MockWebServer mockServer;
  private static String baseUrl;

  @BeforeEach
  public void init() throws IOException {
    MockitoAnnotations.openMocks(this);
  }

  @ParameterizedTest
  @MethodSource("testCallMyMockApiWithResponseCode_condition")
  public void testCallMyMockApiWithResponseCode_retry(List<Integer> codes, int expectedRequestCount,
      Class expectedExceptionClass)
      throws IOException {

    mockServer = new MockWebServer();
    baseUrl = "http://localhost:" + mockServer.getPort();

    codes.forEach(code -> mockServer.enqueue(new MockResponse().setResponseCode(code)));

    var webclient = WebClient.builder()
        .baseUrl(mockServer.url("/status/200").url().toString().replace("status/200", ""))
        .build();

    ReflectionTestUtils.setField(target, "webClient", webclient);

    if (expectedExceptionClass == null) {
      target.callMyMockApiWithResponseCode(200);
    }
    else {
      assertThrows(expectedExceptionClass, () -> target.callMyMockApiWithResponseCode(200));
    }

    assertEquals(expectedRequestCount, mockServer.getRequestCount());
    mockServer.shutdown();
  }

  private static Stream<Arguments> testCallMyMockApiWithResponseCode_condition() {
    return Stream.of(
        // normal
        arguments(List.of(200), 1, null),

        // success after 1 failure
        arguments(List.of(503, 200), 2, null),

        // success after 3 failures
        arguments(List.of(503, 503, 503, 200), 4, null),

        // Failure: success after 4 failures
        arguments(List.of(503, 503, 503, 503, 200), 4, RuntimeException.class),

        // Failure: all 5 failure
        arguments(List.of(503, 503, 503, 503, 503), 4, RuntimeException.class)
    );
  }

  @ParameterizedTest
  @CsvSource({
      "200, Optional.empty",
      "999, input http status is invalid",
  })
  public void testCallMyMockApiWithResponseCode_validation(Integer code, String expectedResult)
      throws IOException {

    mockServer = new MockWebServer();
    baseUrl = "http://localhost:" + mockServer.getPort();

    mockServer.enqueue(new MockResponse().setResponseCode(code));

    var webclient = WebClient.builder()
        .baseUrl(mockServer.url("/status/" + code).url().toString().replace("status/" + code, ""))
        .build();

    ReflectionTestUtils.setField(target, "webClient", webclient);

    assertEquals(expectedResult, target.callMyMockApiWithResponseCode(code));
    mockServer.shutdown();
  }
}