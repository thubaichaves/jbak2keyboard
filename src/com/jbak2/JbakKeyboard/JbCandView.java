package com.jbak2.JbakKeyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.CompletionInfo;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jbak2.JbakKeyboard.EditSetActivity.EditSet;
import com.jbak2.JbakKeyboard.JbKbd.LatinKey;
import com.jbak2.JbakKeyboard.st.ArrayFuncAddSymbolsGest;
import com.jbak2.ctrl.GlobDialog;
import com.jbak2.perm.Perm;
import com.jbak2.words.TextTools;
import com.jbak2.words.WordsService;
import com.jbak2.words.IWords.WordEntry;


public class JbCandView extends RelativeLayout
{
	ArrayList<ArrayFuncAddSymbolsGest> arFuncKey = new ArrayList<ArrayFuncAddSymbolsGest>();
	static Context m_c;
	boolean fl_ac_show = false;
	// флап что коррекция уже была
	boolean fl_correction = false;
	// переменная для хнанения слова по клику
	// на кнопке в аквтодопе.
	String word_on_click = st.STR_NULL;
	
	int RIGHT_MARGIN_LIST_BUTTON = 3;
	String[] ar_mtext = null;;
	HorizontalScrollView hsv = null;
// временная переменная 
	int itmp = 0;
	int calc_adr = -1;

//калькулятор
	// строка для истории вычислений
	String calc_history = st.STR_NULL;
	// отображение клавиши для истории вычислений
	String calc_history_key = st.STR_NULL;
	// флаг что следующей нужно нажать цифру до скольки знаков нужно округлять
	boolean calc_flag_round = false;
	// дробная часть числа
	String calc_round = st.STR_NULL;
	// клавиша в/о нажата первый раз
	boolean calc_flag_vo_first = false;
	// адрес для команд перехода на адрес (БП и тд)
	int calc_adr_bp = 0;
	// сколько еще нажатий цифр для задания адреса для команд перехода на адрес (БП и тд)
	int calc_key_adr_bp = -1;
	// флаг, что дальше идет нажатие для цифр адреса
	boolean calc_fl_bp_adr = false;
	// флаг, что это первый запуск программы
	boolean calc_fl_sp = false;
	// flag, что программа выполняется
	boolean calc_start_prg = false;
	// режим (автоматический/программирования
	boolean calc_regim = false;
	// флаг сохранялось ли значение индикатора
	boolean calc_ind_save = false;
	// храним значение индикатора для автоматич. режима
	String calc_ind_save_str = st.STR_NULL;
	// флаг ПХ
	boolean calc_flag_px;
	// флаг ИПХ
	boolean calc_flag_ipx;
	// индикатор калькулятора
    TextView m_calcind;
	// меню калькулятора
    TextView m_calcmenu;
    // принудительное чтение слов из словарей
    TextView m_forcibly;
    // счетчик нажатий символов
    TextView m_counter;
    TextView m_addVocab;
	// ключ, включена ли дробная часть
    boolean calc_drob = false;
    // регистр Х
    double calcRegX = 0;
    // регистр Y
    double calcRegY = 0;
    // регистр Z
    double calcRegZ = 0;
    // регистр T
    double calcRegT = 0;

    double calc_mem = 0;
    // временная переменная (используется для внутренних целей)
    double calc_temp = 0;
    // предыдущая команда
    int calc_old = 0;
    // команда "Б'стрелка вверх'"
    boolean calc_b_arrow = false;
    boolean calc_zero = false;
 // флаг что параметры сохранены
    boolean calc_par = false;
    // флаг что выводимое значение вычисленное 
    boolean calc_rez = false;
    // флаг что вводится первый символ 
    boolean calc_first_input = true;
// параметры для сохранения автодополнения
    int m_addVocab1;
    int m_counter1;
    int m_keycode1;
    int m_rightView1;

// временные переменные
    String tmp = st.STR_NULL;
// код последней нажатой клавиши
    TextView m_keycode;

    String calc_full = st.STR_NULL;
    String calc_cel = st.STR_NULL;
    String calc_drob_s = st.STR_NULL;
    String calc_tmp = st.STR_NULL;
    String calc_tmp1 = st.STR_NULL;
    String calc_tmp2 = st.STR_NULL;
    String calc_tmp3 = st.STR_NULL;
    String calc_zero_count = st.STR_NULL;
    String calc_ind = st.STR_NULL;

    public static final int AC_PLACE_NONE = 0;
    public static final int AC_PLACE_KEYBOARD = 1;
    public static final int AC_PLACE_TITLE = 2;
    public static final int AC_PLACE_CURSOR_POS = 3;
    public int m_place = AC_PLACE_NONE;
//    String m_texts[] = DEF_WORDS;
    String m_texts[];
    String m_defkey[];
    LayoutInflater m_inflater;
//    ImageView m_rightView;
    TextView m_rightView;
    LinearLayout.LayoutParams m_lp;
    boolean m_bCanCorrect = false;
    boolean m_bBlockClickOnce = false;
    CompletionInfo m_completions[];
    EditSet m_es;
    int m_defaultFontSize=0;
    int m_minWidth = 0;
    int m_maxWidth=0;
    int m_lines=1;
	public static ArrayFuncAddSymbolsGest arFunkKey = new ArrayFuncAddSymbolsGest();
//    public static final String[] DEF_WORDS = new String[]
//    public static final String[] DEF_WORD = new String[]
//    {
//    	st.STR_COMMA,
//        ".",
//        "!",
//        "?",
//        ":",
//        ";",
//        "@",
//        "\"",
//        "-",
//        ":)",
//        ":(",
//    };
    
    public int getPlace()
    {
        return m_place;
    }
    public void setPlace(int place)
    {
        this.m_place = place;
    }
    public JbCandView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    int m_height;
    WindowManager wm;
    LinearLayout m_ll;
    
