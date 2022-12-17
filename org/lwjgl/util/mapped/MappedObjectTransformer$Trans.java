package org.lwjgl.util.mapped;

import org.lwjgl.LWJGLUtil;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

class TransformationAdapter extends ClassAdapter {
  final String className;
  
  boolean transformed;
  
  TransformationAdapter(ClassVisitor cv, String className) {
    super(cv);
    this.className = className;
  }
  
  public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
    MappedObjectTransformer.MappedSubtypeInfo mappedSubtype = MappedObjectTransformer.className_to_subtype.get(this.className);
    if (mappedSubtype != null && mappedSubtype.fields.containsKey(name)) {
      if (MappedObjectTransformer.PRINT_ACTIVITY)
        LWJGLUtil.log(MappedObjectTransformer.class.getSimpleName() + ": discarding field: " + this.className + "." + name + ":" + desc); 
      return null;
    } 
    if ((access & 0x8) == 0)
      return (FieldVisitor)new Object(this, access, name, desc, signature, value); 
    return super.visitField(access, name, desc, signature, value);
  }
  
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    if ("<init>".equals(name)) {
      MappedObjectTransformer.MappedSubtypeInfo mappedSubtype = MappedObjectTransformer.className_to_subtype.get(this.className);
      if (mappedSubtype != null) {
        if (!"()V".equals(desc))
          throw new ClassFormatError(this.className + " can only have a default constructor, found: " + desc); 
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitMethodInsn(183, MappedObjectTransformer.MAPPED_OBJECT_JVM, "<init>", "()V");
        methodVisitor.visitInsn(177);
        methodVisitor.visitMaxs(0, 0);
        name = "constructView$LWJGL";
      } 
    } 
    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
    return (MethodVisitor)new Object(this, access, name, desc, signature, exceptions, mv);
  }
}
