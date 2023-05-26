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
package com.jagrosh.jmusicbot.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Message;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PlaylistsCmd extends MusicCommand {

  public PlaylistsCmd(Bot bot) {
    super(bot);
    this.name = "playlists";
    this.help = "показывает доступные плейлисты";
    this.aliases = bot.getConfig().getAliases(this.name);
    this.guildOnly = true;
    this.beListening = false;
  }

  @Override
  public void doCommand(CommandEvent event) {
    if (!bot.getPlaylistLoader().folderExists(event.getGuild()))
      bot.getPlaylistLoader().createFolder(event.getGuild());
    if (!bot.getPlaylistLoader().folderExists(event.getGuild())) {
      event.reply(event.getClient().getWarning() + " Папки с плейлистами не существует и она будет создана!");
      return;
    }
    List<String> list = bot.getPlaylistLoader().getPlaylistNames(event.getGuild());
    if (list == null)
      event.reply(event.getClient().getError() + " Не удалось получить список плейлистов!");
    else if (list.isEmpty())
      event.reply(event.getClient().getWarning() + " В папке плейлистов нет плейлистов(грустно)!");
    else {
      StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + " Доступные плейлисты:\n");
      list.forEach(str -> builder.append("`").append(str).append("` "));
      builder.append("\nнапишите `").append(event.getClient().getTextualPrefix()).append("play playlist <название>` чтобы включить плейлист");
      event.reply(builder.toString());
    }
  }

  @Override
  public void doSlashCommand(SlashCommandEvent event) {
    if (!bot.getPlaylistLoader().folderExists(event.getGuild()))
      bot.getPlaylistLoader().createFolder(event.getGuild());
    if (!bot.getPlaylistLoader().folderExists(event.getGuild())) {
      event.getHook().editOriginal(event.getClient().getWarning() + " Папки с плейлистами не существует и она будет создана!")
        .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
      return;
    }
    List<String> list = bot.getPlaylistLoader().getPlaylistNames(event.getGuild());
    if (list == null)
      event.getHook().editOriginal(event.getClient().getError() + " Не удалось получить список плейлистов!")
        .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
    else if (list.isEmpty())
      event.getHook().editOriginal(event.getClient().getWarning() + " В папке плейлистов нет плейлистов(грустно)!")
        .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
    else {
      StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + " Доступные плейлисты:\n");
      list.forEach(str -> builder.append("`").append(str).append("` "));
      builder.append("\nнапишите `").append(event.getClient().getTextualPrefix()).append("pplaylist <название>` чтобы включить плейлист");
      event.getHook().editOriginal(builder.toString())
        .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
    }
  }
}
