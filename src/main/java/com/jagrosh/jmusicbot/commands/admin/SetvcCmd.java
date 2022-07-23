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
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SetvcCmd extends AdminCommand {

  public SetvcCmd(Bot bot) {
    this.name = "setvc";
    this.help = "Устанавливает голосовой канал для использования пластинок";
    this.arguments = "<channel|NONE>";
    this.options = Collections.singletonList(new OptionData(OptionType.CHANNEL, "channel", "Голосовой канал для бота. NONE чтобы очистить.").setRequired(true));

    this.aliases = bot.getConfig().getAliases(this.name);
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    event.deferReply().queue();
    Settings s = event.getClient().getSettingsFor(event.getGuild());
    if (event.getOption("channel").getAsString().equalsIgnoreCase("none")) {
      s.setVoiceChannel(null);
      event.getHook().editOriginal(event.getClient().getSuccess() + " Пластинки теперь могут проигрываться повсюду")
        .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
    } else {
      List<VoiceChannel> list = FinderUtil.findVoiceChannels(event.getOption("channel").getAsString(), event.getGuild());
      if (list.isEmpty())
        event.getHook().editOriginal(event.getClient().getWarning() + " Нет Войс Каналов с таким названием \"" + event.getOption("channel").getAsString() + "\"")
          .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
      else if (list.size() > 1)
        event.getHook().editOriginal(event.getClient().getWarning() + FormatUtil.listOfVChannels(list, event.getOption("channel").getAsString()))
          .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
      else {
        s.setVoiceChannel(list.get(0));
        event.getHook().editOriginal(event.getClient().getSuccess() + " Пластинки теперь могут проигрываться только в " + list.get(0).getAsMention())
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
      s.setVoiceChannel(null);
      event.reply(event.getClient().getSuccess() + " Пластинки теперь могут проигрываться повсюду");
    } else {
      List<VoiceChannel> list = FinderUtil.findVoiceChannels(event.getArgs(), event.getGuild());
      if (list.isEmpty())
        event.reply(event.getClient().getWarning() + " Нет Войс Каналов с таким названием \"" + event.getArgs() + "\"");
      else if (list.size() > 1)
        event.reply(event.getClient().getWarning() + FormatUtil.listOfVChannels(list, event.getArgs()));
      else {
        s.setVoiceChannel(list.get(0));
        event.reply(event.getClient().getSuccess() + " Пластинки теперь могут проигрываться только в " + list.get(0).getAsMention());
      }
    }
  }
}
