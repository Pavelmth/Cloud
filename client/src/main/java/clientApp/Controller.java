package clientApp;

import clientApp.fileWork.UserFile;
import clientApp.fileWork.UserFiles;
import clientApp.protocol.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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

    @FXML
    TableColumn<UserFile, String> clientFileName;

    @FXML
    TableColumn<UserFile, Long> clientFileSize;

    @FXML
    TableView<UserFile> serverFile;

    @FXML
    TableColumn<UserFile, String> serverFileName;

    @FXML
    TableColumn<UserFile, Long> serverFileSize;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passwordField;

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
        String login = loginField.getText();
        String password = passwordField.getText();

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
            System.out.println("Авторизация прошла успешно");
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
        try {
            byte byteCode;
            byteCode = new SendFiles().sendFiles(out, in, CLIENT_FOLDER, clientFile.getSelectionModel().getSelectedItem().getName());
            if (byteCode == 3) {
                resetServerSideView();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFileClient(ActionEvent actionEvent) {
        System.out.println("Action: deleteFileClient");
        new DeleteClientFile(clientFile.getSelectionModel().getSelectedItem().getName());
        resetClientSideView();
    }

    public void resetClient(ActionEvent actionEvent) {
        System.out.println("Action: resetClient");
        resetClientSideView();
    }

    public void downloadFile(ActionEvent actionEvent) {
        System.out.println("Action: downloadFile");
        String fileName = serverFile.getSelectionModel().getSelectedItem().getName();
        long fileLength = serverFile.getSelectionModel().getSelectedItem().getSize();
        try {
            byte byteCode;
            byteCode = new DownloadFiles().downloadFiles(out, in, CLIENT_FOLDER, fileName, fileLength);
            if (byteCode == 3) {
                resetClientSideView();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFileServer(ActionEvent actionEvent) {
        System.out.println("Action; deleteFileServer");
        byte byteCode = 0;
        try {
            byteCode = new DeleteServerFile().deleteServerFile(out, in, serverFile.getSelectionModel().getSelectedItem().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (byteCode == 3) {
            resetServerSideView();
        }
    }

    public void resetServer(ActionEvent actionEvent) {
        System.out.println("Action: resetServer");
        resetServerSideView();
    }

    public void initializeFilesTable() {
        clientFileName.setCellValueFactory(new PropertyValueFactory<UserFile, String>("name"));
        clientFileSize.setCellValueFactory(new PropertyValueFactory<UserFile, Long>("size"));

        serverFileName.setCellValueFactory(new PropertyValueFactory<UserFile, String>("name"));
        serverFileSize.setCellValueFactory(new PropertyValueFactory<UserFile, Long>("size"));

        resetClientSideView();
        resetServerSideView();
    }

    private void resetClientSideView() {
        clientFile.getItems().clear();
        UserFiles userFiles = new UserFiles(CLIENT_FOLDER);
        clientFile.getItems().addAll(userFiles.getUseFiles());
    }

    private void resetServerSideView() {
        serverFile.getItems().clear();
        try {
            ResetServer resetServer = new ResetServer(out, in);
            serverFile.getItems().addAll(resetServer.getUserFiles());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
