package org.lwjgl.opencl;

import java.nio.ByteBuffer;
import org.lwjgl.PointerBuffer;

final class null extends InfoUtilAbstract<CLCommandQueue> {
  protected int getInfo(CLCommandQueue object, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
    return CL10.clGetCommandQueueInfo(object, param_name, param_value, null);
  }
}
