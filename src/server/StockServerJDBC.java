package server;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Vector;



import shared.LastPapernameAndPrice;
import shared.PapernameAndPrice;
import shared.PriceAndVolume;
import shared.StockDate;
import shared.StockTime;

public class StockServerJDBC {

	private final static String userName = "halado_java_test";
	private final static String password = "just_testing";
	private final static String hostName = "nemgy.itk.ppke.hu";
	private final static int portNumber = 61000;
	private final static String database = "halado_java_test";

	private static Connection connect() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();

		connectionProps.put("user", userName);
		connectionProps.put("password", password);

		conn = DriverManager.getConnection("jdbc:mysql://" + hostName + ":" + portNumber + "/" + database, connectionProps);

		System.out.println("Connected to database");

		return conn;
	}

	private static void closeConnect(Connection conn) throws SQLException {

		if (conn != null)
			conn.close();

	}

	public static TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>> getDayTradeData(String paperName, StockDate date) {

		TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>> tradeDate = new TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>>();

		TreeMap<StockTime, PriceAndVolume> timeAndPrice;

		StockTime sTime = null;
		PriceAndVolume pV = null;

		Statement stmt = null;
		ResultSet result;
		Connection conn = null;
		try {
			conn = connect();
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e1) {

			e1.printStackTrace();
		}

		try {
			result = stmt.executeQuery("select * from RawStockData where paperName like '" + paperName + "' and tradeDate ='" + date + "' order by tradeDate asc, tradeTime asc");// '2008-02-02'
			// System.out.println("asdasdasd");
			PriceAndVolume pnv;
			SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
			SimpleDateFormat mounthFormat = new SimpleDateFormat("MM");
			SimpleDateFormat dayFormat = new SimpleDateFormat("dd");

			SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
			SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
			SimpleDateFormat secondFormat = new SimpleDateFormat("ss");
			int id;
			String data;
			Date sqldate;
			Time sqltime;
			double price;
			int volume;

			int year;
			int mounth;
			int day;

			int hour;
			int minute;
			int second;
			StockTime befortt=null;
			Double beforPrice= null;
			while (result.next()) {
				id = result.getInt("id");
				data = result.getString("paperName");
				sqldate = result.getDate("tradeDate");
				sqltime = result.getTime("tradeTime");
				price = result.getDouble("price");
				volume = result.getInt("volume");

				year = Integer.parseInt(yearFormat.format(sqldate));
				mounth = Integer.parseInt(mounthFormat.format(sqldate));
				day = Integer.parseInt(dayFormat.format(sqldate));

				hour = Integer.parseInt(hourFormat.format(sqltime));
				minute = Integer.parseInt(minuteFormat.format(sqltime));
				second = Integer.parseInt(secondFormat.format(sqltime));

				StockDate dd = new StockDate(year, mounth, day);
				StockTime tt = new StockTime(hour, minute, second);

				if (tradeDate.containsKey(dd)) {
					System.out.println("egy"+ " ");
					timeAndPrice = tradeDate.get(dd);
					if (beforPrice == price && befortt !=null) {
						System.out.println("egyezes"+ price);
						pnv = timeAndPrice.get(befortt);
						pnv.setVolume(pnv.getVolume() + volume);
						//beforPrice;
						//befortt = tt;

					} else {
						if (timeAndPrice.containsKey(tt)) {
							pnv = timeAndPrice.get(tt);
						} else {
							pnv = new PriceAndVolume();
							timeAndPrice.put(tt, pnv);
						}

						pnv.setPrice(price);
						pnv.setVolume(pnv.getVolume() + volume);
						
						beforPrice = price;
						befortt = tt;
						
						
					}
				} else {
					pnv = new PriceAndVolume(price, volume);

					timeAndPrice = new TreeMap<StockTime, PriceAndVolume>();
					timeAndPrice.put(tt, pnv);
					tradeDate.put(dd, timeAndPrice);
					System.out.println(tradeDate);
					beforPrice = price;
					befortt = tt;
				}

			}
			System.out.println("Az adatok feldolgozva");

			closeConnect(conn);

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return tradeDate;
	}

	public static TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>> getFromTimeTradeData(String paperName, StockDate date, StockTime time) {

		TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>> tradeDate = new TreeMap<StockDate, TreeMap<StockTime, PriceAndVolume>>();

		TreeMap<StockTime, PriceAndVolume> timeAndPrice;

		StockTime sTime = null;
		PriceAndVolume pV = null;

		Statement stmt = null;
		ResultSet result;
		Connection conn = null;
		try {
			conn = connect();
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e1) {

			e1.printStackTrace();
		}

		try {
			result = stmt.executeQuery("select * from RawStockData where paperName like '" + paperName + "' and tradeDate ='" + date + "' and tradeTime > '" + time+"'");// '2008-02-02'

			PriceAndVolume pnv;
			SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
			SimpleDateFormat mounthFormat = new SimpleDateFormat("MM");
			SimpleDateFormat dayFormat = new SimpleDateFormat("dd");

			SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
			SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
			SimpleDateFormat secondFormat = new SimpleDateFormat("ss");
			int id;
			String data;
			Date sqldate;
			Time sqltime;
			double price;
			int volume;

			int year;
			int mounth;
			int day;

			int hour;
			int minute;
			int second;

			while (result.next()) {
				id = result.getInt("id");
				data = result.getString("paperName");
				sqldate = result.getDate("tradeDate");
				sqltime = result.getTime("tradeTime");
				price = result.getDouble("price");
				volume = result.getInt("volume");

				year = Integer.parseInt(yearFormat.format(sqldate));
				mounth = Integer.parseInt(mounthFormat.format(sqldate));
				day = Integer.parseInt(dayFormat.format(sqldate));

				hour = Integer.parseInt(hourFormat.format(sqltime));
				minute = Integer.parseInt(minuteFormat.format(sqltime));
				second = Integer.parseInt(secondFormat.format(sqltime));

				StockDate dd = new StockDate(year, mounth, day);
				StockTime tt = new StockTime(hour, minute, second);

				if (tradeDate.containsKey(dd)) {
					timeAndPrice = tradeDate.get(dd);
					if (timeAndPrice.containsKey(tt)) {
						pnv = timeAndPrice.get(tt);
					} else {
						pnv = new PriceAndVolume();
						timeAndPrice.put(tt, pnv);
					}

					pnv.setPrice(price);
					pnv.setVolume(pnv.getVolume() + volume);

				} else {
					pnv = new PriceAndVolume(price, volume);

					timeAndPrice = new TreeMap<StockTime, PriceAndVolume>();
					timeAndPrice.put(tt, pnv);
					tradeDate.put(dd, timeAndPrice);
					System.out.println(tradeDate);
				}
				System.out.println(tradeDate.get(dd).size());

			}
			System.out.println("Az adatok feldolgozva");

			closeConnect(conn);

		} catch (SQLException e) {

			e.printStackTrace();
		}

		System.out.println("tradeDate");
		return tradeDate;

	}

	public static TreeMap<String, Integer> getLastPaperNamesAndPrice(Vector<String> favPaperNames) {

		TreeMap<String, Integer> papernameAndPrice = new TreeMap<String, Integer>();

		Statement stmt = null;
		ResultSet result;
		Connection conn = null;
		try {
			conn = connect();
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e1) {

			e1.printStackTrace();
		}
		for (Iterator<String> it = favPaperNames.iterator(); it.hasNext();) {
			String paperN = it.next();
			System.out.println(paperN);
			try {
				// SELECT paperName, price, tradeDate, tradeTime FROM
				// RawStockData Where paperName = "OTP" order by tradeDate desc,
				// tradeTime desc

				result = stmt.executeQuery("SELECT price from RawStockData where paperName = '" + paperN + "' order by tradeDate desc, tradeTime desc Limit 1");

				Integer price;
				result.next();

				price = result.getInt("price");

				papernameAndPrice.put(paperN, price);

				System.out.println("Az adatok feldolgozva");

			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		System.out.println(papernameAndPrice.toString());
		try {
			closeConnect(conn);
		} catch (SQLException e) {

			e.printStackTrace();
		}
		// System.out.println(paperNames.size()+"");

		return papernameAndPrice;

	}

	public static Vector<String> getAllPaperNames() {
		Vector<String> paperNames = new Vector<String>();

		Statement stmt = null;
		ResultSet result;
		Connection conn = null;
		try {
			conn = connect();
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e1) {

			e1.printStackTrace();
		}

		try {
			result = stmt.executeQuery("SELECT DISTINCT paperName from RawStockData");
			String name;
			while (result.next()) {

				name = result.getString("paperName");

				paperNames.add(name);

			}
			System.out.println("Az adatok feldolgozva");

			closeConnect(conn);

		} catch (SQLException e) {

			e.printStackTrace();
		}

		System.out.println(paperNames.size() + "");

		return paperNames;
	}

	/*
	 * public get //stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
	 * // ResultSet.CONCUR_UPDATABLE); double avg = 0.0; try { result = stmt
	 * .executeQuery(
	 * "select avg(price) from RawStockDataIndexed where paperName like '%GSPARK%' and tradeDate between '"
	 * +"responseTasks.getStartTime()"+"' and '"+"responseTasks.getEndTime()"+
	 * "' group by paperName");
	 * 
	 * result.next(); avg = result.getDouble(1);
	 * 
	 * 
	 * } catch (SQLException e) {
	 * 
	 * e.printStackTrace(); }
	 */

}
