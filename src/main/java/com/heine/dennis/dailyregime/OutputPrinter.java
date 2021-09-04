package com.heine.dennis.dailyregime;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.StageStyle;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OutputPrinter {

    private VBox root;

    public OutputPrinter(VBox root)
    {
        this.root=root;
    }

    public void delete()
    {
        for(Node n : root.getChildren())
        {
            if(n.getClass().getName().contains("VBox")) {
                root.getChildren().remove(n);
                delete();
                return;
            }
        }
    }

    public void print()
    {
        try {
            try {
                delete();
            }catch (Exception e){};

            Connection c= ScheduleApplication.c;
            String sql = "SELECT * FROM TASKS ORDER BY UNTIL";
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            boolean rowNum=false;
            while (rs.next()) {
                int totalMinutes=rs.getInt("UNTIL");
                int minutes=totalMinutes % 60;
                int hours=(totalMinutes-minutes)/60;

                Label label = new Label((rs.getString("NAME")));
                label.setStyle("-fx-font-weight:bold;");
                Label label1=new Label(String.format( "%02d",hours)+":"+ String.format("%02d",minutes));

                label.setTextAlignment(TextAlignment.CENTER);
                label1.setTextAlignment(TextAlignment.CENTER);

                HBox h=new HBox();
                int width=(int)root.getWidth();

                Hyperlink hl=new Hyperlink("Delete");
                hl.setFont(Font.font("Arial",10));
                hl.setStyle("-fx-text-fill: black;");

                hl.setUnderline(true);
                hl.setOnAction(onClickDelete(rs.getInt("ID")));

                h.setAlignment(Pos.CENTER);
                h.getChildren().add(hl);

                VBox centeredLabel = new VBox(label,label1,h);
                centeredLabel.setAlignment(Pos.CENTER);

                centeredLabel.setPadding(new Insets(5,20,5,20));
                String color="";

                if(rowNum)
                {
                    rowNum=false;
                    color="white";
                }
                else
                {
                    rowNum=true;
                    color="lightgrey";
                }

                if(ScheduleApplication.currentItemId!=0) {
                    if(rs.getInt("ID")== ScheduleApplication.currentItemId)
                    {
                        color="lightgreen";
                    }
                }

                centeredLabel.setStyle("-fx-background-color: "+color+";-fx-border-style:solid;");

                ScheduleApplication.root.getChildren().add(centeredLabel);
                double found=centeredLabel.getHeight()*2;
                int i;
                for(i=0;i<ScheduleApplication.root.getChildren().size();i++)
                {
//                    if(ScheduleApplication.root.getChildren().get(i).getClass().getName().contains("VBox")) {

                        if(ScheduleApplication.root.getChildren().get(i).getLayoutY()+( ScheduleApplication.root.getChildren().get(i)).getBoundsInLocal().getHeight()>found)
                            found=ScheduleApplication.root.getChildren().get(i).getLayoutY()+( ScheduleApplication.root.getChildren().get(i)).getBoundsInLocal().getHeight();
//                    }

                }

               // Bounds boundsInScene = centeredLabel.getBoundsInLocal();//centeredLabel.localToScene(centeredLabel.getBoundsInLocal());
             //   ScheduleApplication.stage.setResizable(true);

               /* ScheduleApplication.root.resize(ScheduleApplication.root.getWidth(), found);
                ScheduleApplication.root.setMaxHeight(found);
                ScheduleApplication.root.setMinHeight(found);
                */

                //ScheduleApplication.root.setMaxHeight(found);

                if(ScheduleApplication.floating)
                    ScheduleApplication.root.setMinHeight(found+65);

                ScheduleApplication.stage.sizeToScene();

/*
                ScheduleApplication.root.resize(ScheduleApplication.root.getWidth(), found);
                ScheduleApplication.root.getScene().getWindow().setHeight(found);
                ScheduleApplication.root.getScene().getWindow().sizeToScene();
                ScheduleApplication.stage.sizeToScene();
*/

              //  ScheduleApplication.stage.setResizable(false);
                ScheduleApplication.stage.setAlwaysOnTop(true);

            }
        }catch (Exception e){System.out.println(e.getMessage()+e.getStackTrace());}
    }

    private EventHandler<ActionEvent> onClickDown(int id) {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        };
    }

    private EventHandler<ActionEvent> onClickUp(int id) {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        };
    }

    private javafx.event.EventHandler<javafx.event.ActionEvent> onClickDelete(int id)
    {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Connection c= ScheduleApplication.c;
                String sql="DELETE FROM TASKS WHERE ID="+id;
                Statement stmt = null;
                try {
                    stmt = c.createStatement();
                    stmt.executeUpdate(sql);
                    new OutputPrinter(ScheduleApplication.root).print();
                    ScheduleApplication.stage.setY((Toolkit.getDefaultToolkit().getScreenSize().getHeight() - ScheduleApplication.stage.getHeight() + 65) / 2);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
