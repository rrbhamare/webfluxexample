package com.example.demowebflux.config.client;

import com.example.demowebflux.config.property.IntegrationClientProperty;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Component
@Slf4j
public class IntegrationClient {

  private final IntegrationClientProperty integrationClientProperty;

  public IntegrationClient(IntegrationClientProperty integrationClientProperty) {
    this.integrationClientProperty = integrationClientProperty;
  }

  @Bean(name = "webClientIntegration")
  WebClient webClientIntegration(WebClient.Builder weBuilderBuilder) {

    Integer connectTimeOut = integrationClientProperty.getConnectionTimeout();
    Integer readTimeout = integrationClientProperty.getReadTimeout();

    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeOut)
        .responseTimeout(Duration.ofMillis(readTimeout)).doOnConnected(
            conn -> conn.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                .addHandlerLast(new WriteTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS)));

    weBuilderBuilder = weBuilderBuilder.baseUrl(integrationClientProperty.getUrl())
        // Set request out, in logger for webclient
        .filter(logRequestResponse()).defaultHeaders(httpHeaders -> {
          // Set the basic auth
          if (StringUtils.hasText(integrationClientProperty.getUser())) {
            httpHeaders.setBasicAuth(integrationClientProperty.getUser(),
                integrationClientProperty.getPassword());
          }
        });

    ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
    weBuilderBuilder = weBuilderBuilder.clientConnector(connector);

    return weBuilderBuilder.build();
  }

  /**
   * TO log the backend webclient request and response log.
   *
   * @return ExchangeFilterFunction
   */
  private static ExchangeFilterFunction logRequestResponse() {

    return (ClientRequest request, ExchangeFunction next) -> {
      logRequest(request);
      long start = System.currentTimeMillis();
      return next.exchange(request)
          .doOnNext((ClientResponse response) -> logResponse(request, response, start))
          .doOnError(e -> logError(request, e.getMessage(), start));
    };

  }

  private static void logError(ClientRequest request, String msg, long startTime){
    log.error(("WebClient request {} {} responded with {} status code in {} ms"), request.method(),
        request.url(), msg, System.currentTimeMillis() - startTime);
  }

  private static void logRequest(ClientRequest request) {
    log.info("WebClient request {} {} {}", request.method(), request.url(), request.headers());
  }

  private static void logResponse(ClientRequest request, ClientResponse response, long startTime) {
    log.info(("WebClient request {} {} responded with {} status code in {} ms"), request.method(),
        request.url(), response.rawStatusCode(), System.currentTimeMillis() - startTime);
  }

}
