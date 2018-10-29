package com.jbak2.JbakKeyboard;

import java.io.File;

import com.jbak2.Dialog.Dlg;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
/** Редактирование шаблонов и просмотр элементов буфера обмена*/
public class TplEditorActivity extends Activity
{
	boolean fl_invert_toolbar = false;
	boolean fl_right_toolbar = false;
    File temp_tpl_cur_dir = null;
    int temp_tpl_m_state = -1;
    String n = null;
    String t = null;
	static TplEditorActivity inst = null;
	// хранит, какая клавиатура была нажата 
    public static int last_kbd = -1;
/** Если эта EXTRA есть в стартовом Intent'е - значит нужно показать вхождение буфера обмена,  
 *  а если она равна -2, то показываем переводчик
 * */    
    public static final String EXTRA_CLIPBOARD_ENTRY = "e_clp";
/** Дата элемента буфера обмена*/    
    Long m_clipbrdDate=null;
/** Поле ввода имени */    
    EditText m_edName;
/** Поле ввода текста */
    EditText m_edText;
    // кнопки панели
	HorizontalScrollView hsv = null;
    LinearLayout ll_tb = null;
    Button btn_save = null;
    Button btn_close= null;
    Button btn_del = null;
    Button btn_spec = null;
    Button btn_share = null;
    Button btn_hotkey = null;
    Button btn_cancelhotkey = null;
    // временные переменные    
    String sss=st.STR_NULL;
    String ss1=st.STR_NULL;
    String filename=st.STR_NULL;
    int tmpi = 0;

// флаг что клавиатуру выводить не надо
    boolean fl_hide_kbd = false;
/** Обработчик нажатия кнопок в окне*/    
    View.OnClickListener m_clkListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
        	ServiceJbKbd.inst.fl_text = true;
            switch(v.getId())
            {
            case  R.id.tpl_cb_invert_button:
            	CheckBox cb = (CheckBox)v;
            	fl_invert_toolbar = cb.isChecked();
            	st.pref(inst).edit().putBoolean(st.PREF_TEMPLATE_INVERT_BUTTON_TOOLBAR, fl_invert_toolbar).commit();
            	setButtonToolbarPos();
           		break;
            case  R.id.tpl_cb_right:
            	CheckBox cb1 = (CheckBox)v;
            	fl_right_toolbar = cb1.isChecked();
            	st.pref(inst).edit().putBoolean(st.PREF_TEMPLATE_RIGHT_BUTTON_TOOLBAR, fl_right_toolbar).commit();
            	setButtonToolbarPos();
           		break;
            case  R.id.tpl_share:
            	st.hidekbd();
            	st.sendShareTextIntent(inst,m_edText.getText().toString());
           		break;
            case  R.id.tpl_save: 
            	if (onSave()) 
            		closeEditor();
           		break;
// просто пример итента вызова файлового менеджера для выбора файла           		
//            case  R.id.aaa: 
//            	ServiceJbKbd.inst.forceHide();
//            	String sss = "file:/"+Templates.inst.m_curDir.toString()+"/*";
//            	Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
//        		intent.setType(sss);
//            	intent.addCategory(Intent.CATEGORY_OPENABLE);
//        		startActivity(intent);
//            	break;
                case  R.id.tpl_spec_options: 
                	onSpecOptions(); 
                	break;
                case  R.id.tpl_close:
               		finish();
               		closeEditor();
                    break;
                case  R.id.tpl_hotkey:
                	Templates.inst.setHotDir(m_edName.getEditableText().toString());
                    finish();
                    break;
                case  R.id.tpl_hotkey_cancel: 
                	Templates.inst.setHotDir(st.STR_NULL);
                	//ServiceJbKbd.inst.m_hotkey_dir=st.STR_NULL; 
                    finish();
                    break;
                case R.id.tpl_delete:
                	delete();
                	fl_hide_kbd = true;
                	break;
            }
            if (Templates.rejim == 2) {
        		ServiceJbKbd.inst.m_acPlace = st.acplce1;
            	st.type_kbd = 7;
            	if (fl_hide_kbd == false){
            		ServiceJbKbd.inst.setTypeKbd();
            	}
            }

        }
    };
    protected void onCreate(Bundle savedInstanceState)
    {
    	inst = this;
    	temp_tpl_cur_dir = null;
    	temp_tpl_m_state = -1;
    	ServiceJbKbd.inst.fl_text = false;
    	if (Templates.rejim == 2) {
    		st.acplce1 = ServiceJbKbd.inst.m_acPlace;
    		ServiceJbKbd.inst.m_acPlace = 0;
    		st.type_kbd = 1;
    		ServiceJbKbd.inst.setTypeKbd();
    	}
    		
        if(Templates.inst==null&&!getIntent().hasExtra(EXTRA_CLIPBOARD_ENTRY))
            finish();
        if(android.os.Build.VERSION.SDK_INT >= 17)
        	st.fl_pref_act =true;
        View v = getLayoutInflater().inflate(R.layout.tpl_editor, null);
        ll_tb = (LinearLayout)v.findViewById(R.id.tpl_lltoolbar);
        hsv = (HorizontalScrollView)v.findViewById(R.id.tpl_hsv);
        btn_save = (Button)v.findViewById(R.id.tpl_save);
        btn_save.setOnClickListener(m_clkListener);
        btn_close = (Button)v.findViewById(R.id.tpl_close);
        btn_close.setOnClickListener(m_clkListener);
        btn_del = (Button)v.findViewById(R.id.tpl_delete);
        btn_del.setOnClickListener(m_clkListener);
        btn_spec = (Button)v.findViewById(R.id.tpl_spec_options);
        btn_spec.setOnClickListener(m_clkListener);
        btn_share = (Button)v.findViewById(R.id.tpl_share);
        btn_share.setOnClickListener(m_clkListener);
        btn_share.setVisibility(View.GONE);
        btn_hotkey = (Button)v.findViewById(R.id.tpl_hotkey);
        btn_hotkey.setOnClickListener(m_clkListener);
        btn_cancelhotkey = (Button)v.findViewById(R.id.tpl_hotkey_cancel);
        btn_cancelhotkey.setOnClickListener(m_clkListener);
        btn_hotkey.setVisibility(View.GONE);
        btn_cancelhotkey.setVisibility(View.GONE);
    	fl_invert_toolbar = st.pref(inst).getBoolean(st.PREF_TEMPLATE_INVERT_BUTTON_TOOLBAR, false);
    	CheckBox cb = (CheckBox)v.findViewById(R.id.tpl_cb_invert_button);
        cb.setOnClickListener(m_clkListener);
        if (cb!=null){
        	cb.setChecked(fl_invert_toolbar);
        	cb.setPadding(60,0,0,0);
        }
        fl_right_toolbar = st.pref(inst).getBoolean(st.PREF_TEMPLATE_RIGHT_BUTTON_TOOLBAR, false);
    	cb = (CheckBox)v.findViewById(R.id.tpl_cb_right);
        cb.setOnClickListener(m_clkListener);
        if (cb!=null){
        	cb.setChecked(fl_right_toolbar);
        	cb.setPadding(60,0,0,0);
        }
        setButtonToolbarPos();
//        v.findViewById(R.id.tpl_save).setOnClickListener(m_clkListener);
//        v.findViewById(R.id.tpl_close).setOnClickListener(m_clkListener);
//        v.findViewById(R.id.tpl_delete).setOnClickListener(m_clkListener);
//        v.findViewById(R.id.hotkey).setOnClickListener(m_clkListener);
//        v.findViewById(R.id.hotkey_cancel).setOnClickListener(m_clkListener);
//        v.findViewById(R.id.tpl_share).setOnClickListener(m_clkListener);
//        v.findViewById(R.id.tpl_share).setVisibility(View.GONE);
//        View bSpec =v.findViewById(R.id.tpl_spec_options); 
//        bSpec.setOnClickListener(m_clkListener);
////		TextView hotview=(TextView) v.findViewById(R.id.hotkey);
//		View hotview = v.findViewById(R.id.hotkey);
//		View hotcancel = v.findViewById(R.id.hotkey_cancel);
//		hotview.setVisibility(View.GONE);
//		hotcancel.setVisibility(View.GONE);
        m_edName = (EditText)v.findViewById(R.id.tpl_name);
        m_edName.setOnKeyListener(m_clkKeyListener);
        m_edText = (EditText)v.findViewById(R.id.tpl_text);
        m_edText.setOnKeyListener(m_clkKeyListener);
        m_edText.setMinLines(3);

        Translate.old_record = null;
        int pos = -1;
        pos = getIntent().getIntExtra(EXTRA_CLIPBOARD_ENTRY, -1);
        if(pos > -1)
        {
// показываем буфер обмена
        	setTitle(getString(R.string.mm_multiclipboard));
            Cursor c = st.stor().getClipboardCursor();
            btn_spec.setVisibility(View.GONE);
            btn_share.setVisibility(View.VISIBLE);
            m_edName.setVisibility(View.GONE);
// запрет на редактирование
//            m_edText.setFocusableInTouchMode(false);
            m_edText.setFocusableInTouchMode(true);
//            m_edText.setMinLines(3);
            if(c!=null)
            {
                c.move(0-pos);
                String cp = c.getString(0);
                m_clipbrdDate = new Long(c.getLong(2));
                m_edText.setText(cp);
                c.close();
            }
        }
        else if (pos == -1)
        {
// показываем шаблоны или калькулятор
    		if (Templates.inst!=null){
    			temp_tpl_cur_dir = Templates.inst.m_curDir;
    			temp_tpl_m_state = Templates.inst.m_state;
    		}
        	File ff;
    		if (Templates.inst.m_editFile!=null)
    			ff =Templates.inst.m_editFile;
    		else
    			ff=null;
//        	if (Templates.rejim == 1) {
    		filename="fkouuhg%$#";
    		if (ff!=null)
    			filename=ff.getName().trim();
       		sss = ServiceJbKbd.inst.m_hotkey_dir.trim();
       		ss1 = Templates.inst.m_curDir.toString()+st.STR_SLASH+filename;
       		if (ff!=null){
   				if (ff.isDirectory()){
   					btn_hotkey.setVisibility(View.VISIBLE);
   				}           			
       		}
       		if (sss.length()>0) {
       			if (sss.contains(ss1)&&sss.length()==ss1.length()) {
       				if (ff.isDirectory()){
       					btn_hotkey.setVisibility(View.GONE);
       					btn_cancelhotkey.setVisibility(View.VISIBLE);
       				}
       				} else {
       					if (ff !=null&&ff.isDirectory()){
       						btn_hotkey.setVisibility(View.VISIBLE);
       						btn_cancelhotkey.setVisibility(View.GONE);
       					}
       				}
       			}
        		File f =Templates.inst.m_editFile; 
        		if(Templates.inst.isEditFolder())
        		{
        			setTitle(R.string.tpl_new_folder);
        			m_edName.setHint(R.string.tpl_folder_name);
        			m_edText.getLayoutParams().width=0;
        			m_edText.setVisibility(View.GONE);
        	        v.findViewById(R.id.tpl_unicode_app).setVisibility(View.GONE);
        	        btn_spec.getLayoutParams().width=0;
        		}
        		if(f!=null)
        		{
        			st.tmps = f.getName();
        			if (Templates.rejim == 2) {
        				if (st.tmps.endsWith(".calc"))
        					st.tmps = st.tmps.substring(0,st.tmps.length()-5);
        			}
        			m_edName.setText(st.tmps);
        			if(!f.isDirectory())
        			{
        				String txt = st.STR_NULL;
        				if (Templates.rejim == 1) {
        					txt = Templates.getFileString(f);
        				} else {
        					txt = Templates.inst.getCalcPrgDesc(f);
        				}
    					if(txt!=null) {
    						m_edText.setText(txt);
    					}
        			}
        			btn_del.getLayoutParams().width = -2;
        		}
        		m_edName.setOnFocusChangeListener(new OnFocusChangeListener()
        		{
        			@Override
        			public void onFocusChange(View v, boolean hasFocus)
        			{
        				if(v==m_edName&&hasFocus)
        				{
        					InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        					imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        				}
        			}
        		});
//        	}
// запись программы калькулятора
           	if (Templates.rejim == 2&&Templates.type == 1) {
           		btn_hotkey.setVisibility(View.GONE);
           		btn_cancelhotkey.setVisibility(View.GONE);
               	setTitle(getString(R.string.calc_save_prg));
               	btn_spec.setVisibility(View.GONE);
                m_edText.setHint(R.string.tpl_text_calc_hint);
           	}
// загрузка программы калькулятора
           	else if (Templates.rejim == 2&&Templates.type == 2) {
           		btn_hotkey.setVisibility(View.GONE);
           		btn_cancelhotkey.setVisibility(View.GONE);
            	setTitle(getString(R.string.calc_load_prg));
            	btn_save.setVisibility(View.GONE);
            	btn_spec.setVisibility(View.GONE);
                m_edText.setHint(R.string.tpl_text_calc_hint);
                m_edText.setFocusableInTouchMode(false);
                m_edName.setFocusableInTouchMode(false);
        	}
        }
//показываем переводчик
        else if (pos == -2)
        {
        	btn_hotkey.setVisibility(View.GONE);
        	btn_cancelhotkey.setVisibility(View.GONE);
        	btn_spec.setVisibility(View.GONE);
	        v.findViewById(R.id.tpl_unicode_app).setVisibility(View.GONE);
        	setTitle(getString(R.string.tpl_translate));
        	m_edName.setHint(R.string.tpl_translate_in);
        	m_edName.setMinLines(2);
        	m_edName.setMaxLines(2);
        	m_edText.setHint(R.string.tpl_translate_out);
        	String rec= getIntent().getStringExtra(Translate.RECORD);
        	if (rec!=null){
                Translate.old_record = rec;
        		String r[] = rec.split("-");
        		if (r.length>0)
        			m_edName.setText(r[0]);
        		if (r.length>1)
        			m_edText.setText(r[1]);
        	}

        }
        super.onCreate(savedInstanceState);
        setContentView(v);
        m_edName.requestFocusFromTouch();
    }
    // чтобы после закрытия активности сразу открывалось вызывавшее com_menu,
    // его вызов нужнопрописывать здесь
    @Override
    protected void onDestroy()
    {
    	ServiceJbKbd.inst.fl_text = true;
        if(Templates.inst!=null&&m_clipbrdDate==null)
            Templates.inst.onCloseEditor();
        if(m_clipbrdDate==null&&Translate.inst!=null){
        	Translate.inst.onCloseEditor();
        }
        else if(m_clipbrdDate!=null)
       		com_menu.showClipboard(false);                    
        super.onDestroy();
    }
