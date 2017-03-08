package main.java.peer;

public class ConsistentFile{
    int version;
    String originID;
    byte[] file;
    ConsistencyState state;
    
    public ConsistentFile(int version, String originID, byte[] file, ConsistencyState state){
        this.version = version;
        this.originID = originID;
        this.file = file;
        this.state = state;
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
}