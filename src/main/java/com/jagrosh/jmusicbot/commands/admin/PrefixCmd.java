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
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class PrefixCmd extends AdminCommand {

  public PrefixCmd(Bot bot) {
    super(bot);
    this.name = "prefix";
    this.help = bot.getTextUtils().localizeDefault("commands_setprefix_help");
    this.arguments = "<prefix|NONE>";
    OptionData prefixOption = new OptionData(OptionType.STRING, "prefix", bot.getTextUtils().localizeDefault("commands_setprefix_option_prefix")).setRequired(true);
    bot.getTextUtils().optionTranslation(prefixOption, "commands_setprefix_option_prefix");
    this.options = Collections.singletonList(prefixOption);
    this.aliases = bot.getConfig().getAliases(this.name);
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    event.deferReply().queue();
    Settings s = event.getClient().getSettingsFor(event.getGuild());

    if (event.getOption("prefix").getAsString().equalsIgnoreCase("none")) {
      s.setPrefix(null);
      event.getHook().editOriginal(bot.getTextUtils().localizeSuccess(s.getMultiLocale(), "commands_setprefix_success_cleared")).queue();
    } else {
      s.setPrefix(event.getOption("prefix").getAsString());
      event.getHook().editOriginal(bot.getTextUtils().localizeSuccess(s.getMultiLocale(), "commands_setprefix_success", event.getGuild().getName(), event.getOption("prefix").getAsString())).queue();
    }
  }

  @Override
  protected void execute(CommandEvent event) {
    Settings s = event.getClient().getSettingsFor(event.getGuild());

    if (event.getArgs().isEmpty()) {
      event.reply(bot.getTextUtils().localizeError(s.getMultiLocale(), "commands_setprefix_error_empty"));
      return;
    }

    String arg = event.getArgs();
    if (arg.equalsIgnoreCase("none")) {
      s.setPrefix(null);
      event.reply(bot.getTextUtils().localizeSuccess(s.getMultiLocale(), "commands_setprefix_success_cleared"));
    } else {
      s.setPrefix(event.getArgs());
      event.reply(bot.getTextUtils().localizeSuccess(s.getMultiLocale(), "commands_setprefix_success", event.getGuild().getName(), arg));
    }
  }
}
