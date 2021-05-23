package com.redcreator37.ImageFetcherBot.Grabbing;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Objects;

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
        Tuple2<MessageChannel, String[]> optChannel = checkGrabberParams(event, true);
        if (optChannel == null) return Mono.empty();
        MessageChannel channel = optChannel.getT1();

        Message progressMsg = channel.createEmbed(spec ->
                ProgressEmbeds.progressEmbed(spec, 1, 1)).block();
        assert progressMsg != null;
        String folderName = Objects.requireNonNull(progressMsg.getGuild().block()).getName();
        Grabber grabber = new Grabber(new OutputDefinition(folderName,
                OutputDefinition.Type.FILE_DIRECTORY), progressMsg);
        grabFromParams(grabber, channel, optChannel.getT2());
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
        Tuple2<MessageChannel, String[]> optChannel = checkGrabberParams(event, false);
        if (optChannel == null) return Mono.empty();
        MessageChannel channel = optChannel.getT1();

        Message progressMsg = channel.createEmbed(spec ->
                ProgressEmbeds.progressEmbed(spec, 1, 1)).block();
        Grabber grabber = new Grabber(new OutputDefinition("output.txt",
                OutputDefinition.Type.LINK_FILE), progressMsg);
        grabFromParams(grabber, channel, optChannel.getT2());
        return channel.createEmbed(ProgressEmbeds::savingLinksFinished).then();
    }

    /**
     * Checks whether the grab command params are valid
     *
     * @param event         the {@link MessageCreateEvent} of the message
     * @param grabbingFiles {@code true} if grabbing files instead of
     *                      plain links
     * @return the associated message's {@link MessageChannel} and an
     * array of string parameters, passed to the command if the
     * syntax is correct. {@code null} is returned on incorrect syntax.
     */
    private static Tuple2<MessageChannel, String[]> checkGrabberParams(MessageCreateEvent event, boolean grabbingFiles) {
        MessageChannel channel = event.getMessage().getChannel().block();
        assert channel != null;
        String[] params = event.getMessage().getContent().split(" ");
        if (params.length > 1 && !params[1].matches("before|after")) {
            channel.createEmbed(spec ->
                    ProgressEmbeds.invalidSyntax(spec, grabbingFiles));
            return null;
        }
        return Tuples.of(channel, params);
    }

    /**
     * Parses the command parameters and runs the appropriate grabber
     *
     * @param grabber the {@link Grabber} with all options set
     * @param channel the {@link MessageChannel} where the command message
     *                has been posted
     * @param params  parameters to the command
     */
    private static void grabFromParams(Grabber grabber, MessageChannel channel, String[] params) {
        if (params.length < 2)   // no parameters, get everything
            grabber.grabBefore(channel, null);
        else if (params.length == 3)    // before / after ID
            if (params[1].equalsIgnoreCase("before"))
                grabber.grabBefore(channel, Snowflake.of(params[2]));
            else if (params[1].equalsIgnoreCase("after"))
                grabber.grabAfter(channel, Snowflake.of(params[2]));
    }

}
