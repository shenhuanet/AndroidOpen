/*
 * Copyright 2017 shenhuanet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shenhua.backgroundkeepalive;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Created by shenhua on 2017-11-08-0008.
 *
 * @author shenhua
 *         Email shenhuanet@126.com
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    static {
        System.loadLibrary("keep-alive");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void start(View view) {
        startJniService("com.shenhua.backgroundkeepalive");
    }

    public void stop(View view) {
        Intent intent = new Intent(this, TestService.class);
        stopService(intent);
    }

    private native void startJniService(String pkgName);

    private static void fromJNI(int a) {
        Log.d(TAG, "received from JNI : " + a);
    }
}
