package com.shenhua.lock;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by shenhua on 3/16/2017.
 * Email shenhuanet@126.com
 */
public class ShortcutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceUtils.getInstance("config");
        String pak = PreferenceUtils.readString(this, getString(R.string.app_name) + 0);

        goToApp(pak);
        this.finish();

    }

    public void goToApp(String appPackageName) {
        try {
            PackageManager packageManager = getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(appPackageName);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "未找到应用！", Toast.LENGTH_SHORT).show();
        }
    }
}
