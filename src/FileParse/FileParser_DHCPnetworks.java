package FileParse;

import DBUtil.SQLEntry;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;

/**
 * Parses the DHCPNetworks excel file. Note: there is a new version, please use that. It properly parses .csv files, this
 * one does some hacky moves instead, causing some bugs.
 * @deprecated
 */
public class FileParser_DHCPnetworks implements FileParser_Interface {
    public SQLEntry parse(String in, int iteratr) {
        String[] splittedOriginal = {"nah"};
        try {
            in = in.replace(",", ",N_ZZZABCA");
            SQLEntry sqe = new SQLEntry();
            sqe.setFileOrigin("DHCP network csv file");

            String[] splitted = in.split(",");

            //System.out.println("Line " + iteratr + " Array length:" + splitted.length);
            for(int i = 0; i < splitted.length; i++){
                splitted[i] = splitted[i].trim();
                splitted[i] = splitted[i].replace("N_ZZZABCA", "");
            }
            splittedOriginal = splitted.clone();
            sqe.setIPAddr(splitted[1]);
            int netmask = -4;
            try {
                // if the netmask is properly set, like 8, it can just be parsed
                netmask = Integer.parseInt(splitted[2]);
            } catch (NumberFormatException e) {
                // if the netmask is more like 255.255.255.0, we have to do this
                try {
                    InetAddress addr = InetAddress.getByName(splitted[2]);
                    netmask = HelperMethods.convertNetmaskToCIDR(addr);
                } catch (Exception f) {
                    System.err.println("Netmask Parse Error!\t" + iteratr);
                }
            }
            sqe.setNetmask("" + netmask);
            sqe.setComment(splitted[8]);

            // only "network" rows have these attributes
            if(splitted[0].equals("network")){
                int vlt = -2;
                try {
                    vlt = Integer.parseInt(splitted[59]);
                } catch (NumberFormatException e) {
                }
                sqe.setVlanTag("" + vlt);
                sqe.setLocation(splitted[52]);
                sqe.setZoneType(splitted[55]);
                sqe.setVRF(splitted[57]);
                sqe.setZone(splitted[63]);
            }


            //System.out.print("Line: " + iteratr + "\t");
            //System.out.println(sqe);
            sqe.setFileOrigin("DHCP Networks csv file");

            return sqe;
        } catch (Exception e) {
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            for(String strx : splittedOriginal){
                System.out.print(strx + "\t");
            }
            System.out.println("\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            String errorS = errors.toString();
            System.err.println("Error parsing at line " + iteratr + ", skipping and returning null.\t" + errorS);
            try{
                Thread.sleep(3000);
            }catch(Exception z){
            }
            return null;
        }
    }

    @Override
    public String getName() {
        return "DHCP csv file";
    }
}
