package com.redcreator37.ImageFetcherBot.Grabbing;

import com.redcreator37.ImageFetcherBot.DiscordBot;
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
        spec.addField("Looking for files in this channel...",
                MessageFormat.format("Progress: {0} of {1}", progress, goal), false);
        spec.setDescription("The status updates for every 20 items");
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
        spec.addField("All files have been saved",
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
        spec.addField("All links have been saved",
                "Look in the file called `output.txt`", false);
        spec.setTimestamp(Instant.now());
    }

    /**
     * Creates an invalid syntax embed
     *
     * @param spec          a blank {@link EmbedCreateSpec}
     * @param grabbingFiles set to {@code true} when grabbing files or
     *                      {@code false} when grabbing links (used
     *                      for supplying the correct command name)
     */
    public static void invalidSyntax(EmbedCreateSpec spec, boolean grabbingFiles) {
        String commandName = DiscordBot.cmdPrefix + (grabbingFiles
                ? "grabfiles" : "grablinks");
        spec.setTitle("Invalid Syntax");
        spec.setDescription("**You're using it wrong!** These are the supported commands:");
        spec.setColor(Color.RED);
        spec.addField("Everything:", commandName, false);
        spec.addField("Before the message ID:", commandName + " before [ID]", true);
        spec.addField("After the message ID:", commandName + " after [ID]", true);
        spec.setFooter("Use Right Click ›› Copy ID with developer options enabled to get message IDs", null);
        spec.setTimestamp(Instant.now());
    }

}
