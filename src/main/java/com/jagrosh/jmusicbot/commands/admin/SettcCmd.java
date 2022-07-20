/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SettcCmd extends AdminCommand {

  public SettcCmd(Bot bot) {
    this.name = "settc";
    this.help = "устанавливает текстовый канал для команд";
    this.arguments = "<channel|NONE>";
    this.options = Collections.singletonList(new OptionData(OptionType.STRING, "channel", "Текстовый канал для команд. NONE чтобы очистить.").setRequired(true));
    this.aliases = bot.getConfig().getAliases(this.name);
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    event.deferReply().queue();
    Settings s = event.getClient().getSettingsFor(event.getGuild());
    if (event.getOption("channel").getAsString().equalsIgnoreCase("none")) {
      s.setTextChannel(null);
      event.getHook().editOriginal(event.getClient().getSuccess() + " Теперь команды можно использовать повсюду")
        .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
    } else {
      List<TextChannel> list = FinderUtil.findTextChannels(event.getOption("channel").getAsString(), event.getGuild());
      if (list.isEmpty())
        event.getHook().editOriginal(event.getClient().getWarning() + " Нет текстового канала с названием \"" +
          event.getOption("channel").getAsString() + "\"")
          .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
      else if (list.size() > 1)
        event.getHook().editOriginal(event.getClient().getWarning() + FormatUtil.listOfTChannels(list, event.getOption("channel").getAsString()))
          .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
      else {
        s.setTextChannel(list.get(0));
        event.getHook().editOriginal(event.getClient().getSuccess() + " Музыкальные каналы теперь можно использовать только в <#" + list.get(0).getId() + ">")
          .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
      }
    }
  }

  @Override
  protected void execute(CommandEvent event) {
    if (event.getArgs().isEmpty()) {
      event.reply(event.getClient().getError() + " Напшите нужный канал или 'NONE' для очистки");
      return;
    }
    Settings s = event.getClient().getSettingsFor(event.getGuild());
    if (event.getArgs().equalsIgnoreCase("none")) {
      s.setTextChannel(null);
      event.reply(event.getClient().getSuccess() + " Теперь команды можно использовать повсюду");
    } else {
      List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
      if (list.isEmpty())
        event.reply(event.getClient().getWarning() + " Нет текстового канала с названием \"" + event.getArgs() + "\"");
      else if (list.size() > 1)
        event.reply(event.getClient().getWarning() + FormatUtil.listOfTChannels(list, event.getArgs()));
      else {
        s.setTextChannel(list.get(0));
        event.reply(event.getClient().getSuccess() + " Музыкальные каналы теперь можно использовать только в <#" + list.get(0).getId() + ">");
      }
    }
  }
}