/** Обработка нажатия на кнопку "Сохранить". */    
    boolean onSave()
    {
        int pos = -1;
        pos = getIntent().getIntExtra(EXTRA_CLIPBOARD_ENTRY, -1);
// запись отредактированной строки в буфер обмена
        if(pos > -1)
        {
        	finish();
        	long ll = m_clipbrdDate.longValue();
//            st.stor().removeClipboardByDate(ll, 0);
            st.stor().saveClipboardString(m_edText.getEditableText().toString(), ll);
            return true;
        }
        // переводчик
        if(pos == -2)
        {
            n = m_edName.getEditableText().toString();
            t = m_edText.getEditableText().toString();
            if (n.isEmpty()||t.isEmpty()){
                st.toast(getString(R.string.tpl_fields_empty));
                return false;
        	}
            if (Translate.inst!=null){
            	finish();
            	Translate.inst.onSave(n+"-"+t, false);
            	Translate.inst.onCloseEditor();
            }
            return true;
        }
        // шаблоны
        if(Templates.inst==null)
            finish();
    	if (Templates.inst!=null&&temp_tpl_cur_dir!=null){
    		Templates.inst.m_curDir = temp_tpl_cur_dir;
    		Templates.inst.m_state = temp_tpl_m_state;
    	}
        
        n = m_edName.getEditableText().toString();
        if(n.length()==0)
        {
            st.toast(getString(R.string.tpl_fields_empty));
            return false;
        }
        if(Templates.inst==null)
            return false;
        n = st.normalizeFileName(n);
        if(Templates.inst.isEditFolder())
        {
            Templates.inst.saveFolder(n);
        } else {
            t = m_edText.getEditableText().toString();
            switch (Templates.rejim)
            {
            case 1: 
                if(n.length()==0|t.length()==0)
                {
                    st.toast(getString(R.string.tpl_fields_empty));
                    return false;
                } else {
                	Templates.inst.saveTemplate(n,t);
                }
            	break;
            case 2: 
                if(n.length()==0) {
                    st.toast(getString(R.string.tpl_fields_empty));
                    return false;
                } else {
//                	n += ".calc";
            		st.type_kbd = 1;
            		ServiceJbKbd.inst.setTypeKbd();
                	Templates.inst.saveTemplate(n,t);
               		st.type_kbd = 7;
               		ServiceJbKbd.inst.setTypeKbd();
                }
            	break;
            }
        }
        finish();
        Templates.inst.onCloseEditor();
        return true;
    }
