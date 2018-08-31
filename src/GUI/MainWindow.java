package GUI;

import Controller.Main;
import DBUtil.Comparators.IntegerSort;
import DBUtil.DatabaseTags;
import DBUtil.SQLEntry;
import DBUtil.SQLReader;
import DBUtil.SQLWriter;
import FileParse.FileParserMaster;
import GUI.CompareCSV.DHCPComparer;
import GUI.CompareCSV.DhcpChooser;
import GUI.ConflictStuff.ConflictFixer;
import GUI.NewFileStuff.ParserChooser;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.ToggleSwitch;

import java.io.File;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * The class called from main, the "main" for the JavaFX GUI setup. All GUI calls at some point go through here.
 *
 * @author Kell Larson <ktech117@gmail.com>
 */
public class MainWindow extends Application {

    private TableView<SQLEntry> table; // the table showing all entries in the database

    private TableHandler editor; // entry edit handler
    private SQLReader dbr; //database read
    private SQLWriter dbw;  //database write
    private FileParserMaster fpm; //fileparse controller

    private boolean tableEditable = false; // toggles if the main table is editable
    private ToggleSwitch editSwt; // switch to toggle the tableEditable boolean

    private VBox tableBox; // Pane that encapsulates the main table, and the buttons above
    private HBox topRow; // the Pane above the table
    private HBox botRow; // the Pane below the table

    private ComboBox<String> columnFilterCombo;
    private TextField filterField;

    private ObservableList<SQLEntry> tableList; // the list that the tableview shows

    private Button deleteBtn; // button that deletes rows

    public MainWindow() {
        this.fpm = new FileParserMaster();
        this.table = new TableView<>();
    }

    /**
     * Initializes the GUI, by calling launch(args);
     *
     * @param args initial commandline arguments
     */
    public static void init(String[] args) {
        System.out.println("GUI Initializing");
        launch(args);

    }

    public void start(Stage primaryStage) {
        Scene scene = new Scene(new Group());
        primaryStage.setTitle("VLAN Database Viewer v" + Main.VERSION);
        primaryStage.setWidth(1400);
        primaryStage.setHeight(550);

        this.dbr = new SQLReader(Main.DATABASEURL, Main.USERNAME, Main.PASSWORD, Main.DATABASE, Main.TABLE); // initialize the DB Reader
        this.dbw = new SQLWriter(Main.DATABASEURL, Main.USERNAME, Main.PASSWORD, Main.DATABASE, Main.TABLE); // initialize the DB Writer

        //this.dbr = new SQLReader();
        //this.dbw = new SQLWriter();

        this.editor = new TableHandler(this.dbw); // initialize the table edit handler class


        this.tableBox = new VBox(); // initialize the main pane
        this.tableBox.setSpacing(5);
        this.tableBox.setPadding(new Insets(10, 0, 0, 10));
        this.topRow = TopRow(); // call method to initialize the button pane above the table
        this.botRow = BottomRow(); // call method to init pane below table
        // setup main table, do first DB pull to populate
        this.dbr.sortQuery(DatabaseTags.getTagArray()[2]);
        this.initMainTable(this.dbr.getSQLEntries());

        this.botRow = this.BottomRow();

        this.tableBox.getChildren().addAll(this.topRow, this.table, this.botRow);


        //refreshTable(this.dbr.getSQLEntries()); // get latest values populated into the table, depreciated.

        ((Group) scene.getRoot()).getChildren().addAll(this.tableBox);  // add main pane to scene

        primaryStage.setScene(scene); // add scene to stage
        primaryStage.show(); // show the stage to user
    }

