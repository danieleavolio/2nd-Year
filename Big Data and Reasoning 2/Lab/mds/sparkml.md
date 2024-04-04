# Cose di SparkML che riesco a scrivere

## Steps da fare prima di iniziare

Dobbiamo capire se dobbiamo fare del `preprocessing` dei dati che potrebbe includere pulizia o costruzione di nuovi 
dati a partire da quelli che abbiamo.

Ad esempio, avendo dati come `Latitudine` e `Longitudine` possiamo creare una nuova colonna che rappresenta la `distanza`.

## Preprocessing
Si inizia sempre **leggendo** i dati. Per farlo si usa il metodo `read` di `SparkSession` che ci permette di leggere

```java

//Runnato in local mode per runnarl nell'IDE
SparkSession spark = SparkSession.builder().master("local").appName("SparkML").getOrCreate();

spark.sparkContext().setLogLevel("ERROR");

Dataset<Row> df = spark.read().option("header", "true").csv("path/to/file.csv");
```

Conveniente farsi una classe che gestisce la parte di `Data Preparation`

```java

public class Preparation(){

    public static Dataset<Row> clean(Dataset<Row> data){
        //TODO
        return data.filter((FilterFunction<Row>) r ->{
            try{
                if(r.anyNull()){
                    return false;
                }
                float amount = 0;
                boolean amountFound = true;
                try{
                    amount = Float.parseFloat(r.getAs("Amount"));
            }
            catch (NumberFormatException e){
                amountFound = false;
            }
        })
        catch (Exception e){
            return false;
        }
        Double.parseDouble(r.getAs("Amount"));
        Double.parseDouble(r.getAs("Latitude"));
        Double.parseDouble(r.getAs("Longitude"));
        //Manca roba perche tropop veloce
        return true;
    });
    }
```

Diciamo che poi me lo vedo dal file sta cosa che non si capisce un cazzo.
