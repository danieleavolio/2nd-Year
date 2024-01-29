# Laboratorio Big Data and Reasoning 
Teoria di Mazzotta 


**Che cos'è hadoop?**
Hadoop è un ecosistema che permette di gestire task da svolgere in parallelo su un cluster di macchine.
Viene usato quando ci sono grosse quantità di dati da gestire. La cosa principale è che puoi usare 
hardware economico per scalare
le applicazioni.

**Cos'è HDFS**

HDFS significa Hadoop Distributed File System ed è un file system distribuito che permette di
gestire file di grandi dimensioni. I file vengono divisi in blocchi e questi blocchi vengono replicati
in modo da garantire la tolleranza ai guasti.

Ci sono nodi:
- Namenode: gestisce i metadati e coordina i datanode
- Datanode: gestisce i blocchi di dati e risponde alle richieste di lettura e scrittura

**Cos'è YARN**
Yet Another Resource Negotiator. E' un framework che permette di gestire le risorse di un cluster
e di schedulare i task da eseguire.

Ci sono vari elementi che compongono YARN:

- ResourceManager: gestisce le risorse del cluster
- Container: astrazione che rappresenta le risorse di un nodo come CPU, RAM, disco e rete
- ApplicationMaster: gestisce le risorse di un'applicazione e coordina l'esecuzione delle applicazioni client.
- NodeManager: gestisce le risorse di un nodo e risponde alle richieste del ResourceManager. E' un servizio Slave


**Cos'è HIVE?**
Hive è un framework che salva tabelle come file nel hadoop file system. Supporta diversi formati per indicizzare le righe.
Usa database relazionali per salvare i metadati degli schemi e delle tabelle e puoi usare linguaggoi simil SQL

**Cos'è HBASE**
HBase è un database NoSQL che permette di salvare dati strutturati in tabelle direttamente nel hdfs. I dati sono distribuiti in **region server** ed è comodo per applicazinoi mapreduce

**Cos'è SQOOP**

Un tool di ETL che permette di 
importare i dati nel RDBMS dall'HDFS e viceversa. Utilizza mapreduce per salvare i dati.