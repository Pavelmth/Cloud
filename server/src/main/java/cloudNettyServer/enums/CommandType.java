package cloudNettyServer.enums;

public enum CommandType {
    EMPTY((byte) - 1), SEND_FILES((byte)15), DOWNLOAD_FILES((byte)16), DELETE_FILES((byte)17), RESET((byte)18);
    byte firstMessageByte;
    CommandType(byte firstMessageByte) {
        this.firstMessageByte = firstMessageByte;
    }

    public static CommandType getDataTypeFromByte(byte b) {
        if (b == SEND_FILES.firstMessageByte) {
            return SEND_FILES;
        }
        if (b == DOWNLOAD_FILES.firstMessageByte) {
            return DOWNLOAD_FILES;
        }
        if (b == DELETE_FILES.firstMessageByte) {
            return DELETE_FILES;
        }
        if (b == RESET.firstMessageByte) {
            return RESET;
        }
        return EMPTY;
    }
}
