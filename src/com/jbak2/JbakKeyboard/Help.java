package com.jbak2.JbakKeyboard;

import com.jbak2.JbakKeyboard.JbKbd.LatinKey;

import android.app.Activity;
import android.view.View;
import android.os.Bundle;
import android.widget.EditText;

public class Help extends Activity
{
	EditText et = null;
	int last_kbd = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
// для вывода любой справки, нужно перед вызовом этой активности задать строку в
// st.help, которая при выходе из активности обнуляется.
// если строка не задана, то вызывается клавиша с кодом -514
        et = (EditText)findViewById(R.id.help_et1);
        et.setText(st.STR_NULL);
        if (st.help.length()>0){
       		et.setText(st.help);
        } else {
        	JbKbd curkbd = st.curKbd();
        	if (curkbd!=null) {
        		LatinKey key = curkbd.getKeyByCode(-514);
        		if (key!=null)
        		et.setText(key.help);
        	}
        }
        st.hidekbd();
        // показ рекламы
       	Ads.count_failed_load = 0;
        Ads.show(this, 5);
	}
    @Override
    public void onPause() {
    	Ads.pause();
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        Ads.resume();
    }

	public void onBackPressed()
	{
		close();
	}
    public void onClickClose(View view) 
    {
    	close();
    }
    public void close() 
    {
		st.help =st.STR_NULL;
		st.type_kbd = last_kbd;
		st.showkbd();
		ServiceJbKbd.setTypeKbd();
		finish();
    }

}
