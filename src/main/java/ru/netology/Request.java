package ru.netology;

import java.io.IOException;

public class Request {
    private final String method;
    private final String path;
    private final String ver;

    public Request(String requestLine) throws IOException {
        final var parts = requestLine.split(" ");
        method = parts[0];
        path = parts[1];
        ver = parts[2];
    }

    public String getMethod() {
        return method;
    }
    public String getPath() {
        return path;
    }

    public String getVer() {
        return ver;
    }
}
