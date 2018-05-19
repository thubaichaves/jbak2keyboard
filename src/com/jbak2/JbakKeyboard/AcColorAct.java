package com.jbak2.JbakKeyboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.os.Bundle;
import android.widget.EditText;

import com.jbak2.JbakKeyboard.st;
import com.jbak2.ctrl.GlobDialog;

// класс установок цветов кнопок для автодополнения
public class AcColorAct extends Activity
	{
	public static EditText et_back = null;
	public static EditText et1 = null;
	public static EditText et2 = null;
	public static EditText et3 = null;
	public static EditText et4 = null;
	public static EditText et5 = null;
	public static EditText et6 = null;
	public static EditText et7 = null;
	public static EditText et8 = null;
	public static EditText et9 = null;
	public static EditText et10 = null;
	public static EditText et11 = null;
	public static EditText et12 = null;
	public static EditText et13 = null;
	public static EditText et14 = null;
	public static EditText et15 = null;
	public static EditText et16 = null;
	
	boolean fl_changed = false;
	TextWatcher tw = new TextWatcher()
	{
        @Override
        public void afterTextChanged(Editable s) 
        {
//        	fl_changed = true;
        }
         
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) 
        {
//        	fl_changed = true;
        }
     
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) 
        {
        	fl_changed = true;
        }
	};

	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.accoloract);

	    et_back = (EditText)findViewById(R.id.accolact_main_ebg);
        et_back.setHint(String.format(st.STR_16FORMAT,st.AC_COLDEF_MAIN_BG));
        et_back.setText(String.format(st.STR_16FORMAT,st.ac_col_main_back));
        et_back.setOnKeyListener(m_keyListener);
        et_back.addTextChangedListener(tw);

        et1 = (EditText)findViewById(R.id.accolact_keycode_ebg);
        et1.setHint(String.format(st.STR_16FORMAT,st.AC_COLDEF_KEYCODE_BG));
        et1.setText(String.format(st.STR_16FORMAT,st.ac_col_keycode_back));
        et1.setOnKeyListener(m_keyListener);
        et1.addTextChangedListener(tw);
        et2 = (EditText)findViewById(R.id.accolact_keycode_et);
        et2.setHint(String.format(st.STR_16FORMAT,st.AC_COLDEF_KEYCODE_T));
        et2.setText(String.format(st.STR_16FORMAT,st.ac_col_keycode_text));
        et2.setOnKeyListener(m_keyListener);
        et2.addTextChangedListener(tw);
        
        et3 = (EditText)findViewById(R.id.accolact_counter_ebg);
        et3.setHint(String.format(st.STR_16FORMAT,st.AC_COLDEF_COUNTER_BG));
        et3.setText(String.format(st.STR_16FORMAT,st.ac_col_counter_back));
        et3.setOnKeyListener(m_keyListener);
        et3.addTextChangedListener(tw);
        et4 = (EditText)findViewById(R.id.accolact_counter_et);
        et4.setHint(String.format(st.STR_16FORMAT,st.AC_COLDEF_COUNTER_T));
        et4.setText(String.format(st.STR_16FORMAT,st.ac_col_counter_text));
        et4.setOnKeyListener(m_keyListener);
        et4.addTextChangedListener(tw);
        
        et5 = (EditText)findViewById(R.id.accolact_forcibly_ebg);
        et5.setHint(String.format(st.STR_16FORMAT,st.AC_COLDEF_FORCIBLY_BG));
        et5.setText(String.format(st.STR_16FORMAT,st.ac_col_forcibly_back));
        et5.setOnKeyListener(m_keyListener);
        et5.addTextChangedListener(tw);
        et6 = (EditText)findViewById(R.id.accolact_forcibly_et);
        et6.setHint(String.format(st.STR_16FORMAT,st.AC_COLDEF_FORCIBLY_T));
        et6.setText(String.format(st.STR_16FORMAT,st.ac_col_forcibly_text));
        et6.setOnKeyListener(m_keyListener);
        et6.addTextChangedListener(tw);
        
        et7 = (EditText)findViewById(R.id.accolact_addvocab_ebg);
        et7.setHint(String.format(st.STR_16FORMAT,st.AC_COLDEF_ADD_BG));
        et7.setText(String.format(st.STR_16FORMAT,st.ac_col_addvocab_back));
        et7.setOnKeyListener(m_keyListener);
        et7.addTextChangedListener(tw);
        et8 = (EditText)findViewById(R.id.accolact_addvocab_et);
        et8.setHint(String.format(st.STR_16FORMAT,st.AC_COLDEF_ADD_T));
        et8.setText(String.format(st.STR_16FORMAT,st.ac_col_addvocab_text));
        et8.setOnKeyListener(m_keyListener);
        et8.addTextChangedListener(tw);
        
        et9 = (EditText)findViewById(R.id.accolact_word_ebg);
        et9.setHint(String.format(st.STR_16FORMAT,st.AC_COLDEF_WORD_BG));
        et9.setText(String.format(st.STR_16FORMAT,st.ac_col_word_back));
        et9.setOnKeyListener(m_keyListener);
        et9.addTextChangedListener(tw);
        et10 = (EditText)findViewById(R.id.accolact_word_et);
        et10.setHint(String.format(st.STR_16FORMAT,st.AC_COLDEF_WORD_T));
        et10.setText(String.format(st.STR_16FORMAT,st.ac_col_word_text));
        et10.setOnKeyListener(m_keyListener);
        et10.addTextChangedListener(tw);
        
        et11 = (EditText)findViewById(R.id.accolact_arrowd_ebg);
        et11.setHint(String.format(st.STR_16FORMAT,st.AC_COLDEF_ARROWDOWN_BG));
        et11.setText(String.format(st.STR_16FORMAT,st.ac_col_arrow_down_back));
        et11.setOnKeyListener(m_keyListener);
        et11.addTextChangedListener(tw);
        et12 = (EditText)findViewById(R.id.accolact_arrowd_et);
        et12.setHint(String.format(st.STR_16FORMAT,st.AC_COLDEF_ARROWDOWN_T));
        et12.setText(String.format(st.STR_16FORMAT,st.ac_col_arrow_down_text));
        et12.setOnKeyListener(m_keyListener);
        et12.addTextChangedListener(tw);
        
        et13 = (EditText)findViewById(R.id.accolact_calcmenu_ebg);
        et13.setHint(String.format(st.STR_16FORMAT,st.AC_COLDEF_CALCMENU_BG));
        et13.setText(String.format(st.STR_16FORMAT,st.ac_col_calcmenu_back));
        et13.setOnKeyListener(m_keyListener);
        et13.addTextChangedListener(tw);
        et14 = (EditText)findViewById(R.id.accolact_calcmenu_et);
        et14.setHint(String.format(st.STR_16FORMAT,st.AC_COLDEF_CALCMENU_T));
        et14.setText(String.format(st.STR_16FORMAT,st.ac_col_calcmenu_text));
        et14.setOnKeyListener(m_keyListener);
        et14.addTextChangedListener(tw);
        
        et15 = (EditText)findViewById(R.id.accolact_calcind_ebg);
        et15.setHint(String.format(st.STR_16FORMAT,st.AC_COLDEF_CALCIND_BG));
        et15.setText(String.format(st.STR_16FORMAT,st.ac_col_calcind_back));
        et15.setOnKeyListener(m_keyListener);
        et15.addTextChangedListener(tw);
        et16 = (EditText)findViewById(R.id.accolact_calcind_et);
        et16.setHint(String.format(st.STR_16FORMAT,st.AC_COLDEF_CALCIND_T));
        et16.setText(String.format(st.STR_16FORMAT,st.ac_col_calcind_text));
        et16.setOnKeyListener(m_keyListener);
        et16.addTextChangedListener(tw);

        fl_changed = false;
	    super.onCreate(savedInstanceState);
