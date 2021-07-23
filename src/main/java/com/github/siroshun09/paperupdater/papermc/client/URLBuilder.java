package com.github.siroshun09.paperupdater.papermc.client;

import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@SuppressWarnings("ClassCanBeRecord")
class URLBuilder {

    private final String current;

    static URLBuilder create(@NotNull String base) {
        return new URLBuilder(base);
    }

    private URLBuilder(@NotNull String current) {
        this.current = current;
    }

    URLBuilder replace(@NotNull String placeholder, @NotNull String replacement) {
        return new URLBuilder(current.replace(placeholder, replacement));
    }

    URI toURI() {
        return URI.create(current);
    }

    URL toURL() throws MalformedURLException {
        return new URL(current);
    }
}