    /**
     * A one-time call to initialize and setup the main table with an initial list, does not return a tableView and
     * instead just sets up the "table" object within the class.
     *
     * @param initialList The initial list. Please do not pass null.
     * @implNote Warns the user if null is passed, due to silents errors being caused if it happens. It does allow it,
     * however.
     */
    private void initMainTable(ArrayList<SQLEntry> initialList) {
        if (initialList == null) {
            System.err.println("Warning! null passed as initialList for the method initMainTable! This causes various" +
                    "Silent errors! IF you really want to pass something similar, just create an empty list. This will" +
                    "make sure no silent errors occur. I learned this the hard way, after 5 hours of stackOverflow" +
                    "trying to figure out why my tableView was broken, so please do not pass null.");
        }
        this.tableList = FXCollections.observableArrayList(initialList);

        this.table = new TableView<>();
        this.table.setEditable(false);

        // create table columns
        TableColumn<SQLEntry, String> nameCol = new TableColumn<>("Name");
        TableColumn<SQLEntry, String> ownerCol = new TableColumn<>("Owner");
        TableColumn<SQLEntry, String> ipaddrCol = new TableColumn<>("IP Address");
        TableColumn<SQLEntry, String> netmaskCol = new TableColumn<>("Netmask");
        TableColumn<SQLEntry, String> vlantagCol = new TableColumn<>("Vlan Tag");
        TableColumn<SQLEntry, String> zoneCol = new TableColumn<>("Zone");
        TableColumn<SQLEntry, String> zonetypeCol = new TableColumn<>("Zone Type");
        TableColumn<SQLEntry, String> vrfCol = new TableColumn<>("VRF");
        TableColumn<SQLEntry, String> locationCol = new TableColumn<>("Location");
        TableColumn<SQLEntry, String> commentCol = new TableColumn<>("Comments");
        TableColumn<SQLEntry, String> originCol = new TableColumn<>("Origin");
        TableColumn<SQLEntry, String> idCol = new TableColumn<>("ID");

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
        this.table.getColumns().addAll(nameCol, ownerCol, ipaddrCol, netmaskCol, vlantagCol, zoneCol, zonetypeCol, vrfCol, locationCol, commentCol, originCol, idCol);

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

        // sets each column to do a specific editing action
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameCol.setCellFactory(stringCellFactory);
        nameCol.setOnEditCommit(this.editor::handleName);

        ownerCol.setCellValueFactory(cellData -> cellData.getValue().ownerProperty());
        ownerCol.setCellFactory(stringCellFactory);
        ownerCol.setOnEditCommit(this.editor::handleOwner);

        ipaddrCol.setCellValueFactory(cellData -> cellData.getValue().IPAddrProperty());
        ipaddrCol.setCellFactory(stringCellFactory);
        ipaddrCol.setOnEditCommit(this.editor::handleIPaddr);

        netmaskCol.setCellValueFactory(cellData -> cellData.getValue().netmaskProperty());
        netmaskCol.setCellFactory(intCellFactory);
        netmaskCol.setOnEditCommit(this.editor::handleNetmask);

        vlantagCol.setCellValueFactory(cellData -> cellData.getValue().vlanTagProperty());
        vlantagCol.setCellFactory(intCellFactory);
        vlantagCol.setOnEditCommit(this.editor::handleVlanTag);

        zoneCol.setCellValueFactory(cellData -> cellData.getValue().zoneProperty());
        zoneCol.setCellFactory(stringCellFactory);
        zoneCol.setOnEditCommit(this.editor::handleZone);

        zonetypeCol.setCellValueFactory(cellData -> cellData.getValue().zoneTypeProperty());
        zonetypeCol.setCellFactory(stringCellFactory);
        zonetypeCol.setOnEditCommit(this.editor::handleZoneType);

        vrfCol.setCellValueFactory(cellData -> cellData.getValue().VRFProperty());
        vrfCol.setCellFactory(stringCellFactory);
        vrfCol.setOnEditCommit(this.editor::handleVRF);

        locationCol.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        locationCol.setCellFactory(stringCellFactory);
        locationCol.setOnEditCommit(this.editor::handleLocation);

        commentCol.setCellValueFactory(cellData -> cellData.getValue().commentProperty());
        commentCol.setCellFactory(stringCellFactory);
        commentCol.setOnEditCommit(this.editor::handleComment);

        this.table.setItems(this.tableList);

    }

