package com.jbak2.Dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.st;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public abstract class DlgFileExplorer 
{
	TextView tvcurpath = null;
	Button bfilt = null;
	Button bhome = null;
	Button bclose = null;
	/** Сторонний обработчик, который был передан в функции {@link #show()}*/    
    st.UniObserver m_MenuObserver;
	Adapt m_adapter = null;
	File cur_dir = null;
	File last_dir = null;
	File parent_dir = null;
	ArrayList<FileInfo> aritems = new ArrayList<FileInfo>();
	public String dir = st.STR_NULL;
	public static String TEXT_FOLD_BEG = "📁["; 
	public static String TEXT_FOLD_END= "]"; 
	public static String TEXT_FOLD_PARENT = "📂[..]"; 
	
	public static int SELECT_FILE = 0; 
	public static int SELECT_FOLDER = 1; 

	public static Activity mact = null;
	public static DlgFileExplorer inst = null;
    static View m_mainview;
	public static String[] arext = null;
	public static String[] arexttmp = null;
	File root = null;
	String oldpath = null;
	/** режим выбоа - директория или файл */
	int sel = 0;
	
	public static final String[] PICTURE_EXT = new String[]
	{
		".png",
		".bmp",
		".jpg",
		".jpeg",
	};
	
	/**  Файл эксплорер.
	 * @param ext - массив отображаемых расширений файлов, если null, 
	 * то выводим весь список
	 * @param startDir - если null, значит выводить домашнюю папку
	 * @param oldPath - если не null, то выводить в корневом меню кнопку с текстом
	 * указанны здесь при нажатии и на неё, посылать в onSelected не файл, а null
	 * */
	public DlgFileExplorer(Activity c, String[] ext, File startDir, String oldPath, int selectDirOrFile){
		mact = c;
		root = startDir;
		sel = selectDirOrFile;
		arext = ext;
		oldpath = oldPath;
		inst = this;
		init();
        
	}
    public void init()
    {
		m_mainview = mact.getLayoutInflater().inflate(R.layout.file_explorer, null);
		tvcurpath = (TextView) m_mainview.findViewById(R.id.fe_curpath);
		tvcurpath.setVisibility(View.VISIBLE);
		bclose= (Button) m_mainview.findViewById(R.id.fe_close);
		bclose.setVisibility(View.VISIBLE);
		bclose.setOnClickListener(m_listener);
		bhome = (Button) m_mainview.findViewById(R.id.fe_home);
		bhome.setVisibility(View.VISIBLE);
		bhome.setOnClickListener(m_listener);
		bfilt = (Button) m_mainview.findViewById(R.id.fe_filter);
		bfilt.setVisibility(View.VISIBLE);
		bfilt.setOnClickListener(m_listener);
		bfilt.setOnLongClickListener(m_longListener);
		if(arext!=null)
			setDrawableFilter(true);
    }
    public void show()
    {
    	st.hidekbd();
    	changeArrayList();
        m_MenuObserver = getClickItemObserver();
        ListView lv = (ListView)m_mainview.findViewById(R.id.fe_list);
        m_adapter = new Adapt(mact, this);
        lv.setAdapter(m_adapter);
//        int rlist = R.layout.tpl_instr_list;
//        final ArrayAdapter<FileInfo> ar = new ArrayAdapter<String>(mact, 
//                                                    rlist,
//                                                    aritems
//                                                    );
        Dlg.CustomViewAndMenu(mact, m_mainview, m_adapter, mact.getString(R.string.fi_title), new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                return 0;
            }
        });
    }
    public st.UniObserver getClickItemObserver()
    {
    	st.UniObserver obs = new st.UniObserver() 
    	{
			
			@Override
			public int OnObserver(Object param1, Object param2) {
				FileInfo fi = (FileInfo)param1;
				boolean longClick = ((Boolean)param2).booleanValue();
				if (!longClick) {
					// короткий клик
					if (fi.file!=null) {
						if (fi.file.isDirectory()) {
							last_dir = fi.file;
							if(fi.file.getAbsoluteFile().toString().compareTo("/")!=0)
								st.pref(mact).edit().putString(st.FILE_EXPLORER_LAST_DIR, last_dir.getAbsolutePath()).commit();
							cur_dir = fi.file;
							parent_dir = cur_dir.getParentFile();
							changeArrayList();
	//kk
						} else {
							if (sel == SELECT_FILE) {
								onSelected(fi.file);
								close();
								}
						}
						
					}
				} 
				// длинное нажатие
				else {
					if (fi.file != null)
						st.copyText(mact, fi.file.getAbsolutePath().toString());
				}
				return 0;
			}
		};
    	return obs;
    }
    static class Adapt extends ArrayAdapter<FileInfo>
    {
    	DlgFileExplorer m_menu; 
        public Adapt(Context context,DlgFileExplorer menu)
        {
            super(context,0);
            m_menu = menu;
        }
        @Override
        public int getCount() 
        {
            return m_menu.aritems.size();
        };
        @SuppressLint("NewApi")
		@Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
        	FileInfo fi = m_menu.aritems.get(position);
            if(convertView!=null)
            {
            	Button b = (Button)convertView;
                m_menu.getNewView(fi,b);
            }
            else
            {
                convertView = m_menu.getNewView(fi,null);
                int jj = 0;
            }
//            arcounter++;
//            if (posY > 0&&arcounter >= getCount()) {
//               ListView lv = (ListView)m_MainView.findViewById(R.id.com_menu_container);
//                lv.smoothScrollToPosition(posY);
//                posY = 0;
//            }
            return convertView;
        }
    }
    /** Класс, хранящий информацию об элементе меню */  
    public static class FileInfo
    {
/** id элемента */      
        int id;
        String text;
        File file;
        
        public FileInfo(int pos, File fname)
        {
            id = pos;
            file = fname;
            text = null;
        }
        public FileInfo(int pos, File fname, String nameItem)
        {
            id = pos;
            file = fname;
            text = nameItem;
        }
        public FileInfo()
        {
            id = -1;
            file = null;
            text = null;
        }
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
    
/** Возвращает массив отсортированных файлов из текущей папки */   
    ArrayList<File> getSortedCurrentDir()
    {
        ArrayList<File> ar = new ArrayList<File>();
        try{
            File af[] = cur_dir.listFiles();
            for(int i=0;i<af.length;i++)
            {
                ar.add(af[i]);
            }
            Collections.sort(ar, new FilesComp());
            return ar;
        }
        catch (Throwable e) {
        	st.toast(R.string.fi_not_open);
        }
        return null;
    }
    public View getNewView(FileInfo fi, Button bb) {
    	if (bb==null)
    		bb = new Button(mact);
    	if (fi.text!=null)
    		bb.setText(fi.text);
    	else {
    		if (fi.file.isDirectory())
    			bb.setText(TEXT_FOLD_BEG+fi.file.getName().toUpperCase()+TEXT_FOLD_END);
    		else
    			bb.setText(fi.file.getName());
    	}
//    	bb.setTextColor(Color.WHITE);
//    	bb.setPadding(2, 0, 2, 10);
    	bb.setGravity(Gravity.LEFT|Gravity.TOP);
    	bb.setOnClickListener(m_listener);
    	bb.setOnLongClickListener(m_longListener);
    	bb.setMaxLines(3);
    	bb.setTag(fi);
    	bb.setTransformationMethod(null);
    	return bb;
    }
	public static File[] getStorages()
	{
		File storages = new File("/storage");
		if(storages.exists())
		{
			File sdcards[] = storages.listFiles();
			return sdcards;
		}
		return null;
	}
    /** Обработчик короткого нажатия кнопок меню */    
    View.OnClickListener m_listener = new View.OnClickListener()
    {
        
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
        	case R.id.fe_close:
        		close();
        		return;
        	case R.id.fe_home:
        		cur_dir=null;
        		changeArrayList();
        		return;
        	case R.id.fe_filter:
        		String ss = st.STR_NULL;
        		ss = mact.getString(R.string.fi_filt1)
        				+st.STR_CR;
        		if (arext !=null) {
            		for (int i=0;i<arext.length;i++) {
            			ss += arext[i]+st.STR_SPACE; 
            		}
        		} else
        			ss += "<empty>";
      			ss+= st.STR_CR+st.STR_CR
        				+mact.getString(R.string.fi_filt2);
        	 
        		st.toastLong(ss);
        		return;
            }
            FileInfo fi = (FileInfo)v.getTag();
            if (fi.file==null) {
				onSelected(null);
				close();
            }
            if(m_MenuObserver!=null)
            {
                m_MenuObserver.OnObserver(fi, new Boolean(false));
            }
        }
    };
    /** пока не юзается */
    OnLongClickListener m_longListener = new OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View v)
        {
            switch (v.getId())
            {
        	case R.id.fe_filter:
        		boolean fl = false;
        		if (arext!=null&&arexttmp==null) {
        			arexttmp = arext;
        			arext = null;
        			fl = false;
        		}
        		else if (arext==null&&arexttmp!=null) {
            		arext = arexttmp;
            		arexttmp = null;
            		fl = true;
        		}
        		setDrawableFilter(fl);
        		st.toast(mact,R.string.fi_done);
        		changeArrayList();
        		return true;
            
            }
            
            FileInfo fi = (FileInfo)v.getTag();
            if(m_MenuObserver!=null)
            {
                m_MenuObserver.OnObserver(fi, new Boolean(true));
            }
            return true;
        }
    };

	public static void close()
	{
		inst = null;
		Dlg.dismiss();
	}
	/** обработка текущей папки (cur_dir) */
	public void changeArrayList()
	{
    	if (aritems!=null)
    		aritems.clear();
        ArrayList<File> arf = new ArrayList<File>();
        int pos = 0;
    	if (cur_dir== null) {
    		tvcurpath.setText(st.STR_NULL);
    		bhome.setVisibility(View.GONE);
    		if (oldpath!=null) {
        		aritems.add(new FileInfo(pos,null,oldpath));
    			pos++;
    		}
    		if (last_dir!=null) {
        		aritems.add(new FileInfo(pos,last_dir,st.STR_NULL+mact.getString(R.string.fi_folder_last)+st.STR_CR+last_dir.getAbsolutePath()));
    			pos++;
    			last_dir = null;
    		} else {
    			String lastfold = st.pref(mact).getString(st.FILE_EXPLORER_LAST_DIR, st.STR_NULL);
    			if (lastfold.length()>0){
    				last_dir = new File(lastfold);
            		aritems.add(new FileInfo(pos,last_dir,st.STR_NULL+mact.getString(R.string.fi_folder_last)+st.STR_CR+last_dir.getAbsolutePath()));
        			pos++;
        			last_dir = null;
    			}

    		}
    			
    		parent_dir = null;
    		aritems.add(new FileInfo(pos,new File("/"),TEXT_FOLD_BEG+mact.getString(R.string.fi_root)+TEXT_FOLD_END));
			pos++;
    		aritems.add(new FileInfo(pos,new File(st.STR_NULL+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)),TEXT_FOLD_BEG+mact.getString(R.string.fi_folder_dowload)+TEXT_FOLD_END));
			pos++;
    		//aritems.add(new FileInfo(2,new File(st.STR_NULL+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)),mact.getString(R.string.fi_folder_emulated)));
    		File sdcards[] = getStorages();
    		if(sdcards!=null&&sdcards.length>0)
    		{
    			//sortFilesByName(sdcards);
    			for(File f:sdcards) {
    				aritems.add(new FileInfo(pos,f));
    				pos++;
    			}
    		}
    		aritems.add(new FileInfo(3,new File(st.STR_NULL+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)),TEXT_FOLD_BEG+mact.getString(R.string.fi_folder_pict)+TEXT_FOLD_END));
    	} else {
    		if (parent_dir!=null&&parent_dir.getAbsoluteFile().toString().compareToIgnoreCase("/")!=0) {
        		aritems.add(new FileInfo(pos,parent_dir.getAbsoluteFile(),TEXT_FOLD_PARENT));
    			pos++;
    		}
    		tvcurpath.setText(cur_dir.getAbsolutePath());
    		bhome.setVisibility(View.VISIBLE);
    		arf = getSortedCurrentDir();
    		if (arf==null) {
    			cur_dir = null;
    			changeArrayList();
    			return;
    		}
            FileInfo fi = null;
            String ext = st.STR_NULL;
            for (File ff:arf) {
            	fi = new FileInfo();
            	if (ff.isDirectory()) {
            		aritems.add(new FileInfo(pos,ff.getAbsoluteFile()));
            	} else {
            		if (arext == null)
                		aritems.add(new FileInfo(pos,ff.getAbsoluteFile()));
            		else {
            			ext = ff.getName().toUpperCase();
            			for (int i=0;i<arext.length;i++) {
            				if (ext.endsWith(arext[i].toUpperCase())) {
                        		aritems.add(new FileInfo(pos,ff.getAbsoluteFile()));
                        		pos++;
                        		break;
            				}
            			}
            			continue;
            		}
            	}
            	pos++;
            }
    	}
    	if (m_adapter!=null) {
    		m_adapter.notifyDataSetChanged();
    		
    	}

	}
	/** выбранный файл или директория */
    public abstract void onSelected(File file);

    public void setDrawableFilter(boolean fl)
	{
    	Drawable top = null;
    	if (!fl)
    		top = mact.getResources().getDrawable(R.drawable.bullet_black);
    	else
    		top = mact.getResources().getDrawable(R.drawable.bullet_red);
		bfilt.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
	}

}