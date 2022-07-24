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

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Message;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SkipCmd extends MusicCommand {

  public SkipCmd(Bot bot) {
    super(bot);
    this.name = "skip";
    this.help = "голосование за пропуск пластинок";
    this.aliases = bot.getConfig().getAliases(this.name);
    this.beListening = true;
    this.bePlaying = true;
  }

  @Override
  public void doCommand(CommandEvent event) {
    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    event.reply(event.getClient().getSuccess() + " Пропущена пластинка **" + handler.getPlayer().getPlayingTrack().getInfo().title + "**");
    handler.getPlayer().stopTrack();
  }

  @Override
  public void doSlashCommand(SlashCommandEvent event) {
    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    event.getHook().editOriginal(event.getClient().getSuccess() + " Пропущена пластинка **" +
        handler.getPlayer().getPlayingTrack().getInfo().title + "**")
      .delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
    handler.getPlayer().stopTrack();
  }
}