//        setContentView(v);
        et_back.requestFocusFromTouch();
       	Ads.count_failed_load = 0;
        Ads.show(this, 2);

//        // показ рекламы
//        mAdView2 = (AdView) findViewById(R.id.AdView_acolact);
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice(st.ID_DEVICE)
//                .build();
//        mAdView2.loadAd(adRequest);
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onBackPressed() 
    {
    	if (GlobDialog.gbshow){
    		GlobDialog.inst.finish();
    		return;
    	}
    	if (fl_changed) {
            GlobDialog gd = new GlobDialog(st.c());
            gd.setPositionOnKeyboard(true);
            gd.set(R.string.data_changed, R.string.yes, R.string.no);
            gd.setObserver(new st.UniObserver()
            {
                @Override
                public int OnObserver(Object param1, Object param2)
                {
                    if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                    {
                        st.pref(st.c()).edit().putString(st.AC_COL_MAIN_BG, et_back.getEditableText().toString().trim()).commit();
                        st.pref(st.c()).edit().putString(st.AC_COL_KEYCODE_BG, et1.getEditableText().toString().trim()).commit();
                        st.pref(st.c()).edit().putString(st.AC_COL_KEYCODE_T, et2.getEditableText().toString().trim()).commit();
                        st.pref(st.c()).edit().putString(st.AC_COL_COUNTER_BG, et3.getEditableText().toString().trim()).commit();
                        st.pref(st.c()).edit().putString(st.AC_COL_COUNTER_T, et4.getEditableText().toString().trim()).commit();
                        st.pref(st.c()).edit().putString(st.AC_COL_FORCIBLY_BG, et5.getEditableText().toString().trim()).commit();
                        st.pref(st.c()).edit().putString(st.AC_COL_FORCIBLY_T, et6.getEditableText().toString().trim()).commit();
                        st.pref(st.c()).edit().putString(st.AC_COL_ADD_BG, et7.getEditableText().toString().trim()).commit();
                        st.pref(st.c()).edit().putString(st.AC_COL_ADD_T, et8.getEditableText().toString().trim()).commit();
                        st.pref(st.c()).edit().putString(st.AC_COL_WORD_BG, et9.getEditableText().toString().trim()).commit();
                        st.pref(st.c()).edit().putString(st.AC_COL_WORD_T, et10.getEditableText().toString().trim()).commit();
                        st.pref(st.c()).edit().putString(st.AC_COL_ARROWDOWN_BG, et11.getEditableText().toString().trim()).commit();
                        st.pref(st.c()).edit().putString(st.AC_COL_ARROWDOWN_T, et12.getEditableText().toString().trim()).commit();
                        st.pref(st.c()).edit().putString(st.AC_COL_CALCMENU_BG, et13.getEditableText().toString().trim()).commit();
                        st.pref(st.c()).edit().putString(st.AC_COL_CALCMENU_T, et14.getEditableText().toString().trim()).commit();
                        st.pref(st.c()).edit().putString(st.AC_COL_CALCIND_BG, et15.getEditableText().toString().trim()).commit();
                        st.pref(st.c()).edit().putString(st.AC_COL_CALCIND_T, et16.getEditableText().toString().trim()).commit();
                   		finish();
                    }
                    return 0;
                }
            });
            gd.showAlert();
    	} else
    		super.onBackPressed();
    	finish();
    }
    View.OnKeyListener m_keyListener = new View.OnKeyListener()
    {
        @SuppressLint("NewApi")
		@Override
        public boolean onKey(View v, int keyCode, KeyEvent event)
        {
    	    if(event.getAction() == KeyEvent.ACTION_DOWN){ 
        	    if(!st.isHoneycomb()&&event.isCtrlPressed()&&keyCode == KeyEvent.KEYCODE_A){
 	    		
        	    	EditText et = (EditText)v;
        	    	if (et !=null){
        	    		et.selectAll();
        	    		return true;
        	    	}
        	    }
    	    }
            return false;
        }
    };
}