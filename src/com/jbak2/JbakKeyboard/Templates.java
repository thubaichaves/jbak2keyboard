package com.jbak2.JbakKeyboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import com.jbak2.ctrl.GlobDialog;

import android.app.AlertDialog;
import android.view.inputmethod.InputConnection;

/** Класс для операций с шаблонами - создание, листинг, обработка */
public class Templates
{
/** Статический объект для доступа к экземпляру класса */    
    static Templates inst;
    public static com_menu menu = null;
    File m_editFile=null;
/** Обнуляет текущий объект */    
    static void destroy()
    {
        if(inst!=null)
            inst = null;
    }
/** Конструктор. Инициализирует rootDir */  
    Templates(int rej, int typ)
    {
        inst = this;
        rejim = rej;
        type = typ;
        setDir(rejim,type);
        String rd = st.getSettingsPath()+template_path;
        m_rootDir = new File(rd);
        if(!m_rootDir.exists())
        {
            if(!m_rootDir.mkdirs())
                m_rootDir = null;
        }
        m_curDir = m_rootDir;
    }
/** Устанавливает редактирования папки шаблонов - для запуска {@link TplEditorActivity}*/    
    void setEditFolder(boolean bSet)
    {
        if(bSet)
        {
            m_state|=STAT_EDIT_FOLDER;
        }
        else
        {
            m_state = st.rem(m_state, STAT_EDIT_FOLDER);
        }
    }
    boolean openFolder(File f)
    {
        m_curDir = f.getAbsoluteFile();
        makeCommonMenu();
        return true;
    }
    boolean isEditFolder()
    {
        return st.has(m_state, STAT_EDIT_FOLDER);
    }
/** Юзер отменил редактирование шаблона */
    void onCloseEditor()
    {
        setEditFolder(false);
        setEditTpl(null);
// !!! ПОКАЗЫВАЕТ ОКНО КЛАВИАТУРЫ        
        ServiceJbKbd.inst.showWindow(true);
        Templates.inst.makeCommonMenu();
    }
    void onDelete()
    {
        if(m_editFile==null)return;
        if(isEditFolder())
            deleteDir(m_editFile);
        else
            m_editFile.delete();
    }
/** Если m_editFile!=null - переименовывает эту папку в name.<br>
 *  Иначе создаёт новую папку с именем name */  
    void saveFolder(String name)
    {
        String fpath = m_curDir.getAbsolutePath()+File.separator+name;
        File f = new File(fpath);
        if(m_editFile!=null)
        {
            m_editFile.renameTo(f);
        }
        else
        {
            f.mkdirs();
        }
    }
/** Сохраняет шаблон с названием name и текстом text */ 
    void saveTemplate(String name,String text)
    {
        fpath = m_curDir.getAbsolutePath()+File.separator+name;
        try{
            if(m_editFile!=null&&rejim != 2)
            {
                if(!m_editFile.delete())
                {
                    return;
                }
            }
            if (rejim == 1) {
                File f = new File(fpath);
                FileOutputStream os = new FileOutputStream(f);
            	os.write(text.getBytes());
                os.close();
            }
            else if (rejim == 2) {
        		if (st.isCalcPrg() == false){
        			st.toast(st.c().getString(R.string.calc_prog_empty));
        		} else {
                    addCalc();
                    File f = new File(fpath);
	        		st.calc_prg_desc = text;
    	        	if (f.exists()) {
    	        		calcSavePrgQuery();
    	        	} else {
    	        		calcSavePrg();
    	        	}
        		}
            }
        }
        catch(Throwable e)
        {
        }
    }
    void calcSavePrg()
    {
     	try {
        	addCalc();
            File f = new File(fpath);
            if (f.exists())
            	f.delete();
        	FileWriter writer = new FileWriter(fpath, false);
        	writer.write(st.CALC_PRG_VERSION+st.STR_CR);
        	writer.write(st.CALC_PRG_DESC_WORD+st.STR_CR);
        	writer.write(st.calc_prg_desc+st.STR_CR);
        	writer.write(st.CALC_PROGRAM_WORD+st.STR_CR);
            String out = st.STR_NULL;
        	for (int i=0;i< st.calc_prog.length;i++){
        		out += String.valueOf(st.calc_prog[i])+",";
        	}
        	writer.write(out+st.STR_CR);
        	writer.close();
//        	int f = ServiceJbKbd.inst.getParSleepValue();
//        	st.sleep(f);
     	}
     	catch(IOException ex){
     	}
    }
/** Устанавливает файл шаблона для редактирования в редакторе шаблона. null - для нового шаблона*/  
    void setEditTpl(File f)
    {
        m_editFile = f;
    }
/** Класс для сравнения двух файлов, используется в сортировке */   
    static class FilesComp implements Comparator<File>
    {
        @Override
        public int compare(File object1, File object2)
        {
            boolean bDir1 = object1.isDirectory(), bDir2 = object2.isDirectory(); 
            if(bDir1&&!bDir2)
                return -1;
            else if(!bDir1&&bDir2)
                return 1;
            return object1.getName().compareToIgnoreCase(object2.getName());
        }
    }
    
/** Возвращает массив отсортированных файлов из текущей папки шаблонов */   
    ArrayList<File> getSortedFiles()
    {
        ArrayList<File> ar = new ArrayList<File>();
        try{
            File af[] = m_curDir.listFiles();
            for(int i=0;i<af.length;i++)
            {
                ar.add(af[i]);
            }
            Collections.sort(ar, new FilesComp());
            return ar;
        }
        catch (Throwable e) {
        }
        return null;
    }
/** Возвращает строку из файла f или null, если произошла ошибка<br>
*@param f Файл для чтения, текст должен быть в кодировке UTF-8
*@return Содержимое файла или null
 */
    static String getFileString(File f)
    {
        try{
            FileInputStream fi = new FileInputStream(f);
            byte buf[] = new byte[(int)f.length()];
            fi.read(buf);
            int start = 0;
            if(buf.length>3&&buf[0]==0xef&&buf[1]==0xbb&&buf[2]==0xbf)
            {
                start = 3;
            }
            fi.close();
            return new String(buf, start, buf.length-start);
        }
        catch(Throwable e)
        {
        }
        return null;
    }
// возвращает описание программы калькулятора
    String getCalcPrgDesc(File f)
    {
     	try {
     		String fs = m_curDir + st.STR_SLASH + f.getName();
        	FileReader fr= new FileReader(fs);
         	Scanner sc = new Scanner(fr);
         	sc.useLocale(Locale.US);
            st.calc_prg_desc = st.STR_NULL;
        	String str = st.STR_NULL;
        	String out = st.STR_NULL;
       		str = sc.nextLine();
       		if (str.trim().equals(st.CALC_PRG_VERSION) == false) {
           		sc.close();
       			return st.STR_NULL;
         	}
       		boolean fl = false;
// алгоритм должен быть именно такой!
       		while (sc.hasNextLine()) {
          		str = sc.nextLine();
         		if (str.equals(st.CALC_PROGRAM_WORD)) {
         			fl = false;
         		}
          		if (fl){
          			if (str.length()>0)
          				out+=str;
          			if (out.length()>0&&out.endsWith(st.STR_SPACE) == false)
          				out+=st.STR_SPACE;
          		}
         		if (str.equals(st.CALC_PRG_DESC_WORD)) {
         			fl = true;
         		}
       		}
       		sc.close();
       		return out;
     	}
     	catch(IOException ex){
     	}
        return st.STR_NULL;
    }
/** Выполняет шаблон s в текущем сервисе */ 
    @SuppressWarnings("deprecation")
	void processTemplate(String s)
    {
        if(s==null)
            return;
        int del = 0;
        int pos = 0;
        int len = s.length();
        CurInput ci = new CurInput();
        InputConnection ic = ServiceJbKbd.inst.getCurrentInputConnection();
        String ss = st.STR_NULL;
        while(true)
        {
            int f = s.indexOf(TPL_SPEC_CHAR, pos);
            if(f<0||f==len-1)
                break;
            if(s.charAt(f+1)==TPL_SPEC_CHAR)
            {
                pos = f+2;
                continue;
            }
            // обработка специнструкций
            boolean bFound = false;
            for(int i=0;i<Instructions.length;i++)
            {
                ss = Instructions[i];
                int ff = s.indexOf(ss, f+1); 
                if(ff==f+1)
                {
                    bFound = true;
                    if(!ci.isInited())
                    {
                        ci.init(ic);
                        ic.beginBatchEdit();
                    }
                	String r1 = st.STR_NULL;
                    String repl = ci.sel;
                	String strformat = st.STR_NULL;
                	String out = st.STR_NULL;
                	boolean fl = false;
                	String sy = st.STR_NULL;
                	char ch = 0;
                    switch(i)
                    {
                        case 0:  break;
                        case 1: 
                            if(repl.length()==0)
                            {
                                if(del==0)
                                    del=IB_WORD;
                                repl = ci.getWordText();
                            }break;
                        case 2: 
                        	if(repl.length()==0)
                            {
                                del = IB_LINE;
                                repl = ci.getLineText(); break;
                            }break;
                        case 3:
                        	String repl2 = st.STR_NULL;
                        	String sub = s.substring(s.indexOf(Instructions[i])+Instructions[i].length());
                        	if (sub.startsWith("[")){
                            	int pos_skob = sub.indexOf("]");
                        		if (pos_skob>1){
                        			try {
                        				strformat = sub.substring(1, pos_skob);
                        				SimpleDateFormat sdf = new SimpleDateFormat(strformat);
                        				repl2 = sdf.format(new Date());
                        			} catch(Throwable e) {
                        				st.toast("format error");
                        				return;
                        			}
                        			int pp = s.indexOf("["+strformat+"]");
                        			s=s.substring(0, pp)+ s.substring(pp+("["+strformat+"]").length(), s.length());
                        		}else
                        			repl2 = java.util.Calendar.getInstance().getTime().toLocaleString().toString();
                        	} else {
                        		repl2 = java.util.Calendar.getInstance().getTime().toLocaleString().toString();
                        	}
                        	if (repl.length()!=0)
                        		repl = repl2;
                        	else
                        		ServiceJbKbd.inst.setWord(repl2,false);
                        		
                		break;
                        case 4:
                        	repl =repl.toLowerCase();
                		break;
                        case 5:
                        	repl =repl.toUpperCase();
                		break;
                        case 6:
// разделение символом
                        	if (repl!=null&&repl.length()>0){
                        		
                        		String str = st.rowInParentheses(s);
                        		String repl1 = st.STR_NULL;
                        		for (int i1=0;i1<repl.length();i1++) {
                        			repl1 += repl.charAt(i1)+ str;
                        		}
                        		repl = repl1.substring(0, repl1.length() - str.length());
                        	}
                		break;
                        case 7:
                        	repl = Translit.toTranslit(repl);
                		break;
                		// режим стихов
                        case 8:
                        	out = st.STR_NULL;
                        	fl = false;
                        	sy = st.STR_NULL;
                        	ch = 0;
                        	for (i=0;i<repl.length();i++){
                        		sy = repl.substring(i,i+1);
                        		if (i==0){
                        			fl = true;
                        		}
                        		ch = sy.charAt(0);
                        		if (ch>32&fl==true){
                        			sy = sy.toUpperCase();
                        			fl=false;
                        			out +=sy;
                        			continue;
                        		}
                        		else if (ch == '\n')
                        			fl = true;
                        		out+=sy;
                        	}
                        	repl = out;
                		break;
                		// как в предложениях
                        case 9:
                        	if (ServiceJbKbd.inst==null)
                        		return;
                        	out = st.STR_NULL;
                        	fl = false;
                        	sy = st.STR_NULL;
                        	ch = 0;
                        	for (i=0;i<repl.length();i++){
                        		sy = repl.substring(i,i+1);
                        		if (i==0){
                        			fl = true;
                        		}
                        		if (ServiceJbKbd.inst.m_SentenceEnds.contains(sy)){
                        			fl=true;
                        			out+=sy;
                        			continue;
                        		}
                        		ch = sy.charAt(0);
                        		
                        		if (ch>32&fl==true){
                        			sy = sy.toUpperCase();
                        			fl=false;
                        			out += sy;
                        			continue;
                        		}
                        		out += sy;
                        	}
                        	repl = out;
                		break;
                        case 10:
                    		if (ServiceJbKbd.inst==null) 
                    			return;
                			repl = ServiceJbKbd.inst.getClipboardCharSequence().toString();
//                        	if (repl.length()!=0) {
//                        		if (ServiceJbKbd.inst!=null) {
//                        			repl = ServiceJbKbd.inst.getClipboardCharSequence().toString();
//                        		}
//                        		repl=st.STR_NULL;
//                        	}
//                    		ServiceJbKbd.inst.processTextEditKey(st.TXT_ED_PASTE);
                        	break;
                    }
                    if(repl==null)
                    {
                        pos = s.length()-1;
                        break;
                    }
//                    s = s.substring(0,f)+repl+s.substring(f+ss.length()+1);
                    String sss = st.STR_NULL;
                    int l=0;
                    if (s.indexOf(ss)!=0){
                    	sss = s.substring(f+ss.length()+1);
                    	if (sss.startsWith("="))
                    		if (sss.indexOf("]")>0){
                    			l = sss.indexOf("]")+1;
                    		}
                    }
                    		
                    s = s.substring(0,f)+repl+s.substring(f+ss.length()+1+l);
                    pos = f+repl.length();
                    break;
                }
            }
            if(!bFound)
                pos++;
        }
        if(del==IB_WORD)
            ci.replaceCurWord(ic, s);
        else if(del==IB_LINE)
            ci.replaceCurLine(ic, s);
        else {
            ServiceJbKbd.inst.onText(s);
        }
        if(ci.isInited())
            ic.endBatchEdit();
		if (ServiceJbKbd.inst!=null&&ServiceJbKbd.inst.isSelMode())
			ServiceJbKbd.inst.stickyOff(-310);

    }
    
/** Обрабатывает щелчок по элементу шаблона */  
    void processTemplateClick(int index, boolean bLong)
    {
        if(index<0)
        {
            openFolder(m_curDir.getParentFile());
            return;
        }
        if(index>m_arFiles.size())
            return;
        file = m_arFiles.get(index);
        if(file.isDirectory())
        {
            if(bLong)
            {
                setEditTpl(file);
                setEditFolder(true);
                st.kbdCommand(st.CMD_TPL_EDITOR);
            }
            else
            {
                openFolder(file);
            }
        }
        else
        {
            if(bLong)
            {
           		setEditTpl(file);
           		st.kbdCommand(st.CMD_TPL_EDITOR);
            }
            else
            {
            	if (rejim == 2&&type == 1) {
            		if (st.isCalcPrg()) {
            			fpath = m_curDir+st.STR_SLASH+file.getName().trim();
            			addCalc();
            			File f1 = new File(fpath);
            			if (f1.exists()) {
            				calcSavePrgQuery();
            			} else {
            				calcSavePrg();
            			}
            		} else
            			st.toast(st.c().getString(R.string.calc_prog_empty));
            	} else {
            		if (rejim == 2&&type == 2) {
            			if (st.isCalcPrg()) {
                			calcLoadPrgQuery();
            			} else {
            				calcLoadPrg();
            			}
           			} else {
       		        	processTemplate(getFileString(file));
           			}
            	}
          	  menu.close();

            }
        }
    }
    void calcLoadPrgQuery()
    {

        GlobDialog gd = new GlobDialog(st.c());
        gd.set(R.string.calc_load_prg_msg, R.string.yes, R.string.no);
        gd.setObserver(new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                {
                	calcLoadPrg();
                }
                return 0;
            }
        });
        gd.showAlert();
    	
    }
