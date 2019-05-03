package cloudNettyServer.enums;

public enum CommandType {
    EMPTY((byte) - 1), SEND_FILES((byte)15), DOWNLOAD_FILES((byte)16);
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
        return EMPTY;
    }
}
