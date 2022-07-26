package com.jagrosh.jmusicbot.commands.fun;

import com.jagrosh.jmusicbot.utils.DefaultContentTypeInterceptor;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class FunUtils {

  public static String genLink(String channelId, long gameId) {
    try {

      URL url = new URL("https://discord.com/api/v8/channels/" + channelId + "/invites");
      String postBody = "{\"max_age\": \"86400\", \"max_uses\": 0, \"target_application_id\":\""+ gameId +"\", \"target_type\":2, \"temporary\": false, \"validate\": null}";

      RequestBody body = RequestBody.create(MediaType.parse("application/json"), postBody);
      OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new DefaultContentTypeInterceptor()).build();
      Request request = new Request.Builder().url(url).post(body).build();

      Call call = client.newCall(request);
      Response response = call.execute();
      JSONObject obj = new JSONObject(response.body().string());
      String code = obj.getString("code");
      response.close();
      return code;
    } catch (IOException exception) {
      return null;
    }
  }
}
