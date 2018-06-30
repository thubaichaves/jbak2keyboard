package com.jbak2.JbakKeyboard;

import java.util.Vector;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.jbak2.Dialog.Dlg;
import com.jbak2.JbakKeyboard.IKeyboard.Lang;
import com.jbak2.ctrl.GlobDialog;

public class LangSetActivity extends Activity
{
    static LangSetActivity inst;
    LangAdapter m_adapt;
    ListView m_list;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        inst = this;
        CustomKeyboard.loadCustomKeyboards(false);
        m_adapt = new LangAdapter(this, R.layout.lang_list_item);
        for(Lang l:st.arLangs) {
        	Lang lang = l;
       		m_adapt.add(l);
        }
        
        View v = getLayoutInflater().inflate(R.layout.pref_view, null);
        m_list = (ListView)v.findViewById(android.R.id.list);
        m_list.setPadding(2, 10, 2, 0);
        m_list.setAdapter(m_adapt);
// кнопка "старая загрузка словарей" в языках и раскладках
//        View topView = v.findViewById(R.id.top_item);
//        topView.setVisibility(View.VISIBLE);
//        TextView tw = (TextView)topView.findViewById(R.id.text);
//        tw.setText(R.string.set_key_ac_load_vocab);
//        tw.setTextColor(0xff0000ff);
//        tw.setBackgroundResource(android.R.drawable.btn_default);
//        ((TextView)topView.findViewById(R.id.desc)).setText(st.STR_SPACE);
//        topView.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                st.runAct(UpdVocabActivity.class);
//            }
//        });
        setContentView(v);
        Ads.show(this,1);
        super.onCreate(savedInstanceState);
        Intent in = getIntent();
        if (in!=null) {
        	int help = in.getIntExtra(Quick_setting_act.EXXTRA_HELP, 0);
        	if (help == 1)
        		Dlg.helpDialog(inst, inst.getString(R.string.qs_btn3_help));
            	//st.help(R.string.qs_btn3_help, inst);
        }
        
	}
    @Override
    public void onBackPressed() {
    	if (GlobDialog.gbshow){
    		GlobDialog.inst.finish();
    		return;
    	}
     	super.onBackPressed();
    }
    @Override
    protected void onDestroy()
    {
        String langs = m_adapt.getLangString();
        st.pref().edit().putString(st.PREF_KEY_LANGS, langs).commit();
        inst = null;
        super.onDestroy();
    }
    View.OnClickListener m_butListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Lang l = (Lang)v.getTag();
//        	st.show_kbd1 = true;
            try{
                Intent in = new Intent(Intent.ACTION_VIEW)
                .setComponent(new ComponentName(v.getContext(), SetKbdActivity.class))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(st.SET_INTENT_ACTION, st.SET_SELECT_KEYBOARD)
                .putExtra(st.SET_INTENT_LANG_NAME, l.name);
                startActivity(in);
            }
            catch(Throwable e)
            {
            }

            
        }
    };
    class LangAdapter extends ArrayAdapter<IKeyboard.Lang>
    {
        String getLangString()
        {
        	Lang ll = null;
            String ret=st.STR_NULL;
            for(String s:m_arLangs)
            {
            	ll = getLangAtByName(s);
            	if (ll==null)
            		continue;
            	if (ll.isVirtualLang())
            		continue;
                if(ret.length()>0)
                    ret+=st.STR_COMMA;
                ret+=s;
            }
            return ret;
        }
        Lang getLangAtByName(String lang)
        {
            for (Lang ll:st.arLangs){
                if(lang.equals(ll.name))
                    return ll;
            }
            return null;
        }
        int searchLang(String lang)
        {
            int pos = 0;
            for(String lng:m_arLangs)
            {
                if(lang.equals(lng))
                    return pos;
                pos++;
            }
            return -1;
        }
        OnCheckedChangeListener m_chkListener = new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                Lang l = (Lang)buttonView.getTag();
                if(isChecked)
                {
                    int f = searchLang(l.name);
                    if(f<0)
                   		m_arLangs.add(l.name);
                }
                else
                {
                    int f = searchLang(l.name);
                    if(f>-1)
                        m_arLangs.remove(f);
                }
            }
        };
        public LangAdapter(Context context, int textViewResourceId)
        {
            super(context, textViewResourceId);
            rId = textViewResourceId;
            m_arLangs = new Vector<String>();
            for(String l:st.getLangsArray(context))
            {
                boolean canAdd = false;
                if(searchLang(l)==-1)
                {
                	
                    for(Lang lang:st.arLangs)
                    {
                        if(lang.name.equals(l))
                        {
                            canAdd = true;
                            break;
                        }
                    }
                }
                if(canAdd)
                    m_arLangs.add(l);
            }
        }
/** Возвращает язык для позиции pos, так, чтобы виртуальные языки были снизу*/        
        Lang getLangAtPos(int pos)
        {
        	if (pos>= st.arLangs.length)
        		return null;
            int cp = 0;
            int vp = -1;
            for(Lang l:st.arLangs)
            {
                if(l.isVirtualLang())
                {
                    if(vp<0)vp = cp;
                }
                else
                {
                    if(cp==pos)
                        return l; 
                    ++cp;
                }
            }
            return st.arLangs[vp+pos-cp];
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if(convertView==null)
                convertView = getLayoutInflater().inflate(rId, null);
            Lang l = getLangAtPos(position);
        if (l.name.contains("hide") == false) {
            CheckBox cb = (CheckBox)convertView.findViewById(R.id.checkbox);
            cb.setText(l.getName(inst));
            cb.setTag(l);
            //cb.setEnabled(!l.isVirtualLang());
            cb.setEnabled(true);
            if (l.isVirtualLang()){
            	cb.setChecked(true);
            	cb.setEnabled(false);
            }
            boolean bCheck = false;
            for(String s:m_arLangs)
            {
                if(s.equals(l.name))
                {
                	if (l.name.contains("hide") == false) {
                		bCheck=true;
                		break;
                	}
                }
            }
            cb.setChecked(bCheck);
            
            cb.setOnCheckedChangeListener(m_chkListener);
            Button b = (Button)convertView.findViewById(R.id.button);
            if(st.getKeybrdArrayByLang(l.name).size()>1)
            {
                b.setOnClickListener(m_butListener);
                b.setVisibility(View.VISIBLE);
            }
            else
            {
                b.setVisibility(View.GONE);
                //b.getLayoutParams().width = 0;
            }
            b.setTag(l);
        }
            return convertView;
        
        }
        int rId;
        Vector<String> m_arLangs;
    }
}
