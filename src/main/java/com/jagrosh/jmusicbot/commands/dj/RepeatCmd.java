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
package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.settings.RepeatMode;
import com.jagrosh.jmusicbot.settings.Settings;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class RepeatCmd extends DJCommand {

  public RepeatCmd(Bot bot) {
    super(bot);
    this.name = "repeat";
    this.help = "добавляет пластинку каждый раз когда она заканичвается";
    this.arguments = "[off|all|single]";
    this.options = Collections.singletonList(new OptionData(OptionType.STRING, "mode", "режим повтора").addChoice("Выключить", "off").addChoice("Повтор всех пластинок", "all").addChoice("Повтор одной пластинки", "single").setRequired(false));
    this.aliases = bot.getConfig().getAliases(this.name);
    this.guildOnly = true;
  }

  // override musiccommand's execute because we don't actually care where this is used
  @Override
  protected void execute(CommandEvent event) {
    String args = event.getArgs();
    RepeatMode value;
    Settings settings = event.getClient().getSettingsFor(event.getGuild());
    if (args.isEmpty()) {
      if (settings.getRepeatMode() == RepeatMode.OFF) value = RepeatMode.ALL; else value = RepeatMode.OFF;
    } else if (args.equalsIgnoreCase("false") || args.equalsIgnoreCase("off")) {
      value = RepeatMode.OFF;
    } else if (args.equalsIgnoreCase("true") || args.equalsIgnoreCase("on") || args.equalsIgnoreCase("all")) {
      value = RepeatMode.ALL;
    } else if (args.equalsIgnoreCase("one") || args.equalsIgnoreCase("single")) {
      value = RepeatMode.SINGLE;
    } else {
      event.replyError("Разрешенные параметры: `off`, `all` или `single`");
      return;
    }
    settings.setRepeatMode(value);
    event.replySuccess("Режим повтора пластинок: `" + value.getUserFriendlyName() + "`");
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    event.deferReply().queue();
    String args = event.getOption("mode").getAsString();
    RepeatMode value;
    Settings settings = event.getClient().getSettingsFor(event.getGuild());
    if (!event.hasOption("mode")) {
      if (settings.getRepeatMode() == RepeatMode.OFF) value = RepeatMode.ALL; else value = RepeatMode.OFF;
    } else if (args.equalsIgnoreCase("false") || args.equalsIgnoreCase("off")) {
      value = RepeatMode.OFF;
    } else if (args.equalsIgnoreCase("true") || args.equalsIgnoreCase("on") || args.equalsIgnoreCase("all")) {
      value = RepeatMode.ALL;
    } else if (args.equalsIgnoreCase("one") || args.equalsIgnoreCase("single")) {
      value = RepeatMode.SINGLE;
    } else {
      event.getHook().editOriginal("Разрешенные параметры: `off`, `all` или `single`")
        .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
      return;
    }
    settings.setRepeatMode(value);
    event.getHook().editOriginal("Режим повтора пластинок: `" + value.getUserFriendlyName() + "`")
      .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
  }

  @Override
  public void doCommand(CommandEvent event) {
    /* Intentionally Empty */
  }

  @Override
  public void doSlashCommand(SlashCommandEvent event) {
    // ^^^
  }
}
