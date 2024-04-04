package hdfs.Menu;

import org.apache.hadoop.fs.FileSystem;

public class ReadCommand extends Hdfs implements Command{

    // The number of lines to read every time
    protected int batch_size = 10;

    public ReadCommand(FileSystem hdfs) throws Exception {
        super(hdfs);
    }


    protected int read() throws Exception {

        System.out.println("COMMAND: READ FILE");
        Command.print_line();
        System.out.println("Which file do you want to see?");
        System.out.println("Please enter the path");
        String path_file = scanner.nextLine();
        return new ReadFileCommand(hdfs, path_file).read() ;

    }

    @Override
    public int execute() throws Exception {
        return read();
    }

    @Override
    public String getString() {
        return  "read file" ;
    }
}
