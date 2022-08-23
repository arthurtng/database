package database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// PLEASE READ:
// The tests in this file will fail by default for a template skeleton, your job is to pass them
// and maybe write some more, read up on how to write tests at
// https://junit.org/junit5/docs/current/user-guide/#writing-tests
final class DBTests {

  private DBServer server;


  // we make a new server for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup(@TempDir File dbDir) {
    // Notice the @TempDir annotation, this instructs JUnit to create a new temp directory somewhere
    // and proceeds to *delete* that directory when the test finishes.
    // You can read the specifics of this at
    // https://junit.org/junit5/docs/5.4.2/api/org/junit/jupiter/api/io/TempDir.html

    // If you want to inspect the content of the directory during/after a test run for debugging,
    // simply replace `dbDir` here with your own File instance that points to somewhere you know.
    // IMPORTANT: If you do this, make sure you rerun the tests using `dbDir` again to make sure it
    // still works and keep it that way for the submission.

    server = new DBServer(dbDir);
  }

  // Here's a basic test for spawning a new server and sending an invalid command,
  // the spec dictates that the server respond with something that starts with `[ERROR]`
  @Test
  void testInvalidCommandIsAnError() {
    assertTrue(server.handleCommand("foo").startsWith("[ERROR]"));
  }

  // Add more unit tests or integration tests here.
  // Unit tests would test individual methods or classes whereas integration tests are geared
  // towards a specific usecase (i.e. creating a table and inserting rows and asserting whether the
  // rows are actually inserted)


  // Table methods
  @Test
  void setDBDirectory(){
    Table newTable = new Table("newTable");
    newTable.setDBDirectory(".");
    assertEquals(".", newTable.getDBDirectory());
  }

  @Test
  void insertColumnInTable(){
    Table newTable = new Table("newTable");
    newTable.addColumnHeading("name");
    newTable.addColumnHeading("age");
    // id automatically added
    assertTrue(newTable.getColumnHeadings().contains("id"));
    assertTrue(newTable.getColumnHeadings().contains("name"));
    assertTrue(newTable.getColumnHeadings().contains("age"));
  }

  @Test
  void removeColumnFromTable(){
    Table newTable = new Table("newTable");
    newTable.addColumnHeading("name");
    newTable.addColumnHeading("age");
    newTable.addColumnHeading("email");
    newTable.removeColumnHeading("age");
    assertTrue(newTable.getColumnHeadings().contains("id"));
    assertTrue(newTable.getColumnHeadings().contains("name"));
    assertFalse(newTable.getColumnHeadings().contains("age"));
    assertTrue(newTable.getColumnHeadings().contains("email"));
  }

  @Test
  void insertRowInTable(){
    Table newTable = new Table("newTable");
    newTable.addColumnHeading("name");
    newTable.addColumnHeading("age");
    // id automatically added
    newTable.insertRow("Andrew 20", true);
    assertEquals("1", newTable.getRow(0).getValueByIndex(0));
    assertEquals("Andrew", newTable.getRow(0).getValueByIndex(1));
    assertEquals("20", newTable.getRow(0).getValueByIndex(2));
  }

  @Test
  void insertMultipleRowsInTable(){
    Table newTable = new Table("newTable");
    newTable.addColumnHeading("name");
    newTable.addColumnHeading("age");
    // id automatically added
    newTable.insertRow("Andrew 20", true);
    newTable.insertRow("John 32", true);
    assertEquals("2", newTable.getRow(1).getValueByIndex(0));
    assertEquals("John", newTable.getRow(1).getValueByIndex(1));
    assertEquals("32", newTable.getRow(1).getValueByIndex(2));
  }

  @Test
  void getRowByIdFromTable(){
    Table newTable = new Table("newTable");
    newTable.addColumnHeading("name");
    newTable.addColumnHeading("age");
    // id automatically added
    newTable.insertRow("Andrew 20", true);
    newTable.insertRow("John 32", true);
    assertEquals("2", newTable.getRowById(2).getValueByIndex(0));
    assertEquals("John", newTable.getRowById(2).getValueByIndex(1));
    assertEquals("32", newTable.getRowById(2).getValueByIndex(2));
  }

  @Test
  void getRowIdFromRowNumber(){
    Table newTable = new Table("newTable");
    newTable.addColumnHeading("name");
    newTable.addColumnHeading("age");
    // id automatically added
    newTable.insertRow("Andrew 20", true);
    newTable.insertRow("John 32", true);
    assertEquals(1, newTable.getRowId(0));
    assertEquals(2, newTable.getRowId(1));
  }

  @Test
  void removeRowFromTable(){
    Table newTable = new Table("newTable");
    newTable.addColumnHeading("name");
    newTable.addColumnHeading("age");
    // id automatically added
    newTable.insertRow("Andrew 20", true);
    newTable.insertRow("John 32", true);
    newTable.removeRowById(1);
    assertNotEquals("1", newTable.getRow(0).getValueByIndex(0));
    assertNotEquals("Andrew", newTable.getRow(0).getValueByIndex(1));
    assertNotEquals("20", newTable.getRow(0).getValueByIndex(2));

  }

  @Test
  void editRowsInTable(){
    Table newTable = new Table("newTable");
    newTable.addColumnHeading("name");
    newTable.addColumnHeading("age");
    // id automatically added
    newTable.insertRow("Andrew 20", true);
    newTable.insertRow("John 32", true);
    newTable.editRowById(newTable.getRowId(0), "name", "Andy");
    newTable.editRowById(newTable.getRowId(1), "age", "30");
    assertEquals("Andy", newTable.getRowById(1).getValueByIndex(1));
    assertEquals("30", newTable.getRowById(2).getValueByIndex(2));
  }

  @Test
  void getNumberOfRowsInTable(){
    Table newTable = new Table("newTable");
    newTable.addColumnHeading("name");
    newTable.addColumnHeading("age");
    // id automatically added
    newTable.insertRow("Andrew 20", true);
    newTable.insertRow("John 32", true);
    newTable.insertRow("Tony 22", true);
    assertEquals(3, newTable.getNumOfRows());
  }



  // Reading and writing files
  @Test
  void writeFileToDir() {
    Table newTable = new Table("newTable");
    newTable.setDBDirectory(server.getDirectory());
    try {
      newTable.writeToFile(".tab");
    } catch (Exception e) {
      assert(false);
    }
    File dir = new File(server.getDirectory());
    Set<String> filesList = Set.of(dir.list());
    assertTrue(filesList.contains("newTable.tab"));
  }

  @Test
  void readFileFromDir() {
    Table newTable = new Table("testTable");
    newTable.setDBDirectory(server.getDirectory());
    newTable.addColumnHeading("testHeading");
    newTable.insertRow("testData", true);
    try {
      newTable.writeToFile(".tab");
    } catch (IOException e) {
      assert(false);
    }
    File fileToOpen = new File(server.getDirectory() + "/testTable.tab");
    Table readTable = new Table("newTable");
    try {
      readTable.readFile(fileToOpen);
    } catch (IOException e) {
      assert(false);
    }
    assertTrue(readTable.getColumnHeadings().contains("testHeading"));
    assertTrue(readTable.getRow(0).toString().contains("testData"));
  }

  @Test
  void writeDBToDir(){
    DB testDB = new DB("testDB");
    server.addDB(testDB);
    File dir = new File(server.getDirectory());
    Set<String> filesList = Set.of(dir.list());
    assertTrue(filesList.contains("testDB"));
  }

  @Test
  void readDBFromDir() {
    DB testDB = new DB("testDB");
    server.addDB(testDB);
    try {
      server.readDBFiles();
    }
    catch(Exception e){
      assert(false);
    }
    assertTrue(server.getDatabaseList().contains("testDB"));
  }

  @Test
  void readMultipleDBsFromDir() {
    DB testDB = new DB("testDB");
    server.addDB(testDB);
    DB testDB2 = new DB("testDB2");
    server.addDB(testDB2);
    try {
      server.readDBFiles();
    }
    catch(Exception e){
      assert(false);
    }
    assertTrue(server.getDatabaseList().contains("testDB"));
    assertTrue(server.getDatabaseList().contains("testDB2"));
  }

  @Test
  void readDBAndFilesFromDir() {
    DB testDB = new DB("testDB");
    server.addDB(testDB);
    server.setDatabase("testDB");
    Table newTable = new Table("newTable");
    newTable.setDBDirectory(server.getDB().getDirectory());
    Table newTable2 = new Table("newTable2");
    newTable2.setDBDirectory(server.getDB().getDirectory());
    try {
      newTable.writeToFile(".tab");
      newTable2.writeToFile(".tab");
    } catch (Exception e) {
      assert(false);
    }
    try {
      server.readDBFiles();
    }
    catch(Exception e){
      assert(false);
    }
    assertTrue(server.getDatabaseList().contains("testDB"));
    assertTrue(server.getDB().getTableList().contains("newTable"));
    assertTrue(server.getDB().getTableList().contains("newTable2"));

  }


  // Tokenizer tests
  @Test
  void tokenizeSimpleCommand() {
    Tokenizer t = new Tokenizer();
    ArrayList<Token> tokens = t.getTokens("CREATE DATABASE fakedatabase;");
    assertEquals(4, tokens.size());
    assertEquals("CT", tokens.get(0).getTokenType());
    assertEquals("CREATE", tokens.get(0).getWord());
    assertEquals("KW", tokens.get(1).getTokenType());
    assertEquals("DATABASE", tokens.get(1).getWord());
    assertEquals("ID", tokens.get(2).getTokenType());
    assertEquals("fakedatabase", tokens.get(2).getWord());
    assertEquals("SY", tokens.get(3).getTokenType());
    assertEquals(";", tokens.get(3).getWord());
  }

  @Test
  void tokenizeBrackets() {
    Tokenizer t = new Tokenizer();
    ArrayList<Token> tokens = t.getTokens("CREATE TABLE faketable (name, age, occupation);");
    assertEquals(11, tokens.size());
    assertEquals("CT", tokens.get(0).getTokenType());
    assertEquals("CREATE", tokens.get(0).getWord());
    assertEquals("KW", tokens.get(1).getTokenType());
    assertEquals("TABLE", tokens.get(1).getWord());
    assertEquals("ID", tokens.get(2).getTokenType());
    assertEquals("faketable", tokens.get(2).getWord());
    assertEquals("LB", tokens.get(3).getTokenType());
    assertEquals("(", tokens.get(3).getWord());
    assertEquals("ID", tokens.get(4).getTokenType());
    assertEquals("name", tokens.get(4).getWord());
    assertEquals("SY", tokens.get(5).getTokenType());
    assertEquals(",", tokens.get(5).getWord());
    assertEquals("ID", tokens.get(6).getTokenType());
    assertEquals("age", tokens.get(6).getWord());
    assertEquals("SY", tokens.get(7).getTokenType());
    assertEquals(",", tokens.get(7).getWord());
    assertEquals("ID", tokens.get(8).getTokenType());
    assertEquals("occupation", tokens.get(8).getWord());
    assertEquals("RB", tokens.get(9).getTokenType());
    assertEquals(")", tokens.get(9).getWord());
    assertEquals("SY", tokens.get(10).getTokenType());
    assertEquals(";", tokens.get(10).getWord());

  }

  @Test
  void tokenizeSpaceBrackets() {
    Tokenizer t = new Tokenizer();
    ArrayList<Token> tokens = t.getTokens("CREATE TABLE faketable ( name, age, occupation );");
    assertEquals(11, tokens.size());
    assertEquals("CT", tokens.get(0).getTokenType());
    assertEquals("CREATE", tokens.get(0).getWord());
    assertEquals("KW", tokens.get(1).getTokenType());
    assertEquals("TABLE", tokens.get(1).getWord());
    assertEquals("ID", tokens.get(2).getTokenType());
    assertEquals("faketable", tokens.get(2).getWord());
    assertEquals("LB", tokens.get(3).getTokenType());
    assertEquals("(", tokens.get(3).getWord());
    assertEquals("ID", tokens.get(4).getTokenType());
    assertEquals("name", tokens.get(4).getWord());
    assertEquals("SY", tokens.get(5).getTokenType());
    assertEquals(",", tokens.get(5).getWord());
    assertEquals("ID", tokens.get(6).getTokenType());
    assertEquals("age", tokens.get(6).getWord());
    assertEquals("SY", tokens.get(7).getTokenType());
    assertEquals(",", tokens.get(7).getWord());
    assertEquals("ID", tokens.get(8).getTokenType());
    assertEquals("occupation", tokens.get(8).getWord());
    assertEquals("RB", tokens.get(9).getTokenType());
    assertEquals(")", tokens.get(9).getWord());
    assertEquals("SY", tokens.get(10).getTokenType());
    assertEquals(";", tokens.get(10).getWord());

  }

  @Test
  void tokenizeWildAttrib() {
    Tokenizer t = new Tokenizer();
    ArrayList<Token> tokens = t.getTokens("SELECT * FROM faketable;");
    assertEquals(5, tokens.size());
    assertEquals("CT", tokens.get(0).getTokenType());
    assertEquals("SELECT", tokens.get(0).getWord());
    assertEquals("SY", tokens.get(1).getTokenType());
    assertEquals("*", tokens.get(1).getWord());
    assertEquals("KW", tokens.get(2).getTokenType());
    assertEquals("FROM", tokens.get(2).getWord());
    assertEquals("ID", tokens.get(3).getTokenType());
    assertEquals("faketable", tokens.get(3).getWord());
    assertEquals("SY", tokens.get(4).getTokenType());
    assertEquals(";", tokens.get(4).getWord());

  }

  @Test
  void tokenizeFloatLiteral() {
    Tokenizer t = new Tokenizer();
    ArrayList<Token> tokens = t.getTokens("SELECT name FROM faketable WHERE row1==20.33;");
    assertEquals(9, tokens.size());
    assertEquals("CT", tokens.get(0).getTokenType());
    assertEquals("SELECT", tokens.get(0).getWord());
    assertEquals("ID", tokens.get(1).getTokenType());
    assertEquals("name", tokens.get(1).getWord());
    assertEquals("KW", tokens.get(2).getTokenType());
    assertEquals("FROM", tokens.get(2).getWord());
    assertEquals("ID", tokens.get(3).getTokenType());
    assertEquals("faketable", tokens.get(3).getWord());
    assertEquals("KW", tokens.get(4).getTokenType());
    assertEquals("WHERE", tokens.get(4).getWord());
    assertEquals("ID", tokens.get(5).getTokenType());
    assertEquals("row1", tokens.get(5).getWord());
    assertEquals("OP", tokens.get(6).getTokenType());
    assertEquals("==", tokens.get(6).getWord());
    assertEquals("FL", tokens.get(7).getTokenType());
    assertEquals("20.33", tokens.get(7).getWord());
    assertEquals("SY", tokens.get(8).getTokenType());
    assertEquals(";", tokens.get(8).getWord());

  }

  @Test
  void tokenizeSignedFloatLiteral() {
    Tokenizer t = new Tokenizer();
    ArrayList<Token> tokens = t.getTokens("SELECT name FROM faketable WHERE row1==-20.33;");
    assertEquals(9, tokens.size());
    assertEquals("CT", tokens.get(0).getTokenType());
    assertEquals("SELECT", tokens.get(0).getWord());
    assertEquals("ID", tokens.get(1).getTokenType());
    assertEquals("name", tokens.get(1).getWord());
    assertEquals("KW", tokens.get(2).getTokenType());
    assertEquals("FROM", tokens.get(2).getWord());
    assertEquals("ID", tokens.get(3).getTokenType());
    assertEquals("faketable", tokens.get(3).getWord());
    assertEquals("KW", tokens.get(4).getTokenType());
    assertEquals("WHERE", tokens.get(4).getWord());
    assertEquals("ID", tokens.get(5).getTokenType());
    assertEquals("row1", tokens.get(5).getWord());
    assertEquals("OP", tokens.get(6).getTokenType());
    assertEquals("==", tokens.get(6).getWord());
    assertEquals("FL", tokens.get(7).getTokenType());
    assertEquals("-20.33", tokens.get(7).getWord());
    assertEquals("SY", tokens.get(8).getTokenType());
    assertEquals(";", tokens.get(8).getWord());

  }

  @Test
  void tokenizeNoSemicolon() {
    Tokenizer t = new Tokenizer();
    ArrayList<Token> tokens = t.getTokens("SELECT name FROM faketable WHERE row1==-20.33");
    assertEquals(8, tokens.size());
    assertEquals("CT", tokens.get(0).getTokenType());
    assertEquals("SELECT", tokens.get(0).getWord());
    assertEquals("ID", tokens.get(1).getTokenType());
    assertEquals("name", tokens.get(1).getWord());
    assertEquals("KW", tokens.get(2).getTokenType());
    assertEquals("FROM", tokens.get(2).getWord());
    assertEquals("ID", tokens.get(3).getTokenType());
    assertEquals("faketable", tokens.get(3).getWord());
    assertEquals("KW", tokens.get(4).getTokenType());
    assertEquals("WHERE", tokens.get(4).getWord());
    assertEquals("ID", tokens.get(5).getTokenType());
    assertEquals("row1", tokens.get(5).getWord());
    assertEquals("OP", tokens.get(6).getTokenType());
    assertEquals("==", tokens.get(6).getWord());
    assertEquals("FL", tokens.get(7).getTokenType());
    assertEquals("-20.33", tokens.get(7).getWord());

  }

  @Test
  void tokenizeIntegerLiteral() {
    Tokenizer t = new Tokenizer();
    ArrayList<Token> tokens = t.getTokens("SELECT name FROM faketable WHERE row1==20;");
    assertEquals(9, tokens.size());
    assertEquals("CT", tokens.get(0).getTokenType());
    assertEquals("SELECT", tokens.get(0).getWord());
    assertEquals("ID", tokens.get(1).getTokenType());
    assertEquals("name", tokens.get(1).getWord());
    assertEquals("KW", tokens.get(2).getTokenType());
    assertEquals("FROM", tokens.get(2).getWord());
    assertEquals("ID", tokens.get(3).getTokenType());
    assertEquals("faketable", tokens.get(3).getWord());
    assertEquals("KW", tokens.get(4).getTokenType());
    assertEquals("WHERE", tokens.get(4).getWord());
    assertEquals("ID", tokens.get(5).getTokenType());
    assertEquals("row1", tokens.get(5).getWord());
    assertEquals("OP", tokens.get(6).getTokenType());
    assertEquals("==", tokens.get(6).getWord());
    assertEquals("IL", tokens.get(7).getTokenType());
    assertEquals("20", tokens.get(7).getWord());
    assertEquals("SY", tokens.get(8).getTokenType());
    assertEquals(";", tokens.get(8).getWord());

  }

  @Test
  void tokenizeSignedIntegerLiteral() {
    Tokenizer t = new Tokenizer();
    ArrayList<Token> tokens = t.getTokens("SELECT name FROM faketable WHERE row1==+20;");
    assertEquals(9, tokens.size());
    assertEquals("CT", tokens.get(0).getTokenType());
    assertEquals("SELECT", tokens.get(0).getWord());
    assertEquals("ID", tokens.get(1).getTokenType());
    assertEquals("name", tokens.get(1).getWord());
    assertEquals("KW", tokens.get(2).getTokenType());
    assertEquals("FROM", tokens.get(2).getWord());
    assertEquals("ID", tokens.get(3).getTokenType());
    assertEquals("faketable", tokens.get(3).getWord());
    assertEquals("KW", tokens.get(4).getTokenType());
    assertEquals("WHERE", tokens.get(4).getWord());
    assertEquals("ID", tokens.get(5).getTokenType());
    assertEquals("row1", tokens.get(5).getWord());
    assertEquals("OP", tokens.get(6).getTokenType());
    assertEquals("==", tokens.get(6).getWord());
    assertEquals("IL", tokens.get(7).getTokenType());
    assertEquals("+20", tokens.get(7).getWord());
    assertEquals("SY", tokens.get(8).getTokenType());
    assertEquals(";", tokens.get(8).getWord());

  }

  @Test
  void tokenizeBooleanLiteral() {
    Tokenizer t = new Tokenizer();
    ArrayList<Token> tokens = t.getTokens("SELECT name FROM faketable WHERE row1==TRUE;");
    assertEquals(9, tokens.size());
    assertEquals("CT", tokens.get(0).getTokenType());
    assertEquals("SELECT", tokens.get(0).getWord());
    assertEquals("ID", tokens.get(1).getTokenType());
    assertEquals("name", tokens.get(1).getWord());
    assertEquals("KW", tokens.get(2).getTokenType());
    assertEquals("FROM", tokens.get(2).getWord());
    assertEquals("ID", tokens.get(3).getTokenType());
    assertEquals("faketable", tokens.get(3).getWord());
    assertEquals("KW", tokens.get(4).getTokenType());
    assertEquals("WHERE", tokens.get(4).getWord());
    assertEquals("ID", tokens.get(5).getTokenType());
    assertEquals("row1", tokens.get(5).getWord());
    assertEquals("OP", tokens.get(6).getTokenType());
    assertEquals("==", tokens.get(6).getWord());
    assertEquals("BL", tokens.get(7).getTokenType());
    assertEquals("TRUE", tokens.get(7).getWord());
    assertEquals("SY", tokens.get(8).getTokenType());
    assertEquals(";", tokens.get(8).getWord());

  }

  @Test
  void tokenizeNull() {
    Tokenizer t = new Tokenizer();
    ArrayList<Token> tokens = t.getTokens("SELECT name FROM faketable WHERE row1==NULL;");
    assertEquals(9, tokens.size());
    assertEquals("CT", tokens.get(0).getTokenType());
    assertEquals("SELECT", tokens.get(0).getWord());
    assertEquals("ID", tokens.get(1).getTokenType());
    assertEquals("name", tokens.get(1).getWord());
    assertEquals("KW", tokens.get(2).getTokenType());
    assertEquals("FROM", tokens.get(2).getWord());
    assertEquals("ID", tokens.get(3).getTokenType());
    assertEquals("faketable", tokens.get(3).getWord());
    assertEquals("KW", tokens.get(4).getTokenType());
    assertEquals("WHERE", tokens.get(4).getWord());
    assertEquals("ID", tokens.get(5).getTokenType());
    assertEquals("row1", tokens.get(5).getWord());
    assertEquals("OP", tokens.get(6).getTokenType());
    assertEquals("==", tokens.get(6).getWord());
    assertEquals("NL", tokens.get(7).getTokenType());
    assertEquals("NULL", tokens.get(7).getWord());
    assertEquals("SY", tokens.get(8).getTokenType());
    assertEquals(";", tokens.get(8).getWord());

  }

  @Test
  void tokenizeStringLiteral() {
    Tokenizer t = new Tokenizer();
    ArrayList<Token> tokens = t.getTokens("SELECT name FROM faketable WHERE row1=='harry';");
    assertEquals(9, tokens.size());
    assertEquals("CT", tokens.get(0).getTokenType());
    assertEquals("SELECT", tokens.get(0).getWord());
    assertEquals("ID", tokens.get(1).getTokenType());
    assertEquals("name", tokens.get(1).getWord());
    assertEquals("KW", tokens.get(2).getTokenType());
    assertEquals("FROM", tokens.get(2).getWord());
    assertEquals("ID", tokens.get(3).getTokenType());
    assertEquals("faketable", tokens.get(3).getWord());
    assertEquals("KW", tokens.get(4).getTokenType());
    assertEquals("WHERE", tokens.get(4).getWord());
    assertEquals("ID", tokens.get(5).getTokenType());
    assertEquals("row1", tokens.get(5).getWord());
    assertEquals("OP", tokens.get(6).getTokenType());
    assertEquals("==", tokens.get(6).getWord());
    assertEquals("SL", tokens.get(7).getTokenType());
    assertEquals("harry", tokens.get(7).getWord());
    assertEquals("SY", tokens.get(8).getTokenType());
    assertEquals(";", tokens.get(8).getWord());

  }

  @Test
  void tokenizeValueList(){
    Tokenizer t = new Tokenizer();
    ArrayList<Token> tokens = t.getTokens("INSERT INTO faketable VALUES( 'Harry', 20, TRUE, NULL, -123);");
    assertEquals(16, tokens.size());
    assertEquals("CT", tokens.get(0).getTokenType());
    assertEquals("INSERT", tokens.get(0).getWord());
    assertEquals("KW", tokens.get(1).getTokenType());
    assertEquals("INTO", tokens.get(1).getWord());
    assertEquals("ID", tokens.get(2).getTokenType());
    assertEquals("faketable", tokens.get(2).getWord());
    assertEquals("KW", tokens.get(3).getTokenType());
    assertEquals("VALUES", tokens.get(3).getWord());
    assertEquals("LB", tokens.get(4).getTokenType());
    assertEquals("(", tokens.get(4).getWord());
    assertEquals("SL", tokens.get(5).getTokenType());
    assertEquals("Harry", tokens.get(5).getWord());
    assertEquals("SY", tokens.get(6).getTokenType());
    assertEquals(",", tokens.get(6).getWord());
    assertEquals("IL", tokens.get(7).getTokenType());
    assertEquals("20", tokens.get(7).getWord());
    assertEquals("SY", tokens.get(8).getTokenType());
    assertEquals(",", tokens.get(8).getWord());
    assertEquals("BL", tokens.get(9).getTokenType());
    assertEquals("TRUE", tokens.get(9).getWord());
    assertEquals("SY", tokens.get(10).getTokenType());
    assertEquals(",", tokens.get(10).getWord());
    assertEquals("NL", tokens.get(11).getTokenType());
    assertEquals("NULL", tokens.get(11).getWord());
    assertEquals("SY", tokens.get(12).getTokenType());
    assertEquals(",", tokens.get(12).getWord());
    assertEquals("IL", tokens.get(13).getTokenType());
    assertEquals("-123", tokens.get(13).getWord());
    assertEquals("RB", tokens.get(14).getTokenType());
    assertEquals(")", tokens.get(14).getWord());
    assertEquals("SY", tokens.get(15).getTokenType());
    assertEquals(";", tokens.get(15).getWord());

  }

  // Parser tests

  @Test
  void parseSimpleUse() {
    Parser p = new Parser("USE fakedatabase ;");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());

  }

  @Test
  void parseMissingSemicolon() {
    Parser p = new Parser("USE fakedatabase ");
    assertNull(p.parse());
    assertEquals("Missing semi-colon", p.getStatusMessage());

  }

  @Test
  void parseCreateDatabase() {
    Parser p = new Parser("CREATE DATABASE fakedatabase ;");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());

  }

  @Test
  void parseCreateTable() {
    Parser p = new Parser("CREATE TABLE faketable;");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());
  }

  @Test
  void parseCreateAttrib() {
    Parser p = new Parser("CREATE TABLE faketable (name);");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());

  }

  @Test
  void parseCreateAttribList() {
    Parser p = new Parser("CREATE TABLE faketable (name, age, occupation);");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());

  }

  @Test
  void parseCreateBadAttrib() {
    Parser p = new Parser("CREATE TABLE faketable (name, age occupation);");
    assertNull(p.parse());
    assertEquals("Incorrect attribute syntax", p.getStatusMessage());

  }

  @Test
  void parseCreateMissingRB() {
    Parser p = new Parser("CREATE TABLE faketable (name, age, occupation;");
    assertNull(p.parse());
    assertEquals("Incorrect attribute syntax", p.getStatusMessage());

  }

  @Test
  void parseCreateNoSemicolon() {
    Parser p = new Parser("CREATE TABLE faketable (name, age, occupation)");
    assertNull(p.parse());
    assertEquals("Missing semi-colon", p.getStatusMessage());

  }

  @Test
  void parseDropDatabase() {
    Parser p = new Parser("DROP DATABASE fakedatabase ;");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());
