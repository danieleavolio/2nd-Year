package org.example;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.PropertyConfigurator;

//Questi estends e implements serve per fare in modo che possa essere runnato con CMD
public class HadoopMainWorkflow extends Configured implements Tool {
    public static void main(String[] args) throws Exception {

        try {
            PropertyConfigurator.configure("log4j.properties");
            File f = new File("output");
            if (f.exists()) {
                for (File x : f.listFiles()) {
                    // stuff
                    System.out.println(x.getName());
                    x.delete();
                }
                f.delete();
            }

            int res = ToolRunner.run(new Configuration(), new HadoopMainWorkflow(), args);
            System.exit(res);
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    // Questi sono metodi che servono per Tool
    @Override
    public int run(String[] strings) throws Exception {
        return runExample2(strings);
    }

    public int runExample(String[] args) throws Exception {

        System.out.println("Starting Hadoop Word Count Example");

        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "word count");

        job.setJarByClass(HadoopMainWorkflow.class);

        job.setMapperClass(WordCounterMapper.class);

        job.setReducerClass(WordCounterReducer.class);

        // set combiner
        job.setCombinerClass(WordCounterReducer.class);

        // Set input format
        job.setInputFormatClass(TextInputFormat.class);

        // Set mapper output format
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        // Set output format
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // set number of reducers
        job.setNumReduceTasks(2);

        // Set input and output directories
        FileInputFormat.addInputPath(job, new Path("test.txt"));
        // Set output directory
        FileOutputFormat.setOutputPath(job, new Path("output"));

        System.out.println("Aspetto qua");
        // Wait for completion
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public int runExample2(String[] args) throws Exception {

        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "word count");

        job.setJarByClass(HadoopMainWorkflow.class);

        job.setMapperClass(DoubleMinMapper.class);

        job.setReducerClass(DoubleMinReducer.class);

        // set combiner
        job.setCombinerClass(DoubleMinReducer.class);

        // Set input format
        job.setInputFormatClass(TextInputFormat.class);
        
        // Set mapper output format
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        // Set output format
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        // set number of reducers
        job.setNumReduceTasks(2);

        // Set input and output directories
        FileInputFormat.addInputPath(job, new Path("numbers.txt"));
        // Set output directory
        FileOutputFormat.setOutputPath(job, new Path("output"));

        // Wait for completion
        return job.waitForCompletion(true) ? 0 : 1;

    }

    public static class WordCounterMapper extends Mapper<Object, Text, Text, IntWritable> {
        private static final IntWritable numberOne = new IntWritable(1);
        private static final Text uniqueText = new Text();

        @Override
        protected void map(Object key, Text value, Mapper<Object, Text, Text, IntWritable>.Context context)
                throws IOException, InterruptedException {
            StringTokenizer tok = new StringTokenizer(value.toString());

            while (tok.hasMoreTokens()) {
                uniqueText.set(tok.nextToken());
                context.write(uniqueText, numberOne);
            }
        }
    }

    // Implementazione reducer
    public static class WordCounterReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values,
                Reducer<Text, IntWritable, Text, IntWritable>.Context context)
                throws IOException, InterruptedException {
            // Per ogni chiave, avremo il contatore delle parole.

            int sum = 0;
            for (IntWritable value : values)
                sum += value.get();
            context.write(key, new IntWritable(sum));
        }
    }

    // Facciamo un average mapper

    public static class DoubleAverageMapper extends Mapper<Object, Text, Text, DoubleWritable> {
        @Override
        protected void map(Object key, Text value, Mapper<Object, Text, Text, DoubleWritable>.Context context)
                throws IOException, InterruptedException {
            double d = Double.parseDouble(value.toString());
            context.write(new Text("Average:"), new DoubleWritable(d));

        }
    }

    public static class DoubleAverageReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
        @Override
        protected void reduce(Text key, Iterable<DoubleWritable> values,
                Reducer<Text, DoubleWritable, Text, DoubleWritable>.Context context)
                throws IOException, InterruptedException {
            double sum = 0;
            int count = 0;

            for (DoubleWritable d : values) {
                sum += d.get();
                count++;
            }

            context.write(new Text("Average:"), new DoubleWritable(sum / count));
        }
    }

    // Minimo di double
    public static class DoubleMinMapper extends Mapper<Object, Text, Text, Text> {
        @Override
        protected void map(Object key, Text value, Mapper<Object, Text, Text, Text>.Context context)
                throws IOException, InterruptedException {
            context.write(new Text("Key:"), value);
        }
    }

    //Nota: STIAMO Usando questo tipo di format <text,text,text,text> per poter usare
    //Il combiner senza fare salti mortali
    public static class DoubleMinReducer extends Reducer<Text, Text, Text, Text> {
        @Override
        protected void reduce(Text key, Iterable<Text> values,
                Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
            // Meglio usare il primo valore come minimo, per evitare problemi in caso di 0
            // elementi o 1 elemento boh

            double min = 0;
            boolean isFirst = true;
            for (Text text : values) {
                if (isFirst) {
                    min = Double.parseDouble(text.toString());
                    isFirst = false;
                } else {
                    double next = Double.parseDouble(text.toString());
                    min = Math.min(min, next);
                }
            }

            if (isFirst)
                context.write(new Text("No value has been found"), new Text(""));
            else
                context.write(new Text("Min:"), new Text(min + ""));
        }
    }

}
