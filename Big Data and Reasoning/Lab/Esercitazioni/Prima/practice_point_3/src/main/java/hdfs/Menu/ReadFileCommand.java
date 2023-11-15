package hdfs.Menu;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ReadFileCommand extends ReadCommand{

    private final String string_path_file ;
    public ReadFileCommand(FileSystem hdfs, String file) throws Exception {
        super(hdfs);
        string_path_file = file ;
    }

    @Override
    protected int read() throws Exception {

        Path path_file = new Path(string_path_file);
        if(!hdfs.exists(path_file)){
            throw new Exception(path_file.getName() + " no such file or directory");
        }
        FileStatus status = hdfs.getFileStatus(path_file);

        if(!status.isFile()) throw  new Exception("Unable to read a folder yet :(") ;

        FSDataInputStream stream = hdfs.open(path_file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        try {
            System.out.println("FILE: " + path_file.getName());
            do {
                for (int i = 0; i < batch_size; i++) {
                    if (reader.ready()) {
                        System.out.println(reader.readLine());
                    } else break;
                }
                System.out.println("Print 'n' to continue");
                System.out.println("Print 'q' to stop");
            } while (scanner.nextLine().equals("n"));

        }finally { stream.close(); }
        return Command.CONTINUE;
    }
}
