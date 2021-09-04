package com.heine.dennis.dailyregime;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class AddItemController {

    @FXML
    private TextField txtName;

    @FXML private Spinner txtBisStunde;


    @FXML private Spinner txtBisMinute;

    @FXML
    protected void onOkButtonClick() {
        try {

            if(txtName.getText().toString()=="" || txtBisStunde.getEditor().getText().toString()=="" || txtBisMinute.getEditor().getText().toString()=="")
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Please fill all fields");
                alert.showAndWait();
            }
            else if(Integer.parseInt(txtBisStunde.getEditor().getText().toString())>23||Integer.parseInt(txtBisMinute.getEditor().getText().toString())>59 )
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Please enter a correct time");
                alert.showAndWait();
            }
            else {
                Connection c = ScheduleApplication.c;

                int until = (Integer.parseInt(txtBisMinute.getEditor().getText().toString()) + (Integer.parseInt(txtBisStunde.getEditor().getText().toString()) * 60));
                int max;

                try {
                    String sql = "SELECT MAX(WEIGHT) FROM TASKS";
                    Statement stmt1 = c.createStatement();
                    ResultSet rs = stmt1.executeQuery(sql);
                    rs.next();
                    max = rs.getInt(0) + 1;
                } catch (Exception e) {
                    max = 0;
                }
                Statement stmt = c.createStatement();

                String sql1 = "INSERT INTO TASKS (NAME,WEIGHT,UNTIL) " +
                        "VALUES ('" + txtName.getText().toString() + "'," + max + ", " + String.valueOf(until) + " );";
                stmt.executeUpdate(sql1);

                Stage stage = (Stage) txtName.getScene().getWindow();

                stage.close();
            }
        }catch(Exception e){System.out.println(e.getMessage()+e.getStackTrace());}
    }

    @FXML
    protected void onCancelButtonClick() {
        Stage stage = (Stage) txtName.getScene().getWindow();
        stage.close();
    }


}
