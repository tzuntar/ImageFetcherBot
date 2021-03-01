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
import java.util.List;
import java.util.Objects;

public class Grabber {

    Grabber(OutputDefinition output, Message progressMsg) {
        this.output = output;
        this.progressMsg = progressMsg;
    }

    private final OutputDefinition output;

    private final Message progressMsg;

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
        if (!Downloader.makeDirIfNotExists(new File("retrieved")))
            return;
        List<Attachment> attachments = grabAttachments(channel, before).collectList().block();
        for (int i = 0; i < Objects.requireNonNull(attachments).size(); i++) {
            reportProgress(i + 1, attachments.size()).block();
            Downloader.downloadAttachment(attachments.get(i));
        }
    }

    private Mono<Void> reportProgress(int progress, int goal) {
        progressMsg.getEmbeds().clear();
        return progressMsg.getChannel().flatMap(messageChannel ->
                messageChannel.createEmbed(spec -> ProgressEmbeds
                        .progressEmbed(spec, progress, goal))).then();
    }

}
