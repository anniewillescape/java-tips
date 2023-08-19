package annie.tips.config;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

@Configuration
public class RequestLoggingConfig {

  @Bean
  public RequestLoggingFilter requestLoggingFilter() {
    return new RequestLoggingFilter();
  }


  @Slf4j
  public static class RequestLoggingFilter extends AbstractRequestLoggingFilter{

    /**
     * Writes a log message before the request is processed
     */
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
      log.info(message);
    }

    /**
     * no-op
     */
    @Override
    protected void afterRequest(HttpServletRequest request, String message) {

    }

    @Override
    protected String createMessage(HttpServletRequest request, String prefix, String suffix){
      var msg = new StringBuilder();
      msg.append(prefix);
      msg.append(request.getMethod()).append(' ');
      msg.append(request.getRequestURI());

      var queryString = request.getQueryString();
      if (queryString != null) {
        msg.append('?').append(queryString);
      }

      var client = request.getRemoteAddr();
      if (StringUtils.hasLength(client)) {
        msg.append(", client=").append(client);
      }

      var session = request.getSession(false);
      if (session != null) {
        msg.append(", session=").append(session);
      }

      var user = request.getRemoteUser();
      if (user != null){
        msg.append(", user=").append(user);
      }

      var xff = request.getHeader("X-Forwarded-for");
      if (StringUtils.hasLength(xff)){
        msg.append(", xff=").append(xff);
      }

      msg.append(suffix);
      return msg.toString();
    }
  }
}
