package com.jagrosh.jmusicbot.localization;

import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.util.*;

public class Locales {
  public static List<MultiLocale> languages = new ArrayList<MultiLocale>() {{
    add(0, new MultiLocale("english","en_us", new Locale("en", "us"), DiscordLocale.ENGLISH_US));
    add(1, new MultiLocale("russian", "ru_ru",  new Locale("ru", "ru"), DiscordLocale.RUSSIAN));
  }};

  public static ResourceBundle getDefaultResourceBundle() {
    return languages.get(0).getResourceBundle();
  }
}
