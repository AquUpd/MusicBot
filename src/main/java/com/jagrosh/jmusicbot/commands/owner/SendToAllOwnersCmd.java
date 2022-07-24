package com.jagrosh.jmusicbot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SendToAllOwnersCmd extends OwnerCommand {

  public SendToAllOwnersCmd(Bot bot) {
    this.name = "sendtoallowners";
    this.help = "отправляет сообщения всем создателям гильдий";
    this.arguments = "text";
    this.options = Collections.singletonList(new OptionData(OptionType.STRING, "text", "сообщение которое нужно отправить").setRequired(true));
    this.aliases = bot.getConfig().getAliases(this.name);
    this.guildOnly = false;
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    event.deferReply().queue();
    for (Guild g: event.getJDA().getGuilds()) {
      g.getOwner().getUser().openPrivateChannel().flatMap(m -> m.sendMessage(event.getOption("text").getAsString())).queue();
    }
    event.getHook().editOriginal("done!")
      .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
  }

  @Override
  protected void execute(CommandEvent event) {
    for (Guild g: event.getJDA().getGuilds()) {
      g.getOwner().getUser().openPrivateChannel().flatMap(m -> m.sendMessage(event.getArgs())).queue();
    }
    event.reply("done!");
  }
}
