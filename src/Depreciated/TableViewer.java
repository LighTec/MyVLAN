package Depreciated;

import DBUtil.SQLEntry;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.ArrayList;

/**
 * @author Kell Larson <ktech117@gmail.com>
 * @deprecated
 */
public class TableViewer{

    public TableView newTableView(ArrayList<SQLEntry> entries){

        TableView table = new TableView();
        table.setEditable(true);

        // create table columns
        TableColumn nameCol = new TableColumn("Name");
        TableColumn ownerCol = new TableColumn("Owner");
        TableColumn ipaddrCol = new TableColumn("IP Address");
        TableColumn netmaskCol = new TableColumn("Netmask");
        TableColumn vlantagCol = new TableColumn("Vlan Tag");
        TableColumn zoneCol = new TableColumn("Zone");
        TableColumn zonetypeCol = new TableColumn("Zone Type");
        TableColumn vrfCol = new TableColumn("VRF");
        TableColumn locationCol = new TableColumn("Location");
        TableColumn commentCol = new TableColumn("Comments");
        TableColumn idCol = new TableColumn("ID");

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
        idCol.setPrefWidth(75);

        // add table columns to main table
        table.getColumns().addAll(nameCol, ownerCol, ipaddrCol, netmaskCol, vlantagCol, zoneCol, zonetypeCol, vrfCol, locationCol, commentCol, idCol);

        // set table cells to read values from SQLEntry ArrayList
        ObservableList<SQLEntry> data = FXCollections.observableArrayList(entries);
        nameCol.setCellValueFactory(new PropertyValueFactory<SQLEntry, String>("Name"));
        ownerCol.setCellValueFactory(new PropertyValueFactory<SQLEntry, Integer>("Owner"));
        ipaddrCol.setCellValueFactory(new PropertyValueFactory<SQLEntry, String>("IPAddr"));
        netmaskCol.setCellValueFactory(new PropertyValueFactory<SQLEntry, String>("Netmask"));
        vlantagCol.setCellValueFactory(new PropertyValueFactory<SQLEntry, Integer>("VlanTag"));
        zoneCol.setCellValueFactory(new PropertyValueFactory<SQLEntry, String>("Zone"));
        zonetypeCol.setCellValueFactory(new PropertyValueFactory<SQLEntry, String>("ZoneType"));
        vrfCol.setCellValueFactory(new PropertyValueFactory<SQLEntry, String>("VRF"));
        locationCol.setCellValueFactory(new PropertyValueFactory<SQLEntry, String>("Location"));
        commentCol.setCellValueFactory(new PropertyValueFactory<SQLEntry, String>("Comment"));
        idCol.setCellValueFactory(new PropertyValueFactory<SQLEntry, Integer>("id"));

        // creates a factory that generates text fields for direct editing of table
        Callback<TableColumn<SQLEntry, String>, TableCell<SQLEntry, String>> cellFactory
                = (TableColumn<SQLEntry, String> param) -> new EditingCell();
/*
        nameCol.setCellValueFactory(cellData -> cellData.getValue());
        nameCol.setCellFactory(cellFactory);
        nameCol.setOnEditCommit(
                (TableColumn.CellEditEvent<SQLEntry, String> t) -> {
                    ((SQLEntry) t.getTableView().getItems()
                            .get(t.getTablePosition().getRow()))
                            .setName(t.getNewValue());

                });

*/
        table.setItems(data);


        return table;
    }

    public class Typ {

        private final SimpleStringProperty typ;

        public Typ(String typ) {
            this.typ = new SimpleStringProperty(typ);
        }

        public String getTyp() {
            return this.typ.get();
        }

        public StringProperty typProperty() {
            return this.typ;
        }

        public void setTyp(String typ) {
            this.typ.set(typ);
        }

        @Override
        public String toString() {
            return typ.get();
        }

    }

    public class EditingCell extends TableCell<SQLEntry, String> {

        private TextField textField;

        private EditingCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText((String) getItem());
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(item);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
//                        setGraphic(null);
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.setOnAction((e) -> commitEdit(textField.getText()));
            textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (!newValue) {
                    System.out.println("Commiting " + textField.getText());
                    commitEdit(textField.getText());
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }
}
