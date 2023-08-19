package annie.tips.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppUtil {


  public boolean isValidHttpCode(int input){

    log.info("isValidHttpCode input: {}", input);

    try {
      HttpStatus.valueOf(input);
      return true;
    } catch (IllegalArgumentException e) {
      log.info(e.getMessage());
      return false;
    }
  }


  public boolean canParseToInt(String input){

    log.info("canParseToInt input: {}", input);

    try {
      Integer.valueOf(input);
      return true;
    } catch (NumberFormatException e) {
      log.info(e.getMessage());
      return false;
    }
  }
}
