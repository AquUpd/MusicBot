package com.jagrosh.jmusicbot.commands.disabled;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.FunCommand;
import net.dv8tion.jda.api.Permission;

import static com.jagrosh.jmusicbot.commands.fun.FunUtils.genLink;

public class DoodleCmd extends FunCommand {

  public DoodleCmd(Bot bot) {
    super(bot);
    this.name = "doodle";
    this.help = "запускает игру \"Doodle Crew\"";
    this.botPermissions = new Permission[] { Permission.MESSAGE_EMBED_LINKS };
    this.beInChannel = true;
  }

  //878067389634314250
  @Override
  public void doCommand(CommandEvent event) {
    String code = genLink(event.getMember().getVoiceState().getChannel().getId(), 878067389634314250L);
    if(code != null) event.reply("https://discord.com/invite/" + code);
    else event.replyError("Я не смог создать ссылку");
  }

  @Override
  public void doSlashCommand(SlashCommandEvent event) {
    String code = genLink(event.getMember().getVoiceState().getChannel().getId(), 878067389634314250L);
    if(code != null)
      event.getHook().editOriginal("https://discord.com/invite/" + code).queue();
    else
      event.getHook().editOriginal("Я не смог создать ссылку")
        .queue();
  }
}
