package FileParse;

import DBUtil.SQLEntry;

public class FileParser_RouterVlanNetwork implements FileParser_Interface {

    public FileParser_RouterVlanNetwork() {
    }

    public SQLEntry parse(String in, int iteratr) {
        if(in.contains("*")){
            return null;
        }
        SQLEntry results = new SQLEntry();
        results.setFileOrigin("Router VlAN network file");
        //Split string into tokens with whitespace in between
        String[] splitted = in.split("\\s+");
        String vlanTagUnformatted = splitted[0];
        String vlanTag = "-2";
        // Just in case there is an error parsing the VLAN tag into an integer
        try{
            vlanTagUnformatted = vlanTagUnformatted.replace("Vlan","");
            vlanTag = "" + Integer.parseInt(vlanTagUnformatted);
        }catch (NumberFormatException e){
            System.err.println("VLAN tag parse error for routervlannetwork file.");
            return null;
        }
        results.setVlanTag("" + vlanTag);
        if(!splitted[1].equals("unassigned")){
            results.setIPAddr(splitted[1]);
        }
        results.setFileOrigin("Router network file");
        return results;
    }

    @Override
    public String getName() {
        return "Router VLAN file";
    }
}
