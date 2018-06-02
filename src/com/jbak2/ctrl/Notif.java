package com.jbak2.ctrl;

import com.jbak2.JbakKeyboard.JbKbdPreference;
import com.jbak2.JbakKeyboard.R;
import com.jbak2.JbakKeyboard.ServiceJbKbd;
import com.jbak2.JbakKeyboard.st;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;

// уведомление в шторке
public class Notif 
{
	public static final String ACTION_SHOW = "com.jbak2.JbakKeyboard.SHOW_KEYBOARD";
	public static ServiceJbKbd mact;
	final public static int NOTIFY_ALL = 0;
	final public static int NOTIFY_ID = 1;
	NotificationManager notificationManager = null;
	
	public Notif(ServiceJbKbd act) {
		mact = act;
	}
	@SuppressLint("NewApi")
	public void createNotif()
	{
		ClipboardManager cl= (ClipboardManager) mact.getSystemService(mact.CLIPBOARD_SERVICE);
		ClipData clip = cl.getPrimaryClip();
		String str = st.STR_NULL;
		if (clip != null)
			str = clip.getItemAt(0).getText().toString();
//        Intent in = new Intent(Intent.ACTION_SEND)
//        .setAction(Intent.ACTION_SEND)
//        .setComponent(new ComponentName(mact, MainActivity.class))
//        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        .putExtra(Intent.EXTRA_TEXT, str)
//        .setType("text/plain");
		
		//Intent in = new Intent(mact, ServiceJbKbd.class);
		
//        Intent in = new Intent();
//        in.setAction(Intent.ACTION_SEND);
//        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        in.putExtra(Intent.EXTRA_TEXT, "bbbb");
//        in.setType("text/plain");
//        in.setComponent(new ComponentName(mact, ShowKeyboard.class));
//        in.setClipData(clip);

        //mact.startActivity(in);
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, "bbb");
//        sendIntent.setType("text/plain");
        //mact.startActivity(sendIntent);
        
		Intent settingIntent =  new Intent(mact, JbKbdPreference.class);
	    PendingIntent settingPendingIntent = PendingIntent.getActivity(mact,
                0, settingIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

		Intent showIntent =  new Intent(mact, ServiceJbKbd.class);
	    PendingIntent showPendingIntent = PendingIntent.getActivity(mact,
                0, showIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

	    Intent mainIntent = new Intent();
	    mainIntent.setAction(ACTION_SHOW);
	    mainIntent.putExtra("com.jbak2.JbakKeyboard.broadcast.Message", "bbb");
	    mainIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
	    mact.sendBroadcast(mainIntent);        
        PendingIntent contentIntent = PendingIntent.getActivity(mact,
                0, mainIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = mact.getResources();
		
        // до версии Android 8.0 API 26
        Notification.Builder builder = new Notification.Builder(mact);

        builder.setContentIntent(contentIntent)
                // обязательные настройки
                .setSmallIcon(R.drawable.icon)
                //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle(res.getString(R.string.ime_name))
                //.setContentText(res.getString(R.string.notifytext))
                .setContentText("Show keyboard") // Текст уведомления
                // необязательные настройки
             // большая картинка
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.icon))
             // текст в строке состояния
                .setTicker(res.getString(R.string.ime_name)) 
                //.setTicker("Последнее китайское предупреждение!")
                .setWhen(System.currentTimeMillis())
                .addAction(R.drawable.save, res.getString(R.string.ime_settings),
                		settingPendingIntent)
                .addAction(R.drawable.icon, "show kbd",
                		showPendingIntent)

                //.setAutoCancel(true);
                .setAutoCancel(false); // автоматически закрыть уведомление после нажатия

        notificationManager =
                (NotificationManager) mact.getSystemService(Context.NOTIFICATION_SERVICE);
		// Альтернативный вариант
		// NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFY_ID, builder.build());		
	}
	
	public void dismiss(int id)
	{
		if (notificationManager==null)
			return;
		// если id = 0, то удаляем все уведомления программы
		switch (id)
		{
		case NOTIFY_ALL:
			notificationManager.cancelAll();
			break;
		case NOTIFY_ID:
			notificationManager.cancel(NOTIFY_ID);
			break;
		}
		mact = null;
	}
}