/** Удаление элемента, открытого в редакторе (шаблона, папки шаблонов, элемента буфера обмена)*/    
    void delete()
    {
        if(m_clipbrdDate!=null)
        {
            st.stor().removeClipboardByDate(m_clipbrdDate.longValue(), 0);
//            if(com_menu.inst!=null){
//            	com_menu.inst.removeLastLongClicked();
//            }
            closeEditor();
            finish();
            return;
        }
        if (Translate.inst!=null){
            n = m_edName.getEditableText().toString();
            t = m_edText.getEditableText().toString();
        	Translate.inst.onDelete(n+"-"+t);
            closeEditor();
            finish();
            return;
        }
        String query=st.STR_NULL;
        if (Templates.inst.m_editFile != null) {
        	query = getString(R.string.tpl_delete,Templates.inst.m_editFile.getName());
        } else {
        	st.toast(st.c().getString(R.string.tpl_empty));
        	return;
        }
//        if (ServiceJbKbd.inst!=null)
//        	ServiceJbKbd.inst.m_candView.hide();
        new Dlg.RunOnYes(this,query){
            @Override
            public void run()
            {
                Templates.inst.onDelete();
                finish();

            }
        };
    }
    @Override
    protected void onUserLeaveHint()
    {
    	st.fl_pref_act =false;;
    	finish();
        //onBackPressed();
    }    
    @Override
    public void onBackPressed() 
    {
    	st.fl_pref_act =false;;
    	ServiceJbKbd.inst.fl_text = true;
    	finish();
        if (Templates.rejim == 2) {
    		ServiceJbKbd.inst.m_acPlace = st.acplce1;
        	st.type_kbd = 7;
        	ServiceJbKbd.inst.setTypeKbd();
        }
//        else if(Translate.inst!=null)
//        	Translate.inst.makeCommonMenu();                    
        else if(m_clipbrdDate!=null)
        	com_menu.inst.showClipboard(false);                    
    };
 /** менюшка "символ для добавления или удаления" для специнструкции
// выводит отдельный список для указанной ниже инструкции */     
    void insertOrDeleteSymbol()
    {
        int rlist = R.layout.tpl_instr_list;
        final ArrayAdapter<String> ar = new ArrayAdapter<String>(this, 
                                                    rlist,
                                                    getResources().getStringArray(R.array.tpl_insertsymbol)
                                                    );
        Dlg.CustomMenu(this, ar, ServiceJbKbd.inst.getString(R.string.insertsymbol_title), new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                int which = ((Integer)param1).intValue();
                if(which>=0)
                {
                    String txt = "="+ar.getItem(which);
                    int f = txt.indexOf(" - ");
                    if(f>0)
                        txt = txt.substring(0,f);
                    boolean movecur = false;
                    if (txt.length()==3)
                   		movecur = true;
                    replaceText(txt, movecur, true);

//                	// показываем клавиатуру
//                	ServiceJbKbd.inst.forceShow();
                	st.toastLong(R.string.insertsymbol_toast);
                }
                return 0;
            }
        });
    }
    void datetimeFormat()
    {
        int rlist = R.layout.tpl_instr_list;
        final ArrayAdapter<String> ar1 = new ArrayAdapter<String>(this, 
                                                    rlist,
                                                    getResources().getStringArray(R.array.datetime_format)
                                                    );
        Dlg.CustomMenu(this, ar1, ServiceJbKbd.inst.getString(R.string.datetime_format_title), new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                int which = ((Integer)param1).intValue();
                if(which>=0)
                {
                    String txt = ar1.getItem(which);
                    int f = txt.indexOf('-');
                    if(f>0)
                        txt = txt.substring(0,f).trim();
//                    int s = m_edText.getSelectionStart();
//                    int e = m_edText.getSelectionEnd();
//                    m_edText.getText().replace(s<e?s:e,e>s?e:s, txt);
                    
//                    int pos = 0;
//                    if (txt.length()>2)
//                    	pos = txt.length()*-1;
//                    else
//                    	pos = -1;
                    boolean movecur = false;
                    if (txt.length()==2)
                   		movecur = true;
                    replaceText(txt, movecur, true);
//                	// показываем клавиатуру
//                	ServiceJbKbd.inst.forceShow();
                }
                return 0;
            }
        });
    }
    /**  @param txt - строка для замены
     * @param movecur - помещать ли курсор после "["
     * @param show_kbd - показ клавиатуры*/
    void replaceText(String txt, boolean movecur, boolean show_kbd)
    {
        int s = m_edText.getSelectionStart();
        int e = m_edText.getSelectionEnd();
        m_edText.getText().replace(s<e?s:e,e>s?e:s, txt);
        // перемещаем курсор
        if (movecur) {
            int cp = m_edText.getSelectionStart();
            int ind = txt.indexOf("[");
            if (ind>-1) {
            	int ps = m_edText.getSelectionStart();
            	ps = ind-txt.length()+1;
            	cp = cp+ps;
            	m_edText.setSelection(cp);
            }
        	
        }
        m_edText.requestFocus();
    	// показываем клавиатуру
        if (show_kbd)
        	ServiceJbKbd.inst.forceShow();
    }
