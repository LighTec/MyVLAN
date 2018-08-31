package GUI.NewFileStuff;

import FileParse.FileParser_Interface;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.Notifications;

import java.io.*;


/**
 * A popup window used to chose a file to be parsed, and which way the file should be parsed. The user picks a file from
 * the builtin JavaFX DhcpChooser, then picks a parser from the dropdown menu. When both of these things have been done,
 * the enter button is enabled, which will close the window and then start the process of parsing the file and sending
 * all entries to the DB.
 * @author Kell Larson <ktech117@gmail.com>
 */
public class ParserChooser {

    boolean isComplete = false;

    FileParser_Interface parser;

    private final String hexGrey1 = "4A444B";
    private final String hexRed = "BA0101";
    private final String hexOffWhite = "FFFFF0";
    private final String hexGrey2 = "78866B";
    private final String hexBlue = "90AFFF";

    public ParserChooser() {

    }

    public void run() {
        createStage();
    }

    protected boolean isComplete() {
        return this.isComplete;
    }

    private void changePort(FileParser_Interface fpi) {
        this.parser = fpi;
    }

    private TextField PathText = new TextField();

    private boolean isDebug = false;

    private Node confirmButton;

    private void createStage() {         // creates the GUI for the popup
        Dialog dialog = new Dialog<>();
        dialog.setTitle("Please Select a File");
        //dialog.setHeaderText("Hello, I'd just like you to let me know about your LED strip.");

        ButtonType confirmButtonType = new ButtonType("Enter", ButtonBar.ButtonData.APPLY);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType);
        dialog.getDialogPane().setStyle("-fx-background-color: #" + this.hexGrey1 + ";");

        PortDropdownMenuFX portChooser = new PortDropdownMenuFX();
        portChooser.refreshMenu();

        Button FileChooseBtn = new Button("Select File");

        portChooser.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends FileParser_Interface> observable, FileParser_Interface oldvalue, FileParser_Interface newvalue) -> {
            changePort(newvalue);
            canContinue();
        });

        FileChooseBtn.setOnAction((ActionEvent event) -> {
            String folder;
            javafx.stage.FileChooser loadChooser = new javafx.stage.FileChooser();
            loadChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File selectedFolder = loadChooser.showOpenDialog(dialog.getOwner());
            if (selectedFolder == null) {
                Notifications.create()
                        .title("Folder Error")
                        .text("Error selecting folder. Try again.")
                        .showWarning();
            } else {
                folder = selectedFolder.getAbsolutePath();
                this.PathText.setText(folder);
            }
            canContinue();
        });

        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: #" + this.hexBlue + ";");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        this.PathText.setPromptText("\\");
        this.PathText.setEditable(false);
        this.PathText.setMaxWidth(800);

        grid.add(FileChooseBtn, 0, 0);
        grid.add(new Label("FilePath:"), 1, 0);
        grid.add(this.PathText, 1, 0);
        grid.add(new Label("File Type:"), 0, 1);
        grid.add(portChooser, 1, 1);
        grid.setGridLinesVisible(false); // for debugging

        this.confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(true);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        //Platform.runLater(() -> PathText.requestFocus());
        dialog.showAndWait();
    }

    private void canContinue(){
     boolean enterEnabled = parser != null && !this.PathText.getText().equals("\\");
     this.confirmButton.setDisable(!enterEnabled);
    }

    public String getFilePath(){
        return this.PathText.getText();
    }

    public String getFileParserType(){
        return this.parser.getName();
    }
}
