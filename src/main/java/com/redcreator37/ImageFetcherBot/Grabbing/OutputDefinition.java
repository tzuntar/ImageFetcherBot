package com.redcreator37.ImageFetcherBot.Grabbing;

public class OutputDefinition {

    enum Type {
        LINK_FILE, FILE_DIRECTORY, CLOUD
    }

    OutputDefinition(String location, Type type) {
        this.location = location;
        this.type = type;
    }

    private final String location;

    private final Type type;

    public String getLocation() {
        return location;
    }

    public Type getType() {
        return type;
    }

}
