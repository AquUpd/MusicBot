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
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class PlaynextCmd extends DJCommand {

  private final String loadingEmoji;

  public PlaynextCmd(Bot bot) {
    super(bot);
    this.loadingEmoji = bot.getConfig().getLoading();
    this.name = "playnext";
    this.arguments = "<title|URL>";
    this.options = Collections.singletonList(new OptionData(OptionType.STRING, "track", "название/URL пластинки").setRequired(true));
    this.help = "воспроизводит пластинку следующей";
    this.aliases = bot.getConfig().getAliases(this.name);
    this.beListening = true;
    this.bePlaying = false;
  }

  @Override
  public void doCommand(CommandEvent event) {
    if (event.getArgs().isEmpty() && event.getMessage().getAttachments().isEmpty()) {
      event.replyWarning("Напишите имя пластинки или URL!");
      return;
    }
    String args = event.getArgs().startsWith("<") && event.getArgs().endsWith(">")
      ? event.getArgs().substring(1, event.getArgs().length() - 1) : event.getArgs().isEmpty()
      ? event.getMessage().getAttachments().get(0).getUrl() : event.getArgs();
    event.reply(loadingEmoji + " Загрузка... `[" + args + "]`",
      m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), args, new ResultHandler(m, event, false)));
  }

  @Override
  public void doSlashCommand(SlashCommandEvent event) {
    String arg = event.getOption("track").getAsString();
    String args = (arg.startsWith("<") && arg.endsWith(">")) ? arg.substring(1, arg.length() - 1) : arg;
    event.getHook().editOriginal(loadingEmoji + " Загрузка... `[" + args + "]`").queue();
    bot.getPlayerManager().loadItemOrdered(event.getGuild(), args, new SlashResultHandler(event.getHook(), event, false));
  }

  private class ResultHandler implements AudioLoadResultHandler {

    private final Message m;
    private final CommandEvent event;
    private final boolean ytsearch;

    private ResultHandler(Message m, CommandEvent event, boolean ytsearch) {
      this.m = m;
      this.event = event;
      this.ytsearch = ytsearch;
    }

    private void loadSingle(AudioTrack track) {
      if (bot.getConfig().isTooLong(track)) {
        m.editMessage(FormatUtil.filter(event.getClient().getWarning() +
          " Эта пластинка (**" + track.getInfo().title + "**) длиннее чем разрешенный лимит: `" +
          FormatUtil.formatTime(track.getDuration()) + "` > `" + FormatUtil.formatTime(bot.getConfig().getMaxSeconds() * 1000) + "`")).queue();
        return;
      }
      AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
      int pos = handler.addTrackToFront(new QueuedTrack(track, event.getAuthor())) + 1;
      String addMsg = FormatUtil.filter(event.getClient().getSuccess() +
        " Добавлена пластинка **" + track.getInfo().title + "** (`" +
        FormatUtil.formatTime(track.getDuration()) + "`) " + (pos == 0 ? "" : " в очередь " + pos));
      m.editMessage(addMsg).queue();
    }

    @Override
    public void trackLoaded(AudioTrack track) {
      loadSingle(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
      AudioTrack single;
      if (playlist.getTracks().size() == 1 || playlist.isSearchResult())
        single = playlist.getSelectedTrack() == null ? playlist.getTracks().get(0) : playlist.getSelectedTrack();
      else if (playlist.getSelectedTrack() != null)
        single = playlist.getSelectedTrack(); else single = playlist.getTracks().get(0);
      loadSingle(single);
    }

    @Override
    public void noMatches() {
      if (ytsearch)
        m.editMessage(FormatUtil.filter(event.getClient().getWarning() + " Результаты не найдены для `" + event.getArgs() + "`.")).queue();
      else
        bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:" + event.getArgs(), new ResultHandler(m, event, true));
    }

    @Override
    public void loadFailed(FriendlyException throwable) {
      if (throwable.severity == FriendlyException.Severity.COMMON) m
        .editMessage(event.getClient().getError() + " Ошибка загрузки: " + throwable.getMessage()).queue();
      else m
        .editMessage(event.getClient().getError() + " Ошибка загрузки трека.").queue();
    }
  }

  private class SlashResultHandler implements AudioLoadResultHandler {

    private final InteractionHook m;
    private final SlashCommandEvent event;
    private final boolean ytsearch;

    private SlashResultHandler(InteractionHook m, SlashCommandEvent event, boolean ytsearch) {
      this.m = m;
      this.event = event;
      this.ytsearch = ytsearch;
    }

    private void loadSingle(AudioTrack track) {
      if (bot.getConfig().isTooLong(track)) {
        m.editOriginal(FormatUtil.filter(event.getClient().getWarning() +
          " Эта пластинка (**" + track.getInfo().title + "**) длиннее чем разрешенный лимит: `" +
          FormatUtil.formatTime(track.getDuration()) + "` > `" + FormatUtil.formatTime(bot.getConfig().getMaxSeconds() * 1000) + "`"))
          .queue();
        return;
      }
      AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
      int pos = handler.addTrackToFront(new QueuedTrack(track, event.getUser())) + 1;
      String addMsg = FormatUtil.filter(event.getClient().getSuccess() +
        " Добавлена пластинка **" + track.getInfo().title + "** (`" +
        FormatUtil.formatTime(track.getDuration()) + "`) " + (pos == 0 ? "" : " в очередь " + pos));
      m.editOriginal(addMsg)
        .queue();
    }

    @Override
    public void trackLoaded(AudioTrack track) {
      loadSingle(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
      AudioTrack single;
      if (playlist.getTracks().size() == 1 || playlist.isSearchResult())
        single = playlist.getSelectedTrack() == null ? playlist.getTracks().get(0) : playlist.getSelectedTrack();
      else if (playlist.getSelectedTrack() != null)
        single = playlist.getSelectedTrack(); else single = playlist.getTracks().get(0);
      loadSingle(single);
    }

    @Override
    public void noMatches() {
      if (ytsearch)
        m.editOriginal(FormatUtil.filter(event.getClient().getWarning() + " Результаты не найдены для `" + event.getOption("track").getAsString() + "`."))
          .queue();
      else
        bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:" + event.getOption("track").getAsString(), new SlashResultHandler(m, event, true));
    }

    @Override
    public void loadFailed(FriendlyException throwable) {
      if (throwable.severity == FriendlyException.Severity.COMMON)
        m.editOriginal(event.getClient().getError() + " Ошибка загрузки: " + throwable.getMessage())
          .queue();
      else
        m.editOriginal(event.getClient().getError() + " Ошибка загрузки трека.")
          .queue();
    }
  }
}
