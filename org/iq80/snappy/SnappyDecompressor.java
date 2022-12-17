package org.iq80.snappy;

final class SnappyDecompressor {
  private static final int MAX_INCREMENT_COPY_OVERFLOW = 20;
  
  public static int getUncompressedLength(byte[] compressed, int compressedOffset) throws CorruptionException {
    return readUncompressedLength(compressed, compressedOffset)[0];
  }
  
  public static byte[] uncompress(byte[] compressed, int compressedOffset, int compressedSize) throws CorruptionException {
    int[] varInt = readUncompressedLength(compressed, compressedOffset);
    int expectedLength = varInt[0];
    compressedOffset += varInt[1];
    compressedSize -= varInt[1];
    byte[] uncompressed = new byte[expectedLength];
    int uncompressedSize = decompressAllTags(compressed, compressedOffset, compressedSize, uncompressed, 0);
    if (expectedLength != uncompressedSize)
      throw new CorruptionException(String.format("Recorded length is %s bytes but actual length after decompression is %s bytes ", new Object[] { Integer.valueOf(expectedLength), 
              Integer.valueOf(uncompressedSize) })); 
    return uncompressed;
  }
  
  public static int uncompress(byte[] compressed, int compressedOffset, int compressedSize, byte[] uncompressed, int uncompressedOffset) throws CorruptionException {
    int[] varInt = readUncompressedLength(compressed, compressedOffset);
    int expectedLength = varInt[0];
    compressedOffset += varInt[1];
    compressedSize -= varInt[1];
    SnappyInternalUtils.checkArgument((expectedLength <= uncompressed.length - uncompressedOffset), "Uncompressed length %s must be less than %s", new Object[] { Integer.valueOf(expectedLength), Integer.valueOf(uncompressed.length - uncompressedOffset) });
    int uncompressedSize = decompressAllTags(compressed, compressedOffset, compressedSize, uncompressed, uncompressedOffset);
    if (expectedLength != uncompressedSize)
      throw new CorruptionException(String.format("Recorded length is %s bytes but actual length after decompression is %s bytes ", new Object[] { Integer.valueOf(expectedLength), 
              Integer.valueOf(uncompressedSize) })); 
    return expectedLength;
  }
  
  private static int decompressAllTags(byte[] input, int inputOffset, int inputSize, byte[] output, int outputOffset) throws CorruptionException {
    int outputLimit = output.length;
    int ipLimit = inputOffset + inputSize;
    int opIndex = outputOffset;
    int ipIndex = inputOffset;
    while (ipIndex < ipLimit - 5) {
      int opCode = SnappyInternalUtils.loadByte(input, ipIndex++);
      int entry = SnappyInternalUtils.lookupShort(opLookupTable, opCode);
      int trailerBytes = entry >>> 11;
      int trailer = readTrailer(input, ipIndex, trailerBytes);
      ipIndex += entry >>> 11;
      int length = entry & 0xFF;
      if ((opCode & 0x3) == 0) {
        int literalLength = length + trailer;
        copyLiteral(input, ipIndex, output, opIndex, literalLength);
        ipIndex += literalLength;
        opIndex += literalLength;
        continue;
      } 
      int copyOffset = entry & 0x700;
      copyOffset += trailer;
      int spaceLeft = outputLimit - opIndex;
      int srcIndex = opIndex - copyOffset;
      if (srcIndex < outputOffset)
        throw new CorruptionException("Invalid copy offset for opcode starting at " + (ipIndex - trailerBytes - 1)); 
      if (length <= 16 && copyOffset >= 8 && spaceLeft >= 16) {
        SnappyInternalUtils.copyLong(output, srcIndex, output, opIndex);
        SnappyInternalUtils.copyLong(output, srcIndex + 8, output, opIndex + 8);
      } else if (spaceLeft >= length + 20) {
        incrementalCopyFastPath(output, srcIndex, opIndex, length);
      } else {
        incrementalCopy(output, srcIndex, output, opIndex, length);
      } 
      opIndex += length;
    } 
    while (ipIndex < ipLimit) {
      int[] result = decompressTagSlow(input, ipIndex, output, outputLimit, outputOffset, opIndex);
      ipIndex = result[0];
      opIndex = result[1];
    } 
    return opIndex - outputOffset;
  }
  
