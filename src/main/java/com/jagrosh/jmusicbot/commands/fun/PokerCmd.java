package com.jagrosh.jmusicbot.commands.fun;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.FunCommand;
import com.jagrosh.jmusicbot.utils.DefaultContentTypeInterceptor;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import okhttp3.*;
import org.json.JSONObject;

public class PokerCmd extends FunCommand {

  public PokerCmd(Bot bot) {
    super(bot);
    this.name = "poker";
    this.help = "запускает Покер";
    this.botPermissions = new Permission[] { Permission.MESSAGE_EMBED_LINKS };
    this.beInChannel = true;
  }

  //755827207812677713
  @Override
  public void doCommand(CommandEvent event) {
    String code = genLink(event.getMember().getVoiceState().getChannel().getId(), 755827207812677713L);
    if(code != null) event.reply("https://discord.com/invite/" + code);
    else event.replyError("Я не смог создать ссылку");
  }

  @Override
  public void doSlashCommand(SlashCommandEvent event) {
    String code = genLink(event.getMember().getVoiceState().getChannel().getId(), 755827207812677713L);
    if(code != null)
      event.getHook().editOriginal("https://discord.com/invite/" + code).queue();
    else
      event.getHook().editOriginal("Я не смог создать ссылку")
        .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
  }

  private String genLink(String channelId, long gameId) {
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
