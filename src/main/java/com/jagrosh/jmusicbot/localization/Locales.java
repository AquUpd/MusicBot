package com.jagrosh.jmusicbot.localization;

import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.util.*;

public class Locales {

  public static List<Locale> languages = new ArrayList<Locale>() {{
    add(0, new Locale("en"));
    add(1, new Locale("ru"));
  }};

  public static HashMap<String, Locale> stringLanguages = new HashMap<String, Locale>() {{
    put("english", new Locale("en"));
    put("russian", new Locale("ru"));
  }};

  public static HashMap<Locale, DiscordLocale> discordLocaleToLocale = new HashMap<Locale, DiscordLocale>() {{
    put(languages.get(0), DiscordLocale.ENGLISH_US);
    put(languages.get(1), DiscordLocale.RUSSIAN);
  }};

  public static ResourceBundle getResourceBundle(Locale locale) {
    return ResourceBundle.getBundle("resourcebundle.lang", locale);
  }

  public static ResourceBundle getResourceBundle() {
    return ResourceBundle.getBundle("resourcebundle.lang", languages.get(0));
  }
}
