package com.jbak2.JbakKeyboard;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.jbak2.CustomGraphics.GradBack;
import com.jbak2.JbakKeyboard.JbKbd.LatinKey;
import com.jbak2.JbakKeyboard.KeyboardGesture.GestureHisList;
import com.jbak2.JbakKeyboard.st.ArrayFuncAddSymbolsGest;
import com.jbak2.ctrl.GlobDialog;
import com.jbak2.ctrl.IntEditor;
import com.jbak2.ctrl.Mainmenu;
import com.jbak2.web.SearchGoogle;
import com.jbak2.words.WordsService;
import com.jbak2.words.UserWords.WordArray;

/** Класс содержит полезные статические переменные */
public class st extends IKeyboard implements IKbdSettings
{
	// вызов клавиатуры из шторки
    public static boolean fl_show_kbd_notif= false;
	// показывать ли сообщение о копировании
    public static boolean fl_copy_toast = false;
	// показывать ли путь к текущей папке в шаблонах
    public static boolean fl_tpl_path = true;
	// своя маленькая клавиатура первой версии
    public static boolean fl_mini_kbd_its = false;
    public static int mini_kbd_btn_size = 0;
    public static int mini_kbd_btn_text_size = 0;
    // размер символов на кнопках
    public static int mini_kbd_text_size = 0;
	// язык на котором выводим настройки
	public static String lang_pref = "ru";
	// на каком языке выводить Как пользоваться клавиатурой
    public static String lang_desckbd = STR_3TIRE;
	// менять ли изображение на ентер в зависимости от контекста 
    public static boolean fl_enter_state = false;
	// мастер быстрых настроек. Значения (1 или 0)
	// 0 -выбор раскладки 
	// 1 -выбор скина
	// 2 -где показывать автодоп
    // 3 - язык программы
    // 4 - высота клавиатуры
	public static int[] qs_ar = new int[] {0,0,0,0,0};
	
	// key sound effect
	public static int[] kse = new int[]
	{
		AudioManager.FX_KEY_CLICK, // all key
		AudioManager.FX_FOCUS_NAVIGATION_LEFT,
		AudioManager.FX_FOCUS_NAVIGATION_RIGHT,
		AudioManager.FX_FOCUS_NAVIGATION_UP,
		AudioManager.FX_FOCUS_NAVIGATION_DOWN,
		AudioManager.FX_KEYPRESS_SPACEBAR,
		AudioManager.FX_KEYPRESS_RETURN,
		AudioManager.FX_KEY_CLICK, // backspace
		AudioManager.FX_KEYPRESS_DELETE,
		AudioManager.FX_KEY_CLICK // shift
	};
			

    public static Vector<KbdGesture> arGestures = new Vector<KbdGesture>();

    public static int set_kbdact_backcol = 0;
// флаг как выводить выпадающий список автодопа - 
// по алфавиту (true)
// или по частоте использования слова (false)
	public static boolean fl_alphabetically = false;
	// вставка ентера после вставки из мультибуфера
	public static boolean fl_enter_key = false;
	// синхронизация мультибуфера
	public static boolean fl_sync = false;
	public static boolean fl_sync_create_new_file = false;
	public static int cs_dur = 1440;
	public static int cs_dur_def = 1440;
	public static int cs_size = 10;
	public static int cs_size_def = 10;
	public static int cs_cnt = 20;
	public static int cs_cnt_def = 20;
	// показывать кнопку синхронизации в мултибуфере
	public static boolean fl_clipbrd_btn_sync_show = true;
	// подавлять сообщение о синхронизации
	public static boolean fl_clipbrd_sync_msg = true;
	
// удалять ли пробел
	public static boolean del_space = false;
// запущена preferences activity или редактор шаблонов
	public static boolean fl_pref_act = false;
// строка для запуска интента для маркета
	public static String RUN_MARKET_STRING = "https://play.google.com/store/apps/details?id=";
// слова в автодополнении по умолчанию
	public static String AC_DEF_WORD = "$[-500,Menu] ! @ ? ; : , .";
// последнее состояние шифта
	public static int last_case = 0;
// флаг, что delsymb() из servicekbd выполнил удаление	
	public static boolean fl_delsymb = false;
// флаг что нажато слово из автодополнения
	public static boolean fl_ac_word = false;
	
// отображение счётчика нажатых клавиш и кода нажатой клавиши
	public static boolean fl_counter = false;
	public static boolean fl_keycode = false;
// показывать код в десятичной (true) или в 16х
    public static boolean fl_keycode_dec = true;
    
// высота кнопок в автодополнении	
	public static int ac_key_height = 2;
//// флаги что требуется открыть клавиатуру при закрытии окна
//// второй флаг нужен чтобы показать клавиатуру после
//// главное меню-настройки-языки и раскладки-сохранить	
//	public static boolean show_kbd = false;
//	public static boolean show_kbd1 = false;
// цвета кнопок и текста в автодополнении	
	public static int ac_col_main_back = AC_COLDEF_MAIN_BG;
	public static int ac_col_keycode_back = AC_COLDEF_KEYCODE_BG;
	public static int ac_col_keycode_text = AC_COLDEF_KEYCODE_T;
	public static int ac_col_counter_back = AC_COLDEF_COUNTER_BG;
	public static int ac_col_counter_text = AC_COLDEF_COUNTER_T;
	public static int ac_col_forcibly_back = AC_COLDEF_FORCIBLY_BG;
	public static int ac_col_forcibly_text = AC_COLDEF_FORCIBLY_T;
	public static int ac_col_addvocab_back = AC_COLDEF_ADD_BG;
	public static int ac_col_addvocab_text = AC_COLDEF_ADD_T;
	public static int ac_col_word_back = AC_COLDEF_WORD_BG;
	public static int ac_col_word_text = AC_COLDEF_WORD_T;
	public static int ac_col_arrow_down_back = AC_COLDEF_ARROWDOWN_BG;
	public static int ac_col_arrow_down_text = AC_COLDEF_ARROWDOWN_T;
	public static int ac_col_calcmenu_back = AC_COLDEF_CALCMENU_BG;
	public static int ac_col_calcmenu_text = AC_COLDEF_CALCMENU_T;
	public static int ac_col_calcind_back = AC_COLDEF_CALCIND_BG;
	public static int ac_col_calcind_text = AC_COLDEF_CALCIND_T;
	
// принудительное чтение из словаря	
	public static boolean fl_suggest_dict = false;
// какое freq ставить при добавлении нового слова
	public static int freq_dict = 500000;
// интеллектуальный ввод
	public static boolean student_dict = false;
// расширенное обучение словаря
	public static boolean student_dict_ext = false;
// количество слов в списке автодополнения 	
	public static int ac_list_value = 20;
// высота окна автодополнения	
	public static int ac_height = 0;

// если строка пуста - папка не фиксируется	
	public static String type_keyboard = st.STR_NULL;

	
// строка хранящая путь к папке шаблона для фиксации
// если строка пуста - папка не фиксируется	
	public static String fiks_tpl = st.STR_NULL;
// флаг что папка щаблоново уже выведена и фиксировать её не нужно 
	public static boolean fl_fiks_tpl = true;
// флаг что мультибуфер закрывать не нужно 
	public static boolean fl_fiks_clip = false;
// строка помощи для активности help
// если задана - то выводится она, иначе текст на клавише -514 заданный в поле help
	public static String help = st.STR_NULL;
// размер окна нажатых клавиш
	public static double popup_win_size  = 1;
// флаг, что нужно остаться на той же клавиатуре qwerty
	public static boolean fl_qwerty_kbd  = false;
	
//*****************************
// параметры для popupCharacter2
//*****************************
	public static boolean win_fix  = false;
	public static int win_bg  = str2hex(IKbdSettings.PREF_KEY_PC2_WIN_BG_DEF,16);
	public static int btn_size  = str2hex(IKbdSettings.PREF_KEY_PC2_BTN_SIZE_DEF,10);
	public static int btn_bg = str2hex(IKbdSettings.PREF_KEY_PC2_BTN_BG_DEF,16);
	public static int btn_tc = str2hex(IKbdSettings.PREF_KEY_PC2_BTN_TCOL_DEF,16);
	public static int btnoff_size  = str2hex(IKbdSettings.PREF_KEY_PC2_BTNOFF_SIZE_DEF,10);
	public static int btnoff_bg = str2hex(IKbdSettings.PREF_KEY_PC2_BTNOFF_BG_DEF,16);
	public static int btnoff_tc = str2hex(IKbdSettings.PREF_KEY_PC2_BTNOFF_TCOL_DEF,16);

//*****************************
// параметры для запуска активности desc_act
//*****************************
// выводить ли принудительно активность, даже если указано "больше не выводить"
	public static boolean desc_fl_input = false;
// какой файл выводить
	public static String desc_file_input = "_desk_kbd.txt";
	public static String FILE_DIARY= "_diary.txt";
	// флаг для отображения "больше не выводить"
		public static boolean desc_fl_not_input = false;
// нужно ли отображать в окне чекбокс "больше не выводить"
	public static int desc_view_input = 0;

// временное отключение звука
// по умолчанию включён
// чтобы временно отключить звук, сперва ставим эту переменную в false,
// потом вызываем ServiceJbKbd.inst.beep(primaryCode)
// и снова ставим её в true
	public static boolean fl_sound = true;
// флаг что список автодополнения открыт
	public static boolean fl_ac_list_view = false;
// переменная хранящая значение где показываем автодополнение
	public static int  ac1 = 1;
// флаги записи макросов
	public static boolean fl_macro1 = false;
	public static boolean fl_macro2 = false;
// массивы макросов
	public static ArrayList<Integer> macro1 = new ArrayList<Integer>();
	public static ArrayList<Integer> macro2 = new ArrayList<Integer>();
	
// флаг что нужно перечитать свои жесты в настройках
	public static boolean fl_gesture_setting = true;
// флаг что нужно скрывать кнопку "стрелка вниз" из автодополнения
	public static boolean ac_place_arrow_down = false;
// флаг что left alt включен
	public static boolean fl_lalt = false;;
	// флаг что right alt включен
	public static boolean fl_ralt = false;;
// флаг что ctrl включен
	public static boolean fl_ctrl = false;
// массив своих жестов
	public static ArrayList<GestureHisList> gc = new ArrayList<GestureHisList>();
// длина жеста 
	public static float gesture_length = 0;
// минимальная длина жеста для настроек
	public static int gesture_min_length = 100;
// минимальная длина жеста 
	public static int minGestSize = 100;
// Скорость жеста	
	public static int gesture_velocity = 150;
// строка для жеста "дополнительные символы"
	public static String gesture_str = st.STR_NULL;
// флаг, что popupcharacter2 обработан
	public static boolean fl_popupcharacter2 = false;;

// временное хранение где показывать автодополнение	
	public static int acplce1 = 0;
	
	
/** временные переменные */
	public static String tmps = st.STR_NULL;
	public static int tmpi = 0;

	
	
// тип открытой в данный момент клавиатуры
	// 1 - qwerty
	// 2 - num
	// 3 - edit
	// 4 - symbol1
	// 5 - symdol2
	// 6 - smile
	// 7 - калькулятор
	public static int type_kbd = 1;
// массив строк ИЗБРАННЫХ запускаемых приложений 
	public static ArrayList<String> runapp_favorite = new ArrayList<String>();
//	public static String[] runapp_favorite = new String[20];
// массив строк всех запускаемых приложений 
//	public static ArrayList<String> runapp_all = new ArrayList<String>();
	public static String[] runapp_all = new String[1500];
	// размер текста кнопок меню
	public static int mm_btn_size = 15;
// размер текста служебных кнопок меню
	public static int mm_btn_off_size = 8;
// массив строк главного меню
	public static String[] mainmmenu = new String[]
			{
				"template",
				"clipboard",
				"setting",
				"input_method", //метод ввода
				"inputlayout",//выбор раскладки
				"inputmethodnumber",//метод ввода цифр
				"compilekbd",
				"decompile",
				"reloadskin",
				st.PREF_KEY_RUNAPP,
				"calculator",
				"ac_hide",
		    };
// сколько всего пунктов главного меню
	public static int mainmmenu_allitem = mainmmenu.length;
// флаг, что нужно пункты главного меню прочесть из настроек
	public static boolean mainmmenu_fl = true;
//массив какие строки главного меню выводить
	public static int[] mainmmenui = new int[]
		{
			1,2,3,11,-1,-1,-1,-1,-1,-1,-1,-1,
	    };
//массив какие строки главного меню НЕвыводить
	public static int[] mainmmenui_n = new int[]
		{
			4,5,6,7,8,9,10,11,12,-1,-1,-1,-1
	    };
//массив хранящий id кнопок
	public static int[] mainmmenui_id = new int[]
		{
			-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1
	    };
//массив хранящий id кнопок непоказываемых пунктов меню
	public static int[] mainmmenui_n_id = new int[]
		{
			-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1
	    };
// массив истории вычислений калькулятора
    public static final String[] calc_history = new String[]
    {
        st.STR_NULL,
        st.STR_NULL,
        st.STR_NULL,
        st.STR_NULL,
        st.STR_NULL,
        st.STR_NULL,
        st.STR_NULL,
        st.STR_NULL,
        st.STR_NULL,
        st.STR_NULL,
        st.STR_NULL,
    };
// *********************************************************    
// КАЛЬКУЛЯТОР
// *********************************************************    
    
