package com.jagrosh.jmusicbot.commands.fun;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.FunCommand;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.entities.Member;

public class ProfilePictureCmd extends FunCommand {

  public ProfilePictureCmd(Bot bot) {
    super(bot);
    this.name = "ppicture";
    this.arguments =
      "[имя пользователя / дискорд тег пользователя / id пользователя]";
    this.help = "получает изображение профиля";
  }

  @Override
  public void doCommand(CommandEvent event) {
    if (event.getGuild().getId().equals("745746205144776774")) {
      event.replyError("Нельзя использовать данную команду на этом сервере");
    } else {
      String argument = String.valueOf(event.getArgs());
      if (argument.length() == 0) {
        event.replySuccess(
          "Ваше изображение профиля: \n" + event.getAuthor().getAvatarUrl()
        );
      } else {
        event.getGuild().loadMembers();
        if (discordtag(argument)) {
          Member member = event.getGuild().getMemberByTag(argument);
          if (member == null) {
            event.replyError(
              "Нет пользователя с таким дискорд тегом на данном сервере"
            );
          } else {
            event.reply(
              "Изображение профиля " +
              member.getEffectiveName() +
              " \n" +
              member.getEffectiveAvatarUrl()
            );
          }
        } else if (discordid(argument)) {
          Member member = event.getGuild().getMemberById(argument);
          if (member == null) {
            event.replyError("Нет пользователя с таким id на данном сервере");
          } else {
            event.reply(
              "Изображение профиля " +
              member.getEffectiveName() +
              " \n" +
              member.getEffectiveAvatarUrl()
            );
          }
        } else {
          if (
            event.getGuild().getMembersByName(argument, true).size() == 0
          ) event.replyError("Нет пользователей с таким ником"); else if (
            event.getGuild().getMembersByName(argument, true).size() > 1
          ) event.replyError(
            "Есть несколько пользователей с таким ником"
          ); else {
            Member member = event
              .getGuild()
              .getMembersByName(argument, true)
              .get(0);
            event.reply(
              "Изображение профиля " +
              member.getEffectiveName() +
              " \n" +
              member.getEffectiveAvatarUrl()
            );
          }
        }
      }
    }
  }

  public static boolean discordtag(String str) {
    String regex = "^.{3,32}#[0-9]{4}$";

    // Compile the ReGex
    Pattern p = Pattern.compile(regex);

    // If the string is empty
    // return false
    if (str == null) {
      return false;
    }

    // Find match between given string
    // and regular expression
    // using Pattern.matcher()
    Matcher m = p.matcher(str);

    // Return if the string
    // matched the ReGex
    return m.matches();
  }

  public static boolean discordid(String str) {
    String regex = "^[0-9]{17,19}$";

    // Compile the ReGex
    Pattern p = Pattern.compile(regex);

    // If the string is empty
    // return false
    if (str == null) {
      return false;
    }

    // Find match between given string
    // and regular expression
    // using Pattern.matcher()
    Matcher m = p.matcher(str);

    // Return if the string
    // matched the ReGex
    return m.matches();
  }
}
