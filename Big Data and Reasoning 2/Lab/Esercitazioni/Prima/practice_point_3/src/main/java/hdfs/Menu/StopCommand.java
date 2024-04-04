package hdfs.Menu;

public class StopCommand implements Command{

    @Override
    public int execute() throws Exception {
        System.out.println("Bye ! :) ");
        Command.print_line();
        return STOP;
    }

    @Override
    public String getString() {
        return "close menu :(";
    }
}
