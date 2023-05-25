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
package com.jagrosh.jmusicbot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.PingCommand;
import com.jagrosh.jmusicbot.commands.admin.*;
import com.jagrosh.jmusicbot.commands.dj.*;
import com.jagrosh.jmusicbot.commands.fun.*;
import com.jagrosh.jmusicbot.commands.general.SettingsCmd;
import com.jagrosh.jmusicbot.commands.music.*;
import com.jagrosh.jmusicbot.commands.owner.*;
import com.jagrosh.jmusicbot.entities.Prompt;
import com.jagrosh.jmusicbot.gui.GUI;
import com.jagrosh.jmusicbot.settings.SettingsManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author John Grosh (jagrosh)
 */
public class  JMusicBot {

  public static final String PLAY_EMOJI = "\u25B6"; // ▶
  public static final String PAUSE_EMOJI = "\u23F8"; // ⏸
  public static final String STOP_EMOJI = "\u23F9"; // ⏹
  public static final Permission[] RECOMMENDED_PERMS = {Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.CREATE_PUBLIC_THREADS, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EXT_EMOJI, Permission.MANAGE_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.NICKNAME_CHANGE,};
  public static final GatewayIntent[] INTENTS = {GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS,};

  static ConsoleListener textInput;

  /**
   * @param args the command line arguments
   */

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));

    long timer = System.currentTimeMillis();
    // startup log
    Logger log = LoggerFactory.getLogger("Startup");

    // create prompt to handle startup
    Prompt prompt = new Prompt("JMusicBot", "переключаемся в nogui режим. Вы можете сами его включать, сделав -Dnogui=true флаг.");

    // get and check latest version
    //String version = OtherUtil.checkVersion(prompt);

    // check for valid java version
    String version = System.getProperty("java.version");
    if (version.startsWith("1.")) {
      version = version.substring(2, 3);
    } else {
      int dot = version.indexOf(".");
      if (dot != -1) {
        version = version.substring(0, dot);
      }
    }
    int v_num = Integer.parseInt(version);

    // load config
    BotConfig config = new BotConfig(prompt);
    config.load();
    if (!config.isValid()) return;

    // set up the listener
    EventWaiter waiter = new EventWaiter();
    SettingsManager settings = new SettingsManager();
    Bot bot = new Bot(waiter, config, settings);

    // set up the command client
    CommandClientBuilder cb = new CommandClientBuilder()
      .setPrefix(config.getPrefix())
      .setAlternativePrefix(config.getAltPrefix())
      .setOwnerId(Long.toString(config.getOwnerId()))
      .setEmojis(config.getSuccess(), config.getWarning(), config.getError())
      .setHelpWord(config.getHelp())
      .setLinkedCacheSize(200)
      .setGuildSettingsManager(settings)
      .addCommands(
        new AboutCmd(bot, RECOMMENDED_PERMS),
        new PingCommand(),
        new SettingsCmd(bot),
        new ChessCmd(bot),
        new DoodleCmd(bot),
        new PokerCmd(bot),
        new RockPaperScissorsCmd(bot),
        new YoutubeCmd(bot),
        new ProfilePictureCmd(bot),
        new LyricsCmd(bot),
        new NowplayingCmd(bot),
        new PlayCmd(bot),
        new PlaylistsCmd(bot),
        new QueueCmd(bot),
        new RemoveCmd(bot),
        new SearchCmd(bot),
        new SCSearchCmd(bot),
        new ShuffleCmd(bot),
        new SkipCmd(bot),
        new SeekCmd(bot),
        new ForceRemoveCmd(bot),
        new ForceskipCmd(bot),
        new MoveTrackCmd(bot),
        new PauseCmd(bot),
        new PlaynextCmd(bot),
        new RepeatCmd(bot),
        new SkiptoCmd(bot),
        new StopCmd(bot),
        new VolumeCmd(bot),
        new DeleteCommandsCmd(bot),
        new PrefixCmd(bot),
        new SetdjCmd(bot),
        new SettcCmd(bot),
        new SetvcCmd(bot),
        new AutoplaylistCmd(bot),
        new DebugCmd(bot),
        new PlaylistCmd(bot),
        new SetavatarCmd(bot),
        new SetgameCmd(bot),
        new SetnameCmd(bot),
        new SetstatusCmd(bot),
        new ShutdownCmd(bot),
        new SendToAllOwnersCmd(bot)
      ).addSlashCommands(
        new AboutCmd(bot, RECOMMENDED_PERMS),
        new SettingsCmd(bot),
        new ChessCmd(bot),
        new DoodleCmd(bot),
        new PokerCmd(bot),
        new RockPaperScissorsCmd(bot),
        new YoutubeCmd(bot),
        new ProfilePictureCmd(bot),
        new LyricsCmd(bot),
        new NowplayingCmd(bot),
        new PlayCmd(bot),
        new PlaylistsCmd(bot),
        new QueueCmd(bot),
        new RemoveCmd(bot),
        new SearchCmd(bot),
        new SCSearchCmd(bot),
        new ShuffleCmd(bot),
        new SkipCmd(bot),
        new SeekCmd(bot),
        new ForceRemoveCmd(bot),
        new ForceskipCmd(bot),
        new MoveTrackCmd(bot),
        new PauseCmd(bot),
        new PlaynextCmd(bot),
        new RepeatCmd(bot),
        new SkiptoCmd(bot),
        new StopCmd(bot),
        new VolumeCmd(bot),
        new DeleteCommandsCmd(bot),
        new PrefixCmd(bot),
        new SetdjCmd(bot),
        new SettcCmd(bot),
        new SetvcCmd(bot),
        new AutoplaylistCmd(bot),
        new DebugCmd(bot),
        new ShutdownCmd(bot),
        new SendToAllOwnersCmd(bot)
      );

    if (config.useEval()) cb.addCommand(new EvalCmd(bot)).addSlashCommands(new EvalCmd(bot));
    boolean nogame = false;
    if (config.getStatus() != OnlineStatus.UNKNOWN) cb.setStatus(config.getStatus());
    if (config.getGame() == null) cb.useDefaultGame();
    else if (config.getGame().getName().equalsIgnoreCase("none")) {
      cb.setActivity(null);
      nogame = true;
    } else cb.setActivity(config.getGame());

    if (!prompt.isNoGUI()) {
      try {
        GUI gui = new GUI(bot);
        bot.setGUI(gui);
        gui.init();
      } catch (Exception e) {
        log.error("Не удалось запустить GUI. Если вы запускаете " + "этого бота на VDS/VPS то запускайте бота через " + "-Dnogui=true флаг.");
      }
    }

    log.info("Конфиг загружен из " + config.getConfigLocation());

    // attempt to log in and start
    try {
      JDA jda = JDABuilder.create(config.getToken(), Arrays.asList(INTENTS))
        .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE, CacheFlag.ONLINE_STATUS)
        .disableCache(CacheFlag.ACTIVITY)
        .setActivity(nogame ? null : Activity.playing("загрузка..."))
        .setStatus(config.getStatus() == OnlineStatus.INVISIBLE || config.getStatus() == OnlineStatus.OFFLINE ? OnlineStatus.INVISIBLE : OnlineStatus.DO_NOT_DISTURB)
        .addEventListeners(cb.build(), waiter, new Listener(bot))
        .setBulkDeleteSplittingEnabled(true)
        .build();
      bot.setJDA(jda);
    } catch (IllegalArgumentException ex) {
      prompt.alert(Prompt.Level.ERROR, "JMusicBot", "Некоторые строчки конфигурационного файла " + "не правильные: " + ex + "\nРасположение config.go: " + config.getConfigLocation());
      System.exit(1);
    }

    // console listener
    textInput = new ConsoleListener(bot);
    textInput.start();

    DateFormat formatter;
    formatter = new SimpleDateFormat("s.SSS");
    formatter.setTimeZone(TimeZone.getTimeZone("UTC+3"));

    log.info("Done (" + formatter.format(System.currentTimeMillis() - timer) + "s)! For help, type " + config.getPrefix() + "help in chat");
  }
}
