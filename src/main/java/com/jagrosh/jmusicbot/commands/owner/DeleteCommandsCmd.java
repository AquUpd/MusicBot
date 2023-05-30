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
package com.jagrosh.jmusicbot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import com.jagrosh.jmusicbot.settings.Settings;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;

public class DeleteCommandsCmd extends OwnerCommand {

  public DeleteCommandsCmd(Bot bot) {
    super(bot);
    this.name = "deletecommands";
    this.help = bot.getTextUtils().localizeDefault("commands_deletecommands_help");
    this.aliases = bot.getConfig().getAliases(this.name);
    this.guildOnly = true;
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    event.deferReply().queue();
    Settings s = event.getClient().getSettingsFor(event.getGuild());

    List<Command> commands = event.getGuild().retrieveCommands().complete();
    if (commands.isEmpty()) event.getHook().editOriginal(bot.getTextUtils().localizeError(s.getMultiLocale(), "commands_deletecommands_error_empty")).queue();
    else {
      int i = commands.size();
      for (Command command: commands) {
        event.getGuild().deleteCommandById(command.getIdLong()).submit();
      }
      event.getHook().editOriginal(bot.getTextUtils().localizeSuccess(s.getMultiLocale(), "commands_deletecommands_success", i)).queue();
    }
  }

  @Override
  protected void execute(CommandEvent event) {
    Settings s = event.getClient().getSettingsFor(event.getGuild());

    List<Command> commands = event.getGuild().retrieveCommands().complete();
    if (commands.isEmpty()) event.reply(bot.getTextUtils().localizeError(s.getMultiLocale(), "commands_deletecommands_error_empty"));
    else {
      int i = commands.size();
      for (Command command: commands) {
        event.getGuild().deleteCommandById(command.getIdLong()).submit();
      }
      event.reply(bot.getTextUtils().localizeSuccess(s.getMultiLocale(), "commands_deletecommands_success", i));
    }
  }
}