    @SuppressLint("NewApi")
	void init(Context context)
    {
    	m_c = context;

    	if (ServiceJbKbd.inst.m_ac_defkey==null)
    		ServiceJbKbd.inst.onCreate();
        m_ll = (LinearLayout)findViewById(R.id.completions);
   		m_defkey=ServiceJbKbd.inst.m_ac_defkey.split(st.STR_SPACE);
   		m_keycode = ((TextView)findViewById(R.id.cand_keycode)); 
    	m_counter = ((TextView)findViewById(R.id.cand_counter));
    	m_forcibly = ((TextView)findViewById(R.id.cand_forcibly));
    	m_calcind = ((TextView)findViewById(R.id.cand_calcind)); 
    	m_calcmenu = ((TextView)findViewById(R.id.cand_calcmenu));
        
    	m_height = context.getResources().getDimensionPixelSize(R.dimen.cand_height);
        m_defaultFontSize = context.getResources().getDimensionPixelSize(R.dimen.candidate_font_height);
        m_inflater = (LayoutInflater)context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        this.setOnLongClickListener(m_LongClickListenerLayout);

        wm = (WindowManager) getContext().getSystemService(Service.WINDOW_SERVICE);
// параметры для addfullviewpart()        
        m_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        m_lp.topMargin = st.ac_height;
//        m_lp.bottomMargin=2+st.ac_height;
//        m_lp.topMargin = st.ac_height;
        m_lp.rightMargin=RIGHT_MARGIN_LIST_BUTTON;
        Point sz = new Point();
        if(android.os.Build.VERSION.SDK_INT >=13)
        {
        	wm.getDefaultDisplay().getSize(sz);
        }
        else
        {
        	sz.x = wm.getDefaultDisplay().getWidth();
        	sz.y = wm.getDefaultDisplay().getHeight();
        }
        m_maxWidth = Math.min(sz.x,sz.y)-10;
        if(st.pref(context).contains(st.PREF_KEY_FONT_PANEL_AUTOCOMPLETE))
        {
            m_es = new EditSet();
            m_es.load(st.PREF_KEY_FONT_PANEL_AUTOCOMPLETE);
            calcEditSet();
        }
        if (st.calc_reg == null) {
        	for (int i=0;i<100;i++){
        		st.calc_reg[i] =0;
        	}
        }
        setTexts(null);
    }
// установка цвета фона окна автодопа
	public void setACBackground()
    {
        if (m_ll!=null){
        	m_ll.setBackgroundColor(st.ac_col_main_back);
        	//m_ll.setOnLongClickListener(m_LongClickListenerLayout);
        }
        if (hsv!=null){
        	hsv.setBackgroundColor(st.ac_col_main_back);
        	//hsv.setOnLongClickListener(m_LongClickListenerLayout);
        }
    	//this.setBackgroundColor(st.ac_col_main_back);
// мой код - делает градиент, задел на будущее
// не забыть посмотреть в плане        
//        if (st.ac_col_main_back !=0){
//            if (m_ll!=null)
//            	m_ll.setBackgroundColor(st.ac_col_main_back);
//        } else {
//        	if (m_ll!=null&&JbKbdView.inst!=null)
//        		m_ll.setBackground(JbKbdView.inst.getBackground());
//        }

    }
	@SuppressLint("NewApi")
	@Override
    protected void onFinishInflate() 
    {
        m_ll = (LinearLayout)findViewById(R.id.completions);
        //m_ll.setPadding(0, st.ac_height, 0, st.ac_height);
        hsv = (HorizontalScrollView)findViewById(R.id.cand_hsv);
        setACBackground();
        m_addVocab = (TextView)findViewById(R.id.cand_left);
   		m_addVocab = keyColor(m_addVocab,7);
        if(m_es!=null)
            m_es.setToEditor(m_addVocab);
        m_addVocab.setVisibility(View.GONE);
        m_addVocab.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        m_addVocab.setPadding(0, 0, 5, 0);
        m_addVocab.setOnClickListener(m_ClickListenerBtn);
        m_addVocab.setOnLongClickListener(m_LongClickListenerBtn);
//        m_addVocab.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//           		st.freq_dict = 500000;
//                ServiceJbKbd.inst.saveUserWord(((TextView)v).getText().toString());
//                createWord(false);
//            }
//        });
        m_counter = (TextView)findViewById(R.id.cand_counter);
        m_counter = keyColor(m_counter,1);
        m_counter.setMinWidth(50);
        if (ServiceJbKbd.inst != null)
        	ServiceJbKbd.inst.setCountTextValue();
        m_counter.setOnClickListener(m_ClickListenerBtn);
//        m_counter.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                ServiceJbKbd.inst.setWord(((TextView)v).getText().toString().trim(),false);
//                ServiceJbKbd.inst.setCountTextValue();
//            }
//        });
        m_calcind = (TextView)findViewById(R.id.cand_calcind);
        m_calcind = keyColor(m_calcind,3);
        m_calcind.setOnClickListener(m_ClickListenerBtn);
//        m_calcind.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                    ServiceJbKbd.inst.setWord(((TextView)v).getText().toString(),false);
//                    ServiceJbKbd.inst.setCountTextValue();
//                    createWord(false);
//            }
//        });
        m_calcmenu = (TextView)findViewById(R.id.cand_calcmenu);
        m_calcmenu = keyColor(m_calcmenu,4);
        m_calcmenu.setOnClickListener(m_ClickListenerBtn);
//        m_calcmenu.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                    ServiceJbKbd.inst.onCalcMenu();
//            }
//        });
        m_keycode = (TextView)findViewById(R.id.cand_keycode);
        m_keycode.setMinWidth(50);
        m_keycode = keyColor(m_keycode,2);
        m_keycode.setOnClickListener(m_ClickListenerBtn);
        m_keycode.setOnLongClickListener(m_LongClickListenerBtn);
//        m_keycode.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                    ServiceJbKbd.inst.setWord(((TextView)v).getText().toString().trim(),false);
//                    m_addVocab.setVisibility(View.GONE);
//            }
//        });
        m_forcibly = (TextView)findViewById(R.id.cand_forcibly);
        m_forcibly.setMinWidth(50);
    	m_forcibly = keyColor(m_forcibly, 5);
    	m_forcibly.setOnClickListener(m_ClickListenerBtn);
//        m_forcibly.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
////            	if (st.type_keyboard.compareToIgnoreCase("calculator")==0)
////            		return;
//            	st.fl_suggest_dict = true;
//            	createWord(true);
//            	m_forcibly.setVisibility(View.GONE);
//            }
//        });
        m_rightView = (TextView)findViewById(R.id.cand_right);
        m_rightView = keyColor(m_rightView, 8);
        m_rightView.setPadding(10, 0, 10, 0);
        m_rightView.setText("∇");
        m_rightView.setOnClickListener(m_ClickListenerBtn);
        m_rightView.setOnLongClickListener(m_LongClickListenerBtn);
    }
    public void ViewCandList()
    {
    	if (st.fl_ac_list_view) {
    		st.fl_ac_list_view = false;
    	} else{
    		st.fl_ac_list_view = true;
    	}
        if(m_bBlockClickOnce)
        {
            m_bBlockClickOnce = true;
            return;
        }
        if(m_popupWnd!=null)
        {
            hideFullView();
        }
        else
        {
            showFullView();
        }
    }

    public void setCompletions(CompletionInfo[] completions)
    {
        m_bCanCorrect = false;
        m_addVocab.setVisibility(View.GONE);
//        if (st.fl_suggest_dict)
//        	completions = null;
        if (completions == null)
        {
            setTexts(null);
            return;
        }
        m_completions = completions;
        String texts[] = new String[completions.length];
        int pos = 0;
        for (CompletionInfo ci : completions)
        {
            if(ci==null)
                texts[pos] = st.STR_NULL;
            if(ci.getText()!=null)
                texts[pos] = ci.getText().toString();
            else if(ci.getLabel()!=null)
                texts[pos] = ci.getLabel().toString();
            else
                texts[pos] = st.STR_NULL;
            pos++;
        }
        if (!st.fl_suggest_dict)
        	setTexts(texts,completions);
    }
	boolean we_is_none = false;
	
