package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import ir.lecer.uwu.Zypux;
import ir.lecer.uwu.interfaces.Account;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class Main {
  public static void main(String[] strings) {
    System.setProperty("java.net.preferIPv4Stack", "true");
    OptionParser optionparser = new OptionParser();
    optionparser.allowsUnrecognizedOptions();
    optionparser.accepts("demo");
    optionparser.accepts("fullscreen");
    optionparser.accepts("checkGlErrors");
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec1 = optionparser.accepts("server").withRequiredArg();
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec2 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(25565), (Object[])new Integer[0]);
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec3 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).required();
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec4 = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class).required();
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec5 = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec6 = optionparser.accepts("proxyHost").withRequiredArg();
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec7 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", (Object[])new String[0]).ofType(Integer.class);
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec8 = optionparser.accepts("proxyUser").withRequiredArg();
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec9 = optionparser.accepts("proxyPass").withRequiredArg();
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec10 = optionparser.accepts("username").withRequiredArg().defaultsTo("Lecer", (Object[])new String[0]);
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec11 = optionparser.accepts("uuid").withRequiredArg();
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec12 = optionparser.accepts("version").withRequiredArg().defaultsTo("1.8.9", (Object[])new String[0]);
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec13 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(854), (Object[])new Integer[0]);
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec14 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(480), (Object[])new Integer[0]);
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec15 = optionparser.accepts("userProperties").withRequiredArg().defaultsTo("{}", (Object[])new String[0]);
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec16 = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}", (Object[])new String[0]);
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec17 = optionparser.accepts("assetIndex").withRequiredArg();
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec18 = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy", (Object[])new String[0]);
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec19 = optionparser.accepts("launcherAccountName").withRequiredArg().ofType(String.class).required();
    ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec20 = optionparser.accepts("launcherAccountPassword").withRequiredArg().ofType(String.class).required();
    OptionSet optionset = optionparser.parse(strings);
    Zypux.account = new Account((String)argumentAcceptingOptionSpec19.value(optionset), (String)argumentAcceptingOptionSpec20.value(optionset));
    String s = (String)optionset.valueOf((OptionSpec)argumentAcceptingOptionSpec6);
    Proxy proxy = Proxy.NO_PROXY;
    if (s != null)
      proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(s, ((Integer)optionset.valueOf((OptionSpec)argumentAcceptingOptionSpec7)).intValue())); 
    final String s1 = (String)optionset.valueOf((OptionSpec)argumentAcceptingOptionSpec8);
    final String s2 = (String)optionset.valueOf((OptionSpec)argumentAcceptingOptionSpec9);
    if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(s1) && isNullOrEmpty(s2))
      Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
              return new PasswordAuthentication(s1, s2.toCharArray());
            }
          }); 
    int i = ((Integer)optionset.valueOf((OptionSpec)argumentAcceptingOptionSpec13)).intValue();
    int j = ((Integer)optionset.valueOf((OptionSpec)argumentAcceptingOptionSpec14)).intValue();
    boolean flag = optionset.has("fullscreen");
    boolean flag1 = optionset.has("checkGlErrors");
    boolean flag2 = optionset.has("demo");
    String s3 = (String)optionset.valueOf((OptionSpec)argumentAcceptingOptionSpec12);
    Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
    PropertyMap propertymap = (PropertyMap)gson.fromJson((String)optionset.valueOf((OptionSpec)argumentAcceptingOptionSpec15), PropertyMap.class);
    PropertyMap propertymap1 = (PropertyMap)gson.fromJson((String)optionset.valueOf((OptionSpec)argumentAcceptingOptionSpec16), PropertyMap.class);
    File file1 = (File)optionset.valueOf((OptionSpec)argumentAcceptingOptionSpec3);
    File file2 = optionset.has((OptionSpec)argumentAcceptingOptionSpec4) ? (File)optionset.valueOf((OptionSpec)argumentAcceptingOptionSpec4) : new File(file1, "assets/");
    File file3 = optionset.has((OptionSpec)argumentAcceptingOptionSpec5) ? (File)optionset.valueOf((OptionSpec)argumentAcceptingOptionSpec5) : new File(file1, "resourcepacks/");
    String s4 = optionset.has((OptionSpec)argumentAcceptingOptionSpec11) ? (String)argumentAcceptingOptionSpec11.value(optionset) : (String)argumentAcceptingOptionSpec10.value(optionset);
    String s5 = optionset.has((OptionSpec)argumentAcceptingOptionSpec17) ? (String)argumentAcceptingOptionSpec17.value(optionset) : null;
    String s6 = (String)optionset.valueOf((OptionSpec)argumentAcceptingOptionSpec1);
    Integer integer = (Integer)optionset.valueOf((OptionSpec)argumentAcceptingOptionSpec2);
    Session session = new Session((String)argumentAcceptingOptionSpec10.value(optionset), s4, String.valueOf(0), (String)argumentAcceptingOptionSpec18.value(optionset));
    GameConfiguration gameconfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, propertymap, propertymap1, proxy), new GameConfiguration.DisplayInformation(i, j, flag, flag1), new GameConfiguration.FolderInformation(file1, file3, file2, s5), new GameConfiguration.GameInformation(flag2, s3), new GameConfiguration.ServerInformation(s6, integer.intValue()));
    Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
          public void run() {
            Minecraft.stopIntegratedServer();
          }
        });
    Thread.currentThread().setName("Client thread");
    (new Minecraft(gameconfiguration)).run();
  }
  
  private static boolean isNullOrEmpty(String str) {
    return (str != null && !str.isEmpty());
  }
}
