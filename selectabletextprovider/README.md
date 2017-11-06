# SelectableTextProvider

[ ![Download](https://api.bintray.com/packages/shenhuanetos/maven/selectableTextProvider/images/download.svg) ](https://bintray.com/shenhuanetos/maven/selectableTextProvider/_latestVersion)

A text view that supports selection and expansion.

## Screenshot:
<div align="center">
    <img width="300" height="533" src="https://github.com/shenhuanet/AndroidOpen/blob/master/selectabletextprovider/img_screenshot.png"/>
</div>

## Usage:
* gradle dependencies:
``` java
compile 'com.shenhua.libs:selectableTextProvider:1.0.1'
```
* simple use:
``` java
new SelectableTextProvider.Builder(TextView textView).build();
```
* more function:
```java
SelectableTextProvider selectableTextProvider = new SelectableTextProvider.Builder(textView)
     .setCursorHandleColor(Color.GREEN)
     .setCursorHandleSizeInDp(20)
     .setSelectedColor(Color.BLUE)
     .build();
selectableTextProvider.setSelectListener(new OnSelectListener() {
     @Override
     public void onTextSelected(CharSequence content) {
         Log.d(TAG, "onTextSelected: " + content);
     }
});
```
## OnKeyDown:
If you need to let it respond to the **return KeyEvent** to disappear event, you can do this:
```java
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        if (selectableTextProvider.isShowing()){
            selectableTextProvider.hide();
        }
        return true;
    }
    return super.onKeyDown(keyCode, event);
}
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
