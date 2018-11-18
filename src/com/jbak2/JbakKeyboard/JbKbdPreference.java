package com.jbak2.JbakKeyboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jbak2.CustomGraphics.draw;
import com.jbak2.Dialog.Dlg;
import com.jbak2.Dialog.DlgFileExplorer;
import com.jbak2.JbakKeyboard.UpdVocabActivity;
import com.jbak2.ctrl.GlobDialog;
import com.jbak2.ctrl.IniFile;
import com.jbak2.ctrl.IntEditor;
import com.jbak2.ctrl.Mail;
import com.jbak2.perm.Perm;

@SuppressLint("NewApi")
public class JbKbdPreference extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
//	/**  время момента нажатия кнопки Да для оценки приложения */
//	long rateStart = 0;
//    long rateLen = 0;
//    String rateapp = null;

    File file_crash;
	private static final int MAX_STACK_STRING = 8192;
	private static final String CAUSED_BY = "caused by";
	public static final String SAVE_CRASH = "/save_crash.txt";
	// имя файла для сохранения строки дополнительных жестов
    String PREF_GESTURE_DOP_SYMB_FILENAME = "gesture_string.txt";
//    // ключ, для сохранения строки значений дополнительного жеста 
//    String gest_dop_symb_save = "save_gesture_dop_symb_str";
//    // ключ, для загрузки строки значений дополнительного жеста 
//    String gest_dop_symb_load = "load_gesture_dop_symb_str";
	boolean intent_share = false;
	boolean fl_temp_del_spase = false;
	public static final String DEF_SIZE_CLIPBRD = "20";
    public static final String DEF_SHORT_VIBRO = "30";
    public static final String DEF_LONG_VIBRO = "15";
    public static JbKbdPreference inst;
/** Массив списков с целыми значениями */    
    IntEntry arIntEntries[];
// *****************************************    
// параметры par.ini (см. onCreate)
 // *****************************************
    
// текущая версия
    String vers = null;
// дата и время из par.ini
    long timeini = 0;
// текущее время
    long cur_time = 0;
// оценивалось приложение или нет
    int rate_app=0;
// первая установленная версия
    String rate_start_version=st.STR_ZERO;
//    String rate_start_time="1";
// выводить ли историю версий
    boolean new_vers = false;
    IniFile ini =null;
// путь и имяфайла 
    public static String path =st.STR_NULL;
	private Thread.UncaughtExceptionHandler androidDefaultUEH;
    
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
		st.fl_pref_act = true;
        inst = this;
        if (!Perm.checkPermission(inst)) {
   			finish();
   			st.runAct(Quick_setting_act.class,inst);
        }
        checkCrash();
		androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				saveCrash(e);
				androidDefaultUEH.uncaughtException(thread, e);
			}
		});
		// просто вызываем ошибку, чтобы сработал фидбек
        //int bbb = Integer.valueOf("huk");
// для инфы мне		
//		String sss = "brand:"+Build.DISPLAY.BRAND
//		+"\nCODENAME: " + Build.VERSION.CODENAME
//		+"\nINCREMENTAL: " + Build.VERSION.INCREMENTAL
//		+"\nRELEASE: " + Build.VERSION.RELEASE
//		+"\nSDK_INT: " + Build.VERSION.SDK_INT;
//		st.help(sss);

	// проверяем был ли послан текст для записи в буфер	
		checkStartIntent();
		cur_time = new Date().getTime();
		ini = null;
		preOper();
//		rateStart = 0;
		
// основной конструктор
// вывод установленных значений
//        inst = this;
        st.getGestureAll();
//		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        arIntEntries = new IntEntry[]{
                new IntEntry(st.PREF_KEY_USE_SHORT_VIBRO, R.string.set_key_short_vibro_desc, R.array.vibro_short_type,st.STR_ONE),
                new IntEntry(st.PREF_KEY_AC_PLACE, R.string.set_key_ac_place_desc, R.array.ac_place,st.STR_ZERO),
                new IntEntry(st.PREF_KEY_PORTRAIT_TYPE, R.string.set_key_portrait_input_type_desc, R.array.array_input_type,st.STR_ZERO),
                new IntEntry(st.PREF_KEY_LANSCAPE_TYPE, R.string.set_key_landscape_input_type_desc, R.array.array_input_type,st.STR_ZERO),
                new IntEntry(st.PREF_KEY_PREVIEW_TYPE, R.string.set_ch_keys_preview_desc, R.array.pv_place,st.STR_ONE),
                new IntEntry(st.PREF_KEY_USE_VOLUME_KEYS, R.string.set_key_use_volumeKeys_desc, R.array.vk_use,st.STR_ZERO),
                new IntEntry(st.PREF_KEY_SOUND_VOLUME, R.string.set_key_sounds_volume_desc, R.array.integer_vals,"5"),
                new IntEntry(st.PREF_KEY_PREVIEW_WINSIZE, R.string.preview_size_win_desc, R.array.popupwndsize,"2"),
                new IntEntry(st.PREF_KEY_MINI_KBD_BTN_SIZE, R.string.pref_mini_kbd_btnsize_desc, R.array.array_minikbd_btn_size,st.STR_ZERO),
                new IntEntry(st.PREF_KEY_MINI_KBD_BTN_TEXT_SIZE, R.string.pref_mini_kbd_textbtnsize_desc, R.array.array_minikbd_btn_text_size,st.STR_ZERO),
            };
        st.upgradeSettings(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pref_view);
        addPreferencesFromResource(R.xml.preferences);
        setShiftState();
//		p = PreferenceManager.getDefaultSharedPreferences(this);

//        p = st.pref(this);
        SharedPreferences p = st.pref(st.c());
        PreferenceScreen ps =getPreferenceScreen(); 
        Preference pr = ps.findPreference(st.PREF_KEY_SAVE);
        pr.setSummary(pr.getSummary().toString()+'\n'+getBackupPath());
        pr = getPreferenceScreen().findPreference(st.PREF_KEY_LOAD);
        pr.setSummary(pr.getSummary().toString()+'\n'+getBackupPath());
        setSummary(st.KBD_BACK_ALPHA, R.string.set_kbd_background_alpha_desc, strVal(p.getString(st.KBD_BACK_ALPHA,st.STR_NULL+st.KBD_BACK_ALPHA_DEF)));
        setSummary(st.KBD_BACK_PICTURE, R.string.set_kbd_background_desc, strVal(p.getString(st.KBD_BACK_PICTURE,st.STR_NULL)));
        setSummary(st.PREF_KEY_CLIPBRD_SIZE, R.string.set_key_clipbrd_size_desc, strVal(p.getString(st.PREF_KEY_CLIPBRD_SIZE,DEF_SIZE_CLIPBRD )));
        setSummary(st.SET_STR_GESTURE_DOPSYMB, R.string.gesture_popupchar_str1_desc, strVal(p.getString(st.SET_STR_GESTURE_DOPSYMB,st.STR_NULL )));
        setSummary(st.SET_GESTURE_LENGTH, R.string.set_key_gesture_length_desc, strVal(p.getString(st.SET_GESTURE_LENGTH,"100" )));
        setSummary(st.SET_GESTURE_VELOCITY, R.string.set_key_gesture_vel_desc, strVal(p.getString(st.SET_GESTURE_VELOCITY,"150" )));
        setSummary(st.MM_BTN_OFF_SIZE, R.string.mm_btnoff_size_desc, strVal(p.getString(st.MM_BTN_OFF_SIZE,"8" )));
        setSummary(st.AC_LIST_VALUE, R.string.ac_list_value_desc, strVal(p.getString(st.AC_LIST_VALUE,"40" )));
        setSummary(st.PREF_AC_DEFKEY, R.string.set_ac_defkey_desc, strVal(p.getString(st.PREF_AC_DEFKEY,st.AC_DEF_WORD )));
        setSummary(st.PREF_AC_HEIGHT, R.string.set_key_ac_height_desc, strVal(p.getString(st.PREF_AC_HEIGHT,st.STR_ZERO )));
        CharSequence entries[] = st.getGestureEntries(this);
        CharSequence entValues[] = st.getGestureEntryValues(); 
        setGestureList(p, st.PREF_KEY_GESTURE_LEFT, entries, entValues);
        setGestureList(p, st.PREF_KEY_GESTURE_RIGHT, entries, entValues);
        setGestureList(p, st.PREF_KEY_GESTURE_UP, entries, entValues);
        setGestureList(p, st.PREF_KEY_GESTURE_DOWN, entries, entValues);
        setGestureList(p, st.PREF_KEY_GESTURE_SPACE_LEFT, entries, entValues);
        setGestureList(p, st.PREF_KEY_GESTURE_SPACE_RIGHT, entries, entValues);
        setGestureList(p, st.PREF_KEY_GESTURE_SPACE_UP, entries, entValues);
        setGestureList(p, st.PREF_KEY_GESTURE_SPACE_DOWN, entries, entValues);
        int index = 0;
        for(IntEntry ie:arIntEntries)
        {
             index = Integer.decode(p.getString(ie.key, ie.defValue));
            setSummary(ie.key, ie.descStringId, strVal(getResources().getStringArray(ie.arrayNames)[index]));
        }

        st.pref(this).registerOnSharedPreferenceChangeListener(this);
        
