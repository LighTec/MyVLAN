package DBUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Kell Larson <ktech117@gmail.com>
 * A storage object, used to store rows that will be entered into the SQL database. Used to simplify data parsing.
 * Default values for data are:
 * Name: null
 * Owner: null
 * IPAddr: null
 * Netmask: null
 * VlanTag: null
 * Zone: null
 * ZoneType: null
 * VRF: null
 * Location: null
 * Comment: null
 * FileOrigin: null
 * id: null
 *
 * Note: All setters and getters do not have javadocs, because they work as-is. By that I mean getName() will return
 * the name, nothing more or less. Will do the same for all other getters, setters, and properties.
 */
public class SQLEntry{

    // These variables represent a single row in the SQL database
    private SimpleStringProperty Name = null;
    private SimpleStringProperty Owner = null;
    private SimpleStringProperty IPAddr = null;
    private SimpleStringProperty Netmask = null;
    private SimpleStringProperty VlanTag = null;
    private SimpleStringProperty Zone = null;
    private SimpleStringProperty ZoneType = null;
    private SimpleStringProperty VRF = null;
    private SimpleStringProperty Location = null;
    private SimpleStringProperty Comment = null;
    private SimpleStringProperty FileOrigin = null;
    private SimpleStringProperty id = null;

    private GridPane grid;
    private boolean editable = false;

    /**
     * Creates a new SQLEntry object, with all values initialized "".
     */
    public SQLEntry() {
        this.Name = new SimpleStringProperty("");
        this.Owner = new SimpleStringProperty("");
        this.IPAddr = new SimpleStringProperty("");
        this.Netmask = new SimpleStringProperty("");
        this.VlanTag = new SimpleStringProperty("");
        this.Zone = new SimpleStringProperty("");
        this.ZoneType = new SimpleStringProperty("");
        this.VRF = new SimpleStringProperty("");
        this.Location = new SimpleStringProperty("");
        this.Comment = new SimpleStringProperty("");
        this.FileOrigin = new SimpleStringProperty("");
        this.id = new SimpleStringProperty("");
    }

    /**
     * Generates the gridpane used to graphically display an SQLEntry object, then places it into the private GridPane
     * object, grid.
     */
    private void generateGridPane(){

        TextField nameField = new TextField();
        TextField ownerField = new TextField();
        TextField ipaddrField = new TextField();
        TextField netmaskField = new TextField();
        TextField vlantagField = new TextField();
        TextField zoneField = new TextField();
        TextField zonetypeField = new TextField();
        TextField vrfField = new TextField();
        TextField locationField = new TextField();
        TextField commentField = new TextField();
        TextField fileoriginField = new TextField();
        TextField idField = new TextField();

        DecimalFormat intFormat = new DecimalFormat( "#" );

        formatSetter(netmaskField, intFormat);
        formatSetter(vlantagField, intFormat);
        formatSetter(idField, intFormat);

        nameField.setText(this.getName());
        ownerField.setText(this.getOwner());
        ipaddrField.setText(this.getIPAddr());
        netmaskField.setText(this.getNetmask());
        vlantagField.setText(this.getVlanTag());
        zoneField.setText(this.getZone());
        zonetypeField.setText(this.getZoneType());
        vrfField.setText(this.getVRF());
        locationField.setText(this.getLocation());
        commentField.setText(this.getComment());
        fileoriginField.setText(this.getFileOrigin());
        idField.setText(this.getId());

        nameField.textProperty().addListener((ob, ol, nw) -> this.setName(nameField.getText()));
        ownerField.textProperty().addListener((ob, ol, nw) -> this.setOwner(ownerField.getText()));
        ipaddrField.textProperty().addListener((ob, ol, nw) -> this.setIPAddr(ipaddrField.getText()));
        netmaskField.textProperty().addListener((ob, ol, nw) -> this.setNetmask(netmaskField.getText()));
        vlantagField.textProperty().addListener((ob, ol, nw) -> this.setVlanTag(vlantagField.getText()));
        zoneField.textProperty().addListener((ob, ol, nw) -> this.setZone(zoneField.getText()));
        zonetypeField.textProperty().addListener((ob, ol, nw) -> this.setZoneType(zonetypeField.getText()));
        vrfField.textProperty().addListener((ob, ol, nw) -> this.setVRF(vrfField.getText()));
        locationField.textProperty().addListener((ob, ol, nw) -> this.setLocation(locationField.getText()));
        commentField.textProperty().addListener((ob, ol, nw) -> this.setComment(commentField.getText()));
        fileoriginField.textProperty().addListener((ob, ol, nw) -> this.setFileOrigin(fileoriginField.getText()));
        idField.textProperty().addListener((ob, ol, nw) -> this.setId(idField.getText()));

        nameField.setPrefWidth(200);
        ownerField.setPrefWidth(100);
        ipaddrField.setPrefWidth(125);
        netmaskField.setPrefWidth(75);
        vlantagField.setPrefWidth(75);
        zoneField.setPrefWidth(100);
        zonetypeField.setPrefWidth(100);
        vrfField.setPrefWidth(75);
        locationField.setPrefWidth(100);
        commentField.setPrefWidth(200);
        fileoriginField.setPrefWidth(100);
        idField.setPrefWidth(75);

        ArrayList<TextField> sqlFields = new ArrayList<>(Arrays.asList(nameField,ownerField,ipaddrField,netmaskField,vlantagField,zoneField,zonetypeField,vrfField,locationField,commentField,fileoriginField,idField));

        for(TextField fld : sqlFields){
            fld.setEditable(this.editable);
        }

        Label nameLbl = new Label("Name: ");
        Label ownerLbl = new Label("Owner: ");
        Label ipaddrLbl = new Label("IP Address: ");
        Label netmaskLbl = new Label("Netmask: ");
        Label vlantagLbl = new Label("Vlan Tag: ");
        Label zoneLbl = new Label("Zone: ");
        Label zonetypeLbl = new Label("Zone Type: ");
        Label vrfLbl = new Label("VRF: ");
        Label locationLbl = new Label("Location: ");
        Label commentLbl = new Label("Comments: ");
        Label fileoriginLbl = new Label("Origin: ");
        Label idLbl = new Label("ID: ");

        grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        grid.add(nameLbl,0,0);
        grid.add(nameField,1,0);
        grid.add(ownerLbl,2,0);
        grid.add(ownerField,3,0);
        grid.add(ipaddrLbl,4,0);
        grid.add(ipaddrField,5,0);
        grid.add(netmaskLbl,6,0);
        grid.add(netmaskField,7,0);

        grid.add(vlantagLbl,0,1);
        grid.add(vlantagField,1,1);
        grid.add(zoneLbl,2,1);
        grid.add(zoneField,3,1);
        grid.add(zonetypeLbl,4,1);
        grid.add(zonetypeField,5,1);
        grid.add(vrfLbl, 6,1);
        grid.add(vrfField,7,1);

        grid.add(locationLbl,0,2);
        grid.add(locationField,1,2);
        grid.add(commentLbl,2,2);
        grid.add(commentField,3,2);
        grid.add(fileoriginLbl,4,2);
        grid.add(fileoriginField,5,2);
        grid.add(idLbl,6,2);
        grid.add(idField,7,2);




        grid.setGridLinesVisible(false); // for debugging
    }

