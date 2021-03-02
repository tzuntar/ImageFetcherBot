package com.redcreator37.ImageFetcherBot.Grabbing;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

/**
 * Manages general command handling and redirection
 */
@SuppressWarnings("BlockingMethodInNonBlockingContext")
public class CommandHandler {

    /**
     * Downloads all files in the channel in which the message was
     * sent
     *
     * @param event the {@link MessageCreateEvent} which occurred when
     *              the message was sent
     * @return an empty {@link Mono}
     */
    public static Mono<Void> grabFiles(MessageCreateEvent event) {
        MessageChannel channel = event.getMessage().getChannel().block();
        assert channel != null;
        Message progressMsg = channel.createEmbed(spec ->
                ProgressEmbeds.progressEmbed(spec, 1, 1)).block();

        String[] params = event.getMessage().getContent().split(" ");
        Grabber grabber = new Grabber(new OutputDefinition("retrieved",
                OutputDefinition.Type.FILE_DIRECTORY), progressMsg);
        grabber.grab(channel, params.length == 1
                ? null : Snowflake.of(params[1]));

        channel.createEmbed(ProgressEmbeds::retrievingFilesFinished).block();
        return Mono.empty();
    }

    /**
     * Saves all links in the channel in which the message was sent
     *
     * @param event the {@link MessageCreateEvent} which occurred when
     *              the message was sent
     * @return an empty {@link Mono}
     */
    public static Mono<Void> grabLinks(MessageCreateEvent event) {
        MessageChannel channel = event.getMessage().getChannel().block();
        assert channel != null;
        Message progressMsg = channel.createEmbed(spec ->
                ProgressEmbeds.progressEmbed(spec, 1, 1)).block();

        String[] params = event.getMessage().getContent().split(" ");
        Grabber grabber = new Grabber(new OutputDefinition("output.txt",
                OutputDefinition.Type.LINK_FILE), progressMsg);
        grabber.grab(channel, params.length == 1
                ? null : Snowflake.of(params[1]));

        channel.createEmbed(ProgressEmbeds::savingLinksFinished).block();
        return Mono.empty();
    }

}
