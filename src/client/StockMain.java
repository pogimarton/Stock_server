package client;

import java.util.Iterator;
import java.util.SortedMap;
import shared.*;

public class StockMain {

	/**
	 * @param args
	 */

	public static void printMemoryState() {
		System.out.println("Total memory: " + Runtime.getRuntime().totalMemory());
		System.out.println("Free memory: " + Runtime.getRuntime().freeMemory());
		System.out.println("Used memory:" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
	}

	public static void main(String[] args) {

		printMemoryState();
		// StockManager.readStockDataFromTextFile("1012/101201.txt");
		SortedMap<StockTime, PriceAndVolume> otp101201 = StockManager.getDayTradesOfPaper("OTP", new StockDate(2010, 12, 01));

		for (Iterator<PriceAndVolume> it = otp101201.values().iterator(); it.hasNext();) {
			PriceAndVolume pnv = it.next();
			System.out.println(pnv);
		}

		/*
		 * for (Entry<StockTime, PriceAndVolume> entry : otp101201.entrySet()) {
		 * System.out.println("key is " + entry.getKey() + " and value is " +
		 * entry.getValue()); }
		 */

		// for (int i = 0; i < 4000000; i++)
		// tradeVector.add(new Trade ("test", 0, new Date (111, 8, 13, 9, 11,
		// 11), 100, 10));

		printMemoryState();

	}

}