 // массив регистров памяти калькулятора
 	public static double[] calc_reg = new double[100];
// если строка пуста - папка не фиксируется	
 	public static String fiks_calc = st.STR_NULL;
// флаг что папка калькулятора уже выведена и фиксировать её не нужно 
 	public static boolean fl_fiks_calc = true;
// описание программы калькулятора
	public static String calc_prg_desc = st.STR_NULL;
// массив для подпрограмм калькулятора (глубина стека до 100 подпрограмм)
	public static int[] calc_pp = new int[100];
// массив программы калькулятора
	public static int[] calc_prog = new int[1000];
// указатель текущего положения в программе калькулятора
	public static int calc_prog_step = 0;
// это первый запуск режима программирования - нужно очистить массив программы (присвоить -1 всем элементам)
	public static boolean calc_iniprg = false;
	// флаг включения индикатора
	static boolean calc_fl_ind = false;

	public static final boolean DEBUG = true;
/** Код, который используется, если основной текст клавиши из нескольких букв*/    
    public static int KeySymbol = -201;
 // теги скинов. 
 // Используются в CustonDesign и конструкторе скинов	
 	public static String arDesignNames[] = new String[]{
             "KeyBackStartColor",
             "KeyBackEndColor",
             "KeyBackGradientType",
             "KeyTextColor",
             "KeyTextBold",
             "KeyGapSize",
             "KeyStrokeStartColor",
             "KeyStrokeEndColor",
             "KeyboardBackgroundStartColor",
             "KeyboardBackgroundEndColor",
             "KeyboardBackgroundGradientType",
             "SpecKeyBackStartColor",
             "SpecKeyBackEndColor",
             "SpecKeyStrokeStartColor",
             "SpecKeyStrokeEndColor",
             "SpecKeyTextColor",
             "KeyBackCornerX",
             "KeyBackCornerY",
             "KeySymbolColor",
             "KeyTextPressedColor",
             "KeySymbolPressedColor",
             "SpecKeySymbolColor",
             "SpecKeyTextPressedColor",
             "SpecKeySymbolPressedColor",
             "KeyBackPressedStartColor",
             "KeyBackPressedEndColor",
             "KeyBackPressedGradientType",
             "KeyPressedStrokeStartColor",
             "KeyPressedStrokeEndColor",
             "SpecKeyBackPressedStartColor",
             "SpecKeyBackPressedEndColor",
             "SpecKeyBackPressedGradientType",
             "SpecKeyPressedStrokeStartColor",
             "SpecKeyPressedStrokeEndColor",
       };
 	// массив для хранения специального формата для клавиш 
 	// из жеста Дополнительных символов
	public static ArrayList<ArrayFuncAddSymbolsGest> ar_asg = new ArrayList<ArrayFuncAddSymbolsGest>();
 	// массив для хранения специального формата для клавиш 
 	// из жеста Дополнительных символов
 	public static class ArrayFuncAddSymbolsGest
    {
 	    public int id = 0;
 	    public int code = 0;
 	    public String visibleText = st.STR_NULL;

 	    public ArrayFuncAddSymbolsGest(int  _id,int _code,String _visibleText)
        {
 	    	id = _id;
 	    	code = _code;
 	    	visibleText = _visibleText;
        }
 	    public ArrayFuncAddSymbolsGest()
        {
        }
    }
 	
 // класс для значений тегов дизайна
 	public static class IntEntry
     {
 	    public static final byte KeyBackStartColor              = 0;
 	    public static final byte KeyBackEndColor                = 1;
 	    public static final byte KeyBackGradientType            = 2;
 	    public static final byte KeyTextColor                   = 3;
 	    public static final byte KeyTextBold                    = 4;
 	    public static final byte KeyGapSize                     = 5;
 	    public static final byte KeyStrokeStartColor            = 6;
 	    public static final byte KeyStrokeEndColor              = 7;
 	    public static final byte KeyboardBackgroundStartColor   = 8;
 	    public static final byte KeyboardBackgroundEndColor     = 9;
 	    public static final byte KeyboardBackgroundGradientType = 10;
 	    public static final byte SpecKeyBackStartColor         =11;
 	    public static final byte SpecKeyBackEndColor =12;
 	    public static final byte SpecKeyStrokeStartColor = 13;
 	    public static final byte SpecKeyStrokeEndColor = 14;
 	    public static final byte SpecKeyTextColor = 15;
 	    public static final byte KeyBackCornerX = 16;
 	    public static final byte KeyBackCornerY = 17;
 	    public static final byte KeySymbolColor=18;
 	    public static final byte KeyTextPressedColor=19;
 	    public static final byte KeySymbolPressedColor=20;
 	    public static final byte SpecKeySymbolColor=21;
 	    public static final byte SpecKeyTextPressedColor=22;
 	    public static final byte SpecKeySymbolPressedColor=23;
 	    public static final byte KeyBackPressedStartColor=24;
 	    public static final byte KeyBackPressedEndColor=25;
 	    public static final byte KeyBackPressedGradientType=26;
 	    public static final byte KeyPressedStrokeStartColor=27;
 	    public static final byte KeyPressedStrokeEndColor=28;
 	    public static final byte SpecKeyBackPressedStartColor=29;
 	    public static final byte SpecKeyBackPressedEndColor=30;
 	    public static final byte SpecKeyBackPressedGradientType=31;
 	    public static final byte SpecKeyPressedStrokeStartColor=32;
 	    public static final byte SpecKeyPressedStrokeEndColor=33;

