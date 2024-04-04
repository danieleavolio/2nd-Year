package hdfs.Menu;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.ArrayList;

public class ShowECOMMERCE extends Hdfs implements Command{


    public ShowECOMMERCE(FileSystem hdfs) throws Exception {
        super(hdfs);
    }

    private void printTree(ArrayList<String> current_path, int depth) throws IOException {

        String path = String.join("/", current_path);

        StringBuilder path_name = new StringBuilder();
        for (int i = 0 ; i < depth ; ++i)
            path_name.append("\t");

        path_name.append(current_path.get(current_path.size() - 1));
//        path_name.append(path);
        System.out.println(path_name);

        FileStatus f_status = hdfs.getFileStatus(new Path(path));
        if(f_status.isFile()) return;



        FileStatus[] listStatus = hdfs.listStatus(new Path(path));
        for (FileStatus status : listStatus) {
            String child_name = status.getPath().getName() ;
            current_path.add(child_name);
            printTree(current_path, depth + 1);
            current_path.remove(current_path.size() - 1);
        }

    }

    @Override
    public int execute() throws Exception {
        System.out.println("TREE E-COMMERCE:");
        ArrayList<String> current_path = new ArrayList<>();
        current_path.add("/e-commerce");
        printTree(current_path,0);
        return Command.CONTINUE;
    }

    @Override
    public String getString() {
        return "show E-COMMERCE";
    }
}
