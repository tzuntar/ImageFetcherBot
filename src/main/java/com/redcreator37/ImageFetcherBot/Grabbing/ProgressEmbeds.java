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

    /**
     * Creates an embed saying that retrieving files has been finished
     *
     * @param spec a blank {@link EmbedCreateSpec}
     */
    public static void retrievingFilesFinished(EmbedCreateSpec spec) {
        spec.setTitle("Done!");
        spec.setColor(Color.GREEN);
        spec.addField("All files have been retrieved",
                "Look in the folder called `retrieved`", false);
        spec.setTimestamp(Instant.now());
    }

    /**
     * Creates an embed saying that saving the links has been finished
     *
     * @param spec a blank {@link EmbedCreateSpec}
     */
    public static void savingLinksFinished(EmbedCreateSpec spec) {
        spec.setTitle("Done!");
        spec.setColor(Color.GREEN);
        spec.addField("All links have been retrieved",
                "Filename: `output.txt`", false);
        spec.setTimestamp(Instant.now());
    }

}
