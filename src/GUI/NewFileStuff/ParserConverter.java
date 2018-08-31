package GUI.NewFileStuff;

import FileParse.*;
import javafx.util.StringConverter;

/**
 * Used to allow SerialPort object to be represented in a JavaFX combo box.
 * @author kell-gigabyte
 */
public class ParserConverter extends StringConverter<FileParser_Interface>{

    @Override
    public String toString(FileParser_Interface fpi) {
        return fpi.getName();
    }

    @Override
    public FileParser_Interface fromString(String string) {
        if(string.equals("Switch Configs")){
            return new FileParser_SwitchConfigs();
        }else if(string.equals("Vlan Tags txt File")){
            return new FileParser_VlanTagsOther();
        }else if(string.equals("DHCP csv file")){
            return new FileParser_DHCPnetworks();
        }else if(string.equals("Router VLAN file")){
            return new FileParser_RouterVlanNetwork();
        }else{
            System.err.println("unknown parser!");
            try{
                throw new UnsupportedOperationException();
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }


        }
    }
    
}
