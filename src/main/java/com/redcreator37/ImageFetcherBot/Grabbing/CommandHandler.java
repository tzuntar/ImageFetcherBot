package com.redcreator37.ImageFetcherBot.Grabbing;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

import java.time.Instant;

public class CommandHandler {

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
        Grabber grabber = new Grabber(new OutputDefinition("retrieved",
                OutputDefinition.Type.FILE_DIRECTORY));
        grabber.downloadFiles(channel, params.length == 1
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
        Grabber grabber = new Grabber(new OutputDefinition("output.txt",
                OutputDefinition.Type.LINK_FILE));
        grabber.downloadFiles(channel, params.length == 1
                ? Snowflake.of(Instant.now())
                : Snowflake.of(params[1]));

        Snowflake before = Snowflake.of(params[1]);
        grabber.saveUrls(grabber.grabAttachments(channel, before));
        channel.createEmbed(spec -> {
            spec.setTitle("Done!");
            spec.setColor(Color.GREEN);
            spec.addField("All links have been retrieved",
                    "Filename: `output.txt`", false);
            spec.setTimestamp(Instant.now());
        }).block();
        return Mono.empty();
    }

}