    public void setTexts(Vector<WordEntry> ar)
    {

    	m_completions = null;
        m_bCanCorrect = false;
        if(ar==null||ar.size()==0)
        {
            if(m_addVocab!=null)
                m_addVocab.setVisibility(View.GONE);
            setTexts(ServiceJbKbd.inst.m_ac_defkey.split(st.STR_SPACE), null);
            return;
        }
        WordEntry wd;
        	wd = ar.get(0);
        int sz = ar.size();
        if(wd.compareType==TextTools.COMPARE_TYPE_NONE&&st.calc_fl_ind == false)
        {
       		m_addVocab.setText(wd.word);
       		m_addVocab.setVisibility(View.VISIBLE);
        	--sz;
        }
        else
        {
            m_addVocab.setVisibility(View.GONE);
        }
        String words[] = new String[sz];
        m_bCanCorrect = sz>0;
        int pos = 0;
        for(WordEntry we:ar)
        {
            if(we.compareType==TextTools.COMPARE_TYPE_NONE)
                continue;
            String s = st.STR_NULL;
            if (ServiceJbKbd.inst.m_ac_space&&!ServiceJbKbd.inst.m_acAutocorrect)
            	s=st.STR_SPACE;
            else if (ServiceJbKbd.inst.m_ac_space&&ServiceJbKbd.inst.m_acAutocorrect)
            	s=st.STR_NULL;
            words[pos]=we.word+s;
            pos++;
        }
        setTexts(words,null);
        if (we_is_none)
        	ar.remove(0);
    }
    public void setTexts(String words[],CompletionInfo[]completions)
    {
        hideFullView();
        if (ServiceJbKbd.inst.m_ac_defkey == null)
       		ServiceJbKbd.inst.onCreate();
        m_defkey=ServiceJbKbd.inst.m_ac_defkey.split(st.STR_SPACE);
        m_texts = words==null?m_defkey:words;
        if(m_ll==null) {
//        	ServiceJbKbd.inst.createNewCandView();
        	return;
        }
        if (hsv!=null)
        	hsv.scrollTo(0,0);
        int pos = 0;
        int cc = m_ll.getChildCount();
        for(String ssss:m_texts)
        {
            if(ssss==null) 
            	break;
            TextView tv = null;
            if(pos<cc)
            {
                tv = (TextView)m_ll.getChildAt(pos);
                if (st.calc_fl_ind == false) {
               		tv.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                tv = createTextView(false);
                if (tv!=null) {
               		m_ll.addView(tv);
                }
            }
         
            if(m_es!=null)
                m_es.setToEditor(tv);
            setTexAndFuncKey(tv,ssss, pos);
            tv.setTag(completions!=null?completions[pos]:null);
            pos++;
        }
        while(pos<cc)
        {
            ((TextView)m_ll.getChildAt(pos)).setVisibility(View.GONE);
            pos++;
        }
        m_ll.measure(0, 0);
        m_addVocab.measure(0, 0);
// ширина экрана без кнопок image
        int w = m_ll.getMeasuredWidth();
// значение  длины строки автодополнения
//        -m_keycode.getWidth()-m_counter.getWidth()
        if(w<getWidth()-m_addVocab.getMeasuredWidth())
        {
            m_rightView.setVisibility(View.GONE);
        }
        else
        {
        	if (st.ac_place_arrow_down == false)
        		if (getWidth()>0)
        			m_rightView.setVisibility(View.VISIBLE);
        }
    }

// тап на служебной кнопке из автодопа    
    View.OnClickListener m_ClickListenerBtn = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
            case R.id.cand_left:
           		st.freq_dict = 500000;
                ServiceJbKbd.inst.saveUserWord(((TextView)v).getText().toString());
                createWord(false);
                return;
            case R.id.cand_counter:
                ServiceJbKbd.inst.setWord(((TextView)v).getText().toString().trim(),false);
                ServiceJbKbd.inst.setCountTextValue();
                return;
            case R.id.cand_calcind:
                ServiceJbKbd.inst.setWord(((TextView)v).getText().toString(),false);
                ServiceJbKbd.inst.setCountTextValue();
                createWord(false);
                return;
            case R.id.cand_calcmenu:
                ServiceJbKbd.inst.onCalcMenu();
                return;
            case R.id.cand_keycode:
                ServiceJbKbd.inst.setWord(((TextView)v).getText().toString().trim(),false);
                m_addVocab.setVisibility(View.GONE);
                return;
            case R.id.cand_forcibly:
            	st.fl_suggest_dict = true;
            	createWord(true);
            	m_forcibly.setVisibility(View.GONE);
                return;
            case R.id.cand_right:
            	ViewCandList();
            	return;
            }
        }
    };
 // долгий тап на служебной кнопке из автодопа    
    View.OnLongClickListener m_LongClickListenerLayout = new View.OnLongClickListener() 
    {
        @Override
        public boolean onLongClick(View v)
        {
			st.runAct(AcColorAct.class);

        	return true;
        }
    };
    View.OnLongClickListener m_LongClickListenerBtn = new View.OnLongClickListener() 
    {
        @Override
        public boolean onLongClick(View v)
        {
            switch (v.getId())
            {
            case R.id.cand_keycode:
            	com_menu.showNotationNumber(getContext(),((TextView)v).getText().toString().trim());
				break;
            case R.id.cand_left:
//            	st.toast("В разработке");
				st.kbdCommand(st.CMD_EDIT_USER_VOCAB);
				break;
            case R.id.cand_right:
				st.hidekbd();
				st.runAct(AcColorAct.class);
				break;
            }
        	return true;
        }
    };
 // долгий тап на слове из автодопа    
    View.OnLongClickListener m_longClickListenerText = new View.OnLongClickListener() 
    {
        @Override
        public boolean onLongClick(View v)
        {
//        	ViewCandList();
        	WordsService.command(WordsService.CMD_DELETE_VOCAB, ((TextView)v).getText().toString().trim(), ServiceJbKbd.inst);
        	return true;
        }
    };
    View.OnClickListener m_ClickListenerText = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if(ServiceJbKbd.inst!=null)
            {
            	int id = v.getId();
            	if (id>-1){
					ArrayFuncAddSymbolsGest el = st.getElementSpecFormatSymbol(arFuncKey, id);
					if (el!=null){
						if (ServiceJbKbd.inst!=null)
							ServiceJbKbd.inst.processKey(el.code);
        			}
					return;
            	}
            	st.fl_ac_word = true;
            	tmp =((TextView)v).getText().toString();
            	if (tmp.length()==1)
            		ServiceJbKbd.inst.setDelSymb(tmp.charAt(0));

                CompletionInfo ci = (CompletionInfo)v.getTag();
// нажатие на слово из автодополнения
                if(ci==null) {
                	word_on_click = ((TextView)v).getText().toString();
                	if (st.fl_ac_separator_symbol) {
                		if (word_on_click.length()==1
                			&&!Character.isLetterOrDigit(word_on_click.charAt(0))
                			) {
                			ServiceJbKbd.inst.onKey(word_on_click.charAt(0), new int[] {});
                			return;
                		}
                	}
                		
//                      ServiceJbKbd.inst.setWord(((TextView)v).getText().toString(),false);
                    ServiceJbKbd.inst.setWord(word_on_click,false);
//увеличение  частоты использования слова в пользовательском словаре если включен интеллектуальный ввод
                    if (st.student_dict) {
                   		st.freq_dict = 1;
                      	WordsService.command(WordsService.CMD_SAVE_WORD, word_on_click.trim(), ServiceJbKbd.inst);
//                      	WordsService.command(WordsService.CMD_SAVE_WORD, ((TextView)v).getText().toString().trim(), ServiceJbKbd.inst);
                    }
                }
                else
                    ServiceJbKbd.inst.setCompletionInfo(ci);
                ServiceJbKbd.inst.processCaseAndCandidates();        
            }
        }
    };
    OnTouchListener m_touchListener = new OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
        	switch (event.getAction())
        	{
        	case MotionEvent.ACTION_DOWN:
        		v.setBackgroundColor(Color.GRAY);
        		break;
        	case MotionEvent.ACTION_CANCEL:
        	case MotionEvent.ACTION_UP:
        		v.setBackgroundColor(st.ac_col_word_back);
        		break;
        	}
            return false;
        }
    };

    PopupWindow m_popupWnd = null;
    void hideFullView()
    {
    	if (ar_mtext!=null&&ar_mtext.length>0){
    		
    		System.arraycopy(ar_mtext, 0, m_texts, 0, ar_mtext.length-1);
    		ar_mtext = null;
    	}
        if(m_popupWnd!=null)
        {
//            m_rightView.setImageResource(R.drawable.cand_arrow_icon);
        	m_rightView.setText("∇");
            m_popupWnd.dismiss();
            m_popupWnd = null;
        }
    }
    @SuppressLint("NewApi")
	void showFullView()
    {
    	if (st.fl_ac_list_view==false)
    		return;
    	if (st.fl_alphabetically){
    		ar_mtext = null;
    		if (m_texts!=null&&m_texts.length>0){
    			ar_mtext = new String[m_texts.length-1];
        		System.arraycopy(m_texts, 0, ar_mtext, 0, m_texts.length-1);
        	    Arrays.sort(m_texts);
    		}
    	    
    	}
//      m_rightView.setImageResource(R.drawable.cand_arrow_up_icon);
        m_rightView.setText("∆");
        final View v = m_inflater.inflate(R.layout.candidates_full, null);
        final LinearLayout ll = (LinearLayout)v.findViewById(R.id.cand_view);
        ll.setPadding(0, 0, 0, 5);

		int width = getWidth();
        addFullViewPart(ll, width, 0);
        int h = st.kv().getHeight();
        if(m_place==AC_PLACE_KEYBOARD){
            h-=m_height;
        }
        m_popupWnd = new PopupWindow(v, getWidth(), h);
//        m_popupWnd.setBackgroundDrawable(new BitmapDrawable());
//        m_popupWnd.
        m_popupWnd.setBackgroundDrawable(new BitmapDrawable());
        m_popupWnd.setTouchable(true);
        m_popupWnd.setSplitTouchEnabled(false);
        m_popupWnd.setOutsideTouchable(true);
        m_popupWnd.setTouchInterceptor(new OnTouchListener()
        {
            
            @Override
            public boolean onTouch(View view, MotionEvent event)
            {
                int act = event.getAction();
                boolean bHide = false;
                if(act==MotionEvent.ACTION_OUTSIDE)
                {
//                    Rect r = new Rect(),rf = new Rect();
//                    m_rightView.getGlobalVisibleRect(r);
//                    m_rightView.getWindowVisibleDisplayFrame(rf);
//                    int x = (int)event.getRawX(),y = (int)event.getRawY();
//                    y-=rf.top;
//                    if(r.contains(x,y))
//                        m_bBlockClickOnce = true;

                	if(m_rightView.dispatchTouchEvent(event)){
                        hideFullView();
                		st.fl_ac_list_view = false;
                        return false;
                	}
                    bHide = true;
                }
                else if(act==MotionEvent.ACTION_DOWN)
                {
                    int r = ll.getRight();
                    int b = ll.getBottom();
                    float x = event.getX();
                    float y = event.getY();
                    if(x>r||y>b)
                        bHide = true;
                }
                if(bHide)
                    hideFullView();
                return bHide;
            }
        });
        int yoff = 0-st.kv().getCurKeyboard().getHeight();
        if(m_place==AC_PLACE_KEYBOARD){
            yoff+=m_height;
            if (st.fl_alphabetically){
                h+=m_height;
            }
        }
        m_popupWnd.showAsDropDown(st.kv(), 0, yoff);
//        m_popupWnd.showAtLocation(st.kv(), Gravity.LEFT|Gravity.TOP, 0, m_height);
//        ServiceJbKbd.inst.setInputView(v);
//        m_bShownFull = true;
    }
    void addFullViewPart(LinearLayout parent,int width,int pos)
    {
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOnLongClickListener(m_LongClickListenerLayout);
        ll.setClipChildren(false);
        ll.setGravity(Gravity.LEFT);
        ll.setPadding(0, 2, 0, 2);
        
        parent.addView(ll);
        int w = 0;
        while (pos<m_texts.length)
        {
            String txt = m_texts[pos];
            if(txt==null)
                return;
            TextView tv = createTextView(true);
            setTexAndFuncKey(tv, txt, pos);
            tv.measure(0, 0);
            w+=tv.getMeasuredWidth()+RIGHT_MARGIN_LIST_BUTTON;
            if(w>width)
            {
                addFullViewPart(parent, width, pos);
                return;
            }
            if(m_completions!=null&&m_completions.length>pos)
                tv.setTag(m_completions[pos]);
//            tv.setOnClickListener(m_ClickListenerText);
//            tv.setOnLongClickListener(m_longClickListenerText);
            ll.setBackgroundColor(st.ac_col_main_back);
            ll.addView(tv,m_lp);
            pos++;
        }
        st.fl_ac_list_view = true;
    }
    
    public void remove()
    {
        hideFullView();
        try{
            if(m_place==AC_PLACE_KEYBOARD&&st.kv()!=null)
            {
                CustomKeyboard kbd = (CustomKeyboard)st.kv().getCurKeyboard();
                kbd.setTopSpace(0);
            }
            m_place = AC_PLACE_NONE;
            wm.removeViewImmediate(this);
        }
        catch (Throwable e) {
        	st.log("bbb");
        	st.logEx(e);
        }
        fl_ac_show = false;
    }
