package DBUtil;

import java.util.Arrays;

public class DatabaseTags {

    private final static String[] columnHeaders = {"Name", "Owner", "IPaddr", "Netmask", "VlanTag", "Zone", "ZoneType", "VRF", "Location", "Comment", "FileOrigin", "id"};

    public static String[] getTagArray(){
        return columnHeaders;
    }

    public static String[] getTagArrayNoID(){
        return Arrays.copyOfRange(columnHeaders, 0 , columnHeaders.length-2);
    }
}
