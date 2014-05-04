package net.chezlestatto.xposed.mods.adaptedlayout;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class AdaptedLayoutMusic extends AdaptedLayout implements IXposedHookInitPackageResources, IXposedHookLoadPackage {

	public static final String MY_CLASS_NAME = AdaptedLayoutMusic.class.getSimpleName();

	// Supported app
	public static final String HK_APP_NAME = "WALKMAN";
	public static final String[] HK_APP_VERSION = { "8.3.A.0.2", "8.3.A.0.5" };
	public static final String HK_PACKAGE_NAME = "com.sonyericsson.music";

	// music view class, method & id
	public static final String HK_CLASS_NAME_1 = HK_PACKAGE_NAME + ".cv";
	public static final String HK_METHOD_NAME_11 = "a";
	public static final int CONTENT_ID = 0x7f0e00da;
	public static final int MINI_PLAYER_ID = 0x7f0e00db;
	
	// player view class & method
	public static final String HK_CLASS_NAME_2 = HK_PACKAGE_NAME + ".PlayerFragment";
	public static final String HK_METHOD_NAME_21 = "h";
	public static final int PLAYER_ALBUM_ART_VIEW_GROUP_ID = 0x7f0e005b;

	private int playerAlbumArtViewGroupOriginalHeight;
	private boolean adaptMusicLayout;
	
	@Override
	public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
		if (!resparam.packageName.equals(HK_PACKAGE_NAME))
			return;

		// frag_player layout inflate hook
	    resparam.res.hookLayout(HK_PACKAGE_NAME, "layout", "frag_player", new XC_LayoutInflated() {

	    	@Override
	        public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
            	log(HK_PACKAGE_NAME + ".layout.frag_player.inflate" + " hooked!");

            	// player_album_art_view_group view
            	try {
            		View playerAlbumArtViewGroup = liparam.view.findViewById(liparam.res.getIdentifier("player_album_art_view_group", "id", HK_PACKAGE_NAME));
       				ViewGroup.MarginLayoutParams playerAlbumArtViewGroupMLP = (ViewGroup.MarginLayoutParams) playerAlbumArtViewGroup.getLayoutParams();
    				playerAlbumArtViewGroupOriginalHeight = playerAlbumArtViewGroupMLP.height;
    				log("player_album_art_view_group.height: " + playerAlbumArtViewGroupOriginalHeight);
            	} catch (Exception e) {
           			log("player_album_art_view_group view not found!");
           		}
	        }

	    }); 

	}

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals(HK_PACKAGE_NAME))
			return;

		// music view hook
		XposedHelpers.findAndHookMethod(HK_CLASS_NAME_1, lpparam.classLoader, HK_METHOD_NAME_11, new XC_MethodHook() {

            @Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            	log(HK_CLASS_NAME_1 + "." + HK_METHOD_NAME_11 + " hooked! (before)");

            	adaptMusicLayout = isMusicLayoutToBeAdapted((Activity) XposedHelpers.getObjectField(param.thisObject, "a"));
        		log("Adapt Music layout: " + adaptMusicLayout);

            }
            
            @Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            	log(HK_CLASS_NAME_1 + "." + HK_METHOD_NAME_11 + " hooked! (after)");
            	
            	if (adaptMusicLayout) {

                	Activity activity = (Activity) XposedHelpers.getObjectField(param.thisObject, "a");

                	// content layout
       				View content =  activity.findViewById(CONTENT_ID);
       				ViewGroup.MarginLayoutParams contentMLP = (ViewGroup.MarginLayoutParams) content.getLayoutParams();
   					log("(b) content.rightMargin: " + contentMLP.rightMargin);
       				if (contentMLP.rightMargin != 0) {
       					contentMLP.rightMargin = 0;
           				content.setLayoutParams(contentMLP);
           				contentMLP = (ViewGroup.MarginLayoutParams) content.getLayoutParams();
       				}
   					log("(a) content.rightMargin: " + contentMLP.rightMargin);
        		
       				// mini_player layout
       				View miniPlayer =  activity.findViewById(MINI_PLAYER_ID);
       				ViewGroup.MarginLayoutParams miniPlayerMLP = (ViewGroup.MarginLayoutParams) miniPlayer.getLayoutParams();
					log("(b) mini_player.bottomMargin: " + miniPlayerMLP.bottomMargin);
					log("(b) mini_player.rightMargin: " + miniPlayerMLP.rightMargin);
       				if ((miniPlayerMLP.bottomMargin != 0) || (miniPlayerMLP.rightMargin != 0)) {
       					if (miniPlayerMLP.bottomMargin != 0) {
       						miniPlayerMLP.bottomMargin = 0;
       					}
       					if (miniPlayerMLP.rightMargin != 0) {
       						miniPlayerMLP.rightMargin = 0;
       					}
       					miniPlayer.setLayoutParams(miniPlayerMLP);
           				miniPlayerMLP = (ViewGroup.MarginLayoutParams) miniPlayer.getLayoutParams();
       				}
					log("(a) mini_player.bottomMargin: " + miniPlayerMLP.bottomMargin);
					log("(a) mini_player.rightMargin: " + miniPlayerMLP.rightMargin);

        		}

            }

		});
	
		// player view hook
		XposedHelpers.findAndHookMethod(HK_CLASS_NAME_2, lpparam.classLoader, HK_METHOD_NAME_21, new XC_MethodHook() {

            @Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            	log(HK_CLASS_NAME_2 + "." + HK_METHOD_NAME_21 + " hooked! (before)");

            	adaptMusicLayout = isMusicLayoutToBeAdapted((Activity) XposedHelpers.getObjectField(param.thisObject, "a"));
        		log("Adapt Music layout: " + adaptMusicLayout);

            }
            	
            @Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            	log(HK_CLASS_NAME_2 + "." + HK_METHOD_NAME_21 + " hooked! (after)");
            	
        		if (adaptMusicLayout) {

        			// view
        			View view = (View) XposedHelpers.callMethod(param.thisObject, "getView");
       				ViewGroup.MarginLayoutParams viewMLP = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
					log("(b) view.bottomMargin: " + viewMLP.bottomMargin);
					log("(b) view.rightMargin: " + viewMLP.rightMargin);
        			// player_album_art_view_group view
       				View playerAlbumArtViewGroup =  view.findViewById(PLAYER_ALBUM_ART_VIEW_GROUP_ID);
       				ViewGroup.MarginLayoutParams playerAlbumArtViewGroupMLP = (ViewGroup.MarginLayoutParams) playerAlbumArtViewGroup.getLayoutParams();
       				log("(b) player_album_art_view_group.height: " + playerAlbumArtViewGroupMLP.height);
       				if ((viewMLP.bottomMargin != 0) || (viewMLP.rightMargin != 0)) {
       					if (viewMLP.bottomMargin != 0) {

       	       				if (playerAlbumArtViewGroupOriginalHeight > 0) {
       	       					playerAlbumArtViewGroupMLP.height = playerAlbumArtViewGroupOriginalHeight + viewMLP.bottomMargin;
       	       					playerAlbumArtViewGroup.setLayoutParams(playerAlbumArtViewGroupMLP);
           	       				playerAlbumArtViewGroupMLP = (ViewGroup.MarginLayoutParams) playerAlbumArtViewGroup.getLayoutParams();
       	       				}
       	        			
       						viewMLP.bottomMargin = 0;
       					}
       					if (viewMLP.rightMargin != 0) {
       						viewMLP.rightMargin = 0;
       					}
       					view.setLayoutParams(viewMLP);
           				viewMLP = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
   						log("(a) view.bottomMargin: " + viewMLP.bottomMargin);
   						log("(a) view.rightMargin: " + viewMLP.rightMargin);
   	       				log("(a) player_album_art_view_group.height: " + playerAlbumArtViewGroupMLP.height);

       				}

        		}

            }

		});

	}

}