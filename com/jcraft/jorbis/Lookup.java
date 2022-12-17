package com.jcraft.jorbis;

class Lookup {
  static final int COS_LOOKUP_SZ = 128;
  
  static final float[] COS_LOOKUP = new float[] { 
      1.0F, 0.9996988F, 0.99879545F, 0.99729043F, 0.9951847F, 0.99247956F, 0.9891765F, 0.98527765F, 0.98078525F, 0.9757021F, 
      0.97003126F, 0.96377605F, 0.95694035F, 0.94952816F, 0.94154406F, 0.9329928F, 0.9238795F, 0.9142098F, 0.9039893F, 0.8932243F, 
      0.8819213F, 0.87008697F, 0.8577286F, 0.8448536F, 0.8314696F, 0.8175848F, 0.8032075F, 0.7883464F, 0.77301043F, 0.7572088F, 
      0.7409511F, 0.7242471F, 0.70710677F, 0.68954057F, 0.671559F, 0.65317285F, 0.6343933F, 0.6152316F, 0.5956993F, 0.57580817F, 
      0.55557024F, 0.53499764F, 0.51410276F, 0.4928982F, 0.47139674F, 0.44961134F, 0.42755508F, 0.4052413F, 0.38268343F, 0.35989505F, 
      0.33688986F, 0.31368175F, 0.29028466F, 0.26671275F, 0.24298018F, 0.21910124F, 0.19509032F, 0.17096189F, 0.14673047F, 0.12241068F, 
      0.09801714F, 0.07356457F, 0.049067676F, 0.024541229F, 0.0F, -0.024541229F, -0.049067676F, -0.07356457F, -0.09801714F, -0.12241068F, 
      -0.14673047F, -0.17096189F, -0.19509032F, -0.21910124F, -0.24298018F, -0.26671275F, -0.29028466F, -0.31368175F, -0.33688986F, -0.35989505F, 
      -0.38268343F, -0.4052413F, -0.42755508F, -0.44961134F, -0.47139674F, -0.4928982F, -0.51410276F, -0.53499764F, -0.55557024F, -0.57580817F, 
      -0.5956993F, -0.6152316F, -0.6343933F, -0.65317285F, -0.671559F, -0.68954057F, -0.70710677F, -0.7242471F, -0.7409511F, -0.7572088F, 
      -0.77301043F, -0.7883464F, -0.8032075F, -0.8175848F, -0.8314696F, -0.8448536F, -0.8577286F, -0.87008697F, -0.8819213F, -0.8932243F, 
      -0.9039893F, -0.9142098F, -0.9238795F, -0.9329928F, -0.94154406F, -0.94952816F, -0.95694035F, -0.96377605F, -0.97003126F, -0.9757021F, 
      -0.98078525F, -0.98527765F, -0.9891765F, -0.99247956F, -0.9951847F, -0.99729043F, -0.99879545F, -0.9996988F, -1.0F };
  
  static final int INVSQ_LOOKUP_SZ = 32;
  
  static float coslook(float a) {
    double d = a * 40.74366592D;
    int i = (int)d;
    return COS_LOOKUP[i] + (float)(d - i) * (COS_LOOKUP[i + 1] - COS_LOOKUP[i]);
  }
  
  static final float[] INVSQ_LOOKUP = new float[] { 
      1.4142135F, 1.3926213F, 1.3719887F, 1.3522468F, 1.3333334F, 1.3151919F, 1.2977713F, 1.2810252F, 1.264911F, 1.2493901F, 
      1.2344269F, 1.2199886F, 1.2060454F, 1.1925696F, 1.1795356F, 1.16692F, 1.1547005F, 1.1428572F, 1.1313709F, 1.1202241F, 
      1.1094004F, 1.0988845F, 1.0886621F, 1.0787197F, 1.069045F, 1.0596259F, 1.0504515F, 1.0415113F, 1.0327955F, 1.0242951F, 
      1.016001F, 1.0079052F, 1.0F };
  
  static final int INVSQ2EXP_LOOKUP_MIN = -32;
  
