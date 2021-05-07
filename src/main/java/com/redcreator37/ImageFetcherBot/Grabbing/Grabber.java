package com.redcreator37.ImageFetcherBot.Grabbing;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Grabs items from message channels
 */
public class Grabber {

    /**
     * Defines where the retrieved content will be stored
     */
    private final OutputDefinition output;
    /**
     * The message object used for progress reporting
     */
    private final Message progressMsg;

    /**
     * Constructs a new {@link Grabber} instance
     *
     * @param output      the {@link OutputDefinition} which defines where the
     *                    content will be stored
     * @param progressMsg the {@link Message} which will be used for
     *                    reporting retrieval progress
     */
    Grabber(OutputDefinition output, Message progressMsg) {
        this.output = output;
        this.progressMsg = progressMsg;
    }

    /**
     * Returns all attachments in this {@link MessageChannel} for all
     * messages before this ID
     *
     * @param channel the {@link MessageChannel} to get the attachments from
     * @param before  the {@link Snowflake} ID before which the messages
     *                will be downloaded. Set to {@code null} to attempt
     *                to download all items
     * @return a {@link Flux} of all attachments
     */
    private Flux<Attachment> grabAttachmentsBefore(MessageChannel channel, Snowflake before) {
        if (before == null) before = Snowflake.of(Instant.now());
        return channel.getMessagesBefore(before)
                .flatMap(message -> Flux.fromIterable(message.getAttachments()));
    }

    /**
     * Returns all attachments in this {@link MessageChannel} for all messages
     * after this ID
     *
     * @param channel the {@link MessageChannel} to get the attachments from
     * @param after   the {@link Snowflake} ID after which the messages
     *                will be downloaded. Set to {@code null} to attempt
     *                to download all items
     * @return a {@link Flux} of all attachments
     */
    private Flux<Attachment> grabAttachmentsAfter(MessageChannel channel, Snowflake after) {
        if (after == null) after = Snowflake.of(Instant.now());
        return channel.getMessagesAfter(after)
                .flatMap(message -> Flux.fromIterable(message.getAttachments()));
    }

    /**
     * Runs the grabber for all messages before this ID
     *
     * @param channel the {@link MessageChannel} on which to run the
     *                grabber
     * @param before  the {@link Snowflake} ID before which the messages
     *                will be downloaded. Set to {@code null} to attempt
     *                to download all items
     */
    void grabBefore(MessageChannel channel, Snowflake before) {
        if (output.getType() == OutputDefinition.Type.LINK_FILE)
            saveUrls(grabAttachmentsBefore(channel, before));
        else downloadFilesBefore(channel, before);
    }

    /**
     * Runs the grabber for all messages after this ID
     *
     * @param channel the {@link MessageChannel} on which to run the
     *                grabber
     * @param after   the {@link Snowflake} ID after which the messages
     *                will be downloaded. Set to {@code null} to attempt
     *                to download all items
     */
    void grabAfter(MessageChannel channel, Snowflake after) {
        if (output.getType() == OutputDefinition.Type.LINK_FILE)
            saveUrls(grabAttachmentsAfter(channel, after));
        else downloadFilesAfter(channel, after);
    }

    /**
     * Saves URLs of each {@link Attachment}
     *
     * @param attachments the {@link List<Attachment>} with attachments
     */
    private void saveUrls(Flux<Attachment> attachments) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(output.getLocation()))) {
            for (Attachment a : attachments.toIterable())
                writer.write(a.getUrl() + "\n");
        } catch (IOException e) {
            System.err.println("Saving data failed: " + e.getMessage());
        }
    }

    /**
     * Downloads all images, videos and other files for all messages
     * before this ID
     *
     * @param channel the {@link MessageChannel} from which to download
     *                the attachments
     * @param before  the {@link Snowflake} ID before which the messages
     *                will be downloaded. Set to {@code null} to attempt
     *                to download all items
     */
    private void downloadFilesBefore(MessageChannel channel, Snowflake before) {
        if (!Downloader.makeDirIfNotExists(new File("retrieved")))
            return;
        List<Attachment> attachments = grabAttachmentsBefore(channel, before).collectList().block();
        for (int i = 1; i <= Objects.requireNonNull(attachments).size(); i++) {
            if (i % 20 == 0)    // only create a new embed for every 20 items
                reportProgress(i + 1, attachments.size()).block();
            Downloader.downloadAttachment(attachments.get(i));
        }
    }

    /**
     * Downloads all images, videos and other files for all messages
     * after this ID
     *
     * @param channel the {@link MessageChannel} from which to download
     *                the attachments
     * @param after   the {@link Snowflake} ID after which the messages
     *                will be downloaded. Set to {@code null} to attempt
     *                to download all items
     */
    private void downloadFilesAfter(MessageChannel channel, Snowflake after) {
        if (!Downloader.makeDirIfNotExists(new File("retrieved")))
            return;
        List<Attachment> attachments = grabAttachmentsAfter(channel, after).collectList().block();
        for (int i = 1; i <= Objects.requireNonNull(attachments).size(); i++) {
            if (i % 20 == 0)    // only create a new embed for every 20 items
                reportProgress(i + 1, attachments.size()).block();
            Downloader.downloadAttachment(attachments.get(i));
        }
    }

    /**
     * Updates the progress message
     *
     * @param progress number of already retrieved items
     * @param goal     number of all items
     * @return an empty {@link Mono}
     */
    private Mono<Void> reportProgress(int progress, int goal) {
        progressMsg.getEmbeds().clear();
        return progressMsg.getChannel().flatMap(messageChannel ->
                messageChannel.createEmbed(spec -> ProgressEmbeds
                        .progressEmbed(spec, progress, goal))).then();
    }

}
