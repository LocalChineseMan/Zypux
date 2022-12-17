package ir.lecer.uwu.features;

import ir.lecer.uwu.tools.tasks.TaskManager;
import ir.lecer.uwu.tools.utilities.TimerUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import net.minecraft.client.Minecraft;

public class Client {
  public void setSocket(Socket socket) {
    this.socket = socket;
  }
  
  public void setBufferedReader(BufferedReader bufferedReader) {
    this.bufferedReader = bufferedReader;
  }
  
  public void setBufferedWriter(BufferedWriter bufferedWriter) {
    this.bufferedWriter = bufferedWriter;
  }
  
  public void setMc(Minecraft mc) {
    this.mc = mc;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Client))
      return false; 
    Client other = (Client)o;
    if (!other.canEqual(this))
      return false; 
    Object this$C2SAccountLogin = getC2SAccountLogin(), other$C2SAccountLogin = other.getC2SAccountLogin();
    if ((this$C2SAccountLogin == null) ? (other$C2SAccountLogin != null) : !this$C2SAccountLogin.equals(other$C2SAccountLogin))
      return false; 
    Object this$S2CAccountLogin = getS2CAccountLogin(), other$S2CAccountLogin = other.getS2CAccountLogin();
    if ((this$S2CAccountLogin == null) ? (other$S2CAccountLogin != null) : !this$S2CAccountLogin.equals(other$S2CAccountLogin))
      return false; 
    Object this$socket = getSocket(), other$socket = other.getSocket();
    if ((this$socket == null) ? (other$socket != null) : !this$socket.equals(other$socket))
      return false; 
    Object this$bufferedReader = getBufferedReader(), other$bufferedReader = other.getBufferedReader();
    if ((this$bufferedReader == null) ? (other$bufferedReader != null) : !this$bufferedReader.equals(other$bufferedReader))
      return false; 
    Object this$bufferedWriter = getBufferedWriter(), other$bufferedWriter = other.getBufferedWriter();
    if ((this$bufferedWriter == null) ? (other$bufferedWriter != null) : !this$bufferedWriter.equals(other$bufferedWriter))
      return false; 
    Object this$mc = getMc(), other$mc = other.getMc();
    return !((this$mc == null) ? (other$mc != null) : !this$mc.equals(other$mc));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Client;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $C2SAccountLogin = getC2SAccountLogin();
    result = result * 59 + (($C2SAccountLogin == null) ? 43 : $C2SAccountLogin.hashCode());
    Object $S2CAccountLogin = getS2CAccountLogin();
    result = result * 59 + (($S2CAccountLogin == null) ? 43 : $S2CAccountLogin.hashCode());
    Object $socket = getSocket();
    result = result * 59 + (($socket == null) ? 43 : $socket.hashCode());
    Object $bufferedReader = getBufferedReader();
    result = result * 59 + (($bufferedReader == null) ? 43 : $bufferedReader.hashCode());
    Object $bufferedWriter = getBufferedWriter();
    result = result * 59 + (($bufferedWriter == null) ? 43 : $bufferedWriter.hashCode());
    Object $mc = getMc();
    return result * 59 + (($mc == null) ? 43 : $mc.hashCode());
  }
  
  public String toString() {
    return "Client(C2SAccountLogin=" + getC2SAccountLogin() + ", S2CAccountLogin=" + getS2CAccountLogin() + ", socket=" + getSocket() + ", bufferedReader=" + getBufferedReader() + ", bufferedWriter=" + getBufferedWriter() + ", mc=" + getMc() + ")";
  }
  
  public final String C2SAccountLogin = "C2SAccountLogin:";
  
  public String getC2SAccountLogin() {
    getClass();
    return "C2SAccountLogin:";
  }
  
  public final String S2CAccountLogin = "S2CAccountLogin:";
  
  public static Client client;
  
  private static TimerUtils timer;
  
  private Socket socket;
  
  private BufferedReader bufferedReader;
  
  private BufferedWriter bufferedWriter;
  
  protected Minecraft mc;
  
  public String getS2CAccountLogin() {
    getClass();
    return "S2CAccountLogin:";
  }
  
  public Socket getSocket() {
    return this.socket;
  }
  
  public BufferedReader getBufferedReader() {
    return this.bufferedReader;
  }
  
  public BufferedWriter getBufferedWriter() {
    return this.bufferedWriter;
  }
  
  public Minecraft getMc() {
    return this.mc;
  }
  
  public Client(Socket socket) {
    try {
      this.socket = socket;
      this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    } catch (IOException ex) {
      ex.printStackTrace();
      closeEverything(socket, this.bufferedReader, this.bufferedWriter);
      Minecraft.running = false;
    } 
  }
  
  public void sendPacket(String string) {
    try {
      if (this.socket.isConnected()) {
        this.bufferedWriter.write("C2SAccountLogin:" + string);
        this.bufferedWriter.newLine();
        this.bufferedWriter.flush();
      } 
    } catch (IOException ex) {
      ex.printStackTrace();
      closeEverything(this.socket, this.bufferedReader, this.bufferedWriter);
      Minecraft.running = false;
    } 
  }
  
  public void listenPacket() {
    while (this.socket.isConnected()) {
      try {
        String recievedPacket = this.bufferedReader.readLine();
        if (recievedPacket.startsWith("S2CAccountLogin:"))
          loginAccount(recievedPacket.replaceFirst("S2CAccountLogin:", "")); 
      } catch (IOException ex) {
        closeEverything(this.socket, this.bufferedReader, this.bufferedWriter);
        ex.printStackTrace();
        Minecraft.running = false;
      } 
    } 
  }
  
  private void loginAccount(String recievedPacket) {
    String[] account, packet = recievedPacket.split("\\|");
    switch (packet[0]) {
      case "failed":
        Minecraft.running = false;
        break;
      case "success":
        account = packet[1].split(",");
        if (!Boolean.parseBoolean(account[3]))
          Minecraft.running = false; 
        break;
    } 
  }
  
  public static void closeEverything(Socket socketC, BufferedReader bufferedReaderC, BufferedWriter bufferedWriterC) {
    try {
      if (bufferedReaderC != null)
        bufferedReaderC.close(); 
      if (bufferedWriterC != null)
        bufferedWriterC.close(); 
      if (socketC != null)
        socketC.close(); 
    } catch (Exception ex) {
      ex.printStackTrace();
      Minecraft.running = false;
    } 
  }
  
  public static void connect() {
    TaskManager.async(() -> {
          try {
            Socket socket = new Socket("5.253.246.168", 1234);
            client = new Client(socket);
            client.listenPacket();
          } catch (IOException ex) {
            ex.printStackTrace();
            Minecraft.running = false;
          } 
        });
  }
}
