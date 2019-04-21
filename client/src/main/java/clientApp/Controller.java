package clientApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;

public class Controller {
    final String IP_ADRESS = "localhost";
    final int PORT = 8091;
    final String FOLDER = "client/folder/";

    Socket socket;
    FileOutputStream out;
    FileInputStream in;

    @FXML
    HBox authorizationPanel;

    @FXML
    VBox workingPanel;

    @FXML
    TableView<UserFile> clientFile;

    private void setAuthorized(boolean isAuthorized) {
        if (isAuthorized) {
            authorizationPanel.setVisible(!isAuthorized);
            workingPanel.setVisible(isAuthorized);

            initializeFilesTable();

            if (socket == null || socket.isClosed()) {
                connect();
            }

        } else {
            authorizationPanel.setVisible(isAuthorized);
            workingPanel.setVisible(!isAuthorized);
        }
    }

    public void tryToAuth(ActionEvent actionEvent) {
        setAuthorized(true);
    }

    public void initializeFilesTable() {
        TableColumn<UserFile, String> tcName = new TableColumn<>("Name");
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<UserFile, String> tcSize = new TableColumn<>("Size");
        tcSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        clientFile.getColumns().addAll(tcName, tcSize);

//        UserFilesStatic userFiles = new UserFilesStatic();
//        userFiles.scanFiles();

        clientFile.getItems().addAll();
    }

    public void connect() {
        try {
            socket = new Socket(IP_ADRESS, PORT);
            //* take stream for receiving data from server
            InputStream in = socket.getInputStream();
            //* write to socket
            OutputStream out = socket.getOutputStream();
            //* reference to file
            File file = new File(FOLDER + "location.txt");
            //* read data from file
            FileInputStream inFile = new FileInputStream(file);
            //*
            //out.write(in.available());

            byte[] arrBytes = new byte[inFile.available()];
            inFile.read(arrBytes);
            out.write(arrBytes);

            System.out.println(arrBytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
