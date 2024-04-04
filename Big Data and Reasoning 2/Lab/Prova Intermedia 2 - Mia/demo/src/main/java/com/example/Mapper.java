package com.example;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.io.Text;

public class Mapper extends TableMapper<Text, Text> {
    @Override
    protected void map(ImmutableBytesWritable key, Result value,
            Context context)
            throws IOException, InterruptedException {

                byte [] nome = value.getValue("nome".getBytes(), null);
                byte [] numero = value.getValue("numero".getBytes(), null);
                byte [] tipologia = value.getValue("tipologia".getBytes(), null);

                if (nome != null && numero != null && tipologia != null) {
                    String nomeString = new String(nome);
                    String numeroString = new String(numero);
                    String tipologiaString = new String(tipologia);

                    context.write(new Text(nomeString), new Text(tipologiaString + " " + numeroString));
                }
    }

}
