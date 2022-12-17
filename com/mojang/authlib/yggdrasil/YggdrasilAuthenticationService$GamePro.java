package com.mojang.authlib.yggdrasil;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;

class GameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {
  private GameProfileSerializer() {}
  
  public GameProfile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject object = (JsonObject)json;
    UUID id = object.has("id") ? (UUID)context.deserialize(object.get("id"), UUID.class) : null;
    String name = object.has("name") ? object.getAsJsonPrimitive("name").getAsString() : null;
    return new GameProfile(id, name);
  }
  
  public JsonElement serialize(GameProfile src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject result = new JsonObject();
    if (src.getId() != null)
      result.add("id", context.serialize(src.getId())); 
    if (src.getName() != null)
      result.addProperty("name", src.getName()); 
    return (JsonElement)result;
  }
}
