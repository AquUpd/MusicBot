package com.jagrosh.jmusicbot;

import java.util.List;
import java.util.Scanner;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("InfiniteRecursion")
public class ConsoleListener implements Runnable {

  static Thread thread;
  private final Bot bot;
  private final JDA jda;
  Logger log = LoggerFactory.getLogger("CL");
  Scanner inputReader = new Scanner(System.in);

  public ConsoleListener(Bot bot) {
    this.bot = bot;
    this.jda = bot.getJDA();
  }

  //This method gets called from the main
  public void start() {
    if (thread == null) {
      thread = new Thread(this);
      thread.start();
    }
  }

  //this method gets called by the thread.start(); from above
  @Override
  public void run() {
    log.info("Консольные команды загружены! Чтобы получить список команд напишите \"help\" в консоль");
    readTextFromConsole();
  }

  public void readTextFromConsole() {
    String[] input = inputReader.nextLine().split("\\s+");
    switch (input[0]) {
      case "help":
        log.info("Список команд: \n" + "getguilds - выводит список всех доступных серверов \n" + "getchannels <guildid> - выводит список всех каналов сервера \n" + "leaveguild <guildid> - выходит из сервера \n" + "sendmessage <guildid> <channelid> <message> - отправляет сообщение в нужный канал сервера \n" + "stop - выключает бота \n");
        break;
      case "stop":
        bot.shutdown();
        break;
      case "sendmessage":
        if (input.length < 3) {
          log.info("Использование команды: sendmessage <guildid> <channelid> <message>");
        } else {
          String gld = input[1];
          String chnl = input[2];
          StringBuilder mesgbuilder = new StringBuilder();
          for (int i = 3; i < input.length; i++) {
            mesgbuilder.append(input[i]).append(" ");
          }
          String mesg = String.valueOf(mesgbuilder);

          try {
            //jda.getGuildById(gld).getTextChannelById(chnl).sendMessage(mesg).queue();
            jda.getGuildById(gld).getVoiceChannelById(chnl).sendMessage(mesg).queue();
            log.info("Отправлено!");
          } catch (InsufficientPermissionException ipe) {
            log.error("Я не могу отправлять сообщения в этот чат: ");
            ipe.printStackTrace();
          } catch (NumberFormatException nfe) {
            log.error("Какой-то из ID введен не правильно: ");
            nfe.printStackTrace();
          } catch (Exception e) {
            log.error("Что-то пошло не так: ");
            e.printStackTrace();
          }
        }
        break;
      case "leaveguild":
        if (input.length != 2) {
          log.info("Использование команды: leaveguild <guildid>");
        } else {
          String gld = input[1];
          try {
            jda.getGuildById(gld).leave().queue();
            log.info("Успешно вышел из сервера");
          } catch (NumberFormatException nfe) {
            log.error("ID введен не правильно: ");
            nfe.printStackTrace();
          } catch (Exception e) {
            log.error("Что-то пошло не так: ");
            e.printStackTrace();
          }
        }
        break;
      case "getguilds":
        StringBuilder guildslist = new StringBuilder();
        List<Guild> allguilds = jda.getGuilds();
        for (Guild guild : allguilds) {
          guildslist.append(guild.getId()).append(" ").append(guild.getName()).append(" ").append(guild.getMemberCount()).append(" участника\n");
        }
        log.info("Список всех гильдий: \n" + guildslist);
        break;
      case "getchannels":
        if (input.length != 2) {
          log.info("Использование команды: getchannels <guildid>");
        } else {
          String gld = input[1];
          try {
            StringBuilder channelslist = new StringBuilder();
            List<GuildChannel> allchannels = jda.getGuildById(gld).getChannels();

            for (GuildChannel channel : allchannels) {
              if (channel.getType() != ChannelType.CATEGORY)
                channelslist.append(channel.getId()).append(" ").append(channel.getName()).append(" ").append(channel.getType()).append("\n");
            }
            log.info("Список всех каналов в гильдии " + jda.getGuildById(gld).getName() + ": \n" + channelslist);
          } catch (NumberFormatException nfe) {
            log.error("ID введен не правильно: ");
            nfe.printStackTrace();
          } catch (Exception e) {
            log.error("Что-то пошло не так: ");
            e.printStackTrace();
          }
        }
        break;
    }

    readTextFromConsole();
  }
}
