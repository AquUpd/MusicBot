package com.jagrosh.jmusicbot.commands.fun;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.FunCommand;
import com.jagrosh.jmusicbot.utils.DefaultContentTypeInterceptor;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import net.dv8tion.jda.api.Permission;
import okhttp3.*;
import org.json.JSONObject;

public class RockPaperScissorsCmd extends FunCommand {

  public RockPaperScissorsCmd(Bot bot) {
    super(bot);
    this.name = "rps";
    this.arguments = "<камень|ножницы|бумага>";
    this.help = "камень, ножницы, бумага";
    this.botPermissions = new Permission[] { Permission.MESSAGE_EMBED_LINKS };
  }

  @Override
  public void doCommand(CommandEvent event) throws IOException {
    String argument = String.valueOf(event.getArgs());
    if (argument.length() != 0) {
      Random random = new Random(System.currentTimeMillis());
      int rpsbot = random.nextInt(3);
      int rpsplayer;
      String rpsbotname;
      String rpsplayername;
      String result;
      String emoji;

      switch (rpsbot) {
        case 0:
          rpsbotname = "камень";
          break;
        case 1:
          rpsbotname = "ножницы";
          break;
        case 2:
          rpsbotname = "бумага";
          break;
        default:
          rpsbotname = "ничего";
          break;
      }

      switch (argument.toLowerCase(Locale.ENGLISH)) {
        case "камень":
          rpsplayer = 0;
          rpsplayername = "камень";
          break;
        case "ножницы":
          rpsplayer = 1;
          rpsplayername = "ножницы";
          break;
        case "бумага":
          rpsplayer = 2;
          rpsplayername = "бумага";
          break;
        default:
          event.replyError(
            "Напишите \"камень\", \"ножницы\" или \"бумага\" после команды."
          );
          return;
      }

      if (rpsbot == rpsplayer) {
        result = "Ничья!";
        emoji = ":upside_down: ";
      } else if (
        (rpsplayer == 0 && rpsbot == 1) ||
        (rpsplayer == 1 && rpsbot == 2) ||
        (rpsplayer == 2 && rpsbot == 0)
      ) {
        result = "Вы победили!";
        emoji = ":sunglasses: ";
      } else {
        result = "Вы проиграли!";
        emoji = ":sob: ";
      }

      event.reply(
        emoji +
        "Бот выбрал: `" +
        rpsbotname +
        "`, Игрок выбрал: `" +
        rpsplayername +
        "`. " +
        result
      );
    } else {
      event.replyError(
        "Напишите \"камень\", \"ножницы\" или \"бумага\" после команды."
      );
    }
  }
}
