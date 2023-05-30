package com.jagrosh.jmusicbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.localization.Locales;
import com.jagrosh.jmusicbot.localization.MultiLocale;
import com.jagrosh.jmusicbot.settings.Settings;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

public class SetlangCmd extends AdminCommand {

  public SetlangCmd(Bot bot) {
    super(bot);
    this.name = "setlang";
    this.help = bot.getTextUtils().localizeDefault("commands_setlang_help");
    this.arguments = "<language>";
    OptionData languageOption = new OptionData(OptionType.STRING, "language", bot.getTextUtils().localizeDefault("commands_setlang_option_language"))
      .addChoice("English", "english")
      .addChoice("Russian", "russian")
      .setRequired(true);
    bot.getTextUtils().optionTranslation(languageOption, "commands_setlang_option_language");

    this.options = Collections.singletonList(languageOption);
    this.aliases = bot.getConfig().getAliases(this.name);
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    event.deferReply().queue();
    String arg = event.getOption("language").getAsString().toLowerCase(Locale.ENGLISH);
    Settings s = event.getClient().getSettingsFor(event.getGuild());

    Optional<MultiLocale> matchingLocale = Locales.languages.stream().filter(multiLoc -> multiLoc.getName().equals(arg)).findFirst();
    if(!matchingLocale.isPresent()) {
      StringBuilder strBuilder = new StringBuilder();
      Locales.languages.forEach(multiLoc -> strBuilder.append("\"").append(multiLoc.getName()).append("\", "));
      strBuilder.deleteCharAt(strBuilder.lastIndexOf(","));

      event.getHook().editOriginal(bot.getTextUtils().localizeError(s.getMultiLocale(), "commands_setlang_error_invalid", strBuilder)).queue();
      return;
    }

    s.setLanguage(matchingLocale.get());
    event.getHook().editOriginal(bot.getTextUtils().localizeSuccess(s.getMultiLocale(), "commands_setlang_success", bot.getTextUtils().localize(s.getMultiLocale(), "language_" + arg))).queue();
  }

  @Override
  protected void execute(CommandEvent event) {
    Settings s = event.getClient().getSettingsFor(event.getGuild());

    if (event.getArgs().isEmpty()) {
      StringBuilder strBuilder = new StringBuilder();
      Locales.languages.forEach(multiLoc -> strBuilder.append("\"").append(multiLoc.getName()).append("\", "));
      strBuilder.deleteCharAt(strBuilder.lastIndexOf(","));

      event.reply(bot.getTextUtils().localizeError(s.getMultiLocale(), "commands_setlang_error_empty", strBuilder));
      return;
    }

    String arg = event.getArgs().toLowerCase(Locale.ENGLISH);
    Optional<MultiLocale> matchingLocale = Locales.languages.stream().filter(multiLoc -> multiLoc.getName().equals(arg)).findFirst();
    if(!matchingLocale.isPresent()) {
      StringBuilder strBuilder = new StringBuilder();
      Locales.languages.forEach(multiLoc -> strBuilder.append("\"").append(multiLoc.getName()).append("\", "));
      strBuilder.deleteCharAt(strBuilder.lastIndexOf(","));

      event.reply(bot.getTextUtils().localizeError(s.getMultiLocale(), "commands_setlang_error_invalid", strBuilder));
      return;
    }

    s.setLanguage(matchingLocale.get());
    event.reply(bot.getTextUtils().localizeSuccess(s.getMultiLocale(), "commands_setlang_success", bot.getTextUtils().localize(s.getMultiLocale(), "language_" + arg)));
  }
}
