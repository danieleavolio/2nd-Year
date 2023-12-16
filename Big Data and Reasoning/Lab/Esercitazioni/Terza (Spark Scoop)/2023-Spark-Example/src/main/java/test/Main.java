package test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.api.java.function.ReduceFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;

import scala.collection.immutable.ArraySeq;
import scala.collection.immutable.Seq;

public class Main {

	private static String master = "yarn";

	public static void main(String[] args) {
		// if(args[0].toLowerCase().equals("p1"))
		// problem1();
		// if(args[0].toLowerCase().equals("p2"))
		problem2();
		// if(args[0].toLowerCase().equals("p3"))
		// problem3();

	}

	private static void problem3() {
		// TODO Auto-generated method stub
		Dataset<Row> wordcount = wordcount();
		wordcount.where(wordcount.col("count").$greater(1)).show();
		wordcount.where(wordcount.col("value").equalTo("spark")).show();
	}

	private static void problem2() {
		SparkSession sessionWithHive = null;
		if (Main.master.equals("local"))
			sessionWithHive = SparkSession.builder().master(Main.master).enableHiveSupport()
					.config("spark.sql.warehouse.dir", "hive_local").appName("student app").getOrCreate();
		else
			sessionWithHive = SparkSession.builder().master(Main.master).enableHiveSupport().appName("student app")
					.getOrCreate();

		createAndPopulateTables(sessionWithHive);

		Dataset<Row> exams = sessionWithHive
				.sql("select * from exam as e inner join student as s on e.student_id = s.id_");

		computeExamAttempByYear(exams);
		Dataset<Exam> reshapedExams = reshapeExams(exams);
		computeFailedSuccessAttemptByYear(reshapedExams);
	}

	private static void problem1() {
		// TODO Auto-generated method stub
		// title popularity #streams author album year #sold copies
		SparkSession session = SparkSession.builder().master(Main.master).appName("songs stats").getOrCreate();
		session.sparkContext().setLogLevel("error");
		Dataset<Row> dataset = session.read().schema("title string, popularity integer, streams integer, author string, album string, year integer, sold_copies integer").csv("data/songs.csv");
		// Dataset<Row> dataset = session.read().option("inferSchema", "true").csv("data/songs.csv");
		// Dataset<Row> dataset = session.read().option("header",
		// "true").option("inferSchema","true").csv("data/songsHeader.csv");
		// Dataset<Row> dataset = session.read().option("header",
		// "true").csv("data/songsHeader.csv");
		dataset.printSchema();

		query1(dataset);
		query2(dataset);
		query3(dataset);
	}

	private static void computeFailedSuccessAttemptByYear(Dataset<Exam> exams) {
		exams.groupBy("exam_year", "student.degree", "result").count().show();
	}

	private static Dataset<Exam> reshapeExams(Dataset<Row> exams) {
		Dataset<Exam> reshapedExams = exams.map(new MapFunction<Row, Exam>() {

			@Override
			public Exam call(Row value) throws Exception {
				// TODO Auto-generated method stub
				Student stud = new Student();
				stud.setDegree(value.getAs("degree"));
				stud.setYear(value.getAs("academic_year"));
				stud.setStudent_id(value.getAs("student_id"));
				int score = value.getAs("score");
				Date date = new SimpleDateFormat("dd/MM/yyyy").parse(value.getAs("exam_day"));
				Calendar instance = Calendar.getInstance();
				instance.setTime(date);
				return new Exam(stud, value.getAs("course_id"), instance.get(Calendar.YEAR), score >= 18);
			}

		}, Encoders.bean(Exam.class));
		return reshapedExams;
	}

	private static void computeExamAttempByYear(Dataset<Row> exams) {
			// TODO Auto-generated method stub
			Dataset<Attempt> attempts = exams.map(new MapFunction<Row, Attempt>() {

				@Override
				public Attempt call(Row value) throws Exception {
					// TODO Auto-generated method stub
					Date date = new SimpleDateFormat("dd/MM/yyyy").parse(value.getAs("exam_day"));
					Calendar instance = Calendar.getInstance();
					instance.setTime(date);
					String degree = value.getAs("degree");
					return new Attempt(instance.get(Calendar.YEAR), degree);
				}

			}, Encoders.bean(Attempt.class));

			attempts.groupBy("year", "degree").count().show();
		}

