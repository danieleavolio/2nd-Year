package hdfs.Menu;

import org.apache.hadoop.conf.Configuration;

import java.util.ArrayList;
import java.util.Scanner;

public class Menu {

    private final ArrayList<Command> commands = new ArrayList<>();
    public Menu() throws Exception {

        String username = "hadoop";

        // Setting builder
        Hdfs.Builder builder = new Hdfs.Builder(username);
        builder.getConfiguration().addResource("hdfs-site.xml");
        builder.getConfiguration().addResource("core-site.xml");
        builder.setupHdfs();

        commands.add(builder.buildShowECOMMERCE());
        commands.add(builder.buildReadCommand());
        commands.add(builder.buildWriteOrderCommand());
        commands.add(new StopCommand());
    }

    public void start(){

        Scanner s = new Scanner(System.in);
        System.out.println("Welcome to HDFS MENU !");
        while (true){
            Command.print_line();
            for (int i = 0; i < commands.size() ; i++) {
                Command command = commands.get(i);
                System.out.println("Type ["+i+"] to "+ command.getString());
            }
            Command.print_line();
            int command_i = s.nextInt();

            if( command_i < 0 || command_i >= commands.size()){
                System.out.println("Key not valid");
                continue;
            }

            try {
                int res = commands.get(command_i).execute();

                // BYE
                if (res == Command.STOP)
                    break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                // TODO: DELETE
                e.printStackTrace();
                return;
            }

        }

    }

}
