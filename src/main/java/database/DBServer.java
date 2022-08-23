package database;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** This class implements the DB server. */
public final class DBServer {

  private static final char END_OF_TRANSMISSION = 4;
  private ArrayList<DB> databaseList;
  private int currentDatabase;
  private String directory;

  public static void main(String[] args) throws IOException {
    new DBServer(Paths.get(".").toAbsolutePath().toFile()).blockingListenOn(8888);

  }

  /**
   * @param databaseDirectory The directory to use for storing any persistent database files such
   *     that starting a new instance of the server with the same directory will restore all
   *     databases.
   */
  public DBServer(File databaseDirectory){
    currentDatabase = -1;
    databaseList = new ArrayList<>();
    directory = databaseDirectory.getPath();
    try {
      readDBFiles();
    }
    catch (Exception e) {
      System.out.println("Error reading DB files");
    }
  }

  public ArrayList<String> getDatabaseList(){
    ArrayList<String> DBList = new ArrayList<>();
    for (DB d : databaseList){
      DBList.add(d.getDatabaseName());
    }
    return DBList;
  }

  public boolean addDB (DB d) {
    if (databaseList.contains(d)) {
      return false;
    }
    databaseList.add(d);
    d.setDirectory(directory + "/" + d.getDatabaseName());
    currentDatabase++;
    File dir;
    try {
      dir = new File(directory + "/" + d.getDatabaseName());
      dir.mkdir();
    } catch (Exception e) {
      return false;
    }
    List<String> filesList = new ArrayList<>();
    if (dir.list()!=null) {
      filesList = Arrays.asList(dir.list());
    }
    if (!filesList.contains("ids.id")) {
      d.makeIdsTable();
    }
    return true;
  }

  public DB getDB(String name){
    for (DB d : databaseList){
      if (d.getDatabaseName().equals(name)){
        return d;
      }
    }
    return null;
  }

  public DB getDB(){
    return getDB(databaseList.get(currentDatabase).getDatabaseName());
  }

  public String getDirectory(){
    return directory;
  }

  public void readDBFiles() throws Exception {
    File file = new File(directory);
    String[] directories = file.list((current, name) -> new File(current, name).isDirectory());
    if (directories == null) {
      return;
    }
    for (String dir : directories){
      if (!dir.equals("src") && !dir.equals("target") && !dir.equals(".mvn") && !dir.equals(".idea")){
        if (!getDBList().contains(dir)) {
          DB newDB = new DB(dir);
          addDB(newDB);
        }
        setDatabase(dir);
        File path = new File(directory + "/" + dir);
        FileFilter fileFilter = nfile -> !nfile.isDirectory() && nfile.getName().endsWith(".tab");
        for (File f : Objects.requireNonNull(path.listFiles(fileFilter))){
            String name = f.getName().substring(0, f.getName().length()-4);
            getDB().addTable(name);
            getDB().getTable(name).readFile(f);
        }
        fileFilter = nfile -> !nfile.isDirectory() && nfile.getName().endsWith(".id");
        if (path.listFiles(fileFilter) !=null && path.listFiles(fileFilter).length > 1){
          return;
        }
        File idsFile = path.listFiles(fileFilter)[0];
        readIDsFile(idsFile);
      }
    }
  }

  private void readIDsFile(File idsFile){
    getDB().idsTable = new Table("ids");
    getDB().idsTable.setDBDirectory(getDB().getDirectory());
    try {
      getDB().idsTable.readFile(idsFile);
    } catch (Exception e) {
      return;
    }
    for (Row r : getDB().idsTable.getRows()){
      String tableName = r.getValueByIndex(1);
      String uniqueId = r.getValueByIndex(2);
      getDB().getTable(tableName).setUniqueRowId(Integer.parseInt(uniqueId));
    }

  }

  private ArrayList<String> getDBList(){
    ArrayList<String> result = new ArrayList<>();
    for (DB d : databaseList){
      result.add(d.getDatabaseName());
    }
    return result;
  }

  /**
   * This method handles all incoming DB commands and carry out the corresponding actions.
   */
  public String handleCommand(String command) {
    if (command==null){
      return null;
    }
    Parser p = new Parser(command);
    DBcmd c = p.parse();
    String result;
    if (c != null) {
      result = p.getCommand().query(this);
    }
    else {
      return "[ERROR]: " + p.getStatusMessage();
    }
    if (!c.isSuccessful()) {
      return "[ERROR]: " + result;
    }
    return "[OK]\n" + result;
  }

  //  === Methods below are there to facilitate server related operations. ===

  /**
   * Starts a *blocking* socket server listening for new connections. This method blocks until the
   * current thread is interrupted.
   *
   *
   * @param portNumber The port to listen on.
   * @throws IOException If any IO related operation fails.
   */
  public void blockingListenOn(int portNumber) throws IOException {
    try (ServerSocket s = new ServerSocket(portNumber)) {
      System.out.println("Server listening on port " + portNumber);
      while (!Thread.interrupted()) {
        try {
          blockingHandleConnection(s);
        } catch (IOException e) {
          System.err.println("Server encountered a non-fatal IO error:");
          e.printStackTrace();
          System.err.println("Continuing...");
        }
      }
    }
  }

  /**
   * Handles an incoming connection from the socket server.
   *
   * @param serverSocket The client socket to read/write from.
   * @throws IOException If any IO related operation fails.
   */
  private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
    try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

      System.out.println("Connection established: " + serverSocket.getInetAddress());
      while (!Thread.interrupted()) {
        String incomingCommand = reader.readLine();
        System.out.println("Received message: " + incomingCommand);
        String result = handleCommand(incomingCommand);
        writer.write(result);
        writer.write("\n" + END_OF_TRANSMISSION + "\n");
        writer.flush();
      }
    }
  }

  public boolean setDatabase(String name){
    if (databaseList == null){
      return false;
    }
    boolean DBexists = false;
    for (int i=0; i < databaseList.size(); i++){
      if (databaseList.get(i).getDatabaseName().equals(name)){
        currentDatabase = i;
        DBexists = true;
      }
    }
    return DBexists;
  }

  public boolean removeDB(String name){

    if (getDBIndex(name) != -1){
      databaseList.remove(getDBIndex(name));
    }
    else{
      return false;
    }
    String dirName = directory + "/" + name;
    try {
      File directoryToBeDeleted = new File(dirName);
      File[] allContents = directoryToBeDeleted.listFiles();
      if (allContents != null) {
        for (File file : allContents) {
          file.delete();
        }
      }
      return directoryToBeDeleted.delete();
    }
    catch (Exception e){
      return false;
    }
  }

  private int getDBIndex(String name){
    for (int i=0; i<databaseList.size(); i++){
      if (databaseList.get(i).getDatabaseName().equals(name)){
        return i;
      }
    }
    return -1;
  }

}
