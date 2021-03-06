import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

/**
 * Created by Zhehui Zhou on 3/23/16.
 */
public class Administrator extends User {
    static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    static {
        format.setLenient(false);
    }

    public Administrator(Connection connection) {
        super(connection);
    }

    @Override
    public void runAction(int action) {
        if (action == 1) {
            try {
                createAllTables();
            } catch (SQLException e) {
                Error.newLineError("Tables already exist.");
            }
        }
        if (action == 2) {
            try {
                dropAllTables();
            } catch (SQLException e) {
                Error.newLineError("Tables have not been created.");
            }
        }
        if (action == 3) {
            try {
                loadData();
            } catch (SQLException e) {
                Error.newLineError("Tables have not been created or some records already exist in the database.");
            } catch (FileNotFoundException e) {
                Error.newLineError("Corresponding files can not been found.");
            } catch (ParseException e) {
                Error.newLineError("Wrong data format.");
            }
        }
        if (action == 4) {
            try {
                getTablesCount();
            } catch (SQLException e) {
                Error.error("Tables have not been created.");
            }
        }
    }

    @Override
    public int subMenu() {
        System.out.println("-----Operation for administrator menu-----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. Create all tables");
        System.out.println("2. Delete all tables");
        System.out.println("3. Load from datafile");
        System.out.println("4. Show number of records in each table");
        System.out.println("5. Return to the main menu");
        System.out.print("Enter Your Choice: ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if (choice == 5) return -1;
        else return choice;
    }

    private void createAllTables() throws SQLException {
        System.out.print("Processing...");
        Statement statement = connection.createStatement();
        String sql = "CREATE TABLE category (cid SMALLINT, max SMALLINT, period SMALLINT, PRIMARY KEY(cid))";
        statement.execute(sql);
        sql = "CREATE TABLE libuser (libuid CHAR(10), name CHAR(25), address CHAR(100), cid SMALLINT, " +
                "PRIMARY KEY(libuid), FOREIGN KEY (cid) REFERENCES category(cid))";
        statement.execute(sql);
        sql = "CREATE TABLE book (callnum CHAR(8), title CHAR(30), publish DATE, PRIMARY KEY(callnum))";
        statement.execute(sql);
        sql = "CREATE TABLE copy (callnum CHAR(8), copynum SMALLINT, PRIMARY KEY(callnum, copynum)," +
                "FOREIGN KEY (callnum) REFERENCES book(callnum))";
        statement.execute(sql);
        sql = "CREATE TABLE borrow (libuid CHAR(10), callnum CHAR(8), copynum SMALLINT, checkout DATE, return DATE, " +
                "PRIMARY KEY(libuid, callnum, copynum, checkout), CONSTRAINT fk_copy FOREIGN KEY (callnum, copynum) " +
                "REFERENCES copy(callnum,copynum), CONSTRAINT fk_user FOREIGN KEY (libuid) REFERENCES libuser(libuid))";
        statement.execute(sql);
        sql = "CREATE TABLE authorship (aname CHAR(25), callnum CHAR(8), PRIMARY KEY(aname, callnum), " +
                "FOREIGN KEY (callnum) REFERENCES book(callnum))";
        statement.execute(sql);
        System.out.println("Done! Database is initialized!");
        statement.close();
    }

    private void dropAllTables() throws SQLException {
        System.out.print("Processing...");
        Statement statement = connection.createStatement();
        String sql = "DROP TABLE authorship";
        statement.execute(sql);
        sql = "DROP TABLE borrow";
        statement.execute(sql);
        sql = "DROP TABLE libuser";
        statement.execute(sql);
        sql = "DROP TABLE category";
        statement.execute(sql);
        sql = "DROP TABLE copy";
        statement.execute(sql);
        sql = "DROP TABLE book";
        statement.execute(sql);
        System.out.println("Done! Database is removed!");
        statement.close();
    }

    private void loadData() throws SQLException, FileNotFoundException, ParseException {
        System.out.print("Type in the Source Data Folder Path: ");
        Scanner scanner = new Scanner(System.in);
        String dir = scanner.nextLine();
        //These 4 input files are named category.txt, user.txt, book.txt and check_out.txt
        //category data file
        System.out.print("Processing...");
        scanner = new Scanner(new File(dir + "/category.txt"));
        PreparedStatement statement = connection.prepareStatement("INSERT INTO category (cid, max, period) VALUES (?,?,?)");
        while (scanner.hasNext()) {
            int cid = scanner.nextInt();
            int max = scanner.nextInt();
            int period = scanner.nextInt();
            statement.setInt(1, cid);
            statement.setInt(2, max);
            statement.setInt(3, period);
            statement.executeUpdate();
        }
        statement.close();
        //library data file
        scanner = new Scanner(new File(dir + "/user.txt"));
        statement = connection.prepareStatement("INSERT INTO libuser (libuid, name, address, cid) VALUES (?,?,?,?)");
        while (scanner.hasNextLine()) {
            String[] attribute = scanner.nextLine().split("\t");
            statement.setString(1, attribute[0]);
            statement.setString(2, attribute[1]);
            statement.setString(3, attribute[2]);
            statement.setInt(4, Integer.parseInt(attribute[3]));
            statement.executeUpdate();
        }
        statement.close();
        //book data file
        scanner = new Scanner(new File(dir + "/book.txt"));
        statement = connection.prepareStatement("INSERT INTO book (callnum, title, publish) VALUES (?,?,?)");
        PreparedStatement copyStatement = connection.prepareStatement("INSERT  INTO copy (callnum, copynum) VALUES (?, ?)");
        PreparedStatement authorStatement = connection.prepareStatement("INSERT  INTO authorship (callnum, aname) VALUES (?, ?)");
        while (scanner.hasNextLine()) {
            String[] attribute = scanner.nextLine().split("\t");
            statement.setString(1, attribute[0]);
            copyStatement.setString(1, attribute[0]);
            authorStatement.setString(1, attribute[0]);
            statement.setString(2, attribute[2]);
            statement.setDate(3, new Date(format.parse(attribute[4]).getTime()));
            statement.executeUpdate();
            int copyNum = Integer.parseInt(attribute[1]);
            for (int i = 1; i <= copyNum; i++) {
                copyStatement.setInt(2, i);
                copyStatement.executeUpdate();
            }
            String[] authorList = attribute[3].split(",");
            for (int i = 0; i < authorList.length; i++) {
                authorStatement.setString(2, authorList[i]);
                authorStatement.executeUpdate();
            }
        }
        copyStatement.close();
        authorStatement.close();
        statement.close();
        //checkout data file
        scanner = new Scanner(new File(dir + "/check_out.txt"));
        statement = connection.prepareStatement("INSERT INTO borrow (callnum, copynum, libuid, checkout, return) VALUES (?,?,?,?,?)");
        while (scanner.hasNextLine()) {
            String[] attribute = scanner.nextLine().split("\t");
            statement.setString(1, attribute[0]);
            statement.setInt(2, Integer.parseInt(attribute[1]));
            statement.setString(3, attribute[2]);
            statement.setDate(4, new Date(format.parse(attribute[3]).getTime()));
            if (attribute[4].equals("null")) statement.setNull(5, Types.DATE);
            else statement.setDate(5, new Date(format.parse(attribute[4]).getTime()));
            statement.executeUpdate();
        }
        System.out.println("Done! Data is inputted to the database!");
        statement.close();
    }

    private void getTablesCount() throws SQLException {
        System.out.println("Number of records in each table");
        Statement statement = connection.createStatement();
        String[] tables = new String[]{"category", "libuser", "book", "copy", "borrow", "authorship"};
        for (String table : tables) {
            String sql = "SELECT COUNT(*) FROM " + table;
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            System.out.println(table + ": " + resultSet.getInt(1));
        }
        statement.close();
    }
}
