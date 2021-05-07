package com.redcreator37.ImageFetcherBot.Grabbing;

import discord4j.core.object.entity.Attachment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contains methods for retrieving files, pictures and
 * other data from the network
 */
public final class Downloader {

    /**
     * Checks if this directory exists and creates it if it doesn't
     *
     * @param dir the directory {@link File} to look for
     * @return {@code true} if the directory exists or was
     * successfully created, {@code false} otherwise.
     */
    static boolean makeDirIfNotExists(File dir) {
        return dir.exists() || dir.mkdir();
    }

    /**
     * Downloads this attachment object and saves it
     *
     * @param attachment the {@link Attachment} to download
     */
    static void downloadAttachment(Attachment attachment) {
        try {
            ReadableByteChannel readableByteChannel = Channels
                    .newChannel(new URL(attachment.getUrl()).openStream());

            // adds attachment snowflake to the filename to avoid collisions
            Path outPath = Paths.get("retrieved", attachment.getId()
                    + "_" + attachment.getFilename()
                    .replace('{', ' ')      // replaces the opening and closing
                    .replace('}', ' '));    // snowflake ID string brackets

            FileOutputStream fileOutputStream = new FileOutputStream(outPath.toFile());
            fileOutputStream.getChannel()
                    .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (IOException e) {   // todo: better error and progress reporting
            System.err.println(e.getMessage());
        }
    }

}
