package DBUtil.Comparators;

import DBUtil.SQLEntry;

import java.util.Comparator;

/**
 * Sorts the SQLEntries by IP Addresses. Nulls go to bottom of the list.
 * @author Kell Larson <ktech117@gmail.com>
 */
public class IPaddrSort implements Comparator<SQLEntry> {

    public IPaddrSort(){

    }

    @Override
    public int compare(SQLEntry o1, SQLEntry o2) {
        if(o1.getIPAddr() == null && o2.getIPAddr() == null){
            return 0;
        }else if(o1.getIPAddr() == null){
            return -1;
        }else if(o2.getIPAddr() == null){
            return 1;
        }else {
            return o1.getIPAddr().compareTo(o2.getIPAddr());
        }
    }
}
