package com.jbak2.JbakKeyboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.jbak2.ctrl.GlobDialog;
import com.jbak2.Dialog.DlgPopupWnd;
import com.jbak2.JbakKeyboard.st;

// класс для вывода активности маленькой клавиатуры второй версии
public class Popup2act extends Activity
	{
	// color picker
    ColorPicker m_colpic = null;
    static Popup2act inst;
    Button btn_unicode_app = null;
    CheckBox cb1 = null;
    CheckBox cb2 = null;
	EditText str_add = null;
	EditText win_bg = null;
	EditText btn_size = null;
	EditText btn_bg = null;
	EditText btn_tc = null;
	EditText btnoff_size = null;
	EditText btnoff_bg = null;
	EditText btnoff_tc = null;

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
	    setContentView(R.layout.popup2act);
	    inst = this;
	    View v = getLayoutInflater().inflate(R.layout.popup2act, null);
        cb1 = (CheckBox)v.findViewById(R.id.pc2act_cb1);
        cb1.setChecked(st.win_fix);
        cb1.setOnClickListener(m_ClickListener);
        
        cb2 = (CheckBox)v.findViewById(R.id.pc2act_cb2);
        cb2.setChecked(st.pc2_lr);
        cb2.setOnClickListener(m_ClickListener);
        btn_unicode_app = (Button)v.findViewById(R.id.pc2act_unicode_app);
        btn_unicode_app.setOnClickListener(m_ClickListener);
        str_add = (EditText)v.findViewById(R.id.pc2act_et_addsymb);
	    str_add.setText(st.gesture_str);
	    str_add.addTextChangedListener(tw);
        win_bg = (EditText)v.findViewById(R.id.pc2act_winbg);
        win_bg.setText(String.format("#%08x",st.win_bg));
        win_bg.setOnKeyListener(number_keyListener);
        win_bg.addTextChangedListener(tw);
        btn_size = (EditText)v.findViewById(R.id.pc2act_et_btn_size);
        btn_size.setText((st.STR_NULL+st.btn_size).trim());
        btn_size.addTextChangedListener(tw);
        btn_bg = (EditText)v.findViewById(R.id.pc2act_et_btn_bg);
        btn_bg.setText(String.format("#%08x",st.btn_bg));
        btn_bg.setOnKeyListener(number_keyListener);
        btn_bg.addTextChangedListener(tw);
        btn_tc = (EditText)v.findViewById(R.id.pc2act_et_btn_tc);
        btn_tc.setText(String.format("#%08x",st.btn_tc));
        btn_tc.setOnKeyListener(number_keyListener);
        btn_tc.addTextChangedListener(tw);
        btnoff_size = (EditText)v.findViewById(R.id.pc2act_et_btnoff_size);
        btnoff_size.setText((st.STR_NULL+st.btnoff_size).trim());
        btnoff_size.addTextChangedListener(tw);
        btnoff_bg = (EditText)v.findViewById(R.id.pc2act_et_btnoff_bg);
        btnoff_bg.setText(String.format("#%08x",st.btnoff_bg));
        btnoff_bg.setOnKeyListener(number_keyListener);
        btnoff_bg.addTextChangedListener(tw);
        btnoff_tc = (EditText)v.findViewById(R.id.pc2act_et_btnoff_tc);
        btnoff_tc.setText(String.format("#%08x",st.btnoff_tc));
        btnoff_tc.setOnKeyListener(number_keyListener);
        btnoff_tc.addTextChangedListener(tw);
        fl_changed = false;
	    super.onCreate(savedInstanceState);
        setContentView(v);
        str_add.requestFocusFromTouch();
    }
    @Override
    public void onBackPressed() 
    {
    	if (ColorPicker.inst!=null) {
    		ColorPicker.inst.finish();
    		return;
    	}
    	if (fl_changed) {
        	final DlgPopupWnd dpw = new DlgPopupWnd(st.c());
        	dpw.setGravityText(Gravity.CENTER_HORIZONTAL);
        	dpw.set(R.string.data_changed, R.string.yes, R.string.no);
        	dpw.setObserver(new st.UniObserver()
            {
                @Override
                public int OnObserver(Object param1, Object param2)
                {
                    if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                    {
                		st.pref().edit().putString(st.SET_STR_GESTURE_DOPSYMB, str_add.getText().toString().trim()).commit();
                		st.pref().edit().putString(st.PREF_KEY_PC2_WIN_BG, win_bg.getText().toString().trim()).commit();
                		st.pref().edit().putBoolean(st.PREF_KEY_PC2_WIN_FIX, cb1.isChecked()).commit();
                		st.pref().edit().putBoolean(st.PREF_KEY_PC2_LR, cb2.isChecked()).commit();
                   		st.pref().edit().putString(st.PREF_KEY_PC2_BTN_SIZE, btn_size.getText().toString().trim()).commit();
                   		st.pref().edit().putString(st.PREF_KEY_PC2_BTN_BG, btn_bg.getText().toString().trim()).commit();
                   		st.pref().edit().putString(st.PREF_KEY_PC2_BTN_TCOL, btn_tc.getText().toString().trim()).commit();
                   		st.pref().edit().putString(st.PREF_KEY_PC2_BTNOFF_SIZE, btnoff_size.getText().toString().trim()).commit();
                   		st.pref().edit().putString(st.PREF_KEY_PC2_BTNOFF_BG, btnoff_bg.getText().toString().trim()).commit();
                   		st.pref().edit().putString(st.PREF_KEY_PC2_BTNOFF_TCOL, btnoff_tc.getText().toString().trim()).commit();
                   		finish();
                   		showKbdAndAdditionalSymbol();
                    } else
                   		showKbdAndAdditionalSymbol();
                	dpw.dismiss();
                    return 0;
                }
            });
        	dpw.show(0);

//    		GlobDialog gd = new GlobDialog(st.c());
//            gd.setPositionOnKeyboard(true);
//            gd.set(R.string.data_changed, R.string.yes, R.string.no);
//            gd.setObserver(new st.UniObserver()
//            {
//                @Override
//                public int OnObserver(Object param1, Object param2)
//                {
//                    if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
//                    {
//                		st.pref().edit().putString(st.SET_STR_GESTURE_DOPSYMB, str_add.getText().toString().trim()).commit();
//                		st.pref().edit().putString(st.PREF_KEY_PC2_WIN_BG, win_bg.getText().toString().trim()).commit();
//                		st.pref().edit().putBoolean(st.PREF_KEY_PC2_WIN_FIX, cb1.isChecked()).commit();
//                   		st.pref().edit().putString(st.PREF_KEY_PC2_BTN_SIZE, btn_size.getText().toString().trim()).commit();
//                   		st.pref().edit().putString(st.PREF_KEY_PC2_BTN_BG, btn_bg.getText().toString().trim()).commit();
//                   		st.pref().edit().putString(st.PREF_KEY_PC2_BTN_TCOL, btn_tc.getText().toString().trim()).commit();
//                   		st.pref().edit().putString(st.PREF_KEY_PC2_BTNOFF_SIZE, btnoff_size.getText().toString().trim()).commit();
//                   		st.pref().edit().putString(st.PREF_KEY_PC2_BTNOFF_BG, btnoff_bg.getText().toString().trim()).commit();
//                   		st.pref().edit().putString(st.PREF_KEY_PC2_BTNOFF_TCOL, btnoff_tc.getText().toString().trim()).commit();
//                   		finish();
//                    }
//                    if (ServiceJbKbd.inst!=null&&ServiceJbKbd.inst.isInputViewShown())
//                    	st.showkbd();
//                    return 0;
//                }
//            });
//            gd.showAlert();
    	} else {
    		super.onBackPressed();
    		st.showkbd();
    	}
    	ColorPicker.inst.fl_color_picker = false;
    	finish();
     }
    public void showKbdAndAdditionalSymbol() {
        if (ServiceJbKbd.inst!=null&&ServiceJbKbd.inst.isInputViewShown()) {
        	st.showkbd();
        	st.popupAdditional(1);
        }
    }
    
//    public void onClickCB(View view) 
//    {
//    	fl_changed = true;
//    }
//    public void onClick_keycode(View view) throws NameNotFoundException {
//    	ServiceJbKbd.inst.forceHide();
//    	st.runApp(this, st.UNICODE_APP);
//    }
//    public void onClickColor(View view) 
//    {
//    	m_colpic = null;
//        m_colpic = (ColorPicker) getLayoutInflater().inflate(R.layout.picker, null);
//        if (m_colpic != null)
//        	m_colpic.show(this, win_bg);
//
//    }
    View.OnKeyListener number_keyListener = new View.OnKeyListener()
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
    View.OnClickListener m_ClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
            case R.id.pc2act_cb1:
            case R.id.pc2act_cb2:
            	fl_changed = true;
                return;
            case R.id.pc2act_unicode_app:
            	ServiceJbKbd.inst.forceHide();
            	st.runApp(inst, st.UNICODE_APP);
                return;
            case R.id.cand_left:
                return;
            }
        }
    };

}
