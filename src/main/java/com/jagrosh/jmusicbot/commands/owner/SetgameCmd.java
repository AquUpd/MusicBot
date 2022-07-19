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
import net.dv8tion.jda.api.entities.Activity;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SetgameCmd extends OwnerCommand {

  public SetgameCmd(Bot bot) {
    this.name = "setgame";
    this.help = "устанавливает игру в которую играет бот";
    this.arguments = "[action] [game]";
    this.aliases = bot.getConfig().getAliases(this.name);
    this.guildOnly = false;
    this.children = new OwnerCommand[]{new SetlistenCmd(), new SetstreamCmd(), new SetwatchCmd(),};
  }

  @Override
  protected void execute(SlashCommandEvent event) {

  }

  @Override
  protected void execute(CommandEvent event) {
    String title = event.getArgs().toLowerCase().startsWith("playing") ? event.getArgs().substring(7).trim() : event.getArgs();
    try {
      event.getJDA().getPresence().setActivity(title.isEmpty() ? null : Activity.playing(title));
      event.reply(event.getClient().getSuccess() + " **" + event.getSelfUser().getName() + "** теперь " + (title.isEmpty() ? "ничего не играет." : "играет в `" + title + "`"));
    } catch (Exception e) {
      event.reply(event.getClient().getError() + " Не удалось установить игру!");
    }
  }

  private class SetstreamCmd extends OwnerCommand {

    private SetstreamCmd() {
      this.name = "stream";
      this.aliases = new String[]{"twitch", "streaming"};
      this.help = "отображает стрим другого человека";
      this.arguments = "<username> <game>";
      this.guildOnly = false;
    }

    @Override
    protected void execute(SlashCommandEvent event) {

    }

    @Override
    protected void execute(CommandEvent event) {
      String[] parts = event.getArgs().split("\\s+", 2);
      if (parts.length < 2) {
        event.replyError("Напишите название канала и название стрима");
        return;
      }
      try {
        event.getJDA().getPresence().setActivity(Activity.streaming(parts[1], "https://twitch.tv/" + parts[0]));
        event.replySuccess("**" + event.getSelfUser().getName() + "** теперь стримит `" + parts[1] + "`");
      } catch (Exception e) {
        event.reply(event.getClient().getError() + " Не удалось установить стрим!");
      }
    }
  }

  private class SetlistenCmd extends OwnerCommand {

    private SetlistenCmd() {
      this.name = "listen";
      this.aliases = new String[]{"listening"};
      this.help = "устанавливает что слушает бот";
      this.arguments = "<title>";
      this.guildOnly = false;
    }

    @Override
    protected void execute(SlashCommandEvent event) {

    }

    @Override
    protected void execute(CommandEvent event) {
      if (event.getArgs().isEmpty()) {
        event.replyError("Напишите название того, что слушает бот!");
        return;
      }
      String title = event.getArgs().toLowerCase().startsWith("to") ? event.getArgs().substring(2).trim() : event.getArgs();
      try {
        event.getJDA().getPresence().setActivity(Activity.listening(title));
        event.replySuccess("**" + event.getSelfUser().getName() + "** теперь слушает `" + title + "`");
      } catch (Exception e) {
        event.reply(event.getClient().getError() + " Не удалось установить пластинку!");
      }
    }
  }

  private class SetwatchCmd extends OwnerCommand {

    private SetwatchCmd() {
      this.name = "watch";
      this.aliases = new String[]{"watching"};
      this.help = "показывает что смотрит бот";
      this.arguments = "<title>";
      this.guildOnly = false;
    }

    @Override
    protected void execute(SlashCommandEvent event) {

    }

    @Override
    protected void execute(CommandEvent event) {
      if (event.getArgs().isEmpty()) {
        event.replyError("Напишите название того, что смотрит бот!");
        return;
      }
      String title = event.getArgs();
      try {
        event.getJDA().getPresence().setActivity(Activity.watching(title));
        event.replySuccess("**" + event.getSelfUser().getName() + "** теперь смотрит `" + title + "`");
      } catch (Exception e) {
        event.reply(event.getClient().getError() + " не удалось показать, что смотрит бот!");
      }
    }
  }
}
