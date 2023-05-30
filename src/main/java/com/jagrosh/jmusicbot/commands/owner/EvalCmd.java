/*
 * Copyright 2016 John Grosh (jagrosh).
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
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Collections;

/**
 * @author John Grosh (jagrosh)
 */
public class EvalCmd extends OwnerCommand {

  public EvalCmd(Bot bot) {
    super(bot);
    this.name = "eval";
    this.help = "evaluates nashorn code";
    this.options = Collections.singletonList(new OptionData(OptionType.STRING, "code", "Код.").setRequired(true));
    this.aliases = bot.getConfig().getAliases(this.name);
    this.guildOnly = false;
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    event.deferReply().queue();
    ScriptEngine se = new ScriptEngineManager().getEngineByName("Nashorn");
    se.put("bot", bot);
    se.put("event", event);
    se.put("jda", event.getJDA());
    se.put("guild", event.getGuild());
    se.put("channel", event.getChannel());
    try {
      event.getHook().editOriginal(event.getClient().getSuccess() + " Evaluated Successfully:\n```\n" + se.eval(event.getOption("code").getAsString()) + " ```")
        .queue();
    } catch (Exception e) {
      event.getHook().editOriginal(event.getClient().getError() + " An exception was thrown:\n```\n" + e + " ```")
        .queue();
    }
  }

  @Override
  protected void execute(CommandEvent event) {
    ScriptEngine se = new ScriptEngineManager().getEngineByName("Nashorn");
    se.put("bot", bot);
    se.put("event", event);
    se.put("jda", event.getJDA());
    se.put("guild", event.getGuild());
    se.put("channel", event.getChannel());
    try {
      event.reply(event.getClient().getSuccess() + " Evaluated Successfully:\n```\n" + se.eval(event.getArgs()) + " ```");
    } catch (Exception e) {
      event.reply(event.getClient().getError() + " An exception was thrown:\n```\n" + e + " ```");
    }
  }
}