    /**
     * Creates the top HBox of the GUI, displaying a label and the refresh button.
     *
     * @return Row of buttoms at the top of the GUI
     */
    private HBox TopRow() {
        HBox topRow = new HBox(); // create the pane and set up spacing
        topRow.setPadding(new Insets(0, 10, 0, 10));
        topRow.setSpacing(50);

        // button to refresh table values
        Button refreshBtn = new Button("Refresh Table");
        refreshBtn.setOnAction(actionEvent -> {
            this.refreshTableList();
        });

        this.editSwt = new ToggleSwitch("Edit Mode");
        this.editSwt.selectedProperty().addListener((ov, t, t1) -> {
            this.tableEditable = this.editSwt.isSelected();
            this.table.setEditable(this.editSwt.isSelected());
        });

        // creates popup window to allow user to select file and let app parse it
        Button parseFile = new Button("Parse New File");
        parseFile.setOnAction(event -> {
            ParserChooser prc = new ParserChooser();
            prc.run();
            File f = new File(prc.getFilePath());
            ArrayList<SQLEntry> entriesFromFile = this.fpm.parseGeneric(f, prc.getFileParserType());
            //System.out.println(entriesFromFile);
            this.dbw.writeDataNoConflictCheck(entriesFromFile);
            this.refreshTableList();
        });

        // creates popup that allows user to create new SQLEntry and have it committed to the database
        Button newSQLEntryBtn = new Button("New Entry");
        newSQLEntryBtn.setOnAction(event -> {
            SQLEntryCreator sqec = new SQLEntryCreator();
            SQLEntry newEntry = sqec.run();
            ArrayList<SQLEntry> tempList = new ArrayList<>();
            tempList.add(newEntry);
            this.dbw.writeDataNoConflictCheck(tempList);
            this.refreshTableList();
        });

        // creates popup window to solve any database conflicts.
        Button conflictBtn = new Button("Confict Solver");
        conflictBtn.setOnAction(event -> {
            boolean success = this.dbw.checkDBForConflicts();
            if (success) {
                Notifications.create()
                        .title("No Conflicts")
                        .text("No conflicts found!")
                        .showInformation();
            } else {
                runConflictor();
            }
            this.refreshTableList();
        });

        this.deleteBtn = new Button("Delete Row");
        this.deleteBtn.setOnAction(event -> {
            SQLEntry selectedItem = this.table.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Row Deletion");
                alert.setHeaderText("Are you sure you want to delete this?");
                alert.getDialogPane().setContent(selectedItem.getGridPane());

                Optional<ButtonType> result = alert.showAndWait();
                try {
                    if (result.get() == ButtonType.OK) {
                        this.dbw.deleteEntry(selectedItem);
                        this.dbr.sortQuery();
                        this.refreshTableList();
                    } else {
                        //pretty sure this will never get triggered, but better safe than sorry :)
                        Notifications.create().title("Delete Cancelled").text("Row deletion cancelled.").showInformation();
                    }
                } catch (NoSuchElementException e) {
                    Notifications.create().title("Delete Cancelled").text("Row deletion cancelled.").showInformation();
                }
            } else {
                Notifications.create().title("No Row Selected!").text("Please select a row to delete.").showWarning();
            }

        });

        Label filterLabel = new Label("Filter:");

        //create combo box (aka dropdown menu) and text field for filtering results
        this.columnFilterCombo = new ComboBox<>(FXCollections.observableArrayList(DatabaseTags.getTagArray()));
        this.filterField = new TextField();
        this.filterField.setPromptText("Search Here");

        // if either filter picking elements are modified, update table with new filter
        this.columnFilterCombo.setOnAction(event -> handleFilterEvent());
        this.filterField.setOnAction(event -> handleFilterEvent());