    /**
     * Applies a formatter to a TextField, which forces specific input into the graphical text field.
     * For example, passing a textfield and the DecimalFormat object constructed with "#" will mean only positive integers
     * can be entered into the textfield, any alphabetical or symbolic characters that the user tries to type into the boxw
     * will be ignored.
     * @param idField TextField that the formatter will get applied to
     * @param format type of formatting to be used
     */
    private void formatSetter(TextField idField, DecimalFormat format) {
        idField.setTextFormatter(new TextFormatter<>(c -> {
            if (c.getControlNewText().isEmpty()) return c;
            ParsePosition parsePosition = new ParsePosition(0);
            Object object = format.parse(c.getControlNewText(), parsePosition);
            if (object == null || parsePosition.getIndex() < c.getControlNewText().length()) {
                return null;
            } else return c;
        }));
    }

    /**
     * Gets the GridPane object, which displays the SQLEntry object it is called from. Put it into a scene, and bam!
     * a graphical representation of the SQLEntry object.
     * @return
     */
    public GridPane getGridPane(){
        generateGridPane();
        return this.grid;
    }

    /**
     * Allows the TextFields of a GridPane returned from a object with this set to true to be edited.
     * @param editable Allows the graphical fields to be editable if set to true
     */
    public void setEditable(boolean editable){
        this.editable = editable;
    }

    public String getName() {
        return Name.get();
    }

    public StringProperty nameProperty() {
        return Name;
    }

    public void setName(String name) {
        this.Name.set(name);
    }

    public String getOwner() {
        return Owner.get();
    }

    public StringProperty ownerProperty() {
        return Owner;
    }

    public void setOwner(String owner) {
        this.Owner.set(owner);
    }

    public String getIPAddr() {
        return IPAddr.get();
    }

    public StringProperty IPAddrProperty() {
        return IPAddr;
    }

    public void setIPAddr(String IPAddr) {
        this.IPAddr.set(IPAddr);
    }

    public String getNetmask() {
        return Netmask.get();
    }

    public StringProperty netmaskProperty() {
        return Netmask;
    }

    public void setNetmask(String netmask) {
        this.Netmask.set(netmask);
    }

    public String getVlanTag() {
        return VlanTag.get();
    }

    public StringProperty vlanTagProperty() {
        return VlanTag;
    }

    public void setVlanTag(String vlanTag) {
        this.VlanTag.set(vlanTag);
    }

    public String getZone() {
        return Zone.get();
    }

    public StringProperty zoneProperty() {
        return Zone;
    }

    public void setZone(String zone) {
        this.Zone.set(zone);
    }

    public String getZoneType() {
        return ZoneType.get();
    }

    public StringProperty zoneTypeProperty() {
        return ZoneType;
    }

    public void setZoneType(String zoneType) {
        this.ZoneType.set(zoneType);
    }

