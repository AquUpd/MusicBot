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
import com.jagrosh.jlyrics.LyricsClient;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.BotConfig;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class LyricsCmd extends MusicCommand {

  private final LyricsClient client = new LyricsClient();

  public LyricsCmd(Bot bot) {
    super(bot);
    this.name = "lyrics";
    this.arguments = "(название пластинки)";
    this.options = Collections.singletonList(new OptionData(OptionType.STRING, "name", "Название пластинки").setRequired(false));
    this.help = "показывает текст пластинки";
    this.aliases = bot.getConfig().getAliases(this.name);
    this.botPermissions = new Permission[] { Permission.MESSAGE_EMBED_LINKS };
  }

  @Override
  public void doCommand(CommandEvent event) {
    String title;
    if (event.getArgs().isEmpty()) {
      AudioHandler sendingHandler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
      event.replyError("Вы должны указать название пластинки!");
      return;
    } else title = event.getArgs();

    event.getChannel().sendTyping().queue();
    client.getLyrics(title).thenAccept(lyrics -> {
      if (lyrics == null) {
        event.replyError("Текст для `" + title + "` не найден!");
        return;
      }

      EmbedBuilder eb = new EmbedBuilder()
        .setAuthor(lyrics.getAuthor())
        .setColor(event.getSelfMember().getColor())
        .setTitle(lyrics.getTitle(), lyrics.getURL());
      if (lyrics.getContent().length() > 15000) {
        event.replyWarning("Текст для `" + title + "` найден, но он, возможно, не правильный: " + lyrics.getURL());
      } else if (lyrics.getContent().length() > 2000) {
        String content = lyrics.getContent().trim();
        while (content.length() > 2000) {
          int index = content.lastIndexOf("\n\n", 2000);
          if (index == -1) index = content.lastIndexOf("\n", 2000);
          if (index == -1) index = content.lastIndexOf(" ", 2000);
          if (index == -1) index = 2000;
          event.reply(eb.setDescription(content.substring(0, index).trim()).build());
          content = content.substring(index).trim();
          eb.setAuthor(null).setTitle(null, null);
        }
        event.reply(eb.setDescription(content).build());
      } else
        event.reply(eb.setDescription(lyrics.getContent()).build());
    });
  }

  @Override
  public void doSlashCommand(SlashCommandEvent event) {
    String title;
    if(event.hasOption("name")) title = event.getOption("name").getAsString();
    else {
      AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
      title = handler.getPlayer().getPlayingTrack().getInfo().title;
    }

    client.getLyrics(title).thenAccept(lyrics -> {
      if (lyrics == null) {
        event.getHook().editOriginal("Текст для `" + title + "` не найден!")
          .queue();
        return;
      }

      EmbedBuilder eb = new EmbedBuilder()
        .setAuthor(lyrics.getAuthor())
        .setColor(event.getGuild().getSelfMember().getColor())
        .setTitle(lyrics.getTitle(), lyrics.getURL());
      if (lyrics.getContent().length() > 15000) {
        event.getHook().editOriginal("Текст для `" + title + "` найден, но он, возможно, не правильный: " + lyrics.getURL()).queue();
      } else if (lyrics.getContent().length() > 2000) {
        String content = lyrics.getContent().trim();
        while (content.length() > 2000) {
          int index = content.lastIndexOf("\n\n", 2000);
          if (index == -1) index = content.lastIndexOf("\n", 2000);
          if (index == -1) index = content.lastIndexOf(" ", 2000);
          if (index == -1) index = 2000;
          event.getHook().editOriginalEmbeds(eb.setDescription(content.substring(0, index).trim()).build()).queue();
          content = content.substring(index).trim();
          eb.setAuthor(null).setTitle(null, null);
        }
        event.getHook().editOriginalEmbeds(eb.setDescription(content).build()).queue();
      } else {
        event.getHook().editOriginalEmbeds(eb.setDescription(lyrics.getContent()).build()).queue();
      }
    });
  }
}
