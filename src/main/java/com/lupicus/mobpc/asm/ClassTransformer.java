package com.lupicus.mobpc.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.lupicus.mobpc.Main;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class ClassTransformer implements IClassTransformer
{
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (transformedName.equals("net.minecraft.entity.EntityLiving"))
			return entityLiving(name, transformedName, basicClass);
		return basicClass;
	}

	private byte[] entityLiving(String name, String transformedName, byte[] object)
	{
		ClassReader cr;
		try {
			cr = new ClassReader(object);
		}
		catch (Exception e)
		{
			return object;
		}
		ClassNode cnode = new ClassNode();
		cr.accept(cnode, 0);

		int count = 0;
		String owner = "net/minecraft/entity/EntityLiving";
		String fn = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, "func_175445_a", "(Lnet/minecraft/entity/item/EntityItem;)V"); // updateEquipmentIfNeeded
		for (MethodNode mobj : cnode.methods)
		{
			AbstractInsnNode node = null;
			if (mobj.name.equals(fn))
			{
				String f1 = FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(owner, "field_82179_bU", null); // persistenceRequired
				node = findFieldInstruction(mobj, Opcodes.PUTFIELD, owner, f1);
				if (node != null)
				{
					AbstractInsnNode node2 = node.getPrevious();
					AbstractInsnNode lb1 = node.getNext();
					if (node2 != null && node2.getOpcode() == Opcodes.ICONST_1 && lb1 != null && lb1.getType() == AbstractInsnNode.LABEL)
					{
						node2 = node2.getPrevious();
						InsnList list = new InsnList();
						list.add(new VarInsnNode(Opcodes.ALOAD, 2));
						list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/lupicus/mobpc/config/MyConfig", "check", "(Lnet/minecraft/item/ItemStack;)Z", false));
						list.add(new JumpInsnNode(Opcodes.IFNE, (LabelNode) lb1));
						mobj.instructions.insertBefore(node2, list);
						count++;
					}
					else
						Main.logger.error("Failed to modify EntityLiving: PUTFIELD is different");
				}
				else
					Main.logger.error("Failed to modify EntityLiving: PUTFIELD not found");
			}
		}
		if (count < 1)
			Main.logger.error("Failed to modify EntityLiving: Method not found");
		if (count > 0)
		{
			ClassWriter cw = new SafeClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			cnode.accept(cw);
			return cw.toByteArray();
		}
		return object;
	}

	private AbstractInsnNode findFieldInstruction(MethodNode obj, int opc, String owner, String name)
	{
		AbstractInsnNode node = obj.instructions.getFirst();
		while (node != null)
		{
			if (node.getOpcode() == opc)
			{
				FieldInsnNode f = (FieldInsnNode) node;
				if (f.name.equals(name) && f.owner.equals(owner))
					return node;
			}
			node = node.getNext();
		}
		return null;
	}

	/**
	 * Safe class writer.
	 * The way COMPUTE_FRAMES works may require loading additional classes. This can cause ClassCircularityErrors.
	 * The override for getCommonSuperClass will ensure that COMPUTE_FRAMES works properly by using the right ClassLoader.
	 * <p>
	 * Code from: https://github.com/JamiesWhiteShirt/clothesline/blob/master/src/core/java/com/jamieswhiteshirt/clothesline/core/SafeClassWriter.java
	 */
	public static class SafeClassWriter extends ClassWriter {
		public SafeClassWriter(int flags) {
			super(flags);
		}

		@Override
		protected String getCommonSuperClass(String type1, String type2) {
			Class<?> c, d;
			ClassLoader classLoader = Launch.classLoader;
			try {
				c = Class.forName(type1.replace('/', '.'), false, classLoader);
				d = Class.forName(type2.replace('/', '.'), false, classLoader);
			} catch (Exception e) {
				throw new RuntimeException(e.toString());
			}
			if (c.isAssignableFrom(d)) {
				return type1;
			}
			if (d.isAssignableFrom(c)) {
				return type2;
			}
			if (c.isInterface() || d.isInterface()) {
				return "java/lang/Object";
			} else {
				do {
					c = c.getSuperclass();
				} while (!c.isAssignableFrom(d));
				return c.getName().replace('.', '/');
			}
		}
	}
}
