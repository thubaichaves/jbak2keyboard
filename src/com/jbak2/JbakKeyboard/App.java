package com.jbak2.JbakKeyboard;

import java.util.Locale;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

public class App extends Application {
	private SharedPreferences preferences;
	private Locale locale;
	private String lang;
	public static String DEF = "default";
	@Override
	public void onCreate() {
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		lang=getResources().getConfiguration().locale.getLanguage();//.getCountry();
		lang = preferences.getString(st.PREF_KEY_LANG_APP, DEF);	
		if (lang.equals(DEF)) {
			lang=getResources().getConfiguration().locale.getLanguage();//.getCountry();
		}
		if (lang.isEmpty())
			lang = st.getSystemLangApp();
		locale = new Locale(lang);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config, null);
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		lang = preferences.getString(st.PREF_KEY_LANG_APP, DEF);	
		if (lang.equals(DEF)) {
			lang=getResources().getConfiguration().locale.getLanguage();
		}
		if (lang.isEmpty())
			lang = st.getSystemLangApp();
        locale = new Locale(lang);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config, null);     
    }	
}