// выводить экран истории версий, или "как пользоваться клавиатурой"
        postOper();
       	
       	Ads.count_failed_load = 0;
        Ads.show(this, 1);
	}
	// проверка текущей версии, если отличается от записанной 
	// - выводим "историю версий"
	public void preOper()
	{
		vers = st.getAppVersionCode(inst);
		ini = new IniFile(inst);
    	ini.setFilename(st.getSettingsPath()+ini.PAR_INI);
		if (!ini.isFileExist()){
			if (!ini.create(st.getSettingsPath(), ini.PAR_INI))
				return;
		}
		new_vers = false;
		String par = ini.getParamValue(ini.VERSION_CODE);
		if (par == null)
			new_vers = false;
		else if (par.compareToIgnoreCase(vers)!=0)
			new_vers = true;
		par = ini.getParamValue(ini.START_TIME);
		if (par == null)
			timeini = cur_time;
		else {
			try {
				timeini=Long.parseLong(par);
			} catch (NumberFormatException e){
				timeini=cur_time;;
			}
		}
		Quick_setting_act.readQuickSetting(ini);
//		par = ini.getParamValue(ini.QUICK_SETTING);
//		if (par != null) {
//			String[] ar = par.split(st.STR_COMMA);
//			int zn = 0;
//			for (int i=0;i<st.qs_ar.length;i++){
//				st.qs_ar[i]=0;
//				try{
//					zn = Integer.valueOf(ar[1]);
//					st.qs_ar[i]=zn;
//				} catch (Throwable e){
//					zn = 0;
//					st.qs_ar[i]=zn;
//				}
//			}
//		}
		par = ini.getParamValue(ini.RATE_APP);
		if (par == null)
			rate_app = 0;
		else {
			try {
				rate_app=Integer.parseInt(par);
			} catch (NumberFormatException e){
				rate_app=0;
			}
		}
		if (!ini.isParamEmpty(ini.VERSION_CODE))
			ini.setParam(ini.VERSION_CODE,  vers);
		if (!ini.isParamEmpty(ini.RATE_APP))
			ini.setParam(ini.RATE_APP,  st.STR_ZERO);
		if (!ini.isParamEmpty(ini.START_TIME))
			ini.setParam(ini.START_TIME,  st.STR_NULL+cur_time);

	}
    public void postOper() 
    {
//    	new_vers = false;
       	if (new_vers) {
       		st.desc_act_ini(2);
       		st.runAct(Desc_act.class,this);
       		if (ini!=null)
       			ini.setParam(ini.VERSION_CODE, vers);
       	} else {
       		if (st.getRegisterKbd(inst)!=2){
       			if (Quick_setting_act.inst==null)
       				st.runAct(Quick_setting_act.class,inst);
       		}
       	}
//       	if (timeini+TIME_MONTH<=cur_time) {
//        	if (rate_app==0){
//        		// если записанное время меньше текущего
//        		// на 2 месяца, то считаем что приложение 
//        		// удаляли и записываем счётчик по новой
//        		if (timeini+TIME_MONTH+TIME_MONTH<=cur_time){
//        			ini.setParam(ini.START_TIME, st.STR_NULL+cur_time);
//        		} else
//        			rate_app();
//        	}
//    	}
    	
    }
    @Override
    public void onPause() {
//        Ads.pause();
        super.onPause();
    }
    @Override
    protected void onResume()
    {
    	
//		st.fl_pref_act = true;
		super.onResume();
//		if (rateStart!=0) {
//			st.toast("rateStart!=0\n");
//			try {
//				if (ini!=null) {
//					rateLen = new Date().getTime();
//					rateapp = ini.getParamValue(ini.RATE_APP);
//					if (rateapp == null) {
//						st.toast("rateapp==null");
//						ini.setParam(ini.RATE_APP, st.STR_ZERO);
//						rateStart = 0;
//					} else {
//						st.toast("rateapp!=null");
//						// если приложение уже оценивалось, то длительность времени
//						// проведенного в маркете не проверяем, а сразу пишем
//						if (rateapp.compareTo(st.STR_ONE)==0) {
//			 	           	ini.setParam(ini.RATE_APP, st.STR_ONE);
//			 	           rateStart = 0;
//						}
//						// время потраченное на оценку - если больше текущего,
//						// то пишем в RATE_APP
//						else if ((long)(rateStart+15000) >= rateLen+15000) {
//				 	           	ini.setParam(ini.RATE_APP, st.STR_ONE);
//				 	           rateStart = 0;
//						}
//					}
//					
//				}
//			} catch (Throwable e) {
//				rateStart = 0;
//			}
//		}

        Ads.show(this,1);

// основной код        
        showHelper();
    }

// копирование выделенного текста из системной кнопки share
@SuppressLint("NewApi")
public void checkStartIntent()
{
	intent_share = false;
	Intent intent = new Intent();
	intent = getIntent();
	String type = intent.getType ();
    String action = intent.getAction();
	String txt = intent.getStringExtra(Intent.EXTRA_TEXT);
    if (txt ==null)
    		return;
    if (Intent.ACTION_SEND.equals(action) && type != null) {
        if ("text/plain".equals(type)) {
        	st.stor().saveClipboardString(txt, 0);
    		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
    		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
    		     ClipboardManager clipboard =  (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
    		        ClipData clip = ClipData.newPlainText("label", txt);
    		        clipboard.setPrimaryClip(clip); 
    		} else{
    		    ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE); 
    		    clipboard.setText(txt);
    		}
    		st.toast(getString(R.string.menu_copy));
        	finish();
        	intent_share= true;
        	st.toast(getString(R.string.menu_copy));
        }
    }
    intent = null;
}
    
    void showHelper()
    {
        Preference pr = getPreferenceScreen().findPreference("helper");
        if(pr==null)
            return;
        int step = st.getRegisterKbd(inst);
        if(step==1)
        {
            
        }
     // Предлагаем включить клавиатуру в настройках
        if(step==0)
        {
            pr.setTitle(R.string.helper_1);
            pr.setSummary(getString(R.string.helper_1_desc)+" \""+getString(R.string.ime_name)+"\"");
        }
        else if(step==1)
        {
            pr.setTitle(R.string.helper_2);
            pr.setSummary(getString(R.string.helper_1_desc)+" \""+getString(R.string.ime_name)+"\"");
        }
        if(step==2)
            getPreferenceScreen().removePreference(pr);
        else
            pr.setOnPreferenceClickListener(getHelperListener(step));
    }
    OnPreferenceClickListener getHelperListener(final int step)
    {
        return new OnPreferenceClickListener()
        {
            
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                if(step==0)
                    startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
                else if(step==1)
                {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Service.INPUT_METHOD_SERVICE);
                    imm.showInputMethodPicker();
                }
                return true;
            }
        };
    }
    void setGestureList(SharedPreferences p,final String set,CharSequence entries[],CharSequence entValues[])
    {
        ListPreference lp = (ListPreference)getPreferenceScreen().findPreference(set);
        if(lp!=null)
        {
            String def = st.getGestureDefault(set);
            String s = p.getString(set, def);
            String sss = st.STR_NULL;
            int index = st.getGestureIndexBySetting(s);
            if(entries==null||entries.length==0)
            {
                if (set.compareTo(st.PREF_KEY_GESTURE_SPACE_DOWN) == 0){
                	sss = strVal(st.getGestureEntries(this)[index].toString())+st.STR_CR;
                	sss += inst.getString(R.string.gesture_space_down_desc);
                   	lp.setSummary(sss);
                } else
                	lp.setSummary(strVal(st.getGestureEntries(this)[index].toString()));
                return;
            }
            lp.setEntries(entries);
            lp.setEntryValues(entValues);
            lp.setValueIndex(index);
            if (set.compareTo(st.PREF_KEY_GESTURE_SPACE_DOWN) == 0){
            	sss = strVal(entries[index].toString())+st.STR_CR;
            	sss += inst.getString(R.string.gesture_space_down_desc);
               	lp.setSummary(sss);
            } else
            	lp.setSummary(strVal(entries[index].toString()));
        }
    }
    final String strVal(String src)
    {
    	if (src.length() < 1)
    		return src;
        return "[ "+src+" ]";
    }
    @Override
    protected void onDestroy()
    {
        inst = null;
        st.pref(this).unregisterOnSharedPreferenceChangeListener(this);
        if(JbKbdView.inst!=null)
            JbKbdView.inst.setPreferences();
        Ads.destroy();
        super.onDestroy();
        st.sleep(100);
    }
