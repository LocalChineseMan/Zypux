package com.jhlabs.math;

import java.util.Random;

public class SCNoise implements Function1D, Function2D, Function3D {
  private static Random randomGenerator = new Random();
  
  public float evaluate(float x) {
    return evaluate(x, 0.1F);
  }
  
  public float evaluate(float x, float y) {
    float sum = 0.0F;
    if (impulseTab == null)
      impulseTab = impulseTabInit(665); 
    int ix = floor(x);
    float fx = x - ix;
    int iy = floor(y);
    float fy = y - iy;
    int m = 2;
    for (int i = -m; i <= m; i++) {
      for (int j = -m; j <= m; j++) {
        int h = this.perm[ix + i + this.perm[iy + j & 0xFF] & 0xFF];
        for (int n = 3; n > 0; n--, h = h + 1 & 0xFF) {
          int h4 = h * 4;
          float dx = fx - i + impulseTab[h4++];
          float dy = fy - j + impulseTab[h4++];
          float distsq = dx * dx + dy * dy;
          sum += catrom2(distsq) * impulseTab[h4];
        } 
      } 
    } 
    return sum / 3.0F;
  }
  
  public float evaluate(float x, float y, float z) {
    float sum = 0.0F;
    if (impulseTab == null)
      impulseTab = impulseTabInit(665); 
    int ix = floor(x);
    float fx = x - ix;
    int iy = floor(y);
    float fy = y - iy;
    int iz = floor(z);
    float fz = z - iz;
    int m = 2;
    for (int i = -m; i <= m; i++) {
      for (int j = -m; j <= m; j++) {
        for (int k = -m; k <= m; k++) {
          int h = this.perm[ix + i + this.perm[iy + j + this.perm[iz + k & 0xFF] & 0xFF] & 0xFF];
          for (int n = 3; n > 0; n--, h = h + 1 & 0xFF) {
            int h4 = h * 4;
            float dx = fx - i + impulseTab[h4++];
            float dy = fy - j + impulseTab[h4++];
            float dz = fz - k + impulseTab[h4++];
            float distsq = dx * dx + dy * dy + dz * dz;
            sum += catrom2(distsq) * impulseTab[h4];
          } 
        } 
      } 
    } 
    return sum / 3.0F;
  }
  
  public short[] perm = new short[] { 
      225, 155, 210, 108, 175, 199, 221, 144, 203, 116, 
      70, 213, 69, 158, 33, 252, 5, 82, 173, 133, 
      222, 139, 174, 27, 9, 71, 90, 246, 75, 130, 
      91, 191, 169, 138, 2, 151, 194, 235, 81, 7, 
      25, 113, 228, 159, 205, 253, 134, 142, 248, 65, 
      224, 217, 22, 121, 229, 63, 89, 103, 96, 104, 
      156, 17, 201, 129, 36, 8, 165, 110, 237, 117, 
      231, 56, 132, 211, 152, 20, 181, 111, 239, 218, 
      170, 163, 51, 172, 157, 47, 80, 212, 176, 250, 
      87, 49, 99, 242, 136, 189, 162, 115, 44, 43, 
      124, 94, 150, 16, 141, 247, 32, 10, 198, 223, 
      255, 72, 53, 131, 84, 57, 220, 197, 58, 50, 
      208, 11, 241, 28, 3, 192, 62, 202, 18, 215, 
      153, 24, 76, 41, 15, 179, 39, 46, 55, 6, 
      128, 167, 23, 188, 106, 34, 187, 140, 164, 73, 
      112, 182, 244, 195, 227, 13, 35, 77, 196, 185, 
      26, 200, 226, 119, 31, 123, 168, 125, 249, 68, 
      183, 230, 177, 135, 160, 180, 12, 1, 243, 148, 
      102, 166, 38, 238, 251, 37, 240, 126, 64, 74, 
      161, 40, 184, 149, 171, 178, 101, 66, 29, 59, 
      146, 61, 254, 107, 42, 86, 154, 4, 236, 232, 
      120, 21, 233, 209, 45, 98, 193, 114, 78, 19, 
      206, 14, 118, 127, 48, 79, 147, 85, 30, 207, 
      219, 54, 88, 234, 190, 122, 95, 67, 143, 109, 
      137, 214, 145, 93, 92, 100, 245, 0, 216, 186, 
      60, 83, 105, 97, 204, 52 };
  
  private static final int TABSIZE = 256;
  
  private static final int TABMASK = 255;
  
  private static final int NIMPULSES = 3;
  
  private static float[] impulseTab;
  
  private static final int SAMPRATE = 100;
  
  private static final int NENTRIES = 401;
  
  private static float[] table;
  
  public static int floor(float x) {
    int ix = (int)x;
    if (x < 0.0F && x != ix)
      return ix - 1; 
    return ix;
  }
  
  public float catrom2(float d) {
    if (d >= 4.0F)
      return 0.0F; 
    if (table == null) {
      table = new float[401];
      for (int j = 0; j < 401; j++) {
        float x = j / 100.0F;
        x = (float)Math.sqrt(x);
        if (x < 1.0F) {
          table[j] = 0.5F * (2.0F + x * x * (-5.0F + x * 3.0F));
        } else {
          table[j] = 0.5F * (4.0F + x * (-8.0F + x * (5.0F - x)));
        } 
      } 
    } 
    d = d * 100.0F + 0.5F;
    int i = floor(d);
    if (i >= 401)
      return 0.0F; 
    return table[i];
  }
  
  static float[] impulseTabInit(int seed) {
    float[] impulseTab = new float[1024];
    randomGenerator = new Random(seed);
    for (int i = 0; i < 256; i++) {
      impulseTab[i++] = randomGenerator.nextFloat();
      impulseTab[i++] = randomGenerator.nextFloat();
      impulseTab[i++] = randomGenerator.nextFloat();
      impulseTab[i++] = 1.0F - 2.0F * randomGenerator.nextFloat();
    } 
    return impulseTab;
  }
}