    public String getVRF() {
        return VRF.get();
    }

    public StringProperty VRFProperty() {
        return VRF;
    }

    public void setVRF(String VRF) {
        this.VRF.set(VRF);
    }

    public String getLocation() {
        return Location.get();
    }

    public StringProperty locationProperty() {
        return Location;
    }

    public void setLocation(String location) {
        this.Location.set(location);
    }

    public String getComment() {
        return Comment.get();
    }

    public StringProperty commentProperty() {
        return Comment;
    }

    public void setComment(String comment) {
        this.Comment.set(comment);
    }

    public String getFileOrigin() {
        return FileOrigin.get();
    }

    public StringProperty fileOriginProperty() {
        return FileOrigin;
    }

    public void setFileOrigin(String fileOrigin) {
        this.FileOrigin.set(fileOrigin);
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    /**
     * DO NOT MODIFY THE ID UNLESS YOU HAVE A CRYSTAL CLEAR IDEA THAT DOING SO WILL 99/100 TIMES CORRUPT THE DATABASE IF DONE WRONG
     *
     * @param id literally the only way to identify data in an absolute way, that if 2 of the same id #'s are present will cause errors
     * @link https://dev.mysql.com/doc/refman/5.7/en/rebuilding-tables.html
     */
    public void setId(String id) {
        this.id.set(id);
    }

    public String[] getValuesNoID(){
        String[] values = new String[11];
        values[0] = this.getName();
        values[1] = this.getOwner();
        values[2] = this.getIPAddr();
        values[3] = this.getNetmask();
        values[4] = this.getVlanTag();
        values[5] = this.getZone();
        values[6] = this.getZoneType();
        values[7] = this.getVRF();
        values[8] = this.getLocation();
        values[9] = this.getComment();
        values[10] = this.getFileOrigin();
        return values;
    }

    public String[] getValues(){
        String[] values = this.getValuesNoID();
        String[] newValues = Arrays.copyOf(values, 12);
        newValues[11] = this.getId();
        return values;
    }

    @Override
    public String toString() {
        return "SQLEntry{" +
                "Name=" + Name.get() +
                ", Owner=" + Owner.get() +
                ", IPAddr=" + IPAddr.get() +
                ", Netmask=" + Netmask.get() +
                ", VlanTag=" + VlanTag.get() +
                ", Zone=" + Zone.get() +
                ", ZoneType=" + ZoneType.get() +
                ", VRF=" + VRF.get() +
                ", Location=" + Location.get() +
                ", Comment=" + Comment.get() +
                ", FileOrigin=" + FileOrigin.get() +
                ", id=" + id.get() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SQLEntry sqlEntry = (SQLEntry) o;
        return Objects.equals(getName(), sqlEntry.getName()) &&
                Objects.equals(getOwner(), sqlEntry.getOwner()) &&
                Objects.equals(getIPAddr(), sqlEntry.getIPAddr()) &&
                Objects.equals(getNetmask(), sqlEntry.getNetmask()) &&
                Objects.equals(getVlanTag(), sqlEntry.getVlanTag()) &&
                Objects.equals(getZone(), sqlEntry.getZone()) &&
                Objects.equals(getZoneType(), sqlEntry.getZoneType()) &&
                Objects.equals(getVRF(), sqlEntry.getVRF()) &&
                Objects.equals(getLocation(), sqlEntry.getLocation()) &&
                Objects.equals(getComment(), sqlEntry.getComment()) &&
                Objects.equals(getFileOrigin(), sqlEntry.getFileOrigin()) &&
                Objects.equals(getId(), sqlEntry.getId());
    }

    public boolean equalsIgnoreID(SQLEntry sqlEntry) {
        if (sqlEntry == null) return false;
        boolean nme = getName().equals(sqlEntry.getName());
        boolean ownr = getOwner().equals(sqlEntry.getOwner());
        boolean ipad = getIPAddr().equals(sqlEntry.getIPAddr());
        boolean mask = getNetmask().equals(sqlEntry.getNetmask());
        boolean vtag = getVlanTag().equals(sqlEntry.getVlanTag());
        boolean zne = getZone().equals(sqlEntry.getZone());
        boolean zntp = getZoneType().equals(sqlEntry.getZoneType());
        boolean veerf = getVRF().equals(sqlEntry.getVRF());
        boolean locn = getLocation().equals(sqlEntry.getLocation());
        /*
        System.out.println("Comparison:");
        System.out.println("Name: " + nme + "\tOwner: " + ownr);
        System.out.println("IPaddr: " + ipad + "\tNetmask: " + mask);
        System.out.println("VlanTag: " + vtag + "\t Zone, ZoneType: " + zne + " " + zntp);
        System.out.println("VRF: " + veerf + "\tLocation: " + locn);
        */
        return nme && ownr && ipad && mask && vtag && zne && zntp && veerf && locn;
    }

    @Override
    public int hashCode() {

        return Objects.hash(getName(), getOwner(), getIPAddr(), getNetmask(), getVlanTag(), getZone(), getZoneType(), getVRF(), getLocation(), getComment(), getFileOrigin(), getId());
    }
}



