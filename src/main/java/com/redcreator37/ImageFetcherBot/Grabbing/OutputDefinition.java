package com.redcreator37.ImageFetcherBot.Grabbing;

/**
 * Defines the storage location for retrieved items
 */
public class OutputDefinition {

    /**
     * All available storage location
     */
    enum Type {
        LINK_FILE, FILE_DIRECTORY, CLOUD
    }

    /**
     * Creates a new {@link OutputDefinition} instance
     *
     * @param location the storage location to use
     * @param type     the type of the storage location
     */
    OutputDefinition(String location, Type type) {
        this.location = location;
        this.type = type;
    }

    /**
     * The storage location to use
     */
    private final String location;

    /**
     * The type of the storage location
     */
    private final Type type;

    public String getLocation() {
        return location;
    }

    public Type getType() {
        return type;
    }

}
