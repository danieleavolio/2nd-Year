package org.example;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.hadoop.util.bloom.BloomFilter;
import org.apache.hadoop.util.bloom.Key;
import org.apache.hadoop.util.hash.Hash;

/*
We are reading the input file and storing the bloom filter hot words file in the local file system (I am using windows)
ideally the file should be read and stored in the hdfs using hadoop hdfs api for simplicity purpose have not included
the code for hdfs filesystem.This Bloom filter file can later be deserialized from HDFS or local system just as easily
as it was written.Just open up the file using the FileSystem object and pass it to BloomFilter.readFields.
 */


public class DepartmentBloomFilterTrainer {
    public static int getBloomFilterOptimalSize(int numElements, float falsePosRate) {
        return (int) (-numElements * (float) Math.log(falsePosRate) / Math.pow(Math.log(2), 2));
    }
    public static int getOptimalK(float numElements, float vectorSize) {
        return (int) Math.round(vectorSize * Math.log(2) / numElements);
    }
    public static void main(String[] args) throws IOException {
        /*
         * I have used my local path in windows change the path as per your
         * local machine
         */
        args = new String[] { "32658", "0.2","Replace this string with Input file location",
                "Replace this string with output path location where the bloom filter hot list data will be stored","" };
        int numMembers = Integer.parseInt(args[0]);
        float falsePosRate = Float.parseFloat(args[1]);
        int vectorSize = getBloomFilterOptimalSize(numMembers, falsePosRate);
        int nbHash = getOptimalK(numMembers, vectorSize);
        BloomFilter filter = new BloomFilter(vectorSize, nbHash, Hash.MURMUR_HASH);
        //ConfigFile configFile = new ConfigFile(args[2], FileType.script, FilePath.absolutePath);
        //String fileContent = configFile.getFileContent();
        // Le righe sopra danno errore e quindi creo questa variabile temporanea
        String fileContent = "";
        String[] fileLine = fileContent.split("\n");
        for (String lineData : fileLine) {
            String lineDataSplit[] = lineData.split(",", -1);
            String departmentName = lineDataSplit[3];
            filter.add(new Key(departmentName.getBytes()));
        }
        DataOutputStream dataOut = new DataOutputStream(new FileOutputStream(args[3]));
        filter.write(dataOut);
        dataOut.flush();
        dataOut.close();
    }
}