
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;

/**
 * Created by Minxuan Xu on 3/26/16.
 */

public class LibraryUser extends User{
    static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    
    public LibraryUser(Connection connection) {
        super(connection);
    }
    
    @Override
    public void runAction(int action) {
        try {
            if (action == 1) searchBook();
            if (action == 2) loanRecord();
        } catch (SQLException e) {
            Error.error("Tables have not been created.");
         }
    }
    
    @Override
    public int subMenu() {
        System.out.println("-----Operation for library user menu-----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. Search for Books");
        System.out.println("2. Show loan record of a user");
        System.out.println("3. Return to the main menu");
        System.out.print("Enter Your Choice: ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if (choice == 3) return -1;
        else return choice;
    }
    
    private void searchBook() throws SQLException {
        System.out.println("Choose the Search criterion: ");
        System.out.println("1. call number");
        System.out.println("2. title");
        System.out.println("3. author");
        System.out.print("Choose the search criterion: ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        
        //read search keyword
        System.out.print("Type in the Search Keyword: ");
        scanner = new Scanner(System.in);
        String keyword = scanner.nextLine();
        
        //start query
        System.out.println("|Call Num|Title|Author|Available No. of Copy|");
        String query, title;
        ResultSet rs;
        Statement stmt = connection.createStatement();
        switch (choice) {
            case 1:
                //query by callnum
                query = "SELECT title FROM book WHERE callnum='" + keyword + "'";
                rs = stmt.executeQuery(query);
                while (rs.next()) {
                    title = rs.getString("title").trim();
                    System.out.println("|" + keyword + "|" + title + "|" + getAuthor(keyword) +
                        "|" + getAvailability(keyword) + "|");
                }
                rs.close();
                break;
            case 2:
                //query by tilte (partial matching)
                query = "SELECT callnum,title FROM book WHERE title LIKE '%" + keyword + "%' "
                        + "ORDER BY callnum ASC";
                rs = stmt.executeQuery(query);
                while (rs.next()) {
                    String callnum = rs.getString("callnum").trim();
                    title = rs.getString("title").trim();
                    System.out.println("|" + callnum + "|" + title + "|" + getAuthor(callnum) +
                        "|" + getAvailability(callnum) + "|");
                }
                rs.close();                
                break;
            case 3:
                //query by author (partial matching)
                query = "SELECT callnum,title FROM book WHERE callnum IN "
                        + "(SELECT callnum FROM authorship WHERE aname LIKE '%" + keyword + "%')"
                        + " ORDER BY callnum ASC";
                rs = stmt.executeQuery(query);
                while (rs.next()) {
                    String callnum = rs.getString("callnum").trim();
                    title = rs.getString("title").trim();
                    System.out.println("|" + callnum + "|" + title + "|" + getAuthor(callnum) +
                        "|" + getAvailability(callnum) + "|");
                }
                rs.close();                
                break;
        }
        stmt.close();
        System.out.println("End of Query");        
    }
    
    private String getAuthor(String callnum) throws SQLException {
        Statement stmt = connection.createStatement();
        String query = "SELECT aname FROM authorship WHERE callnum='" + callnum + "'";
        ResultSet rs = stmt.executeQuery(query);
        String authorConcate="";
        while (rs.next()) 
            authorConcate += rs.getString("aname").trim() + ", ";
        authorConcate = authorConcate.substring(0,authorConcate.lastIndexOf(','));
        rs.close();
        stmt.close();
        return authorConcate;
    } 
    
    private int getAvailability(String callnum) throws SQLException {
        Statement stmt = connection.createStatement();
        String query = "SELECT COUNT(*) AS availcopycount FROM (SELECT * FROM copy"
                        + " WHERE callnum='" + callnum + "' MINUS SELECT callnum,copynum"
                        + " FROM borrow WHERE return IS NULL AND callnum='" + callnum + "')";
        ResultSet rs = stmt.executeQuery(query);
        int availCopyCount = 0;
        while (rs.next()) 
            availCopyCount = rs.getInt("availcopycount");
        rs.close();
        stmt.close();
        return availCopyCount;
    } 
    
    private void loanRecord() throws SQLException {
        System.out.print("Enter the User ID: ");
        //get userid
        Scanner scanner = new Scanner(System.in);
        String userid = scanner.nextLine();
        
        //start query
        Statement stmt = connection.createStatement();
        String query= "SELECT W.callnum,W.copynum,B.title,W.checkout,W.return FROM book B,borrow W"
                + " WHERE B.callnum=W.callnum AND libuid='" + userid + "' ORDER BY W.checkout DESC";
        ResultSet rs = stmt.executeQuery(query);
        System.out.println("|CallNum|CopyNum|Title|Author|Check-out|Returned?|");
        while (rs.next()) {
            String callnum = rs.getString("callnum").trim();
            int copynum = rs.getInt("copynum");
            String title = rs.getString("title").trim();
            Date checkout = rs.getDate("checkout");
            Date returned = rs.getDate("return");
            String returnedYesNo;
            if (returned == null) returnedYesNo = "No"; else returnedYesNo = "Yes";
            System.out.println("|" + callnum + "|" + copynum +"|" + title +
                    "|"+ getAuthor(callnum) + "|"+ format.format(checkout) + 
                    "|" + returnedYesNo + "|");            
        }   
        System.out.println("End of Query");
        rs.close();
        stmt.close();
    }
}
