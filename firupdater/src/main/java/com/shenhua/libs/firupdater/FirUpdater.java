package com.shenhua.libs.firupdater;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.shenhua.libs.firupdater.bean.FirAppInfo;
import com.shenhua.libs.firupdater.utils.FirHttpCallback;
import com.shenhua.libs.firupdater.utils.FirHttpManager;
import com.shenhua.libs.firupdater.utils.FirUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shenhua on 11/30/2016.
 * e-mail shenhuanet@126.com
 */
public class FirUpdater {

    private static final String TAG = "FirUpdater";
    private static FirHttpManager firHttpManager = new FirHttpManager();
    private Activity activity;
    private String apiToken;
    private String apiUrl;
    private DownloadManager downloadManager;
    private CompleteReceiver completeReceiver;
    private long downloadId;

    private static FirUpdater instance;

    public static FirUpdater getInstance() {
        if (instance == null)
            instance = new FirUpdater();
        return instance;
    }

    public void updateAuto(final Activity activity, String appToken) {
        this.activity = activity;
        this.apiToken = appToken;
        registerReceiver();
        apiUrl = FirUtil.buildUpdateUrl(apiToken, activity.getPackageName());

        getNewVersion(apiUrl, new FirHttpCallback() {
            @Override
            public void onSuccess(Object o) {
                FirAppInfo info = (FirAppInfo) o;
                if (FirUtil.compareVersion(activity, info.getAppVersion()))
                    showUpdateDialog(info, "立即更新", "下次再说", null);
            }

            @Override
            public void onFailed(int errorCode, String msg) {

            }
        });
    }

    public void updateManual(final Activity activity, String appToken) {
        this.activity = activity;
        this.apiToken = appToken;
        registerReceiver();
        apiUrl = FirUtil.buildUpdateUrl(apiToken, activity.getPackageName());
        getNewVersion(apiUrl, new FirHttpCallback() {
            @Override
            public void onSuccess(Object o) {
                FirAppInfo info = (FirAppInfo) o;
                if (FirUtil.compareVersion(activity, info.getAppVersion()))
                    showUpdateDialog(info, "立即更新", "下次再说", null);
                else
                    Toast.makeText(activity, "当前已是最新版本", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int errorCode, String msg) {
                Toast.makeText(activity, "版本获取失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onDestroy() {
        if (activity != null)
            activity.unregisterReceiver(completeReceiver);
    }

    /**
     * get version code
     *
     * @param firUrl   url
     * @param callback callback
     */
    public void getNewVersion(final String firUrl, final FirHttpCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = firHttpManager.doGet(firUrl);
                if (result == null) return;
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject binary = object.getJSONObject("binary");
                    FirAppInfo info = new FirAppInfo();
                    info.setAppName(object.getString("name"));
                    info.setAppVersion(Integer.parseInt(object.getString("version")));
                    info.setAppChangeLog(object.getString("changelog"));
                    info.setAppDirectInstallUrl(object.getString("direct_install_url"));
                    info.setAppTime(FirUtil.getFormatTime(object.getLong("updated_at")));
                    info.setAppVersionShort(object.getString("versionShort"));
                    info.setAppInstallUrl(object.getString("install_url"));
                    info.setAppSize(FirUtil.getFormatSize(binary.getLong("fsize")));
                    callback.obtainMessage(FirUtil.SUCCESS, info).sendToTarget();
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.obtainMessage(FirUtil.FAILED, "数据解析异常").sendToTarget();
                }
            }
        }).start();
    }

    /**
     * show update dialog
     *
     * @param positiveStr positiveStr
     * @param negativeStr negativeStr
     * @param neutralStr  neutralStr
     */
    public void showUpdateDialog(FirAppInfo appInfo, String positiveStr, String negativeStr, String neutralStr) {
        showUpdateDialog(appInfo, positiveStr, negativeStr, neutralStr, false);
    }

    /**
     * show update dialog
     *
     * @param positiveStr positiveStr
     * @param negativeStr negativeStr
     * @param neutralStr  neutralStr
     * @param forceUpdate forceUpdate default false
     */
    public void showUpdateDialog(final FirAppInfo appInfo, String positiveStr, String negativeStr, String neutralStr, boolean forceUpdate) {
        View dialog = activity.getLayoutInflater().inflate(R.layout.dialog_update, null);
        TextView version = (TextView) dialog.findViewById(R.id.fir_version_tv);
        TextView time = (TextView) dialog.findViewById(R.id.fir_time_tv);
        TextView log = (TextView) dialog.findViewById(R.id.fir_log_tv);
        TextView size = (TextView) dialog.findViewById(R.id.fir_size_tv);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.string_finded);
        version.setText(formatStr(R.string.fir_string_new_version, appInfo.getAppVersionShort()));
        time.setText(formatStr(R.string.fir_string_new_time, appInfo.getAppTime()));
        log.setText(appInfo.getAppChangeLog());
        size.setText(formatStr(R.string.fir_string_new_size, appInfo.getAppSize()));
        builder.setView(dialog);
        builder.setPositiveButton(positiveStr, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadApk(appInfo);
            }
        });

        if (!TextUtils.isEmpty(negativeStr)) {
            if (!forceUpdate) builder.setNegativeButton(negativeStr, null);
        }

        if (!TextUtils.isEmpty(neutralStr)) {
            if (!forceUpdate)
                builder.setNeutralButton(neutralStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doIgnore();
                    }
                });
        }
        builder.setCancelable(!forceUpdate);
        try {
            builder.show();
        } catch (WindowManager.BadTokenException e) {
            Log.i(TAG, "showUpdateDialog: Unable to add window");
        }
    }

    private void registerReceiver() {
        completeReceiver = new CompleteReceiver();
        activity.registerReceiver(completeReceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * ignore
     */
    private void doIgnore() {

    }

    private String formatStr(int resId, String info) {
        return String.format(activity.getResources().getString(resId), info);
    }

    private void downLoadApk(FirAppInfo appInfo) {
        downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(appInfo.getAppDirectInstallUrl()));
        request.setDestinationInExternalFilesDir(activity, Environment.DIRECTORY_DOWNLOADS, "update.apk");
        request.setTitle(appInfo.getAppName());
        request.setDescription(appInfo.getAppVersionShort());
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(false);
        request.setMimeType("application/cn.trinea.download.file");
        downloadId = downloadManager.enqueue(request);
    }

    class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (completeDownloadId == downloadId) {
                if (getStatusById(downloadManager, downloadId, DownloadManager.COLUMN_STATUS)
                        == DownloadManager.STATUS_SUCCESSFUL) {
                    Toast.makeText(context, "新版本已下载成功", Toast.LENGTH_SHORT).show();
                    Uri downloadUri = downloadManager.getUriForDownloadedFile(downloadId);
                    if (downloadUri != null) {
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        install.setDataAndType(downloadUri, "application/vnd.android.package-archive");
                        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(install);
                    } else {
                        Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private int getStatusById(DownloadManager downloadManager, long downloadId, String columnName) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        int result = -1;
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                result = c.getInt(c.getColumnIndex(columnName));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

}
