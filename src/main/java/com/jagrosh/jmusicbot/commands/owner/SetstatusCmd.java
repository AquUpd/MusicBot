/*
 * Copyright 2017 John Grosh <john.a.grosh@gmail.com>.
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
import net.dv8tion.jda.api.OnlineStatus;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SetstatusCmd extends OwnerCommand {

  public SetstatusCmd(Bot bot) {
    this.name = "setstatus";
    this.help = "устанавливает статус что делает бот";
    this.arguments = "<status>";
    this.aliases = bot.getConfig().getAliases(this.name);
    this.guildOnly = false;
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    event.deferReply().queue();

  }

  @Override
  protected void execute(CommandEvent event) {
    try {
      OnlineStatus status = OnlineStatus.fromKey(event.getArgs());
      if (status == OnlineStatus.UNKNOWN) {
        event.replyError("Напишите подходящий статус: `ONLINE`, `IDLE`, `DND`, `INVISIBLE`");
      } else {
        event.getJDA().getPresence().setStatus(status);
        event.replySuccess("Установлен `" + status.getKey().toUpperCase() + "` статус");
      }
    } catch (Exception e) {
      event.reply(event.getClient().getError() + " Статус невозможно установить!");
    }
  }
}
