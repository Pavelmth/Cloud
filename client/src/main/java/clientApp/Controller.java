package clientApp;

import clientApp.protocol.DownloadFiles;
import clientApp.protocol.SendFiles;
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
    final String CLIENT_FOLDER = "client/folder/";

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
//create  socket
                try {
                    socket = new Socket(IP_ADRESS, PORT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            authorizationPanel.setVisible(isAuthorized);
            workingPanel.setVisible(!isAuthorized);
        }
    }

    public void tryToAuth(ActionEvent actionEvent) {
        setAuthorized(true);
    }

    public void sendFile(ActionEvent actionEvent) {
        String fileName = "location.txt";
        new SendFiles(socket, CLIENT_FOLDER, fileName);
    }

    public void downloadFile(ActionEvent actionEvent) {
        String fileName = "location.txt";
        new DownloadFiles(socket, CLIENT_FOLDER, fileName);
    }

    public void initializeFilesTable() {
        TableColumn<UserFile, String> tcName = new TableColumn<>("Name");
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<UserFile, String> tcSize = new TableColumn<>("Size");
        tcSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        clientFile.getColumns().addAll(tcName, tcSize);

//        UserFiles userFiles = new UserFiles();
//        userFiles.scanFiles();

        clientFile.getItems().addAll();
    }
}
