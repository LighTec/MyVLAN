package FileParse;

import DBUtil.SQLEntry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Given one of four files (DHCP network file, VLAN network file, VLAN tags other file, switch config file) it will parse
 * the plaintext/csv file into SQLEntries, which mimick database rows without being in the database. Recommend after
 * running any of these parse methods to run the arraylist through the sqlconflictor to remove duplicates and to merge
 * duplicates.
 */
public class FileParserMaster {

    private FileParser_VlanTagsOther fpv;
    private FileParser_RouterVlanNetwork fpr;
    private FileParser_DHCPnetworksV2 fpd2;
    private FileParser_DHCPnetworks fpd;
    private FileParser_SwitchConfigs fps;

    public final String[] PARSE_STRINGS = {"DHCP csv file","Router VLAN file","Switch Configs","Vlan Tags txt File"};

    public FileParserMaster(){
    }

    /**
     * Parses the "DHCPNetworks.csv" excel file.
     * @param f
     * @return
     */
    public ArrayList<SQLEntry> parseDHCPFile(File f){
        ArrayList<SQLEntry> entries = new ArrayList<>();
        while (true) {
            try {
                //fpd = new FileParser_DHCPnetworks();
                fpd2 = new FileParser_DHCPnetworksV2(); // new version
                entries.addAll(parseFile(fpd2, f));
                break;
            } catch (Exception e) {
                printErrorTrace(f,"", e);
            }
        }
        return entries;
    }

    /**
     * Parses the returned values, for example, from ssh-ing into 10.31.1.111 and running the command: show ip int brief | include Vlan
     * @param f
     * @return
     */
    public ArrayList<SQLEntry> parseRouterVlanNetworkFile(File f){
        // For the router-vlan-network file
        ArrayList<SQLEntry> entries = new ArrayList<>();
        while (true) {
            try {
                fpr = new FileParser_RouterVlanNetwork();
                entries.addAll(parseFile(fpr, f));
                break;
            } catch (Exception e) {
                printErrorTrace(f,"", e);
            }
        }
        return entries;
    }

    /**
     * Parses the hand-editied "vlan_tags_other.txt" file. If this throws an error, double check your formatting.
     * I tried to make it resilient, but it can only do so much...
     * @param f
     * @return
     */
    public ArrayList<SQLEntry> parseVLANTagsOtherFile(File f){
        ArrayList<SQLEntry> entries = new ArrayList<>();
        while (true) {
            try {
                this.fpv = new FileParser_VlanTagsOther();
                entries.addAll(parseFile(this.fpv, f));
                break;
            } catch (Exception e) {
                printErrorTrace(f,"", e);
            }
        }
        return entries;
    }

    /**
     * Parses switch config files, such as "10.10.220.220.config.hp.HP-Stack.18-06-22", which come from switches.
     * @param f
     * @return
     */
    public ArrayList<SQLEntry> parseSwitchConfigFile(File f) {
        ArrayList<SQLEntry> entries = new ArrayList<>();
        while (true) {
            try {
                fps = new FileParser_SwitchConfigs();
                entries.addAll(fps.parse(f));
                break;
            } catch (Exception e) {
                printErrorTrace(f, "", e);
            }
        }
        return entries;
    }

    public ArrayList<SQLEntry> parseGeneric(File f, String parserString){
        System.out.println(parserString);
        if(parserString.equals(this.PARSE_STRINGS[0])){
            return parseDHCPFile(f);
        }else if(parserString.equals(this.PARSE_STRINGS[1])){
            return parseRouterVlanNetworkFile(f);
        }else if(parserString.equals(this.PARSE_STRINGS[2])){
            return parseSwitchConfigFile(f);
        }else if(parserString.equals(this.PARSE_STRINGS[3])){
            return parseVLANTagsOtherFile(f);
        }else{
            System.err.println("parserString not found.");
            return null;
        }
    }

    /**
     * Prints a stack trace, including the file path being parsed, the name of the parser, and the stack trace for bugixing.
     * @param file
     * @param parserName
     * @param e
     */
    private void printErrorTrace(File file, String parserName, Exception e){
        System.err.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.err.println("Generic error parsing " + parserName + "! Dump:");
        System.err.println("File being parsed: " + file.getAbsolutePath());
        System.err.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        e.printStackTrace();
    }

    /**
     * Runs through each line for parsing files, with an interface to allow less code duplication.
     * Put a file though here from one of the parse____File methods!
     * @param parser
     * @param input
     * @return
     * @throws IOException
     */
    private ArrayList<SQLEntry> parseFile(FileParser_Interface parser, File input) throws IOException {
        int iteratr = 1;
        ArrayList<SQLEntry> entries = new ArrayList<>();
        //Scanner object to scan the files contents
        Scanner fsc = new Scanner(input);
        //Lets user know which file is being processed
        System.out.println("Handling the file " + input.getAbsolutePath());

        //Moves all lines of the file to an array
        while (fsc.hasNext()) {
            //System.out.println("Parsing line " + this.iteratr + "..."); // for testing
            // if the entry is one of the known lines that throws an error, print for debugging and skip
            // if it not a known bad line, parse it.
            String line = fsc.nextLine();
            // star characters '*' denote comments, which should not be parsed.
            if (!line.contains("*")) {
                SQLEntry temp = parser.parse(line, iteratr);
                // if an error, comment, or some other reason for the line not to be parsed to the database,
                // then null is returned. We need to filter out any nulls before writing to db
                if(temp != null) {
                    entries.add(temp);
                    System.out.println("Found VLAN entry!\t\t" + temp);
                }
            } else {
                System.out.println("Comment detected on line " + iteratr + ", skipping...");
            }


            iteratr++;
        }
        System.out.println("Parsing done.");
        return entries;
    }

}
