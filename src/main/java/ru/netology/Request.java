package ru.netology;

import java.io.IOException;

public class Request {
    private String method;
    private String path;
    private String ver;

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
