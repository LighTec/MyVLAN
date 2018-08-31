package FileParse;

import DBUtil.SQLEntry;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileParser_SwitchConfigs implements FileParser_Interface{

    public ArrayList<SQLEntry> parse(File in) throws FileNotFoundException {
        ArrayList<SQLEntry> results = new ArrayList<>();
        Scanner linesc = new Scanner(in);
        int iter = 1;
        while(linesc.hasNext()){
            String line = linesc.nextLine();
            // this denotes the start of a vlan file
            if(line.startsWith("vlan")){
                //System.out.println("Found vlan info! " + line);
                SQLEntry vlanEntry = new SQLEntry();
                vlanEntry.setFileOrigin("Switch config files");
                // removes the "vlan " from the line to parse the number
                String vlanTagStr = line.substring(5);
                vlanTagStr = vlanTagStr.trim();
                int vlanTag = -2;
                try{
                    vlanTag = Integer.parseInt(vlanTagStr);
                }catch (NumberFormatException e){
                    System.err.println("VLAN parse error for switch config @ " + iter);
                }
                if(vlanTag == 1){
                    continue;
                }
                vlanEntry.setVlanTag("" + vlanTag);
                boolean keepGoing = true;
                while(keepGoing){
                    //System.out.println("IN INNER LOOP");
                    iter++;
                    String vlanLine = linesc.nextLine();
                    String[] splitted = vlanLine.split("\\s+");
                    //System.out.println(vlanLine);
                    //System.out.println(splitted[1]);
                    if(splitted.length < 2){
                        continue;
                    }else if(splitted[1].equals("exit")){
                        //System.out.println("FOUND EXIT");
                        keepGoing = false;
                    }else if(splitted[1].startsWith("no")){
                        //System.out.println("FOUND NO");
                        continue;
                    }else if(splitted[1].startsWith("name")){
                        //System.out.println("FOUND NAME");
                        vlanLine.replaceAll("\"", "");
                        vlanEntry.setName(vlanLine.substring(5));
                    }else if(splitted[1].startsWith("ip") && splitted[2].startsWith("addr")){
                        //System.out.println("FOUND IP ADDR AND NETMASK");
                        String trimmedIpNet = vlanLine.substring(11);
                        vlanEntry.setIPAddr(splitted[3]);
                        int netmask = -4;
                        try {
                            netmask = HelperMethods.convertNetmaskToCIDR(InetAddress.getByName(splitted[4]));
                        } catch (UnknownHostException e) {
                            System.err.println("Netmask parse error!");
                        }catch (ArrayIndexOutOfBoundsException a){
                        }
                        vlanEntry.setNetmask("" + netmask);
                    }else{
                        vlanEntry.setComment(vlanEntry.getComment() + vlanLine.trim());
                    }
                }
                //System.out.println("Added VLAN to SQL arraylist!");
                results.add(vlanEntry);
            }
            iter++;
        }
        System.out.println("File " + in.getAbsolutePath() + " Done!");
        return results;
    }

    @Override
    public SQLEntry parse(String in, int iteratr) {
        return null;
    }

    @Override
    public String getName() {
        return "Switch Configs";
    }
}
