package com.redcreator37.ImageFetcherBot;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
import java.time.Instant;

@SuppressWarnings("BlockingMethodInNonBlockingContext")
public class Grabber {

    private static final String LINK_OUTPUT = "output.txt";

    public static Mono<Void> grabFiles(MessageCreateEvent event) {
        MessageChannel channel = event.getMessage().getChannel().block();
        assert channel != null;
        channel.createEmbed(spec -> {
            spec.setTitle("Hold on...");
            spec.setColor(Color.GREEN);
            spec.addField("Looking for all files in this channel...",
                    "You will be notified once the process has been completed", false);
            spec.setTimestamp(Instant.now());
        }).block();

        String[] params = event.getMessage().getContent().split(" ");
        downloadFiles(channel, params.length == 1
                ? Snowflake.of(Instant.now())
                : Snowflake.of(params[1]));

        channel.createEmbed(spec -> {
            spec.setTitle("Done!");
            spec.setColor(Color.GREEN);
            spec.addField("All files have been retrieved",
                    "Look in the folder called `retrieved`", false);
            spec.setTimestamp(Instant.now());
        }).block();
        return Mono.empty();
    }

    public static Mono<Void> grabLinks(MessageCreateEvent event) {
        MessageChannel channel = event.getMessage().getChannel().block();
        assert channel != null;
        channel.createEmbed(spec -> {
            spec.setTitle("Hold on...");
            spec.setColor(Color.YELLOW);
            spec.addField("Looking for all links in this channel...",
                    "You will be notified once the process has been completed", false);
            spec.setTimestamp(Instant.now());
        }).block();

        String[] params = event.getMessage().getContent().split(" ");
        downloadFiles(channel, params.length == 1
                ? Snowflake.of(Instant.now())
                : Snowflake.of(params[1]));

        Snowflake before = Snowflake.of(params[1]);
        saveUrls(grabAttachments(channel, before));
        channel.createEmbed(spec -> {
            spec.setTitle("Done!");
            spec.setColor(Color.GREEN);
            spec.addField("All links have been retrieved",
                    "Filename: `output.txt`", false);
            spec.setTimestamp(Instant.now());
        }).block();
        return Mono.empty();
    }

    private static Flux<Attachment> grabAttachments(MessageChannel channel, Snowflake before) {
        return channel.getMessagesBefore(before)
                .flatMap(message -> Flux.fromIterable(message.getAttachments()));
    }

    private static void saveUrls(Flux<Attachment> attachments) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LINK_OUTPUT))) {
            for (Attachment a : attachments.toIterable())
                writer.write(a.getUrl() + "\n");
        } catch (IOException e) {
            System.err.println("Saving data failed: " + e.getMessage());
        }
    }

    private static void downloadFiles(MessageChannel channel, Snowflake before) {
        if (!makeDirIfNotExists(new File("retrieved")))
            return;
        grabAttachments(channel, before).doOnNext(Grabber::downloadAttachment);
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
