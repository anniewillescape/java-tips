package annie.tips.config;

import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpStatusCodeException;

@Configuration
public class RetryTemplateConfig {

  private static final int DEFAULT_RETRY_COUNT = 3;
  private static final int DEFAULT_RETRY_INTERVAL = 150;

  private final SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(1 + DEFAULT_RETRY_COUNT);
  private final NeverRetryPolicy neverRetryPolicy = new NeverRetryPolicy();

  @Bean
  public RetryTemplate retryTemplate() {

    var retryTemplate = new RetryTemplate();

    var backOffPolicy = new ExponentialBackOffPolicy();
    backOffPolicy.setInitialInterval(DEFAULT_RETRY_INTERVAL);
    retryTemplate.setBackOffPolicy(backOffPolicy);

    var retryPolicy = new ExceptionClassifierRetryPolicy();
    retryPolicy.setExceptionClassifier(configureHttpStatusBaseRetryPolicy());

    return retryTemplate;
  }

  private Classifier<Throwable, RetryPolicy> configureHttpStatusBaseRetryPolicy(){

    return throwable -> {
      if (throwable instanceof HttpStatusCodeException) {
        var exception = (HttpStatusCodeException) throwable;
        return getRetryPolicyForStatus(exception.getStatusCode());
      }
      return neverRetryPolicy;
    };
  }

  private RetryPolicy getRetryPolicyForStatus(HttpStatus httpStatus){

    switch (httpStatus){
      case SERVICE_UNAVAILABLE:
        return simpleRetryPolicy;
      default:
        return neverRetryPolicy;
    }

  }
}


