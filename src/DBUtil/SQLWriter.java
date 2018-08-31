package DBUtil;

import Controller.Main;
import DBUtil.Comparators.IPaddrSort;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Used to write to the SQL database. Will handle all entries, and has conflict resolution built-in, and an option
 * to bypass that if you really want. Also manages editing current entries.
 */
public class SQLWriter {

    // Connection objects
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    // Connection parameters
    private String sqlDatabaseURL = "mysql://localhost/vlan?";
    private String SQLUsername = "kell";
    private String SQLpassword = "Bfk.vlan12345";
    private String targetDB = "vlan";
    private String targetTable = "testing1";

    // ArrayLists of entries
    private ArrayList<SQLEntry> newEntries;
    private ArrayList<SQLEntry> DBEntries;
    private ArrayList<ArrayList<SQLEntry>> conflictedEntries;

    //Database searcher object
    private SQLReader dbs;

    /**
     * Connects the writer and reader to a specified URL with chosen username and password.
     *
     * @param databaseURL URL for database connection
     * @param username    username for database connection
     * @param password    password for database connection
     */
    public SQLWriter(String databaseURL, String username, String password, String targetDb, String targetTble) {
        this.sqlDatabaseURL = databaseURL;
        this.SQLUsername = username;
        this.SQLpassword = password;
        this.targetDB = targetDb;
        this.targetTable = targetTble;
        this.newEntries = null;
        this.dbs = new SQLReader(this.sqlDatabaseURL, this.SQLUsername, this.SQLpassword, this.targetDB, this.targetTable);
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            // uses url, and account username + password.
            this.connect = DriverManager.getConnection("jdbc:" + this.sqlDatabaseURL
                    + "user=" + this.SQLUsername + "&password=" + this.SQLpassword);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects the writer and reader to the test database.
     */
    public SQLWriter() {
        this.newEntries = null;
        this.dbs = new SQLReader(this.sqlDatabaseURL, this.SQLUsername, this.SQLpassword, this.targetDB, this.targetTable);
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            // uses url, and account username + password.
            this.connect = DriverManager.getConnection("jdbc:" + this.sqlDatabaseURL
                    + "user=" + this.SQLUsername + "&password=" + this.SQLpassword);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes an ArrayList of SQLEntry objects to parse into the database, ensuring there are no duplicates
     * within the database (e.g. the same IP addresses)
     *
     * @param entries          New entries for the database
     * @param deleteIdenticals whether or not to automatically delete identical entries between the database and the new entries
     * @return true if conflicts > 0
     */
    public boolean addSQLEntries(ArrayList<SQLEntry> entries, boolean deleteIdenticals) {
        this.newEntries = entries;
        this.conflictedEntries = conflictedEntries();
        if (conflictedEntries.size() > 0) {
            return false;
        }
        writeDataNoConflictCheck(entries);
        return true;
    }

    /**
     * Check all current data for conflicts...
     *
     * @return if conflicts > 0
     */
    public boolean checkDBForConflicts() {
        this.newEntries = new ArrayList<SQLEntry>();
        this.conflictedEntries = conflictedEntries();
        if (conflictedEntries.size() > 0) {
            return false;
        }
        return true;
    }

    /**
     * Edits the database to remove all solved conflicts, and only keeping the good entries. Each good entry matches up
     * to an arraylist of conflicts, and then this method makes sure all of the conflicted entries on that list get
     * deleted, and then the good entry gets added to the database.
     *
     * @param goodEntries     the list of entries from the solved conflicts, each conflict has a good entry here.
     * @param conflictsSolved list of conflicts groups, where a good entry has been selected, or a new entry will replace.
     */
    public void fixConflicts(ArrayList<SQLEntry> goodEntries, ArrayList<ArrayList<SQLEntry>> conflictsSolved) {

        for(ArrayList<SQLEntry> subList: conflictsSolved){
            for(SQLEntry entryTBD : subList){
                System.out.println("Deleting Entry " + entryTBD);
                deleteEntry(entryTBD.getId());
            }
        }
        System.out.println("Adding entries " + goodEntries);
        writeDataNoConflictCheck(goodEntries);
    }

    /**
     * Deletes an SQLEntry from the database based on its ID.
     *
     * @param idToDelete the id integer, in a String. If a non-parsable String is passed, an exception will occur.
     */
    private void deleteEntry(String idToDelete) {
        this.connect();
        int iter = this.dbs.getNextAvailableID();

        // Writes error message if write fails
        try {
            Integer.parseInt(idToDelete);
            System.out.println("ID to be deleted: " + idToDelete);
            statement = connect.createStatement();
            //System.out.println(sqlEntry);
            // PreparedStatements can use variables and are more efficient
            preparedStatement = connect
                    .prepareStatement("delete from " + this.targetTable + " where id=" + idToDelete);
            preparedStatement.executeUpdate();

            if(connect != null){
                connect.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Improper ID number passed when delete attempt executed. ID: " + idToDelete);
            System.err.println("Note: If this is caused by using a user created entry when fixing conflicts, this is normal, and ignore this warning.");
            //e.printStackTrace();
        }
    }

    private void connect(){
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            // uses url, and account username + password.
            this.connect = DriverManager.getConnection("jdbc:" + this.sqlDatabaseURL
                    + "user=" + this.SQLUsername + "&password=" + this.SQLpassword);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Any entries passed will be checked if they are in the database. If they are, they will get deleted.
     * @param entries
     */
    public void deleteAllEntries(ArrayList<SQLEntry> entries){
        this.connect();
        ArrayList<SQLEntry> fixedEntries = new ArrayList<>();
        for(SQLEntry e : entries){
            if(e.getNetmask().equals("")){
                e.setNetmask("-1");
            }
            if(e.getVlanTag().equals("")){
                e.setVlanTag("-1");
            }
            fixedEntries.add(e);
        }
        this.dbs.sortQuery();
        ArrayList<SQLEntry> db = this.dbs.getSQLEntries();
        System.out.println("######################################################");
        for(SQLEntry entry : fixedEntries){
            //System.out.println("Searching for match for " + entry);
            for(SQLEntry dbEntry : db){
                //System.out.println(entry);
                //System.out.println(dbEntry);
                if(entry.equalsIgnoreID(dbEntry)){
                    System.out.println("Deleting Entry:\n" + dbEntry);

                    // Writes error message if write fails
                    String idToDelete = dbEntry.getId();
                    try {
                        Integer.parseInt(idToDelete);
                        System.out.println("ID to be deleted: " + idToDelete);
                        statement = connect.createStatement();
                        //System.out.println(sqlEntry);
                        // PreparedStatements can use variables and are more efficient
                        preparedStatement = connect
                                .prepareStatement("delete from " + this.targetTable + " where id=" + idToDelete);
                        preparedStatement.executeUpdate();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (NumberFormatException e) {
                        System.err.println("Improper ID number passed when delete attempt executed. ID: " + idToDelete);
                        System.err.println("Note: If this is caused by using a user created entry when fixing conflicts, this is normal, and ignore this warning.");
                        //e.printStackTrace();
                    }
                    System.out.println("######################################################");
                }
            }
        }
        if(connect != null){
            try {
                connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the conflicted entries between the new and old entries.
     *
     * @return conflicts
     */
    public ArrayList<ArrayList<SQLEntry>> getConflicts() {
        return this.conflictedEntries;
    }

    private void getDBEntries() {
        dbs.sortQuery(DatabaseTags.getTagArray()[2]); // ip address
        this.DBEntries = dbs.getSQLEntries();
    }


    /**
     * Returns all conflicted SQL Entries, in the form of an Arraylist of conflicted entries, encased within an
     * ArrayList of all conflicts.
     *
     * @return Conflicted entries
     */
    private ArrayList<ArrayList<SQLEntry>> conflictedEntries() {
        //System.out.println("Hello World!");

        // get all entries from database to compare against
        this.dbs.sortQuery("IPaddr");
        this.DBEntries = this.dbs.getSQLEntries();
        //System.out.println("Size of DB: " + this.DBEntries.size());
        // arraylist of arraylists containing conflict groups
        ArrayList<ArrayList<SQLEntry>> results = new ArrayList<>();
        // arraylist for all entries conflicting in a single spot
        ArrayList<SQLEntry> conflictedGroup = new ArrayList<>();
        // entries to check against during the conflict loop
        SQLEntry entry; // current entry comparing against
        SQLEntry lastEntry = null; // last entry, started as null for edge case at beginning where no last entry
        // arraylist of all entries: database and new. Then sort by IP addresses.
        ArrayList<SQLEntry> allentries = new ArrayList<>(this.newEntries);
        allentries.addAll(this.DBEntries);
        allentries.sort(new IPaddrSort());

        if (false) { // for debugging, flip boolean for debug messages
            //System.out.println("Sqeg Foo!");
            System.out.println(allentries.size());
            for (SQLEntry sqegg : allentries) {
                System.out.println("######################################################");
                System.out.println(sqegg);
            }
            System.out.println("######################################################");
        }

        Iterator<SQLEntry> iter = allentries.iterator();
        // whether or not currently in a loop filling a conflict arraylist
        boolean fillingList = false;
        // loops over ALL SQLEntries.
        while (iter.hasNext()) {
            entry = iter.next();
            // check that the entry is not null and that it's ip is not null
            if (entry != null && entry.getIPAddr() != null) {
                // check if the last entry is not null, and that the last and current entries IP addressses match
                if (lastEntry != null && lastEntry.getIPAddr().equals(entry.getIPAddr())) {
                    //System.out.println("Found group!  " + entry.getIPAddr()); // for testing
                    fillingList = true; // we are now filling a conflict list
                    conflictedGroup.add(lastEntry); // add the last entry to the list
                } else if (fillingList) {
                    fillingList = false; // no longer filling the list
                    conflictedGroup.add(lastEntry); // add the last entry to the list before commit to master list
                    results.add(conflictedGroup); // add to master list
                    conflictedGroup = new ArrayList<>(); // reset conflict list for new group
                }
            } else { // if null, or IP address is null, create an arraylist where it is the only element and commit to master list
                conflictedGroup = new ArrayList<>();
                conflictedGroup.add(entry);
                results.add(conflictedGroup);
                entry = null; // make sure to turn entry to null to ensure it cannot be compared against in next looping
            }
            //System.out.println(entry); // for testing
            lastEntry = entry; // overwrite last entry with current one to store for next loop iteration
        }

        ArrayList<ArrayList<SQLEntry>> groupsToRemove = new ArrayList<>();

        // iterates over list, adding any 1-length groups to a seperate list for removal
        for (ArrayList<SQLEntry> remList : results) {
            if (remList.size() == 1) {
                groupsToRemove.add(remList);
            }
        }

        // removes any objects in the result list that are found in the to-remove list
        for (ArrayList<SQLEntry> remover : groupsToRemove) {
            results.remove(remover);
        }
        // We cannot remove objects as Java iterates over them, so this is why it works as a find-all then delete-all
        // system rather than a remove-as-found system.

        // prints out the results
        //System.err.println("Conflicted ip address groups: " + results.size());

        /* // For testing
        FileWriter fw = null;
        try {
            fw = new FileWriter("Output1.out");
            PrintWriter output = new PrintWriter(fw);
            for (ArrayList<SQLEntry> group : results) {
                output.println("GROUP:");
                for (SQLEntry ent : group) {
                    output.println(ent);
                }
            }
            output.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        return results;
    }


    /**
     * Writes data in the form of objects, each DBUtil.SQLEntry object contains all data for one row to be written.
     *
     * @param sqe entries to be written to the database
     */
    public void writeDataNoConflictCheck(ArrayList<SQLEntry> sqe) {
        this.connect();
        int iter = this.dbs.getNextAvailableID();
        // Writes error message if write fails
        try {
            System.out.println("List length: " + sqe.size());

            for (SQLEntry sqlEntry : sqe) {
                statement = connect.createStatement();
                //System.out.println(sqlEntry);
                // PreparedStatements can use variables and are more efficient
                preparedStatement = connect
                        .prepareStatement("insert into   " + Main.TABLE + "  values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                // Parameters start with 1
                preparedStatement.setString(1, sqlEntry.getName());
                preparedStatement.setString(2, sqlEntry.getOwner());
                preparedStatement.setString(3, sqlEntry.getIPAddr());
                preparedStatement.setInt(4, parseNetmask(sqlEntry));
                preparedStatement.setInt(5, parseVlanTag(sqlEntry));
                preparedStatement.setString(6, sqlEntry.getZone());
                preparedStatement.setString(7, sqlEntry.getZoneType());
                preparedStatement.setString(8, sqlEntry.getVRF());
                preparedStatement.setString(9, sqlEntry.getLocation());
                preparedStatement.setString(10, sqlEntry.getComment());
                preparedStatement.setString(11, sqlEntry.getFileOrigin());
                preparedStatement.setInt(12, iter);
                // Write to table
                if (iter % 5 == 0) {
                    System.out.println("Writing row " + iter + " to table...");
                }
                iter++;
                try {
                    preparedStatement.executeUpdate();
                } catch (Exception e) {
                    System.err.println("Write execution error.");
                    System.err.println("Object being written:");
                    System.err.println(sqlEntry);
                    e.printStackTrace();
                }

            }

        } catch (Exception e) {
            System.err.println("Generic SQL write error while writing values " + iter);
        } finally {
            // close the connenction, and do nothing if the object is a null
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (Exception e) {
                System.err.println("SQL write close statement/connection/resultSet error!");
            }
        }
    }

    /**
     * After editing a row in the table viewer, this is called to commit the changes to the database.
     *
     * @param entry the edited row
     * @return if the write was a success
     */
    public boolean writeEdit(SQLEntry entry) {
        this.connect();
        try {

            // gets all database column headers, adds them to a string with ? between to allow a prepared
            // statement to set values to it, then remove the ", " at the end.
            // Yes, it is a complicated way of doing it, but it is clean.
            String setString = "";
            ArrayList<String> tagsWOid = new ArrayList<>();
            for (String s : DatabaseTags.getTagArray()) {
                tagsWOid.add(s);
            }
            // created an arraylist with everything but the ID header
            String deleted = tagsWOid.remove(tagsWOid.size() - 1);
            System.out.println("Deleted tag: " + deleted); // for testing
            for (String s : tagsWOid) {
                setString += s + " = ?, ";
            }
            setString = setString.substring(0, setString.length() - 2);

            // show what the sql statement is
            System.out.println("UPDATE " + this.targetDB + "." + this.targetTable + " SET " + setString + " WHERE " + DatabaseTags.getTagArray()[11] + " = ?");


            statement = connect.createStatement();
            //System.out.println(sqlEntry);
            // PreparedStatements can use variables and are more efficient
            preparedStatement = connect
                    .prepareStatement("UPDATE " + this.targetDB + "." + this.targetTable + " SET " + setString + " WHERE " + DatabaseTags.getTagArray()[11] + " = ?");
            // Parameters start with 1
            preparedStatement.setString(1, entry.getName());
            preparedStatement.setString(2, entry.getOwner());
            preparedStatement.setString(3, entry.getIPAddr());
            preparedStatement.setInt(4, parseNetmask(entry));
            preparedStatement.setInt(5, parseVlanTag(entry));
            preparedStatement.setString(6, entry.getZone());
            preparedStatement.setString(7, entry.getZoneType());
            preparedStatement.setString(8, entry.getVRF());
            preparedStatement.setString(9, entry.getLocation());
            preparedStatement.setString(10, entry.getComment());
            preparedStatement.setString(11, entry.getFileOrigin());
            preparedStatement.setInt(12, parseID(entry));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // did not work, print stack trace and return false to tell GUI it failed.
            return false;
        }
        return true;
    }

    public void deleteEntry(SQLEntry toBeDeleted){
        String id = toBeDeleted.getId();
        this.deleteEntry(id);
        System.out.println("ID of entry being deleted: " + id);
    }

    private int parseNetmask(SQLEntry e) {
        if (e.getNetmask().equals("")) {
            return -1;
        } else {
            try {
                return Integer.parseInt(e.getNetmask());
            } catch (NumberFormatException ex) {
                System.err.println("Write error on netmask!");
                ex.printStackTrace();
            }
            return 0;
        }

    }

    private int parseVlanTag(SQLEntry e) {
        if (e.getVlanTag().equals("")) {
            return -1;
        } else {
            try {
                return Integer.parseInt(e.getVlanTag());
            } catch (NumberFormatException ex) {
                System.err.println("Write error on vlan tag!");
                ex.printStackTrace();
            }
            return 0;
        }
    }

    private int parseID(SQLEntry e) {
        if (e.getId().equals("")) {
            return -1;
        } else {
            try {
                return Integer.parseInt(e.getId());
            } catch (NumberFormatException ex) {
                System.err.println("Write error on id!");
                ex.printStackTrace();
            }
            return 0;
        }
    }
}