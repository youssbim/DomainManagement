package it.unimib.sd2024;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.Test;

public class UserRegistrationTest {
	@Test
	public void missingParametersInUserRegisterRequest() throws IOException {
		var server = new Socket("localhost", 6600);
		var writer = new PrintWriter(server.getOutputStream(), true);
		var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

		writer.println("POST /users/register");
		var res = reader.readLine();

		assertEquals("GARBAGE_REQUEST InvalidNumberOfParameters", res);

		writer.close();
		reader.close();
		server.close();
	}

	@Test
	public void missingNameOrSurnameParameterInUserRegisterRequest() throws IOException {
		var server = new Socket("localhost", 6600);
		var writer = new PrintWriter(server.getOutputStream(), true);
		var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

		writer.println("POST /users/register Nome");
		var res = reader.readLine();

		assertEquals("GARBAGE_REQUEST InvalidNumberOfParameters", res);

		writer.close();
		reader.close();
		server.close();
	}

	@Test
	public void requestWithMoreThanOneLineMessage() throws IOException {
		var server = new Socket("localhost", 6600);
		var writer = new PrintWriter(server.getOutputStream(), true);
		var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

		writer.println("POST");
		writer.println("/users/register");
		writer.println("Nome");
		writer.println("Cognome");

		var res = reader.readLine();
		assertEquals("GARBAGE_REQUEST", res);

		writer.close();
		reader.close();
		server.close();
	}

	@Test
	public void validUserRegisterRequest() throws IOException {
		var server = new Socket("localhost", 6600);
		var writer = new PrintWriter(server.getOutputStream(), true);
		var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

		writer.println("POST /users/register leonardo bizzoni l.bizzoni@campus.unimib.it");
		var res = reader.readLine();

		assertEquals("OK", res);

		writer.close();
		reader.close();
		server.close();
	}

	@Test
	public void validUserRegisterRequestWithMultiplePrint() throws IOException {
		var server = new Socket("localhost", 6600);
		var writer = new PrintWriter(server.getOutputStream(), true);
		var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

		writer.print("POST ");
		writer.print("/users/register ");
		writer.print("gianluca ");
		writer.print("rota ");
		writer.println("g.rota35@campus.unimib.it");

		var res = reader.readLine();
		assertEquals("OK", res);

		writer.close();
		reader.close();
		server.close();
	}

	@Test
	public void multipleSimultaneousRegisterRequest() throws Exception {
		StringBuilder res1 = new StringBuilder();
		StringBuilder res2 = new StringBuilder();

		var runner1 = new Thread(new Runnable() {
			public void run() {
				try {
					var server = new Socket("localhost", 6600);
					var writer = new PrintWriter(server.getOutputStream(), true);
					var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

					writer.println("POST /users/register youssef bimezzagh y.bimezzagh@campus.unimib.it");
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

					writer.println("POST /users/register youssef bimezzagh y.bimezzagh@campus.unimib.it");
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
