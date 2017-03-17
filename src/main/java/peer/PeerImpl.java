package main.java.peer;

import javafx.util.Pair;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import javax.swing.Timer;

/**
 * Server part of the Peer
 */
public class PeerImpl implements PeerInt {
    private static final int MAX_ENTRIES = 100;
    String folder;
    Set<String> fileIndex;
    HashMap<Pair<String, Integer>, String> upstreamMap;
    HashMap<Pair<String, Integer>, Integer> invalidateMap;
    Map<String, ConsistentFile> fileMap;
    Map<String, Timer> timerMap;
    String thisIP;
    String[] neighbors;
    int defaultTTR;
    String mode;

    /**
     * Constructor for exporting each peer to the registry
     */
    public PeerImpl(String folder, String[] neighbors, Set<String> fileIndex, String id, int defaultTTR, String mode) {
        try {
            this.folder = folder;
            thisIP = id;
            this.neighbors = neighbors;
            this.fileIndex = fileIndex;
            this.defaultTTR = defaultTTR;
            //upstreamMap = new HashMap<Pair<String, Integer>, String>();
            upstreamMap = new LinkedHashMap<Pair<String, Integer>, String>(MAX_ENTRIES + 1, .75F, false){

                protected boolean removeEldestEntry(Map.Entry eldest) {
                    return size() > MAX_ENTRIES;
                }
            };
            invalidateMap = new LinkedHashMap<Pair<String, Integer>, Integer>(MAX_ENTRIES + 1, .75F, false){

                protected boolean removeEldestEntry(Map.Entry eldest) {
                    return size() > MAX_ENTRIES;
                }
            };
            fileMap = new HashMap();
            timerMap = new HashMap();
            this.mode = mode;
            PeerInt stub = (PeerInt) UnicastRemoteObject.exportObject(this, 0);
            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("PeerInt", stub);
            System.err.println("PeerImpl ready");
        } catch (Exception e) {
            System.err.println("PeerImpl exception: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Pass chunks of the file to the clients remote peer object until the file is written
     */
    public ConsistentFile obtain(String fileName)
	throws IOException {

        try {
            byte[] requestedFile = Files.readAllBytes(Paths.get(folder+"/"+fileName));
            ConsistentFile cf = fileMap.get(fileName);
            int TTR = 0;
            if(mode.equals("pull")) {
                if (thisIP.equals(cf.getOriginID())) {
                    TTR = defaultTTR;
                } else {
                    TTR = timerMap.get(fileName).getDelay();
                }
            }
            return new ConsistentFile(cf.getVersion(), thisIP, requestedFile, TTR);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        ConsistentFile x = new ConsistentFile();
        return x;
    }

    /**
     * Query a peer for a file, this query propagates until the time-to-live reaches 0
     */
    public void query (Pair<String, Integer> messageID, int TTL, String fileName)
            throws RemoteException {
	try {
        String upstreamIP = RemoteServer.getClientHost();
        if(!upstreamMap.containsKey(messageID) && TTL >= 0) {
            upstreamMap.put(messageID, upstreamIP);
		    if (fileIndex.contains(fileName)) {
		            if(fileMap.get(fileName).getState().equals(ConsistencyState.VALID)) {
                        queryhit(messageID, fileName, thisIP, 1099);
                    }
                    else if(mode.equals("push")){
                        System.out.println("Peer: " + thisIP + " has an invalid version of file : " + fileName + " and will not share with " + messageID.getKey());
                        }
                }
                if(TTL > 0)
                    queryNeighbors(fileName, TTL - 1, messageID);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A Query hit is propagated back to the original peer to request a file so a direct file request may begin
     */
    public void queryhit(Pair<String, Integer> messageID, String fileName, String peerIP, int portNumber)
            throws RemoteException {
        long time;
        try {
            if(messageID.getKey().equals(thisIP)){
                //Insert Time Stamp Log Here
		        time=System.nanoTime();
		        System.out.println("LOGGING: Receiving query: " + fileName + " " + time);
		        if(!fileIndex.contains(fileName)) {
                    Registry registry = LocateRegistry.getRegistry(peerIP, portNumber);
                    PeerInt peerStub = (PeerInt) registry.lookup("PeerInt");
                    fileIndex.add(fileName);
                    ConsistentFile cf = peerStub.obtain(fileName);
                    byte[] requestedFile = cf.getFile();
                    fileMap.put(fileName, cf);
                    writeFile(requestedFile, fileName);
                    class ExpireActionListener implements ActionListener {
                        private String fileName;

                        public ExpireActionListener(String fn) {fileName = fn;}

                        public void actionPerformed(ActionEvent e) {
                            fileMap.get(fileName).setState(ConsistencyState.EXPIRED);
                            System.out.println("File: " + fileName + " has expired");}
                    }
                    if(mode.equals("pull")) {
                        int delay = cf.getInitialTTR();
                        ActionListener actionListener = new ExpireActionListener(fileName);
                        Timer t = new Timer(delay, actionListener);
                        t.start();
                        timerMap.put(fileName, t);
                    }
                }
            }
            else {
                String upstreamIP = upstreamMap.get(messageID);
                Registry registry = LocateRegistry.getRegistry(upstreamIP, portNumber);
                PeerInt peerStub = (PeerInt) registry.lookup("PeerInt");
                peerStub.queryhit(messageID, fileName, peerIP, portNumber);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Query all of this peers neighbors for a file
     */
    public void queryNeighbors(String fileName, int TTL, Pair<String, Integer> messageID){
        try {
            for (String neighbor : neighbors) {
		//System.out.println("neighbor found: " + neighbor);
		Registry registry = LocateRegistry.getRegistry(neighbor,1099);
		//System.out.println("locate registry succeeded " + registry);
		PeerInt peerStub = (PeerInt) registry.lookup("PeerInt");
		//System.out.println("registry lookup " + peerStub);
		peerStub.query(messageID, TTL, fileName);
		//System.out.println("query succedded");
	    }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Write the file to the peer's local disk
     */
    public void writeFile(byte[] x, String fileName){
        try {
            System.out.println("LOGGING: Received File " + folder + "/peer/" + fileName);
            FileOutputStream out = new FileOutputStream(new File(folder + "/" + fileName));
            out.write(x);
            out.close();

        } catch (IOException e) {
            System.out.println("Exception" + e);
        }
    }

    public void invalidate(Pair<String, Integer> messageID, String fileName, int version)throws RemoteException{
        if(!invalidateMap.containsKey(messageID) || invalidateMap.get(messageID) != version){
            invalidateMap.put(messageID, version);
            if (fileMap.containsKey(fileName)){
                fileMap.get(fileName).setState(ConsistencyState.INVALID);
            }
        }
        invalidateNeighbors(messageID, fileName,version);
    }

    public void invalidateNeighbors(Pair<String, Integer> messageID, String fileName, int version){
        try {
            for (String neighbor : neighbors) {
                //System.out.println("neighbor found: " + neighbor);
                Registry registry = LocateRegistry.getRegistry(neighbor,1099);
                //System.out.println("locate registry succeeded " + registry);
                PeerInt peerStub = (PeerInt) registry.lookup("PeerInt");
                //System.out.println("registry lookup " + peerStub);
                peerStub. invalidate(messageID,fileName,version);
                //System.out.println("query succedded");
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public int poll(String fileName, int version)throws RemoteException{
        if (fileMap.get(fileName).getVersion() != version){
            return 0;
        }
        else{
            return defaultTTR;
        }
    }

    public void refresh(String fileName){
        try {
                ConsistentFile cf = fileMap.get(fileName);
                Registry registry = LocateRegistry.getRegistry(cf.getOriginID(), 1099);
                PeerInt peerStub = (PeerInt) registry.lookup("PeerInt");
                if(peerStub.poll(fileName, cf.getVersion()) != 0) {
                    cf = peerStub.obtain(fileName);
                    byte[] requestedFile = cf.getFile();
                    fileMap.put(fileName, cf);
                    writeFile(requestedFile, fileName);
                    System.out.println("File: " + fileName + " has been refreshed");
                }
                else{
                    System.out.println("File: " + fileName + " is currently up to date");
                }
            }
        catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void pseudoUpdate(){

    }

}
