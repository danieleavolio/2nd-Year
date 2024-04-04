package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.evaluation.ClusteringEvaluator;
import org.apache.spark.ml.feature.OneHotEncoder;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.ml.tuning.CrossValidator;
import org.apache.spark.ml.tuning.CrossValidatorModel;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import data.Preparation;
import data.TaxiRide;
import util.Constant;

public class KMeansWithRegression {
	
	public static final String toIntSuffix="ToInt";
	public static final String toVecSuffix="ToVec";
	public static final String[] featuresCols = {Constant.DAY_OF_THE_WEEK+toVecSuffix,Constant.PERIOD+toVecSuffix,Constant.PASSENGER,Constant.DISTANCE};
	public static final String label = Constant.AMOUNT;
	public static final String featuresColumnName = "features";
	CrossValidator validator;
	
	ParamMap[] hyperParameter;
	ClusteringEvaluator evaluator;
	Pipeline estimator;
	
	StringIndexer textToInt;
	OneHotEncoder intToClassVector;
	VectorAssembler featureAssembler;
	KMeans kmeansAlg;
	
	CrossValidatorModel kmeansModel;
	
	LinearRegressionModel[] regressionByCluster;
	
	int k_clusters;
	public KMeansWithRegression() {
		// TODO Auto-generated constructor stub
		textToInt = new StringIndexer()
				.setInputCols(new String[] {Constant.PERIOD})
				.setOutputCols(new String[] {Constant.PERIOD+toIntSuffix});
		
		intToClassVector = new OneHotEncoder()
				.setInputCols(new String[]{Constant.DAY_OF_THE_WEEK,Constant.PERIOD+toIntSuffix})
				.setOutputCols(new String[]{Constant.DAY_OF_THE_WEEK+toVecSuffix,Constant.PERIOD+toVecSuffix});
		
		
		featureAssembler = new VectorAssembler().setInputCols(featuresCols).setOutputCol(featuresColumnName);
		
		kmeansAlg = new KMeans().setSeed(1L);
		
		estimator = new Pipeline().setStages(new PipelineStage[]{textToInt,intToClassVector,featureAssembler,kmeansAlg});
		evaluator = new ClusteringEvaluator();
		hyperParameter = new ParamGridBuilder().addGrid(kmeansAlg.k(), new int[] {4,8,12}).build();
		validator = new CrossValidator()
				.setEstimator(estimator)
				.setEvaluator(evaluator)
				.setEstimatorParamMaps(hyperParameter)
				.setNumFolds(5)
				.setSeed(1L);
		
	}
	
	public void fit(Dataset<TaxiRide> data) {
		
		kmeansModel = validator.fit(data);
		

		String prefix = String.valueOf(System.currentTimeMillis());
		try {
			kmeansModel.write().save(prefix+"CrossValidatorModel");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Unable to store kmeans");
		}
		ParamMap[] estimatorParamMaps = kmeansModel.getEstimatorParamMaps();
		
		double[] avgMetrics = kmeansModel.avgMetrics();
		for (int i = 0; i < avgMetrics.length; i++) {
			System.out.println(estimatorParamMaps[i]+" "+avgMetrics[i]);
		} 
		Dataset<Row> clusteredData = kmeansModel.transform(data);
		clusteredData.persist();
		k_clusters=(int) clusteredData.select("prediction").distinct().as(Encoders.INT()).count();
		System.out.println("Cluster number: "+k_clusters);
		
		//applying linear regression
		regressionByCluster=new LinearRegressionModel[k_clusters];
		for (int i = 0; i < regressionByCluster.length; i++) {
			Dataset<Row> trainRegression = clusteredData.where(clusteredData.col("prediction").equalTo(i)).drop("prediction");
			
			LinearRegression model=new LinearRegression().setFeaturesCol(featuresColumnName).setLabelCol(label);
			regressionByCluster[i]=model.fit(trainRegression);
			try {
				regressionByCluster[i].write().save(prefix+"-0"+i+"-RegressionModel");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("Unable to store linear regression");
			}
		}
		clusteredData.unpersist();
	}
	
	public List<Dataset<Row>> transform(Dataset<TaxiRide> data){
		
		List<Dataset<Row>> partial = new ArrayList<Dataset<Row>>();
		Dataset<Row> clustered = kmeansModel.transform(data);
		clustered.persist();
		for (int i = 0; i < regressionByCluster.length; i++) {
			Dataset<Row> clusterInstances = clustered.where(clustered.col("prediction").equalTo(i)).drop("prediction");
			if(!clusterInstances.isEmpty()) {
				Dataset<Row> predicted = regressionByCluster[i].transform(clusterInstances);
				partial.add(predicted);
			}
		}
		clustered.unpersist();
		return partial;
	}

}
