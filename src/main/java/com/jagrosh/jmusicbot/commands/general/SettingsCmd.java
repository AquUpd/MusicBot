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
package com.jagrosh.jmusicbot.commands.general;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.settings.RepeatMode;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SettingsCmd extends SlashCommand {

  private static final String EMOJI = "\uD83C\uDFA7"; // 🎧

  public SettingsCmd(Bot bot) {
    this.name = "settings";
    this.help = "показывает настройки бота";
    this.aliases = bot.getConfig().getAliases(this.name);
    this.guildOnly = true;
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    event.deferReply().queue();
    Settings s = event.getClient().getSettingsFor(event.getGuild());
    MessageEditBuilder builder = new MessageEditBuilder()
        .setContent(EMOJI + " **" + FormatUtil.filter(event.getJDA().getSelfUser().getName()) + "** настройки:");
    Channel tchan = s.getTextChannel(event.getGuild());
    VoiceChannel vchan = s.getVoiceChannel(event.getGuild());
    Role role = s.getRole(event.getGuild());
    EmbedBuilder ebuilder = new EmbedBuilder()
      .setColor(event.getGuild().getSelfMember().getColor())
      .setDescription("Текстовый канал: " + (tchan == null ? "Any" : "**#" + tchan.getName() + "**") +
          "\nГолосовой канал: " + (vchan == null ? "Any" : vchan.getAsMention()) +
          "\nDJ Роль: " + (role == null ? "None" : "**" + role.getName() + "**") +
          "\nКастомный префикс: " + (s.getPrefix() == null ? "None" : "`" + s.getPrefix() + "`") +
          "\nПовторение: " + (s.getRepeatMode() == RepeatMode.OFF ? s.getRepeatMode().getUserFriendlyName() : "**" + s.getRepeatMode().getUserFriendlyName() + "**") +
          "\nАвтоплейлист: " + (s.getDefaultPlaylist() == null ? "None" : "**" + s.getDefaultPlaylist() + "**"))
      .setFooter(event.getJDA().getGuilds().size() + " servers | " +
          event.getJDA().getGuilds().stream().filter(g -> g.getSelfMember().getVoiceState().inAudioChannel()).count() + " голосовых подключений",
        null);
    event.getHook().editOriginal(builder.setEmbeds(ebuilder.build()).build()).queue();
  }

  @Override
  protected void execute(CommandEvent event) {
    Settings s = event.getClient().getSettingsFor(event.getGuild());
    MessageCreateBuilder builder = new MessageCreateBuilder()
        .setContent(EMOJI + " **" + FormatUtil.filter(event.getSelfUser().getName()) + "** настройки:");
    Channel tchan = s.getTextChannel(event.getGuild());
    VoiceChannel vchan = s.getVoiceChannel(event.getGuild());
    Role role = s.getRole(event.getGuild());
    EmbedBuilder ebuilder = new EmbedBuilder()
      .setColor(event.getSelfMember().getColor())
      .setDescription("Текстовый канал: " + (tchan == null ? "Any" : "**#" + tchan.getName() + "**") +
        "\nГолосовой канал: " + (vchan == null ? "Any" : vchan.getAsMention()) +
        "\nDJ Роль: " + (role == null ? "None" : "**" + role.getName() + "**") +
        "\nКастомный префикс: " + (s.getPrefix() == null ? "None" : "`" + s.getPrefix() + "`") +
        "\nПовторение: " + (s.getRepeatMode() == RepeatMode.OFF ? s.getRepeatMode().getUserFriendlyName() : "**" + s.getRepeatMode().getUserFriendlyName() + "**") +
        "\nАвтоплейлист: " + (s.getDefaultPlaylist() == null ? "None" : "**" + s.getDefaultPlaylist() + "**"))
      .setFooter(event.getJDA().getGuilds().size() + " servers | " +
          event.getJDA().getGuilds().stream().filter(g -> g.getSelfMember().getVoiceState().inAudioChannel()).count() + " голосовых подключений",
        null);
    event.getChannel().sendMessage(builder.setEmbeds(ebuilder.build()).build()).queue();
  }
}
