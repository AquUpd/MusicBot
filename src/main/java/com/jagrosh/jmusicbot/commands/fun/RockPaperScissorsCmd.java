package com.jagrosh.jmusicbot.commands.fun;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.FunCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public class RockPaperScissorsCmd extends FunCommand {

  public RockPaperScissorsCmd(Bot bot) {
    super(bot);
    this.name = "rps";
    this.arguments = "<камень|ножницы|бумага>";
    this.options = Collections.singletonList(new OptionData(OptionType.STRING, "rps", "<камень|ножницы|бумага>").setRequired(true));
    this.help = "камень, ножницы, бумага";
    this.botPermissions = new Permission[] { Permission.MESSAGE_EMBED_LINKS };
  }

  @Override
  public void doCommand(CommandEvent event) {
    String argument = String.valueOf(event.getArgs()).toLowerCase();
    if (argument.length() != 0) {
      String arg;
      switch(rps(argument)) {
        case -1:
          event.replyError("Напишите \"камень\", \"ножницы\" или \"бумага\" после команды.");
          break;
        case 1:
          event.reply(":upside_down: Бот выбрал: `" + argument + "`, Игрок выбрал: `" + argument + "`. Ничья!");
          break;
        case 2:
          if(argument.equals("камень")) arg = "ножницы";
          else if(argument.equals("ножницы")) arg = "бумага";
          else arg = "камень";
          event.reply(":sunglasses: Бот выбрал: `" + arg + "`, Игрок выбрал: `" + argument + "`. Вы выйграли!");
          break;
        case 3:
          if(argument.equals("камень")) arg = "бумага";
          else if(argument.equals("ножницы")) arg = "камень";
          else arg = "ножницы";
          event.reply(":sob: Бот выбрал: `" + arg + "`, Игрок выбрал: `" + argument + "`. Вы проиграли!");
      }
    } else {
      event.replyError("Напишите \"камень\", \"ножницы\" или \"бумага\" после команды.");
    }
  }

  @Override
  public void doSlashCommand(SlashCommandEvent event) {
    String argument = event.getOption("rps").getAsString();
    String arg;
    switch(rps(argument)) {
      case -1:
        event.getHook().editOriginal("Напишите \"камень\", \"ножницы\" или \"бумага\" после команды.")
          .queue();
        break;
      case 1:
        event.getHook().editOriginal(":upside_down: Бот выбрал: `" + argument + "`, Игрок выбрал: `" + argument + "`. Ничья!").queue();
        break;
      case 2:
        if (argument.equals("камень")) arg = "ножницы";
        else if (argument.equals("ножницы")) arg = "бумага";
        else arg = "камень";
        event.getHook().editOriginal(":sunglasses: Бот выбрал: `" + arg + "`, Игрок выбрал: `" + argument + "`. Вы выйграли!").queue();
        break;
      case 3:
        if (argument.equals("камень")) arg = "бумага";
        else if (argument.equals("ножницы")) arg = "камень";
        else arg = "ножницы";
        event.getHook().editOriginal(":sob: Бот выбрал: `" + arg + "`, Игрок выбрал: `" + argument + "`. Вы проиграли!").queue();
    }
  }

  private int rps(String input){
    Random random = new Random(System.currentTimeMillis());
    int rpsbot = random.nextInt(3);
    int rpsplayer;

    switch (input.toLowerCase(Locale.ENGLISH)) {
      case "камень":
        rpsplayer = 0;
        break;
      case "ножницы":
        rpsplayer = 1;
        break;
      case "бумага":
        rpsplayer = 2;
        break;
      default:
        return -1;
    }

    if (rpsbot == rpsplayer) {
      return 1; //Ничья
    } else if ((rpsplayer == 0 && rpsbot == 1) || (rpsplayer == 1 && rpsbot == 2) || (rpsplayer == 2 && rpsbot == 0)) {
      return 2; //Выигрыш
    } else {
      return 3; //Проигрыш
    }
  }
}
