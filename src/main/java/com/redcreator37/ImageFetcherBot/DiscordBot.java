package com.redcreator37.ImageFetcherBot;

import com.redcreator37.ImageFetcherBot.Grabbing.CommandHandler;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * A Discord bot that fetches images / files from a specific server
 *
 * @author RedCreator37
 */
public class DiscordBot {

    /**
     * The prefix to look for when parsing messages into commands
     */
    public static final char cmdPrefix = '-';

    /**
     * The currently used {@link GatewayDiscordClient} object when
     * connecting to Discord's servers
     */
    private static GatewayDiscordClient client = null;

    /**
     * A {@link HashMap} holding all currently implemented commands
     */
    private static final Map<String, Command> commands = new HashMap<>();

    /**
     * Registers the bot commands
     */
    private static void setUpCommands() {
        commands.put("grablinks", event -> CommandHandler.grabLinks(event).then());
        commands.put("grabfiles", event -> CommandHandler.grabFiles(event).then());
    }

    /**
     * Initializes and hooks up the event handlers
     */
    private static void setUpEventDispatcher() {
        client.getEventDispatcher().on(MessageCreateEvent.class)
                .flatMap(e -> Mono.just(e.getMessage().getContent())
                        .flatMap(content -> Flux.fromIterable(commands.entrySet())
                                .filter(entry -> content.startsWith(cmdPrefix + entry.getKey()))
                                .flatMap(entry -> entry.getValue().execute(e)).next()))
                .subscribe();
    }

    /**
     * Starts up the bot, loads the local database and connects to the
     * Discord's API
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Provide the token!");
            System.exit(1);
        }
        setUpCommands();
        client = DiscordClientBuilder.create(args[0]).build().login().block();
        if (client == null) {
            System.err.println("Login failed.");
            System.exit(1);
        }
        setUpEventDispatcher();
        client.onDisconnect().block();
    }

}
