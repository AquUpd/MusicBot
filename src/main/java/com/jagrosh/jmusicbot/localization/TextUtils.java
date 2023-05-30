package com.jagrosh.jmusicbot.localization;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.BotConfig;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.MissingResourceException;

public class TextUtils {

  Bot bot;
  BotConfig config;
  Logger logger = LoggerFactory.getLogger("TextUtil");

  public TextUtils(Bot bot) {
    this.bot = bot;
    this.config = bot.getConfig();
  }

  public String localizeDefaultError(String translation, Object... objects) {
    return config.getError() + " " + localize(bot.getLocales().getDefaultLocale(), translation, objects);
  }

  public String localizeDefaultSuccess(String translation, Object... objects) {
    return config.getSuccess() + " " + localize(bot.getLocales().getDefaultLocale(), translation, objects);
  }

  public String localizeDefault(String translation, Object... objects) {
    return localize(bot.getLocales().getDefaultLocale(), translation, objects);
  }

  public String localizeError(MultiLocale locale, String translation, Object... objects) {
    return config.getError() + " " + localize(locale, translation, objects);
  }

  public String localizeSuccess(MultiLocale locale, String translation, Object... objects) {
    return config.getSuccess() + " " + localize(locale, translation, objects);
  }

  public String localize(MultiLocale locale, String translation, Object... objects) {
    try {
      return String.format(locale.getResourceBundle().getString(translation), objects);
    } catch(MissingResourceException ex) {
      logger.error("Exception when localizing:", ex);
      return translation;
    }
  }

  public void optionTranslation(OptionData option, String translation) {
    Locales.languages.forEach(
      (locale) -> option.setDescriptionLocalization(locale.getDiscordLocale(), this.localize(locale, translation))
    );
  }
}