//    void runSetKbd(int action, Context c)
//    {
//        try{
//        	if (st.getRegisterKbd(c) < 2) {
//        		st.toast(c.getString(R.string.kbd_warning));
//        		return;
//        	}
//            Intent in = new Intent(Intent.ACTION_VIEW)
//            .setComponent(new ComponentName(this, SetKbdActivity.class))
//            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            .putExtra(st.SET_INTENT_ACTION, action);
//            startActivity(in);
//        }
//        catch(Throwable e)
//        {
//        }
//
//    }
    @SuppressWarnings("deprecation")
	@Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, 
    		Preference preference) 
    {
        inst = this;
        if (!Perm.checkPermission(inst)) {
   			st.runAct(Quick_setting_act.class,inst);
   			return true;
        }

        String k = preference.getKey();
        Context c = this;
        if("pref_ac_height".equals(k))
        {
        	showAcHeight();
            return true;
        }
//        else if("calc_corr_ind".equals(k))
//        {
//        	showCalcHeightCorrInd();
//            return true;
//        }
        else if("show_kbd_notif".equals(k))
        {
    		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
    			st.toast(inst, R.string.honeycomb);
                return false;
    		}
        }
        else if("kbd_background_alpha".equals(k))
        {
        	showAlpha();
            return true;
        }
        else if("kbd_background_pict".equals(k))
        {
        	String txt = null;
        	if (st.kbd_back_pict!=null&&st.kbd_back_pict.length()>0)
        		txt = inst.getString(R.string.cancel_kbd_background);
            new DlgFileExplorer(inst,
            		DlgFileExplorer.PICTURE_EXT,
            		null,
            		txt,
            		DlgFileExplorer.SELECT_FILE) {
                @Override
                public void onSelected(File file)
                {
                	if (file == null)
                		st.kbd_back_pict = st.STR_NULL;
                	else
                    	st.kbd_back_pict = file.getAbsolutePath();
                	st.pref().edit().putString(st.KBD_BACK_PICTURE, st.kbd_back_pict).commit();
                	if (st.kv()!=null)
                        st.kv().reloadSkin();
                    SharedPreferences p = st.pref(st.c());
                    setSummary(st.KBD_BACK_PICTURE, R.string.set_kbd_background_desc, strVal(st.pref(st.c()).getString(st.KBD_BACK_PICTURE,st.STR_NULL)));

                }
            }
            .show();
            return true;
        }
        else if("rate_application".equals(k))
        {
        	rate_app();
            return true;
        }
        else if("clipbrd_sync".equals(k))
        {
            st.runAct(ClipbrdSyncAct.class,c);
            return true;
        }
        else if("clipboard_size".equals(k))
        {
        	showClipboardSize();
            return true;
        }
        else if("ac_list_value".equals(k))
        {
        	showAcCountWord();
            return true;
        }
        else if("quick_setting".equals(k))
        {
            st.runAct(Quick_setting_act.class,c);
            return true;
        }
        else if("skin_constructor".equals(k))
        {
            st.runAct(SkinConstructorAct.class,c);
            return true;
        }
        else if("set_sound".equals(k))
        {
            st.runAct(SetSound.class,c);
        }
        else if("ac_load_vocab".equals(k))
        {
            st.runAct(UpdVocabActivity.class,c);
        }
        else if("vibro_durations".equals(k))
        {
            showVibroDuration();
        }
        else if("intervals".equals(k))
        {
            showIntervalsEditor();
        }
        else if("pop_txt_color".equals(k))
        {
        	showTextColorPopupWindowValsEditor();
        }
        else if("pop_back_color".equals(k))
        {
        	showBackColorPopupWindowValsEditor();
        }
        else if("default_setting".equals(k))
        {
            Dlg.yesNoDialog(inst, getString(R.string.are_you_sure), new st.UniObserver()
            {
                @Override
                public int OnObserver(Object param1, Object param2)
                {
                    if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                    {
                    	st.pref().edit().clear().commit();
                    	System.exit(0);
                    	st.toast(R.string.ok);
                    }
                    return 0;
                }
            });
        	
//            GlobDialog gd = new GlobDialog(st.c());
//            gd.set(R.string.are_you_sure, R.string.yes, R.string.no);
//            gd.setObserver(new st.UniObserver()
//            {
//                @Override
//                public int OnObserver(Object param1, Object param2)
//                {
//                    if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
//                    {
//                    	st.pref().edit().clear().commit();
//                    	st.toast(R.string.ok);
//                    }
//                    return 0;
//                }
//            });
//            gd.showAlert();
        }
        else if(st.PREF_KEY_LOAD.equals(k))
        {
        	if (st.getRegisterKbd(inst) < 2)
        		st.toast(getString(R.string.kbd_warning));
        	else
        		backup(false);
        }
        else if(st.PREF_KEY_SAVE.equals(k))
        {
        	if (st.getRegisterKbd(inst) < 2)
        		st.toast(getString(R.string.kbd_warning));
        	else
        		backup(true);
        }
        else if("set_skins".equals(k))
        {
            String err = CustomKbdDesign.loadCustomSkins();
            if(err.length()>0)
            {
               Toast.makeText(this, err, 1000).show();
            }
            st.runSetKbd(inst,st.SET_SELECT_SKIN);
            return true;
        }
        else if("pref_calib_portrait".equals(k))
        {
            st.runSetKbd(inst,st.SET_KEY_CALIBRATE_PORTRAIT);
            return true;
        }
        else if("pref_calib_landscape".equals(k))
        {
        	st.runSetKbd(inst,st.SET_KEY_CALIBRATE_LANDSCAPE);
            return true;
        }
        else if("pref_port_key_height".equals(k))
        {
        	st.runSetKbd(inst,st.SET_KEY_HEIGHT_PORTRAIT);
            return true;
        }
        else if("pref_land_key_height".equals(k))
        {
        	st.runSetKbd(inst,st.SET_KEY_HEIGHT_LANDSCAPE);
            return true;
        }
        else if("save_pop2_str".equals(k))
        {
            File f = new File(st.getSettingsPath()+"/gesture_string.txt");
            if (f.exists()){
                GlobDialog gd = new GlobDialog(st.c());
                gd.set(R.string.pop2s_file_not_empty, R.string.yes, R.string.no);
                gd.setObserver(new st.UniObserver()
                {
                    @Override
                    public int OnObserver(Object param1, Object param2)
                    {
                        if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                        {
                        	savePop2str();
                        }
                        return 0;
                    }
                });
                gd.showAlert();
            } else
            	savePop2str();
            return true;
        }
        else if("load_pop2_str".equals(k))
        {
            
            if (st.gesture_str.length()>0){
                GlobDialog gd = new GlobDialog(st.c());
                gd.set(R.string.pop2s_str_not_empty, R.string.yes, R.string.no);
                gd.setObserver(new st.UniObserver()
                {
                    @Override
                    public int OnObserver(Object param1, Object param2)
                    {
                        if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                        {
                        	loadPop2str();
                        }
                        return 0;
                    }
                });
                gd.showAlert();
            } else
            	loadPop2str();
            return true;
        }
  // вставка пробела после символов
        else if("space_sentence".equals(k))
        {

//        	GlobDialog gd = new GlobDialog(st.c());
//            gd.set(R.string.calc_load_prg_msg, R.string.yes, R.string.no);
//            gd.setObserver(new st.UniObserver()
//            {
//                @Override
//                public int OnObserver(Object param1, Object param2)
//                {
//                    if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
//                    {
//                    	
//                    }
//                    return 0;
//                }
//            });
//            gd.showAlert();
//        	return true;
        }