// загрузка программы калькулятора    
    void calcLoadPrg()
    {
     	try {
     		String fs = m_curDir + st.STR_SLASH + file1.getName();
        	FileReader fr= new FileReader(fs);
         	Scanner sc = new Scanner(fr);
         	sc.useLocale(Locale.US);
        	String str = st.STR_NULL;
        	String out = st.STR_NULL;
       		boolean fl = false;
// алгоритм должен быть именно такой!
       		while (sc.hasNextLine()) {
          		str = sc.nextLine();
          		if (fl){
          			out+=str;
          		}
         		if (str.equals(st.CALC_PROGRAM_WORD)) {
         			fl = true;
         		}
       		}
       		sc.close();
       		String[] arprg = null;
       		arprg = out.split(",");
       		if (arprg != null) {
       			for (int i=0;i<arprg.length;i++){
       				st.calc_prog[i]=Integer.valueOf(arprg[i]);
       			} 
       		}  else {
   				st.toast("Not loading./\nError format");
       		}
     	}
     	catch(IOException ex){
     	}
    }
/** Основная функция для вывода шаблонов в CommonMenu*/
    void makeCommonMenu()
    {
        if(m_curDir==null)
            return;
        menu = new com_menu();
    	if (Templates.inst == null)
    		return;
        if (rejim == 1) {
        	menu.textMenuButton(ServiceJbKbd.inst.textMenuName(st.CMD_TPL));
        }
        else if (rejim == 2&&type == 1)
        	menu.textMenuButton(ServiceJbKbd.inst.textMenuName(st.CMD_CALC_SAVE));
        else if (rejim == 2&&type == 2)
        	menu.textMenuButton(ServiceJbKbd.inst.textMenuName(st.CMD_CALC_LOAD));
        if (rejim == 1&&st.fiks_tpl.length()>0&&st.fl_fiks_tpl) {
        	m_curDir = new File(st.fiks_tpl);
        	st.fl_fiks_tpl = false;
        }
        if (rejim == 2&&st.fiks_calc.length()>0&&st.fl_fiks_calc) {
        	m_curDir = new File(st.fiks_calc);
        	st.fl_fiks_calc = false;
        }
        menu.m_state|=com_menu.STAT_TEMPLATES;
        m_arFiles = getSortedFiles();
        if(m_arFiles==null)
            return;
        if(!m_curDir.getAbsolutePath().equals(m_rootDir.getAbsolutePath()))
        {
            menu.add("[..]",-1);
        }
        String str = st.STR_NULL;
        int pos = 0;
        for(File f:m_arFiles)
        {
            if(f.isDirectory())
            {
// вывод папок шаблонов
            	dir = m_curDir+st.STR_SLASH+f.getName().trim();
            	
            		if ((ServiceJbKbd.inst.m_hotkey_dir).length()>0&&
           				dir.contains(ServiceJbKbd.inst.m_hotkey_dir)&&
           				(ServiceJbKbd.inst.m_hotkey_dir).length()==dir.length()){
            			initHot(dir);
                    	menu.add(str+"[(sel) "+f.getName()+"]",pos);
            		}
            		else { 
           				menu.add(str+"["+f.getName()+"]",pos);
            		}
            	}
            else
            {
// вывод простых шаблонов
            	if (rejim == 1) {
            		menu.add(str+f.getName(),pos);
            	}
            	else if (rejim == 2) {
            		st.tmps = f.getName();
            		if (st.tmps.endsWith(".calc"))
            			menu.add(str+st.tmps.substring(0, st.tmps.length()-5),pos);
            	}
            }
            pos++;
        }
//        TextView tv_path = (TextView) menu.m_MainView.findViewById(R.id.path);
//        tv_path.setVisibility(View.VISIBLE);
//        tv_path.setText(st.c().getString(R.string.mm_path)+":\n" + m_curDir.getPath());
        
//      st.UniObserver obs = new st.UniObserver()
		st.help = st.c().getString(R.string.mm_path)+st.STR_CR + Templates.inst.m_curDir.getPath()+st.STR_CR+st.STR_CR;
        st.UniObserver obs =  UniObserver();
//        {
//            @Override
//            public int OnObserver(Object param1, Object param2)
//            {
//                int pos = ((Integer)param1).intValue();
//                boolean bLong = ((Boolean)param2).booleanValue();
//                if (pos > -1)
//                	file1 = m_arFiles.get(pos);
//               	processTemplateClick(pos,bLong);
//                return 0;
//            }
//        };
        menu.show(obs, false);
    }
