package cloudNettyServer.enums;

public enum ActionStage {
    //use in ClientAuthorization
    AUTHORIZED,
    UNAUTHORIZED,

    //use in ClientHandler
    GETTING_CLIENT_FOLDER,
    GETTING_COMMAND,
    GETTING_FILE_LENGTH,
    GETTING_FILE_NAME_LENGTH,
    GETTING_FILE_NAME,
    GETTING_FILE_CONTENT,
    SENDING_FILE_CONTENT,
    DELETE_FILES,
    SENDING_FILE_NAME_LIST,
}
