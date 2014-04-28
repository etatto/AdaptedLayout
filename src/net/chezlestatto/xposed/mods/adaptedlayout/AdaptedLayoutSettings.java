package net.chezlestatto.xposed.mods.adaptedlayout;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class AdaptedLayoutSettings extends Activity {

    public static final String PREF_KEY_ENABLE_ALBUM = "pref_enable_album";
    public static final String PREF_KEY_ENABLE_MUSIC = "pref_enable_music";
    public static final String PREF_KEY_ENABLE_VIDEO = "pref_enable_video";
    public static final String PREF_KEY_ENABLE_GB_ED_SUPPORT = "pref_enable_gb_ed_support";
    public static final String PREF_KEY_ENABLE_LOGGING = "pref_enable_logging";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTitle(R.string.app_name);
		super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        if (savedInstanceState == null)
			getFragmentManager().beginTransaction().replace(android.R.id.content,
	                new PrefsFragment()).commit();
	}

	public static class PrefsFragment extends PreferenceFragment {

		@SuppressWarnings("deprecation")
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// this is important because although the handler classes that read these settings
			// are in the same package, they are executed in the context of the hooked package
			getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
			addPreferencesFromResource(R.xml.prefs);
		}

	}

}