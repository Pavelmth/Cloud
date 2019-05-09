package clientApp;

import clientApp.protocol.ClientAuthService;
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

    public void tryToAuth(ActionEvent actionEvent) {
        String login = "Ivan84";
        String password = "pass1";
        byte responseCod = -1;

        System.out.println("Action: tryToAuth");
        if (socket == null || socket.isClosed()) {
            try {
                socket = new Socket(IP_ADRESS, PORT);
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                responseCod = new ClientAuthService().clientAuthService(out, in, login, password);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (responseCod != -1 && responseCod != 31 && responseCod != 32) {
            setAuthorized(true);
        } else if (responseCod == 31) {
            System.out.println("Пользователя с таким логином нет в базе");
            try {
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (responseCod == 32) {
            System.out.println("Логин и пароль не совпадают");
            try {
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Что-то пошло не так");
            try {
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendFile(ActionEvent actionEvent) {
        System.out.println("Action: sendFile");
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

    public void deleteFileClient(ActionEvent actionEvent) {
        System.out.println("Action: deleteFileClient");

    }

    public void resetClient(ActionEvent actionEvent) {
        System.out.println("Action: resetClient");
    }

    public void downloadFile(ActionEvent actionEvent) {
        System.out.println("Action: downloadFile");
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

    public void deleteFileServer(ActionEvent actionEvent) {
        System.out.println("Action; deleteFileServer");
    }

    public void resetServer(ActionEvent actionEvent) {
        System.out.println("Action: resetServer");
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
