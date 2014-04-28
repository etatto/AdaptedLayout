package net.chezlestatto.xposed.mods.adaptedlayout;

import android.app.Activity;
import android.provider.Settings;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class AdaptedLayout implements IXposedHookZygoteInit {

	public static final String MY_PACKAGE_NAME = AdaptedLayoutMusic.class.getPackage().getName();

	private XSharedPreferences prefs;
	
	// GravityBox "Expanded Desktop" Support
    public static final String GB_PACKAGE_NAME = "com.ceco.gm2.gravitybox";
    public static final String GB_SETTING_EXPANDED_DESKTOP_STATE = "gravitybox_expanded_desktop_state";
    public static final String GB_PREF_KEY_EXPANDED_DESKTOP = "pref_expanded_desktop";
    public static final int GB_ED_DISABLED = 0;
    public static final int GB_ED_STATUSBAR = 1;
    public static final int GB_ED_NAVBAR = 2;
    public static final int GB_ED_BOTH = 3;

    private XSharedPreferences gbPrefs;
	
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		prefs = new XSharedPreferences(MY_PACKAGE_NAME);
		gbPrefs = new XSharedPreferences(GB_PACKAGE_NAME);
	}

	protected boolean isAlbumLayoutToBeAdapted(Activity activity) {
		if (!isAlbumEnabled()) {
			return false;
		} else if (!isGBInstalled(activity) || !isGBEDSupportEnabled()) {
			return true;
		} else {
			return isNavBarHiddenByGB(activity);
		}
	}

	protected boolean isMusicLayoutToBeAdapted(Activity activity) {
		if (!isMusicEnabled()) {
			return false;
		} else if (!isGBInstalled(activity) || !isGBEDSupportEnabled()) {
			return true;
		} else {
			return isNavBarHiddenByGB(activity);
		}
	}

	protected boolean isVideoLayoutToBeAdapted(Activity activity) {
		if (!isVideoEnabled()) {
			return false;
		} else if (!isGBInstalled(activity) || !isGBEDSupportEnabled()) {
			return true;
		} else {
			return isNavBarHiddenByGB(activity);
		}
	}

	protected void log(String message) {
        if (isLoggingEnabled()) {
                XposedBridge.log(MY_PACKAGE_NAME + ": " + message);
        }

	}

    private boolean isAlbumEnabled() {
    	prefs.reload();
    	return prefs.getBoolean(AdaptedLayoutSettings.PREF_KEY_ENABLE_ALBUM, false);
    }
    
    private boolean isMusicEnabled() {
    	prefs.reload();
    	return prefs.getBoolean(AdaptedLayoutSettings.PREF_KEY_ENABLE_MUSIC, false);
    }
    
    private boolean isVideoEnabled() {
    	prefs.reload();
    	return prefs.getBoolean(AdaptedLayoutSettings.PREF_KEY_ENABLE_VIDEO, false);
    }
    
    private boolean isLoggingEnabled() {
    	prefs.reload();
    	return prefs.getBoolean(AdaptedLayoutSettings.PREF_KEY_ENABLE_LOGGING, false);
    }
    
	private boolean isGBInstalled(Activity activity) {
		try {
			return !"".equals(activity.getPackageManager().getPackageInfo(GB_PACKAGE_NAME, 0).versionName);			
		} catch (Exception e) {
			return false;
		}
	}

    private boolean isGBEDSupportEnabled() {
    	prefs.reload();
    	return prefs.getBoolean(AdaptedLayoutSettings.PREF_KEY_ENABLE_GB_ED_SUPPORT, false);
    }
    
	private boolean isGBExpandedDesktopEnabled() {
		gbPrefs.reload();
        int gbEDMode = GB_ED_DISABLED;
        try {
        	gbEDMode = Integer.valueOf(gbPrefs.getString(GB_PREF_KEY_EXPANDED_DESKTOP, "-1"));
        } catch(NumberFormatException nfe) {
            log("Invalid value for GB_PREF_KEY_EXPANDED_DESKTOP preference");
        }
        return (gbEDMode != GB_ED_DISABLED) && (gbEDMode != GB_ED_STATUSBAR);
    }

    private boolean isGBExpandedDesktopOn(Activity activity) {
        return (Settings.System.getInt(activity.getContentResolver(),
        		GB_SETTING_EXPANDED_DESKTOP_STATE, 0) == 1);
    }

    private boolean isNavBarHiddenByGB(Activity activity) {
    	return !isGBExpandedDesktopEnabled() || isGBExpandedDesktopOn(activity);
    }

}
