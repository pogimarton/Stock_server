package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.TreeMap;
import java.util.Vector;


import shared.*;

public class StockServerClientHandler implements Runnable {

	Socket				clientSocket	= null;
	ObjectInputStream	ois				= null;
	ObjectOutputStream	oos				= null;

	public StockServerClientHandler (Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	@Override
	public void run() {
		try {
			ois = new ObjectInputStream (clientSocket.getInputStream());
			oos = new ObjectOutputStream (clientSocket.getOutputStream());
			
			StockClientRequest	request = (StockClientRequest) ois.readObject();
			
			Object requestObject = request.getRequestData();
			
			switch (request.getRequestType()) {
				case GetDayTradeOfPaper:
					
					if (requestObject instanceof DayTradeRequestData) {
						DayTradeRequestData requestData = (DayTradeRequestData)requestObject;
						String stockName =	requestData.getPaperName();
						StockDate date =	requestData.getDate();
						
						   
						oos.writeObject(new StockServerResponse<TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>>>(request.getRequestType(), readDayTradeDataFromSQLServer(stockName, date)));
						
						System.out.println("Adatok elkuldve");
						
					} else
					{
						oos.writeObject(new StockServerResponse<String>(request.getRequestType(), "Wrong request data!"));
						System.out.println("Adatok nem elkuldve");
					}
					break;
				case GetFromTimeTradeData:
					
					
					
					if (requestObject instanceof FromTimeTradeRequestData) {
						FromTimeTradeRequestData requestData = (FromTimeTradeRequestData)requestObject;
						String stockName =	requestData.getPaperName();
						StockDate date =	requestData.getDate();
						StockTime  time=	requestData.getTime();
						   
						oos.writeObject(new StockServerResponse<TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>>>(request.getRequestType(), readFromTimeTradeDataFromSQLServer(stockName, date, time)));
						System.out.println("Adatok elkuldve");
						
					} else
					{
						oos.writeObject(new StockServerResponse<String>(request.getRequestType(), "Wrong request data!"));
						System.out.println("Adatok nem elkuldve");
					}

					break;
					
					
				case GetPaperName:
					
											   
					oos.writeObject(new StockServerResponse<Vector<String>>(request.getRequestType(), readPaperNameFromSQLServer()));
						
					break;
				case GetLastPapernameAndPrice:
					
					
					if (requestObject instanceof LastPapernameAndPrice) {
						LastPapernameAndPrice requestData = (LastPapernameAndPrice)requestObject;
						Vector<String> favPaperNames =	requestData.getFavPaperNames();
						
						   
						oos.writeObject(new StockServerResponse<TreeMap<String, Integer>>(request.getRequestType(), readLastPapernameAndPriceFromSQLServer(favPaperNames)));
						System.out.println("Adatok elkuldve");
						
					} else
					{
						oos.writeObject(new StockServerResponse<String>(request.getRequestType(), "Wrong request data!"));
						System.out.println("Adatok nem elkuldve");
					}
					
					
   
						
						
									
					break;
				
				
				default:
					oos.writeObject(new StockServerResponse<String>(request.getRequestType(), "Unknown client request!"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				ois.close ();
				oos.close ();
				clientSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>> readDayTradeDataFromSQLServer (String stockName, StockDate date) {
		
		
		TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>> actDayTrades = new TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>> ();
		
		actDayTrades=StockServerJDBC.getDayTradeData(stockName, date);
			
		return actDayTrades;
		
	}
	
	private TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>> readFromTimeTradeDataFromSQLServer(
			String stockName, StockDate date, StockTime time) {
		
		
		TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>> actTimeTrades ;//= new TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>>();
		
		
		actTimeTrades=StockServerJDBC.getFromTimeTradeData(stockName, date, time);
		//System.out.println(actTimeTrades);
		return actTimeTrades;
	}


	
	
	
	
	private TreeMap<String, Integer> readLastPapernameAndPriceFromSQLServer(Vector<String> favPaperNames) {
		
		
		TreeMap<String, Integer> lastData = new TreeMap<String, Integer>();
		
		lastData=StockServerJDBC.getLastPaperNamesAndPrice(favPaperNames);
		
		return lastData;
	}

	private Vector<String> readPaperNameFromSQLServer() {
		
		Vector<String> paperNames=new Vector<String>();
		
		paperNames= StockServerJDBC.getAllPaperNames();
		
		return paperNames;
	}


	
	
	public static TreeMap<StockTime, PriceAndVolume> readDayTradeDataFromTextFile (String stockName, StockDate date) {
		
		
		TreeMap<StockTime, PriceAndVolume> actDayTrades = new TreeMap<StockTime, PriceAndVolume> ();
		
		
		String year = "" + date.getYear();
		year = year.substring(2);
		
		String month = "";
		if (date.getMonth() < 10)
			month += "0";
		month += (date.getMonth());
		
		String day = "";
		if (date.getDay() < 10)
			day += "0";
		day += date.getDay();
		
		String fileName = year + month + "/" + year + month + day + ".txt";


		BufferedReader input = null;
		try {
			input =  new BufferedReader(new FileReader(fileName));
			
			String line = null;
			
			input.readLine();
			
			while ((line = input.readLine()) != null) {
				String[] tradeValues = line.split(",");
				
				String	actStockName	= tradeValues[0];
				
				if (!actStockName.equals (stockName))
					continue;
				
				String	time		= tradeValues[3];
				
				int		hours		= Integer.parseInt (time.substring(0, 2));
				int		minutes		= Integer.parseInt (time.substring(2, 4));
				int		seconds		= Integer.parseInt (time.substring(4, time.length()));
				
				StockTime stockTime = new StockTime (hours, minutes, seconds);
				PriceAndVolume pnv;
				if (actDayTrades.containsKey(stockTime)) {
					pnv = actDayTrades.get(stockTime);
				} else {
					pnv = new PriceAndVolume ();
					actDayTrades.put(stockTime, pnv);
				}
				
				double	Price		= Double.parseDouble(tradeValues[4]);
				int		volume		= Integer.parseInt(tradeValues[5]);
				
				pnv.setPrice(Price);
				pnv.setVolume(pnv.getVolume() + volume);
			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
		}
		return actDayTrades;
	}

}