  private static int[] decompressTagSlow(byte[] input, int ipIndex, byte[] output, int outputLimit, int outputOffset, int opIndex) throws CorruptionException {
    int opCode = SnappyInternalUtils.loadByte(input, ipIndex++);
    int entry = SnappyInternalUtils.lookupShort(opLookupTable, opCode);
    int trailerBytes = entry >>> 11;
    int trailer = 0;
    switch (trailerBytes) {
      case 4:
        trailer = (input[ipIndex + 3] & 0xFF) << 24;
      case 3:
        trailer |= (input[ipIndex + 2] & 0xFF) << 16;
      case 2:
        trailer |= (input[ipIndex + 1] & 0xFF) << 8;
      case 1:
        trailer |= input[ipIndex] & 0xFF;
        break;
    } 
    ipIndex += trailerBytes;
    int length = entry & 0xFF;
    if ((opCode & 0x3) == 0) {
      int literalLength = length + trailer;
      copyLiteral(input, ipIndex, output, opIndex, literalLength);
      ipIndex += literalLength;
      opIndex += literalLength;
    } else {
      int copyOffset = entry & 0x700;
      copyOffset += trailer;
      int spaceLeft = outputLimit - opIndex;
      int srcIndex = opIndex - copyOffset;
      if (srcIndex < outputOffset)
        throw new CorruptionException("Invalid copy offset for opcode starting at " + (ipIndex - trailerBytes - 1)); 
      if (length <= 16 && copyOffset >= 8 && spaceLeft >= 16) {
        SnappyInternalUtils.copyLong(output, srcIndex, output, opIndex);
        SnappyInternalUtils.copyLong(output, srcIndex + 8, output, opIndex + 8);
      } else if (spaceLeft >= length + 20) {
        incrementalCopyFastPath(output, srcIndex, opIndex, length);
      } else {
        incrementalCopy(output, srcIndex, output, opIndex, length);
      } 
      opIndex += length;
    } 
    return new int[] { ipIndex, opIndex };
  }
  
  private static int readTrailer(byte[] data, int index, int bytes) {
    return SnappyInternalUtils.loadInt(data, index) & wordmask[bytes];
  }
  
  private static void copyLiteral(byte[] input, int ipIndex, byte[] output, int opIndex, int length) throws CorruptionException {
    assert length > 0;
    assert ipIndex >= 0;
    assert opIndex >= 0;
    int spaceLeft = output.length - opIndex;
    int readableBytes = input.length - ipIndex;
    if (readableBytes < length || spaceLeft < length)
      throw new CorruptionException("Corrupt literal length"); 
    if (length <= 16 && spaceLeft >= 16 && readableBytes >= 16) {
      SnappyInternalUtils.copyLong(input, ipIndex, output, opIndex);
      SnappyInternalUtils.copyLong(input, ipIndex + 8, output, opIndex + 8);
    } else {
      int fastLength = length & 0xFFFFFFF8;
      if (fastLength <= 64) {
        for (int i = 0; i < fastLength; i += 8)
          SnappyInternalUtils.copyLong(input, ipIndex + i, output, opIndex + i); 
        int slowLength = length & 0x7;
        for (int j = 0; j < slowLength; j++)
          output[opIndex + fastLength + j] = input[ipIndex + fastLength + j]; 
      } else {
        SnappyInternalUtils.copyMemory(input, ipIndex, output, opIndex, length);
      } 
    } 
  }
  
  private static void incrementalCopy(byte[] src, int srcIndex, byte[] op, int opIndex, int length) {
    do {
      op[opIndex++] = src[srcIndex++];
    } while (--length > 0);
  }
  
