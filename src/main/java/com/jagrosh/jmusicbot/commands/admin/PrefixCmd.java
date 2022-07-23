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
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.settings.Settings;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class PrefixCmd extends AdminCommand {

  public PrefixCmd(Bot bot) {
    this.name = "prefix";
    this.help = "выбирает префикс для определенного сервера";
    this.arguments = "<prefix|NONE>";
    this.options = Collections.singletonList(new OptionData(OptionType.STRING, "prefix", "Префикс для команд. NONE чтобы очистить.").setRequired(true));
    this.aliases = bot.getConfig().getAliases(this.name);
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    event.deferReply().queue();
    Settings s = event.getClient().getSettingsFor(event.getGuild());
    if (event.getOption("prefix").getAsString().equalsIgnoreCase("none")) {
      s.setPrefix(null);
      event.getHook().editOriginal("Префикс очищен.")
        .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
    } else {
      s.setPrefix(event.getOption("prefix").getAsString());
      event.getHook().editOriginal("Префикс на сервере **" + event.getGuild().getName() + "** изменен на '" +
          event.getOption("prefix").getAsString() + "'")
        .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
    }
  }

  @Override
  protected void execute(CommandEvent event) {
    if (event.getArgs().isEmpty()) {
      event.replyError("Напшите нужный префикс или 'NONE' для очистки");
      return;
    }

    Settings s = event.getClient().getSettingsFor(event.getGuild());
    if (event.getArgs().equalsIgnoreCase("none")) {
      s.setPrefix(null);
      event.replySuccess("Префикс очищен.");
    } else {
      s.setPrefix(event.getArgs());
      event.replySuccess("Префикс на сервере **" + event.getGuild().getName() + "** изменен на '" + event.getArgs() + "'");
    }
  }
}
