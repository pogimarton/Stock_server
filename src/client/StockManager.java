package client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

import shared.*;

public class StockManager {
	//first key: paper name
	//second key: date
	//third key: time
	
	//  !! itt kell megcsinalni hogy ami helyileg megvan azt ne toltse le
	
	static final private HashMap<String, TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>>>	stocks	= new HashMap<String, TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>>> ();
	
	private StockManager() {
	}
	
	public static TreeMap<StockTime, PriceAndVolume> getDayTradesOfPaper (String paperName, StockDate date) {
		if (stocks.containsKey(paperName)) {
			TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>> paperTrades = stocks.get(paperName);
			if (paperTrades.containsKey(date))
				return paperTrades.get(date);
		}
		
		StockClientRequest<DayTradeRequestData> request = new StockClientRequest<DayTradeRequestData>(RequestType.GetDayTradeOfPaper, new DayTradeRequestData(paperName, date));
		StockServerResponse response = StockClient.processRequest(request);
		if (response.getResponseTo() == RequestType.GetDayTradeOfPaper) {
			Object responseObject = response.getResponseData();
			if (responseObject instanceof TreeMap) {
				TreeMap<StockTime, PriceAndVolume> responseData = (TreeMap<StockTime, PriceAndVolume>) responseObject;
				if (responseData != null && responseData.size() > 0) {
					TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>> actPaperTrades;
					if (stocks.containsKey(paperName)) {
						actPaperTrades = stocks.get(paperName);
					} else {
						actPaperTrades = new TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>> ();
						stocks.put(paperName, actPaperTrades);
					}
					
					if (!actPaperTrades.containsKey(date))
						actPaperTrades.put(date, responseData);
				}
				return responseData;
			}
		}
		

		
		return new TreeMap<StockTime, PriceAndVolume>();
	}
	


}
