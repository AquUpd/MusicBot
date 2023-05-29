package com.jagrosh.jmusicbot.localization;

import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ResourceBundle;

public class TextUtils {

  public static String localize(String translation, Object... objects) {
    return String.format(Locales.getResourceBundle().getString(translation), objects);
  }

  public static String localize(ResourceBundle bundle, String translation, Object... objects) {
    return String.format(bundle.getString(translation), objects);
  }

  public static void optionTranslation(OptionData option, String translation) {
    Locales.languages.forEach(
      (locale) -> option.setDescriptionLocalization(Locales.discordLocaleToLocale.get(locale), Locales.getResourceBundle(locale).getString(translation))
    );
  }
}
