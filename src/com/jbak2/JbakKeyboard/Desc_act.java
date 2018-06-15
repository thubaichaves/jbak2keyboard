package com.jbak2.JbakKeyboard;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Locale;

import com.jbak2.Dialog.Dlg;

import android.app.Activity;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Desc_act extends Activity
{
	private Thread.UncaughtExceptionHandler androidDefaultUEH;
	static Desc_act inst;
	LinearLayout llcont = null;
	boolean big_size = false;
	boolean searchviewpanel = false;
	EditText et = null;
	EditText et_search = null;
	TextView tv_search = null;
	RelativeLayout searchpanel;
	ArrayList<Integer> arpos_search = new ArrayList<Integer>();
	int pos_search = -1;

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.desc_act);
        inst = this;
		androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				st.toast("error - long reading.");
				androidDefaultUEH.uncaughtException(thread, e);
			}
		});
		tv_search = null;
		et = null;
		et_search = null;
		searchpanel = null;
    	llcont = (LinearLayout)findViewById(R.id.desc_llcontrol);
        searchpanel = (RelativeLayout)findViewById(R.id.desc_rlsearch_panel);

        et = (EditText) findViewById(R.id.desc_et1);
		setViewText();
        // автолинк из xml атрибутов не работает, хоть текст и выделяет
        Linkify.addLinks(et, Linkify.ALL);
        et.setFocusableInTouchMode(true);
        Button btn_search = (Button)llcont.findViewById(R.id.desc_btn_search);
       	btn_search.setVisibility(View.VISIBLE);
        Button btn_lang = (Button)llcont.findViewById(R.id.desc_btn_sellang);
        btn_lang.setVisibility(View.VISIBLE);
        switch (st.desc_view_input)
        {
    	case 0: // вывод desc_kbd.txt при запуске настроек
        	setTitle(getString(R.string.ann));
        	break;
    	case 1: // вывод desc_kbd.txt при запуске "как пользоваться клавиатурой"
        	setTitle(getString(R.string.ann));
        	
        	break;
    	case 2: // вывод _diary.txt
        	setTitle(getString(R.string.diary));
           	btn_search.setVisibility(View.GONE);
           	btn_lang.setVisibility(View.GONE);
        	break;
        }
        hideSearchPanel();
        setButtonHeightAndSizeText();
        Ads.show(this, 3);
	}
    public void onClick(View view) 
    {
        switch (view.getId())
        {
        case R.id.desc_btn_sellang:
        	String[] ars = new String[2];
        	ars[0] = new Locale("en").getDisplayName();
        	ars[0] = st.upFirstSymbol(ars[0]);
        	ars[1] = new Locale("ru").getDisplayName();
        	ars[1] = st.upFirstSymbol(ars[1]);
           	ArrayAdapter<String> ar = new ArrayAdapter<String>(this, 
           			R.layout.tpl_instr_list,
                    ars
                    );
            Dlg.CustomMenu(inst, ar, 
            		inst.getString(R.string.euv_lang_text), 
            		new st.UniObserver()
            {
                @Override
                public int OnObserver(Object param1, Object param2)
                {
                	int pos = ((Integer)param1).intValue();
                	if ((pos == 0&&st.lang_desckbd.contains("en"))
                		||(pos == 1&&st.lang_desckbd.contains("ru"))	
                		)
                		return 0;
                	if (pos == 0)
                		st.lang_desckbd= "en";
                	else
                		st.lang_desckbd= "ru";
                	st.pref(inst).edit().putString(st.PREF_KEY_DESC_LANG_KBD, st.lang_desckbd).commit();
                	setViewText();
                	return 0;
                }
            });

            return;
        case R.id.desc_btn_size:
        	if (big_size)
        		big_size = false;
        	else
        		big_size=true;
        	setButtonHeightAndSizeText();
            return;
        case R.id.desc_btn_search:
        	showSearchPanel();
            return;
        case R.id.desc_search_close:
        	if (searchpanel !=null)
        		searchpanel.setVisibility(View.GONE);
        	if (et!=null)
        		et.setEnabled(true);
        	hideSearchPanel();
            return;
        case R.id.desc_search_down:
        	viewPosSearch(1);
            return;
        case R.id.desc_search_up:
        	viewPosSearch(-1);
            return;
//        case R.id.desc_CheckBox1:
//            CheckBox cb = (CheckBox) view.findViewById(R.id.desc_CheckBox1);
//            st.desc_fl_not_input = cb.isChecked();
//        	String path = st.getSettingsPath();
//       		path =path.substring(0,path.length()-1);
//       		File file2 = new File(path);
//       		if (file2!=null&&file2.isDirectory()) {
//       			if (st.desc_fl_not_input)
//       				JbKbdPreference.saveIniParam("desc_begin", st.STR_ONE);
//       		}
//            return;
        case R.id.desc_btn_start:
        	et.setSelection(0);
            return;
        case R.id.desc_btn_end:
        	et.setSelection(et.getText().toString().length());
            return;
        case R.id.desc_btn_pgdn: 
            et.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_MULTIPLE,
            		KeyEvent.KEYCODE_PAGE_DOWN));
            return;
        case R.id.desc_btn_pgup: 
            et.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_MULTIPLE,
            		KeyEvent.KEYCODE_PAGE_UP));
            return;
        }
    }
    public void showSearchPanel() 
    {
    	if (searchviewpanel)
    		return;
    	if (searchpanel == null)
    		return;
    	searchpanel.setVisibility(View.VISIBLE);
    	tv_search = (TextView)searchpanel.findViewById(R.id.desc_search_result);
    	et_search = (EditText)searchpanel.findViewById(R.id.desc_search_edit);
    	
    	et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView tv, int action, KeyEvent tvent) {
				  if (action == EditorInfo.IME_ACTION_SEARCH) {
	   	    			search();
	   	    			st.hidekbd();
	       				return true;
				  }
	       		return false;
			}
		});
    	et_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (hasFocus){
					et_search.setBackgroundResource(R.drawable.edittext_back_focus_style);
				}else 
					et_search.setBackgroundResource(R.drawable.edittext_back_notfocus_style);
			}
		});
    	st.showkbd(et_search);
    	if (et_search.getText().toString().length()>0){
    		search();
    	}
    	searchviewpanel = true;
    }
    public void hideSearchPanel() 
    {
    	int pos = et.getSelectionStart();
    	et.setText(et.getText().toString());
    	et.setSelection(pos);
		et.setEnabled(true);
    	if (searchpanel == null)
    		return;
    	searchpanel.setVisibility(View.GONE);
    	et_search = null;
    	searchviewpanel = false;
    }
    public void search()
    {
    	if (et.isSelected())
    		st.toast("selected");
    	String search_str = et_search.getText().toString().toLowerCase().trim(); 
    	String ettxt = et.getText().toString().toLowerCase(); 
    	String subtxt = ettxt; 
    	arpos_search.clear();;
    	if (search_str.length() == 0) {
    		et.setText(ettxt);
    		viewPosSearch(0);
    		return;
    	}
    	int pos=-1;
    	int pos1 = 0;
    	boolean fl = true;
    	while (fl)
    	{
    		pos = subtxt.indexOf(search_str);
    		if (pos != -1) {
    			pos = pos + pos1; 
    			arpos_search.add(pos);
    			if (pos<=ettxt.length()){
    				int bbb = pos+search_str.length();
    				subtxt = ettxt.substring(pos+search_str.length());
    			} else {
    				break;
    			}
    			if (pos1 == 0)
    				pos1 = pos;
    			else
    				pos1 = pos+search_str.length();
    			continue;
    		}
    		fl = false;
    	}
//!!!
    	if (arpos_search.size()>1){
    		arpos_search.remove(1);
    	}
    	if (arpos_search.size()>0){
        	Spannable text = new SpannableString(et.getText().toString());
        	for (int i=0; i<arpos_search.size();i++){
            	text.setSpan(new BackgroundColorSpan(0x88ff8c00), arpos_search.get(i), arpos_search.get(i)+search_str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        	}
        	et.setText(text);
        	et.requestFocus();
        	et.setSelection(arpos_search.get(0).intValue());
        	pos_search = 0;
        	
    	} else {
        	pos = et.getSelectionStart();
        	et.setText(et.getText().toString());
        	et.setSelection(pos);

        	et.requestFocus();
        	pos_search = -1;
    	}
    	if (searchviewpanel){
    		et.setEnabled(false);
    		et.setBackgroundColor(Color.WHITE);
    		et.setTextColor(Color.BLACK);
    	}
    	else
    		et.setEnabled(true);

    	et.setCursorVisible(true);
		viewPosSearch(0);
   }
    public void viewPosSearch(int pos) 
    {
    	if (tv_search == null)
    		return;
    	if (arpos_search.size() == 0){
    		tv_search.setText("[0/0]");
    		return;
    	}
    	if (pos == 0){
    		pos_search = 0;
    	}
    	else if (pos == 1){
    		pos_search++;
    		if (pos_search >= arpos_search.size())
    			pos_search = 0;
//			pos_search = arpos.size()-1;
    	}
    	else if (pos == -1){
    		pos_search--;
    		if (pos_search < 0)
    			pos_search = arpos_search.size()-1;
//			pos_search = 0;
    	}
    	tv_search.setText("["+(pos_search+1)+st.STR_SLASH+arpos_search.size()+"]");
    	et.requestFocus();
		et.setSelection(arpos_search.get(pos_search).intValue());
    }
    @Override
    public void onBackPressed()
    {
    	if (searchviewpanel){
    		hideSearchPanel();
    		return;
    	}
 		super.onBackPressed();
    }
    public void setButtonHeightAndSizeText()
    {
    	if (llcont == null)
    		return;
    	Button btn;
    	int size = 1;
    	for (int i=0;i<llcont.getChildCount();i++){
    		btn = null;
    		btn = (Button)llcont.getChildAt(i);
    		if (btn == null)
    			continue;
    		btn.measure(0, 0);
    		size = btn.getMeasuredHeight();
    		if (big_size){
    			size+=15;
    		} else {
    			size =LinearLayout.LayoutParams.WRAP_CONTENT;
    		}
    		if (btn.getId() == R.id.desc_btn_size){
        		if (big_size)
        			btn.setText(R.string.ann_btn_small);
        		else
        			btn.setText(R.string.ann_btn_big);
    		}
    		btn.setHeight(size);
    	}
    	float ts = 17;
    	if (et!=null){
    		if (big_size)
    			ts+=5;
    		et.setTextSize(ts);
    		
    	}
    		
    }
// возвращает язык выводимого desc_kbd.txt    
    public String getLangDescKbd()
    {
    	String out = Locale.getDefault().getLanguage();
    	if (st.lang_desckbd.contains(st.STR_3TIRE)){
    		if (out.contains("ru")
        	  ||out.contains("az")
        	  ||out.contains("hy")
        	  ||out.contains("ba")
        	  ||out.contains("be")
        	  ||out.contains("ka")
        	  ||out.contains("kk")
        	  ||out.contains("ky")
        	  ||out.contains("kv")
        	  ||out.contains("lv")
        	  ||out.contains("lt")
        	  ||out.contains("tg")
        	  ||out.contains("tt")
        	  ||out.contains("uz")
        	  ||out.contains("uk")
        	  ||out.contains("cv")
        	  ||out.contains("et")
    		  )
    			out = "ru";
    		else
    			out = "en";
    	} else
    		out = st.lang_desckbd;
    	return out;
    }
    public void setViewText()
    {
        byte[] buffer = null;
        InputStream is;
        try {
        	String fname = st.desc_file_input;
        	if (st.desc_file_input.contains(st.FILE_INPUT_FOR_VIEW)){
        		fname = st.STR_UNDERSCORING+getLangDescKbd()+st.desc_file_input;
        	}
            is = getAssets().open(fname);
            int size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        String str_data = new String(buffer);
		et.setBackgroundColor(Color.WHITE);
        et.setText(str_data);
        if (searchviewpanel){
        	hideSearchPanel();
        }
   }

}