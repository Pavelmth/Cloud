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
    DataOutputStream out;
    DataInputStream in;

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

        } else {
            authorizationPanel.setVisible(isAuthorized);
            workingPanel.setVisible(!isAuthorized);
        }
    }

    public void connect() {
        try {
            socket = new Socket(IP_ADRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new ClientAuthService(out);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        setAuthorized(true);
    }

    public void sendFile(ActionEvent actionEvent) {
        String fileName = "location.txt";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new SendFiles(out, CLIENT_FOLDER, fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void downloadFile(ActionEvent actionEvent) {
        String fileName = "fileInServer.txt";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new DownloadFiles(out, in, CLIENT_FOLDER, fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
