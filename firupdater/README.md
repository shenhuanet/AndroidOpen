#FirUpdater

[ ![jCenter](https://img.shields.io/badge/version-2.0-yellowgreen.svg) ](https://dl.bintray.com/shenhuanetos/maven/com/shenhua/libs/fir-updater/2.0/)
[![Build Status](https://img.shields.io/travis/rust-lang/rust/master.svg)](https://bintray.com/shenhuanetos/maven/fir-updater)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

A fir.im-based Android online update library.

## how to use:

#####1.Dependencies
```
dependencies {
    compile 'com.shenhua.libs:firupdater:2.0'
}
```
#####2.Code
If you need automatic updates, use the following code
```java
FirUpdater.getInstance().updatesAuto(this,API_TOKEN);
```

If you need to manually update, use the following code
```java
FirUpdater.getInstance().updatesManual(this,API_TOKEN);
```

The most important thing is you must use the following code to release the FirUpdater!
```java
FirUpdater.getInstance().onDestroy();
```
## 关于作者
博客：http://blog.csdn.net/klxh2009<br>
简书：http://www.jianshu.com/u/12a81897d5bc

## License

    Copyright 2017 ShenhuaNet

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.