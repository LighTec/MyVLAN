package GUI;

import DBUtil.SQLEntry;
import DBUtil.SQLWriter;
import javafx.scene.control.TableColumn;

/**
 * @author Kell Larson <ktech117@gmail.com>
 *     Handles all table edits within the GUI, telling the backend to save the change, then edit the database to also
 *     have the same change. Is a seperate class, in order to clean up the MainWindow class.
 */
public class TableHandler {

    SQLWriter dbw;

    public TableHandler(SQLWriter writer) {
        this.dbw = writer;
    }

    public void handleName(TableColumn.CellEditEvent<SQLEntry, String> t) {
        System.out.println("Name being edited.");
        SQLEntry entry = (t.getTableView().getItems()
                .get(t.getTablePosition().getRow()));
        System.out.println("Old: " + entry.getName());
        entry.setName(t.getNewValue());
        System.out.println("New: " + entry.getName());
        this.dbw.writeEdit(entry);

    }

    public void handleOwner(TableColumn.CellEditEvent<SQLEntry, String> t) {
        System.out.println("Owner being edited.");
        SQLEntry entry = (t.getTableView().getItems()
                .get(t.getTablePosition().getRow()));
        entry.setOwner(t.getNewValue());
        this.dbw.writeEdit(entry);
    }

    public void handleIPaddr(TableColumn.CellEditEvent<SQLEntry, String> t) {
        System.out.println("IP Address being edited.");
        SQLEntry entry = (t.getTableView().getItems()
                .get(t.getTablePosition().getRow()));
        entry.setIPAddr(t.getNewValue());
        this.dbw.writeEdit(entry);
    }

    public void handleNetmask(TableColumn.CellEditEvent<SQLEntry, String> t) {
        System.out.println("Netmask being edited.");
        SQLEntry entry = (t.getTableView().getItems()
                .get(t.getTablePosition().getRow()));
        entry.setNetmask(t.getNewValue());
        this.dbw.writeEdit(entry);
    }

    public void handleVlanTag(TableColumn.CellEditEvent<SQLEntry, String> t) {
        System.out.println("VLan Tag being edited.");
        SQLEntry entry = (t.getTableView().getItems()
                .get(t.getTablePosition().getRow()));
        entry.setVlanTag(t.getNewValue());
        this.dbw.writeEdit(entry);
    }

    public void handleZone(TableColumn.CellEditEvent<SQLEntry, String> t) {
        System.out.println("Zone being edited.");
        SQLEntry entry = (t.getTableView().getItems()
                .get(t.getTablePosition().getRow()));
        entry.setZone(t.getNewValue());
        this.dbw.writeEdit(entry);
    }

    public void handleZoneType(TableColumn.CellEditEvent<SQLEntry, String> t) {
        System.out.println("Zone Type being edited.");
        SQLEntry entry = (t.getTableView().getItems()
                .get(t.getTablePosition().getRow()));
        entry.setZoneType(t.getNewValue());
        this.dbw.writeEdit(entry);
    }

    public void handleVRF(TableColumn.CellEditEvent<SQLEntry, String> t) {
        System.out.println("VRF being edited.");
        SQLEntry entry = (t.getTableView().getItems()
                .get(t.getTablePosition().getRow()));
        entry.setVRF(t.getNewValue());
        this.dbw.writeEdit(entry);
    }

    public void handleLocation(TableColumn.CellEditEvent<SQLEntry, String> t) {
        System.out.println("Location being edited.");
        SQLEntry entry = (t.getTableView().getItems()
                .get(t.getTablePosition().getRow()));
        entry.setLocation(t.getNewValue());
        this.dbw.writeEdit(entry);
    }

    public void handleComment(TableColumn.CellEditEvent<SQLEntry, String> t) {
        System.out.println("Comment being edited.");
        SQLEntry entry = (t.getTableView().getItems()
                .get(t.getTablePosition().getRow()));
        entry.setComment(t.getNewValue());
        this.dbw.writeEdit(entry);
    }
}