        // add all buttons to the pane
        topRow.getChildren().addAll(this.editSwt, refreshBtn, parseFile, newSQLEntryBtn, conflictBtn, this.deleteBtn, filterLabel, this.columnFilterCombo, this.filterField);
        return topRow;
    }

    private HBox BottomRow() {

        HBox botRow = new HBox();

        Button testBtn = new Button("Test!");
        testBtn.setOnAction(event -> {
            FileParserMaster fpm = new FileParserMaster();
            ParserChooser pc = new ParserChooser();
            pc.run();
            String fpath = pc.getFilePath();
            File fil = new File(fpath);
            if(fil.exists()) {
                ArrayList<SQLEntry> entriesTest = fpm.parseDHCPFile(fil);
                for (SQLEntry e : entriesTest) {
                    System.out.println(e);
                }
            }else{
                System.err.println("error selecting input file!");
            }
        });

        Button compareCSV = new Button("Compare CSV files");
        compareCSV.setOnAction(event -> {
            DhcpChooser dhcpc = new DhcpChooser();
            dhcpc.run();
            File inp = new File(dhcpc.getInputFilePath());
            String outStr = dhcpc.getOutputFilePath();
            if(inp.exists()) {
                ArrayList<SQLEntry> entriesTest = fpm.parseDHCPFile(inp);
                this.dbr.sortQuery(DatabaseTags.getTagArray()[2]);
                ArrayList<SQLEntry> entriesDB = this.dbr.getSQLEntries();
                DHCPComparer dhcompare = new DHCPComparer(entriesDB,entriesTest,outStr);
                dhcompare.parseDifferences();
                dhcompare.writeDiff();
            }else{
                System.err.println("error selecting input and/or output file!");
            }
        });

        botRow.getChildren().addAll(/*testBtn, */compareCSV); // commented out test button
        return botRow;
    }

    /**
     * Uses the passed list and sets it as the list to display. Simply takes the tableList, deletes all entries within it,
     * and copies over the new list in its place.
     *
     * @param newList the new List to display on the main window tableView.
     * @implNote Apparently faster than comparing the two and adding / removing entries,
     * which I did originally. Comparing lists, adding/delting entries: 6.3 seconds. Deleting entire list, copying over
     * new list: 0.005 seconds. So deleting all entries and copying over a list is about 1100x faster.
     */
    private void refreshTableList(ArrayList<SQLEntry> newList) {
        this.tableList.setAll(newList);
        this.table.refresh();
    }

    /**
     * Uses the passed list and sets it as the list to display. Simply takes the tableList, deletes all entries within it,
     * and copies over the new list in its place.
     *
     * @implNote Apparently faster than comparing the two and adding / removing entries,
     * which I did originally. Comparing lists, adding/delting entries: 6.3 seconds. Deleting entire list, copying over
     * new list: 0.005 seconds. So deleting all entries and copying over a list is about 1100x faster. Instead of a passed
     * Arraylist, just calls this.dbr.sortQuery(DatabaseTags.getTagArray()[2]); instead, aka sort by ipaddr.
     */
    private void refreshTableList() {
        this.dbr.sortQuery(DatabaseTags.getTagArray()[2]);
        ArrayList<SQLEntry> newEntries = this.dbr.getSQLEntries();
        this.tableList.setAll(newEntries);
        this.table.refresh();
    }

    private void handleFilterEvent() {
        if (this.columnFilterCombo.getItems().size() > 0 && !this.filterField.getText().isEmpty()) {
            String comboBoxChosenColumn = this.columnFilterCombo.getItems().get(0);
            String filterStatement = comboBoxChosenColumn + " LIKE '%" + this.filterField.getText() + "%'";
            this.dbr.sortQuery(DatabaseTags.getTagArray()[2], filterStatement);
            /*
            for(SQLEntry sq : this.dbr.getSQLEntries()){
                System.out.println(sq);
            } */
            //refreshTable(this.dbr.getSQLEntries());
            System.err.println("Not set up yet, line 302 MainWindow class.");
            this.refreshTableList(this.dbr.getSQLEntries()); // get latest values populated into the table
        }
    }


    /**
     * Runs the conflict fixer popup window. Pulls all conflicts from the DB, and gets solved conflicts back from the popup.
     */
    private void runConflictor() {
        ConflictFixer cxf = new ConflictFixer(this.dbw.getConflicts());
        cxf.run();
        ArrayList<SQLEntry> goodList = cxf.getGoodEntries();
        ArrayList<ArrayList<SQLEntry>> solvedGroups = new ArrayList<>(cxf.getFixedConflicts());
        System.out.println("Fixed conflicts:");
        System.out.println(solvedGroups);
        this.dbw.fixConflicts(goodList, solvedGroups);
    }

    /**
     * Returns a boolean that is set in relation to a slider on the main window, chooses if edits to the table should
     * be written or ignored.
     *
     * @return true if editing should be allowed, false if editing should not be allowed.
     */
    public boolean isTableEditable() {
        return tableEditable;
    }
}