/** Функция для поиска конца строки в тексте, выбранном из едитора.
*@param f1 Позиция символа \r . для bLast=true вычисляется через lastIndexOf, для bLast =false - через indexOf 
*@param f2 Позиция символа \n. Или наоборот, пофигу
*@param bLast true - поиск ведется вверх от курсора, false - вниз от курсора
*@param len Текущая длина строки. Если len<4000 и ни одного переноса строки не найдено - для bLast = true вернет 0, для bLast=false - len
*@return Возвращает позицию конца или начала строки или -1, если не найдено (текст >=4000 символов и в нём нет ни одного переноса)*/
    static int chkPos(int f1,int f2,boolean bLast,int len)
    {
        int s = 0;
        if(f1>-1&&f2==-1)
            s = bLast?f1+1:f1;
        else if(f2>-1&&f1==-1)
            s = bLast?f2+1:f2;
        else if(f1==-1&&f2==-1)
        {
            if(len<4000)
            {
                if(bLast)
                    s = 0;
                else
                    s=len;
            }
            else 
                s = -1;
        }
        else 
        {
            if(bLast)
                s = f1>f2?f1:f2;
            else
                s = f1>f2?f2:f1;
        }
        return s;
    }
/** Класс для получения информации о текущем выделении, текущем слове и текущей строке 
 * А также для операций с ними */   
    static class CurInput
    {
        String wordStart;
        String wordEnd;
        String lineStart;
        String lineEnd;
        String sel=st.STR_NULL;
        boolean bInited = false;
        public boolean hasCurLine = false;
        boolean isInited()
        {
            return bInited;
        }
        String getLineText()
        {
            if(lineStart==null||lineEnd==null)
                return null;
            return lineStart+lineEnd;
        }
        String getWordText()
        {
            if(wordStart==null||wordEnd==null)
                return null;
            return wordStart+wordEnd;
        }
        boolean replaceCurWord(InputConnection ic,String repl)
        {
            if(!deleteCurWord(ic))
                return false;
            ic.commitText(repl, 1);
            return true;
        }
        boolean replaceCurLine(InputConnection ic,String repl)
        {
            if(!deleteCurLine(ic))
                return false;
            ic.commitText(repl, 1);
            return true;
        }
        boolean deleteCurLine(InputConnection ic)
        {
            if(lineStart==null||lineEnd==null)
                return false;
            ic.deleteSurroundingText(lineStart.length(), lineEnd.length());
            return true;
        }
        boolean deleteCurWord(InputConnection ic)
        {
            if(wordStart==null||wordEnd==null)
                return false;
            ic.deleteSurroundingText(wordStart.length(), wordEnd.length());
            return true;
        }
        /** Функция для получения текстов из редактора
         * @param positions
         *@return true - инициализация удалась, false - нет */
            boolean init(InputConnection ic)
            {
                bInited = true;
                try{
                    if(ServiceJbKbd.inst==null)
                        return false;
                    if(ServiceJbKbd.inst.m_SelStart<0||ServiceJbKbd.inst.m_SelEnd<0)
                        return false;
                    int ss = ServiceJbKbd.inst.m_SelStart,  se = ServiceJbKbd.inst.m_SelEnd;
                    // ss - реальная позиция курсора, может быть меньше, чем ss
                    int cnt = se>ss?se-ss:ss-se;
                    int cp = se>ss?ss:se;
                    if(cnt>0)
                    {
                        // Получаем выделенный фрагмент
                        ic.setSelection(cp, cp);
                        sel = ic.getTextAfterCursor(cnt, 0).toString();
                    }
                    cp = se;
                    if(cnt>0)
                        ic.setSelection(cp, cp);
                    CharSequence sec1 = ic.getTextBeforeCursor(4000, 0);
                    CharSequence sec2 = ic.getTextAfterCursor(4000, 0);
                    String bef = sec1.toString();
                    String aft = sec2.toString();
                    int s = chkPos(bef.lastIndexOf('\n'), bef.lastIndexOf('\r'), true, bef.length());
                    int e = chkPos(aft.indexOf('\n'), aft.indexOf('\r'), false, aft.length());
                    if(s!=-1&&e!=-1)
                    {
                        lineStart = bef.substring(s); 
                        lineEnd =aft.substring(0,e); 
                        hasCurLine = s>0&&e<aft.length();
                    }
                    wordStart = getCurWordStart(sec1,sec1.length()==4000); 
                    wordEnd = getCurWordEnd(sec2,sec2.length()==4000); 
                    if(cnt>0)
                        ic.setSelection(ss, se);
                    return true;
                }
                catch(Throwable e)
                {
                }
                return false;
            }
    }
