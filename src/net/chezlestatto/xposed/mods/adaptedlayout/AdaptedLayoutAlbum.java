package net.chezlestatto.xposed.mods.adaptedlayout;

import android.app.Activity;
import android.os.Bundle;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class AdaptedLayoutAlbum extends AdaptedLayout implements IXposedHookLoadPackage {

	public static final String MY_CLASS_NAME = AdaptedLayoutAlbum.class.getSimpleName();

	// Supported app
	public static final String HK_APP_NAME = "Album";
	public static final String[] HK_APP_VERSION = { "6.0.A.0.26" };
	public static final String HK_PACKAGE_NAME = "com.sonyericsson.album";

	// MainActivity class & method
	public static final String HK_CLASS_NAME1 = HK_PACKAGE_NAME + ".MainActivity";
	public static final String HK_METHOD_NAME11 = "onCreate";
	
	// BarUtils class & methods
	public static final String HK_CLASS_NAME2 = HK_PACKAGE_NAME + ".util.BarUtils";
	public static final String HK_METHOD_NAME21 = "getNavigationbarHeightLandscape";
	public static final String HK_METHOD_NAME22 = "getNavigationbarHeightPortrait";

	private Activity activity;
	private boolean adaptAlbumLayout;
	
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals(HK_PACKAGE_NAME))
			return;

		// method11 hook
		XposedHelpers.findAndHookMethod(HK_CLASS_NAME1, lpparam.classLoader, HK_METHOD_NAME11, Bundle.class, new XC_MethodHook() {

            @Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            	log(HK_CLASS_NAME1 + "." + HK_METHOD_NAME11 + " hooked! (after)");

            	activity = (Activity) XposedHelpers.getObjectField(XposedHelpers.getObjectField(param.thisObject, "mActivityHelper"), "mActivity");

            }
            
		});
	
		// method21 hook 
		XposedHelpers.findAndHookMethod(HK_CLASS_NAME2, lpparam.classLoader, HK_METHOD_NAME21, new XC_MethodHook() {

            @Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            	log(HK_CLASS_NAME2 + "." + HK_METHOD_NAME21 + " hooked! (before)");

            	adaptAlbumLayout = isAlbumLayoutToBeAdapted(activity);
            	if (adaptAlbumLayout) {
                	param.setResult(0);
            	}

            }
            
		});
	
		// method22 hook 
		XposedHelpers.findAndHookMethod(HK_CLASS_NAME2, lpparam.classLoader, HK_METHOD_NAME22, new XC_MethodHook() {

            @Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            	log(HK_CLASS_NAME2 + "." + HK_METHOD_NAME22 + " hooked! (before)");

            	adaptAlbumLayout = isAlbumLayoutToBeAdapted(activity);
            	if (adaptAlbumLayout) {
                	param.setResult(0);
            	}

            }
            
		});
	
	}

}