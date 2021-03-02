package com.redcreator37.ImageFetcherBot.Grabbing;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

import java.text.MessageFormat;
import java.time.Instant;

/**
 * Prepares message embeds containing progress and error reporting
 * messages
 */
public class ProgressEmbeds {

    /**
     * Creates an embed reporting the number of already retrieved and
     * remaining items
     *
     * @param spec     a blank {@link EmbedCreateSpec}
     * @param progress the number of already retrieved item
     * @param goal     the number of remaining items
     */
    public static void progressEmbed(EmbedCreateSpec spec, int progress, int goal) {
        spec.setTitle("Hold on...");
        spec.setColor(Color.GREEN);
        spec.addField("Looking for all files in this channel...",
                MessageFormat.format("Progress: {0} of {1}", progress, goal), false);
        spec.setTimestamp(Instant.now());
    }

}
