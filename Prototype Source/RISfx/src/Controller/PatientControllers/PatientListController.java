package Controller.PatientControllers;

import Controller.Main;
import Model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PatientListController implements Initializable {

      ////////////////////////
     //Variable Declaration//
    ////////////////////////
    @FXML private Button                            uniButton;
    @FXML private TableView<Patient>                PatientList;
    @FXML private TableColumn<Patient, String>      patientID, firstname, lastname, dob, sex, email;
    @FXML private TableColumn<Patient, Integer>     phoneNumber;


      ////////////////
     //Initializers//
    ////////////////
    public void initialize(URL url, ResourceBundle arg1) {
        //setSQLQuery("select title, description, content FROM item");
        ArrayList pms = Main.getSessionUser().getPermissions();
        if(pms.contains(1)){
            try {
                updateTable(getPatientList(Main.getSessionUser().getEmployeeId()));
                uniButton.setText("New Referral");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            updateTable();
        PatientList.setOnMouseClicked((MouseEvent event) -> {
            //DOUBLE CLICK ON CELL
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
                try{
                    sendPatientToView(PatientList.getSelectionModel().getSelectedItem());
                    setPatientView();

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public static void setView()throws Exception{
        Main.setCenterPane("PatientViews/PatientList.fxml");
    }

    public void updateTable() {
        try {

            PatientList.setItems(getPatientList());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("UNABLE TO FILL TABLE");
            e.printStackTrace();
        }
        fillTable();
    }
    public void updateTable(ObservableList<Patient> patients) {
        PatientList.setItems(patients);
        fillTable();
    }


      ////////////////////
     //Database Queries//
    ////////////////////



      ///////////////////
     //List Generators//
    ///////////////////
    @SuppressWarnings("Duplicates")
    public ObservableList<Patient>  getPatientList() throws Exception {
        ObservableList<Patient> patients = FXCollections.observableArrayList();

        ResultSet resultSet = Patient.queryAllPatients();
        while (resultSet.next()) {
            patients.add(new Patient(
                    resultSet.getInt("patient_id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    dateFormatter(resultSet.getString("date_of_birth")),
                    resultSet.getString("sex"),
                    resultSet.getString("home_phone"),
                    resultSet.getString("email"),
                    resultSet.getString("insurance_number"),
                    resultSet.getString("policy_number"),
                    ""
            ));
        }
        return patients;
    }
    @SuppressWarnings("Duplicates")
    public ObservableList<Patient>  getPatientList(int EmployeeID) throws Exception {
        ObservableList<Patient> patients = FXCollections.observableArrayList();

        ResultSet resultSet = Patient.queryPatients(EmployeeID);
        while (resultSet.next()) {
            patients.add(new Patient(
                    resultSet.getInt("patient_id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    dateFormatter(resultSet.getString("date_of_birth")),
                    resultSet.getString("sex"),
                    resultSet.getString("home_phone"),
                    resultSet.getString("email"),
                    resultSet.getString("insurance_number"),
                    resultSet.getString("policy_number"),
                    ""
            ));
        }
        return patients;
    }


      //////////////////
     //Button Methods//
    //////////////////
    public void setAddPatientView()throws Exception{
        NewPatientController.setView();
    }

    public static void setPatientView()throws Exception{
       PatientViewController.setView();
    }


      ///////////////////
     //Form Validation//
    ///////////////////
    private LocalDate dateFormatter(String date){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, format);
    }

    private void sendPatientToView(Patient selectedItem) throws Exception{
        int patient_id = selectedItem.getPatientID();

        ResultSet rs = Patient.queryPatientInfo(patient_id);
        rs.next();

        ResultSet addr = Patient.queryAddress(rs.getInt("address_id"));
        addr.next();

        String address = addr.getString("street_name") + ", " + addr.getString("city") + ", " + addr.getString("state") + ", " + addr.getInt("zip") ;
        Main.setPatientFocus((new Patient(
                rs.getInt("patient_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                LocalDate.parse(rs.getString("date_of_birth")),
                rs.getString("sex"),
                rs.getString("home_phone"),
                rs.getString("email"),
                rs.getString("insurance_number"),
                rs.getString("policy_number"),
                address
                )));
    }

    private void fillTable(){
        patientID.setCellValueFactory(new PropertyValueFactory<Patient, String>("patientID"));
        firstname.setCellValueFactory(new PropertyValueFactory<Patient, String>("firstname"));
        lastname.setCellValueFactory(new PropertyValueFactory<Patient, String>("lastname"));
        dob.setCellValueFactory(new PropertyValueFactory<Patient, String>("dob"));
        sex.setCellValueFactory(new PropertyValueFactory<Patient, String>("sex"));
        phoneNumber.setCellValueFactory(new PropertyValueFactory<Patient, Integer>("phoneNumber"));
        email.setCellValueFactory(new PropertyValueFactory<Patient, String>("email"));
    }
}
