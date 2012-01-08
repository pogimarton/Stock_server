package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class StockServer {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerSocket stockServerSocket;
		try {
			//TODO 61700-61800
			stockServerSocket = new ServerSocket(12000);
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		while (true) {

			while (true) {
				try {
					System.out.println("Waiting for connections...");
					Socket clientSocket = stockServerSocket.accept();
					System.out.println("Client connected from: "+ clientSocket.getInetAddress()+ clientSocket.getPort());
					StockServerClientHandler clientHandler = new StockServerClientHandler(
							clientSocket);
					new Thread(clientHandler).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
