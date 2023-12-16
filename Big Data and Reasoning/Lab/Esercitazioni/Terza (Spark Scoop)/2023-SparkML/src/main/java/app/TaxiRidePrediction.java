package app;

import java.util.List;

import org.apache.spark.api.java.function.ReduceFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;

import data.Preparation;
import data.TaxiRide;
import model.KMeansWithRegression;

public class TaxiRidePrediction {

	public static void main(String[] args) {
		
		SparkSession spark = SparkSession.builder().master("local").appName("Fare Amount Prediction").getOrCreate();
//		SparkSession spark = SparkSession.builder().master("yarn").appName("Fare Amount Prediction").getOrCreate();
		spark.sparkContext().setLogLevel("ERROR");
		Dataset<Row> raw_data = spark.read().option("header", "true").csv("TaxiData/small_sample.csv");
		Dataset<Row> cleaned = Preparation.clean(raw_data);
		
		Dataset<TaxiRide> training = Preparation.transform(cleaned);
		Dataset<Row> raw_test = spark.read().option("header", "true").csv("TaxiData/test.csv");
		Dataset<Row> cleanedTest = Preparation.clean(raw_test);
		Dataset<TaxiRide> test = Preparation.transform(cleanedTest);
		
		training.show();
		test.show();
		
		KMeansWithRegression model = new KMeansWithRegression();
		model.fit(training);
		List<Dataset<Row>> result = model.transform(test);
		double sum = 0d;
		double count = 0d;
		for(Dataset<Row> data : result) {
			data.persist();
			data.select(functions.avg("prediction"),functions.stddev("prediction")).show();
			sum += data.select("prediction").as(Encoders.DOUBLE()).reduce((ReduceFunction<Double>) (f1,f2)->f1+f2);
			count += data.count();
			data.unpersist();
		}
		System.out.println("AVG prediction: "+sum/count);
	}
}
