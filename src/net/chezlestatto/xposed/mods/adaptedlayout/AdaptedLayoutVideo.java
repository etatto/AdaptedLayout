package net.chezlestatto.xposed.mods.adaptedlayout;

import android.content.res.XModuleResources;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class AdaptedLayoutVideo implements IXposedHookZygoteInit, IXposedHookInitPackageResources {
	private static String MODULE_PATH = null;
	
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		MODULE_PATH = startupParam.modulePath;
	}

	@Override
	public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
		if (!resparam.packageName.equals("com.sonyericsson.video"))
			return;

		XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);
		resparam.res.setReplacement("com.sonyericsson.video", "dimen", "browser_fragments_padding_right", modRes.fwd(R.dimen.browser_fragments_padding_right));
		resparam.res.setReplacement("com.sonyericsson.video", "dimen", "browser_fragments_padding_bottom", modRes.fwd(R.dimen.browser_fragments_padding_bottom));

	}

}