/** Возвращает текст начала слова вверх от курсора
*@param seq Текст, взятый функцией {@link InputConnection#getTextBeforeCursor(int, int)}. Может быть null
*@return Текст начала слова под курсором **/
    static String getCurWordStart(CharSequence seq,boolean bRetEmptyIfNotDelimiter)
    {
        if(seq==null)
        {
            seq = ServiceJbKbd.inst.getCurrentInputConnection().getTextBeforeCursor(40, 0);
        }
        if(seq==null)
            return null;
        int apostr = -1;
        for(int i=seq.length()-1;i>=0;i--)
        {
            char ch = seq.charAt(i);
            if(ch=='\'')
            {
                apostr=i;
                continue;
            }
            if(!Character.isLetterOrDigit(ch))
//            if(!ServiceJbKbd.inst.isWordSeparator(ch))
            {
                return seq.subSequence(apostr>-1&&i==apostr-1?apostr+1:i+1, seq.length()).toString();
            }
        }
        if(bRetEmptyIfNotDelimiter)
            return null;
        return seq.toString();
    }
/** Возвращает текст конца слова вниз от курсора
*@param seq Текст, взятый функцией {@link InputConnection#getTextBeforeCursor(int, int)}. Может быть null
*@param bRetEmptyIfNotDelimiter - true - если не найден конец слова, вернёт пустую строку. false - вернёт строку seq
*@return Текст конца слова под курсором **/
    static String getCurWordEnd(CharSequence seq,boolean bRetEmptyIfNotDelimiter)
    {
        if(seq==null)
        {
            seq=ServiceJbKbd.inst.getCurrentInputConnection().getTextAfterCursor(40, 0);
        }
        if(seq==null)
            return bRetEmptyIfNotDelimiter?null:st.STR_NULL;
        int apostr = -1;
        for(int i=0;i<seq.length();i++)
        {
            char ch = seq.charAt(i);
            if(ch=='\'')
            {
                apostr = i;
                continue;
            }
            if(!Character.isLetterOrDigit(ch))
//            if(!ServiceJbKbd.inst.isWordSeparator(ch))
            {
                return seq.subSequence(0, apostr>-1&&i==apostr+1?apostr:i).toString();
            }
        }
        if(bRetEmptyIfNotDelimiter)
            return null;
        return seq.toString();
    }
    public static boolean deleteDir(File dir)
    {
        if(!dir.isDirectory())
            return false;
        String[] children = dir.list();
        for (String p:children) 
        {
           File temp =  new File(dir, p);
           if(temp.isDirectory())
           {
               if(!deleteDir(temp))
                   return false;
           }
           else
           {
               if(!temp.delete())
                   return false;
           }
        }
        dir.delete();
        return true;
    }
    void setHotDir(String dir)
    {
    	st.pref().edit().putString(st.PREF_KEY_HOT_DIR, m_curDir.getAbsolutePath()+st.STR_SLASH+dir.trim()).commit();
    	//ServiceJbKbd.inst.m_hotkey_dir=m_curDir.getAbsolutePath()+st.STR_SLASH+dir.trim();
    }
    void initHot(String dir)
    {
    	File myFolder = new File(dir);
    	String[] fn=myFolder.list();
    	int ii=0;
    	for (int i = 0; i < fn.length; i++) {
			File f = new File(dir+st.STR_SLASH+fn[i]);
			if (f.isFile()){
				if (fn[i].startsWith("#[")){
					fn[i]=fn[i].toUpperCase();
					if (fn[i].contains("]")){
						ServiceJbKbd.inst.m_hot_str[ii]=fn[i].substring(2, fn[i].indexOf("]"));
						ServiceJbKbd.inst.m_hot_tpl[ii]=getFileString(f);
						ii++;
					} else
						st.toast("Error format (not \"]\") in\n"+fn[i]);
				}
    		}
    	}
		if (ii>100)
			ii=100;
		ServiceJbKbd ss = new ServiceJbKbd();
		if (ii>0)
        	ss.setTplCount(ii); 
		else
        	ss.setTplCount(0); 
    	ServiceJbKbd.inst.m_hotkey_dir = dir.trim();
    }
    void setDir(int dir, int typ)
    {
    	if (dir == 1) {
    		template_path = FOLDER_TEMPLATES;
    		rejim = 1;
    		type = 0;
    	}
    	else if (dir == 2) {
    		template_path = FOLDER_CALC;
    		rejim = 2;
    		type = typ;
    	}
    }
    void rootDir()
    {
        setDir(rejim,type);
        String rd = st.getSettingsPath()+template_path;
        m_rootDir = new File(rd);
        if(!m_rootDir.exists())
        {
            if(!m_rootDir.mkdirs())
                m_rootDir = null;
        }
        m_curDir = m_rootDir;
    }
    void addCalc()
    {
    	if (fpath.endsWith(".calc") == false) {
    		fpath+=".calc";
    	}
    }
    st.UniObserver UniObserver()
    {
      st.UniObserver obs = new st.UniObserver()
      {
          @Override
          public int OnObserver(Object param1, Object param2)
          {
              int pos = ((Integer)param1).intValue();
              boolean bLong = ((Boolean)param2).booleanValue();
              if (pos > -1) {
              	file1 = m_arFiles.get(pos);
              }
              processTemplateClick(pos,bLong);
              return 0;
          }
      };
      return obs;
    }
    void calcSavePrgQuery()
    {
        GlobDialog gd = new GlobDialog(st.c());
        gd.set(R.string.rewrite_question, R.string.yes, R.string.no);
        gd.setObserver(new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                {
                	calcSavePrg();
                }
                return 0;
            }
        });
        gd.showAlert();
    }

