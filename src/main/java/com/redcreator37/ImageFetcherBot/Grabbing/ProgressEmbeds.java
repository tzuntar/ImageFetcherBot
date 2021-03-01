package com.redcreator37.ImageFetcherBot.Grabbing;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

import java.text.MessageFormat;
import java.time.Instant;

public class ProgressEmbeds {

    public static void progressEmbed(EmbedCreateSpec spec, int progress, int goal) {
        spec.setTitle("Hold on...");
        spec.setColor(Color.GREEN);
        spec.addField("Looking for all files in this channel...",
                MessageFormat.format("Progress: {0} of {1}", progress, goal), false);
        spec.setTimestamp(Instant.now());
    }

}
