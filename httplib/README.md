# httplib
[ ![jCenter](https://img.shields.io/badge/version-1.0-yellowgreen.svg) ](https://dl.bintray.com/shenhuanetos/maven/com/shenhua/libs/httplib/1.0/)
[![Build Status](https://img.shields.io/travis/rust-lang/rust/master.svg)](https://bintray.com/shenhuanetos/maven/httpLibrary)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

An HTTP request encapsulation library for general api request testing.

## Usage:
```java
XXXXService service = HttpLib.getInstance().getBaseRetrofitService(this, URL).create(XXXXService.class);
Call<String> call = service.getString("js", 0, 6);
call.enqueue(new Callback<String>() {
    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        Log.d(TAG, "onResponse: " + response.body());
        mTextView.setText(response.body());
    }
    @Override
    public void onFailure(Call<String> call, Throwable t) {
        Log.d(TAG, "onFailure: ");
    }
});
```

## About Me
CSDN：http://blog.csdn.net/klxh2009<br>
JianShu：http://www.jianshu.com/u/12a81897d5bc

## License

    Copyright 2017 shenhuanet

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.