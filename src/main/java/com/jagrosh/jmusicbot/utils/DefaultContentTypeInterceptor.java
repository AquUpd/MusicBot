package com.jagrosh.jmusicbot.utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class DefaultContentTypeInterceptor implements Interceptor {

  public Response intercept(Interceptor.Chain chain) throws IOException {
    Config config = ConfigFactory.load();

    Request originalRequest = chain.request();
    Request requestWithUserAgent = originalRequest.newBuilder()
      .header("Content-Type", "application/json").header("Authorization", "Bot " + config.getString("token"))
      .build();

    return chain.proceed(requestWithUserAgent);
  }
}
