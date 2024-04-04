package data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;

import util.Constant;

public class Preparation {

	public static Dataset<Row> clean(Dataset<Row> data) {
		// TODO Auto-generated method stub
		return data.filter((FilterFunction<Row>) r ->{
			try {
				if(r.anyNull())
					return false;
				float amount = 0f;
				boolean amountFound=true;
				
				try {
					amount = Float.parseFloat(r.getAs(Constant.AMOUNT));	
				}catch (IllegalArgumentException e) {
					// TODO: handle exception
					amountFound=false;
				}
				
				Double.parseDouble(r.getAs(Constant.P_LON));
				Double.parseDouble(r.getAs(Constant.P_LAT));
				Double.parseDouble(r.getAs(Constant.D_LON));
				Double.parseDouble(r.getAs(Constant.D_LAT));
				int passenger = Integer.parseInt(r.getAs(Constant.PASSENGER));
				String timestamp = r.getAs(Constant.TIMESTAMP);
				
				if(passenger > 5 || (amountFound && amount<= 0f))
					return false;
				new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(timestamp);
//				String[] dateAndTime = timestamp.split(" ");
//				if(dateAndTime.length!=3 || dateAndTime[0].split("-").length != 3 || dateAndTime[1].split(":").length != 3)
//					return false;
				
				return true;
			}catch (Exception e) {
				// TODO: handle exception
				return false;
			}
		});
	}
	
	public static Dataset<TaxiRide> transform(Dataset<Row> data) {
	
		return data.map((MapFunction<Row,TaxiRide>) r ->{
			float amount = 0f;
			try {
				amount = Float.parseFloat(r.getAs(Constant.AMOUNT));	
			}catch (IllegalArgumentException e) {
			}
			//input fields
			double p_lon = Double.parseDouble(r.getAs(Constant.P_LON));
			double p_lat = Double.parseDouble(r.getAs(Constant.P_LAT));
			double d_lon = Double.parseDouble(r.getAs(Constant.D_LON));
			double d_lat = Double.parseDouble(r.getAs(Constant.D_LAT));
			int passenger = Integer.parseInt(r.getAs(Constant.PASSENGER));
			String timestamp = r.getAs(Constant.TIMESTAMP);
			
			double distance = compute(p_lat, p_lon, d_lat, d_lon);
			String period = getPeriod(timestamp);
			int day = getDay(timestamp);
			
			return new TaxiRide(amount, period, day, distance, passenger);
		}, Encoders.bean(TaxiRide.class));
	}
	
	public static int getDay(String timestamp) throws ParseException {
		Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(timestamp);
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    return cal.get(Calendar.DAY_OF_WEEK);
	}
	private static String getPeriod(String timestamp) throws ParseException {
		// TODO Auto-generated method stub
		Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(timestamp);
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    int hour = cal.get(Calendar.HOUR_OF_DAY);
		if(hour<=10)
			return "morning";
		if(hour <= 14)
			return "mid-day";
		if(hour <= 18)
			return "afternoon";
		return "night";
	}
	public final static double AVERAGE_RADIUS_OF_EARTH = 6371;
	
	private static int compute(double userLat, double userLng, double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                        (Math.cos(Math.toRadians(userLat))) *
                        (Math.cos(Math.toRadians(venueLat))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH * c));

    }
	
	public static Dataset<Row> getClusterInstance(int cluster_id,String clusterColumn,Dataset<Row> data){
		return data.filter((FilterFunction<Row>) r ->{
			int predictedCluster = r.getAs(clusterColumn);
			return predictedCluster==cluster_id;
		});
	}

}
