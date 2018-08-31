package FileParse;

import DBUtil.SQLEntry;

public class FileParser_VlanTagsOther implements FileParser_Interface {

    public FileParser_VlanTagsOther() {
    }

    public SQLEntry parse(String in, int iteratr) {
        try {
            SQLEntry results = new SQLEntry();
            results.setFileOrigin("VLAN tags other file");
            //Split string into tokens with whitespace in between
            String[] splitted = in.split("\\s+");
            //check for don't use comment
            if (in.contains("dont use")) {
                //System.out.println("Don't use detected @ " + this.iteratr);
                try {
                    int vlantag = Integer.parseInt(splitted[2]);
                    results.setVlanTag("" + vlantag);
                    results.setComment("Do not use: Processed as: \t" + in);
                } catch (NumberFormatException e) {
                    System.err.println("Don't use VLAN net # error");
                }
            }
        /*
        splitted will break up the file into nifty entries: (if the text file actually has everything in it right)
        index 0 = name
        index 1 = owner
        index 2 = IPaddr + netmask (if applicable, if not instead is "space")
        index 3 = "tag", can throw away
        index 4 = the VLAN tag
        index 5 = name again
        index 6 = IPaddr + netmask again (if applicable, if not is empty)
         */

        /*
        Smart parse script, tries to identify all entries even with missing values and changing formats,
         */

            // Incrementer over the splitted string array.
            int splitInc = 0;
            // First entry is always owner, with exception to the "don't use" case hardcoded above
            results.setName(splitted[splitInc]);
            splitInc++;
            // if there is a period in the next entry, skip it. if not, then it's the name. Used in case no name specified.
            // Reasoning: IPv4 addresses use periods to separate each 8 bytes in the address, but names do not have periods.
            // Also skips "space" in case no name AND no IP are specified. Name should not be "space".
            // If your parents named you "space", I'm sorry...
            if (!splitted[splitInc].contains(".") && !splitted[splitInc].contains("space")) {
                results.setOwner(splitted[splitInc]);
                splitInc++;
            }
            // When "space" is detected here, it means there is no IP.
            if (!splitted[splitInc].equals("space")) {
                //System.out.println("IPaddr detected, parsing..."); // for testing
                // get ipaddr and netmask from next entry
                String ipAndMask = splitted[splitInc];
                // find netmask '/' symbol, then split up IP and Netmask into separate entries via finding the symbol
                int splitter = ipAndMask.indexOf('/');
                //System.out.println("Netmask line: " + splitter);
                // Check to ensure there is a netmask (if it cannot find '/', then splitter = -1)
                if (splitter > 0) {
                    String ipAddr = ipAndMask.substring(0,splitter);
                    results.setIPAddr(ipAddr);
                    //System.out.print(sqe.getIPAddr() + "\t");
                    String netmask = ipAndMask.substring(splitter + 1);
                    // in case the netmask has been entered as /xx, which cannot be parsed as a number
                    if (netmask.contains("x")) {
                        results.setNetmask("-3");
                    } else {
                        // just in case some weird non-numeric netmask has been typed, let user know and send -4 to database
                        try {
                            int mask = Integer.parseInt(netmask);
                            results.setNetmask("" + mask);
                        } catch (NumberFormatException e) {
                            System.err.println("Netmask parse error @ " + iteratr);
                            results.setNetmask("-4");
                        }
                    }
                } else {
                    // If there is an IP, but no netmask. Does not check if IP address is valid, in case it has a .xx in it.
                    results.setIPAddr(ipAndMask);
                    results.setNetmask("-2");
                }
/*                  //BROKEN - does not work due to pivot
            // Check if IPAddr and Netmask are the same on the same line for a sanity check
            if (!splitted[2].equals(splitted[6])) {
                sqe.setComment("IPaddr/Netmasks do not match: " + splitted[2] + " " + splitted[6]);
            }
        }
        // Sanity check on names
        if(!splitted[0].equals(splitted[5])){
            sqe.setComment(sqe.getComment() + "\tNames do not match: " + splitted[0] + " " + splitted[5]);
        }
*/
            }
            // VLAN tag finder
            try {
                for (int i = 0; i < splitted.length; i++) {
                    // VLAN tag is always 1 entry after "tag"
                    if (splitted[i].equals("tag")) {
                        int vlantag = Integer.parseInt(splitted[i + 1]);
                        results.setVlanTag("" + vlantag);
                        // gets Zone tag, which is usually name but could be slightly different
                        results.setZoneType(splitted[i + 2]);
                    }
                }
                // if the VLAN tag is not a number for some reason, or if it does not parse as an integer
            } catch (NumberFormatException e) {
                results.setVlanTag("-2");
            }
            return results;
        }catch(Exception e){
            System.err.println("Error parsing at line " + iteratr + ", skipping and returning null.");
            return null;
            }
    }

    @Override
    public String getName() {
        return "Vlan Tags txt File";
    }
}