	private static void createAndPopulateTables(SparkSession sessionWithHive) {
		// TODO Auto-generated method stub
		Dataset<Row> courses = sessionWithHive.read().schema("id_ string, name string, cfu integer")
				.csv("data/course.csv");
		Dataset<Row> students = sessionWithHive.read().schema("id_ string, academic_year integer, degree string")
				.csv("data/student.csv");
		Dataset<Row> exams = sessionWithHive.read()
				.schema("student_id string, course_id string, exam_day string, score integer").csv("data/exam.csv");

		courses.write().saveAsTable("course");
		students.write().saveAsTable("student");
		exams.write().saveAsTable("exam");

	}

	private static void query3(Dataset<Row> dataset) {
		// TODO Auto-generated method stub
		Dataset<Row> stats = dataset.groupBy("album").agg(functions.avg("streams"), functions.count("title"),
				functions.stddev("streams"));
		stats.show();

	}

	private static void query2(Dataset<Row> dataset) {
		// TODO Auto-generated method stub
		Integer sum = dataset.filter(dataset.col("author").equalTo("author1")).select("sold_copies").as(Encoders.INT())
				.reduce(new ReduceFunction<Integer>() {
					@Override
					public Integer call(Integer v1, Integer v2) throws Exception {
						// TODO Auto-generated method stub
						return v1 + v2;
					}
				});
		System.out.println("-------- Author 1 sold " + sum + " copies --------");
	}

	private static void query1(Dataset<Row> dataset) {
		// TODO Auto-generated method stub
		// ------------------------------ Method 1 ------------------------------
		String mostFrequent = dataset.reduce(new ReduceFunction<Row>() {

			@Override
			public Row call(Row v1, Row v2) throws Exception {
				// TODO Auto-generated method stub
				int p1 = v1.getAs("popularity");
				int p2 = v2.getAs("popularity");
				return p1 >= p2 ? v1 : v2;
			}
		}).getAs("title");
		System.out.println("Most frequent song: " + mostFrequent);

		// ------------------------------ Method 2 ------------------------------
		Dataset<String> limit = dataset.orderBy(dataset.col("popularity").desc()).limit(1).select("title")
				.as(Encoders.STRING());
		limit.show();

		// ------------------------------ Method 3 ------------------------------
		Dataset<Row> selectMax = dataset.select(functions.max("popularity").as("maxPopularity"));
		Dataset<Row> mostPopularSongs = dataset.join(selectMax,
				dataset.col("popularity").equalTo(selectMax.col("maxPopularity")));
		mostPopularSongs.show();

	}

	private static Dataset<Row> wordcount() {
		// TODO Auto-generated method stub
		SparkSession session = SparkSession.builder().master(Main.master).appName("read file app").getOrCreate();
		Dataset<Row> dataset = session.read().text("sample-text-file.txt");
		dataset.show(2);

		Dataset<Row> dataset2 = dataset.flatMap(new FlatMapFunction<Row, String>() {

			@Override
			public Iterator<String> call(Row t) throws Exception {
				// TODO Auto-generated method stub
				//Prende la singola riga
				String line = t.getAs("value");

				List<String> res = new ArrayList<String>();
				List<String> punctuation = Arrays.asList(".", ",", ":", ";", "?", "!");
				
				//Per ogni parola della riga
				for (String word : Arrays.asList(line.split(" "))) {
					String currentWord = word;
					
					// Se la parola contiene punteggiatura la rimuove
					for (String character : punctuation) {
						currentWord = currentWord.replace(character, "");
					}
					// Lo aggiunge alla lista di parole
					res.add(currentWord.toLowerCase());
				}
				//CAZI SUOI
				return res.iterator();
			}
		}, Encoders.STRING())
				// Filtra le parole vuote e quelle che non sono rilevanti
				//Ritorna un dataset di String
				.filter(new FilterFunction<String>() {
					@Override
					public boolean call(String value) throws Exception {
						// TODO Auto-generated method stub
						return !value.isEmpty() && !value.equals("the") && !value.equals("a") && !value.equals("an");
					}
					//Conta le occeurenze di ogni parola
				}).groupBy("value").count();

		return dataset2;
	}
}