         int index=-1;
         int value=-1;
         int defvalue=-1;
         int resId_et = -1;
         public IntEntry()
         {}
         public IntEntry(int ind,int val)
         {
         	index = ind;
         	value=val;
         	resId_et = -1;
         	defvalue = -1;
         }
         public IntEntry(int ind,int val,int res_id_et)
         {
         	index = ind;
         	value=val;
         	defvalue = -1;
         	resId_et = res_id_et;
         }
         public IntEntry(int ind,int val,int defval,int res_id_et)
         {
         	index = ind;
         	value=val;
         	defvalue = defval;
         	resId_et = res_id_et;
         }
         
    	}

//--------------------------------------------------------------------------
    /** Универсальный обсервер. Содержит 2 параметра m_param1 и m_param2, которые вызываются и меняются в зависимости от контекста*/
    public static abstract class UniObserver
    {
    /** Конструктор с двумя параметрами */
        public UniObserver(Object param1,Object param2)
        {
            m_param1 = param1;
            m_param2 = param2;
        }
    /** Пустой конструктор. Оба параметра - null*/
        public UniObserver()
        {
        }
    /** Вызов функции {@link #OnObserver(Object, Object)} с текущими параметрами*/
        public int Observ()
        {
        	return OnObserver(m_param1, m_param2);
        }
    /** Основная функция обработчика */ 
        public abstract int OnObserver(Object param1,Object param2);
    /** Пользовательский параметр 1 */  
        public Object m_param1;
    /** Пользовательский параметр 2 */  
        public Object m_param2;
    }
    // возвращает элемент массива для функциональных клавиш или null
    public static ArrayFuncAddSymbolsGest getElementSpecFormatSymbol(ArrayList<ArrayFuncAddSymbolsGest> ar,int id)
    {
    	for (ArrayFuncAddSymbolsGest elem:ar){
    		if (elem.id == id)
    			return elem;
    	}
    	return null;
    }
    // устанавливает в массив ar новое значение для функциональных клавиш
    public static void setElementSpecFormatAddSymbol(ArrayList<ArrayFuncAddSymbolsGest> ar, String txt, int id)
    {
    	if (!txt.startsWith(st.STR_PREFIX))
    		return;
    	txt = txt.substring(2, txt.length()-1);
    	String[] art = txt.split(st.STR_COMMA);
    	int i = 0;
		try {
			i =Integer.parseInt(art[0]);
		} catch (NumberFormatException e){}
		ArrayFuncAddSymbolsGest tmp = st.getElementSpecFormatSymbol(ar, id);
		if (tmp==null)
			tmp = new ArrayFuncAddSymbolsGest();
		if (art==null||art.length<2){
			tmp.code = 0;
			tmp.id = id;
			tmp.visibleText = st.STR_ERROR;
		} else {
			tmp.code = i;
			tmp.id = id;
			tmp.visibleText = art[1];
		}
		ar.add(tmp);
    }
    public static Drawable getBack()
    {
        return new GradBack(0xff000088, 0xff008800).setCorners(0, 0).setGap(0).setDrawPressedBackground(false).getStateDrawable();
    }
    /** Эквивалент вызова (val&flag)>0*/        
    public static final boolean has(int val,int flag)
    {
        return (val&flag)>0;
    }
/** Убирает бит flag из значения val, если бит выставлен*/      
    public static final int rem(int val,int flag)
    {
        if(has(val,flag))
            return val^flag;
        return val;
    }
    public static final int min(int val1,int val2)
    {
    	if(val1<val2)return val1;
    	return val2;
    }
    public static final int max(int val1,int val2)
    {
    	if(val1>val2)return val1;
    	return val2;
    }
    public static final void logEx(Throwable e)
    {
        if(DEBUG)
        {
        if(e.getMessage()!=null)
        Log.e(TAG, Log.getStackTraceString(e));
        }
    }
/** Возвращает клавиатуру для языка с именем langName */    
    public static Keybrd getKeybrdForLangName(String langName)
    {
        boolean isLandscape = isLandscape(c());
        String pname = isLandscape?st.PREF_KEY_LANG_KBD_LANDSCAPE:st.PREF_KEY_LANG_KBD_PORTRAIT;
        pname+=langName;
        String kbdName = pref().getString(pname, st.STR_NULL);
        for(int i=0;i<3;i++)
        {
            for(Keybrd k:arKbd)
            {
                if(k.isLang(langName))
                {
                    if(kbdName.length()==0&&(isLandscape||i>0||k.isWide)||kbdName.equals(k.path))
                    {
                        // если не настроено - вернем первую в списке клаву с тем же языком
                        return k;
                    }
                }
            }
            // Теперь попробуем перезагрузить клавиатуры...
            if(i==0)
                CustomKeyboard.loadCustomKeyboards(false);
            else if(i==1)
                kbdName = st.STR_NULL;
        }
        Context c = st.c();
        if(c!=null)
            Toast.makeText(c, "Lang not found:"+langName, Toast.LENGTH_LONG).show();
        return arKbd[0];
    }
    static void log(String txt)
    {
        if(DEBUG)
            Log.w(TAG, txt);
    }
/** Сохраняет текущий ресурс qwerty-клавиатуры, если редактирование происходит в qwerty */    
    public static void saveCurLang()
    {
        JbKbd kb = curKbd();
        if(kb==null||kb.kbd==null)
            return;
        pref().edit().putString(st.PREF_KEY_LAST_LANG, kb.kbd.lang.name).commit();
    }
/** Возвращает текущий ресурс для qwerty-клавиатуры */    
    public static Keybrd getCurQwertyKeybrd()
    {
        SharedPreferences p =pref();
        if(p==null||!p.contains(PREF_KEY_LAST_LANG))
        {
            String lang = Locale.getDefault().getLanguage();
            Keybrd l = getKeybrdForLangName(lang);
            if(l!=null)
                return l;
            return defKbd();
        }
        String lang = p.getString(PREF_KEY_LAST_LANG, defKbd().lang.name);
        String langs[] = getLangsArray(st.c());
        boolean bExist = false;
        for(String l:langs)
        {
            if(l.equals(lang))
            {
                bExist = true;
                break;
            }
        }
        if(!bExist)
        {
            if(langs.length>0)
                lang = langs[0];
            else
                lang = defKbd().lang.name;
        }
        return getKeybrdForLangName(lang);
    }
/** Возвращает текущую клавиатуру или null*/    
    public static JbKbd curKbd()
    {
        if(JbKbdView.inst==null) 
        	return null;
        return (JbKbd)JbKbdView.inst.getCurKeyboard();
    }
/** Возвращает активный контекст. Если запущено {@link SetKbdActivity} - то возвращает его, иначе - {@link ServiceJbKbd}*/    
    public static Context c()
    {
        if(SetKbdActivity.inst!=null)
            return SetKbdActivity.inst;
        if(ServiceJbKbd.inst!=null)
            return ServiceJbKbd.inst;
        if(LangSetActivity.inst!=null)
            return LangSetActivity.inst;
        if(EditSetActivity.inst!=null)
            return EditSetActivity.inst;
        return JbKbdPreference.inst;
    }
  //********************************************************************
    /** Класс для запуска пользовательского кода синхронно или асинхронно
     * Создаётся без параметров. По окончании выполнения запускается обработчик */
    public static abstract class SyncAsycOper extends AsyncTask<Void,Void,Void>
    {
    /** Конструктор
     * @param obs Обработчик, который запустится по выполнении */
        public SyncAsycOper(UniObserver obs)
        {
            m_obs = obs;
        }
    /** Синхронно стартует операцию {@link #makeOper(UniObserver)}*/
        public void startSync()
        {
            makeOper(m_obs);
        }
    /** Асинхронно стартует операцию {@link #makeOper(UniObserver)}*/
        public void startAsync()
        {
            execute();
        }
    /** @hide */
        @Override
        protected void onProgressUpdate(Void... values)
        {
            if(m_obs!=null)
                m_obs.Observ();
        }
    /** @hide */
        @Override
        protected Void doInBackground(Void... arg0)
        {
            try{
                makeOper(m_obs);
                publishProgress();
            }
            catch (Exception e) {
            }
            return null;
        }
    /** Выполняемая операция  */
        public abstract void makeOper(UniObserver obs);
    /** Обработчик операции */  
        UniObserver m_obs;
    }
    public static boolean isQwertyKeyboard(Keybrd k)
    {
        return !k.lang.isVirtualLang();
    }
    static Vector <Keybrd> getKeybrdArrayByLang(String lang)
    {
        Vector<Keybrd> ret = new Vector<IKeyboard.Keybrd>();
        for(Keybrd k:st.arKbd)
        {
            if(k.lang.name.equals(lang))
                ret.add(k);
        }
        return ret;
    }

/** Установка клавиатуры редактирования текста */
    public static void setTextEditKeyboard()
    {
    	st.type_kbd = 3;

    	boolean m_def_window=true;
    	JbKbdView.inst.setKeyboard(loadKeyboard(getKeybrdForLangName(LANG_EDITTEXT)));

    }
/** Установка клавиатуры смайликов */
    public static void setSmilesKeyboard()
    {
    	boolean m_def_window=true;
        JbKbdView.inst.setKeyboard(loadKeyboard(getKeybrdForLangName(LANG_SMILE)));
    }
    /** Установка цифровой клавиатуры */
    public static void setNumberKeyboard()
    {
        JbKbdView.inst.setKeyboard(loadKeyboard(getKeybrdForLangName(LANG_NUMBER)));
    }
    /** Установа клавиатуры калькулятора*/
    public static void setCalcKeyboard()
    {
        JbKbdView.inst.setKeyboard(loadKeyboard(getKeybrdForLangName(LANG_CALCULATOR)));
    }
/** Установка символьной клавиатуры 
*@param bShift true - для установки symbol_shift, false - для symbol */
    public static void setSymbolKeyboard(boolean bShift)
    {
        Keybrd k = getKeybrdForLangName(bShift?LANG_SYM_KBD1:LANG_SYM_KBD);
        JbKbdView.inst.setKeyboard(loadKeyboard(k));
    }
/** Установка qwerty-клавиатуры с учётом последнего использования */    
    public static void setQwertyKeyboard()
    {
        tempEnglishQwerty = false;
        setQwertyKeyboard(false);
    }
/** Установка qwerty-клавиатуры с учётом последнего использования */    
    public static void setQwertyKeyboard(boolean bForce)
    {
    	st.type_kbd = 1;
//        CustomKeyboard ck = new CustomKeyboard(JbKbdView.inst.getContext(), "/mnt/sdcard/jbakKeyboard/keyboards/qwerty_ru.xml");
//        JbKbdView.inst.setKeyboard(ck);
        Keybrd cur = getCurQwertyKeybrd();
        JbKbd kb = curKbd();
        if(!bForce&&!(kb==null||kb.kbd==null||kb.kbd!=cur))
        {
            JbKbdView.inst.setKeyboard(kb);
            return;
        }
        JbKbdView.inst.setKeyboard(loadKeyboard(cur));
        saveCurLang();
    }
    static JbKbd loadKeyboard(Keybrd k)
    {
        KeySymbol = -201;
        CustomKeyboard jk =  new CustomKeyboard(st.c(), k);
        if(!jk.m_bBrokenLoad)
        {
            return jk;
        }
        for(Keybrd ck:arKbd)
        {
            if(ck.lang.name.equals(k.lang.name))
                return loadKeyboard(ck);
        }
        return loadKeyboard(arKbd[0]);
    }
    public static boolean tempEnglishQwerty = false;
/** Временно устанавливает английскую клавиатуру без запоминания языка */    
    public static void setTempEnglishQwerty()
    {
        tempEnglishQwerty = true;
        JbKbd kb = curKbd();
        Keybrd k = getKeybrdForLangName(arLangs[LANG_EN].name);
//        if(kb!=null&&kb.resId==k.resId)
//            return;
        JbKbdView.inst.setKeyboard(loadKeyboard(k));
    }
    public static String getCurLang()
    {
        return pref().getString(PREF_KEY_LAST_LANG, defKbd().lang.name);
    }
/** Возвращает текущий запущеный {@link JbKbdView}*/    
    public static JbKbdView kv()
    {
    	if (JbKbdView.inst!=null)
    		JbKbdView.inst.m_KeyHeight = getKeyHeight();
        return JbKbdView.inst;
    }
    // возвращает высоту клавиш в зависимости от ориентации
    public static int getKeyHeight()
    {
    	int ret = 0;
    	if (!st.isLandscape(c()))
    		ret = KeyboardPaints.getValue(c(), st.pref(), KeyboardPaints.VAL_KEY_HEIGHT_PORTRAIT);
    	else
    		ret = KeyboardPaints.getValue(c(), st.pref(), KeyboardPaints.VAL_KEY_HEIGHT_LANDSCAPE);

    			
    	return ret;
    }
/** Возвращает строку по умолчанию для переключения языков <br>
 * По умолчанию - язык текущей локали+английский, если нет языка текущей локали - то только английский
 */
    public static String getDefaultLangString()
    {
        String lang = Locale.getDefault().getLanguage();
        String defKbdLang = defKbd().lang.name;
        if(getKeybrdForLangName(lang)!=null&&!lang.equals(defKbdLang))
        {
            return lang+','+defKbdLang;
        }
        return defKbdLang;
    }
/** Возвращает массив языков для переключения */    
    public static String[] getLangsArray(Context c)
    {
        String langs = pref().getString(st.PREF_KEY_LANGS, st.getDefaultLangString());
        return langs.split(",");
    }
/** Возвращает позицию строки search в массиве ar, или -1, если не найдено*/    
    public static int searchStr(String search,String ar[])
    {
        if(ar==null||search==null)
            return -1;
        for(int i=0;i<ar.length;i++)
        {
            if(ar[i].equals(search))
                return i;
        }
        return -1;
    }
/** Возвращает коннект к БД или создаёт новый */    
    static Stor stor()
    {
        if(Stor.inst!=null)
            return Stor.inst;
        if(st.c()==null)
            return null;
        return new Stor(st.c());
    }
/** */ 
    static boolean runAct(Class<?>cls)
    {
        return runAct(cls,c());
    }
    static boolean runAct(Class<?>cls,Context c)
    {
        try{
            
            c.getApplicationContext().startActivity(
                    new Intent(Intent.ACTION_VIEW)
                        .setComponent(new ComponentName(c,cls))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            );
        }
        catch(Throwable e)
        {
        	st.logEx(e);
            return false;
        }
        return true;
    }
/** Заменяет спецсимволы в строке fn на _ , возвращает модифицированную строку */   
    public static final String normalizeFileName(String fn)
    {
        return fn.replaceAll("[\\\"\\/:?!\\\'\\\\]", "_");
    }
/** Выполняет клавиатурную команду с кодом cmd*/    
    static boolean kbdCommand(int action)
    {
    	Context c = st.c();
        switch(action)
        {
        	case st.CMD_EDIT_USER_VOCAB:
                st.runAct(EditUserVocab.class,c);
            	com_menu.close();
            break;
        	case st.CMD_HEIGHT_KEYBOARD:
        		st.hidekbd();
        		if (st.isLandscape(c))
        			runSetKbd(c,st.SET_KEY_HEIGHT_LANDSCAPE);
        		else
        			runSetKbd(c,st.SET_KEY_HEIGHT_PORTRAIT);
        		return true;
        	case st.CMD_SHARE_SELECTED:
        		st.sendShareTextIntent(c);
            	com_menu.close();
        		st.hidekbd();
        		return true;
            case CMD_FULL_DISPLAY_EDIT:
                if(ServiceJbKbd.inst==null)
                	return true;
                ServiceJbKbd.inst.forceHide();
            	com_menu.close();
    	    	String txt = c.getString(R.string.set_key_landscape_input)+"\n(";
    	    	if (st.isLandscape(c))
    	    		txt += c.getString(R.string.set_key_landscape_input_type);
    	    	else
    	    		txt += c.getString(R.string.set_key_portrait_input_type);
    	    	txt+=")";
       			String[] ars = c.getResources().getStringArray(R.array.array_input_type);
       	        GlobDialog gd = new GlobDialog(st.c());
       	        gd.setGravityText(Gravity.LEFT|Gravity.TOP);
       	        gd.fl_back_key = true;
       	        gd.set(txt, R.string.yes,R.string.no);
       	        gd.setObserver(new st.UniObserver()
       	        {
       	            @Override
       	            public int OnObserver(Object param1, Object param2)
       	            {
                        if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                        {
                        	if (st.isLandscape(ServiceJbKbd.inst))
                        		ServiceJbKbd.inst.m_LandscapeEditType = 1;
                        	else
                        		ServiceJbKbd.inst.m_PortraitEditType = 1;
                        	st.toastLong(R.string.full_display_input_toast);
                        }
                        else if(((Integer)param1).intValue()==AlertDialog.BUTTON_NEUTRAL)
                        {
                        	if (st.isLandscape(ServiceJbKbd.inst))
                        		ServiceJbKbd.inst.m_LandscapeEditType = 0;
                        	else
                        		ServiceJbKbd.inst.m_PortraitEditType = 0;
                        }
                        st.pref().edit().putString(st.PREF_KEY_PORTRAIT_TYPE, st.STR_NULL+ServiceJbKbd.inst.m_PortraitEditType).commit();
                        st.pref().edit().putString(st.PREF_KEY_LANSCAPE_TYPE, st.STR_NULL+ServiceJbKbd.inst.m_LandscapeEditType).commit();
                        st.showkbd();
       	                return 0;
       	            }
       	        });
       	        gd.showAlert();
            	return true;
            case CMD_COMPILE_KEYBOARDS:
                CustomKeyboard.loadCustomKeyboards(true);
            	com_menu.close();
            break;
            case CMD_MAIN_MENU: 
                if(st.kv().isUserInput())
                {
                	if (st.fl_ac_list_view) 
                		ServiceJbKbd.inst.m_candView.ViewCandList();
                    ServiceJbKbd.inst.onOptions();
                }
            break;
            case CMD_VOICE_RECOGNIZER:
            	ServiceJbKbd.inst.m_voice.startVoiceRecognition();
//                new VRTest().startVoice(); 
            	return true;//return runAct(VRActivity.class);
            case CMD_TPL_EDITOR: 
            	return runAct(TplEditorActivity.class);
            case CMD_TPL_NEW_FOLDER: 
                if(Templates.inst==null)
                    return false;
                Templates.inst.setEditFolder(true);
                return runAct(TplEditorActivity.class);
            case CMD_TPL:
            	new Templates(1,0).makeCommonMenu();
//            	tpl.setDir(1,0);
//            	tpl.makeCommonMenu(); 
            	return true;
            case CMD_TRANSLATE_SELECTED:
            	new Translate(0).makeCommonMenu();
            	return true;
            case CMD_TRANSLATE_COPYING:
            	new Translate(1).makeCommonMenu();
            	return true;
            case CMD_SEARCH_SELECTED:
            	SearchGoogle.search(0);
            	return true;
            case CMD_SEARCH_COPYING:
            	SearchGoogle.search(1);
            	return true;
            case CMD_PREFERENCES: 
                if(ServiceJbKbd.inst!=null) 
                	ServiceJbKbd.inst.forceHide();
            	com_menu.close();
                return runAct(JbKbdPreference.class);
            case CMD_INPUT_METHOD:
            	if (ServiceJbKbd.inst.input_method==1)
            		ServiceJbKbd.inst.input_method=2;
            	else
            		ServiceJbKbd.inst.input_method=1;
            	//return true;
            	
            case CMD_SELECT_KEYBOARD:
            	if (getRegisterKbd(c) < 2) {
            		st.getStr(R.string.kbd_warning);
            		return true;
            	}
                if(ServiceJbKbd.inst!=null)
                    ServiceJbKbd.inst.forceHide();
                c.getApplicationContext().startActivity(new Intent(c,SetKbdActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .putExtra(SET_INTENT_LANG_NAME, getCurLang())
                                    .putExtra(SET_INTENT_ACTION, st.SET_SELECT_KEYBOARD)
                                    .putExtra(SET_SCREEN_TYPE, isLandscape(c())?2:1));
                com_menu.close();
                break;
                // запуск внешнего приложения
            case CMD_RUN_SKIN_SELECT:
                try{
                	if (getRegisterKbd(c) < 2) {
                		st.getStr(R.string.kbd_warning);
                		return true;
                	}
                    if(ServiceJbKbd.inst!=null)
                        ServiceJbKbd.inst.forceHide();
                    st.runSetKbd(c, st.SET_SELECT_SKIN);
//                    Intent in = new Intent(Intent.ACTION_VIEW)
//                    .setComponent(new ComponentName(c, SetKbdActivity.class))
//                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    .putExtra(st.SET_INTENT_ACTION, st.SET_SELECT_SKIN);
//                    c.startActivity(in);
                }
                catch(Throwable e)
                {
                }
            	com_menu.close();
            break;
            case CMD_INPUT_KEYBOARD:
                InputMethodManager imm = (InputMethodManager)c().getApplicationContext().getSystemService(Service.INPUT_METHOD_SERVICE);
				imm.showInputMethodPicker();
            break;
        	// запуск активности помощи (android:help)
            case CMD_RUN_MAINMENU_SETTING:
                if(ServiceJbKbd.inst!=null)
                    ServiceJbKbd.inst.forceHide();
                st.runAct(MainmenuAct.class,c);
            	com_menu.close();
            break;
            // запуск настройки отображения пунтов главного меню
            case CMD_HELP:
                st.runAct(Help.class,c);
            break;
            // запуск внешнего приложения
            case CMD_RUN_APP:
                st.runAct(Runapp.class,c);
            	com_menu.close();
            break;
            // запуск раскладки редактирования            
            case CMD_RUN_KBD_EDIT:
                st.setTextEditKeyboard();
            break;
            // запуск раскладки смайликов            
            case CMD_RUN_KBD_SMILE:
                st.setSmilesKeyboard();
            break;
         // запуск числовой раскладки            
            case CMD_RUN_KBD_NUM:
            	fl_qwerty_kbd = true;
                st.setNumberKeyboard();
            break;
         // запуск калькулятора
            case CMD_CALC:
            	new Templates(2,1);
            	com_menu.close();
//            	tpl.setDir(2,1);
                st.setCalcKeyboard();
            break;
            case CMD_CALC_HISTORY: return com_menu.showCalcHistory();
            case CMD_CALC_LIST: return com_menu.showCalcList();
            case CMD_CALC_SAVE:
            	new Templates(2,1).makeCommonMenu();
            	return true;
            case CMD_CALC_LOAD:
            	new Templates(2,2).makeCommonMenu();
            	return true;
            case GESTURE_ADDITIONAL_SYMBOL1:
            	popupAdditional(1);
            break;
            case GESTURE_AC_PLACE_LIST:
            	ServiceJbKbd.inst.m_candView.ViewCandList();
            break;
            case GESTURE_SPACE_QUANTITY:
            	float z = st.gesture_length / minGestSize;
            	st.fl_sound = false;
            	for  (int i = 0; i<= z;i++) {
                    ServiceJbKbd.inst.processKey(32);
            	}
            	st.fl_sound = true;
                ServiceJbKbd.inst.beep(32);
            break;
            case CMD_CLIPBOARD:
            	return com_menu.showClipboard(false);
            case CMD_AC_HIDE:
                if(ServiceJbKbd.inst!=null)
                    ServiceJbKbd.inst.viewAcPlace();
            	com_menu.close();
            break;
            default: 
                if(ServiceJbKbd.inst!=null)
                    ServiceJbKbd.inst.processKey(action);
                return true;
        }
        
        return false;
    }
/** Возвращает команду по текстовой метке */    
    static int getCmdByLabel(String label)
    {
        if(!label.startsWith(DRW_PREFIX))
        {
            if(label.equals("tab"))
                return 9;
            if(label.equals("opt"))
                return CMD_MAIN_MENU;
            return 0;
        }
        String l = label.substring(DRW_PREFIX.length());
        if(l.equals("vr"))
            return CMD_VOICE_RECOGNIZER;
        return 0;
    }
/** Возвращает id иконки по команде*/    
    static Bitmap getBitmapByCmd(int cmd)
    {
        int bid = 0;
        switch (cmd)
        {
            case CMD_VOICE_RECOGNIZER: bid = R.drawable.vr_small_white;
        }
        if(bid!=0)
            return BitmapFactory.decodeResource(st.c().getResources(), bid);
        return null;
    }
//    gPref = null;
//    public static final SharedPreferences pref()
//    {
//    	if(gPref == null) {
//    		String name = Prefs.getSharedPrefsName();
//    		if(name==null)
//    			gPref = PreferenceManager.getDefaultSharedPreferences(App.get());
//    		else
//    			gPref = App.get().getSharedPreferences(name,Context.MODE_PRIVATE);
//    	}
//    	return gPref;
//    }

    public static final SharedPreferences pref()
    {
    	// чтобы сохранялись переменные в настройках, нужно в классе в котором происходит сохранение указать конекст
    	// например AcColorAct = inst, и не забыть поправвить метод c()
        return pref(c());
    }
    public static final SharedPreferences pref(Context c)
    {
        return PreferenceManager.getDefaultSharedPreferences(c);
    }
 // перезагрузка настроек если клавиатура на экране
    static final void prefReload()
    {
        if (ServiceJbKbd.inst != null)
   			ServiceJbKbd.inst.onSharedPreferenceChanged(pref(), null);
    }
    static final KeyboardPaints paint()
    {
        if(KeyboardPaints.inst==null)
            return new KeyboardPaints();
        return KeyboardPaints.inst;
    }
// возвращает int с указанной системой счисления
 // radix - система счисления (2,8,10,16 и т.д.    
    public static int parseInt(String string,int radix) {
    	if (string.compareToIgnoreCase("ffffffff")==0)
    		string = "FFFFFFFE";
        int result = 0;
        int degree = 1;
        for(int i=string.length()-1;i>=0;i--)
        {
            int digit = Character.digit(string.charAt(i), radix);
            if (digit == -1) {
                continue;
            }
            result+=degree*digit;
            degree*=radix;
        }
        return result;
    }
    public static String num2str(int num,int radix) {
        return num2str(String.valueOf(num), radix);
    }
    // возвращает строку числа str в указанной системе счисления 
    public static String num2str(String str,int radix) {
    	int base = 0;
    	try{
    		base = Integer.valueOf(str);
    	} catch (NumberFormatException e) {
    		return st.STR_NULL;
    	}
    	if (str.startsWith("-"))
    		return st.STR_NULL+base; 
    	switch (radix)
    	{
    	case 2: return st.STR_NULL+Integer.toBinaryString(base);
    	case 8: return st.STR_NULL+Integer.toOctalString(base);
    	case 10: return st.STR_NULL+base;
    	case 16: return st.STR_NULL+Integer.toHexString(base);
    	}
        return st.STR_NULL;
    }
 // возвращает первое вхождение строки заключённой в конструции =[строка]    
    public static String rowInParentheses(String str) {
    	String out =st.STR_NULL;
    	int start = str.indexOf("=[");
    	int end = str.indexOf("]");
    	if (start>0&&end>0) {
    		out = str.substring(start+2,end);
    	} else {
    		toast("Error format");
    	}
        return out;
    }
    static final boolean isLandscape(Context c)
    {
        return c.getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT;
    }
    public static KbdDesign getSkinByPath(String path)
    {
    	boolean fl = false;
    	if (path.startsWith(CustomKbdDesign.ASSETS))
    		fl = true;
    	else if(path.startsWith(st.STR_SLASH))
    		fl = true;
    	if (fl)
        {
            for(int i=0;i<2;i++)
            {
                for(KbdDesign d:st.arDesign)
                {
                    if(path.equals(d.path)){
                        return d;
                    }
                }
                // Учтем возможность отключения карты и сделаем перезагрузку скинов
                CustomKbdDesign.loadCustomSkins();
            }
            path = STR_ZERO;
        }
        try{
            return arDesign[Integer.decode(path)];
        }
        catch (Throwable e) {
        }
        return arDesign[KBD_DESIGN_STANDARD];
    }
    public static String getSkinPath(KbdDesign kd)
    {
        if(kd.path!=null)
            return kd.path;
        int pos = 0;
        for(KbdDesign k:arDesign)
        {
            if(k==kd)
                return st.STR_NULL+pos;
            ++pos;
        }
        return STR_ZERO;
    }
/** Обновление настроек. Если у юзера старые настройки - меняем их на новые */    
    @SuppressWarnings("deprecation")
    public static void upgradeSettings(Context c)
    {
        SharedPreferences pref = st.pref(c);
        Editor ped = pref.edit();
// 0.91 - 0.92 Меняем индекс скина на путь к скину
        if(pref.contains(st.PREF_KEY_KBD_SKIN))
        {
            CustomKbdDesign.loadCustomSkins();
            int id = pref.getInt(st.PREF_KEY_KBD_SKIN, 0);
            if(st.arDesign.length>id&&id>=0)
            {
               ped.putString(st.PREF_KEY_KBD_SKIN_PATH, st.getSkinPath(st.arDesign[id]));
            }
            ped.remove(st.PREF_KEY_KBD_SKIN);
        }
 // 0.92 - 0.93 Меняем настройку высоты клавиш в пикселях на процент от экрана
        if(pref.contains(st.PREF_KEY_HEIGHT_PORTRAIT))
        {
            int ph = pref.getInt(st.PREF_KEY_HEIGHT_PORTRAIT,0);
            ped.remove(st.PREF_KEY_HEIGHT_PORTRAIT);
            ped.putFloat(st.PREF_KEY_HEIGHT_PORTRAIT_PERC, KeyboardPaints.pixelToPerc(c, true,ph));
        }
        if(pref.contains(st.PREF_KEY_HEIGHT_LANDSCAPE))
        {
            int ph = pref.getInt(st.PREF_KEY_HEIGHT_LANDSCAPE,0);
            ped.remove(st.PREF_KEY_HEIGHT_LANDSCAPE);
            ped.putFloat(st.PREF_KEY_HEIGHT_LANDSCAPE_PERC, KeyboardPaints.pixelToPerc(c,false, ph));
        }
// 0.92 - 0.93 Меняет тип вибрации (было вкл/выкл , стало - выкл/при нажатии/при отпускании)         
        if(pref.contains(st.PREF_KEY_VIBRO_SHORT_KEY))
        {
            boolean bVibroShort = pref.getBoolean(st.PREF_KEY_VIBRO_SHORT_KEY, false);
            String vt = bVibroShort?st.STR_ONE:st.STR_ZERO;
            ped.putString(st.PREF_KEY_USE_SHORT_VIBRO,vt);
            ped.remove(st.PREF_KEY_VIBRO_SHORT_KEY);
        }
 // 0.94 - 0.95 Меняем настройку "показ просмотра клавиш" на трёхпозиционную настройку PREF_KEY_PREVIEW_TYPE
        if(!pref.contains(PREF_KEY_PREVIEW_TYPE))
        {
            ped.putString(PREF_KEY_PREVIEW_TYPE, pref.getBoolean(PREF_KEY_PREVIEW, true)?STR_ONE:STR_ZERO);
        }
        ped.commit();
    }
    public static File[] getFilesByExt(File dir,final String ext)
    {
        return dir.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String filename)
            {
                int pos = filename.lastIndexOf('.');
                if(pos<0)return false;
                if(ext.length()>0&&ext.charAt(0)!='.')
                    pos++;
                return filename.substring(pos).compareTo(ext)==0;
            }
        });
    }
    public static CharSequence[] getGestureEntries(Context c)
    {
        CharSequence ret[] = new CharSequence[arGestures.size()];
        for(int i=0;i<ret.length;i++){
       		ret[i] = c.getText(arGestures.get(i).nameId);
        }
        return ret;
    }
    public static CharSequence[] getGestureEntryValues()
    {
        CharSequence ret[] = new CharSequence[arGestures.size()];
        for(int i=0;i<ret.length;i++)
            ret[i] = String.valueOf(arGestures.get(i).code);
        return ret;
    }
    public static int getGestureIndexBySetting(String set)
    {
        try{
            int code = Integer.decode(set);
            for(int i=0;i<arGestures.size();i++)
            {
                if(arGestures.get(i).code==code)
                    return i;
            }
        }catch (Throwable e) {
        }
        return 0;
    }
    public static KbdGesture getGesture(String prefName,SharedPreferences p)
    {
        return arGestures.get(getGestureIndexBySetting(p.getString(prefName, getGestureDefault(prefName))));
    }
    public static String getGestureDefault(String prefName)
    {
        return PREF_KEY_GESTURE_DOWN.equals(prefName)?STR_NULL+arGestures.get(9).code:STR_ZERO;
    }
    public static String compileText(String label)
    {
        for(int i=0;i<label.length();i++)
        {
            if(label.charAt(i)=='\\'&&i<label.length()-1)
            {
                char c = label.charAt(i+1);
                switch (c)
                {
                    case 'n':
                        c = '\n';
                        break;
                    case 't':
                        c = '\t';
                        break;
                    default:
                    break;
                }
                label = label.substring(0,i)+c+label.substring(i+2);
            }
        }
        return label;
    }
    public static String decompileText(String label)
    {
      	if (label == null)
      		return st.STR_NULL;
    	String out=st.STR_NULL;
       	for(int i=0;i<label.length();i++)
        {
       		char c= label.charAt(i);
            switch (c)
            {
            case '\n': out += "\\"+"n";break;
            case '\t': out += "\\"+"t";break;
            case '&': out+="&amp;";break;
            case '<': out+="&lt;";break;
            case '>': out+="&gt;";break;
            case '\"': out+="&quot;";break;
            case '\'': out+="\\'";break;
            case '\\': out+="\\"+"\\";break;
            default:
            	out += c;
            	break;
            }
        }
       	return out;
    }
    // запись вычисления для истории вычислений
    public static void save_calc_history(String txt)
    {
    	for (int i = 10; i > 0; i--) {
    		calc_history[i] = calc_history[i-1];
    	}
    	calc_history[0] = txt;
   	}
    // вывод сообщения длительностью 700мс
    public static void toast(String txt)
    {
        Context c = st.c();
        Toast.makeText(c, txt, 700).show();
   	}
    // вывод сообщения по id длительностью 700мс
    public static void toast(int id)
    {
        Context c = st.c();
        if (c!=null)
        	Toast.makeText(c, getStr(id), 700).show();
   	}
    public static void toast(Context c,int id)
    {
        Toast.makeText(c, c.getString(id), 700).show();
   	}
    public static void toast(boolean cr,Integer ...id)
    {
    	String out = st.STR_NULL;
        Context c = st.c();
        if (c==null)
        	return;
        for (int i=0; i<id.length;i++) {
        	if (cr)
        		out+=getStr(id[i])+st.STR_CR;
        	else
        		out+=getStr(id[i]);
        }
    	Toast.makeText(c, out, 700).show();
   	}
    public static void toastLong(int res_str)
    {
        Context c = st.c();
        st.toastLong(c.getString(res_str));
   	}
    // вывод длительного (>3сек) сообщения 
    public static void toastLong(String txt)
    {
        Context c = st.c();
        Toast.makeText(c, txt, Toast.LENGTH_LONG).show();
   	}
    
    public static void toastCopy()
    {
        if(android.os.Build.VERSION.SDK_INT <= 17)
    		st.toast(R.string.menu_copy);

    }
        public static void sleep(int ms)
        {
        	try {
        	Thread.sleep(ms); // спать sl милисекунд.
          } catch(Exception e){}    	        
       	}
        public static int str2int(String val, int min, int max, String msg)
        {
        	int i1=0;
        	try {
        		if (val.length()!=0)
        			i1 = Integer.valueOf(val);
        		if (i1>max)
        			i1=max;
        		if (i1<min)
        			i1=min;
        	}catch (NumberFormatException e) {
        		toast(msg+"\nerror format");
        	}  
            return i1;    	        
       	}
        public static int str2hex(String sss, int radix)
        {
        	boolean bbb = true;
        	if (sss.length() == 0) {
//        		st.toast("Empty.");
        		return 0;
        	}
        	sss = sss.toUpperCase();
        	int i1=0;
        	try {
        		if (sss.startsWith("0X")) {
        			i1 = st.parseInt(sss.substring(2),radix);
        			bbb = false;
        		}
        		else if (sss.startsWith("#")) {
        			i1 = st.parseInt(sss.substring(1),radix);
        			bbb = false;
        		}
        		 if (bbb) {
        			i1 = st.parseInt(sss.trim(),radix);
        		}
//        		i1 = Integer.parseInt(sss.replace("#",st.STR_NULL),16);
        	}catch (NumberFormatException e) {
        		toast("Error numeric format");
        		i1 = -1;
        	}
            return i1;    	        
       	}
