import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;

/*      
  Created by Yilin Jiang on 27 Mar 2016
*/

public class LibraryDirector extends User {
  static SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");
  public LibraryDirector(Connection connection){
    super(connection);
  }

  @Override
  public int subMenu(){
    System.out.println("-----Operations for library director menu-----");
    System.out.println("What kinds of operation would you like to perform?");
    System.out.println("1. List all un-returned book copies which are checked-out within a period");
    System.out.println("2. Return to the main menu");
    System.out.print("Enter Your Choice: ");
    Scanner scanner = new Scanner(System.in);
    int choice = scanner.nextInt();
    if (choice == 1) listBook;
    if (choice == 2) return -1;
  }
  private void listBook() throws SQLException {
    System.out.print("Type in the starting date [dd/mm/yyyy]: ");
    Scanner scanner = new Scanner(System.in);
    String startDate = scanner.nextLine();
    
    System.out.print("Type in the ending date [dd/mm/yyyy]: ");
    Scanner scanner = new Scanner(System.in);
    String endDate = scanner.nextLine();
    
    System.out.println("List of UnReturned Book:");
    System.out.println("|LibUID|CallNum|CopyNum|Checkout|");
    
    Statement stmt = connection.createStatement();
    String query = "SELECT libuid, callnum, copynum, checkout FROM borrow" + " WHERE return IS NULL AND checkout >='"
+ startDate + " ' AND checkout <= '" + endDate + "'";
	ResultSet rs = stmt.executeQuery(query);
	while (rs.next()){
		String libuid = rs.getString("libuid");
		String callnum = rs.getString("callnum");
		int copynum = rs.getInt("copynum");
		String checkout = rs.getDate("checkout").toString();
		System.out.println("|"+libuid + "|" + callnum + "|" + copynum + "|" + checkout + "|");

	}    
	System.out.println("End of Query");
    
  }
}