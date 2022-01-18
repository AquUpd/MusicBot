package com.jagrosh.jmusicbot.commands.dj;


import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.TimeZone;

/**
 * Command that provides users the ability to move a track in the playlist.
 */
public class SpeedCmd extends DJCommand
{

    public SpeedCmd(Bot bot)
    {
        super(bot);
        this.name = "speed";
        this.help = "ускоряет пластинку (баганная, не меняет текущий трек)";
        this.arguments = "<скорость 0.25-4.0>";
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event)
    {

        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        AudioPlayer player = handler.getPlayer();


        String parts = event.getArgs();

        float speed = 0;
        try
        {
            speed = Float.parseFloat(parts);
        }
        catch (NumberFormatException e)
        {
            player.setFilterFactory((track, format, output)->{
                TimescalePcmAudioFilter audioFilter = new TimescalePcmAudioFilter(output, format.channelCount, format.sampleRate);
                audioFilter.setSpeed(1.0);
                return Collections.singletonList(audioFilter);
            });
            String reply = "Скорость пластинки: **1.0x**";
            event.replySuccess(reply);
            return;
        }
        if((speed < 0.25) || (speed > 4.0)) {
            event.reply(event.getClient().getError()+" Скорость должна быть между 0.25 и 4.0!!");
            return;
        }

        if (!handler.getPlayer().getPlayingTrack().isSeekable()) {
            event.replyError("Эту пластинку нельзя ускорить.");
        } else {
            float finalSpeed = speed;
            player.setFilterFactory((track, format, output)->{
                TimescalePcmAudioFilter audioFilter = new TimescalePcmAudioFilter(output, format.channelCount, format.sampleRate);
                audioFilter.setSpeed(finalSpeed);
                return Collections.singletonList(audioFilter);
            });
            String reply = "Скорость пластинки: **" + speed + "**x";
            event.replySuccess(reply);
        }
    }
}