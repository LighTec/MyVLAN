package GUI.NewFileStuff;

import FileParse.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

import java.util.Arrays;

/**
 * A modified version of the PortDropdownMenu for javaFX.
 * Used to pick which type of parser will be used when selecting a file to be parsed.
 * @author kell-gigabyte
 */
public class PortDropdownMenuFX extends ComboBox<FileParser_Interface> {

    /**
     * WARNING: CALL THIS METHOD TO INITIALIZE THE COMBO BOX. failing to do so
     * will result in an empty combo box, and an unhappy program.
     */
    public void refreshMenu() {
        ObservableList list = FXCollections.observableArrayList();
        FileParser_Interface[] parsers = {new FileParser_DHCPnetworks(), new FileParser_RouterVlanNetwork(), new FileParser_SwitchConfigs(), new FileParser_VlanTagsOther()};
        list.addAll(Arrays.asList(parsers));
        this.setItems(list);
        //this.getSelectionModel().selectFirst();              // causes NullPointerExceptions when used, thou are warned
        this.setConverter(new ParserConverter());
    }
}
