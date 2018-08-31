package GUI;

import DBUtil.SQLEntry;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * A popup window for creating a new SQLEntry, then committing it to the database.
 * @author Kell Larson <ktech117@gmail.com>
 */
public class SQLEntryCreator {

    boolean isComplete = false;

    private final String hexGrey1 = "4A444B";
    private final String hexRed = "BA0101";
    private final String hexOffWhite = "FFFFF0";
    private final String hexGrey2 = "78866B";
    private final String hexBlue = "90AFFF";

    private Node confirmButton;

    private SQLEntry sqeg;

    public SQLEntryCreator() {
    }

    public SQLEntry run() {
        createStage();
        return this.sqeg;
    }

    protected boolean isComplete() {
        return this.isComplete;
    }


    private void createStage() {         // creates the GUI for the popup
        Dialog dialog = new Dialog<>();
        dialog.setTitle("Create a New Entry");
        //dialog.setHeaderText("Hello, I'd just like you to let me know about your LED strip.");

        ButtonType confirmButtonType = new ButtonType("Enter", ButtonData.APPLY);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType);
        dialog.getDialogPane().setStyle("-fx-background-color: #" + this.hexOffWhite + ";");


        this.confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);

        this.sqeg = new SQLEntry();
        this.sqeg.setEditable(true);

        dialog.getDialogPane().setContent(this.sqeg.getGridPane());

        // Request focus on the username field by default.
        //Platform.runLater(() -> LengthStrText.requestFocus());
        dialog.showAndWait();
    }
}
