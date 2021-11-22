var asmapi = Java.type('net.minecraftforge.coremod.api.ASMAPI')
var opc = Java.type('org.objectweb.asm.Opcodes')
var AbstractInsnNode = Java.type('org.objectweb.asm.tree.AbstractInsnNode')
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode')
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode')
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode')
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode')
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode')
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode')

function initializeCoreMod() {
    return {
    	'MobEntity': {
    		'target': {
    			'type': 'CLASS',
    			'name': 'net.minecraft.world.entity.Mob'
    		},
    		'transformer': function(classNode) {
    			var count = 0
    			var fn = asmapi.mapMethod('m_21468_') // setItemSlotAndDropWhenKilled
    			for (var i = 0; i < classNode.methods.size(); ++i) {
    				var obj = classNode.methods.get(i)
    				if (obj.name == fn) {
    					patch_m_21468_(obj)
    					count++
    				}
    			}
    			if (count < 1)
    				asmapi.log("ERROR", "Failed to modify MobEntity: Method not found")
    			return classNode;
    		}
    	}
    }
}

// add the test: if (!(MyConfig.check(stack)))
function patch_m_21468_(obj) {
	var f1 = asmapi.mapField('f_21353_') // persistenceRequired
	var n1 = "net/minecraft/world/entity/Mob"
	var node = asmapi.findFirstInstruction(obj, opc.PUTFIELD)
	if (node && node.owner == n1 && node.name == f1) {
		var node2 = node.getPrevious()
		var lb1 = node.getNext()
		if (node2 && node2.getOpcode() == opc.ICONST_1 && lb1 && lb1.getType() == AbstractInsnNode.LABEL)
		{
			node2 = node2.getPrevious()
			var desc = "(Lnet/minecraft/world/item/ItemStack;)Z"
			var op1 = new VarInsnNode(opc.ALOAD, 2)
			var op2 = asmapi.buildMethodCall("com/lupicus/mobpc/config/MyConfig", "check", desc, asmapi.MethodType.STATIC)
			var op3 = new JumpInsnNode(opc.IFNE, lb1)
			var list = asmapi.listOf(op1, op2, op3)
			obj.instructions.insertBefore(node2, list)
		}
		else
			asmapi.log("ERROR", "Failed to modify MobEntity: PUTFIELD is different")
	}
	else
		asmapi.log("ERROR", "Failed to modify MobEntity: PUTFIELD not found")
}
