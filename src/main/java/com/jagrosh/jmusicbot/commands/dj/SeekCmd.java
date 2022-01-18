package com.jagrosh.jmusicbot.commands.dj;


import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import java.text.*;
import java.util.Collections;
import java.util.TimeZone;

/**
 * Command that provides users the ability to move a track in the playlist.
 */
public class SeekCmd extends DJCommand
{

    public SeekCmd(Bot bot)
    {
        super(bot);
        this.name = "seek";
        this.help = "проматывает пластинку до нужного момента";
        this.arguments = "[час:][мин:]<сек>";
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event)
    {

        int seconds; int minutes; int hours;
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        int musicduration = (int) handler.getPlayer().getPlayingTrack().getDuration();
        int needseek = 0;
        String[] parts = event.getArgs().split(":+", 3);

        try {
            if (parts.length == 1) { seconds = Integer.parseInt(parts[0]); needseek = seconds * 1000; }
            else if (parts.length == 2) { minutes = Integer.parseInt(parts[0]); seconds = Integer.parseInt(parts[1]); needseek = seconds * 1000 + minutes * 60000; }
            else if (parts.length == 3) { hours = Integer.parseInt(parts[0]); minutes = Integer.parseInt(parts[1]); seconds = Integer.parseInt(parts[2]); needseek = seconds * 1000 + minutes * 60000 + hours * 3600000; }
        }
        catch (NumberFormatException e)
        {
            event.replyError("Напишите время в формате `[час:][мин:]<сек>` без других символов.");
            return;
        }
        if (!handler.getPlayer().getPlayingTrack().isSeekable()) {
            event.replyError("Эту пластинку нельзя перемотать.");
        } else {
            if (musicduration < needseek) {
                DateFormat formatter;
                if (musicduration >= 3600000) {
                    formatter = new SimpleDateFormat("HH:mm:ss");
                } else {
                    formatter = new SimpleDateFormat("mm:ss");
                }
                formatter.setTimeZone(TimeZone.getTimeZone("UTC+3"));
                event.replyError("Время которое вы ввели превышает время пластинки. Максимальное время которое можно ввести: **" +
                    formatter.format(musicduration) + "**");
            }
            else {
                DateFormat formatter;
                if (needseek >= 3600000) {
                    formatter = new SimpleDateFormat("HH:mm:ss");
                } else {
                    formatter = new SimpleDateFormat("mm:ss");
                }
                formatter.setTimeZone(TimeZone.getTimeZone("UTC+3"));

                handler.getPlayer().getPlayingTrack().setPosition(needseek);
                String reply = "Пластинка успешно перемотана на: **" + formatter.format(needseek) + "**";
                event.replySuccess(reply);
            }
        }
    }
}