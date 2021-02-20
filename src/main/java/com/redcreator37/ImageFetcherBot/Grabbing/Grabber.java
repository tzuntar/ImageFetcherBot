package com.redcreator37.ImageFetcherBot.Grabbing;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Flux;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class Grabber {

    Grabber(OutputDefinition output, Snowflake progressMsg) {
        this.output = output;
        this.progressMsg = progressMsg;
    }

    private final OutputDefinition output;

    private final Snowflake progressMsg;

    Flux<Attachment> grabAttachments(MessageChannel channel, Snowflake before) {
        return channel.getMessagesBefore(before)
                .flatMap(message -> Flux.fromIterable(message.getAttachments()));
    }

    void saveUrls(Flux<Attachment> attachments) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(output.getLocation()))) {
            for (Attachment a : attachments.toIterable())
                writer.write(a.getUrl() + "\n");
        } catch (IOException e) {
            System.err.println("Saving data failed: " + e.getMessage());
        }
    }

    void downloadFiles(MessageChannel channel, Snowflake before) {
        if (!makeDirIfNotExists(new File("retrieved")))
            return;
        List<Attachment> attachments = grabAttachments(channel, before).collectList().block();
        for (int i = 0; i < Objects.requireNonNull(attachments).size(); i++) {
            reportProgress(i + 1, attachments.size());
            downloadAttachment(attachments.get(i));
        }
    }

    private void reportProgress(int progress, int goal) {

    }

    private static boolean makeDirIfNotExists(File dir) {
        return dir.exists() || dir.mkdir();
    }

    private static void downloadAttachment(Attachment attachment) {
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
