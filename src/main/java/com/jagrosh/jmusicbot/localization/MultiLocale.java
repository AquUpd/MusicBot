package com.jagrosh.jmusicbot.localization;

import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.util.Locale;
import java.util.ResourceBundle;

public class MultiLocale {
  private final String name;
  private final String shortName;
  private final Locale locale;
  private final DiscordLocale discordLocale;
  private final ResourceBundle resourceBundle;

  public MultiLocale(String name, String shortName, Locale locale, DiscordLocale discordLocale) {
    this.name = name;
    this.shortName = shortName;
    this.locale = locale;
    this.discordLocale = discordLocale;
    this.resourceBundle = ResourceBundle.getBundle("resourcebundle.lang", locale);
  }

  public String getShortName() {
    return shortName;
  }

  public String getName() {
    return name;
  }

  public Locale getLocale() {
    return locale;
  }

  public DiscordLocale getDiscordLocale() {
    return discordLocale;
  }

  public ResourceBundle getResourceBundle() {
    return resourceBundle;
  }
}
