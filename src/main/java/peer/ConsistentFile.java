package main.java.peer;

public class ConsistentFile {
    int version;
    String originID;
    byte[] file;
    ConsistencyState state;

    public ConsistentFile(){
        version = 0;
        originID = "";
        file = "x".getBytes();
    }

    public ConsistentFile(int version, String originID, byte[] file) {
        this.version = version;
        this.originID = originID;
        this.file = file;
        state = ConsistencyState.VALID;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getOriginID() {
        return originID;
    }

    public void setOriginID(String originID) {
        this.originID = originID;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public ConsistencyState getState() {
        return state;
    }

    public void setState(ConsistencyState state) {
        this.state = state;
    }

}