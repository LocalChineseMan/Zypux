package com.mojang.realmsclient.dto;

import com.google.common.collect.ComparisonChain;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsServerPing;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServer extends ValueObject {
  private static final Logger LOGGER = LogManager.getLogger();
  
  public long id;
  
  public String remoteSubscriptionId;
  
  public String name;
  
  public String motd;
  
  public State state;
  
  public String owner;
  
  public String ownerUUID;
  
  public List<PlayerInfo> players;
  
  public Map<Integer, RealmsWorldOptions> slots;
  
  public String ip;
  
  public boolean expired;
  
  public boolean expiredTrial;
  
  public int daysLeft;
  
  public WorldType worldType;
  
  public int activeSlot;
  
  public String minigameName;
  
  public int minigameId;
  
  public String minigameImage;
  
  public String resourcePackUrl;
  
  public String resourcePackHash;
  
  public RealmsServerPing serverPing = new RealmsServerPing();
  
  public String getDescription() {
    return this.motd;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getMinigameName() {
    return this.minigameName;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setDescription(String motd) {
    this.motd = motd;
  }
  
  public void updateServerPing(RealmsServerPlayerList serverPlayerList) {
    StringBuilder builder = new StringBuilder();
    int players = 0;
    for (String uuid : serverPlayerList.players) {
      if (uuid.equals(Realms.getUUID()))
        continue; 
      String name = "";
      try {
        name = (String)RealmsUtil.nameCache.get(uuid);
      } catch (Exception exception) {
        LOGGER.error("Could not get name for " + uuid, exception);
        continue;
      } 
      if (builder.length() > 0)
        builder.append("\n"); 
      builder.append(name);
      players++;
    } 
    this.serverPing.nrOfPlayers = String.valueOf(players);
    this.serverPing.playerList = builder.toString();
  }
  
  public static RealmsServer parse(JsonObject node) {
    RealmsServer server = new RealmsServer();
    try {
      server.id = JsonUtils.getLongOr("id", node, -1L);
      server.remoteSubscriptionId = JsonUtils.getStringOr("remoteSubscriptionId", node, null);
      server.name = JsonUtils.getStringOr("name", node, null);
      server.motd = JsonUtils.getStringOr("motd", node, null);
      server.state = getState(JsonUtils.getStringOr("state", node, State.CLOSED.name()));
      server.owner = JsonUtils.getStringOr("owner", node, null);
      if (node.get("players") != null && node.get("players").isJsonArray()) {
        server.players = parseInvited(node.get("players").getAsJsonArray());
        sortInvited(server);
      } else {
        server.players = new ArrayList<PlayerInfo>();
      } 
      server.daysLeft = JsonUtils.getIntOr("daysLeft", node, 0);
      server.ip = JsonUtils.getStringOr("ip", node, null);
      server.expired = JsonUtils.getBooleanOr("expired", node, false);
      server.expiredTrial = JsonUtils.getBooleanOr("expiredTrial", node, false);
      server.worldType = getWorldType(JsonUtils.getStringOr("worldType", node, WorldType.NORMAL.name()));
      server.ownerUUID = JsonUtils.getStringOr("ownerUUID", node, "");
      if (node.get("slots") != null && node.get("slots").isJsonArray()) {
        server.slots = parseSlots(node.get("slots").getAsJsonArray());
      } else {
        server.slots = getEmptySlots();
      } 
      server.minigameName = JsonUtils.getStringOr("minigameName", node, null);
      server.activeSlot = JsonUtils.getIntOr("activeSlot", node, -1);
      server.minigameId = JsonUtils.getIntOr("minigameId", node, -1);
      server.minigameImage = JsonUtils.getStringOr("minigameImage", node, null);
      server.resourcePackUrl = JsonUtils.getStringOr("resourcePackUrl", node, null);
      server.resourcePackHash = JsonUtils.getStringOr("resourcePackHash", node, null);
    } catch (Exception e) {
      LOGGER.error("Could not parse McoServer: " + e.getMessage());
    } 
    return server;
  }
  
  private static void sortInvited(RealmsServer server) {
    Collections.sort(server.players, new Comparator<PlayerInfo>() {
          public int compare(PlayerInfo o1, PlayerInfo o2) {
            return ComparisonChain.start().compare(Boolean.valueOf(o2.getAccepted()), Boolean.valueOf(o1.getAccepted())).compare(o1.getName().toLowerCase(), o2.getName().toLowerCase()).result();
          }
        });
  }
  
  private static List<PlayerInfo> parseInvited(JsonArray jsonArray) {
    ArrayList<PlayerInfo> invited = new ArrayList<PlayerInfo>();
    for (JsonElement aJsonArray : jsonArray) {
      try {
        JsonObject node = aJsonArray.getAsJsonObject();
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setName(JsonUtils.getStringOr("name", node, null));
        playerInfo.setUuid(JsonUtils.getStringOr("uuid", node, null));
        playerInfo.setOperator(JsonUtils.getBooleanOr("operator", node, false));
        playerInfo.setAccepted(JsonUtils.getBooleanOr("accepted", node, false));
        invited.add(playerInfo);
      } catch (Exception exception) {}
    } 
    return invited;
  }
  
  private static Map<Integer, RealmsWorldOptions> parseSlots(JsonArray jsonArray) {
    Map<Integer, RealmsWorldOptions> slots = new HashMap<Integer, RealmsWorldOptions>();
    for (JsonElement aJsonArray : jsonArray) {
      try {
        RealmsWorldOptions options;
        JsonObject node = aJsonArray.getAsJsonObject();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(node.get("options").getAsString());
        if (element == null) {
          options = RealmsWorldOptions.getDefaults();
        } else {
          options = RealmsWorldOptions.parse(element.getAsJsonObject());
        } 
        int slot = JsonUtils.getIntOr("slotId", node, -1);
        slots.put(Integer.valueOf(slot), options);
      } catch (Exception exception) {}
    } 
    for (int i = 1; i <= 3; i++) {
      if (!slots.containsKey(Integer.valueOf(i)))
        slots.put(Integer.valueOf(i), RealmsWorldOptions.getEmptyDefaults()); 
    } 
    return slots;
  }
  
  private static Map<Integer, RealmsWorldOptions> getEmptySlots() {
    HashMap<Object, Object> slots = new HashMap<Object, Object>();
    slots.put(Integer.valueOf(1), RealmsWorldOptions.getEmptyDefaults());
    slots.put(Integer.valueOf(2), RealmsWorldOptions.getEmptyDefaults());
    slots.put(Integer.valueOf(3), RealmsWorldOptions.getEmptyDefaults());
    return (Map)slots;
  }
  
  public static RealmsServer parse(String json) {
    RealmsServer server = new RealmsServer();
    try {
      JsonParser parser = new JsonParser();
      JsonObject object = parser.parse(json).getAsJsonObject();
      server = parse(object);
    } catch (Exception e) {
      LOGGER.error("Could not parse McoServer: " + e.getMessage());
    } 
    return server;
  }
  
  private static State getState(String state) {
    try {
      return State.valueOf(state);
    } catch (Exception e) {
      return State.CLOSED;
    } 
  }
  
  private static WorldType getWorldType(String state) {
    try {
      return WorldType.valueOf(state);
    } catch (Exception e) {
      return WorldType.NORMAL;
    } 
  }
  
  public int hashCode() {
    return (new HashCodeBuilder(17, 37)).append(this.id).append(this.name).append(this.motd).append(this.state).append(this.owner).append(this.expired).toHashCode();
  }
  
  public boolean equals(Object obj) {
    if (obj == null)
      return false; 
    if (obj == this)
      return true; 
    if (obj.getClass() != getClass())
      return false; 
    RealmsServer rhs = (RealmsServer)obj;
    return (new EqualsBuilder()).append(this.id, rhs.id).append(this.name, rhs.name).append(this.motd, rhs.motd).append(this.state, rhs.state).append(this.owner, rhs.owner).append(this.expired, rhs.expired).append(this.worldType, this.worldType).isEquals();
  }
  
  public RealmsServer clone() {
    RealmsServer server = new RealmsServer();
    server.id = this.id;
    server.remoteSubscriptionId = this.remoteSubscriptionId;
    server.name = this.name;
    server.motd = this.motd;
    server.state = this.state;
    server.owner = this.owner;
    server.players = this.players;
    server.slots = cloneSlots(this.slots);
    server.ip = this.ip;
    server.expired = this.expired;
    server.expiredTrial = this.expiredTrial;
    server.daysLeft = this.daysLeft;
    server.serverPing = new RealmsServerPing();
    server.serverPing.nrOfPlayers = this.serverPing.nrOfPlayers;
    server.serverPing.playerList = this.serverPing.playerList;
    server.worldType = this.worldType;
    server.ownerUUID = this.ownerUUID;
    server.minigameName = this.minigameName;
    server.activeSlot = this.activeSlot;
    server.minigameId = this.minigameId;
    server.minigameImage = this.minigameImage;
    server.resourcePackUrl = this.resourcePackUrl;
    server.resourcePackHash = this.resourcePackHash;
    return server;
  }
  
  public Map<Integer, RealmsWorldOptions> cloneSlots(Map<Integer, RealmsWorldOptions> slots) {
    Map<Integer, RealmsWorldOptions> newSlots = new HashMap<Integer, RealmsWorldOptions>();
    for (Map.Entry<Integer, RealmsWorldOptions> entry : slots.entrySet())
      newSlots.put(entry.getKey(), ((RealmsWorldOptions)entry.getValue()).clone()); 
    return newSlots;
  }
  
  public static class McoServerComparator implements Comparator<RealmsServer> {
    private final String refOwner;
    
    public McoServerComparator(String owner) {
      this.refOwner = owner;
    }
    
    public int compare(RealmsServer server1, RealmsServer server2) {
      return ComparisonChain.start().compareTrueFirst(server1.state.equals(RealmsServer.State.UNINITIALIZED), server2.state.equals(RealmsServer.State.UNINITIALIZED)).compareTrueFirst(server1.expiredTrial, server2.expiredTrial).compareTrueFirst(server1.owner.equals(this.refOwner), server2.owner.equals(this.refOwner)).compareFalseFirst(server1.expired, server2.expired).compareTrueFirst(server1.state.equals(RealmsServer.State.OPEN), server2.state.equals(RealmsServer.State.OPEN)).compare(server1.id, server2.id).result();
    }
  }
  
  public enum State {
    CLOSED, OPEN, UNINITIALIZED;
  }
  
  public enum WorldType {
    NORMAL, MINIGAME, ADVENTUREMAP;
  }
}
