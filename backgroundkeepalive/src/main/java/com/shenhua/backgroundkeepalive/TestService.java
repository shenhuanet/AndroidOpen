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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by shenhua on 2017-11-08-0008.
 *
 * @author shenhua
 *         Email shenhuanet@126.com
 */
public class TestService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("shenhuaLog -- " + TestService.class.getSimpleName(), "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("shenhuaLog -- " + TestService.class.getSimpleName(), "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("shenhuaLog -- " + TestService.class.getSimpleName(), "onDestroy: ");
    }
}
