import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Zhehui Zhou on 3/23/16.
 */
public class Test {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@db12:1521/db12.cse.cuhk.edu.hk";
        String username = "c006";
        String password = "beanacko";
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
}
