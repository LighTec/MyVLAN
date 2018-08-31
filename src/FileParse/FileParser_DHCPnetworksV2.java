package FileParse;

import DBUtil.SQLEntry;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * Parses the DHCPNetworks excel file.
 */
public class FileParser_DHCPnetworksV2 implements FileParser_Interface {

    public SQLEntry parse(String in, int iteratr) {
        try {
            SQLEntry sqe = new SQLEntry();
            sqe.setFileOrigin("DHCP network csv file");

            Scanner crawler = new Scanner(in);
            crawler.useDelimiter(",");

            String[] splitted = new String[175];

            for(int crawlerIter = 0; crawler.hasNext(); crawlerIter++){
                String s = crawler.next();
                if(s.length() > 0){
                    s = s.trim();
                    if(s.charAt(0) == '"'){
                        if(s.equals("\"")){
                            s = "";
                        }else{
                            s = s.substring(1, s.length() - 1);
                        }
                    }
                }
                splitted[crawlerIter] = s;
            }

            sqe.setIPAddr(splitted[1]);
            int netmask = -4;
            if(!splitted[2].isEmpty()){
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
                        for(String s :splitted){
                            System.out.println(s);
                        }
                    }
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