//        else if("rate_app".equals(k))
//        {
//        	Intent intent = new Intent(Intent.ACTION_VIEW);
//        	intent.setData(Uri.parse("market://details?id=com.jbak2.JbakKeyboard"));
//        	startActivity(intent);        
//        }
        else if("jbak2layout_app".equals(k))
        {
        	Intent intent = new Intent(Intent.ACTION_VIEW);
        	intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.jbak2.layout"));
        	startActivity(intent);
        }
        else if("jbak2skin_app".equals(k))
        {
        	Intent intent = new Intent(Intent.ACTION_VIEW);
        	intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.jbak2.skin"));
        	startActivity(intent);
        }
        else if("set_key_main_font".equals(k))
        {
            c.startActivity(
                    new Intent(c,EditSetActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(EditSetActivity.EXTRA_PREF_KEY, st.PREF_KEY_MAIN_FONT)
                    .putExtra(EditSetActivity.EXTRA_DEFAULT_EDIT_SET, draw.paint().getDefaultMain().toString())
                );
            
        }
        else if("set_key_second_font".equals(k))
         {
             c.startActivity(
                     new Intent(c,EditSetActivity.class)
                     .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                     .putExtra(EditSetActivity.EXTRA_PREF_KEY, st.PREF_KEY_SECOND_FONT)
                     .putExtra(EditSetActivity.EXTRA_DEFAULT_EDIT_SET, draw.paint().getDefaultSecond().toString())
                 );
             
         }
        else if("set_key_label_font".equals(k))
         {
             c.startActivity(
                     new Intent(c,EditSetActivity.class)
                     .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                     .putExtra(EditSetActivity.EXTRA_PREF_KEY, st.PREF_KEY_LABEL_FONT)
                     .putExtra(EditSetActivity.EXTRA_DEFAULT_EDIT_SET, draw.paint().getDefaultLabel().toString())
                 );
             
         }
        else if("pref_languages".equals(k))
        {
        	if (!isKbdRegister()) {
        		return false;
        	}
            st.runAct(LangSetActivity.class,c);
            return true;
        }
        else if("fs_editor_set".equals(k))
        {
            getApplicationContext().startActivity(
                    new Intent(getApplicationContext(),EditSetActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(EditSetActivity.EXTRA_PREF_KEY, st.PREF_KEY_EDIT_SETTINGS)
                );
            return true;
        }
        else if("ac_font".equals(k))
        {
            getApplicationContext().startActivity(
                    new Intent(getApplicationContext(),EditSetActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(EditSetActivity.EXTRA_PREF_KEY, st.PREF_KEY_FONT_PANEL_AUTOCOMPLETE)
                    .putExtra(EditSetActivity.EXTRA_DEFAULT_EDIT_SET, JbCandView.getDefaultEditSet(this).toString())
                );
            return true;
        }
        else if("edit_user_vocab".equals(k))
        {
            vocabTest();
        	st.runAct(EditUserVocab.class,c);
            return true;
        }
        else if("annotation".equals(k))
        {
            vocabTest();
        	st.desc_act_ini(1);
        	st.runAct(Desc_act.class,c);
            return true;
        }
        else if("dict_app".equals(k))
        {
            vocabTest();
        	st.runApp(this,"com.jbak2.dictionary");
            return true;
        }
// моя старая Новая загрузка словарей
//        else if("dict_app".equals(k))
//        {
//            vocabTest();
//            st.runAct(NewDictionary.class,c);
//            return true;
//        }
        else if("about_app".equals(k))
        {
            vocabTest();
            st.runAct(AboutActivity.class,c);
            return true;
        }
        else if("mainmenu_setting".equals(k))
        {
        	if (!isKbdRegister()) {
        		return false;
        	}
            vocabTest();
            st.runAct(MainmenuAct.class,c);
            return true;
        }
        else if("gesture_create".equals(k))
        {
            vocabTest();
            st.runAct(Gesture_create.class,c);
            return true;
        }
        else if("ac_key_color".equals(k))
        {
           	if (!isKbdRegister()) {
           		return false;
           	}
            vocabTest();
            st.runAct(AcColorAct.class,c);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
    void setSummary(String prefName,int summaryStr,String value)
    {
		Preference p = getPreferenceScreen().findPreference(prefName);
        if(p!=null)
        {
            String summary;
            if(summaryStr==0)
            {
                summary = value;
            }
            else
            {
            	if (value!=null&&value.length() > 0){
            		summary = value+"\n"+getString(summaryStr);
            	} else {
            		summary = value+getString(summaryStr);
            	}
                	
            } 
            	
            p.setSummary(summary);
        }
    }
    void setShiftState()
    {
        int v = Integer.decode(st.pref(this).getString(st.PREF_KEY_SHIFT_STATE, st.STR_ZERO));
        setSummary(st.PREF_KEY_SHIFT_STATE,0,getResources().getStringArray(R.array.array_shift_vars)[v]);
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if(st.PREF_KEY_GESTURE_LEFT.equals(key)||st.PREF_KEY_GESTURE_RIGHT.equals(key)
            ||st.PREF_KEY_GESTURE_UP.equals(key)||st.PREF_KEY_GESTURE_DOWN.equals(key)
            ||st.PREF_KEY_GESTURE_SPACE_LEFT.equals(key)||st.PREF_KEY_GESTURE_SPACE_RIGHT.equals(key)
            ||st.PREF_KEY_GESTURE_SPACE_UP.equals(key)||st.PREF_KEY_GESTURE_SPACE_DOWN.equals(key)
         )
        {
            JbKbdView.inst = null;
            setGestureList(sharedPreferences, key, null, null);
        }
        if(st.PREF_KEY_USE_GESTURES.equals(key))
            JbKbdView.inst = null;
        if(st.PREF_KEY_SHIFT_STATE.equals(key))
            setShiftState();
        for(IntEntry ie:arIntEntries)
        {
            if(ie.key.equals(key))
            {
                int index = Integer.decode(sharedPreferences.getString(key, ie.defValue));
                setSummary(key, ie.descStringId, strVal(getResources().getStringArray(ie.arrayNames)[index]));
                break;
            }
        }
        if(st.SET_GESTURE_LENGTH.equals(key))
        {
            setSummary(key, R.string.set_kbd_background_desc, st.STR_NULL);
        }
// вывод установленних значений под строкой настройки (не массивов!)
        if(st.PREF_KEY_CLIPBRD_SIZE.equals(key))
        {
            if(checkIntValue(key,DEF_SIZE_CLIPBRD))
            {
            	setValue(key,R.string.set_key_clipbrd_size_desc, DEF_SIZE_CLIPBRD);
            	
            }
        }
        if(st.KBD_BACK_ALPHA.equals(key))
        {
        	setValue(key,R.string.set_kbd_background_alpha_desc, st.STR_NULL+st.KBD_BACK_ALPHA_DEF);
        }
        if(st.PREF_AC_DEFKEY.equals(key))
        {
    		Preference p = getPreferenceScreen().findPreference(key);
    		if (p.getSharedPreferences().getString(key, st.AC_DEF_WORD).trim().isEmpty())
    			p.getEditor().putString(key, st.AC_DEF_WORD).commit();        	
    		setValue(key,R.string.set_ac_defkey_desc, st.AC_DEF_WORD);
        }
        if(st.PREF_AC_HEIGHT.equals(key))
        {
        	setValue(key,R.string.set_key_ac_height_desc, st.STR_ZERO);
        }
        //setSummary(st.AC_HEIGHT, R.string.set_key_ac_height_desc, strVal(p.getString(st.AC_HEIGHT,st.STR_ZERO )));

        if(st.SET_GESTURE_LENGTH.equals(key))
        {
        	setValue(key,R.string.set_key_gesture_length_desc, "100");
        }
        if(st.SET_GESTURE_VELOCITY.equals(key))
        {
        	setValue(key,R.string.set_key_gesture_vel_desc, "150");
        }
        if(st.AC_LIST_VALUE.equals(key))
        {
        	setValue(key,R.string.ac_list_value_desc, "20");
        }
        if(st.SET_STR_GESTURE_DOPSYMB.equals(key))
        {
        	setValue(key,R.string.gesture_popupchar_str1_desc, st.STR_NULL);
        }
        if(st.MM_BTN_SIZE.equals(key))
        {
        	setValue(key,R.string.mm_btn_size_desc, "15");
        }
        if(st.MM_BTN_OFF_SIZE.equals(key))
        {
        	setValue(key,R.string.mm_btnoff_size_desc, "8");
        }
    }
// вывод текущего значения параметра НЕ МАССИВА, в виде [value]\ntext
    void setValue(String key, int id, String defValue)
    {
        try{
            setSummary(key, id,strVal(st.pref(this).getString(key, defValue)));
        }
        catch (Throwable e) {
        }
    }
    boolean checkIntValue(String key,String defValue)
    {
        String v = st.pref(this).getString(key, st.STR_ZERO);
        boolean bOk = true;
        for(int i = v.length()-1;i>=0;i--)
        {
            if(!Character.isDigit(v.charAt(i)))
            {
                bOk = false; 
                break;
            }
        }
        if(!bOk)
        {
            Toast.makeText(this, "Incorrect integer value!", 700).show();
            st.pref(this).edit().putString(key, defValue).commit();
        }
        return bOk;
    }
/** ��������� ���������� ������� */    
    void showIntervalsEditor()
    {
        final View v = getLayoutInflater().inflate(R.layout.edit_intervals, null);
        int max = 5000,min = 50;
//        int steps[] = new int[]{50,100,100};
        int steps[] = new int[]{10,10,10};
        final SharedPreferences p = st.pref(this);

        ((TextView)v.findViewById(R.id.ei_title)).setText(R.string.set_key_intervals);

        IntEditor ie = null;
        ie = (IntEditor)v.findViewById(R.id.long_press);
        ie.setMinAndMax(min, max);
        ie.setValue(p.getInt(st.PREF_KEY_LONG_PRESS_INTERVAL, 500));
        ie.setSteps(steps);
        
        ie = (IntEditor)v.findViewById(R.id.first_repeat);
        min = 50;
        ie.setMinAndMax(min, max);
        ie.setValue(p.getInt(st.PREF_KEY_REPEAT_FIRST_INTERVAL, 400));
        ie.setSteps(steps);

        ie = (IntEditor)v.findViewById(R.id.next_repeat);
        min = 50;
        ie.setMinAndMax(min, max);
        ie.setValue(p.getInt(st.PREF_KEY_REPEAT_NEXT_INTERVAL, 50));
        ie.setSteps(steps);
        st.UniObserver obs = new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                {
                    IntEditor ie;
                    Editor e = p.edit();
                    ie = (IntEditor)v.findViewById(R.id.long_press);
                    e.putInt(st.PREF_KEY_LONG_PRESS_INTERVAL, ie.getValue());
                    ie = (IntEditor)v.findViewById(R.id.first_repeat);
                    e.putInt(st.PREF_KEY_REPEAT_FIRST_INTERVAL, ie.getValue());
                    ie = (IntEditor)v.findViewById(R.id.next_repeat);
                    e.putInt(st.PREF_KEY_REPEAT_NEXT_INTERVAL, ie.getValue());
                    e.commit();
                    if(OwnKeyboardHandler.inst!=null)
                        OwnKeyboardHandler.inst.loadFromSettings();
                }
                return 0;
            }
        };
        Dlg.CustomDialog(this, v, getString(R.string.ok), getString(R.string.cancel), null, obs);
    }
    void showTextColorPopupWindowValsEditor()
    {
        final View v = getLayoutInflater().inflate(R.layout.edit_intervals, null);
        int max = 255,min = 0;
        int steps[] = new int[]{1,5,20};
        final SharedPreferences p = st.pref(this);

        ((TextView)v.findViewById(R.id.ei_title)).setText(R.string.pop_scr_txt);

        ((TextView)v.findViewById(R.id.interval1)).setText(R.string.pop_scr_fon_r);
        ((TextView)v.findViewById(R.id.interval2)).setText(R.string.pop_scr_fon_g);
        ((TextView)v.findViewById(R.id.interval3)).setText(R.string.pop_scr_fon_b);

        final IntEditor ie = (IntEditor)v.findViewById(R.id.long_press);
        ie.setMinAndMax(min, max);
        ie.setValue(st.str2int(p.getString(st.POP_COLOR_TEXT_R, st.STR_ZERO),0,255,st.STR_ERROR));
        ie.setSteps(steps);
        
        final IntEditor ie1 = (IntEditor)v.findViewById(R.id.first_repeat);
        ie1.setMinAndMax(min, max);
        ie1.setValue(st.str2int(p.getString(st.POP_COLOR_TEXT_G, st.STR_ZERO),0,255,st.STR_ERROR));
        ie1.setSteps(steps);

        final IntEditor ie2 = (IntEditor)v.findViewById(R.id.next_repeat);
        ie2.setMinAndMax(min, max);
        ie2.setValue(st.str2int(p.getString(st.POP_COLOR_TEXT_B, st.STR_ZERO),0,255,st.STR_ERROR));
        ie2.setSteps(steps);
        st.UniObserver obs = new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                {
                    IntEditor ie;
                    Editor e = p.edit();
                    ie = (IntEditor)v.findViewById(R.id.long_press);
                    e.putString(st.POP_COLOR_TEXT_R, st.STR_NULL+ie.getValue());
                    ie = (IntEditor)v.findViewById(R.id.first_repeat);
                    e.putString(st.POP_COLOR_TEXT_G, st.STR_NULL+ie.getValue());
                    ie = (IntEditor)v.findViewById(R.id.next_repeat);
                    e.putString(st.POP_COLOR_TEXT_B, st.STR_NULL+ie.getValue());
                    ie = (IntEditor)v.findViewById(R.id.next_repeat);
                    e.commit();
                    if(OwnKeyboardHandler.inst!=null)
                        OwnKeyboardHandler.inst.loadFromSettings();
                    
                }
                return 0;
            }
        };
        final Button btn = (Button)v.findViewById(R.id.ei_btn_def);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ie.setValue(0);
                ie1.setValue(0);
                ie2.setValue(0);
                if(OwnKeyboardHandler.inst!=null)
                    OwnKeyboardHandler.inst.loadFromSettings();
            }
        });
        Dlg.CustomDialog(this, v, getString(R.string.ok), getString(R.string.cancel), null, obs);
    }
    void showBackColorPopupWindowValsEditor()
    {
        final View v = getLayoutInflater().inflate(R.layout.edit_intervals, null);
        int max = 255,min = 0;
        int steps[] = new int[]{1,5,20};
        final SharedPreferences p = st.pref(this);

        ((TextView)v.findViewById(R.id.ei_title)).setText(R.string.pop_scr_fon);

        ((TextView)v.findViewById(R.id.interval1)).setText(R.string.pop_scr_fon_r);
        final IntEditor ie = (IntEditor)v.findViewById(R.id.long_press);
        ie.setMinAndMax(min, max);
        ie.setValue(st.str2int(p.getString(st.POP_COLOR_R, st.STR_NULL+max),0,255,st.STR_ERROR));
        ie.setSteps(steps);
        
        ((TextView)v.findViewById(R.id.interval2)).setText(R.string.pop_scr_fon_g);
        final IntEditor ie1 = (IntEditor)v.findViewById(R.id.first_repeat);
        ie1.setMinAndMax(min, max);
        ie1.setValue(st.str2int(p.getString(st.POP_COLOR_G, st.STR_NULL+max),0,255,st.STR_ERROR));
        ie1.setSteps(steps);

        ((TextView)v.findViewById(R.id.interval3)).setText(R.string.pop_scr_fon_b);
        final IntEditor ie2 = (IntEditor)v.findViewById(R.id.next_repeat);
        ie2.setMinAndMax(min, max);
        ie2.setValue(st.str2int(p.getString(st.POP_COLOR_B, st.STR_NULL+max),0,255,st.STR_ERROR));
        ie2.setSteps(steps);
        st.UniObserver obs = new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                {
                    IntEditor ie;
                    Editor e = p.edit();
                    ie = (IntEditor)v.findViewById(R.id.long_press);
                    e.putString(st.POP_COLOR_R, st.STR_NULL+ie.getValue());
                    ie = (IntEditor)v.findViewById(R.id.first_repeat);
                    e.putString(st.POP_COLOR_G, st.STR_NULL+ie.getValue());
                    ie = (IntEditor)v.findViewById(R.id.next_repeat);
                    e.putString(st.POP_COLOR_B, st.STR_NULL+ie.getValue());
                    ie = (IntEditor)v.findViewById(R.id.next_repeat);
                    e.commit();
                    if(OwnKeyboardHandler.inst!=null)
                        OwnKeyboardHandler.inst.loadFromSettings();
                }
                return 0;
            }
        };
        final Button btn = (Button)v.findViewById(R.id.ei_btn_def);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ie.setValue(255);
                ie1.setValue(255);
                ie2.setValue(255);
                if(OwnKeyboardHandler.inst!=null)
                    OwnKeyboardHandler.inst.loadFromSettings();
            }
        });
        Dlg.CustomDialog(this, v, getString(R.string.ok), getString(R.string.cancel), null, obs);
    }
    void showAcHeight()
    {
        final View v = inst.getLayoutInflater().inflate(R.layout.edit_intervals, null);
        int max = 20,min = 0;
        int steps[] = new int[]{1,2,2};
        final SharedPreferences p = st.pref(inst);

        ((TextView)v.findViewById(R.id.ei_title)).setText(R.string.set_key_ac_height);

        ((TextView)v.findViewById(R.id.interval1)).setVisibility(View.GONE);
        final IntEditor ie = (IntEditor)v.findViewById(R.id.long_press);
        ie.setMinAndMax(min, max);
        ie.setValue(st.str2int(p.getString(st.PREF_AC_HEIGHT, st.STR_ZERO),min,max,st.STR_ERROR));
        ie.setSteps(steps);
        
        ((TextView)v.findViewById(R.id.interval2)).setVisibility(View.GONE);
        ((IntEditor)v.findViewById(R.id.first_repeat)).setVisibility(View.GONE);
        ((TextView)v.findViewById(R.id.interval3)).setVisibility(View.GONE);
        ((IntEditor)v.findViewById(R.id.next_repeat)).setVisibility(View.GONE);

        st.UniObserver obs = new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                {
                    IntEditor ie;
                    Editor e = p.edit();
                    ie = (IntEditor)v.findViewById(R.id.long_press);
                    e.putString(st.PREF_AC_HEIGHT, st.STR_NULL+ie.getValue());
                    e.commit();
                    if(OwnKeyboardHandler.inst!=null)
                        OwnKeyboardHandler.inst.loadFromSettings();
                }
                return 0;
            }
        };
        final Button btn = (Button)v.findViewById(R.id.ei_btn_def);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ie.setValue(0);
                if(OwnKeyboardHandler.inst!=null)
                    OwnKeyboardHandler.inst.loadFromSettings();
            }
        });
        Dlg.CustomDialog(inst, v, inst.getString(R.string.ok), inst.getString(R.string.cancel), null, obs);
    }
    /**коррекция высоты индикатора над калькулятором */
