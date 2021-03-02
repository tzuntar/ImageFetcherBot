package com.redcreator37.ImageFetcherBot;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

/**
 * Represents a bot command
 */
public interface Command {

    /**
     * Executes the command triggered by this {@link MessageCreateEvent}
     *
     * @param event the preceding {@link MessageCreateEvent} which
     *              occurred when the message was sent
     */
    Mono<Void> execute(MessageCreateEvent event);

}