// юзается    
    public void hide()
    {
        hideFullView();
        try{
            m_place = AC_PLACE_NONE;
            wm.removeView(this);
        }
        catch (Throwable e) {
        }
    }
    int getFixedHeight()
    {
        return m_height;
    }
    int getYCursor()
    {
        if(ServiceJbKbd.inst!=null&&ServiceJbKbd.inst.m_cursorRect!=null)
        {
            
            int ret = ServiceJbKbd.inst.m_cursorRect.top-m_height;
//            if(ServiceJbKbd.inst.isFullscreenMode())
//                ret-=ServiceJbKbd.inst.m_extraText.getHeight();
            return ret;
        }
        return 0;
    }
    public void show(JbKbdView kv,int place)
    {
        if(place==AC_PLACE_CURSOR_POS&&place==m_place&&getYCursor()==m_yPos)
            return;
//        if(m_place!=AC_PLACE_NONE)
//        	remove();
        if(!ServiceJbKbd.inst.isInputViewShown())
            return;
        // я добавил, было выше
       if(fl_ac_show){
    	   remove();
       }
       fl_ac_show = true;
        CustomKeyboard kbd = (CustomKeyboard)kv.getCurKeyboard();
        m_place = place;
        kbd.setTopSpace(place==AC_PLACE_KEYBOARD?m_height:0);
        int ypos = place==AC_PLACE_KEYBOARD?getContext().getResources().getDisplayMetrics().heightPixels-kbd.getHeight():0;
        if(place==AC_PLACE_CURSOR_POS)
        {
            ypos = getYCursor();
        }
        showInView(ypos,place==AC_PLACE_TITLE);
    }
    int m_yPos = -10000;
    // если поставить lp.type = TYPE_SYSTEM_ERROR 
    // и в токене lp.type=TYPE_APPLICATION_SUB_PANEL
    // а на теле сперва поставить автодоп с статус баре и потом 
    // переключить на вверху кейборды, то автодоп становится
    // не поверх всех окон! 
    //Но смещён на полстроки кнопок ниже. 
    public void showInView(int yPos,boolean bSystemAlert)
    {
        m_yPos = yPos;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

//        |WindowManager.LayoutParams.FLAG_FULLSCREEN
        
        lp.height = m_height;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        			|WindowManager.LayoutParams.FLAG_FULLSCREEN
                    |WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    |WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    ;
        lp.gravity = Gravity.LEFT|Gravity.TOP;
        // тип окна не менять!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
		} else {
			lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		}
        

// автодоп в статус баре    	
//        if (ServiceJbKbd.inst.m_acPlace == AC_PLACE_TITLE)
//        	lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        	

      IBinder tok = st.kv().getWindowToken();
      if (tok == null)
    	  tok = new Binder();
      if(tok!=null&&!bSystemAlert)
      {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
  			lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
  		} else {
  			lp.type = WindowManager.LayoutParams.TYPE_PHONE;
  		}
        	
    	  lp.token = tok;
        }
        lp.x = 0;
        lp.y = yPos;
        
        if (!Perm.checkPermission(m_c)) {
        	st.toastLong(R.string.perm_not_all_perm);
        	return;
        }
        try {
           	wm.addView(this, lp);
		} catch (Throwable e) {
		}

    }
// задание параметров кнопок НЕ СЛОВ!
// для слов подсказок отдельно в createtextview    
    void calcEditSet()
    {
        if(m_es.fontSize<=m_defaultFontSize)
        {
// 2
        	m_lines = 2;
        }
        else
        {
            m_lines = 1;
        }
        TextView tv = createTextView(false);
        tv.setText("Tgd");
        tv.setPadding(0, st.ac_height, 0, st.ac_height);
        tv.measure(0, 0);
        m_minWidth = tv.getMeasuredHeight();
    	m_height = tv.getMeasuredHeight();//(m_es.fontSize+m_es.fontSize/2)*m_lines+dp2*2;
    	m_height +=st.ac_height*2;
// чтобы задать высоту и ширину слов, нужно в разметке указать match_parent
// а потом уже здесь:    	
//    	tv.setWidth(100);
//    	tv.setHeight(100);
    }
    public TextView createTextView(boolean fullView)
    {
    	LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
    		    ViewGroup.LayoutParams.WRAP_CONTENT, 
    		    ViewGroup.LayoutParams.WRAP_CONTENT
    		);
// отступ кнопки в лайоте
    	param.setMargins(2, 0, 1, 0);
    	TextView tv = new TextView(getContext());
        tv.setLayoutParams(param);
        
// отступ текста от краёв кнопки
        tv.setPadding(5, 0, 5, 0);
        
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(st.ac_col_word_back);
        tv.setTextColor(st.ac_col_word_text);
        tv.setMaxWidth(m_maxWidth);
        tv.setHeight(m_height+(st.ac_height*2));
        //tv.setHeight((m_height));
        if (m_minWidth == 0)
            tv.setMinWidth(50);
