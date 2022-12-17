package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.realms.RealmsScreen;

public class RealmsWorldOptions {
  public Boolean pvp;
  
  public Boolean spawnAnimals;
  
  public Boolean spawnMonsters;
  
  public Boolean spawnNPCs;
  
  public Integer spawnProtection;
  
  public Boolean commandBlocks;
  
  public Boolean forceGameMode;
  
  public Integer difficulty;
  
  public Integer gameMode;
  
  public String slotName;
  
  public long templateId;
  
  public String templateImage;
  
  public boolean empty = false;
  
  private static boolean forceGameModeDefault = false;
  
  private static boolean pvpDefault = true;
  
  private static boolean spawnAnimalsDefault = true;
  
  private static boolean spawnMonstersDefault = true;
  
  private static boolean spawnNPCsDefault = true;
  
  private static int spawnProtectionDefault = 0;
  
  private static boolean commandBlocksDefault = false;
  
  private static int difficultyDefault = 2;
  
  private static int gameModeDefault = 0;
  
  private static String slotNameDefault = null;
  
  private static long templateIdDefault = -1L;
  
  private static String templateImageDefault = null;
  
  public RealmsWorldOptions(Boolean pvp, Boolean spawnAnimals, Boolean spawnMonsters, Boolean spawnNPCs, Integer spawnProtection, Boolean commandBlocks, Integer difficulty, Integer gameMode, Boolean forceGameMode, String slotName) {
    this.pvp = pvp;
    this.spawnAnimals = spawnAnimals;
    this.spawnMonsters = spawnMonsters;
    this.spawnNPCs = spawnNPCs;
    this.spawnProtection = spawnProtection;
    this.commandBlocks = commandBlocks;
    this.difficulty = difficulty;
    this.gameMode = gameMode;
    this.forceGameMode = forceGameMode;
    this.slotName = slotName;
  }
  
  public static RealmsWorldOptions getDefaults() {
    return new RealmsWorldOptions(Boolean.valueOf(pvpDefault), Boolean.valueOf(spawnAnimalsDefault), Boolean.valueOf(spawnMonstersDefault), Boolean.valueOf(spawnNPCsDefault), Integer.valueOf(spawnProtectionDefault), Boolean.valueOf(commandBlocksDefault), Integer.valueOf(difficultyDefault), Integer.valueOf(gameModeDefault), Boolean.valueOf(forceGameModeDefault), slotNameDefault);
  }
  
  public static RealmsWorldOptions getEmptyDefaults() {
    RealmsWorldOptions options = new RealmsWorldOptions(Boolean.valueOf(pvpDefault), Boolean.valueOf(spawnAnimalsDefault), Boolean.valueOf(spawnMonstersDefault), Boolean.valueOf(spawnNPCsDefault), Integer.valueOf(spawnProtectionDefault), Boolean.valueOf(commandBlocksDefault), Integer.valueOf(difficultyDefault), Integer.valueOf(gameModeDefault), Boolean.valueOf(forceGameModeDefault), slotNameDefault);
    options.setEmpty(true);
    return options;
  }
  
  public void setEmpty(boolean empty) {
    this.empty = empty;
  }
  
  public static RealmsWorldOptions parse(JsonObject jsonObject) {
    RealmsWorldOptions newOptions = new RealmsWorldOptions(Boolean.valueOf(JsonUtils.getBooleanOr("pvp", jsonObject, pvpDefault)), Boolean.valueOf(JsonUtils.getBooleanOr("spawnAnimals", jsonObject, spawnAnimalsDefault)), Boolean.valueOf(JsonUtils.getBooleanOr("spawnMonsters", jsonObject, spawnMonstersDefault)), Boolean.valueOf(JsonUtils.getBooleanOr("spawnNPCs", jsonObject, spawnNPCsDefault)), Integer.valueOf(JsonUtils.getIntOr("spawnProtection", jsonObject, spawnProtectionDefault)), Boolean.valueOf(JsonUtils.getBooleanOr("commandBlocks", jsonObject, commandBlocksDefault)), Integer.valueOf(JsonUtils.getIntOr("difficulty", jsonObject, difficultyDefault)), Integer.valueOf(JsonUtils.getIntOr("gameMode", jsonObject, gameModeDefault)), Boolean.valueOf(JsonUtils.getBooleanOr("forceGameMode", jsonObject, forceGameModeDefault)), JsonUtils.getStringOr("slotName", jsonObject, slotNameDefault));
    newOptions.templateId = JsonUtils.getLongOr("worldTemplateId", jsonObject, templateIdDefault);
    newOptions.templateImage = JsonUtils.getStringOr("worldTemplateImage", jsonObject, templateImageDefault);
    return newOptions;
  }
  
  public String getSlotName(int i) {
    if (this.slotName == null || this.slotName.equals("")) {
      if (this.empty)
        return RealmsScreen.getLocalizedString("mco.configure.world.slot.empty"); 
      return RealmsScreen.getLocalizedString("mco.configure.world.slot", new Object[] { Integer.valueOf(i) });
    } 
    return this.slotName;
  }
  
  public String getDefaultSlotName(int i) {
    return RealmsScreen.getLocalizedString("mco.configure.world.slot", new Object[] { Integer.valueOf(i) });
  }
  
  public String toJson() {
    JsonObject jsonObject = new JsonObject();
    if (this.pvp.booleanValue() != pvpDefault)
      jsonObject.addProperty("pvp", this.pvp); 
    if (this.spawnAnimals.booleanValue() != spawnAnimalsDefault)
      jsonObject.addProperty("spawnAnimals", this.spawnAnimals); 
    if (this.spawnMonsters.booleanValue() != spawnMonstersDefault)
      jsonObject.addProperty("spawnMonsters", this.spawnMonsters); 
    if (this.spawnNPCs.booleanValue() != spawnNPCsDefault)
      jsonObject.addProperty("spawnNPCs", this.spawnNPCs); 
    if (this.spawnProtection.intValue() != spawnProtectionDefault)
      jsonObject.addProperty("spawnProtection", this.spawnProtection); 
    if (this.commandBlocks.booleanValue() != commandBlocksDefault)
      jsonObject.addProperty("commandBlocks", this.commandBlocks); 
    if (this.difficulty.intValue() != difficultyDefault)
      jsonObject.addProperty("difficulty", this.difficulty); 
    if (this.gameMode.intValue() != gameModeDefault)
      jsonObject.addProperty("gameMode", this.gameMode); 
    if (this.forceGameMode.booleanValue() != forceGameModeDefault)
      jsonObject.addProperty("forceGameMode", this.forceGameMode); 
    if (!this.slotName.equals(slotNameDefault) && !this.slotName.equals(""))
      jsonObject.addProperty("slotName", this.slotName); 
    return jsonObject.toString();
  }
  
  public RealmsWorldOptions clone() {
    return new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.slotName);
  }
}
