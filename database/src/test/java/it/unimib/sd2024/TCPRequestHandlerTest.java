package it.unimib.sd2024;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.Test;

public class TCPRequestHandlerTest {
    @Test
    public void emptyRequest() throws IOException{
		var server = new Socket("localhost", 6600);
		var writer = new PrintWriter(server.getOutputStream(), true);
		var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

		writer.println();
		var res = reader.readLine();

		assertEquals("GARBAGE_REQUEST", res);

		writer.close();
		reader.close();
		server.close();
    }

    @Test
    public void garbageRequest() throws IOException{
		var server = new Socket("localhost", 6600);
		var writer = new PrintWriter(server.getOutputStream(), true);
		var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

		writer.println("Hello, World!");
		var res = reader.readLine();

		assertEquals("GARBAGE_REQUEST", res);

		writer.close();
		reader.close();
		server.close();
    }
}
