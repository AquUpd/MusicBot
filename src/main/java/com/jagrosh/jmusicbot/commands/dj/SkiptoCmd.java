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
package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.DJCommand;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SkiptoCmd extends DJCommand {

  public SkiptoCmd(Bot bot) {
    super(bot);
    this.name = "skipto";
    this.help = "пропускает пластинки до определенного места";
    this.arguments = "<position>";
    this.options = Collections.singletonList(new OptionData(OptionType.INTEGER, "position", "Позиция в очереди.").setRequired(true));
    this.aliases = bot.getConfig().getAliases(this.name);
    this.bePlaying = true;
  }

  @Override
  public void doCommand(CommandEvent event) {
    int index;
    try {
      index = Integer.parseInt(event.getArgs());
    } catch (NumberFormatException e) {
      event.reply(event.getClient().getError() + " `" + event.getArgs() + "` не допустимое число!");
      return;
    }
    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    if (index < 1 || index > handler.getQueue().size()) {
      event.reply(event.getClient().getError() + " Позиция должна быть между 1 и " + handler.getQueue().size() + "!");
      return;
    }
    handler.getQueue().skip(index - 1);
    event.reply(event.getClient().getSuccess() + " Пропущены пластинки до **" + handler.getQueue().get(0).getTrack().getInfo().title + "**");
    handler.getPlayer().stopTrack();
  }

  @Override
  public void doSlashCommand(SlashCommandEvent event) {
    int index = event.getOption("position").getAsInt();

    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    if (index < 1 || index > handler.getQueue().size()) {
      event.getHook().editOriginal(event.getClient().getError() + " Позиция должна быть между 1 и " + handler.getQueue().size() + "!")
        .queue();
      return;
    }
    handler.getQueue().skip(index - 1);
    event.getHook().editOriginal(event.getClient().getSuccess() + " Пропущены пластинки до **" + handler.getQueue().get(0).getTrack().getInfo().title + "**")
      .queue();
    handler.getPlayer().stopTrack();
  }
}
