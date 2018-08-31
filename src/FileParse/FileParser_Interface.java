package FileParse;

import DBUtil.SQLEntry;

public interface FileParser_Interface {
    public SQLEntry parse(String in, int iteratr);

    public String getName();
}
