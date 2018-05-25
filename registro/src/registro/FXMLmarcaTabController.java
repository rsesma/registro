/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import registro.model.MarcaTab;
import registro.model.getRegistroData;

/**
 * FXML Controller class
 *
 * @author r
 */
public class FXMLmarcaTabController implements Initializable {

    @FXML
    private TableView<MarcaTab> table;
    @FXML
    private TableColumn<MarcaTab,String> codCol;
    @FXML
    private TableColumn<MarcaTab,String> marcaCol;
    @FXML
    private TableColumn<MarcaTab,String> alquitCol;
    @FXML
    private TableColumn<MarcaTab,String> nicCol;
    @FXML
    private TableColumn<MarcaTab,String> coCol;
    @FXML
    private TextField cod;
    @FXML
    private TextField marca;
    @FXML
    private TextField alq;
    @FXML
    private TextField nic;
    @FXML
    private TextField co;
    
    private ObservableList<MarcaTab> data = FXCollections.observableArrayList();
    
    public getRegistroData d;
    public Boolean changed = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        Callback<TableColumn<MarcaTab,String>, TableCell<MarcaTab,String>> cellMarca
                = (TableColumn<MarcaTab, String> param) -> new EditingCell("MARCA",false,this);
        Callback<TableColumn<MarcaTab,String>, TableCell<MarcaTab,String>> cellAlq
                = (TableColumn<MarcaTab, String> param) -> new EditingCell("ALQUIT",true,this);
        Callback<TableColumn<MarcaTab,String>, TableCell<MarcaTab,String>> cellNic
                = (TableColumn<MarcaTab, String> param) -> new EditingCell("NICOT",true,this);
        Callback<TableColumn<MarcaTab,String>, TableCell<MarcaTab,String>> cellCO
                = (TableColumn<MarcaTab, String> param) -> new EditingCell("CO",true,this);

        
        codCol.setCellValueFactory(cellData -> cellData.getValue().codProperty());
        
        marcaCol.setCellValueFactory(cellData -> cellData.getValue().marcaProperty());
        marcaCol.setCellFactory(cellMarca);
        
        alquitCol.setCellValueFactory(cellData -> cellData.getValue().alqProperty());
        alquitCol.setCellFactory(cellAlq);

        nicCol.setCellValueFactory(cellData -> cellData.getValue().nicProperty());
        nicCol.setCellFactory(cellNic);
        
        coCol.setCellValueFactory(cellData -> cellData.getValue().coProperty());
        coCol.setCellFactory(cellCO);        
        
        table.setItems(data);
        
        String valid = "0123456789.";
        cod.setOnKeyTyped((KeyEvent e) -> {if (!valid.contains(e.getCharacter())) e.consume(); });
        alq.setOnKeyTyped((KeyEvent e) -> {if (!valid.contains(e.getCharacter())) e.consume(); });
        nic.setOnKeyTyped((KeyEvent e) -> {if (!valid.contains(e.getCharacter())) e.consume(); });
        co.setOnKeyTyped((KeyEvent e) -> {if (!valid.contains(e.getCharacter())) e.consume(); });
    }
    
    @FXML
    void addMarca(ActionEvent event) {
        if (!cod.getText().trim().isEmpty() && !marca.getText().trim().isEmpty()) {
            if (!d.ExisteCodMarcaTab(cod.getText())) {
                if (!d.ExisteMarcaTab(marca.getText())) {
                    MarcaTab m = new MarcaTab(cod.getText(),marca.getText(),alq.getText(),nic.getText(),co.getText());
                    this.d.addTabaco(m);
                    this.data.add(m);
                    this.changed = true;
                    
                    cod.setText("");
                    marca.setText("");
                    alq.setText("");
                    nic.setText("");
                    co.setText("");
                } else {
                    Alert a = new Alert(Alert.AlertType.WARNING, "La marca " + marca.getText() + " ya existe en el diccionario");
                    a.setTitle("Error");
                    a.setHeaderText("Marca repetida");
                    a.showAndWait();
                }
            } else {
                Alert a = new Alert(Alert.AlertType.WARNING, "El código " + cod.getText() + " ya existe en el diccionario");
                a.setTitle("Error");
                a.setHeaderText("Identificador repetido");
                a.showAndWait();
            }
        } else {
            Alert a = new Alert(Alert.AlertType.WARNING, "El código y la marca son necesarios");
            a.setTitle("Error");
            a.setHeaderText("Falta información");
            a.showAndWait();
        }
    }

    @FXML
    void delMarca(ActionEvent event) {
        MarcaTab m = table.getSelectionModel().getSelectedItem();
        if (m != null) {
            if (!d.MarcaTabEnUso(m.getCod())) {
                this.d.delTabaco(m);
                data.remove(m);
                this.changed = true;
            } else {
                Alert a = new Alert(Alert.AlertType.WARNING, "La marca " + m.getMarca() + " está en uso");
                a.setTitle("Atención");
                a.setHeaderText("No se puede borrar el registro");
                a.showAndWait();
            }
            
        }
    }

    @FXML
    void pbCerrar(ActionEvent event) {
        Stage stage = (Stage) this.table.getScene().getWindow();
        stage.close();
    }
    
    public void SetData(getRegistroData d) {
        this.d = d;
        
        try {
            ResultSet rs = this.d.getDic("DTabaco");
            while (rs.next()) {
                this.data.add(new MarcaTab(rs.getString("IDMARC"),rs.getString("MARCA"),rs.getString("ALQUIT"),rs.getString("NICOT"),rs.getString("CO")));
            }
            rs.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public String getCodMarcaSelected() {
        return table.getSelectionModel().getSelectedItem().getCod();
    }
    
    
    class EditingCell extends TableCell<MarcaTab, String> {
        private TextField textField;
        private Boolean numeric;
        private FXMLmarcaTabController contr;
        private String field;

        private EditingCell(String field, Boolean numeric, FXMLmarcaTabController o) {
            this.field = field;
            this.numeric = numeric;
            this.contr = o;
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
                    if (textField != null) textField.setText(getString());
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
            if (this.numeric) {
                textField.setOnKeyTyped((KeyEvent e) -> { 
                    if (!"0123456789.".contains(e.getCharacter())) e.consume();
                });
            }
            textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (!newValue) {
                    this.contr.d.updateTabaco(textField.getText(),this.field,this.contr.getCodMarcaSelected());
                    this.contr.changed = true;
                    commitEdit(textField.getText());
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }
}
