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
import com.jagrosh.jdautilities.menu.Paginator;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.JMusicBot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.settings.RepeatMode;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class QueueCmd extends MusicCommand {

  private final Paginator.Builder builder;

  public QueueCmd(Bot bot) {
    super(bot);
    this.name = "queue";
    this.help = "показывает очередь";
    this.arguments = "[страница]";
    this.options = Collections.singletonList(new OptionData(OptionType.INTEGER, "page", "Страница очереди").setRequired(false));
    this.aliases = bot.getConfig().getAliases(this.name);
    this.bePlaying = true;
    this.botPermissions = new Permission[] {Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EMBED_LINKS,};
    builder = new Paginator.Builder()
        .setColumns(1)
        .setFinalAction(m -> { try {m.clearReactions().queue(); } catch (PermissionException ignore) { } })
        .setItemsPerPage(10)
        .waitOnSinglePage(false)
        .useNumberedItems(true)
        .showPageNumbers(true)
        .wrapPageEnds(true)
        .setEventWaiter(bot.getWaiter())
        .setTimeout(1, TimeUnit.MINUTES);
  }

  @Override
  public void doCommand(CommandEvent event) {
    int pagenum = 1;
    try {
      pagenum = Integer.parseInt(event.getArgs());
    } catch (NumberFormatException ignore) {}
    AudioHandler ah = (AudioHandler) event
      .getGuild()
      .getAudioManager()
      .getSendingHandler();
    List<QueuedTrack> list = ah.getQueue().getList();
    if (list.isEmpty()) {
      MessageEditData nowp = ah.getNowPlaying(event.getJDA());
      MessageEditData nonowp = ah.getNoMusicPlayingE(event.getJDA());
      MessageEditData built = new MessageEditBuilder().setContent(event.getClient().getWarning() + " Нет пластинок в очереди!")
        .setEmbeds((nowp == null ? nonowp : nowp).getEmbeds().get(0)).build();
      event.reply(MessageCreateData.fromEditData(built), m -> {if (nowp != null) bot.getNowplayingHandler().setLastNPMessage(m);});
      return;
    }
    String[] songs = new String[list.size()];
    long total = 0;
    for (int i = 0; i < list.size(); i++) {
      total += list.get(i).getTrack().getDuration();
      songs[i] = list.get(i).toString();
    }
    Settings settings = event.getClient().getSettingsFor(event.getGuild());
    long fintotal = total;
    builder.setText((i1, i2) -> getQueueTitle(ah, event.getClient().getSuccess(), songs.length, fintotal, settings.getRepeatMode()))
      .setItems(songs).setUsers(event.getAuthor()).setColor(event.getSelfMember().getColor());
    builder.build().paginate(event.getChannel(), pagenum);
  }

  @Override
  public void doSlashCommand(SlashCommandEvent event) {
    int pagenum = 1;
    if(event.hasOption("page")) pagenum = event.getOption("page").getAsInt();
    AudioHandler ah = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    List<QueuedTrack> list = ah.getQueue().getList();
    if (list.isEmpty()) {
      MessageEditData nowp = ah.getNowPlaying(event.getJDA());
      MessageEditData nonowp = ah.getNoMusicPlayingE(event.getJDA());
      MessageEditData built = new MessageEditBuilder().setContent(event.getClient().getWarning() + " Нет пластинок в очереди!")
        .setEmbeds((nowp == null ? nonowp : nowp).getEmbeds().get(0)).build();
      event.getHook().editOriginal(built).queue();
      return;
    }
    String[] songs = new String[list.size()];
    long total = 0;
    for (int i = 0; i < list.size(); i++) {
      total += list.get(i).getTrack().getDuration();
      songs[i] = list.get(i).toString();
    }
    Settings settings = event.getClient().getSettingsFor(event.getGuild());
    long fintotal = total;
    event.getHook().deleteOriginal().queue();
    builder.setText((i1, i2) -> getQueueTitle(ah, event.getClient().getSuccess(), songs.length, fintotal, settings.getRepeatMode()))
      .setItems(songs).setUsers(event.getUser()).setColor(event.getGuild().getSelfMember().getColor());
    builder.build().paginate(event.getChannel(), pagenum);
  }

  private String getQueueTitle(AudioHandler ah, String success, int songslength, long total, RepeatMode repeatmode) {
    StringBuilder sb = new StringBuilder();
    if (ah.getPlayer().getPlayingTrack() != null) {
      sb.append(ah.getPlayer().isPaused() ? JMusicBot.PAUSE_EMOJI : JMusicBot.PLAY_EMOJI)
        .append(" **").append(ah.getPlayer().getPlayingTrack().getInfo().title).append("**\n");
    }
    return FormatUtil.filter(
      sb.append(success).append(" Данная очередь состоит из | ").append(songslength)
        .append(" пластинок(ки) | `").append(FormatUtil.formatTime(total)).append("` ")
        .append(repeatmode.getEmoji() != null ? "| " + repeatmode.getEmoji() : "").toString());
  }
}
