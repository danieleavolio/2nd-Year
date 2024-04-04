import hdfs.Menu.Menu;

public class Main {
    public static void main(String[] args) {
        try {
            new Menu().start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}