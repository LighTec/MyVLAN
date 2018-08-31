package DBUtil;

import Controller.Main;
import com.sun.rowset.CachedRowSetImpl;

import java.sql.*;
import java.util.ArrayList;

/**
 * Used to read from the database, executing specific filter and sort functions, too.
 * @author Kell Larson <ktech117@gmail.com>
 */
public class SQLReader {

    private ArrayList<SQLEntry> sqem;
    private ResultSet rs;

    //Connection
    private Connection conn = null;

    // Connection parameters
    private String sqlDatabaseURL = "mysql://localhost/vlan?";
    private String SQLUsername = "kell";
    private String SQLpassword = "Bfk.vlan12345";
    private String targetDB = "vlan";
    private String targetTable = "testing1";

    public SQLReader(String DbUrl, String username, String password, String targetDb, String targetTble) {

        this.sqlDatabaseURL = DbUrl;
        this.SQLUsername = username;
        this.SQLpassword = password;
        this.targetDB = targetDb;
        this.targetTable = targetTble;

        this.sqem = new ArrayList<>();
        rs = null;
    }

    /**
     * Uses testing database url, username and password
     */
    public SQLReader(){
        this.sqem = new ArrayList<>();
        rs = null;
    }

    public ArrayList<SQLEntry> getSQLEntries() {
        return this.sqem;
    }

    public void printSQLEntries(){
        for (SQLEntry entry : this.sqem) {
            System.out.println(entry);
        }
    }

    /**
     * selects * from the given table, no order or filter given.
     */
    public void sortQuery() {
        try {
            this.rs = dbExecuteQuery("SELECT * FROM  " + Main.TABLE + " ;");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        parseSQLEntriesFromResultSet();
    }

    /**
     * selects * from the given table, with a sort and filter function.
     * @param sortFunc
     * @param filterFunc
     */
    public void sortQuery(String sortFunc, String filterFunc) {
        try {
            this.rs = dbExecuteQuery("SELECT * FROM " + Main.TABLE + " WHERE " + filterFunc + " ORDER BY " + sortFunc + ";");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        parseSQLEntriesFromResultSet();
    }

    /**
     * selects * from the given table, with a sort function.
     * @param sortFunc
     */
    public void sortQuery(String sortFunc) {
        try {
            this.rs = dbExecuteQuery("SELECT * FROM " + Main.TABLE + " ORDER BY " + sortFunc + ";");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        parseSQLEntriesFromResultSet();
    }

    /**
     * turns a ResultSet from the database into a list of SQLEntry objects.
     */
    private void parseSQLEntriesFromResultSet() {
        int iter = 1;
        this.sqem.clear();
        try {
            for (iter = 1; this.rs.next(); iter++) {

                SQLEntry sqe = new SQLEntry();

                sqe.setName(this.rs.getString(1));
                sqe.setOwner(this.rs.getString(2));
                sqe.setIPAddr(this.rs.getString(3));
                sqe.setNetmask("" + this.rs.getInt(4));
                sqe.setVlanTag("" + this.rs.getInt(5));
                sqe.setZone(this.rs.getString(6));
                sqe.setZoneType(this.rs.getString(7));
                sqe.setVRF(this.rs.getString(8));
                sqe.setLocation(this.rs.getString(9));
                sqe.setComment(this.rs.getString(10));
                sqe.setId("" + this.rs.getInt(12));

                this.sqem.add(sqe);
            }

        } catch (SQLException e) {
            System.err.println("SQL error parsing ResultSet @ pull " + iter);
            e.printStackTrace();
        }
    }

    /**
     * Returns the next available ID from the database for adding new entries, so ID's within the database will not conflict.
     * @return
     */
    public int getNextAvailableID(){
        try {
            ResultSet newID = dbExecuteQuery("SELECT MAX(ID) FROM  " + Main.TABLE + " ;");
            newID.next();
            int newid = newID.getInt(1);
            System.out.println("ID number chosen for new entry: " + newid);
            return newid + 1;
        } catch (SQLException e) {
            System.err.println("Attempted to get the next ID for a new entry, but failed.");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("SQL class not found :(");
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Connect to DB
     * Created by ONUR BASKIRT on 22.02.2016.
     * Modified by Kell Larson, on 25.6.2018.
     */
    private void dbConnect() throws SQLException, ClassNotFoundException {
        //Setting Oracle JDBC Driver
        try {
            //Declare JDBC Driver
            String JDBC_DRIVER = "com.mysql.jdbc.Driver";
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver??");
            e.printStackTrace();
            throw e;
        }

        System.out.println("MySQL JDBC Driver Registered!");

        //Establish the Oracle Connection using Connection String
        try {
            conn = DriverManager.getConnection("jdbc:" + this.sqlDatabaseURL
                    + "user=" + this.SQLUsername + "&password=" + this.SQLpassword);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console" + e);
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Close Connection
     * Created by ONUR BASKIRT on 22.02.2016.
     * Modified by Kell Larson, on 25.6.2018.
     */
    private void dbDisconnect() throws SQLException {
        if (this.conn != null && !this.conn.isClosed()) {
            this.conn.close();
        }
    }

    /**
     * DB Execute Query Operation
     * Created by ONUR BASKIRT on 22.02.2016.
     * Modified by Kell Larson, on 25.6.2018.
     */
    private ResultSet dbExecuteQuery(String queryStmt) throws SQLException, ClassNotFoundException {
        //Declare statement, resultSet and CachedResultSet as null
        Statement stmt = null;
        ResultSet resultSet = null;
        CachedRowSetImpl crs;
        try {
            //Connect to DB (Establish Oracle Connection)
            dbConnect();
            System.out.println("Select statement: " + queryStmt + "\n");

            //Create statement
            stmt = conn.createStatement();

            //Execute select (query) operation
            resultSet = stmt.executeQuery(queryStmt);

            //CachedRowSet Implementation
            //In order to prevent "java.sql.SQLRecoverableException: Closed Connection: next" error
            //We are using CachedRowSet
            crs = new CachedRowSetImpl();
            crs.populate(resultSet);
        } catch (SQLException e) {
            System.out.println("Problem occurred at executeQuery operation : " + e);
            throw e;
        } finally {
            if (resultSet != null) {
                //Close resultSet
                resultSet.close();
            }
            if (stmt != null) {
                //Close Statement
                stmt.close();
            }
            //Close connection
            dbDisconnect();
        }
        //Return CachedRowSet
        return crs;
    }

    /**
     * DB Execute Update (For Update/Insert/Delete) Operation
     * Created by ONUR BASKIRT on 22.02.2016.
     * Modified by Kell Larson, on 25.6.2018.
     */
    private void dbExecuteUpdate(String sqlStmt) throws SQLException, ClassNotFoundException {
        //Declare statement as null
        Statement stmt = null;
        try {
            //Connect to DB (Establish Oracle Connection)
            dbConnect();
            //Create Statement
            stmt = this.conn.createStatement();
            //Run executeUpdate operation with given sql statement
            stmt.executeUpdate(sqlStmt);
        } catch (SQLException e) {
            System.out.println("Problem occurred at executeUpdate operation : " + e);
            throw e;
        } finally {
            if (stmt != null) {
                //Close statement
                stmt.close();
            }
            //Close connection
            dbDisconnect();
        }
    }
}
