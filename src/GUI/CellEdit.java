package GUI;

import DBUtil.SQLEntry;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;

/**
 * Used on specific cells within the mainview table, to allow them to be editable.
 * I'd like to be more specific, but this code is almost untouched by me, so I have no clue the dark magic that
 * lets this work.
 * @author Kell Larson (modified by)
 * @author Hasan Kara <hasan.kara@fhnw.ch>
 */
public class CellEdit extends TableCell<SQLEntry, String> {

    private TextField textField;

    public CellEdit(boolean parseInt) {
        textField = new TextField();
        if(parseInt){
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
        }

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

    public void createTextField() {
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

    public String getString() {
        return getItem() == null ? "" : getItem();
    }
}
