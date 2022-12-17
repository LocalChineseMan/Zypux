package ir.lecer.uwu.interfaces;

public class Account {
  private final String name;
  
  private final String password;
  
  public Account(String name, String password) {
    this.name = name;
    this.password = password;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getPassword() {
    return this.password;
  }
}