  private static void incrementalCopyFastPath(byte[] output, int srcIndex, int opIndex, int length) {
    int copiedLength = 0;
    while (opIndex + copiedLength - srcIndex < 8) {
      SnappyInternalUtils.copyLong(output, srcIndex, output, opIndex + copiedLength);
      copiedLength += opIndex + copiedLength - srcIndex;
    } 
    for (int i = 0; i < length - copiedLength; i += 8)
      SnappyInternalUtils.copyLong(output, srcIndex + i, output, opIndex + copiedLength + i); 
  }
  
  private static final int[] wordmask = new int[] { 0, 255, 65535, 16777215, -1 };
  
  private static final short[] opLookupTable = new short[] { 
      1, 2052, 4097, 8193, 2, 2053, 4098, 8194, 3, 2054, 
      4099, 8195, 4, 2055, 4100, 8196, 5, 2056, 4101, 8197, 
      6, 2057, 4102, 8198, 7, 2058, 4103, 8199, 8, 2059, 
      4104, 8200, 9, 2308, 4105, 8201, 10, 2309, 4106, 8202, 
      11, 2310, 4107, 8203, 12, 2311, 4108, 8204, 13, 2312, 
      4109, 8205, 14, 2313, 4110, 8206, 15, 2314, 4111, 8207, 
      16, 2315, 4112, 8208, 17, 2564, 4113, 8209, 18, 2565, 
      4114, 8210, 19, 2566, 4115, 8211, 20, 2567, 4116, 8212, 
      21, 2568, 4117, 8213, 22, 2569, 4118, 8214, 23, 2570, 
      4119, 8215, 24, 2571, 4120, 8216, 25, 2820, 4121, 8217, 
      26, 2821, 4122, 8218, 27, 2822, 4123, 8219, 28, 2823, 
      4124, 8220, 29, 2824, 4125, 8221, 30, 2825, 4126, 8222, 
      31, 2826, 4127, 8223, 32, 2827, 4128, 8224, 33, 3076, 
      4129, 8225, 34, 3077, 4130, 8226, 35, 3078, 4131, 8227, 
      36, 3079, 4132, 8228, 37, 3080, 4133, 8229, 38, 3081, 
      4134, 8230, 39, 3082, 4135, 8231, 40, 3083, 4136, 8232, 
      41, 3332, 4137, 8233, 42, 3333, 4138, 8234, 43, 3334, 
      4139, 8235, 44, 3335, 4140, 8236, 45, 3336, 4141, 8237, 
      46, 3337, 4142, 8238, 47, 3338, 4143, 8239, 48, 3339, 
      4144, 8240, 49, 3588, 4145, 8241, 50, 3589, 4146, 8242, 
      51, 3590, 4147, 8243, 52, 3591, 4148, 8244, 53, 3592, 
      4149, 8245, 54, 3593, 4150, 8246, 55, 3594, 4151, 8247, 
      56, 3595, 4152, 8248, 57, 3844, 4153, 8249, 58, 3845, 
      4154, 8250, 59, 3846, 4155, 8251, 60, 3847, 4156, 8252, 
      2049, 3848, 4157, 8253, 4097, 3849, 4158, 8254, 6145, 3850, 
      4159, 8255, 8193, 3851, 4160, 8256 };
  
  private static int[] readUncompressedLength(byte[] compressed, int compressedOffset) throws CorruptionException {
    int bytesRead = 0;
    int b = compressed[compressedOffset + bytesRead++] & 0xFF;
    int result = b & 0x7F;
    if ((b & 0x80) != 0) {
      b = compressed[compressedOffset + bytesRead++] & 0xFF;
      result |= (b & 0x7F) << 7;
      if ((b & 0x80) != 0) {
        b = compressed[compressedOffset + bytesRead++] & 0xFF;
        result |= (b & 0x7F) << 14;
        if ((b & 0x80) != 0) {
          b = compressed[compressedOffset + bytesRead++] & 0xFF;
          result |= (b & 0x7F) << 21;
          if ((b & 0x80) != 0) {
            b = compressed[compressedOffset + bytesRead++] & 0xFF;
            result |= (b & 0x7F) << 28;
            if ((b & 0x80) != 0)
              throw new CorruptionException("last byte of compressed length int has high bit set"); 
          } 
        } 
      } 
    } 
    return new int[] { result, bytesRead };
  }
}
