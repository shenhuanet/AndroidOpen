package com.shenhua.lock;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Process;

public class MainActivity extends Activity {

    private ComponentName name;
    public static final int SHORTCUT_NUMBER = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DevicePolicyManager manager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        name = new ComponentName(this, LockReceiver.class);
        if (manager.isAdminActive(name)) {
            manager.lockNow();
            Process.killProcess(Process.myPid());
        } else {
            activeManager();
        }
    }

    private void activeManager() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, name);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.app_name));
        startActivity(intent);

        createShortcut();

    }

    private void createShortcut() {
        Intent actionIntent = new Intent(Intent.ACTION_MAIN);
        actionIntent.addCategory(Intent.CATEGORY_DEFAULT);
        actionIntent.setClass(this, ShortcutActivity.class);

        PreferenceUtils.getInstance("config");
        Intent intent;
        String shortcutName;
        for (int i = 0; i < SHORTCUT_NUMBER; i++) {
            shortcutName = getString(R.string.app_name) + i;
            intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            intent.putExtra("duplicate", true);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
            PreferenceUtils.write(this, shortcutName, getPackageName());
            sendBroadcast(intent);
        }
    }
}