//    System.out.println(p.parse());
//    System.out.println(p.getStatusMessage());
  }

  @Test
  void parseDropTable() {
    Parser p = new Parser("DROP TABLE faketable ;");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());

  }

  @Test
  void parseDropMissing() {
    Parser p = new Parser("DROP TABLE ;");
    assertNull(p.parse());
    assertEquals("Missing table name", p.getStatusMessage());

  }

  @Test
  void parseAlterAdd() {
    Parser p = new Parser("ALTER TABLE faketable ADD dob ;");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());

  }

  @Test
  void parseAlterDrop() {
    Parser p = new Parser("ALTER TABLE faketable DROP dob ;");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());

  }

  @Test
  void parseAlterMissingType() {
    Parser p = new Parser("ALTER TABLE faketable  ;");
    assertNull(p.parse());
    assertEquals("Missing alteration type", p.getStatusMessage());

  }

  @Test
  void parseInsertOneValue() {
    Parser p = new Parser("INSERT INTO faketable VALUES(20) ;");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());

  }

  @Test
  void parseInsertMultValues() {
    Parser p = new Parser("INSERT INTO faketable VALUES(20, 'harry', TRUE, NULL, -100.11) ;");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());

  }

  @Test
  void parseInsertMissingValue() {
    Parser p = new Parser("INSERT INTO faketable VALUES() ;");
    assertNull(p.parse());
    assertEquals("Missing value", p.getStatusMessage());

  }

  @Test
  void parseInsertNoRB() {
    Parser p = new Parser("INSERT INTO faketable VALUES(10 ;");
    assertNull(p.parse());
    assertEquals("No closing bracket", p.getStatusMessage());

  }

  @Test
  void parseInsertNoLB() {
    Parser p = new Parser("INSERT INTO faketable VALUES 10, FALSE ;");
    assertNull(p.parse());
    assertEquals("Missing opening bracket", p.getStatusMessage());

  }

  @Test
  void parseSimpleSelect() {
    Parser p = new Parser("SELECT name FROM faketable ;");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());
  }

  @Test
  void parseSelectWild() {
    Parser p = new Parser("SELECT * FROM faketable ;");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());
  }

  @Test
  void parseMultAttribs() {
    Parser p = new Parser("SELECT col1, col2, col3 FROM faketable ;");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());
  }

  @Test
  void parseSelectCondition() {
    Parser p = new Parser("SELECT name FROM faketable WHERE name=='harry';");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());
  }

  @Test
  void parseMultConditions() {
    Parser p = new Parser("SELECT name FROM faketable WHERE (name=='harry')AND(age>20);");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());
  }

  @Test
  void parseNestedConditions() {
    Parser p = new Parser("SELECT name FROM faketable WHERE ((name=='harry')AND(age>20)) OR ((name LIKE 'john')OR(employee==TRUE));");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());
  }

  @Test
  void parseSelectNoWhere() {
    Parser p = new Parser("SELECT name FROM faketable -123.56 ;");
    assertNull(p.parse());
    assertEquals("Missing WHERE keyword", p.getStatusMessage());
  }

  @Test
  void parseSimpleUpdate() {
    Parser p = new Parser("Update faketable SET col1=12.34 WHERE name != 'harry' ;");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());
  }

  @Test
  void parseNameValueList() {
    Parser p = new Parser("Update faketable SET col1=12.34, col2=false, col3=null WHERE name != 'harry' ;");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());
  }

  @Test
  void parseUpdateMultConditions() {
    Parser p = new Parser("Update faketable SET col1=12.34, col2=false, col3=null WHERE ((name=='harry')AND(age>20)) OR ((name LIKE 'john')OR(employee==TRUE)) ;");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());
  }

  @Test
  void parseUpdateNoWhere() {
    Parser p = new Parser("Update faketable SET col1=12.34, col2=false, col3=null ((name=='harry')AND(age>20)) OR ((name LIKE 'john')OR(employee==TRUE)) ;");
    assertNull(p.parse());
    assertEquals("Missing comma or WHERE keyword", p.getStatusMessage());
  }

  @Test
  void parseUpdateBadNVP() {
    Parser p = new Parser("Update faketable SET col1=12.34 col2=false col3=null WHERE ((name=='harry')AND(age>20)) OR ((name LIKE 'john')OR(employee==TRUE)) ;");
    assertNull(p.parse());
    assertEquals("Missing comma or WHERE keyword", p.getStatusMessage());
  }

  @Test
  void parseSimpleDelete(){
    Parser p = new Parser("DELETE FROM faketable WHERE age >= 20 ;");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());
  }

  @Test
  void parseDeleteMultConditions(){
    Parser p = new Parser("DELETE FROM faketable WHERE ((name=='harry')AND(age>20)) OR ((name LIKE 'john')OR(employee==TRUE));");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());
  }

  @Test
  void parseDeleteMultNestedConditions(){
    Parser p = new Parser("DELETE FROM faketable WHERE (((name=='harry')AND(age>20)) OR ((name LIKE 'john')OR(employee==TRUE))) AND ((occupation==NULL) OR (income < 30.56));");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());
  }

  @Test
  void parseDeleteNoTable() {
    Parser p = new Parser("DELETE FROM WHERE age >= 20 ;");
    assertNull(p.parse());
    // Parser treats "WHERE" as table name
    assertEquals("Missing WHERE keyword", p.getStatusMessage());
  }

  @Test
  void parseDeleteNoFrom() {
    Parser p = new Parser("DELETE faketable WHERE age >= 20 ;");
    assertNull(p.parse());
    assertEquals("Incorrect command syntax", p.getStatusMessage());
  }

  @Test
  void parseDeleteNoWhere() {
    Parser p = new Parser("DELETE FROM faketable age >= 20 ;");
    assertNull(p.parse());
    assertEquals("Missing WHERE keyword", p.getStatusMessage());
  }

  @Test
  void parseDeleteBadCond() {
    Parser p = new Parser("DELETE FROM faketable WHERE age 20 ;");
    assertNull(p.parse());
    assertEquals("Missing operator", p.getStatusMessage());
  }

  @Test
  void parseSimpleJoin(){
    Parser p = new Parser("JOIN faketable1 AND faketable2 ON col1 AND col2;");
    assertNotNull(p.parse());
    assertEquals("[OK]", p.getStatusMessage());
  }

  @Test
  void parseJoinTwoSemicolons(){
    Parser p = new Parser("JOIN faketable1 AND faketable2 ON col1 AND col2;;");
    assertNull(p.parse());
    assertEquals("No more commands after semi-colon", p.getStatusMessage());
  }

  @Test
  void parseJoinNoAnd(){
    Parser p = new Parser("JOIN faketable1 faketable2 ON col1 AND col2 ;");
    assertNull(p.parse());
    assertEquals("Missing AND", p.getStatusMessage());
  }

  @Test
  void parseJoinNoOn(){
    Parser p = new Parser("JOIN faketable1 AND faketable2 col1 AND col2 ;");
    assertNull(p.parse());
    assertEquals("Missing ON", p.getStatusMessage());
  }

  @Test
  void parseJoinNoTable2(){
    Parser p = new Parser("JOIN faketable1 AND ON col1 AND col2 ;");
    assertNull(p.parse());
    // Parser treats ON as table name
    assertEquals("Missing ON", p.getStatusMessage());
  }

  @Test
  void parseJoinMissingAttrib(){
    Parser p = new Parser("JOIN faketable1 AND faketable2 ON AND col2 ;");
    assertNull(p.parse());
    // Parser treats AND as attribute name
    assertEquals("Missing AND", p.getStatusMessage());
  }

  @Test
  void createDB(){
    Parser p = new Parser("CREATE DATABASE testDatabase;");
    DBcmd d = p.parse();
    assertEquals("testDatabase database created.", d.query(server));
    File dir = new File(server.getDirectory());
    Set<String> filesList = Set.of(dir.list());
    assertTrue(filesList.contains("testDatabase"));
  }

  @Test
  void useDB(){
    Parser p = new Parser("CREATE DATABASE testDatabase;");
    DBcmd d = p.parse();
    assertEquals("testDatabase database created.", d.query(server));
    Parser t = new Parser("USE testDatabase;");
    DBcmd use = t.parse();
    assertEquals("testDatabase selected.", use.query(server));
    assertEquals("testDatabase", server.getDB().getDatabaseName());
  }

  @Test
  void serverReadDBsFromDisk(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase2 database created.", server.handleCommand("CREATE DATABASE testDatabase2;"));
    File dir = new File(server.getDirectory());
    DBServer d = new DBServer(dir);
    assertTrue(d.getDatabaseList().contains("testDatabase"));
    assertTrue(d.getDatabaseList().contains("testDatabase2"));
  }

  @Test
  void useDBOutOfMultiple(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase2 database created.", server.handleCommand("CREATE DATABASE testDatabase2;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("testDatabase", server.getDB().getDatabaseName());
  }

  @Test
  void createTable(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable;"));
    File dir = new File(server.getDirectory()+"/testDatabase");
    Set<String> filesList = Set.of(dir.list());
    assertTrue(filesList.contains("testTable.tab"));
    assertTrue(server.getDB().getTableList().contains("testTable"));
  }

  @Test
  void createTablesDiffDBs(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase2 database created.", server.handleCommand("CREATE DATABASE testDatabase2;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable;"));
    assertEquals("[OK]\ntestDatabase2 selected.", server.handleCommand("USE testDatabase2;"));
    assertEquals("[OK]\ntestTable2 table created.", server.handleCommand("CREATE TABLE testTable2;"));
    File dir = new File(server.getDirectory()+"/testDatabase");
    Set<String> filesList = Set.of(dir.list());
    assertTrue(filesList.contains("testTable.tab"));
    assertTrue(server.getDB("testDatabase").getTableList().contains("testTable"));
    dir = new File(server.getDirectory()+"/testDatabase2");
    filesList = Set.of(dir.list());
    assertTrue(filesList.contains("testTable2.tab"));
    assertTrue(server.getDB().getTableList().contains("testTable2"));
    assertTrue(server.getDB("testDatabase2").getTableList().contains("testTable2"));
  }

  @Test
  void createMultipleTables(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable;"));
    assertEquals("[OK]\ntestTable2 table created.", server.handleCommand("CREATE TABLE testTable2;"));
    File dir = new File(server.getDirectory()+"/testDatabase");
    Set<String> filesList = Set.of(dir.list());
    assertTrue(filesList.contains("testTable.tab"));
    assertTrue(filesList.contains("testTable2.tab"));
    assertTrue(server.getDB().getTableList().contains("testTable"));
    assertTrue(server.getDB().getTableList().contains("testTable2"));
  }

  @Test
  void createTableAttributes(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    File dir = new File(server.getDirectory()+"/testDatabase");
    Set<String> filesList = Set.of(dir.list());
    assertTrue(filesList.contains("testTable.tab"));
    assertTrue(server.getDB().getTableList().contains("testTable"));
    assertTrue(server.getDB().getTable("testTable").getColumnHeadings().contains("id"));
    assertTrue(server.getDB().getTable("testTable").getColumnHeadings().contains("name"));
    assertTrue(server.getDB().getTable("testTable").getColumnHeadings().contains("age"));
    assertTrue(server.getDB().getTable("testTable").getColumnHeadings().contains("profession"));
  }

  @Test
  void serverReadTablesFromDisk(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable;"));
    assertEquals("[OK]\ntestTable2 table created.", server.handleCommand("CREATE TABLE testTable2;"));
    File dir = new File(server.getDirectory());
    DBServer d = new DBServer(dir);
    assertTrue(d.getDB("testDatabase").getTableList().contains("testTable"));
    assertTrue(d.getDB("testDatabase").getTableList().contains("testTable2"));
  }

  @Test
  void dropTable(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable;"));
    assertEquals("[OK]\ntestTable removed.", server.handleCommand("DROP TABLE testTable;"));
    File dir = new File(server.getDirectory()+"/testDatabase");
    Set<String> filesList = Set.of(dir.list());
    assertTrue(!filesList.contains("testTable.tab"));
    assertTrue(!server.getDB().getTableList().contains("testTable"));
  }

  @Test
  void dropDB(){
    assertEquals("[OK]\ntestDatabase3 database created.", server.handleCommand("CREATE DATABASE testDatabase3;"));
    assertEquals("[OK]\ntestDatabase3 removed.", server.handleCommand("DROP DATABASE testDatabase3;"));
    File dir = new File(server.getDirectory());
    Set<String> filesList = Set.of(dir.list());
    assertTrue(!filesList.contains("testDatabase3"));
    assertTrue(!server.getDatabaseList().contains("testDatabase3"));
  }

  @Test
  void alterTableAdd(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable;"));
    assertEquals("[OK]\nattribute added.", server.handleCommand("ALTER TABLE testTable ADD name;"));
    assertTrue(server.getDB().getTableList().contains("testTable"));
    assertTrue(server.getDB().getTable("testTable").getColumnHeadings().contains("id"));
    assertTrue(server.getDB().getTable("testTable").getColumnHeadings().contains("name"));
  }

  @Test
  void alterTableDrop(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nattribute dropped.", server.handleCommand("ALTER TABLE testTable DROP name;"));
    assertTrue(server.getDB().getTableList().contains("testTable"));
    assertTrue(server.getDB().getTable("testTable").getColumnHeadings().contains("id"));
    assertTrue(!server.getDB().getTable("testTable").getColumnHeadings().contains("name"));
    assertTrue(server.getDB().getTable("testTable").getColumnHeadings().contains("age"));
    assertTrue(server.getDB().getTable("testTable").getColumnHeadings().contains("profession"));
  }

  @Test
  void alterTableDropId(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[ERROR]: cannot alter id attribute", server.handleCommand("ALTER TABLE testTable DROP id;"));
    assertTrue(server.getDB().getTableList().contains("testTable"));
    assertTrue(server.getDB().getTable("testTable").getColumnHeadings().contains("id"));
  }

  @Test
  void alterNoTable(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[ERROR]: could not retrieve table", server.handleCommand("ALTER TABLE tTable DROP name;"));
    assertTrue(server.getDB().getTable("testTable").getColumnHeadings().contains("name"));
}

  @Test
  void insertValues(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertTrue(server.getDB().getTable("testTable").getRowById(1).toString().contains("Harry"));
    assertTrue(server.getDB().getTable("testTable").getRowById(1).toString().contains("20"));
    assertTrue(server.getDB().getTable("testTable").getRowById(1).toString().contains("NULL"));
  }

  @Test
  void insertNoTable(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[ERROR]: No such table.", server.handleCommand("INSERT INTO tTable VALUES ( 'Harry', 20, NULL);"));
  }

  @Test
  void insertTooFewValues(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[ERROR]: The number of values does not match number of columns.", server.handleCommand("INSERT INTO testTable VALUES ( 20, null);"));
  }

  @Test
  void insertTooManyValues(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[ERROR]: The number of values does not match number of columns.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', false, 20, null);"));
  }

  @Test
  void selectWild(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, true);"));
    assertTrue(server.handleCommand("SELECT * FROM testTable;").startsWith("[OK]\nid\tname\tage\tprofession"));
  }

  @Test
  void selectOneCol(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, true);"));
    assertTrue(server.handleCommand("SELECT name FROM testTable;").startsWith("[OK]\nname"));
  }

  @Test
  void selectMultipleCols(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, true);"));
    assertTrue(server.handleCommand("SELECT name, age, profession FROM testTable;").startsWith("[OK]\nname\tage\tprofession"));
  }

  @Test
  void selectWildCondition(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, true);"));
    assertTrue(server.handleCommand("SELECT * FROM testTable WHERE age >25;").startsWith("[OK]\nid\tname\tage\tprofession\t\n2\tJohn"));
  }

  @Test
  void selectWildMultConditions(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, true);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Jay', 35, false);"));
    assertTrue(server.handleCommand("SELECT * FROM testTable WHERE (age >25) AND (profession==FALSE);").startsWith("[OK]\nid\tname\tage\tprofession\t\n3\tJay"));
  }

  @Test
  void selectColsMultConditions(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, true);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Jay', 35, false);"));
    assertTrue(server.handleCommand("SELECT id, name FROM testTable WHERE (age >25) AND (profession==FALSE);").startsWith("[OK]\nid\tname\t\n3\tJay"));
  }

  @Test
  void updateOneValue(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, true);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Jay', 35, false);"));
    assertEquals("[OK]\nvalues updated.", server.handleCommand("UPDATE testTable SET age=25 WHERE name=='Harry';"));
    assertTrue(server.handleCommand("SELECT * FROM testTable;").startsWith("[OK]\nid\tname\tage\tprofession\t\n1\tHarry\t25"));
  }

  @Test
  void updateMultipleValues(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, true);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Jay', 35, false);"));
    assertEquals("[OK]\nvalues updated.", server.handleCommand("UPDATE testTable SET age=25, profession=true WHERE name=='Harry';"));
    assertTrue(server.handleCommand("SELECT * FROM testTable;").startsWith("[OK]\nid\tname\tage\tprofession\t\n1\tHarry\t25\tTRUE"));
  }

  @Test
  void updateMultipleConditions(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession, avg);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL, 12.45);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, false, -333.2);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Jay', 35, false, 130);"));
    assertEquals("[OK]\nvalues updated.", server.handleCommand("UPDATE testTable SET profession=true WHERE (profession==FALSE) AND (avg > 1);"));
    assertTrue(server.handleCommand("SELECT * FROM testTable WHERE name=='Jay';").startsWith("[OK]\nid\tname\tage\tprofession\tavg\t\n3\tJay\t35\tTRUE\t130"));
  }

  @Test
  void deleteOneCondition(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, true);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Jay', 35, false);"));
    assertEquals("[OK]\n", server.handleCommand("DELETE FROM testTable WHERE name=='Harry';"));
    assertTrue(server.handleCommand("SELECT * FROM testTable;").startsWith("[OK]\nid\tname\tage\tprofession\t\n2\tJohn"));
  }

  @Test
  void deleteMultipleConditions(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, true);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Jay', 35, false);"));
    assertEquals("[OK]\n", server.handleCommand("DELETE FROM testTable WHERE (name=='Harry') OR (age < 35);"));
    assertTrue(server.handleCommand("SELECT * FROM testTable;").startsWith("[OK]\nid\tname\tage\tprofession\t\n3\tJay"));
  }

  @Test
  void deleteInsertIDNumberIncrements(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, true);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Jay', 35, false);"));
    assertEquals("[OK]\n", server.handleCommand("DELETE FROM testTable WHERE (name=='Harry') OR (age < 35);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertTrue(server.handleCommand("SELECT * FROM testTable;").startsWith("[OK]\nid\tname\tage\tprofession\t\n3\tJay\t35\tFALSE\n4"));
  }

  @Test
  void deleteWrongTable(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, true);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Jay', 35, false);"));
    assertEquals("[ERROR]: No such table", server.handleCommand("DELETE FROM wrongTable WHERE (name=='Harry') OR (age < 35);"));
  }

  @Test
  void deleteNone(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, true);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Jay', 35, false);"));
    assertEquals("[OK]\n", server.handleCommand("DELETE FROM testTable WHERE (name=='Harry') AND (age > 35);"));
    assertTrue(server.handleCommand("SELECT * FROM testTable;").startsWith("[OK]\nid\tname\tage\tprofession\t\n1\tHarry"));
  }

  @Test
  void joinTables(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\npeople table created.", server.handleCommand("CREATE TABLE people (Name, Age, Email);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO people VALUES ( 'Bob', 21, 'bob@bob.net');"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO people VALUES ( 'Harry', 32, 'harry@harry.com');"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO people VALUES ( 'Chris', 42, 'chris@chris.ac.uk');"));
    assertEquals("[OK]\nsheds table created.", server.handleCommand("CREATE TABLE sheds (Name, Height, PurchaserID);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO sheds VALUES ( 'Dorchester', 1800, 3);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO sheds VALUES ( 'Plaza', 1200, 1);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO sheds VALUES ( 'Excelsior', 1000, 2);"));
    assertTrue(server.handleCommand("JOIN people AND sheds ON id AND PurchaserID;").startsWith("[OK]\nid\tName\tAge\tEmail\tName\tHeight\t\n1\tBob\t21\tbob@bob.net\tPlaza\t1200"));
  }

  // Other cases

  @Test
  void invalidOperatorsBooleans() {
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, true);"));
    assertEquals("[ERROR]: Could not evaluate expression", server.handleCommand("SELECT * FROM testTable WHERE profession > FALSE;"));
  }

  @Test
  void invalidOperatorsNull() {
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, true);"));
    assertEquals("[ERROR]: Could not evaluate expression", server.handleCommand("SELECT * FROM testTable WHERE profession < NULL;"));
  }

  @Test
  void invalidOperatorsNumbers() {
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, true);"));
    assertEquals("[ERROR]: Could not evaluate expression", server.handleCommand("SELECT * FROM testTable WHERE age LIKE 20;"));
  }

  @Test
  void plusSignedNumbers() {
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', +20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', +30, true);"));
    assertTrue(server.handleCommand("SELECT * FROM testTable;").startsWith("[OK]\nid\tname\tage\tprofession\t\n1\tHarry\t20"));
  }

  @Test
  void useKeywordsAsIdentifiers() {
    assertEquals("[OK]\ndatabase database created.", server.handleCommand("CREATE DATABASE database;"));
    assertEquals("[OK]\ndatabase selected.", server.handleCommand("USE database;"));
    assertEquals("[OK]\nselect table created.", server.handleCommand("CREATE TABLE select (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO select VALUES ( 'Harry', +20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO select VALUES ( 'John', +30, true);"));
    assertTrue(server.handleCommand("SELECT * FROM select;").startsWith("[OK]\nid\tname\tage\tprofession\t\n1\tHarry\t20"));
  }

  @Test
  void alterFilledTable(){
    assertEquals("[OK]\ntestDatabase database created.", server.handleCommand("CREATE DATABASE testDatabase;"));
    assertEquals("[OK]\ntestDatabase selected.", server.handleCommand("USE testDatabase;"));
    assertEquals("[OK]\ntestTable table created.", server.handleCommand("CREATE TABLE testTable (name, age, profession);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'Harry', 20, NULL);"));
    assertEquals("[OK]\nvalues inserted.", server.handleCommand("INSERT INTO testTable VALUES ( 'John', 30, true);"));
    assertEquals("[OK]\nattribute added.", server.handleCommand("ALTER TABLE testTable ADD dob;"));
    assertTrue(server.handleCommand("SELECT * FROM testTable;").startsWith("[OK]\nid\tname\tage\tprofession\tdob"));
  }

}
