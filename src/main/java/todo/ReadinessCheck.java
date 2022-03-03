package todo;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.jboss.logging.Logger;

@Readiness
@ApplicationScoped
public class ReadinessCheck implements HealthCheck {
  @Inject Logger logger;

  @ConfigProperty(name = "app.readiness-wait-in-seconds", defaultValue = "0")
  String waitTime;

  private final Instant initializationTime = Instant.now();

  private boolean isReady() {
    Instant readyTime = initializationTime.plusSeconds(Integer.parseInt(waitTime));
    if (Instant.now().isBefore(readyTime)) {
      long secondsLeft = Instant.now().until(readyTime, ChronoUnit.SECONDS);
      logger.info("App not ready. Please be patient for another " + secondsLeft + " seconds.");
      return false;
    } else {
      logger.info("App ready.");
      return true;
    }
  }

  @Override
  public HealthCheckResponse call() {
    return HealthCheckResponse.builder()
            .name("custom-readiness")
            .status(isReady())
            .build();
  }
}