//            tv.setMinWidth(m_defaultFontSize);
        else
        	tv.setMinWidth(m_minWidth);
    	tv.setIncludeFontPadding(false);
        if(!fullView)
        	tv.setMaxLines(m_lines);
        tv.setOnTouchListener(m_touchListener);
        tv.setOnClickListener(m_ClickListenerText);
        tv.setOnLongClickListener(m_longClickListenerText);
        if(m_es!=null)
            m_es.setToEditor(tv);
        return tv;
    }
    public boolean applyCorrection(int code)
    {
        if(!m_bCanCorrect||m_ll.getChildCount()<1||WordsService.isSelectNow())
        	return false;
//        if(!m_bCanCorrect&&fl_correction)
//            return false;
//        if(m_ll.getChildCount()<1)
//            return false;
//        if(WordsService.isSelectNow())
//            return false;
//        fl_correction = true;
        TextView tv = (TextView)m_ll.getChildAt(0);
        String text = st.STR_NULL;
        if (code == 0)
            text = tv.getText().toString();
        else
            text = tv.getText().toString()+(char)code;
        ServiceJbKbd.inst.setWord(text,true);
        return true;
    }
    static EditSet getDefaultEditSet(Context c)
    {
        EditSet es = new EditSet();
        es.fontSize = (int)st.floatDp(10, c);
        return es;
    }
    public void setCounter(String s)
    {

//   		m_counter = keyColor(m_counter, 1);
   		m_counter.setText(s);
    }
    public void setCalcInd(int text, int code)
    {
    	if (st.fl_suggest_dict == false)
    		st.fl_suggest_dict = true;
    	m_forcibly.setVisibility(View.GONE);
    	
// определяет какой режим использовать (прг или авто), или нажата клавиша с/п
    	if (text==31) {
    		calc_regim = false;
    		if (calc_ind_save)
    			m_calcind.setText(calc_ind_save_str);
    		calc_ind_save = false;
    	}
    	if (text==32) {
// первоначальная очистка маасива команд калькулятора    		
    		if (st.calc_iniprg==false){
        		st.calcClear();
    			st.calc_iniprg=true;
    		}
    		if (calc_ind_save == false) {
    			calc_ind_save_str = m_calcind.getText().toString();
    			calc_ind_save=true;
    		}
    		calc_regim = true;
    	}
    	if (st.calc_iniprg==false) {
    		st.calcClear();
    		st.calc_iniprg=true;
    	}
		if (m_counter != null)
			m_counter.setVisibility(View.GONE);
		if (m_forcibly != null)
			m_forcibly.setVisibility(View.GONE);
  		if (calc_regim == false) {
   			m_calcmenu.setVisibility(View.VISIBLE);
   			setCalcIndAuto( text, code);
   		} else {
//   			m_calcmenu.setVisibility(View.GONE);
   			setCalcIndPrg( text, code);
   			return;
   		}
  	// выполняет программу
    	if (calc_start_prg==true) {
    		while (st.calc_prog[st.calc_prog_step] > -1&&st.calc_prog_step<1000&&calc_start_prg == true) {
//    			if (calc_flag_round) {
//    				setCalcIndAuto(st.calc_prog[st.calc_prog_step], code);
//    			}
    			setCalcIndAuto(st.calc_prog[st.calc_prog_step], code);
				st.calc_prog_step++;
//    			if (calc_flag_vo_first) {
//    				calc_flag_vo_first = false;
//    				st.calc_prog_step--;
//    			}
    			// задержка, чтобы не грелся проц    		
    			st.sleep(10);
    		}
//    		if (st.calc_prog[st.calc_prog_step] == -1)
    			calc_start_prg = false;
    	} 
    }
    public void setCalcIndPrg(int text, int code)
    {
// режим прг
    	if (text==22&&st.calc_prog_step<1000){
    		st.calc_prog_step++;
    	}
    	if (text==23&&st.calc_prog_step>0){
    		st.calc_prog_step--;
    	}
    	if (st.calc_prog_step>999&&st.calc_prog_step<0)
    		st.calc_prog_step=0;
// вывод индикатора
    	itmp = st.calc_prog_step;
    	if (st.calc_prog_step <= 999&&st.calc_prog_step >= 0){
    		if (text!=32&&text!=22&&text!=23) {
    			st.calc_prog[st.calc_prog_step] = text;
    			st.calc_prog_step++;
    		}
    	}
    	calc_tmp2 = st.STR_NULL;
    	calc_tmp3 = st.STR_NULL;
       	if (itmp <= 999&&itmp >= 0){
    		calc_tmp2=st.STR_SPACE+threeSymb(itmp);
    	}
       	calc_tmp3=calc_tmp2;
    	calc_tmp2 = st.STR_NULL;
       	if (itmp-1 <= 999&&itmp-1 >= 0){
    		calc_tmp2=st.STR_SPACE+threeSymb(itmp-1);
    	}
       	calc_tmp3=calc_tmp3+calc_tmp2;
    	calc_tmp2 = st.STR_NULL;
       	if (itmp-2 <= 999&&itmp-2 >= 0){
    		calc_tmp2=st.STR_SPACE+threeSymb(itmp-2);
    	}
       	calc_tmp3=calc_tmp3+calc_tmp2;
   		calc_tmp = String.valueOf(st.calc_prog_step);
    	while (calc_tmp.length()<4){
    		calc_tmp=st.STR_ZERO+calc_tmp;
    	}
    	calc_tmp="."+calc_tmp.trim();
    	while (calc_tmp3.length()<12) {
    		calc_tmp3=calc_tmp3+" ---";
    	}
    	m_calcind.setText(st.lenstr(calc_tmp3, 12, st.STR_SPACE, true)+calc_tmp);
    }
 // возвращает строку из 3 символов, для отображения в индикаторе кода в режиме программирования
    public String threeSymb(int step)
    {
    	calc_tmp1 = st.STR_NULL;
    	if (st.calc_prog[step] == -1)
    		calc_tmp1 = "---";
    	else
    		calc_tmp1 = String.valueOf(st.calc_prog[step]);
    	calc_tmp1=st.lenstr(calc_tmp1,3,st.STR_ZERO,true);
    		
    	return calc_tmp1;
    }
    public boolean setCalcAdress(int adr_col)
    {
// проверяет адрес в прг и если адрес верный, сохраняет его в calc_adr
		if (st.calc_prog_step+adr_col > 999){
			st.toast("error step (step "+st.calc_prog_step+").");
			calc_start_prg = false;
			return false;
		}
    	for (int iii=1; iii<=adr_col;iii++){
    		if (st.calc_prog[st.calc_prog_step+iii]>10&&st.calc_prog[st.calc_prog_step+iii]<0) {
    			st.toast("error. Invalid address format.(step "+st.calc_prog_step+").");
    		}
    	}
    	calc_adr=0;
    	for (int iii=0; iii<adr_col;iii++){
    		if (calc_adr > 0)
    			calc_adr*=10;
    		calc_adr += st.calc_prog[st.calc_prog_step+iii];
    	}
//		calc_adr += st.calc_prog[st.calc_prog_step+adr_col-1];
		st.calc_prog_step+=adr_col;
    	return true;
    }
        public void setCalcIndAuto(int text, int code)
        {
    // все команды калькулятора доступны в IkbdSettings.java и ServiceKbd.java
    // режим автомат
    	saveAc_place();
		if (text==0)
	    	calc_zero = true; 

        m_counter.setVisibility(View.GONE);
        m_keycode.setVisibility(View.GONE);
		m_rightView.setVisibility(View.GONE);
		m_forcibly.setVisibility(View.GONE);
		m_addVocab.setVisibility(GONE);
		int cc = m_ll.getChildCount();
		int[]tv_visible;
		tv_visible = new int[cc];
		TextView[] tv;
		tv = new TextView[cc];
		for (int i=0;i<cc;i++) {
			tv[i] = (TextView)m_ll.getChildAt(i);
			tv_visible[i] = tv[i].getVisibility();
			tv[i].setVisibility(View.GONE);
    	}
//		m_calcind = ((TextView)findViewById(R.id.cand_calcind)); 
		m_calcind = keyColor(m_calcind, 3);
    	m_calcind.setVisibility(View.VISIBLE);
		m_calcmenu = keyColor(m_calcmenu, 4);
    	m_calcmenu.setVisibility(View.VISIBLE);
    	calc_ind= m_calcind.getText().toString();
    	if (text == -10){
            if (st.calc_fl_ind==false&&ServiceJbKbd.inst.m_acPlace==0) {
                CustomKeyboard kbd = (CustomKeyboard)st.kv().getCurKeyboard();
            	int ypos = getContext().getResources().getDisplayMetrics().heightPixels-ServiceJbKbd.inst.calc_corr_ind-kbd.getHeight();
                showInView(ypos,true);
            }
            st.calc_fl_ind=true;
    		return;
    	}
		calc_history_key = st.getCalcCommandText(text);
// обработка px
    	if (calc_flag_px) {
			calc_flag_px=false;
   		// программа выполняется
    		if (calc_start_prg == true) {
    			if (setCalcAdress(2)) {
        			LatinKey key = st.curKbd().getKeyByRegistry(calc_adr);
        			if (key == null) {
        				st.toast("Error. Not registry: "+st.calc_prog_step);
        				calc_start_prg=false;
        			} else {
        				st.calc_reg[key.calc_reg_fl]=Double.valueOf(m_calcind.getText().toString());
                    	calc_flag_ipx=false;
                		calc_rez=true;
                		calc_b_arrow = true;
        			}
    			}
    		} else {
    	   		// режим авт
                LatinKey key = st.curKbd().getKeyByCode(code);
                if (key.calc_reg_fl>-1) {
            		calcRegY=calcRegX;
            		calcRegZ=calcRegY;
            		calcRegT=calcRegZ;
            		st.calc_reg[key.calc_reg_fl] = Double.valueOf(calc_ind);
                	calcRegX = st.calc_reg[key.calc_reg_fl];
            		calc_rez=true;
            		calc_b_arrow = true;
            		text=-1;
                } else {
                	st.toast("Error. Not registry. Step "+st.calc_prog_step);
                	return;
                }
        	}
			calc_flag_px=false;
			calc_flag_ipx=false;
			text = -1;
   		}
    	if (text == 28) {
    		if (calc_flag_px) {
    			calc_flag_px=false;
    			calc_flag_ipx=false;
    		} else {
    			calc_flag_px=true;
    			calc_flag_ipx=false;
    			calc_drob = false;
    		}
    	}
// обработка ipx
    	if (calc_flag_ipx) {
			calc_flag_ipx = false;
   		// программа выполняется
    		if (calc_start_prg == true) {
    			if (setCalcAdress(2)) {
        			LatinKey key = st.curKbd().getKeyByRegistry(calc_adr);
        			if (key == null) {
        				st.toast("Error. Not registry. Step "+st.calc_prog_step);
        				calc_start_prg=false;
        			} else {
                		calcRegT=calcRegZ;
                		calcRegZ=calcRegY;
                		calcRegY=calcRegX;
        				calcRegX=st.calc_reg[key.calc_reg_fl];
        				calc_ind = String.valueOf(calcRegX);
                    	calc_flag_ipx=false;
                		calc_rez=true;
                		calc_b_arrow = true;
        			}
    			} else {
    				calc_start_prg = false;
    				return;
    			}
    		} else {
    	   		// режим авт
                LatinKey key = st.curKbd().getKeyByCode(code);
                if (key.calc_reg_fl>-1) {
            		calcRegT=calcRegZ;
            		calcRegZ=calcRegY;
            		calcRegY=calcRegX;
                	calcRegX = st.calc_reg[key.calc_reg_fl];
                	calc_ind = String.valueOf(calcRegX);
            		calc_rez=true;
            		calc_b_arrow = true;
            		text=-1;
                } else {
                	st.toast("Error. Not registry. Step "+st.calc_prog_step);
                	return;
                }
        	}
			calc_flag_px=false;
			calc_flag_ipx=false;
			text = -1;
   		}
    	if (text == 29) {
    		if (calc_flag_ipx) {
    			calc_flag_ipx=false;
    			calc_flag_px=false;
    		} else {
    			calc_flag_ipx=true;
    			calc_flag_px=false;
    			calc_drob = false;
    		}
    	}

    	if (text>-1&text<10) {
			// команда round
 			if (calc_flag_round) {
 				if (text >=0&&text<=9) {
 					if (calc_ind.contains(".")) {
 						
 						calc_round = calc_ind.substring(calc_ind.indexOf(".")+1);
 						double bbb = Double.valueOf(calc_ind);
 						double ttt = 1;
 						for (int ii=0;ii<text;ii++) {
 							ttt *= 10;
 						}
						bbb = bbb*ttt;
 						bbb= Math.round(bbb);
 						bbb = bbb / ttt;
 						calcRegX = bbb;
 						calc_ind = String.valueOf(calcRegX);
 						calc_flag_round = false;
 					}
 				}
			}
 			else if (calc_fl_bp_adr == false) {
    			setCalcInd1(text);
    			if (calc_rez) {
    				calcRegY=calcRegX;
    				calcRegZ=calcRegY;
    				calcRegT=calcRegZ;
    				calcRegX=0;
    				calc_rez=false;
    			}
			calcRegX=Double.valueOf(calc_ind);
    		} else{
    			// команда БП (установка адреса)
     			if (calc_fl_bp_adr) {
     				if (calc_key_adr_bp > 0) {
     					if (text < 9&& text>=0) {
     						calc_adr_bp=(calc_adr_bp*10)+text;
     						calc_key_adr_bp -= 1;
     						if (calc_key_adr_bp == 0) {
     	     					calc_key_adr_bp = -1;
     	     					st.calc_prog_step = calc_adr_bp;
     	     					calc_adr_bp = 0;
     	     					calc_fl_bp_adr = false;
     						}
     						return;
     					}
     				} else {
     					calc_key_adr_bp = -1;
     					st.calc_prog_step = calc_adr_bp;
     					calc_adr_bp = 0;
     					calc_fl_bp_adr = false;
     				}
 				}

    		}
		}
    	if (text ==10) {
    		calcRegX = Double.valueOf(calc_ind);
    		calcRegX=calcRegY*calcRegX;
    		setCalcRegRotation();
    		calc_ind=String.valueOf(calcRegX);
    		calc_rez=true;
    		calc_b_arrow = true;
    	}
    	if (text ==11) {
    		calcRegX = Double.valueOf(calc_ind);
    		calcRegX=calcRegY/calcRegX;
    		setCalcRegRotation();
    		calc_ind=String.valueOf(calcRegX);
    		calc_rez=true;
    		calc_b_arrow = true;
    	}
    	if (text ==12) {
    		calcRegX = Double.valueOf(calc_ind);
    		calcRegX=calcRegY+calcRegX;
    		setCalcRegRotation();
    		calc_ind=String.valueOf(calcRegX);
    		calc_rez=true;
    		calc_b_arrow = true;
    	}
    	if (text ==13) {
    		calcRegX = Double.valueOf(calc_ind);
    		calcRegX=calcRegY-calcRegX;
    		setCalcRegRotation();
    		calc_ind=String.valueOf(calcRegX);
    		calc_rez=true;
    		calc_b_arrow = true;
    	}
    	if (text ==14) {
            LatinKey key = st.curKbd().getKeyByCode(-556);
            if(key!=null&&key.calc_keyboard == true) {
            	st.type_kbd=7;
            }
    		st.save_calc_history(calc_history+" (="+calc_ind+")");
			calc_history = st.STR_NULL; 
    		calc_ind=st.STR_ZERO;
    		calcRegX=0;
    		calc_zero_count =st.STR_NULL;
    		calc_drob = false;
    		calc_zero = false;
    		calc_first_input=true;
            if (st.calc_fl_ind==false&&ServiceJbKbd.inst.m_acPlace==0) {
                CustomKeyboard kbd = (CustomKeyboard)st.kv().getCurKeyboard();
            	int ypos = getContext().getResources().getDisplayMetrics().heightPixels-ServiceJbKbd.inst.calc_corr_ind-kbd.getHeight();
                showInView(ypos,true);
            }
        	st.calc_fl_ind = true;
    	}
    	if (text ==15) {
    		calcRegX = Double.valueOf(calc_ind);
    		calcRegT=calcRegZ;
    		calcRegZ=calcRegY;
    		calcRegY=calcRegX;
    		calc_temp= calcRegX;
    		calcRegX=0;
    		calc_drob = false;
    		calc_first_input=true;
    	}
    	if (text ==16) {
     		calcRegX = Double.valueOf(calc_ind);
    		double d=calcRegY;
    		calcRegY=calcRegX;
    		calcRegX=d;
    		calc_tmp=String.valueOf(calcRegX);
    		calc_tmp1 = calc_tmp.substring(0, calc_tmp.indexOf("."));
    		calc_tmp2 = calc_tmp.substring(calc_tmp.indexOf(".")+1);
    		calc_tmp=".";
    		if (calc_tmp2.length()==1&calc_tmp2.contains(st.STR_ZERO)) {
        			calc_tmp=st.STR_NULL;
        			calc_tmp2=st.STR_NULL;
    		}
    		calc_ind=calc_tmp1+calc_tmp+calc_tmp2;

    	}
    	if (text ==17) {
     		calcRegX = Double.valueOf(calc_ind);
    		calcRegX=calcRegX*(-1);
    		calc_tmp=String.valueOf(calcRegX);
    		calc_tmp1 = calc_tmp.substring(0, calc_tmp.indexOf("."));
    		calc_tmp2 = calc_tmp.substring(calc_tmp.indexOf(".")+1);
    		calc_tmp=st.STR_NULL;
    		if (calc_tmp2.length()==1&calc_tmp2.contains(st.STR_ZERO)) {
    			calc_tmp2=st.STR_NULL;
    			calc_tmp=st.STR_NULL;
    		}
    		calc_ind=calc_tmp1+calc_tmp+calc_tmp2;
    	}
    	if (text ==18) {
    		calc_drob = true;
    		calc_zero_count =st.STR_NULL;
    	}
// пустая операция (клавиша "on"
    	if (text ==19) {
    	}
     	if (text ==20) {
    		calcRegX = Double.valueOf(calc_ind);
    		calcRegX*=calcRegX;
    		setCalcRegRotation();
    		calc_ind=String.valueOf(calcRegX);
    		calc_rez=true;
    		calc_b_arrow = true;

     	}
     	if (text ==21) {
     		calcRegX = Double.valueOf(calc_ind);
    		calcRegX =Math.sqrt(calcRegX);
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     // команда "шаг вперед"    
     	if (text ==22) {
     		calc_ind+=st.STR_ZERO;
     	}
     // команда "шаг назад"
     	if (text ==23) {
     		calc_ind=calc_ind.substring(0,calc_ind.length()-1);
     		calcRegX = Double.valueOf(calc_ind);
     	}
     	if (text ==24) {
     		calcRegX = Double.valueOf(calc_ind);
    		calcRegX =(calcRegY/100)*calcRegX;
//     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text ==25) {
     		calc_mem = Double.valueOf(calc_ind);
     	}
     	if (text ==26) {
     		calc_ind=String.valueOf(calc_mem);
     		calcRegX = Double.valueOf(calc_ind);
     		calc_rez=false;
     	}
     	if (text ==27) {
     		calc_mem=0;
     	}
//     	if (text ==28) {
//     		if (calc_flag_px)
//     			calc_flag_px=false;
//     		else 
//     			calc_flag_px=true;
// 			calc_flag_ipx=false;
//     	}
//     	if (text ==29) {
//     		if (calc_flag_ipx)
//     			calc_flag_ipx=false;
//     		else 
//     			calc_flag_ipx=true;
// 			calc_flag_px=false;
//     	}
     	if (text ==30) {
     		if (calc_start_prg) {
     			if (st.calc_pp[0]!=0){
     				stepPpDown();
     			} else {
         			st.calc_prog_step=-1;
     			}
     		} else {
     			st.calc_prog_step=0;
     			calc_flag_vo_first =true;
     		}
     	}
     	if (text ==33) {
            GlobDialog gd = new GlobDialog(st.c());
            gd.set(R.string.clear_question, R.string.yes, R.string.no);
            gd.setObserver(new st.UniObserver()
            {
                @Override
                public int OnObserver(Object param1, Object param2)
                {
                    if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                    {
            			for (int i = 0; i <= 999;i++) {
            				st.calc_prog[i]=-1;
            			}
            			for (int i=0;i<st.calc_pp.length;i++){
            				st.calc_pp[i]=0;
            			}
            			st.calc_prog_step=0;
            			st.calc_iniprg=false;
                    }
                    return 0;
                }
            });
            gd.showAlert();
     	}
     	if (text ==34) {
     		if (calc_start_prg) {
     			calc_start_prg = false;
//				st.calc_prog_step++;

     		} else {
     			calc_start_prg = true;
     			if (st.calc_prog_step > -1&&st.calc_prog_step<=999) {
     				if (st.calc_prog[st.calc_prog_step] == 34) {
     					st.calc_prog_step++;
     				}
     			}
     		}
     	}
     	if (text ==35) {
     		// режим прг
     		if (calc_start_prg) {
     			st.calc_prog_step++;
    			if (setCalcAdress(3)) {
    				calc_adr--;
    				if (calc_adr<0)
    					calc_adr=0;
    				if (calc_adr>999)
    					calc_adr=999;
    				st.calc_prog_step=calc_adr;
    				
    			}
     		} else {
     			// режим авто
     			if (calc_fl_bp_adr==false&&calc_key_adr_bp==-1) {
 					calc_fl_bp_adr = true;
 					calc_key_adr_bp=3;
     			}
     		}
     	}
     	if (text == 42) {
     		calcRegX = Double.valueOf(calc_ind);
    		calcRegX =Math.PI;
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text == 43) {
     		calcRegX = Double.valueOf(calc_ind);
    		calcRegX =Math.sin(calcRegX);
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
     		calc_b_arrow = true;
     	}
     	if (text == 44) {
     		calcRegX = Double.valueOf(calc_ind);
    		calcRegX =Math.cos(calcRegX);
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text == 45) {
     		calcRegX = Double.valueOf(calc_ind);
    		calcRegX =Math.tan(calcRegX);
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text == 46) {
     		calcRegX = Double.valueOf(calc_ind);
    		calcRegX =Math.asin(calcRegX);
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text == 47) {
     		calcRegX = Double.valueOf(calc_ind);
    		calcRegX =Math.acos(calcRegX);
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text == 48) {
     		calcRegX = Double.valueOf(calc_ind);
    		calcRegX =Math.atan(calcRegX);
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text == 49) {
     		calcRegX = Double.valueOf(calc_ind);
    		calcRegX = 1/calcRegX;
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text == 50) {
     		calcRegX = Double.valueOf(calc_ind);
    		calcRegX = Math.pow(calcRegY,calcRegX);
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text == 51) {
			calcRegX =Math.log(Double.valueOf(calc_ind));
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text == 52) {
    		calcRegX = Math.random();
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text == 53) {
     		if (calc_ind.contains(".")) {
     			calc_ind = calc_ind.substring(0,calc_ind.indexOf("."));
     			calcRegX = Double.valueOf(calc_ind);
     		}
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text == 54) {
			calcRegX =Math.log10(Double.valueOf(calc_ind));
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text == 55) {
			calcRegX =Math.toRadians(Double.valueOf(calc_ind));
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text == 56) {
			calcRegX =Math.toDegrees(Double.valueOf(calc_ind));
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     // обработка round
    	if (text == 57) {
     		if (calc_start_prg) {
//     			if (calc_flag_round)
//     				calc_flag_round= false;
//     			else
//     				calc_flag_round = true;
     			calc_flag_round = true;
     			calc_rez=true;
     			calc_b_arrow = true;
     			return;
     		} else {
     			calc_flag_round = true;
     			calc_rez=true;
     			calc_b_arrow = true;
     		}
     	}
     	if (text == 58) {
			calcRegX =Math.E;
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text == 59) {
			calcRegX =Math.exp(Double.valueOf(calc_ind));
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text == 60) {
     		double tempd =1;
			calcRegX =(Double.valueOf(calc_ind));
			for (int i=1;i<=calcRegX;i++) {
				if (tempd<=1*10E154)
					tempd = tempd*i;
				else {
					st.toast("Errog factorial. Big number");
					return;
				}
			}
			calcRegX = tempd;
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text == 61) {
     		// режим прг
     		if (calc_start_prg) {
     			int bbb = st.calc_prog_step+3;
     			st.calc_prog_step++;
    			if (setCalcAdress(3)) {
    				if (calc_adr<0)
    					calc_adr=0;
    				if (calc_adr>999)
    					calc_adr=999;
    				stepPpUp(bbb);
    				st.calc_prog_step=calc_adr-1;
    				
    			}
     		} 
     	}
     	if (text == 62) {
    		// x>=0
     		if (calc_start_prg) {
    			calcRegX =(Double.valueOf(calc_ind));
    			if (calcRegX>=0) {
    				st.calc_prog_step+=3;
    			} else {
    				int bbb = st.calc_prog_step+3;
    				st.calc_prog_step++;
    				if (setCalcAdress(3)) {
    					if (calc_adr<0)
    						calc_adr=0;
    					if (calc_adr>999)
    						calc_adr=999;
    					st.calc_prog_step=calc_adr-1;
    				}
    			}
     		} 
     	}
     	if (text == 63) {
    		// x<0
     		if (calc_start_prg) {
    			calcRegX =(Double.valueOf(calc_ind));
    			if (calcRegX<0) {
    				st.calc_prog_step+=3;
    			} else {
    				int bbb = st.calc_prog_step+3;
    				st.calc_prog_step++;
    				if (setCalcAdress(3)) {
    					if (calc_adr<0)
    						calc_adr=0;
    					if (calc_adr>999)
    						calc_adr=999;
    					st.calc_prog_step=calc_adr-1;
    				}
    			}
     		} 
     	}
     	if (text == 64) {
    		// x=0
     		if (calc_start_prg) {
    			calcRegX =(Double.valueOf(calc_ind));
    			if (calcRegX==0) {
    				st.calc_prog_step+=3;
    			} else {
    				int bbb = st.calc_prog_step+3;
    				st.calc_prog_step++;
    				if (setCalcAdress(3)) {
    					if (calc_adr<0)
    						calc_adr=0;
    					if (calc_adr>999)
    						calc_adr=999;
    					st.calc_prog_step=calc_adr-1;
    				}
    			}
     		} 
     	}
     	if (text == 65) {
    		// x!=0
     		if (calc_start_prg) {
    			calcRegX =(Double.valueOf(calc_ind));
    			if (calcRegX!=0) {
    				st.calc_prog_step+=3;
    			} else {
    				int bbb = st.calc_prog_step+3;
    				st.calc_prog_step++;
    				if (setCalcAdress(3)) {
    					if (calc_adr<0)
    						calc_adr=0;
    					if (calc_adr>999)
    						calc_adr=999;
    					st.calc_prog_step=calc_adr-1;
    				}
    			}
     		} 
     	}
     	if (text == 66) {
    		// x>=y
     		if (calc_start_prg) {
    			calcRegX =(Double.valueOf(calc_ind));
    			if (calcRegX>=calcRegY) {
    				st.calc_prog_step+=3;
    			} else {
    				int bbb = st.calc_prog_step+3;
    				st.calc_prog_step++;
    				if (setCalcAdress(3)) {
    					if (calc_adr<0)
    						calc_adr=0;
    					if (calc_adr>999)
    						calc_adr=999;
    					st.calc_prog_step=calc_adr-1;
    				}
    			}
     		} 
     	}
     	if (text == 67) {
    		// x<y
     		if (calc_start_prg) {
    			calcRegX =(Double.valueOf(calc_ind));
    			if (calcRegX < calcRegY) {
    				st.calc_prog_step+=3;
    			} else {
    				int bbb = st.calc_prog_step+3;
    				st.calc_prog_step++;
    				if (setCalcAdress(3)) {
    					if (calc_adr<0)
    						calc_adr=0;
    					if (calc_adr>999)
    						calc_adr=999;
    					st.calc_prog_step=calc_adr-1;
    				}
    			}
     		} 
     	}
     	if (text == 68) {
    		// x=y
     		if (calc_start_prg) {
    			calcRegX =(Double.valueOf(calc_ind));
    			if (calcRegX == calcRegY) {
    				st.calc_prog_step+=3;
    			} else {
    				int bbb = st.calc_prog_step+3;
    				st.calc_prog_step++;
    				if (setCalcAdress(3)) {
    					if (calc_adr<0)
    						calc_adr=0;
    					if (calc_adr>999)
    						calc_adr=999;
    					st.calc_prog_step=calc_adr-1;
    				}
    			}
     		} 
     	}
     	if (text == 69) {
    		// x!=y
     		if (calc_start_prg) {
    			calcRegX =(Double.valueOf(calc_ind));
    			if (calcRegX != calcRegY) {
    				st.calc_prog_step+=3;
    			} else {
    				st.calc_prog_step++;
    				if (setCalcAdress(3)) {
    					if (calc_adr<0)
    						calc_adr=0;
    					if (calc_adr>999)
    						calc_adr=999;
    					st.calc_prog_step=calc_adr-1;
    				}
    			}
     		} 
     	}
     	if (text == 70) {
    		// t to x
     		double bbb = calcRegX;
     		calcRegX = calcRegY;
     		calcRegY = calcRegZ;
     		calcRegZ = calcRegT;
     		calcRegT = bbb;
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
     	if (text == 71) {
     		calcRegX = Double.valueOf(calc_ind);
    		calcRegX = Math.pow(10,calcRegX);
     		setCalcRegRotation();
     		calc_ind=String.valueOf(calcRegX);
     		calc_rez=true;
		   calc_b_arrow = true;
     	}
// вывод индикатора
     	if (text != 14)
     		calc_history += calc_history_key.trim();
   		calc_full=Double.toString(calcRegX);
   		if (calc_rez) {
   			if (calc_ind.length()!=1&&calc_ind.contains(".")) {
   				calc_tmp2=calc_ind;
   				calc_tmp1 = calc_tmp2.substring(0, calc_tmp2.indexOf("."));
   				calc_tmp = calc_tmp2.substring(calc_tmp2.indexOf(".")+1);
   				if (calc_tmp.length()==1&calc_tmp.contains(st.STR_ZERO)) {
   					calc_tmp=st.STR_NULL;
   					calc_ind = calc_tmp1;
   				}
   			}
   		}
   		
        m_calcind.setText(calc_ind);
        calc_old = text;
//		calc_zap = false;
		calc_zero=false;
    }
        public void stepPpUp(int step)
        {
        	int item = st.calc_pp.length;
        	for (int i=st.calc_pp.length-1;i>0;i--){
        		st.calc_pp[i]=st.calc_pp[i-1];
        	}
        	st.calc_pp[0]= step;
        }
        public void stepPpDown()
        {
        	st.calc_prog_step = st.calc_pp[0];
        	for (int i=0;i<st.calc_pp.length-1;i++)
        	{
        		st.calc_pp[i]=st.calc_pp[i+1];
        	}
        	st.calc_pp[st.calc_pp.length-1] = 0;
        }
    public void setCalcRegRotation()
    {
       	calcRegY=calcRegZ;
       	calcRegZ=calcRegT;
       	calcRegT=0;
    }
    public void saveAc_place()
    {
    	if (calc_par==false) {
            m_counter1 = m_counter.getVisibility();
            m_keycode1 = m_keycode.getVisibility();
            m_addVocab1 = m_addVocab.getVisibility();
            m_forcibly.setVisibility(View.GONE);
            m_rightView1 =m_rightView.getVisibility();
    		int cc = m_ll.getChildCount();
    		int[]tv_visible;
    		tv_visible = new int[cc];
    		TextView[] tv;
    		tv = new TextView[cc];
    		for (int i=0;i<cc;i++) {
    			tv[i] = (TextView)m_ll.getChildAt(i);
    			tv_visible[i] = tv[i].getVisibility();
        	}
    		m_calcind = ((TextView)findViewById(R.id.cand_calcind)); 
    		m_calcind = keyColor(m_calcind, 3);
        	m_calcind.setVisibility(View.VISIBLE);
    		m_calcmenu = ((TextView)findViewById(R.id.cand_calcmenu)); 
    		m_calcmenu = keyColor(m_calcmenu, 4);
        	m_calcmenu.setVisibility(View.VISIBLE);

        	calc_par = true;
    	}
    	m_counter.setVisibility(View.GONE);
    	m_keycode.setVisibility(View.GONE);
		m_forcibly.setVisibility(View.GONE);

    }
    public void restoreAc_place()
    {
    	m_calcind.setVisibility(View.GONE);
    	m_calcmenu.setVisibility(View.GONE);
		m_forcibly.setVisibility(View.GONE);
    	if(st.calc_fl_ind == true)
    		m_addVocab.setVisibility(m_addVocab1);
        m_counter.setVisibility(m_counter1);
        m_keycode.setVisibility(m_keycode1);
        m_rightView.setVisibility(m_rightView1);
    	if (ServiceJbKbd.inst.m_acPlace == 0) {
    		remove();
    	}
    	
   		int cc = m_ll.getChildCount();
		int[]tv_visible;
		tv_visible = new int[cc];
   		TextView[] tv;
   		tv = new TextView[cc];
   		for (int i=0;i<cc;i++) {
   			tv[i] = (TextView)m_ll.getChildAt(i);
   			tv[i].setVisibility(tv_visible[i]);
       	}

    }
    public void setCalcInd1(int text)
    {
    	if (calc_full.length() == 0)
    		calc_full = "0.0";
		calc_cel = calc_full.substring(0, calc_full.indexOf("."));
		calc_drob_s = calc_full.substring(calc_full.indexOf(".")+1);
		if (calc_drob_s.length()==1&calc_drob_s.contains(st.STR_ZERO))
			calc_drob_s =st.STR_NULL;
		calc_tmp=st.STR_NULL;
		if (calc_drob == true) {
			calc_tmp = ".";
//			calc_drob=false;
		}
		if (calc_cel.length()==1&calc_cel.contains(st.STR_ZERO))
			calc_cel = st.STR_NULL;
		if (calc_first_input == true) {
			calc_ind=st.STR_NULL;
			calc_first_input = false;
		}
		if (calc_b_arrow == true) {
			calc_ind=st.STR_NULL;
			calc_cel=st.STR_NULL;
			calc_drob_s=st.STR_NULL;
			calc_b_arrow = false;
		}
		if (text !=-1)
			calc_ind=calc_cel+calc_tmp+calc_drob_s+text;
		else
			calc_ind+=calc_cel+calc_tmp+calc_drob_s;
    }
    public void setKeycode(int num)
    {
   		m_keycode.setText(String.valueOf(num));
    }
    public void setInd(boolean ind)
    {
    	st.calc_fl_ind=ind;
	}
    public void setDefaultWords(String defword)
    {
    	defword+=st.STR_SPACE;
    	String w=st.STR_NULL;
    	int cnt=0;
    	for (int i = 0; i < defword.length(); i++) {
            if(defword.charAt(i)==' ')
            	cnt++;
    	}
    	m_texts= new String[cnt];
    	int cnt1=0;
    	for (int i = 0; i < defword.length(); i++) {
    		char c= defword.charAt(i);
            switch (c)
            {
                case ' ': {
                	m_texts[cnt1]=w;
                	cnt1++;
                	w=st.STR_NULL;
                ;break;
                }
                default:
                    w += c;
                break;
            }
    	}
    	if (w.length() > 0)
    		m_texts[cnt1+1]=w;
    	m_defkey=m_texts;
    }
// установки цветов кнопок и текста в них в автодополнении НЕ ИЗ 0.97    
    public TextView keyColor(TextView tv, int btn_type)
    {
//        tv.setBackgroundResource(R.drawable.cand_item_background);
        int color = 0;
        switch (btn_type)
        {
        // 1 - counter
        // 2 - keycod
        // 3 - calcind
        // 4 - calc_menu
        // 5 - принудительный словарь
        // 6 - слова
        // 7 - добавить
        // 8 - стрелка вниз
        case 1:
            tv.setBackgroundColor(st.ac_col_counter_back);
            tv.setTextColor(st.ac_col_counter_text);
            break;
        case 2:
            tv.setBackgroundColor(st.ac_col_keycode_back);
            tv.setTextColor(st.ac_col_keycode_text);
            break;
        case 3:
            tv.setBackgroundColor(st.ac_col_calcind_back);
            tv.setTextColor(st.ac_col_calcind_text);
            break;
        case 4:
            tv.setBackgroundColor(st.ac_col_calcmenu_back);
            tv.setTextColor(st.ac_col_calcmenu_text);
            break;
        case 5:
            tv.setBackgroundColor(st.ac_col_forcibly_back);
            tv.setTextColor(st.ac_col_forcibly_text);
            break;
        case 6:
            tv.setBackgroundColor(st.ac_col_word_back);
            tv.setTextColor(st.ac_col_word_text);
            break;
        case 7:
            tv.setBackgroundColor(st.ac_col_addvocab_back);
            tv.setTextColor(st.ac_col_addvocab_text);
            break;
        case 8:
            tv.setBackgroundColor(st.ac_col_arrow_down_back);
            tv.setTextColor(st.ac_col_arrow_down_text);
            break;
        default:
            tv.setBackgroundColor(Color.BLACK);
            tv.setTextColor(Color.WHITE);
        break;
      }
        
//        tv.setBackgroundColor(color);
        if (m_es != null) {
    		m_es.setToEditor(tv);
    	}
    	return tv;
	}
// подбирает подходящие слова из текущей позиции курсора или выдаёт
// строку по умолчанию
// bfl - выдавать звук или нет    
    public void createWord(boolean fl_beep)
    {
    	ServiceJbKbd.inst.openWords();
    	ServiceJbKbd.inst.showCandView(true);
    	ServiceJbKbd.inst.getCandidates();
    	if (fl_beep)
    		ServiceJbKbd.inst.beep(0);
	}
    public void setVisible(TextView tv, boolean visible)
    {
    	if (visible)
    		tv.setVisibility(View.VISIBLE);
    	else
    		tv.setVisibility(View.GONE);
    }
    // преобразует кнопку в функциональную или пишет на ней текст
	public void setTexAndFuncKey(TextView tv, String txt, int id)
    {
//		if (tv.getText().toString().startsWith(st.STR_PREFIX)){
		if (txt.startsWith(st.STR_PREFIX)){
			id++;
//			ArrayFuncAddSymbolsGest ar = st.getElementSpecFormatSymbol(arFuncKey, id);
//        	if (ar==null){
//            	tv.setId(id);
//        	}
        	st.setElementSpecFormatAddSymbol(arFuncKey,txt,id);
        	ArrayFuncAddSymbolsGest ar = st.getElementSpecFormatSymbol(arFuncKey, id);
        	if (ar!=null){
        		txt = ar.visibleText+st.STR_SPACE;
        		tv.setId(id);
        		Drawable img = m_c.getResources().getDrawable( R.drawable.bullet_red);
        		img.setBounds( 0, 0, 15, 15 );
        		tv.setCompoundDrawables( img, null, null, null );
        		tv.setCompoundDrawablePadding(0);
        	}
		} else {
			tv.setId(-1);
    		tv.setCompoundDrawables( null, null, null, null );
		}
		tv.setText(txt);
    }

}