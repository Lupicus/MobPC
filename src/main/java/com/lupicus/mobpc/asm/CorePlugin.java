package com.lupicus.mobpc.asm;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("ModPC Plugin")
@IFMLLoadingPlugin.TransformerExclusions("com.lupicus.mobpc.asm")
@IFMLLoadingPlugin.SortingIndex(1001) // After runtime deobfuscation
public class CorePlugin implements IFMLLoadingPlugin
{
	public static boolean runtimeDeobfEnabled = false;

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {"com.lupicus.mobpc.asm.ClassTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		runtimeDeobfEnabled = (Boolean) data.get("runtimeDeobfuscationEnabled");
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