/** Меню специальных инструкций для добавления в шаблон */    
    void onSpecOptions()
    {
    	ServiceJbKbd.inst.forceHide();
        int rlist = R.layout.tpl_instr_list;
        final ArrayAdapter<String> ar = new ArrayAdapter<String>(this, 
                                                    rlist,
                                                    getResources().getStringArray(R.array.tpl_spec_instructions)
                                                    );
        Dlg.CustomMenu(this, ar, ServiceJbKbd.inst.getString(R.string.tpl_spec_options), new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                int which = ((Integer)param1).intValue();
                if(which>=0)
                {
                    String txt = ar.getItem(which);
                    int f = txt.indexOf(' ');
                    if(f>0)
                        txt = txt.substring(0,f);
                    int s = m_edText.getSelectionStart();
                    int e = m_edText.getSelectionEnd();
                    m_edText.getText().replace(s<e?s:e,e>s?e:s, txt);

// символы для разделения
					if (which == 4 )
						datetimeFormat();
					else if (which == 7 )
						insertOrDeleteSymbol();
					else if (which == 8 )
						insertOrDeleteSymbol();
                    else
                    	// показываем клавиатуру
                    	ServiceJbKbd.inst.forceShow();
                }
                return 0;
            }
        });
        
    }
// закрывает эдитор и  восстанавливает что было на экране
    void closeEditor()
    {
        if(m_clipbrdDate==null&&Templates.inst!=null){
            Templates.inst.onCloseEditor();
        }
        else if(m_clipbrdDate==null&&Translate.inst!=null){
        	Translate.inst.onCloseEditor();
//    		Translate.close();
//    		new Translate().makeCommonMenu();
        }
        else if(m_clipbrdDate!=null)
       		com_menu.showClipboard(false);                    
    }
    public void onClick_keycode(View view) throws NameNotFoundException {
    	ServiceJbKbd.inst.forceHide();
    	st.runApp(this, st.UNICODE_APP);
    }
    View.OnKeyListener m_clkKeyListener = new View.OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) 
		{
    	    if(event.getAction() == KeyEvent.ACTION_DOWN && 
    	    	(keyCode == KeyEvent.KEYCODE_ENTER))
       		{
    	    	if (m_clipbrdDate==null){
        	    	switch (v.getId())
        	    	{
        	    	case R.id.tpl_name:
        	    		if (m_edText!=null&&m_edText.getVisibility()==View.VISIBLE)
        	    			m_edText.requestFocus();
        	    		return true;
// с текста переход не делать, иначе в тексте шаблона нельзя ввести ентер        	    		
//        	    	case R.id.tpl_text:
//        	    		if (m_edName!=null&&m_edName.getVisibility()==View.VISIBLE)
//        	    			m_edName.requestFocus();
//        	    		return true;
        	    	}
    	    	}
       		}
       		return false;
		}
	};
    public void setButtonToolbarPos()
    {
    	if (ll_tb==null)
    		return;
    	ll_tb.removeAllViews();
    	if (!fl_invert_toolbar){
    		ll_tb.addView(btn_save);
    		ll_tb.addView(btn_close);
    		ll_tb.addView(btn_del);
    		ll_tb.addView(btn_spec);
    		ll_tb.addView(btn_share);
    		ll_tb.addView(btn_hotkey);
    		ll_tb.addView(btn_cancelhotkey);
    	} else {
    		ll_tb.addView(btn_cancelhotkey);
    		ll_tb.addView(btn_hotkey);
    		ll_tb.addView(btn_share);
    		ll_tb.addView(btn_spec);
    		ll_tb.addView(btn_del);
    		ll_tb.addView(btn_close);
    		ll_tb.addView(btn_save);
    	}
    	if (hsv!=null){
    		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
    				RelativeLayout.LayoutParams.WRAP_CONTENT,
    				RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.BELOW, R.id.tpl_cb_right);

        	if (!fl_right_toolbar){
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        	} else {
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        	}
        	hsv.setLayoutParams(lp);
    	}
    }

}
