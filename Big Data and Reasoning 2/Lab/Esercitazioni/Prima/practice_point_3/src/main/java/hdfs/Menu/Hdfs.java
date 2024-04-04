package hdfs.Menu;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.util.ByteArrayManager;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public abstract  class Hdfs {

    public static final String EOF="-1";
    protected FileSystem hdfs ;
    protected Scanner scanner = new Scanner(System.in);

    public Hdfs(FileSystem hdfs) throws Exception {
        this.hdfs = hdfs ;
    }

    static class Builder{

        private  Configuration configuration = new Configuration() ;
        private String username ;
        private FileSystem hdfs = null ;

        public Builder(String username){
           this.username = username ;
           System.setProperty("HADOOP_USER_NAME", "hadoop");
        }

        public Configuration getConfiguration() {
            return configuration;
        }

        public void setupHdfs() throws IOException {
            hdfs = FileSystem.get(configuration);
        }

        public void checkBeforeBuild() throws Exception {
            if (Objects.equals(username, "")) throw new Exception("No username set");
            if (hdfs == null) throw new Exception("No hdfs, first call  setupHdfs() to build a command");
        }

        public ReadCommand buildReadCommand() throws Exception {
            checkBeforeBuild();
            return new ReadCommand(hdfs);
        }

        public ShowECOMMERCE buildShowECOMMERCE() throws Exception {
            checkBeforeBuild();
            return new ShowECOMMERCE(hdfs);
        }

        public WriteOrderCommand buildWriteOrderCommand() throws Exception {
            checkBeforeBuild();
            return new WriteOrderCommand(hdfs);
        }

    }

}