// проверяем правильность ввода 16-тиричного формата
//xaarrggbb или #aarrggbb
        public static boolean checkHexValue(String val, int radix)
        {
        	String val1 = st.STR_NULL;
        	boolean fl = false;
        	if (val.startsWith("0x")){
        		val1 = val.substring(2);
        		fl = true;
        	}
        	else if (val.startsWith("#")){
        		val1 = val.substring(1);
        		fl = true;
        	}
        	if (fl){
        		if (val1.length() != 8){
        			return false;
        		}
        		fl = true;
        	} else 
        		return false;
        	fl = false;
        	for (int i=0;i<val1.length();i++){
                switch (val1.toLowerCase().charAt(i))
                {
                case '0': continue;
                case '1': continue;
                case '2': continue;
                case '3': continue;
                case '4': continue;
                case '5': continue;
                case '6': continue;
                case '7': continue;
                case '8': continue;
                case '9': continue;
                case 'a': continue;
                case 'b': continue;
                case 'c': continue;
                case 'd': continue;
                case 'e': continue;
                case 'f': continue;
                default:
                	fl = true;
                	break;
                }
        	}
        	if (fl)
        		return false;
        	int ch = st.str2hex(val, radix);
        	if (ch == -1)
        		return false;
        	return true;
        }
// дополняет строку пробелами до указанной длины
// pos - где добавлять пробелы. true - вначале
// len - до какой длины нужна строка
     // symb - каким символом заполнять
        public static String lenstr(String sss, int len, String symb, boolean pos)
        {
          	while (sss.length()<len)
           	{
           		if (pos)
           			sss=symb+sss;
           		else
           			sss = sss + symb;
           	}
            return sss;    	        
       	}
     // выводит строки popupcharacter для жестов доп. символы
        public static void popupAdditional(int num)
        {
        	CharSequence out=st.STR_NULL;
    		CharSequence popupcharacter_save=st.STR_NULL;
// подбираем клавишу к которой привязываемся
// если нет - ищем следующую, иначе сообщаем об невозможности выполнить жест        	
        	LatinKey lk = null;
//enter
        	lk = st.curKbd().getKeyByCode(10);
//123
        	if (lk==null)
        		lk = st.curKbd().getKeyByCode(-2);
// shift
        	if (lk==null)
        		lk = st.curKbd().getKeyByCode(-1);
        	if (lk==null)
        		lk = st.curKbd().getKeyByCode(-561);
        	if (lk != null) {
                switch (num)
                {
                case 1: out = gesture_str;break;
                default:
                	break;
                }
                popupcharacter_save = lk.popupCharacters;
        		lk.popupCharacters=out;
        		lk.popupResId = R.xml.kbd_empty;
        		OwnKeyboardHandler.inst.m_kv.onLongPress(lk);
        		st.sleep(100);
                lk.popupCharacters = popupcharacter_save;
        	} else 
        		toast("error gesture. Not a \"shift\", \"enter\", or \"123\" key on layout");
       	}
    public static String getCalcCommandText(int command)
    {
    	String out =st.STR_NULL;
        switch (command)
        {
        case 0: out =st.STR_ZERO;break;
        case 1: out ="1";break;
        case 2: out ="2";break;
        case 3: out ="3";break;
        case 4: out ="4";break;
        case 5: out ="5";break;
        case 6: out ="6";break;
        case 7: out ="7";break;
        case 8: out ="8";break;
        case 9: out ="9";break;
        case 10: out ="*";break;
        case 11: out ="➗";break;
        case 12: out ="+";break;
        case 13: out ="-";break;
        case 14: out ="Cx";break;
        case 15: out ="B↑";break;
        case 16: out ="x⇄y";break;
        case 17: out ="/-/";break;
        case 18: out ="[.]";break;
        case 19: out ="NOP";break;
        case 20: out ="[x²]";break;
        case 21: out ="[²√]";break;
//        case 22: out ="Step left";break;
//        case 23: out ="Step right";break;
        case 24: out ="[%]";break;
        case 25: out ="[M]";break;
        case 26: out ="[MR]";break;
        case 27: out ="[MC]";break;
        case 28: out ="[Пх]";break;
        case 29: out ="[ИПх]";break;
        case 30: out ="[в/о]";break;
//        case 31: out ="AUT ";break;
//        case 32: out ="PRG ";break;
        case 33: out ="[clr]";break;
        case 34: out ="[с/п]";break;
        case 35: out ="[БП]";break;

        case 42: out ="[π]";break;
        case 43: out ="sin";break;
        case 44: out ="cos";break;
        case 45: out ="tan";break;
        case 46: out ="asin";break;
        case 47: out ="acos";break;
        case 48: out ="atan";break;
        case 49: out ="[1/x]";break;
        case 50: out ="[x^y]";break;
        case 51: out ="[LOG]";break;
        case 52: out ="[rnd]";break;
        case 53: out ="[int]";break;
        case 54: out ="LG10";break;
        case 55: out ="[°→r]";break;
        case 56: out ="[к→°]";break;
        case 57: out ="round";break;
        case 58: out ="[e]";break;
        case 59: out ="[e^x]";break;
        case 60: out ="[n!]";break;
        case 61: out ="[ПП]";break;
        case 62: out ="[x≥0]";break;
        case 63: out ="[x<0]";break;
        case 64: out ="[x=0]";break;
        case 65: out ="[x≠0]";break;
        case 66: out ="[x≥y]";break;
        case 67: out ="[x<y]";break;
        case 68: out ="[x=y]";break;
        case 69: out ="[x≠y]";break;
        case 70: out ="[T↺X]";break;
        default:
        	out = "err";
        	break;
        }
        while (out.length()<4) {
        	out += st.STR_SPACE;
        }
    	return out;
  	}
    // возвращает числовой массив в строку через запятую
    public static String intToStr(ArrayList<Integer> ar)
    {
    	String str = st.STR_NULL;
       	for(int i=0;i<ar.size();i++)
        {
       		if(ar.get(i)!=0){
       			str += String.valueOf(ar.get(i))+",";
       		}
        }
       	return str;
   	}
    // есть ли программа в калькуляторе
    public static boolean isCalcPrg()
    {
       	for(int i=0;i<st.calc_prog.length;i++)
        {
       		if(st.calc_prog[i]!=-1){
       			return true;
       		}
        }
       	return false;
   	}
    // очистка программы калькулятора
    public static void calcClear()
    {
		for (int i = 0; i <= 999; i++ ) {
			st.calc_prog[i]=-1;
				}
		for (int i=0;i<st.calc_pp.length;i++){
			st.calc_pp[i]=0;
		}
   	}
    // установки параметров для запуска активности desc_act
    // type - тип окна, чего выводим
    // метод должен вызываться до старта активности 
    public static void desc_act_ini(int type)
    {
        switch (type)
        {
    	case 0: // вывод desc_kbd.txt при запуске настроек
        	desc_file_input = FILE_INPUT_FOR_VIEW;
        	desc_view_input = 0;
        	desc_fl_input = false;
        	break;
    	case 1: // вывод desc_kbd.txt при запуске "как пользоваться клавиатурой"
        	desc_file_input = FILE_INPUT_FOR_VIEW;
        	desc_view_input = 1;
        	desc_fl_input = true;
        	break;
    	case 2: // вывод _diary.txt
        	desc_file_input = FILE_DIARY;
        	desc_view_input = 2;
        	desc_fl_input = false;
        	break;
        }
   	}
    public static String returnZamok(boolean b)
    {
    	if (b)
    		return "🔒";
		return "🔓";
    }