/** Текущая папка */    
    File m_cd;
 // режим использования 
    // 1 - шаблоны
    // 2 - калькулятор
    public static int rejim = 1;
    public static String FOLDER_TEMPLATES = "templates";
    public static String FOLDER_CALC = "calc";
 // тип
    // 0 - шаблоны
    // 1 - запись программы
    // 2 - загрузить программу
    public static int type = 0;
    public static String template_path = FOLDER_TEMPLATES;
    File file1;
    File m_rootDir;
    File m_curDir;
    int m_state=0;
    String dir_hotkey=st.STR_NULL;
    String m_hot_format=st.STR_NULL;
    String cur_dir=st.STR_NULL;
    String dir=st.STR_NULL;
    String fpath = st.STR_NULL;
/** Состояние - редактирование папки */ 
    public static final int STAT_EDIT_FOLDER = 0x00001;
    public static final int IB_SEL = 0;
    public static final int IB_WORD = 1;
    public static final int IB_LINE = 2;
    public static final char TPL_SPEC_CHAR = '$';
//    public static final String[] Instructions = {"select","selword","selline","datetime","sellowercase","selupcase","selreceptions"};
    public static final String[] Instructions = 
    	{
    	"select",
    	"selword",
    	"selline",
    	"datetime",
    	"sellowercase",
    	"selupcase",
    	"selinsertword",
    	"seltranslit",
    	"selVerseMode",
    	"selAsInTheSentences",
    	"paste"
    	};
    File file;
    ArrayList<File> m_arFiles;
}