//    void showCalcHeightCorrInd()
//    {
//        final View v = inst.getLayoutInflater().inflate(R.layout.edit_intervals, null);
//        int max = 200,min = -200;
//        int steps[] = new int[]{1,5,10};
//        final SharedPreferences p = st.pref(inst);
//
//        ((TextView)v.findViewById(R.id.ei_title)).setText(R.string.calc_corr_ind);
//
//        ((TextView)v.findViewById(R.id.interval1)).setVisibility(View.GONE);
//        final IntEditor ie = (IntEditor)v.findViewById(R.id.long_press);
//        ie.setMinAndMax(min, max);
//        ie.setValue(st.str2int(p.getString(st.PREF_CALC_CORRECTION_IND, st.STR_NULL+25),min,max,st.STR_ERROR));
//        ie.setSteps(steps);
//        
//        ((TextView)v.findViewById(R.id.interval2)).setVisibility(View.GONE);
//        ((IntEditor)v.findViewById(R.id.first_repeat)).setVisibility(View.GONE);
//        ((TextView)v.findViewById(R.id.interval3)).setVisibility(View.GONE);
//        ((IntEditor)v.findViewById(R.id.next_repeat)).setVisibility(View.GONE);
//
//        st.UniObserver obs = new st.UniObserver()
//        {
//            @Override
//            public int OnObserver(Object param1, Object param2)
//            {
//                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
//                {
//                    IntEditor ie;
//                    Editor e = p.edit();
//                    ie = (IntEditor)v.findViewById(R.id.long_press);
//                    e.putString(st.PREF_CALC_CORRECTION_IND, st.STR_NULL+ie.getValue());
//                    e.commit();
//                    if(OwnKeyboardHandler.inst!=null)
//                        OwnKeyboardHandler.inst.loadFromSettings();
//                }
//                return 0;
//            }
//        };
//        final Button btn = (Button)v.findViewById(R.id.ei_btn_def);
//        btn.setVisibility(View.VISIBLE);
//        btn.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                ie.setValue(25);
//                if(OwnKeyboardHandler.inst!=null)
//                    OwnKeyboardHandler.inst.loadFromSettings();
//            }
//        });
//        Dlg.CustomDialog(inst, v, inst.getString(R.string.ok), inst.getString(R.string.cancel), null, obs);
//    }
    void showAlpha()
    {
        final View v = inst.getLayoutInflater().inflate(R.layout.edit_intervals, null);
        int max = 10,min = 0;
        int steps[] = new int[]{1,1,1};
        final SharedPreferences p = st.pref(inst);

        ((TextView)v.findViewById(R.id.ei_title)).setText(R.string.set_kbd_background_alpha);

        ((TextView)v.findViewById(R.id.interval1)).setVisibility(View.GONE);
        final IntEditor ie = (IntEditor)v.findViewById(R.id.long_press);
        ie.setMinAndMax(min, max);
        ie.setValue(st.str2int(p.getString(st.KBD_BACK_ALPHA, st.STR_NULL+st.KBD_BACK_ALPHA_DEF),min,max,st.STR_ERROR));
        ie.setSteps(steps);
        
        ((TextView)v.findViewById(R.id.interval2)).setVisibility(View.GONE);
        ((IntEditor)v.findViewById(R.id.first_repeat)).setVisibility(View.GONE);
        ((TextView)v.findViewById(R.id.interval3)).setVisibility(View.GONE);
        ((IntEditor)v.findViewById(R.id.next_repeat)).setVisibility(View.GONE);

        st.UniObserver obs = new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                {
                    IntEditor ie;
                    Editor e = p.edit();
                    ie = (IntEditor)v.findViewById(R.id.long_press);
                	st.arDesign = null;
                    e.putString(st.KBD_BACK_ALPHA, st.STR_NULL+ie.getValue());
                    e.commit();
                    if(OwnKeyboardHandler.inst!=null)
                        OwnKeyboardHandler.inst.loadFromSettings();
                }
                return 0;
            }
        };
        final Button btn = (Button)v.findViewById(R.id.ei_btn_def);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ie.setValue(st.KBD_BACK_ALPHA_DEF);
            	st.arDesign = null;
                if(OwnKeyboardHandler.inst!=null)
                    OwnKeyboardHandler.inst.loadFromSettings();
            }
        });
        Dlg.CustomDialog(inst, v, inst.getString(R.string.ok), inst.getString(R.string.cancel), null, obs);
    }
    void showAcCountWord()
    {
        final View v = getLayoutInflater().inflate(R.layout.edit_intervals, null);
        int max = 1000,min = 1;
        int steps[] = new int[]{1,5,20};
        final SharedPreferences p = st.pref(this);

        ((TextView)v.findViewById(R.id.ei_title)).setText(R.string.ac_list_value);

        ((TextView)v.findViewById(R.id.interval1)).setVisibility(View.GONE);
        final IntEditor ie = (IntEditor)v.findViewById(R.id.long_press);
        ie.setMinAndMax(min, max);
        ie.setValue(st.str2int(p.getString(st.AC_LIST_VALUE, st.STR_NULL+40),min,max,st.STR_ERROR));
        ie.setSteps(steps);
        
        ((TextView)v.findViewById(R.id.interval2)).setVisibility(View.GONE);
        ((IntEditor)v.findViewById(R.id.first_repeat)).setVisibility(View.GONE);
        ((TextView)v.findViewById(R.id.interval3)).setVisibility(View.GONE);
        ((IntEditor)v.findViewById(R.id.next_repeat)).setVisibility(View.GONE);

        st.UniObserver obs = new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                {
                    IntEditor ie;
                    Editor e = p.edit();
                    ie = (IntEditor)v.findViewById(R.id.long_press);
                    e.putString(st.AC_LIST_VALUE, st.STR_NULL+ie.getValue());
                    e.commit();
                    if(OwnKeyboardHandler.inst!=null)
                        OwnKeyboardHandler.inst.loadFromSettings();
                }
                return 0;
            }
        };
        final Button btn = (Button)v.findViewById(R.id.ei_btn_def);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ie.setValue(40);
                if(OwnKeyboardHandler.inst!=null)
                    OwnKeyboardHandler.inst.loadFromSettings();
            }
        });
        Dlg.CustomDialog(this, v, getString(R.string.ok), getString(R.string.cancel), null, obs);
    }
    void showClipboardSize()
    {
        final View v = getLayoutInflater().inflate(R.layout.edit_intervals, null);
        int max = 1000,min = 1;
        int steps[] = new int[]{1,10,50};
        final SharedPreferences p = st.pref(this);

        ((TextView)v.findViewById(R.id.ei_title)).setText(R.string.set_key_clipbrd_size);

        ((TextView)v.findViewById(R.id.interval1)).setVisibility(View.GONE);
        final IntEditor ie = (IntEditor)v.findViewById(R.id.long_press);
        ie.setMinAndMax(min, max);
        ie.setValue(st.str2int(p.getString(st.PREF_KEY_CLIPBRD_SIZE, st.STR_NULL+20),min,max,st.STR_ERROR));
        ie.setSteps(steps);
        
        ((TextView)v.findViewById(R.id.interval2)).setVisibility(View.GONE);
        ((IntEditor)v.findViewById(R.id.first_repeat)).setVisibility(View.GONE);
        ((TextView)v.findViewById(R.id.interval3)).setVisibility(View.GONE);
        ((IntEditor)v.findViewById(R.id.next_repeat)).setVisibility(View.GONE);

        st.UniObserver obs = new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                {
                    IntEditor ie;
                    Editor e = p.edit();
                    ie = (IntEditor)v.findViewById(R.id.long_press);
                    e.putString(st.PREF_KEY_CLIPBRD_SIZE, st.STR_NULL+ie.getValue());
                    e.commit();
                    if(OwnKeyboardHandler.inst!=null)
                        OwnKeyboardHandler.inst.loadFromSettings();
                }
                return 0;
            }
        };
        final Button btn = (Button)v.findViewById(R.id.ei_btn_def);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ie.setValue(20);
                if(OwnKeyboardHandler.inst!=null)
                    OwnKeyboardHandler.inst.loadFromSettings();
            }
        });
        Dlg.CustomDialog(this, v, getString(R.string.ok), getString(R.string.cancel), null, obs);
    }
    void showVibroDuration()
    {
        final View v = getLayoutInflater().inflate(R.layout.edit_intervals, null);
        int max = 5000,min = 10;
        ((TextView)v.findViewById(R.id.interval1)).setText(R.string.set_key_short_vibro_duration);
        ((TextView)v.findViewById(R.id.interval2)).setText(R.string.set_key_long_vibro_duration);
        ((TextView)v.findViewById(R.id.interval3)).setText(R.string.set_key_repeat_vibro_duration);
        ((TextView)v.findViewById(R.id.ei_title)).setText(R.string.set_key_vibro_durations);
        int steps[] = new int[]{5,10,20};
        final SharedPreferences p = st.pref(this);


        IntEditor ie = null;
        IntEditor.OnChangeValue cv = new IntEditor.OnChangeValue()
        {
            @Override
            public void onChangeIntValue(IntEditor edit)
            {
                VibroThread.getInstance(inst).runForce(edit.getValue());
            }
        };
        ie = (IntEditor)v.findViewById(R.id.long_press);
        ie.setMinAndMax(min, max);
        ie.setValue(Integer.decode(p.getString(st.PREF_KEY_VIBRO_SHORT_DURATION, DEF_LONG_VIBRO)));
        ie.setSteps(steps);
        ie.setOnChangeValue(cv);

        ie = (IntEditor)v.findViewById(R.id.first_repeat);
        ie.setMinAndMax(min, max);
        ie.setValue(Integer.decode(p.getString(st.PREF_KEY_VIBRO_LONG_DURATION, DEF_LONG_VIBRO)));
        ie.setSteps(steps);
        ie.setOnChangeValue(cv);

        ie = (IntEditor)v.findViewById(R.id.next_repeat);
        ie.setMinAndMax(min, max);
        ie.setValue(Integer.decode(p.getString(st.PREF_KEY_VIBRO_REPEAT_DURATION, DEF_LONG_VIBRO)));
        ie.setSteps(steps);
        ie.setOnChangeValue(cv);
        st.UniObserver obs = new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                {
                    IntEditor ie;
                    Editor e = p.edit();
                    ie = (IntEditor)v.findViewById(R.id.long_press);
                    e.putString(st.PREF_KEY_VIBRO_SHORT_DURATION, st.STR_NULL+ie.getValue());
                    ie = (IntEditor)v.findViewById(R.id.first_repeat);
                    e.putString(st.PREF_KEY_VIBRO_LONG_DURATION, st.STR_NULL+ie.getValue());
                    ie = (IntEditor)v.findViewById(R.id.next_repeat);
                    e.putString(st.PREF_KEY_VIBRO_REPEAT_DURATION, st.STR_NULL+ie.getValue());
                    e.commit();
                    if(VibroThread.inst!=null)
                        VibroThread.inst.readSettings();
                }
                return 0;
            }
        };
        Dlg.CustomDialog(this, v, getString(R.string.ok), getString(R.string.cancel), null, obs);
    }
    final String getBackupPath()
    {
        return st.getSettingsPath()+st.SETTINGS_BACKUP_FILE;
    }
