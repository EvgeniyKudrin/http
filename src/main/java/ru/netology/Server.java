package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService service = Executors.newFixedThreadPool(64);
    static ConcurrentMap<String, HashMap<String, Handler>> handlers = new ConcurrentHashMap<>();
    final Handler handlerNotFound = (request, responseStream) -> {
        System.out.println("handler not found");
        responseStream.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        responseStream.flush();
    };
    final static Handler handlerBadRequest = (request, responseStream) -> {
        System.out.println("handler bad request");
        responseStream.write((
                "HTTP/1.1 400 Bad request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        responseStream.flush();
    };

    public Server() {
        System.out.println("Server started");
    }

    public void listen(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                service.submit(new connection(serverSocket.accept()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addHandler(String method, String path, ru.netology.Handler handler) {
        if (!handlers.containsKey(method)) {
            handlers.put(method, new HashMap<>());
        }
        handlers.get(method).put(path, handler);
    }

    private class connection implements Runnable {
        public Socket socket;

        public connection(Socket socket) {
            this.socket = socket;
        }

        private final HashMap<String, Handler> mapBadRequest = new HashMap<>();

        @Override
        public void run() {
            try (
                    final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    final var out = new BufferedOutputStream(socket.getOutputStream());
            ) {
                while (true) {
                    Request request = new Request(in.readLine());
                    mapBadRequest.put(request.getPath(), handlerBadRequest);
                    handlers.getOrDefault(request.getMethod(), mapBadRequest)
                            .getOrDefault(request.getPath(), handlerNotFound)
                            .handle(request, out);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
