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

public class Downloader {
    static boolean makeDirIfNotExists(File dir) {
        return dir.exists() || dir.mkdir();
    }

    static void downloadAttachment(Attachment attachment) {
        try {
            ReadableByteChannel readableByteChannel = Channels
                    .newChannel(new URL(attachment.getUrl()).openStream());

            // prefix filenames with snowflakes to avoid collisions
            Path outPath = Paths.get("retrieved", attachment.getId().toString()
                    + "_" + attachment.getFilename());

            FileOutputStream fileOutputStream = new FileOutputStream(outPath.toFile());
            fileOutputStream.getChannel()
                    .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (IOException e) {   // todo: better error and progress reporting
            System.err.println(e.getMessage());
        }
    }
}
