package GUI.ConflictStuff;

import DBUtil.Comparators.IntegerSort;
import DBUtil.SQLEntry;
import GUI.CellEdit;
import GUI.SQLEntryCreator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.Notifications;

import java.util.ArrayList;

/**
 * The popup GUI used to resolve conflicts within the DB (2 seperate entries with the same IP addresses for example)
 *
 * @author Kell Larson <ktech117@gmail.com>
 */

public class ConflictFixer {

    private ArrayList<ArrayList<SQLEntry>> originalConflicts; // all conflicts from the SQLWriter class
    private ObservableList<ArrayList<SQLEntry>> conflicts; // observable list of the original conflicts
    private ObservableList<String> conflictStrings; // converted string version of conflicts
    private ListView<String> conflictGroupList; // listView of conflictStrings

    private ObservableList<SQLEntry> solvedConflicts;   // Paired with the list below to remove entries from DB
    private ObservableList<ArrayList<SQLEntry>> solvedConflictGroups;

    private ObservableList<SQLEntry> currentConflicts; // list shown on the right list, a group from the conflicts lists
    private TableView<SQLEntry> IPaddrGroupView; // list of currentConflicts

    public ConflictFixer(ArrayList<ArrayList<SQLEntry>> originalConflicts) {
        this.originalConflicts = originalConflicts;
        SQLEntry temp1 = new SQLEntry();
        ArrayList<SQLEntry> tempList = new ArrayList<>();
        tempList.add(temp1);
        this.currentConflicts = FXCollections.observableArrayList(tempList);
        this.solvedConflicts = FXCollections.observableArrayList();
        this.solvedConflictGroups = FXCollections.observableArrayList();
    }

    // various hex color codes for styling
    private final String hexGrey1 = "4A444B";
    private final String hexRed = "BA0101";
    private final String hexOffWhite = "FFFFF0";
    private final String hexGrey2 = "78866B";
    private final String hexBlue = "90AFFF";

    public void run() {
        createStage();
    }

    private Node confirmButton; // confirm button

    private void createStage() {         // creates the GUI for the popup
        Dialog dialog = new Dialog<>();
        dialog.setTitle("Conflict Resolver");
        //dialog.setHeaderText("Hello, I'd just like you to let me know about your LED strip.");

        ButtonType confirmButtonType = new ButtonType("Enter", ButtonBar.ButtonData.APPLY);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType);
        dialog.getDialogPane().setStyle("-fx-background-color: #" + this.hexBlue + ";");

        HBox listSplitterBox = new HBox();
        // add list of conflicts, and the VBox
        VBox entryPickBox = new VBox();
        // add list of individual conflicts as SQLEntry gridpanes
        // add HBox with buttoms
        HBox buttonBox = new HBox();

        Button selectBtn = new Button("Select");
        selectBtn.setOnAction(event ->{
            SQLEntry entrySelected = this.IPaddrGroupView.getSelectionModel().getSelectedItem();
            if(entrySelected != null) {
                ArrayList<SQLEntry> groupSelected = ConflictStringConverter.getSQLArrListFromString(this.conflictGroupList.getSelectionModel().getSelectedItem(), this.conflicts);
                this.solvedConflicts.add(entrySelected);
                this.solvedConflictGroups.add(groupSelected);
                this.conflicts.remove(groupSelected);
                //System.out.println("######################################################");
                //System.out.println("Object selected: " + entrySelected);
                //System.out.println("######################################################");
                //System.out.println("Conflict group solved: ");
                //System.out.println(groupSelected);
                //System.out.println("######################################################");
                this.conflictStrings.remove(this.conflictGroupList.getSelectionModel().getSelectedItem());
                this.conflictGroupList.refresh();
                Notifications.create()
                        .title("Success!")
                        .text("Entry conflict solved.")
                        .showConfirm();
            }else{
                System.out.println("Entry was attempted to be selected, returned null.\n");
                Notifications.create()
                        .title("Error")
                        .text("No entry selected!")
                        .showWarning();
            }
        });

