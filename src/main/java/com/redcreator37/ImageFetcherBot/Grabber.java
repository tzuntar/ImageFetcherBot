package com.redcreator37.ImageFetcherBot;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("BlockingMethodInNonBlockingContext")
public class Grabber {

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
                : Snowflake.of(params[1])).block();

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
        grabAll(channel).block();
        channel.createEmbed(spec -> {
            spec.setTitle("Done!");
            spec.setColor(Color.GREEN);
            spec.addField("All links have been retrieved",
                    "Filename: `output.txt`", false);
            spec.setTimestamp(Instant.now());
        }).block();
        return Mono.empty();
    }

    private static Mono<Void> grabAll(MessageChannel channel) {
        List<Message> messages = channel.getMessagesBefore(Snowflake
                .of(Instant.now())).collectList().block();

        List<Attachment> attachments = new ArrayList<>();
        Objects.requireNonNull(messages).forEach(message ->
                attachments.addAll(message.getAttachments()));

        return saveUrls(Objects.requireNonNull(attachments));
    }

    private static Mono<Void> saveUrls(Iterable<Attachment> attachments) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            for (Attachment a : attachments)
                writer.write(a.getUrl() + "\n");
        } catch (IOException e) {
            System.err.println("Saving data failed: " + e.getMessage());
        }
        return Mono.empty();
    }

    private static Mono<Void> downloadFiles(MessageChannel channel, Snowflake messagesBefore) {
        List<Message> messages = channel.getMessagesBefore(messagesBefore)
                .collectList().block();

        List<Attachment> attachments = new ArrayList<>();
        Objects.requireNonNull(messages).forEach(message ->
                attachments.addAll(message.getAttachments()));

        if (!new File("retrieved").mkdir()) return Mono.empty();
        attachments.forEach(attachment -> {
            try {
                ReadableByteChannel readableByteChannel = Channels
                        .newChannel(new URL(attachment.getUrl()).openStream());

                // the following prefixes message snowflakes to avoid filename collisions
                Path outPath = Paths.get("retrieved", attachment.getId().toString()
                        + "_" + attachment.getFilename());

                FileOutputStream fileOutputStream = new FileOutputStream(outPath.toFile());
                fileOutputStream.getChannel()
                        .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            } catch (IOException e) {   // todo: better error and progress reporting
                System.err.println(e.getMessage());
            }
        });
        return saveUrls(Objects.requireNonNull(attachments));
    }

}