// значения keyevent для ctrl и alt   
    public static int returnCtrlAltCode(int primaryCode)
    {
    	int ret = 0;
		String str = String.valueOf(primaryCode);
    	str=str.toUpperCase();
    	int ret1 = Integer.valueOf(str.charAt(0));
        switch (ret1)
        {
        case 1: ret = primaryCode;break;
        default:
        	ret = primaryCode;
        	break;
        }
		return ret;
    }
    public static void showkbd()
    {
    	if (ServiceJbKbd.inst!=null)
    		ServiceJbKbd.inst.forceShow();
    }
    public static void showkbd(final EditText et)
    {
       (new Handler()).postDelayed(new Runnable() {

			public void run() {

                et.requestFocus();
                et.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
                et.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));                       
                if (et !=null)
                	et.setSelection(et.getText().toString().length());
            }
        }, 200);
    }    
    public static void hidekbd()
    {
    	if (ServiceJbKbd.inst!=null)
    		ServiceJbKbd.inst.forceHide();
    }
    public static String getStr(int id)
    {
    	if (ServiceJbKbd.inst!=null&id!=0)
    		return ServiceJbKbd.inst.getString(id);
    	return st.STR_NULL;
    }
    public static float screenDensity(Context c)
    {
        return c.getResources().getDisplayMetrics().density;
    }
    public static float floatDp(float value,Context c)
    {
        return value*screenDensity(c);
    }
    public static int getWidthDisplay()
    {
    	return c().getResources().getDisplayMetrics().widthPixels;
    }
    public static int getHeightDisplay()
    {
        // height текущего окна
        //int displayh = getResources().getConfiguration().screenHeightDp;
    	// width получаем также
    	return c().getResources().getDisplayMetrics().heightPixels;
    }
    public static int getOrientation(Context c)
    {
    	/*int orient = getResources().getConfiguration().orientation;

		switch (orient) {
		case Configuration.ORIENTATION_PORTRAIT:
			size = (int) (measureHeight * port);

			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			size = (int) (measureHeight * land);
			break;
		}*/
    	return c.getResources().getConfiguration().orientation;
    }
    // запуск устакновленного приложения,
    // если оно не установлено то запуск маркета на установку
    public static void runApp(Context c, String packageName)
    {
    	Intent intent = null;
    	try {
			PackageInfo pi = c.getPackageManager().getPackageInfo(packageName, 0);
	    	if (pi!= null){
				  intent = c.getPackageManager().getLaunchIntentForPackage(packageName);
				  c.startActivity(intent);
	        	} else {
		        	intent = new Intent(Intent.ACTION_VIEW);
		            intent.setData(Uri.parse(RUN_MARKET_STRING+packageName));
		            c.startActivity(intent);
	        	}
		} catch (NameNotFoundException e) {
        	intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(RUN_MARKET_STRING+packageName));
            c.startActivity(intent);
		}        
   	}
