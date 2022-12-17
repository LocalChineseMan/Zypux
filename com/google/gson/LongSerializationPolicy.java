package com.google.gson;

enum null {
  public JsonElement serialize(Long value) {
    return new JsonPrimitive(String.valueOf(value));
  }
}