//    final String getGestureAdditionalPath()
//    {
//    	String sss =st.getSettingsPath()+st.STR_SLASH+PREF_GESTURE_DOP_SYMB_FILENAME;;
//        return st.getSettingsPath()+st.STR_SLASH+PREF_GESTURE_DOP_SYMB_FILENAME;
//    }
    void backup(final boolean bSave)
    {
        Dlg.yesNoDialog(this, getString(bSave?R.string.set_key_save_pref:R.string.set_key_load_pref)+" ?", new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                {
                    int ret = BackupPref(bSave);
                    try{
                    if(ret==0)
                        Toast.makeText(getApplicationContext(), st.STR_ERROR, 700).show();
                    else if(ret==1)
                    	if(!bSave)
                    	{
                    		st.exitApp();
                    		//Toast.makeText(getApplicationContext(), R.string.reboot, Toast.LENGTH_LONG).show();                        
                    	} else{
                          Toast.makeText(getApplicationContext(), R.string.ok, 700).show();
                    	}
                   }
                   catch(Throwable e)
                    {
                    	st.toast("error save/load setting");
                    }
                }
                return 0;
            }
        });
    }
    int BackupPref(boolean bSave)
    {
        try{
            String appname = this.getPackageName();
            String path = getBackupPath();
            String prefDir = getFilesDir().getParent()+"/shared_prefs/";
            File ar[] = st.getFilesByExt(new File(prefDir), st.EXT_XML);
            if(ar==null||ar.length==0)
                return 0;
            File f = new File(path);
            FileInputStream in;
            FileOutputStream out = null;
            if(bSave)
            {

                in = new FileInputStream(ar[0]);
                f.delete();
                out = new FileOutputStream(f);
            }
            else
            {
                if(!f.exists())
                {
                    Toast.makeText(this, "File not exist: "+path, 700).show();
                    return -1;
                }
                for (int i=0; i< ar.length;i++) {
                	if (ar[i].toString().indexOf(appname+"_preferences.xml")>=0) {
                        out = new FileOutputStream(ar[i]);
                	}
                }
                if (out==null)
                	return -1;
                in = new FileInputStream(f);
            }
            
            byte b[] = new byte[in.available()];
            in.read(b);
            out.write(b);
            out.flush();
            in.close();
            out.close();
            if(!bSave)
            {
                if(JbKbdView.inst!=null)
                    JbKbdView.inst = null;
                if(ServiceJbKbd.inst!=null) {
                    ServiceJbKbd.inst.stopSelf();
                }
            }
            return 1;
        }
        catch (Throwable e) {
        }
        return 0;
    }
    public void vocabTest()
    {
//        Words w = new Words();
//        w.open("ru");
//        String test[] = new String[]{"��","��"};
//        long times []= new long[test.length];
//        for(int i=0;i<test.length;i++)
//        {
//            long time = System.currentTimeMillis();
//            String s[] = w.getWords(test[i]);
//            time = System.currentTimeMillis()-time;
//            times[i]=time;
//        }
//        long total = 0;
//        String log = "Test words: {";
//        for(int i=0;i<test.length;i++)
//        {
//            long time = times[i];
//            total+=time;
//            log+=test[i]+st.STR_COLON+time;
//        }
//        log+="} total:"+total;
//        Log.w("Words test", log);
    }
    public static class IntEntry
    {
        public IntEntry(String prefName,int descString,int names,String defaultValue)
        {
            key = prefName;
            descStringId = descString;
            defValue = defaultValue;
            arrayNames = names;
        }
        String key;
        int descStringId;
        int arrayNames;
        String defValue;
    }
    public void onStartService()
    {
        showHelper();
    }