// возвращает строку описания жеста или символ назначенный
// на клавишу    
    public static String getKeyDesc(int key)
    {
    	String ret = st.STR_NULL;
        switch (key)
        {
    	case 0: 
        	break;
    	case 1: 
        	break;
    	case 2: 
        	break;
        }
    	return ret;
   	}
    public static void help(int id_txt) 
    {
    	help(st.c().getString(id_txt));
    }
    // показывает окно GlobDialog на экране с одной кнопкой Ок
    public static void help(String txt) 
    {
    	if (GlobDialog.fl_help)
    		return;
        GlobDialog gd = new GlobDialog(st.c());
        gd.setGravityText(Gravity.LEFT|Gravity.TOP);
        gd.set(txt, R.string.ok, 0);
        gd.fl_help=true;
        gd.setObserver(new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
            	GlobDialog.fl_help = false;
                return 0;
            }
        });
        gd.showAlert();
    }
    // установлен ли пакет packageName на гаджете
    public static boolean isAppInstalled(Context c, String packageName) {
        PackageManager pm = c.getPackageManager();
        boolean installed = false;
        try {
           pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
           installed = true;
        } catch (PackageManager.NameNotFoundException e) {
           installed = false;
        }
        return installed; 
    }
    // удаляет символы del_symb входящие в строку in
    public static String getDelSpace(String in, String del_symb) 
    {
    	if (in.indexOf(del_symb)<=0)
    		return in;
    	String out = st.STR_NULL;
    	String[] ar = in.split(del_symb);
    	for (int i=0;i<ar.length;i++){
    		out +=ar[i];
    	}
    	return out;
    }
    /** true - число чётное */
    public static boolean isEven(int x)
    {
    	return (x%2) == 0;
    }
    // запасной метод обработки клавиш громкости для
    // вызова из GlobDialog
    public static void processVolumeKey(int keycode, boolean down)
    {
    	if(ServiceJbKbd.inst!=null
    			&&ServiceJbKbd.inst.m_volumeKeys>0
    			)
    		ServiceJbKbd.inst.processVolumeKey(keycode, down);
    }
    // аналог вызова метода runsetkbd из jbkPreferences для настройки высоты клавиатуры
    public static void runSetKbd(Context c, int action)
    {
        try{
//        	if (registerKbd() < 2) {
//        		st.toast(getString(R.string.kbd_warning));
//        		return;
//        	}
            Intent in = new Intent(Intent.ACTION_VIEW)
            .setComponent(new ComponentName(c, SetKbdActivity.class))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(st.SET_INTENT_ACTION, action);
            c.startActivity(in);
        }
        catch(Throwable e)
        {
        }

    }
    public static void messageCopyClipboard()
    {
        if(st.fl_copy_toast){
    		st.toast(st.getStr(R.string.menu_copy));
        }
   	}
    public static class KbdGesture
    {
        public KbdGesture(int name,int cod)
        {
            nameId = name;
            code = cod;
        }
        int nameId;
        int code;
    }
 // доступные жесты, название и команда обработки    
    public static void getGestureAll()
    {
    	if (!arGestures.isEmpty())
    		arGestures.clear();
    	arGestures.add(new KbdGesture(R.string.cmd_none, 0));
    	arGestures.add(new KbdGesture(R.string.gesture_popupchar1, GESTURE_ADDITIONAL_SYMBOL1));
    	arGestures.add(new KbdGesture(R.string.gesture_cand_list, GESTURE_AC_PLACE_LIST));
    	arGestures.add(new KbdGesture(R.string.cmd_lang_change, CMD_LANG_CHANGE));
    	arGestures.add(new KbdGesture(R.string.cmd_left, KeyEvent.KEYCODE_DPAD_LEFT));
    	arGestures.add(new KbdGesture(R.string.cmd_right, KeyEvent.KEYCODE_DPAD_RIGHT));
    	arGestures.add(new KbdGesture(R.string.cmd_up, KeyEvent.KEYCODE_DPAD_UP));
    	arGestures.add(new KbdGesture(R.string.cmd_down, KeyEvent.KEYCODE_DPAD_DOWN));
    	arGestures.add(new KbdGesture(R.string.mm_multiclipboard, CMD_CLIPBOARD));
    	arGestures.add(new KbdGesture(R.string.mm_templates, CMD_TPL));
    	arGestures.add(new KbdGesture(R.string.mm_settings, CMD_PREFERENCES));
    	arGestures.add(new KbdGesture(R.string.cmd_close, Keyboard.KEYCODE_CANCEL));
    	arGestures.add(new KbdGesture(R.string.cmd_space, 32));
    	arGestures.add(new KbdGesture(R.string.gesture_space_quantity, GESTURE_SPACE_QUANTITY));
    	arGestures.add(new KbdGesture(R.string.cmd_enter, 10));
    	arGestures.add(new KbdGesture(R.string.cmd_bs, Keyboard.KEYCODE_DELETE));
    	arGestures.add(new KbdGesture(R.string.gesture_delword, TXT_ED_DEL_WORD));
    	arGestures.add(new KbdGesture(R.string.gesture_del, TXT_ED_DEL));
    	arGestures.add(new KbdGesture(R.string.cmd_shift, Keyboard.KEYCODE_SHIFT));
       	arGestures.add(new KbdGesture(R.string.gesture_select, TXT_ED_SELECT));
    	arGestures.add(new KbdGesture(R.string.gesture_select_all, TXT_ED_SELECT_ALL));
    	arGestures.add(new KbdGesture(R.string.mm_runapp, CMD_RUN_APP));
    	arGestures.add(new KbdGesture(R.string.gesture_to_start, TXT_ED_START));
    	arGestures.add(new KbdGesture(R.string.gesture_to_end, TXT_ED_FINISH));
    	arGestures.add(new KbdGesture(R.string.gesture_begin, TXT_ED_HOME));
    	arGestures.add(new KbdGesture(R.string.gesture_end, TXT_ED_END));
    	arGestures.add(new KbdGesture(R.string.gesture_homestr, TXT_ED_HOME_STR));
    	arGestures.add(new KbdGesture(R.string.gesture_endstr, TXT_ED_END_STR));
    	arGestures.add(new KbdGesture(R.string.gesture_mainmenu, CMD_MAIN_MENU));
    	arGestures.add(new KbdGesture(R.string.gesture_d_vr, CMD_VOICE_RECOGNIZER));
    	arGestures.add(new KbdGesture(R.string.lang_calc, CMD_CALC));
    	arGestures.add(new KbdGesture(R.string.lang_edittext, CMD_RUN_KBD_EDIT));
    	arGestures.add(new KbdGesture(R.string.lang_smiles, CMD_RUN_KBD_SMILE));
    	arGestures.add(new KbdGesture(R.string.lang_numbers, CMD_RUN_KBD_NUM));
    	arGestures.add(new KbdGesture(R.string.lang_symbol_selector, CMD_RUN_KBD_SYMBOL));
    	arGestures.add(new KbdGesture(R.string.gesture_cut, TXT_ED_CUT));
    	arGestures.add(new KbdGesture(R.string.gesture_copy, TXT_ED_COPY));
    	arGestures.add(new KbdGesture(R.string.gesture_paste, TXT_ED_PASTE));
    	arGestures.add(new KbdGesture(R.string.mm_ac_hide0, CMD_AC_HIDE));
    	arGestures.add(new KbdGesture(R.string.set_keyboard_height, CMD_HEIGHT_KEYBOARD));
    	arGestures.add(new KbdGesture(R.string.gesture_upcase, CMD_SYMBOL_UP_CASE));
    	arGestures.add(new KbdGesture(R.string.gesture_lowercase, CMD_SYMBOL_LOWER_CASE));
    	arGestures.add(new KbdGesture(R.string.gesture_long_text, CMD_INPUT_LONG_GESTURE));
    	arGestures.add(new KbdGesture(R.string.set_key_landscape_input, CMD_FULL_DISPLAY_EDIT));
    	arGestures.add(new KbdGesture(R.string.gesture_select_share, CMD_SHARE_SELECTED));
    	arGestures.add(new KbdGesture(R.string.gesture_trans_sel, CMD_TRANSLATE_SELECTED));
       	arGestures.add(new KbdGesture(R.string.gesture_trans_copy, CMD_TRANSLATE_COPYING));
       	arGestures.add(new KbdGesture(R.string.gesture_search_sel, CMD_SEARCH_SELECTED));
       	arGestures.add(new KbdGesture(R.string.gesture_search_copy, CMD_SEARCH_COPYING));
       	arGestures.add(new KbdGesture(R.string.euv_actname, CMD_EDIT_USER_VOCAB));
           }
