package org.example;

import java.io.File;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.PropertyConfigurator;

//Questi estends e implements serve per fare in modo che possa essere runnato con CMD
public class ChainExample extends Configured implements Tool {
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
        return runExample(strings);
    }

    public int runExample(String[] args) throws Exception {
        //In questo esempio vogliamo fare:
        /*
         *  - Dato un file contenente testo
         *  - Map -> Reduce -> Map
         *  - Usiamo l'idea dei ChainMapper
         *      - Job complessi
         *      - Più fasi di mapping seguiti da fase di reduce (anche possibilmente seguiti da un map)
         * 
         *  - Vogliamo sapere quante volte compare la parola che compare meno nel file
         *  - Quindi calcolare il minimo tra le parole e stampare in <Parola, Occorrenze>
         */

         Configuration conf = new Configuration();

         Job job = Job.getInstance(conf, "word count");

         //Job to consider 
        return 1;
    }

    


}
