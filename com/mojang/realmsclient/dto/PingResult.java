package com.mojang.realmsclient.dto;

import java.util.ArrayList;
import java.util.List;

public class PingResult {
  public List<RegionPingResult> pingResults = new ArrayList<RegionPingResult>();
  
  public List<Long> worldIds = new ArrayList<Long>();
}
