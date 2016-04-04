/**
 * Created by Zhehui Zhou on 4/4/16.
 */
public class Error {
    public static void error(String msg) {
        System.out.println("[Error]: " + msg);
    }
    public static void newLineError(String msg) {
        System.out.println();
        error(msg);
    }
}
