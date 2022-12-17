package com.mojang.authlib.yggdrasil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.UserMigratedException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse;
import com.mojang.authlib.yggdrasil.response.Response;
import com.mojang.util.UUIDTypeAdapter;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

public class YggdrasilAuthenticationService extends HttpAuthenticationService {
  private final String clientToken;
  
  private final Gson gson;
  
  public YggdrasilAuthenticationService(Proxy proxy, String clientToken) {
    super(proxy);
    this.clientToken = clientToken;
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(GameProfile.class, new GameProfileSerializer(null));
    builder.registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer());
    builder.registerTypeAdapter(UUID.class, new UUIDTypeAdapter());
    builder.registerTypeAdapter(ProfileSearchResultsResponse.class, new ProfileSearchResultsResponse.Serializer());
    this.gson = builder.create();
  }
  
  public UserAuthentication createUserAuthentication(Agent agent) {
    return (UserAuthentication)new YggdrasilUserAuthentication(this, agent);
  }
  
  public MinecraftSessionService createMinecraftSessionService() {
    return (MinecraftSessionService)new YggdrasilMinecraftSessionService(this);
  }
  
  public GameProfileRepository createProfileRepository() {
    return new YggdrasilGameProfileRepository(this);
  }
  
  protected <T extends Response> T makeRequest(URL url, Object input, Class<T> classOfT) throws AuthenticationException {
    try {
      String jsonResult = (input == null) ? performGetRequest(url) : performPostRequest(url, this.gson.toJson(input), "application/json");
      Response response = (Response)this.gson.fromJson(jsonResult, classOfT);
      if (response == null)
        return null; 
      if (StringUtils.isNotBlank(response.getError())) {
        if ("UserMigratedException".equals(response.getCause()))
          throw new UserMigratedException(response.getErrorMessage()); 
        if (response.getError().equals("ForbiddenOperationException"))
          throw new InvalidCredentialsException(response.getErrorMessage()); 
        throw new AuthenticationException(response.getErrorMessage());
      } 
      return (T)response;
    } catch (IOException e) {
      throw new AuthenticationUnavailableException("Cannot contact authentication server", e);
    } catch (IllegalStateException e) {
      throw new AuthenticationUnavailableException("Cannot contact authentication server", e);
    } catch (JsonParseException e) {
      throw new AuthenticationUnavailableException("Cannot contact authentication server", e);
    } 
  }
  
  public String getClientToken() {
    return this.clientToken;
  }
  
  private static class YggdrasilAuthenticationService {}
}
