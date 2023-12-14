# Codice del prof

Problema delle canzoni (pdf tra poco)

## Problem 1


```java

// Nel Main


SparkSession session = SparkSession.builder().master(Main.MASTER).appName("Songs stats").getOrCreate();
session.sparkContext().setLogLevel("ERROR");

Dataset<Row> dataset = session.read().option("inferSchema", true).option("header", true).csv("path/to/file.csv");

dataset.printSchema();


// Find the most popular song

private static void query1(Dataset<Row> dataset){

    // Metodo 1
    String mostFrequent = dataset.reduce(new ReduceFunction<Row>(){
        @Override
        public Row call(Row row1, Row row2) throws Exception{
            int p1 = v1.getAs("popularity");
            int p2 = v2.getAs("popularity");

            return p1 >= p2 ? v1 : v2;
    }
}).getAs("title");i8

System.out.println("Most popular song: " + mostFrequent);

// Metodo 2
Dataset<String> limit = dataset.orderBy(dataset.col("popularity").desc()).limit(1).select("title").as(Encoders.STRING());
limit.show();


// Metodo 3
Dataset<row> selectMax = dataset.select(functions.max("popularity").as("maxPopularity"));
Dataset<Row> mostPopularSongs = dataset.join(selectMax,dataset.col("popularity").equalTo(selectMax.col("maxPopularity")));

mostPopularSongs.show();
 }
