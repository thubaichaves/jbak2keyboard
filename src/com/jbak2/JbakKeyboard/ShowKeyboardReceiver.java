// должен лежать только по такому пути, иначе система его не видит
package com.jbak2.JbakKeyboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
// ресейвер для показа клавиатуры из шторки

public class ShowKeyboardReceiver extends BroadcastReceiver
{
    public ShowKeyboardReceiver() 
    {
    	
    }
    @Override
    public void onReceive(Context c, Intent in)
    {
//    	if (JbKbdView.inst==null)
    	
    		st.toast("show");

//        if(Intent.ACTION_BOOT_COMPLETED.equals(in.getAction()))
//        {
//            c.startService(new Intent(c,ClipbrdService.class));
//        }
    }

	public static void shoKbd()
	{
	if (ServiceJbKbd.inst!=null&&!ServiceJbKbd.inst.isShowInputRequested())
		st.showkbd();
	}

}
