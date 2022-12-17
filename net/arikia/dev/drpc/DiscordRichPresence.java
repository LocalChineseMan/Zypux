package net.arikia.dev.drpc;

public class Builder {
  private DiscordRichPresence p;
  
  public Builder(String state) {
    this.p = new DiscordRichPresence();
    this.p.state = state;
  }
  
  public Builder setDetails(String details) {
    this.p.details = details;
    return this;
  }
  
  public Builder setStartTimestamps(long start) {
    this.p.startTimestamp = start;
    return this;
  }
  
  public Builder setEndTimestamp(long end) {
    this.p.endTimestamp = end;
    return this;
  }
  
  public Builder setBigImage(String key, String text) {
    if (text != null && !text.equalsIgnoreCase("") && key == null)
      throw new IllegalArgumentException("Image key must not be null when assigning a hover text."); 
    this.p.largeImageKey = key;
    this.p.largeImageText = text;
    return this;
  }
  
  public Builder setSmallImage(String key, String text) {
    if (text != null && !text.equalsIgnoreCase("") && key == null)
      throw new IllegalArgumentException("Image key must not be null when assigning a hover text."); 
    this.p.smallImageKey = key;
    this.p.smallImageText = text;
    return this;
  }
  
  public Builder setParty(String party, int size, int max) {
    this.p.partyId = party;
    this.p.partySize = size;
    this.p.partyMax = max;
    return this;
  }
  
  @Deprecated
  public Builder setSecrets(String match, String join, String spectate) {
    this.p.matchSecret = match;
    this.p.joinSecret = join;
    this.p.spectateSecret = spectate;
    return this;
  }
  
  public Builder setSecrets(String join, String spectate) {
    this.p.joinSecret = join;
    this.p.spectateSecret = spectate;
    return this;
  }
  
  @Deprecated
  public Builder setInstance(boolean i) {
    this.p.instance = i ? 1 : 0;
    return this;
  }
  
  public DiscordRichPresence build() {
    return this.p;
  }
  
  public static class DiscordRichPresence {}
}
