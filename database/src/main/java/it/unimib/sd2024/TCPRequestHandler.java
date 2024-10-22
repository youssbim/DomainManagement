package it.unimib.sd2024;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import it.unimib.sd2024.Database.DBError;
import it.unimib.sd2024.Database.DBErrorVariant;
import it.unimib.sd2024.Database.DatabaseHandler;
import it.unimib.sd2024.Database.Model.OrderType;

public class TCPRequestHandler extends Thread {
	private Socket client;

	private PrintWriter writer;
	private BufferedReader reader;

	public TCPRequestHandler(Socket client) {
		this.client = client;
	}

	public void run() {
		try {
			this.writer = new PrintWriter(client.getOutputStream(), true);
			this.reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

			var input_parts = reader.readLine().trim().split(" ");

			switch (input_parts[0]) {
				case "GET": {
					this.get_handler(input_parts);
				} break;
				case "POST": {
					this.post_handler(input_parts);
				} break;
				default: {
					this.garbage_request();
				} break;
			}

			reader.close();
			writer.close();
			client.close();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	private void get_handler(String[] input_parts) {
		try {
			switch (input_parts[1]) {
				case "/domains": {
					this.get_domain(input_parts);
				} break;
				case "/domains/all": {
					this.get_all_domains(input_parts);
				} break;
				case "/orders/done_by": {
					this.get_orders_done_by(input_parts);
				} break;
				case "/userinfo": {
					this.get_user(input_parts);
				} break;
				default: {
					this.garbage_request();
				} break;
			}
		} catch (Exception e) {
			this.garbage_request();
		}
	}

	private void get_user(String[] input_parts) {
		try {
			this.reply(DatabaseHandler.get_instance().get_user_info(input_parts[2]));
		} catch (DBError err) {
			this.garbage_request(err);
		} catch (IndexOutOfBoundsException e) {
			this.garbage_request(new DBError(DBErrorVariant.InvalidNumberOfParameters));
		}
	}

	private void get_all_domains(String[] input_parts) {
		try {
			this.reply(DatabaseHandler.get_instance().get_domains_owned_by(input_parts[2]));
		} catch (DBError err) {
			this.garbage_request(err);
		} catch(IndexOutOfBoundsException e) {
			this.garbage_request(new DBError(DBErrorVariant.InvalidNumberOfParameters));
		}
	}

	private void get_orders_done_by(String[] input_parts) {
		try {
			this.reply(DatabaseHandler.get_instance().get_orders_done_by(input_parts[2]));
		} catch(DBError err) {
			this.garbage_request(err);
		} catch(IndexOutOfBoundsException e) {
			this.garbage_request(new DBError(DBErrorVariant.InvalidNumberOfParameters));
		}
	}

	private void get_domain(String[] input_parts) {
		try {
			this.reply(DatabaseHandler.get_instance().get_domain(input_parts[2]));
		} catch(IndexOutOfBoundsException e) {
			this.garbage_request(new DBError(DBErrorVariant.InvalidNumberOfParameters));
		}
	}

	private void post_handler(String[] input_parts) {
		try {
			switch (input_parts[1]) {
				case "/users/register": {
					this.add_user(input_parts);
				} break;
				case "/orders": {
					this.add_order(input_parts);
				} break;
				default: {
					this.garbage_request();
				} break;
			}
		} catch (Exception e) {
			this.garbage_request();
		}
	}

	private void add_user(String[] input_parts) {
		try {
			DatabaseHandler.get_instance().try_add_user(input_parts[2], input_parts[3], input_parts[4]);
			this.confirm();
		} catch(DBError err) {
			this.garbage_request(err);
		} catch(IndexOutOfBoundsException e) {
			this.garbage_request(new DBError(DBErrorVariant.InvalidNumberOfParameters));
		}
	}

	private void add_order(String[] input_parts) {
		try {
			OrderType order_type;

			switch (input_parts[4].trim()) {
				case "renewal": {
					order_type = OrderType.Renewal;
				} break;
				case "register": {
					order_type = OrderType.NewRegistration;
				} break;
				default: {
					throw new DBError(DBErrorVariant.InvalidOrderType);
				}
			}

			input_parts[5] = input_parts[5].replaceAll(",", ".");
			var amount = Double.parseDouble(input_parts[5]);
			var rent_for = Integer.parseInt(input_parts[6]);

			DatabaseHandler.get_instance().try_add_order(input_parts[2], input_parts[3], order_type, amount, rent_for);
			this.confirm();
		} catch(DBError err) {
			this.garbage_request(err);
		} catch(IndexOutOfBoundsException e) {
			this.garbage_request(new DBError(DBErrorVariant.InvalidNumberOfParameters));
		}
	}

	private void confirm() {
		this.writer.println("OK");
	}

	private void reply(String data) {
		this.writer.println(data);
	}

	private void garbage_request() {
		this.writer.println("GARBAGE_REQUEST");
	}

	private void garbage_request(DBError reason) {
		this.writer.println("GARBAGE_REQUEST " + reason);
	}
}
