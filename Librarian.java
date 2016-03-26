
import java.sql.*;
import java.util.Scanner;

/**
 *
 * Created by Minxuan Xu on 3/26/16.
 */
public class Librarian extends User {
    public Librarian(Connection connection) {
        super(connection);
    }
    
    @Override
    public void runAction(int action) {
        try {
            if (action == 1) bookBorrow();
            if (action == 2) bookReturn();
        } catch (SQLException e) {
            e.printStackTrace();
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
        //get callnum
        System.out.print("Enter The Call Number: ");
        scanner = new Scanner(System.in);
        String callnum = scanner.nextLine();
        //get copynum
        System.out.print("Enter The Copy Number: ");
        scanner = new Scanner(System.in);
        int copynum = scanner.nextInt();
         
        //check whether the copy is available to be borrowed
        Statement stmt = connection.createStatement();
        String query= "SELECT FROM borrow WHERE callnum='" + callnum + "' AND copynum="
                + copynum + " AND return IS NULL";
        ResultSet rs = stmt.executeQuery(query);
        if (rs.getRow() > 0) {
            System.out.println("The book copy has not been returned yet:(");
        }
        else 
        {
           //borrow the book
           String today=""; //how to get today?
           String update="INSERT INTO borrow VALUES ('" + userid + "','" + callnum +
                   "'," + copynum + ","+ today + ",NULL)";
           stmt.executeUpdate(update);
           System.out.println("Book borrowing performed successfully!!!"); 
        }
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
        String query= "SELECT FROM borrow WHERE libuid='" + userid + "' AND callnum='" 
                + callnum + "' AND copynum=" + copynum + " AND return IS NULL";
        ResultSet rs = stmt.executeQuery(query);
        if (rs.getRow() > 0) {
            System.out.println("The book copy has not been checked out by you:(");
        }
        else 
        {
           //return the book
           String today=""; //how to get today?
           String update="UPDATE borrow SET return=" + today + " WHERE libuid='" 
                + userid + "' AND callnum='" + callnum + "' AND copynum=" + copynum
                +"  AND return IS NULL";
           stmt.executeUpdate(update);
           System.out.println("Book returning performed successfully!!!"); 
        }
     }
}
