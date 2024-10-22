package it.unimib.sd2024;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    public static final int PORT = 6600;

    public static void main(String[] args) throws IOException {
        var server = new ServerSocket(PORT);

        System.out.println("Database listening at localhost:" + PORT);

        try {
            while (true) {
                new TCPRequestHandler(server.accept()).start();
	    }
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            server.close();
        }
    }
}
