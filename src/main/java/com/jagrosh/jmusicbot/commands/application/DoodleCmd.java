package com.jagrosh.jmusicbot.commands.application;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.ApplicationCommand;
import com.jagrosh.jmusicbot.utils.DefaultContentTypeInterceptor;
import net.dv8tion.jda.api.Permission;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;


public class DoodleCmd extends ApplicationCommand {
    public DoodleCmd(Bot bot)
    {
        super(bot);
        this.name = "doodle";
        this.help = "запускает игру \"Doodle Crew\"";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.beInChannel = true;
    }

    @Override
    public void doCommand(CommandEvent event) throws IOException {
        String current = event.getMember().getVoiceState().getChannel().getId();
        URL url = new URL ("https://discord.com/api/v8/channels/" + current + "/invites");
        String postBody = "{\"max_age\": \"86400\", \"max_uses\": 0, \"target_application_id\":\"878067389634314250\", \"target_type\":2, \"temporary\": false, \"validate\": null}";

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), postBody);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new DefaultContentTypeInterceptor()).build();

        Request request = new Request.Builder().url(url).post(body).build();

        Call call = client.newCall(request);
        Response response = call.execute();
        JSONObject obj = new JSONObject(response.body().string());
        String code = obj.getString("code");
        event.reply("https://discord.com/invite/" + code);
        response.close();
    }
}
