package com.jagrosh.jmusicbot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SendToAllOwnersCmd extends OwnerCommand {

  List<User> sendUsers = new LinkedList<>();

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
    for (Guild g: event.getJDA().getGuilds()) {
      try {
        User owner = g.getOwner().getUser();
        if(!sendUsers.contains(owner)) {
          owner.openPrivateChannel().flatMap(m -> m.sendMessage(event.getOption("text").getAsString())).queue();
          System.out.println("Получилось отправить сообщение пользователю " + owner.getName() + " с id " + owner.getIdLong() + "");
          sendUsers.add(owner);
        }
      } catch (Exception ex) {
        System.out.println("Не получилось отправить сообщение пользователю");
      }
    }
    event.getHook().editOriginal("done!")
      .queue();
  }

  @Override
  protected void execute(CommandEvent event) {
    for (Guild g: event.getJDA().getGuilds()) {
      try {
        User owner = g.getOwner().getUser();
        if(!sendUsers.contains(owner)) {
          owner.openPrivateChannel().flatMap(m -> m.sendMessage(event.getArgs())).queue();
          System.out.println("Получилось отправить сообщение пользователю " + owner.getName() + " с id " + owner.getIdLong() + "");
          sendUsers.add(owner);
        }
      } catch (Exception ex) {
        System.out.println("Не получилось отправить сообщение пользователю");
      }
    }
    event.reply("done!");
  }
}
