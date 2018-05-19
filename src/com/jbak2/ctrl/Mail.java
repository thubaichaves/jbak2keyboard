package com.jbak2.ctrl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;

import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.st;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class Mail 
{
	public static final String MAIL = "wolf3d@tut.by";

	public static void sendFeedback(Context c) {
		sendFeedback(c, null);
	}
	public static void sendFeedback(Context c,File crash) {
		StringBuilder info;
		info = new StringBuilder();
//		if (crash!=null)
//			info = new StringBuilder(String.format(Locale.ENGLISH,
//				"\n\n%s", "О "+ getAppNameAndVersion(c)));
		String delim = ": ";
		info.append(String.format(Locale.ENGLISH, "%s%s%s%s\n","Device locale",
				delim, st.STR_NULL,
				Locale.getDefault().getLanguage()));
		info.append(String.format(Locale.ENGLISH, "%s%s%s%s\n","Os",
				delim, "Android ",
				Build.VERSION.RELEASE));
		info.append(String.format(Locale.ENGLISH, "%s%s%s%s\n","Manufacture",
				delim, st.STR_NULL,
				Build.MANUFACTURER));
		info.append(String.format(Locale.ENGLISH, "%s%s%s\n",
				"Device", delim, Build.MODEL));
		info.append(st.STR_CR);
		if(crash!=null)
			info.append(c.getString(R.string.crash_desc));
		info.append("===\n");
		if(crash!=null)
		{
			info.append(strFile(crash));
			info.append("===\n");
		}
		final Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("text/message");
		emailIntent.putExtra(Intent.EXTRA_EMAIL,
				new String[] {MAIL});

		String subj = null;
		if(crash==null)
			subj = "О "+getAppNameAndVersion(c);
		else
			subj = "Crash report "+getAppNameAndVersion(c);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subj);
		emailIntent.putExtra(Intent.EXTRA_TEXT, info.toString());
		c.startActivity(Intent.createChooser(emailIntent, "Crash report "+getAppNameAndVersion(c)));
	}
    public static String strFile(File f)
    {
    	String s= null;
		try{
			FileInputStream fin = new FileInputStream(f);
			byte buf[] = new byte[(int) f.length()];
			fin.read(buf);
			fin.close();
			s = new String(buf);
		}
		catch(Throwable e)
		{
		}
		return s;
    }
	public static String getAppNameAndVersion(Context c)
	{
		return c.getString(R.string.ime_name)+" "
				+st.getAppVersionName(c)+"("+st.getAppVersionCode(c)+")";
	}

}