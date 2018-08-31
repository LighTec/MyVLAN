package DBUtil.Comparators;

import java.util.Comparator;

/**
 * @author Kell Larson <ktech117@gmail.com>
 */
public class IntegerSort implements Comparator<String> {

    public IntegerSort(){

    }

    @Override
    public int compare(String o1, String o2) {
        int nm1 = -1;
        int nm2 = -1;
        try {
            nm1 = Integer.parseInt(o1);
            nm2 = Integer.parseInt(o2);
        } catch (NumberFormatException e) {
            System.err.println("ID sort error!");
            return -1;
        }
        return Integer.compare(nm1, nm2);
    }
}
