package GUI.CompareCSV;

import DBUtil.Comparators.IPaddrSort;
import DBUtil.DatabaseTags;
import DBUtil.SQLEntry;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Kell Larson <ktech117@gmail.com>
 */
public class DHCPComparer {

    private boolean debug = false;

    private ArrayList<SQLEntry> db;
    private ArrayList<SQLEntry> dhcp;
    private ArrayList<SQLEntry> diff;
    private String outputLoc;

    public DHCPComparer(ArrayList<SQLEntry> DB, ArrayList<SQLEntry> DHCPFile, String outputLocation) {
        this.db = DB;
        this.db.sort(new IPaddrSort());
        this.dhcp = DHCPFile;
        this.dhcp.sort(new IPaddrSort());
        this.diff = new ArrayList<>();
        this.outputLoc = outputLocation;

        ArrayList<SQLEntry> toRemove = new ArrayList<>();
        for(SQLEntry entry : this.dhcp){
            if (entry.getIPAddr() == null) {
                toRemove.add(entry);
            }else if(entry.getIPAddr().isEmpty()){
                toRemove.add(entry);
            }else{
                entry.setFileOrigin("DHCP csv file");
            }
        }
        for(SQLEntry entry : toRemove){
            this.dhcp.remove(entry);
        }

        toRemove = new ArrayList<>();
        for(SQLEntry entry : this.db){
            if (entry.getIPAddr() == null) {
                toRemove.add(entry);
            }else if(entry.getIPAddr().isEmpty()){
                toRemove.add(entry);
            }else{
                String fo = entry.getFileOrigin();
                entry.setFileOrigin("Database, from " + fo);
            }
        }
        for(SQLEntry entry : toRemove){
            this.db.remove(entry);
        }
    }

    /**
     * If true, prints out lots of debug information.
     *
     * @param debug whether or not to print debug messages
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void parseDifferences() {
        SQLEntry dhcpentry;
        SQLEntry dbentry;
        if (this.debug) { // for debugging, flip boolean for debug messages
            for (SQLEntry sqegg : dhcp) {
                System.out.println("######################################################");
                System.out.println(sqegg);
            }
            System.out.println("######################################################");
        }

        Iterator<SQLEntry> iter = this.dhcp.iterator();
        while (iter.hasNext()) {
            dhcpentry = iter.next();
            Iterator<SQLEntry> looper = this.db.iterator();
            boolean firstTime = true;
            while (looper.hasNext()) {
                dbentry = looper.next();
                if (dhcpentry != null && dhcpentry.getIPAddr() != null && dbentry != null && dbentry.getIPAddr() != null) {
                    if (dbentry.getIPAddr().equals(dhcpentry.getIPAddr())) {
                        if (firstTime) {
                            this.diff.add(dhcpentry);
                            firstTime = false;
                        }
                        this.diff.add(dbentry);
                    }
                }
            }
            this.SplitterLine();
        }
    }

    private void SplitterLine() {
        SQLEntry entry = new SQLEntry();
        entry.setName("--------");
        entry.setIPAddr("--------");
        entry.setZone("--------");
        entry.setZoneType("--------");
        entry.setFileOrigin("--------");
        entry.setComment("--------");
        entry.setVRF("--------");
        entry.setFileOrigin("--------");
        entry.setOwner("--------");
        entry.setNetmask("--------");
        entry.setVlanTag("--------");
        entry.setLocation("--------");
        this.diff.add(entry);
    }

    /**
     * Writes the difference between the given csv file and the database to a second, different csv file,
     * whose filepath had been given when instantiating this object.
     */
    public void writeDiff() {
        if (this.diff.size() > 0) {
            writeStringList();
        } else {
            System.out.println("List of size zero attempted to be written, so nothing has been written.");
        }
    }

    /**
     * Writes the arraylist to a given file.
     */
    private void writeStringList() {
        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(this.outputLoc));

            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader(DatabaseTags.getTagArrayNoID()));

            for(SQLEntry e : this.diff){
                csvPrinter.printRecord(e.getValuesNoID());
            }
            csvPrinter.flush();
            csvPrinter.close();
            writer.flush();
            writer.close();
        } catch (IOException e) {
        }
    }
}
