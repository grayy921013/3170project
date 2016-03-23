import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Zhehui Zhou on 3/23/16.
 */
public class InquirySystem {
    public static void main(String[] args) {
        InquirySystem system = new InquirySystem();
        User user;
        Connection con = system.getConnection();
        System.out.println("Welcome to library inquiry system!");
        while (true) {
            System.out.println();
            int choice = system.mainMenu();
            if (choice == 5) {
                return;
            } else {
                user = system.getUserType(choice, con);
                while (true) {
                    System.out.println();
                    int action = user.subMenu();
                    if (action <= 0) break;
                    else user.runAction(action);
                }
            }
        }
    }


    private Connection getConnection() {
        String url = "jdbc:oracle:thin:@db12:1521/db12.cse.cuhk.edu.hk";
        String username = "c006";
        String password = "beanacko";
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int mainMenu() {
        System.out.println("-----Main menu-----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. Operation for administrator");
        System.out.println("2. Operation for library user");
        System.out.println("3. Operation for librarian");
        System.out.println("4. Operation for library director");
        System.out.println("5. Exit this program");
        System.out.print("Enter Your Choice: ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }

    private User getUserType(int choice, Connection connection) {
        if (choice == 1) return new Administrator(connection);
        return null;
    }
}
