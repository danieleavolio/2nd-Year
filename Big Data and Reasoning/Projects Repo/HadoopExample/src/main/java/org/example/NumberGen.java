package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class NumberGen {
    public static void main(String[] args) {
        generateIntDouble();
    }

    public static void generateDouble() {
        String filePath = "d:/Lovaion/University/2nd Year/Big Data and Reasoning/Projects Repo/HadoopExample/numbers.txt";
        int numDoubles = 1000000;
        Random random = new Random();
        try {
            FileWriter writer = new FileWriter(filePath);
            for (int i = 0; i < numDoubles; i++) {
                double randomDouble = random.nextDouble() * 2000;
                writer.write(Double.toString(randomDouble) + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateIntDouble(){
        //Generate 500000 lines of "Int Double" pairs
        String filePath = "d:/Lovaion/University/2nd Year/Big Data and Reasoning/Projects Repo/HadoopExample/numbers2.txt";
        int numDoubles = 500000;
        Random random = new Random();
        try {
            FileWriter writer = new FileWriter(filePath);
            for (int i = 0; i < numDoubles; i++) {
                double randomDouble = random.nextDouble() * 2000;
                int randomInt = random.nextInt(10000);
                writer.write(Integer.toString(randomInt) + " " + Double.toString(randomDouble) + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
