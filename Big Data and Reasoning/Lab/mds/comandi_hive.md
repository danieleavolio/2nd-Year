
### Create a Managed Table

```sql
create table base_table(id integer, name string) row format delimited fields terminated by ',' lines terminated by '\n';
load data inpath 'tabledata.txt' into table base_table; //tabledata.txt is a path in the hdfs
```

### Managed Table with SerDe

```sql
create table csv_table(name string, surname string) row format serde 'org.apache.hadoop.hive.serde2.OpenCSVSerde';
insert into csv_table values ("mario", "rossi"), ("mario", "verdi");

create table json_table(name string, surname string) row format serde 'org.apache.hive.hcatalog.data.JsonSerDe';
insert into json_table values ("mario", "rossi"), ("mario", "verdi");
```

### Table with Partition

```sql
create table part_table(name string, surname string) partitioned by (extraction string) row format serde 'org.apache.hadoop.hive.serde2.OpenCSVSerde';
insert into part_table partition (extraction="extr1") values ("mario", "rossi");
insert into part_table partition (extraction="extr2") values ("mario", "verdi"), ("mario", "bianchi");
insert into part_table partition (extraction="extr3") values ("franco", "verdi"), ("franco", "bianchi");
```

### Table with Buckets

```sql
create table bucket_table(name string, surname string) clustered by (name) into 2 buckets row format serde 'org.apache.hadoop.hive.serde2.OpenCSVSerde';
insert into bucket_table values ("mario", "rossi"), ("franco", "rossi"), ("mario", "verdi"), ("mario franco", "rossi");
```

### CTAS Create Table As Select stored as orc table

```sql
create table orc_table stored as orc as select * from base_table;
```

### External Tables

```bash
hdfs dfs -mkdir domains_folder
hdfs dfs -put domains.txt domains_folder
create external table url(domain string, university string, department string) row format delimited fields terminated by '.' lines terminated by '\n' location '/user/hadoop/domains_folder';

select count(*) from url where domain='com';

select distinct(university) from url where domain='com';

drop table orc_table;

drop table url;
```

# Starting hiveserver2
``` 
    hiveserver2
```


# Client will use beeline to open a CLI with hiveserver

```
    beeline -u jdbc:hive2://master:10000

    show databases #query example to test hive connectivity
```

# Copiare file da source a destintion con ssh

```bash
scp /path/to/local/file username@remote-server:/path/on/remote/server
scp /path/to/local/example.txt username@192.168.56.XX:~


```