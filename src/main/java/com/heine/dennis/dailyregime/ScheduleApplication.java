package com.heine.dennis.dailyregime;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.sql.*;

import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;

public class ScheduleApplication extends Application {

    public static Connection c;
    public static VBox root;
    public static Stage stage;
    public static int currentItemId=0;
    private static boolean isRuning=true;
    public static boolean floating=false;
    private static final String iconImageLoc = "Images/icon.png";
    private DateFormat timeFormat = SimpleDateFormat.getTimeInstance();

    //Source: https://stackoverflow.com/questions/40571199/creating-tray-icon-using-javafx
    private void addAppToTray() {
        try {
            java.awt.Toolkit.getDefaultToolkit();

            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();

            java.awt.Image image = ImageIO.read(new File(iconImageLoc));
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);

            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            java.awt.MenuItem openItem = new java.awt.MenuItem("Show");
            openItem.addActionListener(event -> Platform.runLater(this::showStage));

            java.awt.Font defaultFont = java.awt.Font.decode(null);
            java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
            openItem.setFont(boldFont);

            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(event -> {
                isRuning=false;
                Platform.exit();
                tray.remove(trayIcon);
            });

            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            tray.add(trayIcon);
        } catch (java.awt.AWTException | IOException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }

    /**
     * Shows the application stage and ensures that it is brought ot the front of all stages.
     */
    private void showStage() {
        if (stage != null) {
            stage.show();
            ScheduleApplication.stage.setY((Toolkit.getDefaultToolkit().getScreenSize().getHeight() - ScheduleApplication.stage.getHeight() + 65) / 2);
            new OutputPrinter(root).print();
            stage.toFront();
        }
    }

    private static void resizeThread(){
        while(isRuning && !floating)
        {
            try {
                String[] cmd = new String[]{"/bin/bash", "-c", "[ $(xwininfo -id $(xdotool getactivewindow) -all | awk '/Maximized/{print}' | wc -l) -eq 2 ] && (wmctrl -r :ACTIVE: -b remove,maximized_vert,maximized_horz ; wmctrl -r :ACTIVE: -e 0,1,1,1,1 ; wmctrl -r :ACTIVE: -e 0,0,0,1720,1080)"};
                Process pr = Runtime.getRuntime().exec(cmd);
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // [ $(xwininfo -id $(xdotool getactivewindow) -all | awk '/Maximized/{print}' | wc -l) -eq 2 ] & (wmctrl -r :ACTIVE: -b remove,maximized_vert,maximized_horz & wmctrl -r :ACTIVE: -e 0,1,1,1,1 & wmctrl -r :ACTIVE: -e 0,1,1,1720,1080)
        }
    }

    private void timerThread() {
        while(isRuning){
            try {
                LocalTime now = LocalTime.now();
                String sql = "SELECT * FROM TASKS ORDER BY `UNTIL`";
                Statement stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                boolean rowNum = false;
                boolean found=false;

                boolean cont=true;
                LocalTime itemTimeNext=null;
                while (rs.next()) {
                    int totalMinutes = rs.getInt("UNTIL");
                    int minutes = totalMinutes % 60;
                    int hours = (totalMinutes - minutes) / 60;

                    LocalTime itemTime=LocalTime.of(hours,minutes,0,0);

                    if(now.isBefore(itemTime)) {
                        currentItemId = rs.getInt("ID");
                        break;
                    }
               }

                Thread.sleep(5000);
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        new OutputPrinter(ScheduleApplication.root).print();
                    }
                });

            }catch (Exception e){System.out.println(e.getMessage()+e.getStackTrace());}

        }
    }

    private void connectDB()
    {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
            c.setAutoCommit(true);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        try{
            Statement stmt = null;
            stmt = c.createStatement();
            String sql = "CREATE TABLE TASKS " +
                    "(ID INTEGER PRIMARY KEY  AUTOINCREMENT   NOT NULL," +
                    "NAME           TEXT    NOT NULL, " +
                    "WEIGHT         INT     NOT NULL, " +
                    "UNTIL           INT" +
                    ")";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
        }catch(Exception e){System.out.println(e.getMessage()+e.getStackTrace());}
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.stage=stage;
        Platform.setImplicitExit(false);
        javax.swing.SwingUtilities.invokeLater(this::addAppToTray);
        connectDB();

        FXMLLoader fxmlLoader = new FXMLLoader(ScheduleApplication.class.getResource("hello-view.fxml"));
        root=fxmlLoader.load();
        Scene scene = new Scene(root);

        stage.setAlwaysOnTop(true);
        stage.setTitle("Daily Schedule");
        stage.setScene(scene);
       // stage.setResizable(false);


        stage.setMinHeight(300);
        root.setMinHeight(300);
        stage.setMinWidth(200);

        root.setMinWidth(200);

        stage.setX(Toolkit.getDefaultToolkit().getScreenSize().getWidth()-stage.getMinWidth());
        if(floating) {
            ScheduleApplication.stage.setY((Toolkit.getDefaultToolkit().getScreenSize().getHeight() - ScheduleApplication.stage.getHeight() + 65) / 2);
        }
       else {
            stage.setMinHeight(Toolkit.getDefaultToolkit().getScreenSize().getHeight());
            stage.setY(0);
        }




        stage.initStyle(StageStyle.UNDECORATED);

        stage.show();

        new OutputPrinter(root).print();

        (new MainThread()).start();
        if(!floating)
            (new ResizeThread()).start();

    }
    public class MainThread extends Thread {
        public void run() {
            timerThread();
        }
    }

    public static class ResizeThread extends Thread {
        public void run() {
            resizeThread();
        }
    }

    public static void toggleFloating()
    {
        if(floating)
        {
            floating=false;
            (new ResizeThread()).start();
            stage.setMinHeight(Toolkit.getDefaultToolkit().getScreenSize().getHeight());
            stage.setY(0);

        }
        else
        {
            floating=true;
            stage.setMinHeight(300);
            root.setMinHeight(300);
            new OutputPrinter(root).print();

            ScheduleApplication.stage.setY((Toolkit.getDefaultToolkit().getScreenSize().getHeight() - ScheduleApplication.stage.getHeight() + 65) / 2);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}