//просьба оценить приложение
     public void rate_app()
     {
 		 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
 		 
 	      alertDialogBuilder.setTitle(getString(R.string.rate_title)); // Set title
 	 
 	      alertDialogBuilder
 	          // Set dialog message
 	          .setMessage(getString(R.string.rate_about))
 	                  .setCancelable(true)
 	    	          .setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
 	    	 	            public void onClick(DialogInterface dialog, int id) {
 	    	 	            	if (ini==null)
 	    	 	            		return;
 	    	 	            	// не делать оценку проведённого времени в маркете
 	    	 	            	// гугл может это воспринять как повторное вымогательство
 	    	 	            	// отзыва
// 	    	 	            	try {
// 	 	    	 	            	rateStart = new Date().getTime();
//								} catch (Throwable e) {
//								}

 	    	 	              ini.setParam(ini.RATE_APP, st.STR_ONE);

// 	    	  	              saveIniParam(st.INI_RATE_APP,st.STR_ONE);
// 	    	 	              saveIniParam("rate_start_time",st.STR_ZERO);
 	    	 	            	
 	    	 	              Uri uri = Uri.parse(st.RUN_MARKET_STRING + getPackageName()); // Go to Android market
 	    	 	              Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
 	    	 	              if (goToMarket.resolveActivity(getPackageManager()) != null) {
 	    	 	            	  startActivity(goToMarket);
 	    	 	              }
 	    	 	              dialog.cancel();
 	    	 	            }
 	    	 	          })
 	          .setPositiveButton(getString(R.string.no), new DialogInterface.OnClickListener() {
 	            public void onClick(DialogInterface dialog, int id) {
//	 	            	saveIniParam(START_TIME,st.STR_NULL+(long)(cur_time+TIME_NEGATIVE_MONTH));
//	 	 	            saveIniParam(st.INI_RATE_APP,st.STR_ZERO);

// 	            	rateStart = 0;	
 	            	dialog.cancel();
 	            }
 	          });
 	      AlertDialog alertDialog = alertDialogBuilder.create(); // Create alert dialog
 	      alertDialog.show(); // Show alert dialog
 	}
