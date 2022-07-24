/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import com.jagrosh.jmusicbot.settings.Settings;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class AutoplaylistCmd extends OwnerCommand {

  private final Bot bot;

  public AutoplaylistCmd(Bot bot) {
    this.bot = bot;
    this.guildOnly = true;
    this.name = "autoplaylist";
    this.arguments = "<name|NONE>";
    this.options = Collections.singletonList(new OptionData(OptionType.STRING, "name", "Название плейлиста или NONE").setRequired(true));
    this.help = "делает автоматический плейлист который будет воспроизоводится при входе в канал";
    this.aliases = bot.getConfig().getAliases(this.name);
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    event.deferReply().queue();
    String args = event.getOption("name").getAsString();
    if (args.equalsIgnoreCase("none")) {
      Settings settings = event.getClient().getSettingsFor(event.getGuild());
      settings.setDefaultPlaylist(null);
      event.getHook().editOriginal(event.getClient().getSuccess() + " Убран автоматический плейлист для **" + event.getGuild().getName() + "**")
        .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
      return;
    }
    String pname = args.replaceAll("\\s+", "_");
    if (bot.getPlaylistLoader().getPlaylist(pname) == null) {
      event.getHook().editOriginal(event.getClient().getError() + " Не могу найти `" + pname + ".txt`!")
        .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
    } else {
      Settings settings = event.getClient().getSettingsFor(event.getGuild());
      settings.setDefaultPlaylist(pname);
      event.getHook().editOriginal(event.getClient().getSuccess() + " Автоматический плейлист для **" + event.getGuild().getName() + "** теперь `" + pname + "`")
        .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
    }
  }

  @Override
  public void execute(CommandEvent event) {
    if (event.getArgs().isEmpty()) {
      event.reply(event.getClient().getError() + " Пожалуйста напишите название плейлиста или NONE");
      return;
    }
    if (event.getArgs().equalsIgnoreCase("none")) {
      Settings settings = event.getClient().getSettingsFor(event.getGuild());
      settings.setDefaultPlaylist(null);
      event.reply(event.getClient().getSuccess() + " Убран автоматический плейлист для **" + event.getGuild().getName() + "**");
      return;
    }
    String pname = event.getArgs().replaceAll("\\s+", "_");
    if (bot.getPlaylistLoader().getPlaylist(pname) == null) {
      event.reply(event.getClient().getError() + " Не могу найти `" + pname + ".txt`!");
    } else {
      Settings settings = event.getClient().getSettingsFor(event.getGuild());
      settings.setDefaultPlaylist(pname);
      event.reply(event.getClient().getSuccess() + " Автоматический плейлист для **" + event.getGuild().getName() + "** теперь `" + pname + "`");
    }
  }
}