// запускает сервис синхронизации мультибуфера
    public static void startSyncServise()
    {
		if (ClipbrdSyncService.inst!=null)
			return;
		if (ServiceJbKbd.inst!=null)
			new ClipbrdSyncService(ServiceJbKbd.inst);
		st.fl_sync = true;
    }
// останавливает сервис синхронизации мультибуфера
    public static void stopSyncServise()
    {
		if (ClipbrdSyncService.inst!=null)
    		if (ServiceJbKbd.inst!=null)
    			ClipbrdSyncService.inst.delete(ServiceJbKbd.inst);
		st.fl_sync = false;
    }
    // ИСПОЛЬЗУЕТСЯ В ДВУХ МЕСТАХ!
    // если запущено на эмуляторе (значит ведётся отладка 
    // и отчёт о падении выводить не надо)
    public static boolean isDebugEmulator()
    {
    	// запущено на genymotion
        if (Build.VERSION.INCREMENTAL.contains("genymo"))
           	return true;
    	return false;
    }
    // преобразует строку - первый сивол заглавный
    public static String upFirstSymbol(String in)
    {
    	if (in.length()>0)
    		in = Character.toUpperCase(in.charAt(0))+in.substring(1);
    	return in;
    }
    public static void createFolderApp()
    {
    	String pt = st.STR_NULL;
    	File f=null;
        try{
        	pt = st.getSettingsPath()+CustomKeyboard.KEYBOARD_FOLDER;
            f = new File(pt);
            if(!f.exists())
            {
                f.mkdirs();
            }
        	pt = st.getSettingsPath()+CustomKbdDesign.FOLDER_SKINS;
            f = new File(pt);
            if(!f.exists())
            {
                f.mkdirs();
            }
        	pt = st.getSettingsPath()+Templates.FOLDER_TEMPLATES;
            f = new File(pt);
            if(!f.exists())
            {
                f.mkdirs();
            }
        	pt = st.getSettingsPath()+Templates.FOLDER_CALC;
            f = new File(pt);
            if(!f.exists())
            {
                f.mkdirs();
            }
        	pt = st.getSettingsPath()+ClipbrdSyncService.SAVEDIR;
            f = new File(pt);
            if(!f.exists())
            {
                f.mkdirs();
            }
        	pt = st.getSettingsPath()+WordsService.DEF_PATH;
            f = new File(pt);
            if(!f.exists())
            {
                f.mkdirs();
            }
        }
        catch(Throwable e)
        {
        }

    }
    // если андроид меньше 3, то true
    public static boolean isHoneycomb()
    {
    	if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
    		return false;
		return true;
    }
 // возвращает язык программы по умолчанию в зависимости от системного    
    public static String getSystemLangApp()
    {
    	String out = Locale.getDefault().getDisplayCountry();//.getLanguage();
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
       	  ||out.contains("cv")
       	  ||out.contains("et")
   		  )
   			out = "ru";
		else if (out.contains("en"))
			out = "en";
		else if (out.contains("es"))
			out = "es";
		else if (out.contains("uk"))
			out = "uk";
		else
			out = "en";
    	return out;
    }
    // возвращает строку с номером текущей версии
    public static String getAppVersionCode(Context c)
    {
		try {
				return st.STR_NULL+c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionCode;
//		} catch (NameNotFoundException e) {}
		} catch (Throwable e) {}
		return st.STR_ZERO;
    }
    // возвращает строку с названием текущей версии
    public static String getAppVersionName(Context c)
    {
		try {
				return st.STR_NULL+c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {}
		return st.STR_ZERO;
    }
    public static String getPackageName(Context c)
    {
		try {
				return st.STR_NULL+c.getPackageManager().getPackageInfo(c.getPackageName(), 0).packageName;
		} catch (Throwable e) {}
		return st.STR_ZERO;
    }
 // проверка регистрации клавиатуры
 // возвращает 
 //0 - нет регистрации, 
 //1 - клавиатура отключена, 
 //2 - клавиатура не активна      
      public static int getRegisterKbd(Context c)
      {
          InputMethodManager imm = (InputMethodManager)c.getSystemService(Service.INPUT_METHOD_SERVICE);
          String pn = c.getPackageName();
          List<InputMethodInfo> imlist =  imm.getEnabledInputMethodList();
          int step = 0;
          String curId = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
  // Проверяем регистрацию в InputMethodManager        
          for(InputMethodInfo ii:imlist)
          {
              if(pn.equals(ii.getPackageName()))
              {
                  step = 1;
                  if(ii.getId().equals(curId))
                      step =2;
              }
          }

      	return step;
     	}
      // поделиться выделенным
      public static void sendShareTextIntent(Context c)
      {
      	if (ServiceJbKbd.inst==null)
      		return;
          InputConnection ic = ServiceJbKbd.inst.getCurrentInputConnection();
          String txt = ic.getSelectedText(0).toString();
          st.sendShareTextIntent(c, txt);
     }
     // поделиться
      public static void sendShareTextIntent(Context c, String txt)
      {
      	if (ServiceJbKbd.inst==null)
       		return;
      	Intent sendIntent = new Intent();
      	sendIntent.setAction(Intent.ACTION_SEND);
      	sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      	sendIntent.putExtra(Intent.EXTRA_TEXT, txt);
      	sendIntent.setType("text/plain");
      	c.startActivity(sendIntent);
      	ServiceJbKbd.inst.stickyOff(st.TXT_ED_SELECT);
      	st.toast(R.string.send_share_create_list);
     }
      // возвращает исходное число в форматированном виде (b, kb, mb, gb и tb)
     public static String getLengthOfString(long val)
     {
    	 if(val <= 0) 
    		 return st.STR_ZERO;
    	 if (val > 10995116277760l)
    		 return ">10Tb";
    	 final String[] units = new String[] { "b", "kb", "Mb", "Gb", "Tb" };
    	 int digitGroups = (int) (Math.log10(val)/Math.log10(1024));
    	 return new DecimalFormat("#,##0.#").format(val/Math.pow(1024, digitGroups)) + "" + units[digitGroups];
     }
     public static WordArray getWordArrayElementById(int id, ArrayList<WordArray> ar) {
     	for (WordArray wa:ar) {
     		if (wa.id == id)
     			return wa;
     	}
     	return null;
     }

}