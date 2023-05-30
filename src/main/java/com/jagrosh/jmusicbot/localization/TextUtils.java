package com.jagrosh.jmusicbot.localization;

import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class TextUtils {

  public static String localize(String translation, Object... objects) {
    return String.format(Locales.getDefaultResourceBundle().getString(translation), objects);
  }

  public static String localize(MultiLocale locale, String translation, Object... objects) {
    return String.format(locale.getResourceBundle().getString(translation), objects);
  }

  public static void optionTranslation(OptionData option, String translation) {
    Locales.languages.forEach(
      (locale) -> option.setDescriptionLocalization(locale.getDiscordLocale(), locale.getResourceBundle().getString(translation))
    );
  }
}
