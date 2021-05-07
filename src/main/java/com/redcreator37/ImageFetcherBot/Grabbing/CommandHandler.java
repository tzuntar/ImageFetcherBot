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
        String[] params = event.getMessage().getContent().split(" ");
        if (params.length > 1 && !params[1].matches("before|after")) {
            return channel.createEmbed(spec ->
                    ProgressEmbeds.invalidSyntax(spec, true)).then();
        }

        Message progressMsg = channel.createEmbed(spec ->
                ProgressEmbeds.progressEmbed(spec, 1, 1)).block();
        Grabber grabber = new Grabber(new OutputDefinition("retrieved",
                OutputDefinition.Type.FILE_DIRECTORY), progressMsg);

        if (params.length < 2) {   // no parameters, get everything
            grabber.grabBefore(channel, null);
        } else if (params.length == 3) {    // before / after ID
            if (params[1].equalsIgnoreCase("before"))
                grabber.grabBefore(channel, Snowflake.of(params[2]));
            else if (params[1].equalsIgnoreCase("after"))
                grabber.grabAfter(channel, Snowflake.of(params[2]));
        }

        return channel.createEmbed(ProgressEmbeds::retrievingFilesFinished).then();
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
        String[] params = event.getMessage().getContent().split(" ");
        if (params.length > 1 && !params[1].matches("before|after")) {
            return channel.createEmbed(spec ->
                    ProgressEmbeds.invalidSyntax(spec, true)).then();
        }

        Message progressMsg = channel.createEmbed(spec ->
                ProgressEmbeds.progressEmbed(spec, 1, 1)).block();
        Grabber grabber = new Grabber(new OutputDefinition("output.txt",
                OutputDefinition.Type.LINK_FILE), progressMsg);

        if (params.length < 2) {   // no parameters, get everything
            grabber.grabBefore(channel, null);
        } else if (params.length == 3) {    // before / after ID
            if (params[1].equalsIgnoreCase("before"))
                grabber.grabBefore(channel, Snowflake.of(params[2]));
            else if (params[1].equalsIgnoreCase("after"))
                grabber.grabAfter(channel, Snowflake.of(params[2]));
        }

        return channel.createEmbed(ProgressEmbeds::savingLinksFinished).then();
    }

}
