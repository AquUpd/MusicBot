package com.jagrosh.jmusicbot.commands.fun;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.FunCommand;
import java.awt.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AboutCmd extends FunCommand {
  private String oauthLink;
  private final Permission[] perms;

  public AboutCmd(Bot bot, Permission... perms){
    super(bot);
    this.name = "about";
    this.help = "информация о боте";
    this.perms = perms;
    this.botPermissions = new Permission[] { Permission.MESSAGE_EMBED_LINKS };
  }

  @Override
  public void doCommand(CommandEvent event) {
    try {
      ApplicationInfo info = event.getJDA().retrieveApplicationInfo().complete();
      info.setRequiredScopes("applications.commands");
      this.oauthLink = info.isBotPublic() ? info.getInviteUrl(this.perms) : "";
    } catch (Exception ex) {
      Logger log = LoggerFactory.getLogger("OAuth2");
      log.error("Could not generate invite link ", ex);
      this.oauthLink = "123";
    }

    EmbedBuilder eb = new EmbedBuilder();
    eb.setTitle("Информация о боте");
    eb.setAuthor("Aquatic(AqUpd)", "https://vk.com/aqupd", "https://images-ext-2.discordapp.net/external/wUDMMv2yHwSTyppYBdpQOrP0Mf0HWwonKMKFHwHghAE/https/cdn.discordapp.com/avatars/459442554623098882/552da0b00be2c96574fd823ea48803eb.png");
    //eb.set(bot.getJDA().getSelfUser().getAvatarUrl());
    eb.setColor(new Color(53, 180, 219, 255));
    eb.setDescription("Здравствуйте! Данный бот позволяет проигрывать музыку в войс чатах.\n " +
      "База этого бота: [GitHub](https://github.com/jagrosh/MusicBot). Ссылка на репозиторий данного бота: [GitHub](https://github.com/Ivan-Khar/MusicBot-rus)\n" +
      "Для большинства команд используется вот эта библиотека: [GitHub](https://github.com/Chew/JDA-Chewtils)" +
      ((oauthLink.equals("123")) ? "" : ("\nВы можете пригласить данного бота используя эту кнопку: [Ссылка](" + this.oauthLink + ")")));
    if (event.getJDA().getShardInfo() == JDA.ShardInfo.SINGLE) {
      eb.addField("Статистика", event.getJDA().getGuilds().size() + " Серверов\n1 Шард", true);
      eb.addField("Пользователей", event.getJDA().getUsers().size() + " Уникальных\n" + event.getJDA().getGuilds().stream().mapToInt((g) -> g.getMembers().size()).sum() + " Всего", true);
      eb.addField("Каналы", event.getJDA().getTextChannels().size() + " Текстовых\n" + event.getJDA().getVoiceChannels().size() + " Голосовых", true);
    } else {
      eb.addField("Статистика", event.getClient().getTotalGuilds() + " Серверов\nШард " + (event.getJDA().getShardInfo().getShardId() + 1) + "/" + event.getJDA().getShardInfo().getShardTotal(), true);
      eb.addField("Данный шард имеет", event.getJDA().getUsers().size() + " Users\n" + event.getJDA().getGuilds().size() + " Серверов", true);
      eb.addField("", event.getJDA().getTextChannels().size() + " Текстовых\n" + event.getJDA().getVoiceChannels().size() + " Голосовых", true);
    }
    event.reply(eb.build());
  }

  @Override
  public void doSlashCommand(SlashCommandEvent event) {
    try {
      ApplicationInfo info = event.getJDA().retrieveApplicationInfo().complete();
      info.setRequiredScopes("applications.commands");
      this.oauthLink = info.isBotPublic() ? info.getInviteUrl(this.perms) : "";
    } catch (Exception ex) {
      Logger log = LoggerFactory.getLogger("OAuth2");
      log.error("Could not generate invite link ", ex);
      this.oauthLink = "123";
    }

    EmbedBuilder eb = new EmbedBuilder();
    eb.setTitle("Информация о боте");
    eb.setAuthor("Aquatic(AqUpd)", "https://vk.com/aqupd", "https://images-ext-2.discordapp.net/external/wUDMMv2yHwSTyppYBdpQOrP0Mf0HWwonKMKFHwHghAE/https/cdn.discordapp.com/avatars/459442554623098882/552da0b00be2c96574fd823ea48803eb.png");
    eb.setColor(new Color(53, 180, 219, 255));
    eb.setDescription("Здравствуйте! Данный бот позволяет проигрывать музыку в войс чатах.\n " +
      "База этого бота: [GitHub](https://github.com/jagrosh/MusicBot). Ссылка на репозиторий данного бота: [GitHub](https://github.com/Ivan-Khar/MusicBot-rus)\n" +
      "Для большинства команд используется вот эта библиотека: [GitHub](https://github.com/Chew/JDA-Chewtils)" +
      ((oauthLink.equals("123")) ? "" : ("\nВы можете пригласить данного бота используя эту кнопку: [Ссылка](" + this.oauthLink + ")")));
    if (event.getJDA().getShardInfo() == JDA.ShardInfo.SINGLE) {
      eb.addField("Статистика", event.getJDA().getGuilds().size() + " Серверов\n1 Шард", true);
      eb.addField("Пользователей", event.getJDA().getUsers().size() + " Уникальных\n" + event.getJDA().getGuilds().stream().mapToInt((g) -> g.getMembers().size()).sum() + " Всего", true);
      eb.addField("Каналы", event.getJDA().getTextChannels().size() + " Текстовых\n" + event.getJDA().getVoiceChannels().size() + " Голосовых", true);
    } else {
      eb.addField("Статистика", event.getClient().getTotalGuilds() + " Серверов\nШард " + (event.getJDA().getShardInfo().getShardId() + 1) + "/" + event.getJDA().getShardInfo().getShardTotal(), true);
      eb.addField("Данный шард имеет", event.getJDA().getUsers().size() + " Пользователей\n" + event.getJDA().getGuilds().size() + " Серверов", true);
      eb.addField("", event.getJDA().getTextChannels().size() + " Текстовых\n" + event.getJDA().getVoiceChannels().size() + " Голосовых", true);
    }
    event.getHook().editOriginalEmbeds(eb.build()).queue();
  }
}
