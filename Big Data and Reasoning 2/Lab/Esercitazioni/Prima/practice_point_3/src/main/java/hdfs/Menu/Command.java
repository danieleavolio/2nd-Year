package hdfs.Menu;

import java.io.IOException;

public interface Command {

    int CONTINUE = 0;
    int STOP = 1;

    int execute() throws Exception;
    String getString();

    static void print_line(){
        StringBuilder line = new StringBuilder();
        for (int i = 0 ; i < 20 ; ++i)
            line.append("-");
        System.out.println(line) ;
    }

}
