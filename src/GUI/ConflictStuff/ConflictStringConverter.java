package GUI.ConflictStuff;

import DBUtil.SQLEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * @author Kell Larson <ktech117@gmail.com>
 */
public class ConflictStringConverter {

    public static String getStringFromSQLArrList(ArrayList<SQLEntry> entrylist) {
        return "IP address: " + entrylist.get(0).getIPAddr() + "   Conflicts: " + entrylist.size();
    }

    public static ArrayList<SQLEntry> getSQLArrListFromString(String s, ObservableList<ArrayList<SQLEntry>> compareList) {
        ArrayList<SQLEntry> returnedList = new ArrayList<>();
        int ipBegin = 12; // the fixed start of the string where the IP address is
        int ipEnd = s.indexOf("Conflicts:") - 3; // 3 spaces before the word, do not want them
        String IPaddrToMatch = s.substring(ipBegin, ipEnd); // the IP addr to match against
        //System.out.println("######################################################");
        //System.out.println("!" + IPaddrToMatch + "!"); // for testing

        for(ArrayList<SQLEntry> subList : compareList){
            //System.out.println(subList);
            // if the IP addresses match for the first entry, return this list
            if(subList.get(0).getIPAddr() != null){
                if(subList.get(0).getIPAddr().equals(IPaddrToMatch)){
                    returnedList = subList;
                }
            }else{
                //System.err.println("Null detected!");
            }

        }
        if(returnedList.size() == 0){
            System.err.println("List not found from string given in ConflictStringConverter");
        }
        //System.out.println(returnedList); // for testing
        return returnedList;
    }

    public static ObservableList<String> getStringsFromSQLArrListOfArrLists(ObservableList<ArrayList<SQLEntry>> superList) {
        ArrayList<String> stringList = new ArrayList<>();
        for (ArrayList<SQLEntry> subList : superList) {
            stringList.add(getStringFromSQLArrList(subList));
        }
        return FXCollections.observableArrayList(stringList);
    }
}
