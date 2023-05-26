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
package com.jagrosh.jmusicbot.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import com.jagrosh.jmusicbot.commands.music.PlayCmd;
import com.jagrosh.jmusicbot.playlist.PlaylistLoader.Playlist;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PlaylistCmd extends DJCommand {

  //TODO: Сделать чтобы команда была досутпна всем и у каждого сервера была своя папка со своими плейлистами

  private final Bot bot;

  public PlaylistCmd(Bot bot) {
    super(bot);
    this.bot = bot;
    this.guildOnly = true;
    this.name = "playlist";
    this.arguments = "<append|delete|make|setdefault>";
    this.help = "управление плейлистами";
    this.aliases = bot.getConfig().getAliases(this.name);
    this.children = new MusicCommand[]{new ListCmd(bot), new AppendlistCmd(bot), new DeletelistCmd(bot), new MakelistCmd(bot), new PlayCmd(bot)};
  }

  @Override
  public void doSlashCommand(SlashCommandEvent event) {
    event.deferReply().queue();
  }

  @Override
  public void doCommand(CommandEvent event) {
    StringBuilder builder = new StringBuilder(event.getClient().getWarning() + " Команды управления плейлистом:\n");
    for (Command cmd : this.children)
      builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName()).append(" ").append(cmd.getArguments() == null ? "" : cmd.getArguments()).append("` - ").append(cmd.getHelp());
    event.reply(builder.toString());
  }

  public class MakelistCmd extends DJCommand {

    public MakelistCmd(Bot bot) {
      super(bot);
      this.name = "make";
      this.aliases = new String[]{"create"};
      this.help = "создает новый плейлист";
      this.arguments = "<name>";
      this.options = Collections.singletonList(new OptionData(OptionType.STRING, "name", "Название плейлиста", true).setMaxLength(16).setMinLength(3));
      this.guildOnly = true;
    }

    @Override
    public void doSlashCommand(SlashCommandEvent event) {
      String pname = event.getOption("name").getAsString().replaceAll("\\s+", "_");
      if (!validateName(pname)) {
        event.getHook().editOriginal(event.getClient().getError() + " Название плейлиста не может содержать сиимволы кроме: a-z, A-Z, а-я, А-Я, 0-9").queue();
        return;
      }
      if (bot.getPlaylistLoader().getPlaylist(event.getGuild(), pname) == null) {
        try {
          bot.getPlaylistLoader().createPlaylist(event.getGuild(), pname);
          event.getHook().editOriginal(event.getClient().getSuccess() + " Успешно создан плейлист `" + pname + "`!").queue();
        } catch (IOException e) {
          event.getHook().editOriginal(event.getClient().getError() + " Я не могу создать плейлист: " + e.getLocalizedMessage()).queue();
        }
      } else event.getHook().editOriginal(event.getClient().getError() + " Плейлист с названием `" + pname + "` уже существует!").queue();
    }

    @Override
    public void doCommand(CommandEvent event) {
      String pname = event.getArgs().replaceAll("\\s+", "_");
      if (!validateName(pname)) {
        event.reply(event.getClient().getError() + " Название плейлиста не может содержать сиимволы кроме: a-z, A-Z, а-я, А-Я, 0-9");
        return;
      }
      if (bot.getPlaylistLoader().getPlaylist(event.getGuild(), pname) == null) {
        try {
          bot.getPlaylistLoader().createPlaylist(event.getGuild(), pname);
          event.reply(event.getClient().getSuccess() + " Успешно создан плейлист `" + pname + "`!");
        } catch (IOException e) {
          event.reply(event.getClient().getError() + " Я не могу создать плейлист: " + e.getLocalizedMessage());
        }
      } else event.reply(event.getClient().getError() + " Плейлист с названием `" + pname + "` уже существует!");
    }
  }

  public class DeletelistCmd extends DJCommand {

    public DeletelistCmd(Bot bot) {
      super(bot);
      this.name = "delete";
      this.aliases = new String[]{"remove"};
      this.help = "удаляет плейлист";
      this.arguments = "<name>";
      this.options = Collections.singletonList(new OptionData(OptionType.STRING, "name", "Название плейлиста", true).setMaxLength(16).setMinLength(3));
      this.guildOnly = true;
    }

    @Override
    public void doSlashCommand(SlashCommandEvent event) {
      String pname = event.getOption("name").getAsString().replaceAll("\\s+", "_");
      if (!validateName(pname)) {
        event.getHook().editOriginal(event.getClient().getError() + " Название плейлиста не может содержать сиимволы кроме: a-z, A-Z, а-я, А-Я, 0-9").queue();
        return;
      }

      if (bot.getPlaylistLoader().getPlaylist(event.getGuild(), pname) == null)
        event.getHook().editOriginal(event.getClient().getError() + " Плейлист с названием `" + pname + "` не существует!").queue();
      else {
        try {
          bot.getPlaylistLoader().deletePlaylist(event.getGuild(), pname);
          event.getHook().editOriginal(event.getClient().getSuccess() + " Успешно удален плейлист `" + pname + "`!").queue();
        } catch (IOException e) {
          event.getHook().editOriginal(event.getClient().getError() + " Я не могу удалить плейлист: " + e.getLocalizedMessage()).queue();
        }
      }
    }

    @Override
    public void doCommand(CommandEvent event) {
      String pname = event.getArgs().replaceAll("\\s+", "_");
      if (!validateName(pname)) {
        event.reply(event.getClient().getError() + " Название плейлиста не может содержать сиимволы кроме: a-z, A-Z, а-я, А-Я, 0-9");
        return;
      }

      if (bot.getPlaylistLoader().getPlaylist(event.getGuild(), pname) == null)
        event.reply(event.getClient().getError() + " Плейлист с названием `" + pname + "` не существует!");
      else {
        try {
          bot.getPlaylistLoader().deletePlaylist(event.getGuild(), pname);
          event.reply(event.getClient().getSuccess() + " Успешно удален плейлист `" + pname + "`!");
        } catch (IOException e) {
          event.reply(event.getClient().getError() + " Я не могу удалить плейлист: " + e.getLocalizedMessage());
        }
      }
    }
  }

  public class AppendlistCmd extends DJCommand {

    public AppendlistCmd(Bot bot) {
      super(bot);
      this.name = "append";
      this.aliases = new String[]{"add"};
      this.help = "добавляет пластинки в плейлист";
      this.arguments = "<name> <URL>";
      this.options = new ArrayList<OptionData>(){{ add(new OptionData(OptionType.STRING, "name", "Название плейлиста", true).setMaxLength(16).setMinLength(3)); add(new OptionData(OptionType.STRING, "music", "Ссылка пластинки/плейлиста", true)); }};
      this.guildOnly = true;
    }

    @Override
    public void doSlashCommand(SlashCommandEvent event) {
      String[] parts = {event.getOption("name").getAsString(), event.getOption("music").getAsString()};

      String pname = parts[0];
      if (!validateName(pname)) {
        event.getHook().editOriginal(event.getClient().getError() + " Название плейлиста не может содержать сиимволы кроме: a-z, A-Z, а-я, А-Я, 0-9").queue();
        return;
      }

      Playlist playlist = bot.getPlaylistLoader().getPlaylist(event.getGuild(), pname);
      if (playlist == null)
        event.getHook().editOriginal(event.getClient().getError() + " Плейлист с названием `" + pname + "` не существует!").queue();
      else {
        StringBuilder builder = new StringBuilder();
        playlist.getItems().forEach(item -> builder.append("\r\n").append(item));
        String[] urls = parts[1].split("\\|");
        for (String url : urls) {
          String u = url.trim();
          if (u.startsWith("<") && u.endsWith(">")) u = u.substring(1, u.length() - 1);
          builder.append("\r\n").append(u);
        }
        try {
          bot.getPlaylistLoader().writePlaylist(event.getGuild(), pname, builder.toString());
          switch (urls.length) {
            case 1:
              event.getHook().editOriginal(event.getClient().getSuccess() + " Успешно добавлена " + urls.length + " пластинка в плейлист `" + pname + "`!").queue();
              break;
            case 2:
            case 3:
            case 4:
              event.getHook().editOriginal(event.getClient().getSuccess() + " Успешно добавлено " + urls.length + " пластинки в плейлист `" + pname + "`!").queue();
              break;
            default:
              event.getHook().editOriginal(event.getClient().getSuccess() + " Успешно добавлено " + urls.length + " пластинок в плейлист `" + pname + "`!").queue();
              break;
          }
        } catch (IOException e) {
          event.getHook().editOriginal(event.getClient().getError() + " Я не могу добавить пластинку в плейлист: " + e.getLocalizedMessage()).queue();
        }
      }

    }

    @Override
    public void doCommand(CommandEvent event) {
      String[] parts = event.getArgs().split("\\s+", 2);
      if (parts.length < 2) {
        event.reply(event.getClient().getError() + " Пожалуйста напишите название плейлиста и название пластинки");
        return;
      }
      String pname = parts[0];
      if (!validateName(pname)) {
        event.reply(event.getClient().getError() + " Название плейлиста не может содержать сиимволы кроме: a-z, A-Z, а-я, А-Я, 0-9");
        return;
      }

      Playlist playlist = bot.getPlaylistLoader().getPlaylist(event.getGuild(), pname);
      if (playlist == null)
        event.reply(event.getClient().getError() + " Плейлист с названием `" + pname + "` не существует!");
      else {
        StringBuilder builder = new StringBuilder();
        playlist.getItems().forEach(item -> builder.append("\r\n").append(item));
        String[] urls = parts[1].split("\\|");
        for (String url : urls) {
          String u = url.trim();
          if (u.startsWith("<") && u.endsWith(">")) u = u.substring(1, u.length() - 1);
          builder.append("\r\n").append(u);
        }
        try {
          bot.getPlaylistLoader().writePlaylist(event.getGuild(), pname, builder.toString());
          switch (urls.length) {
            case 1:
              event.reply(event.getClient().getSuccess() + " Успешно добавлена " + urls.length + " пластинка в плейлист `" + pname + "`!");
              break;
            case 2:
            case 3:
            case 4:
              event.reply(event.getClient().getSuccess() + " Успешно добавлено " + urls.length + " пластинки в плейлист `" + pname + "`!");
              break;
            default:
              event.reply(event.getClient().getSuccess() + " Успешно добавлено " + urls.length + " пластинок в плейлист `" + pname + "`!");
              break;
          }
        } catch (IOException e) {
          event.reply(event.getClient().getError() + " Я не могу добавить пластинку в плейлист: " + e.getLocalizedMessage());
        }
      }
    }
  }

  public class ListCmd extends DJCommand {

    public ListCmd(Bot bot) {
      super(bot);
      this.name = "all";
      this.aliases = new String[]{"available", "list"};
      this.help = "показывает список всех доступных плейлистов";
      this.guildOnly = true;
    }

    @Override
    public void doSlashCommand(SlashCommandEvent event) {
      if (!bot.getPlaylistLoader().folderExists(event.getGuild())) bot.getPlaylistLoader().createFolder(event.getGuild());
      if (!bot.getPlaylistLoader().folderExists(event.getGuild())) {
        event.getHook().editOriginal(event.getClient().getWarning() + " Папки с плейлистами не существует и она будет создана!").queue();
        return;
      }
      List<String> list = bot.getPlaylistLoader().getPlaylistNames(event.getGuild());
      if (list == null) event.reply(event.getClient().getError() + " Не удалось получить список плейлистов!").queue();
      else if (list.isEmpty())
        event.getHook().editOriginal(event.getClient().getWarning() + " В папке плейлистов нет плейлистов(грустно)!").queue();
      else {
        StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + " Доступные плейлисты:\n");
        list.forEach(str -> builder.append("`").append(str).append("` "));
        event.getHook().editOriginal(builder.toString()).queue();
      }
    }

    @Override
    public void doCommand(CommandEvent event) {
      if (!bot.getPlaylistLoader().folderExists(event.getGuild())) bot.getPlaylistLoader().createFolder(event.getGuild());
      if (!bot.getPlaylistLoader().folderExists(event.getGuild())) {
        event.reply(event.getClient().getWarning() + " Папки с плейлистами не существует и она будет создана!");
        return;
      }
      List<String> list = bot.getPlaylistLoader().getPlaylistNames(event.getGuild());
      if (list == null) event.reply(event.getClient().getError() + " Не удалось получить список плейлистов!");
      else if (list.isEmpty())
        event.reply(event.getClient().getWarning() + " В папке плейлистов нет плейлистов(грустно)!");
      else {
        StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + " Доступные плейлисты:\n");
        list.forEach(str -> builder.append("`").append(str).append("` "));
        event.reply(builder.toString());
      }
    }
  }

  public class PlayCmd extends MusicCommand {
    private final String loadingEmoji;

    public PlayCmd(Bot bot) {
      super(bot);
      this.loadingEmoji = bot.getConfig().getLoading();
      this.name = "play";
      this.aliases = new String[] { "pl" };
      this.arguments = "<name>";
      this.options = Collections.singletonList(new OptionData(OptionType.STRING, "name", "Название плейлиста", true).setMaxLength(16).setMinLength(3));
      this.help = "проигрывает нужный плейлист";
      this.guildOnly = true;
      this.beListening = true;
      this.bePlaying = false;
    }

    @Override
    public void doSlashCommand(SlashCommandEvent event) {
      String pname = event.getOption("name").getAsString().replaceAll("\\s+", "_");
      if (!validateName(pname)) {
        event.getHook().editOriginal(event.getClient().getError() + " Название плейлиста не может содержать сиимволы кроме: a-z, A-Z, а-я, А-Я, 0-9").queue();
        return;
      }

      Playlist playlist = bot.getPlaylistLoader().getPlaylist(event.getGuild(), pname);
      if (playlist == null) {
        event.getHook().editOriginal("Плейлист с названием `" + pname + ".txt` не существует.");
        return;
      }
      event
        .getHook()
        .editOriginal(loadingEmoji + " Загружен плейлист с **" + pname + "**... (" + playlist.getItems().size() + " пластинками)")
        .queue(m -> {
          AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
          playlist.loadTracks(bot.getPlayerManager(),
            at -> handler.addTrack(new QueuedTrack(at, event.getUser())),
            () -> {
              StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty()
                ? event.getClient().getWarning() + " Пластинки не были загружены!" : event.getClient().getSuccess() + " Пластинки были загружены");
              if (!playlist.getErrors().isEmpty())
                builder.append("\nНе удалось загрузить данные пластинки:");
              playlist.getErrors().forEach(err ->
                builder.append("\n`[").append(err.getIndex() + 1).append("]` **").append(err.getItem()).append("**: ").append(err.getReason()));
              String str = builder.toString();
              if (str.length() > 2000) str = str.substring(0, 1994) + " (...)";
              m.editMessage(FormatUtil.filter(str)).queue();
            }
          );
        });
    }

    @Override
    public void doCommand(CommandEvent event) {
      String pname = event.getArgs().replaceAll("\\s+", "_");
      if (!validateName(pname)) {
        event.reply(event.getClient().getError() + " Название плейлиста не может содержать сиимволы кроме: a-z, A-Z, а-я, А-Я, 0-9");
        return;
      }

      Playlist playlist = bot.getPlaylistLoader().getPlaylist(event.getGuild(), pname);
      if (playlist == null) {
        event.replyError("Плейлист с названием `" + pname + ".txt` не существует.");
        return;
      }
      event.getChannel()
        .sendMessage(loadingEmoji + " Загружен плейлист с **" + pname + "**... (" + playlist.getItems().size() + " пластинками)")
        .queue(m -> {
          AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
          playlist.loadTracks(bot.getPlayerManager(),
            at -> handler.addTrack(new QueuedTrack(at, event.getAuthor())),
            () -> {
              StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty()
                ? event.getClient().getWarning() + " Пластинки не были загружены!" : event.getClient().getSuccess() + " Пластинки были загружены");
              if (!playlist.getErrors().isEmpty())
                builder.append("\nНе удалось загрузить данные пластинки:");
              playlist.getErrors().forEach(err ->
                builder.append("\n`[").append(err.getIndex() + 1).append("]` **").append(err.getItem()).append("**: ").append(err.getReason()));
              String str = builder.toString();
              if (str.length() > 2000) str = str.substring(0, 1994) + " (...)";
              m.editMessage(FormatUtil.filter(str)).queue();
            }
          );
        });
    }
  }

  public boolean validateName(String name) {
    return name.matches("^[a-zA-Z0-9а-яА-Я]+$");
  }
}