// если true - клавиатура зарегистрирована и включена в системе
// и тогда читаются настройки программы
     public boolean isKbdRegister()
    {
    	 if (st.getRegisterKbd(inst) < 2) {
     		st.toast(getString(R.string.kbd_warning));
     		return false;
     	}
    	return true;
 	}
     public void savePop2str()
    {
    	String path = st.getSettingsPath();
        File f = new File(path);
   		if (!f.isDirectory()) {
   			st.toast(getString(R.string.kbd_warning));
   			return;
   		}
    	path += st.STR_SLASH+PREF_GESTURE_DOP_SYMB_FILENAME;
		FileWriter writer;
		try {
				writer = new FileWriter(path, false);
			writer.write(st.gesture_str.trim());
			writer.close();
   			st.toast(getString(R.string.ok));
		} catch (IOException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
 	}
     public void loadPop2str()
    {
    	 String path = st.getSettingsPath();
        File f = new File(path);
   		if (!f.isDirectory()) {
   			st.toast(getString(R.string.kbd_warning));
   			return;
   		}
     	try {
        	path += st.STR_SLASH+PREF_GESTURE_DOP_SYMB_FILENAME;
     		FileReader fr= new FileReader(path);
     		Scanner sc = new Scanner(fr);
     		sc.useLocale(Locale.US);
     		st.gesture_str = st.STR_NULL;
     		while (sc.hasNextLine()) {
     			st.gesture_str += sc.nextLine();
     		}
     		sc.close();
            st.pref(st.c()).edit().putString(st.SET_STR_GESTURE_DOPSYMB, st.gesture_str.trim()).commit();
   			st.toast(getString(R.string.ok));
     	} catch(IOException ex){
     	}
 	}
     @Override
     public void onBackPressed()
     {
 		st.fl_pref_act = false;
  		 super.onBackPressed();
     }
     public boolean strToFile(String s,File f)
     {
		// выводим дату последнего редактирования
		String dt = "dd.MM.yyyy HH:mm";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dt);
			dt = sdf.format(new Date());
			dt = "Report created "+dt+st.STR_CR;
			dt += "in App: "+st.getAppNameAndVersion(inst)+st.STR_CR+st.STR_CR;
		} catch (Throwable e) {
			dt = null;
		}

     	try{
     		
     		f.delete();
     		FileOutputStream fout = new FileOutputStream(f);
     		if (dt!=null)
     			fout.write(dt.getBytes());
     		fout.write(s.getBytes());
     		fout.close();
     		return true;
     	}
     	catch(Throwable e){}
     	return false;
     }
     public String getStackString(Throwable e)
     {
     	if(e==null)
     		e = new Exception();
     	StringBuffer msg = new StringBuffer(e.getClass().getName());
     		if(!TextUtils.isEmpty(e.getMessage()))
     			msg.append(' ').append(e.getMessage());
     	msg.append('\n');
     	StackTraceElement st[] = e.getStackTrace();
     	for(StackTraceElement s:st)
     		msg.append(s.toString()).append('\n');
     	Throwable cause = e.getCause();
     	if(cause!=null&&msg.length()<MAX_STACK_STRING)
     		msg.append('\n').append(CAUSED_BY).append('\n').append(getStackString(cause));
     	String ret = msg.toString();
     	return ret;
     }
	public void saveCrash(Throwable e)
 	{
 		e.printStackTrace();
 		strToFile(getStackString(e), new File(this.getFilesDir(),SAVE_CRASH));
 	}
	public boolean checkCrash()
 	{
		// проверяем, что ведётся разработка на эмуляторе
		// и отчёт о краше выводить не надо
        if (st.isDebugEmulator())
        	return false;
 		try {
 			String path =  getPackageManager().getPackageInfo(getPackageName(), 0).applicationInfo.dataDir;

 			path+="/files"+SAVE_CRASH;
 			file_crash = new File(path);
 			if(!file_crash.exists())
 				return false;
 		} catch(Throwable e)
 		{
 		}
 		Dlg.yesNoDialog(inst, inst.getString(R.string.crash_question), new st.UniObserver() {
			
			@Override
			public int OnObserver(Object param1, Object param2) {
                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                {
                 	if (file_crash!=null){
                		Mail.sendFeedback(inst, file_crash);
                	}
                }
             	if (file_crash!=null){
               	file_crash.delete();
            		file_crash=null;
            	}
				return 0;
			}
		});
 		return true;
 	}
	public void setLangApp()
	{
		st.lang_pref = "ru";
		if (st.qs_ar[3]==0)
			st.lang_pref = Locale.getDefault().getLanguage();
		else if (st.qs_ar[3]==1)
			st.lang_pref = "ru";
		else if (st.qs_ar[3]==2)
			st.lang_pref = "en";
		else if (st.qs_ar[3]==3)
			st.lang_pref = "es";
		else if (st.qs_ar[3]==4)
			st.lang_pref = "uk";

		if (!st.lang_pref.contains(Locale.getDefault().getCountry())){
			Locale locale = new Locale(st.lang_pref);
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config, null);
			inst.recreate();
		}
 	}
}