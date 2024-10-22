package it.unimib.sd2024;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.Test;

public class DomainRegistrationTest {
	@Test
	public void missingParametersInDomainRegisterRequest() throws IOException {
		var server = new Socket("localhost", 6600);
		var writer = new PrintWriter(server.getOutputStream(), true);
		var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

		writer.println("POST /orders");
		var res = reader.readLine();

		assertEquals("GARBAGE_REQUEST InvalidNumberOfParameters", res);

		writer.close();
		reader.close();
		server.close();
	}

	@Test
	public void missingSomeParameterInDomainRegisterRequest() throws IOException {
		var server = new Socket("localhost", 6600);
		var writer = new PrintWriter(server.getOutputStream(), true);
		var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

		writer.println("POST /orders email domain_name 42.42");
		var res = reader.readLine();

		assertEquals("GARBAGE_REQUEST InvalidOrderType", res);

		writer.close();
		reader.close();
		server.close();
	}

	@Test
	public void validDomainRegisterRequest() throws IOException {
		var server = new Socket("localhost", 6600);
		var writer = new PrintWriter(server.getOutputStream(), true);
		var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

		writer.println("POST /orders l.bizzoni@campus.unimib.it leonardobizzoni.com register 42.42 10");
		var res = reader.readLine();

		assertEquals("OK", res);

		writer.close();
		reader.close();
		server.close();
	}

	@Test
	public void validDomainRegisterRequestWithMultiplePrint() throws IOException {
		var server = new Socket("localhost", 6600);
		var writer = new PrintWriter(server.getOutputStream(), true);
		var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

		writer.print("POST ");
		writer.print("/orders ");
		writer.print("g.rota35@campus.unimib.it ");
		writer.print("gianlucarota.com ");
		writer.print("register ");
		writer.print("22.1 ");
		writer.println("2");

		var res = reader.readLine();
		assertEquals("OK", res);

		writer.close();
		reader.close();
		server.close();
	}

	@Test
	public void DomainRenewalBeforeRegistrationRequest() throws IOException {
		var server = new Socket("localhost", 6600);
		var writer = new PrintWriter(server.getOutputStream(), true);
		var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

		writer.println("POST /orders y.bimezzagh@campus.unimib.it youssefbimezzagh.com renewal 10.42 7");
		var res = reader.readLine();

		assertEquals("GARBAGE_REQUEST DomainNotRegistered", res);

		writer.close();
		reader.close();
		server.close();
	}

	@Test
	public void DomainRenewalDoneByDifferentUserRequest() throws IOException {
		var server = new Socket("localhost", 6600);
		var writer = new PrintWriter(server.getOutputStream(), true);
		var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

		writer.println("POST /orders y.bimezzagh@campus.unimib.it leonardobizzoni.com renewal 10.42 7");
		var res = reader.readLine();

		assertEquals("GARBAGE_REQUEST DomainOwnedByDifferentUser", res);

		writer.close();
		reader.close();
		server.close();
	}

	@Test
	public void multipleSimultaneousDomainRegisterRequest() throws Exception {
		StringBuilder res1 = new StringBuilder();
		StringBuilder res2 = new StringBuilder();

		var runner1 = new Thread(new Runnable() {
			public void run() {
				try {
					var server = new Socket("localhost", 6600);
					var writer = new PrintWriter(server.getOutputStream(), true);
					var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

					writer.println("POST /orders y.bimezzagh@campus.unimib.it sistemidistribuiti.it register 10.42 7");
					res1.append(reader.readLine());

					writer.close();
					reader.close();
					server.close();
				} catch (Exception e) {
				}
			}
		});
		var runner2 = new Thread(new Runnable() {
			public void run() {
				try {
					var server = new Socket("localhost", 6600);
					var writer = new PrintWriter(server.getOutputStream(), true);
					var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

					writer.println("POST /orders g.rota35@campus.unimib.it sistemidistribuiti.it register 10.42 3");
					res2.append(reader.readLine());

					writer.close();
					reader.close();
					server.close();
				} catch (Exception e) {
				}
			}
		});

		runner1.start();
		runner2.start();

		runner1.join();
		runner2.join();

		assertFalse(res1.toString().equals(res2.toString()));
	}
}
