
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;

/**
 * Created by Minxuan Xu on 3/26/16.
 */

public class LibraryUser extends User{
    public LibraryUser(Connection connection) {
        super(connection);
    }
    
    @Override
    public void runAction(int action) {
        try {
            if (action == 1) searchBook();
            if (action == 2) loanRecord();
        } catch (SQLException e) {
            e.printStackTrace();
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
        System.out.println("Type in the Search Keyword: ");
        scanner = new Scanner(System.in);
        String keyword = scanner.nextLine();
        
        //start query
        System.out.println("|Call Num|Title|Author|Available No. of Copy|");
        switch (choice) {
            case 1:
                Statement stmt=connection.createStatement();
                //get title
                String query = "SELECT FROM book WHERE callnum='" + keyword + "'";
                ResultSet rs = stmt.executeQuery(query);
                String title = "";
                while (rs.next())
                    title = rs.getString("title");
                
                //get authors
                query = "SELECT aname FROM authorship WHERE callnum='" + keyword + "'";
                rs = stmt.executeQuery(query);
                String authorConcate="";
                while (rs.next()) 
                    authorConcate += rs.getString("aname") + ", ";
                authorConcate = authorConcate.substring(0,authorConcate.lastIndexOf(','));
                
                //get available no. of copy
                query = "SELECT COUNT(*) AS availcopycount FROM (SELECT FROM copy"
                        + " WHERE callnum='" + keyword + "' MINUS SELECT callnum,copynum"
                        + " FROM borrow WHERE return IS NULL AND callnum='" + keyword + "')";
                rs = stmt.executeQuery(query);
                int availCopyCount = 0;
                while (rs.next()) 
                    availCopyCount = rs.getInt("availcopycount");
                
                //print result
                System.out.println("|" + keyword + "|" + title + "|" + authorConcate +
                        "|" + availCopyCount + "|");
                break;
            case 2:
                //to be done later
            case 3:
                //to be done later
        }
        System.out.println("End of Query");        
    }
    
    private void loanRecord() throws SQLException {
        System.out.print("Enter the User ID: ");
        //get userid
        Scanner scanner = new Scanner(System.in);
        String userid = scanner.nextLine();
        
        //start query
        Statement stmt = connection.createStatement();
        String query= "SELECT callnum,copynum,title,checkout,return FROM book B, borrow W"
                + " WHERE B.callnum=W.callnum AND libuid='" + userid + "' ORDER BY checkout DESC";
        ResultSet rs = stmt.executeQuery(query);
        System.out.println("|CallNum|CopyNum|Title|Author|Check-out|Returned?|");
        while (rs.next()) {
            String callnum = rs.getString("callnum");
            int copynum = rs.getInt("copynum");
            String title = rs.getString("title");
            String checkout = rs.getDate("checkout").toString();
            String returned = rs.getDate("return").toString();
            if (returned.equals("NULL")) returned = "No"; else returned = "Yes";
            //how to deal with authors?
            System.out.println("|" + callnum + "|" + copynum +"|" + title +
                     "|Author|"+ checkout + "|" + returned + "|");            
        }   
        System.out.println("End of Query");  
    }
}
