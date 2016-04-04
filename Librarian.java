
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by Minxuan Xu on 3/26/16.
 */
public class Librarian extends User {
    static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    public Librarian(Connection connection) {
        super(connection);
    }

    @Override
    public void runAction(int action) {
        try {
            if (action == 1) bookBorrow();
            if (action == 2) bookReturn();
        } catch (SQLException e) {
            Error.error("Tables have not been created.");
        }
    }

    @Override
    public int subMenu() {
        System.out.println("-----Operation for library user menu-----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. Book Borrowing");
        System.out.println("2. Book Returning");
        System.out.println("3. Return to the main menu");
        System.out.print("Enter Your Choice: ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if (choice == 3) return -1;
        else return choice;
    }

    private void bookBorrow() throws SQLException {
        //get userid
        System.out.print("Enter The User ID: ");
        Scanner scanner = new Scanner(System.in);
        String userid = scanner.nextLine();


        //check whether the user exists
        Statement stmt = connection.createStatement();
        String query = "SELECT * FROM libuser WHERE libuid='" + userid + "'";
        ResultSet rs = stmt.executeQuery(query);
        if (!rs.next()) {
            //the user does not exist
            Error.error("The user does not exist:(");
            return;
        }

        //get callnum
        System.out.print("Enter The Call Number: ");
        scanner = new Scanner(System.in);
        String callnum = scanner.nextLine();
        //get copynum
        System.out.print("Enter The Copy Number: ");
        scanner = new Scanner(System.in);
        int copynum = scanner.nextInt();

        //check whether the copy exists
        stmt = connection.createStatement();
        query = "SELECT * FROM borrow WHERE callnum='" + callnum + "' AND copynum="
                + copynum;
        rs = stmt.executeQuery(query);
        if (!rs.next()) {
            //the copy does not exist
            Error.error("The book copy does not exist:(");
            return;
        }
        //check whether the copy is available to be borrowed
        stmt = connection.createStatement();
        query = "SELECT * FROM borrow WHERE callnum='" + callnum + "' AND copynum="
                + copynum + " AND return IS NULL";
        rs = stmt.executeQuery(query);
        if (rs.next()) {
            System.out.println("The book copy has not been returned yet:(");
        } else {
            //borrow the book
            String today = format.format(new Date());
            String update = "INSERT INTO borrow VALUES ('" + userid + "','" + callnum +
                    "'," + copynum + ",to_date('" + today + "','dd/mm/yyyy'),NULL)";
            stmt.executeUpdate(update);
            System.out.println("Book borrowing performed successfully!!!");
        }
        rs.close();
        stmt.close();
    }

    private void bookReturn() throws SQLException {
        //get userid
        System.out.print("Enter The User ID: ");
        Scanner scanner = new Scanner(System.in);
        String userid = scanner.nextLine();
        //get callnum
        System.out.print("Enter The Call Number: ");
        scanner = new Scanner(System.in);
        String callnum = scanner.nextLine();
        //get copynum
        System.out.print("Enter The Copy Number: ");
        scanner = new Scanner(System.in);
        int copynum = scanner.nextInt();

        //check whether the specified checkout record exists
        Statement stmt = connection.createStatement();
        String query = "SELECT * FROM borrow WHERE libuid='" + userid + "' AND callnum='"
                + callnum + "' AND copynum=" + copynum + " AND return IS NULL";
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            //return the book
            String today = format.format(new Date());
            String update = "UPDATE borrow SET return=to_date('" + today + "','dd/mm/yyyy') "
                    + "WHERE libuid='" + userid + "' AND callnum='" + callnum + "' AND copynum="
                    + copynum + "  AND return IS NULL";
            stmt.executeUpdate(update);
            System.out.println("Book returning performed successfully!!!");
        } else {
            System.out.println("The book copy has not been checked out by you:(");
        }
        rs.close();
        stmt.close();
    }
}
