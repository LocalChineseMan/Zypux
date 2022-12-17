package org.lwjgl.opengl;

class null implements AMDDebugOutputCallback.Handler {
  public void handleMessage(int id, int category, int severity, String message) {
    String description;
    System.err.println("[LWJGL] AMD_debug_output message");
    System.err.println("\tID: " + id);
    switch (category) {
      case 37193:
        description = "API ERROR";
        break;
      case 37194:
        description = "WINDOW SYSTEM";
        break;
      case 37195:
        description = "DEPRECATION";
        break;
      case 37196:
        description = "UNDEFINED BEHAVIOR";
        break;
      case 37197:
        description = "PERFORMANCE";
        break;
      case 37198:
        description = "SHADER COMPILER";
        break;
      case 37199:
        description = "APPLICATION";
        break;
      case 37200:
        description = "OTHER";
        break;
      default:
        description = printUnknownToken(category);
        break;
    } 
    System.err.println("\tCategory: " + description);
    switch (severity) {
      case 37190:
        description = "HIGH";
        break;
      case 37191:
        description = "MEDIUM";
        break;
      case 37192:
        description = "LOW";
        break;
      default:
        description = printUnknownToken(severity);
        break;
    } 
    System.err.println("\tSeverity: " + description);
    System.err.println("\tMessage: " + message);
  }
  
  private String printUnknownToken(int token) {
    return "Unknown (0x" + Integer.toHexString(token).toUpperCase() + ")";
  }
  
  public static interface AMDDebugOutputCallback {}
}