        Button newEntryBtn = new Button("New Entry");
        newEntryBtn.setOnAction(event -> {
            SQLEntryCreator newEntry = new SQLEntryCreator();
            SQLEntry UserCreatedEntry = newEntry.run();
            if(this.conflictGroupList.getSelectionModel().getSelectedItem() != null){
                int selectIndex = this.conflictGroupList.getSelectionModel().getSelectedIndex();
                System.out.println("index of gorup selected: " + selectIndex);
                this.conflicts.get(selectIndex).add(UserCreatedEntry);
                this.IPaddrGroupView.refresh();
                this.currentConflicts.add(UserCreatedEntry);
            }else{
                Notifications.create()
                        .title("Error")
                        .text("No conflict group selected to add new entry to!")
                        .showWarning();
            }
        });

        this.conflicts = FXCollections.observableArrayList(this.originalConflicts);
        this.conflictStrings = ConflictStringConverter.getStringsFromSQLArrListOfArrLists(this.conflicts);

        this.conflictGroupList = new ListView<>(this.conflictStrings);
        this.conflictGroupList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.conflictGroupList.setPrefWidth(250);
        this.conflictGroupList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            //System.out.println("VALUE RETURNED: " + newValue + " FROM LISTLISTTABLE");
            ArrayList<SQLEntry> tempList = ConflictStringConverter.getSQLArrListFromString(newValue, this.conflicts);
            //System.out.println("######################################################");
            //System.out.println("ArrayList returned from String:");
            //System.out.println(tempList);
            //System.out.println("######################################################");
            if (this.currentConflicts != null) {
                this.currentConflicts.removeAll(this.currentConflicts);
                this.currentConflicts.addAll(tempList);
            } else {
                this.currentConflicts = FXCollections.observableArrayList(tempList);
            }
            //System.out.println("LIST OF CONFLICTS TO DISPLAY: ");
            //System.out.println(currentConflicts);
            //System.out.println("######################################################");
            this.IPaddrGroupView.refresh();
            //System.out.println(this.IPaddrGroupView.getItems());
        });


        getTable();
        this.IPaddrGroupView.refresh();

        listSplitterBox.getChildren().addAll(this.conflictGroupList, entryPickBox);
        entryPickBox.getChildren().addAll(this.IPaddrGroupView, buttonBox);
        buttonBox.getChildren().addAll(selectBtn, newEntryBtn);

        this.confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(true);

        dialog.getDialogPane().setContent(listSplitterBox);

        dialog.showAndWait();
    }

    public ArrayList<SQLEntry> getGoodEntries() {
        ArrayList<SQLEntry> reformatted;
        if (this.solvedConflicts == null) {
            reformatted = new ArrayList<>();
        } else {
            reformatted = new ArrayList<>(this.solvedConflicts);
        }
        return reformatted;
    }

    public ObservableList<ArrayList<SQLEntry>> getFixedConflicts() {
        return this.solvedConflictGroups;
    }

    private void getTable() {

        //System.out.println("Getting refreshed table!");

        this.IPaddrGroupView = new TableView<>();
        //this.IPaddrGroupView.setEditable(false);

        // create table columns
        TableColumn<SQLEntry, String> nameCol = new TableColumn("Name");
        TableColumn<SQLEntry, String> ownerCol = new TableColumn("Owner");
        TableColumn<SQLEntry, String> ipaddrCol = new TableColumn("IP Address");
        TableColumn<SQLEntry, String> netmaskCol = new TableColumn("Netmask");
        TableColumn<SQLEntry, String> vlantagCol = new TableColumn("Vlan Tag");
        TableColumn<SQLEntry, String> zoneCol = new TableColumn("Zone");
        TableColumn<SQLEntry, String> zonetypeCol = new TableColumn("Zone Type");
        TableColumn<SQLEntry, String> vrfCol = new TableColumn("VRF");
        TableColumn<SQLEntry, String> locationCol = new TableColumn("Location");
        TableColumn<SQLEntry, String> commentCol = new TableColumn("Comments");
        TableColumn<SQLEntry, String> originCol = new TableColumn("Origin");
        TableColumn<SQLEntry, String> idCol = new TableColumn("ID");

        // set column widths
        nameCol.setPrefWidth(200);
        ownerCol.setPrefWidth(100);
        ipaddrCol.setPrefWidth(125);
        netmaskCol.setPrefWidth(75);
        vlantagCol.setPrefWidth(75);
        zoneCol.setPrefWidth(100);
        zonetypeCol.setPrefWidth(100);
        vrfCol.setPrefWidth(75);
        locationCol.setPrefWidth(100);
        commentCol.setPrefWidth(200);
        originCol.setPrefWidth(100);
        idCol.setPrefWidth(75);

        // add table columns to main table
        this.IPaddrGroupView.getColumns().addAll(nameCol, ownerCol, ipaddrCol, netmaskCol, vlantagCol, zoneCol, zonetypeCol, vrfCol, locationCol, commentCol, originCol, idCol);

        // set each column to read a specific portion of the table
        nameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("Owner"));
        ipaddrCol.setCellValueFactory(new PropertyValueFactory<>("IPAddr"));
        netmaskCol.setCellValueFactory(new PropertyValueFactory<>("Netmask"));
        vlantagCol.setCellValueFactory(new PropertyValueFactory<>("VlanTag"));
        zoneCol.setCellValueFactory(new PropertyValueFactory<>("Zone"));
        zonetypeCol.setCellValueFactory(new PropertyValueFactory<>("ZoneType"));
        vrfCol.setCellValueFactory(new PropertyValueFactory<>("VRF"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("Location"));
        originCol.setCellValueFactory(new PropertyValueFactory<>("FileOrigin"));
        commentCol.setCellValueFactory(new PropertyValueFactory<>("Comment"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));


        netmaskCol.setComparator(new IntegerSort());
        vlantagCol.setComparator(new IntegerSort());
        idCol.setComparator(new IntegerSort());

        // whenever a cell is clicked on, these classes allow editing of the cells.
        Callback<TableColumn<SQLEntry, String>, TableCell<SQLEntry, String>> stringCellFactory
                = (TableColumn<SQLEntry, String> param) -> new CellEdit(false);
        Callback<TableColumn<SQLEntry, String>, TableCell<SQLEntry, String>> intCellFactory
                = (TableColumn<SQLEntry, String> param) -> new CellEdit(true);

        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameCol.setCellFactory(stringCellFactory);

        ownerCol.setCellValueFactory(cellData -> cellData.getValue().ownerProperty());
        ownerCol.setCellFactory(stringCellFactory);

        ipaddrCol.setCellValueFactory(cellData -> cellData.getValue().IPAddrProperty());
        ipaddrCol.setCellFactory(stringCellFactory);

        netmaskCol.setCellValueFactory(cellData -> cellData.getValue().netmaskProperty());
        netmaskCol.setCellFactory(intCellFactory);

        vlantagCol.setCellValueFactory(cellData -> cellData.getValue().vlanTagProperty());
        vlantagCol.setCellFactory(intCellFactory);

        zoneCol.setCellValueFactory(cellData -> cellData.getValue().zoneProperty());
        zoneCol.setCellFactory(stringCellFactory);

        zonetypeCol.setCellValueFactory(cellData -> cellData.getValue().zoneTypeProperty());
        zonetypeCol.setCellFactory(stringCellFactory);

        vrfCol.setCellValueFactory(cellData -> cellData.getValue().VRFProperty());
        vrfCol.setCellFactory(stringCellFactory);

        locationCol.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        locationCol.setCellFactory(stringCellFactory);

        commentCol.setCellValueFactory(cellData -> cellData.getValue().commentProperty());
        commentCol.setCellFactory(stringCellFactory);

        this.IPaddrGroupView.setItems(this.currentConflicts);
    }
}
