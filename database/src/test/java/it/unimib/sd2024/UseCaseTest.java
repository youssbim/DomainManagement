package it.unimib.sd2024;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.Test;

public class UseCaseTest {
	String name = "flavio";
	String surname = "de_paoli";
	String email = "flavio.de_paoli@campus.unimib.it";
	String domain = "unimib.it";
	double amount = 10.1;
	int years = 3;

	@Test
	public void registerUserDomainAndCheckOrders() throws Exception {
		String response = "";

		response = send_request("POST /users/register " + name + " " + surname + " " + email);
		assertEquals("OK", response);

		response = send_request("POST /orders " + email + " " + domain + " register " + amount + " " + years);
		assertEquals("OK", response);

		response = send_request("GET /orders/done_by " + email);
		assertFalse(response.startsWith("GARBAGE_REQUEST"));
	}

	private String send_request(String req) throws Exception {
		var server = new Socket("localhost", 6600);
		var writer = new PrintWriter(server.getOutputStream(), true);
		var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

		writer.println(req);
		var response = reader.readLine();

		writer.close();
		reader.close();
		server.close();

		return response;
	}
}
