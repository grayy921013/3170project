import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

/*      
  Created by Yilin Jiang on 27 Mar 2016
*/

public class LibraryDirector extends User {
    static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    static {
        format.setLenient(false);
    }
    public LibraryDirector(Connection connection) {
        super(connection);
    }

    @Override
    public int subMenu() {
        System.out.println("-----Operations for library director menu-----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. List all un-returned book copies which are checked-out within a period");
        System.out.println("2. Return to the main menu");
        System.out.print("Enter Your Choice: ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if (choice == 2) return -1;
        return choice;
    }

    @Override
    public void runAction(int action) {
        try {
            if (action == 1) listBook();
        } catch (SQLException e) {
            Error.error("Tables have not been created.");
        } catch (ParseException e) {
            Error.error("Wrong input format.");
        }
    }

    private void listBook() throws SQLException, ParseException {
        System.out.print("Type in the starting date [dd/mm/yyyy]: ");
        Scanner scanner = new Scanner(System.in);
        String startDate = scanner.nextLine();
        format.parse(startDate);

        System.out.print("Type in the ending date [dd/mm/yyyy]: ");
        String endDate = scanner.nextLine();
        format.parse(endDate);

        System.out.println("List of UnReturned Book:");
        System.out.println("|LibUID|CallNum|CopyNum|Checkout|");

        Statement stmt = connection.createStatement();
        String query = "SELECT libuid, callnum, copynum, checkout FROM borrow" + " WHERE return IS NULL AND checkout >="
                + "to_date('" + startDate + "','dd/mm/yyyy') AND checkout <= to_date('" + endDate + "','dd/mm/yyyy')" +
                "ORDER BY checkout DESC";
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            String libuid = rs.getString("libuid");
            String callnum = rs.getString("callnum");
            int copynum = rs.getInt("copynum");
            String checkout = format.format(rs.getDate("checkout"));
            System.out.println("|" + libuid + "|" + callnum + "|" + copynum + "|" + checkout + "|");

        }
        System.out.println("End of Query");

    }
}