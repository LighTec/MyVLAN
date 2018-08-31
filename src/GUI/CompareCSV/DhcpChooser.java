package GUI.CompareCSV;

import FileParse.FileParser_Interface;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.controlsfx.control.Notifications;

import java.io.File;


/**
 * A popup window used to chose a file to be parsed, and which way the file should be parsed. The user picks a file from
 * the builtin JavaFX DhcpChooser, then picks a parser from the dropdown menu. When both of these things have been done,
 * the enter button is enabled, which will close the window and then start the process of parsing the file and sending
 * all entries to the DB.
 * @author Kell Larson <ktech117@gmail.com>
 */
public class DhcpChooser {

    boolean isComplete = false;

    FileParser_Interface parser;

    private final String hexGrey1 = "4A444B";
    private final String hexRed = "BA0101";
    private final String hexOffWhite = "FFFFF0";
    private final String hexGrey2 = "78866B";
    private final String hexBlue = "90AFFF";

    public DhcpChooser() {

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

    private TextField InputText = new TextField();
    private TextField OutputText = new TextField();

    private String outputFP = "";

    private boolean isDebug = false;

    private Node confirmButton;

    private void createStage() {         // creates the GUI for the popup
        Dialog dialog = new Dialog<>();
        dialog.setTitle("Please Select Input and Output Files");
        //dialog.setHeaderText("Hello, I'd just like you to let me know about your LED strip.");

        ButtonType confirmButtonType = new ButtonType("Enter", ButtonBar.ButtonData.APPLY);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType);
        dialog.getDialogPane().setStyle("-fx-background-color: #" + this.hexGrey1 + ";");


        Button FileChooseBtn = new Button("Select input File");

        FileChooseBtn.setOnAction((ActionEvent event) -> {
            String folder;
            FileChooser loadChooser = new FileChooser();
            loadChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File selectedFolder = loadChooser.showOpenDialog(dialog.getOwner());
            if (selectedFolder == null) {
                Notifications.create()
                        .title("Folder Error")
                        .text("Error selecting folder. Try again.")
                        .showWarning();
            } else {
                folder = selectedFolder.getAbsolutePath();
                this.InputText.setText(folder);
                this.canContinue();
            }
            canContinue();
        });

        Button outputBtn = new Button("Select Output Location");

        outputBtn.setOnAction((ActionEvent event) -> {
            FileChooser saveChooser = new FileChooser();
            saveChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File selectedFolder = saveChooser.showSaveDialog(dialog.getOwner());
            if (selectedFolder == null) {
                Notifications.create()
                        .title("Folder Error")
                        .text("Error selecting folder. Try again.")
                        .showWarning();
            } else {
                this.outputFP = selectedFolder.getAbsolutePath();
                this.OutputText.setText(this.outputFP);
                this.canContinue();
            }
        });

        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: #" + this.hexBlue + ";");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        this.InputText.setEditable(false);
        this.InputText.setPrefWidth(400);

        this.OutputText.setEditable(false);
        this.OutputText.setPrefWidth(400);

        grid.add(FileChooseBtn, 0, 0);
        grid.add(this.InputText, 1, 0);
        grid.add(outputBtn,0,1);
        grid.add(this.OutputText,1,1);
        grid.setGridLinesVisible(false); // for debugging

        this.confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(true);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        //Platform.runLater(() -> InputText.requestFocus());
        dialog.showAndWait();
    }

    private void canContinue(){
     boolean enterEnabled = !this.OutputText.getText().equals("") && !this.InputText.getText().equals("");
     this.confirmButton.setDisable(!enterEnabled);
    }

    public String getInputFilePath(){
        return this.InputText.getText();
    }

    public String getOutputFilePath() { return this.OutputText.getText(); }
}