  static final int INVSQ2EXP_LOOKUP_MAX = 32;
  
  static float invsqlook(float a) {
    double d = (a * 64.0F - 32.0F);
    int i = (int)d;
    return INVSQ_LOOKUP[i] + (float)(d - i) * (INVSQ_LOOKUP[i + 1] - INVSQ_LOOKUP[i]);
  }
  
  static final float[] INVSQ2EXP_LOOKUP = new float[] { 
      65536.0F, 46340.95F, 32768.0F, 23170.475F, 16384.0F, 11585.237F, 8192.0F, 5792.6187F, 4096.0F, 2896.3093F, 
      2048.0F, 1448.1547F, 1024.0F, 724.07733F, 512.0F, 362.03867F, 256.0F, 181.01933F, 128.0F, 90.50967F, 
      64.0F, 45.254833F, 32.0F, 22.627417F, 16.0F, 11.313708F, 8.0F, 5.656854F, 4.0F, 2.828427F, 
      2.0F, 1.4142135F, 1.0F, 0.70710677F, 0.5F, 0.35355338F, 0.25F, 0.17677669F, 0.125F, 0.088388346F, 
      0.0625F, 0.044194173F, 0.03125F, 0.022097087F, 0.015625F, 0.011048543F, 0.0078125F, 0.0055242716F, 0.00390625F, 0.0027621358F, 
      0.001953125F, 0.0013810679F, 9.765625E-4F, 6.9053395E-4F, 4.8828125E-4F, 3.4526698E-4F, 2.4414062E-4F, 1.7263349E-4F, 1.2207031E-4F, 8.6316744E-5F, 
      6.1035156E-5F, 4.3158372E-5F, 3.0517578E-5F, 2.1579186E-5F, 1.5258789E-5F };
  
  static final int FROMdB_LOOKUP_SZ = 35;
  
  static final int FROMdB2_LOOKUP_SZ = 32;
  
  static final int FROMdB_SHIFT = 5;
  
  static final int FROMdB2_SHIFT = 3;
  
  static final int FROMdB2_MASK = 31;
  
  static float invsq2explook(int a) {
    return INVSQ2EXP_LOOKUP[a - -32];
  }
  
  static final float[] FROMdB_LOOKUP = new float[] { 
      1.0F, 0.63095737F, 0.39810717F, 0.25118864F, 0.15848932F, 0.1F, 0.06309573F, 0.039810717F, 0.025118865F, 0.015848933F, 
      0.01F, 0.0063095735F, 0.0039810715F, 0.0025118864F, 0.0015848932F, 0.001F, 6.3095737E-4F, 3.9810716E-4F, 2.5118864E-4F, 1.5848932E-4F, 
      1.0E-4F, 6.309574E-5F, 3.981072E-5F, 2.5118865E-5F, 1.5848931E-5F, 1.0E-5F, 6.3095736E-6F, 3.9810716E-6F, 2.5118864E-6F, 1.5848932E-6F, 
      1.0E-6F, 6.3095735E-7F, 3.9810718E-7F, 2.5118865E-7F, 1.5848931E-7F };
  
  static final float[] FROMdB2_LOOKUP = new float[] { 
      0.9928303F, 0.9786446F, 0.9646616F, 0.95087844F, 0.9372922F, 0.92390007F, 0.9106993F, 0.89768714F, 0.8848609F, 0.8722179F, 
      0.8597556F, 0.8474713F, 0.83536255F, 0.8234268F, 0.8116616F, 0.8000645F, 0.7886331F, 0.777365F, 0.76625794F, 0.7553096F, 
      0.7445176F, 0.7338799F, 0.72339416F, 0.71305823F, 0.70287F, 0.6928273F, 0.68292814F, 0.6731704F, 0.66355205F, 0.65407115F, 
      0.64472574F, 0.63551384F };
  
  static float fromdBlook(float a) {
    int i = (int)(a * -8.0F);
    return (i < 0) ? 1.0F : ((i >= 1120) ? 0.0F : (FROMdB_LOOKUP[i >>> 5] * FROMdB2_LOOKUP[i & 0x1F]));
  }
}
