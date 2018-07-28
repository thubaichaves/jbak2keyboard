package com.jbak2.JbakKeyboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jbak2.CustomGraphics.ColorsGradientBack;
import com.jbak2.ctrl.Mail;

public class AboutActivity extends Activity
{
	static AboutActivity inst = null;
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        inst = this;
        View v = getLayoutInflater().inflate(R.layout.about, null);
        v.setBackgroundDrawable(new ColorsGradientBack().setCorners(0, 0).setGap(0).getStateDrawable());
        try{
//            String vers = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            String vers = st.getAppVersionName(inst)+" ("+st.getAppVersionCode(inst)+")";
            String app = getString(R.string.about_version)+st.STR_SPACE+vers+st.STR_CR
                           +getString(R.string.about_web); 
            ((TextView)v.findViewById(R.id.version)).setText(app);
        }
        catch (Throwable e) {}
        
        setContentView(v);
    }
    public void onClick(View v) {
    	switch (v.getId())
    	{
	case R.id.about_btn_mail:
		Mail.sendFeedback(inst);
		break;
	case R.id.about_btn_keycode:
    	st.runApp(inst,st.UNICODE_APP);
		break;
	case R.id.about_btn_diary:
    	st.desc_act_ini(2);
    	st.runAct(Desc_act.class,inst);
		break;
	case R.id.about_btn_other_app:
    	Intent intent = new Intent(Intent.ACTION_VIEW);
    	intent.setData(Uri.parse(st.ALL_APP_INMARKET));
    	startActivity(intent);
		break;
    	}
    	}
}
