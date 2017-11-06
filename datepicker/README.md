# DatePicker

[Download](https://github.com/shenhuanet/AndroidOpen/raw/master/--Downloads/DatePickerDemo.zip)

比较优美的选择控件,包含 值选择框、日期选择框、星座选择框.

## 使用
#### 值选择框
```java
        NumberPicker valuePicker = (NumberPicker) findViewById(R.id.valuepicker);
        String[] city = {"立水桥", "霍营", "回龙观", "龙泽", "西二旗", "上地"};
        valuePicker.setDisplayedValues(city);
        valuePicker.setMinValue(0);
        valuePicker.setWrapSelectorWheel(false);
        valuePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        valuePicker.setMaxValue(city.length - 1);
        valuePicker.setValue(4);
```
#### 日期选择框
```java
        DatePicker birthdayPicker = new DatePicker(this, DatePicker.YEAR_MONTH_DAY);
        birthdayPicker.setRange(1900, 2016);//年份范围
        birthdayPicker.setLabel("", "", "");
        birthdayPicker.setSelectedItem(2012, 8, 8);
        birthdayPicker.setAnimationStyle(R.style.Animation_Popup);
        birthdayPicker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                birthday = year + "年" + month + "月" + day + "日";
                System.out.println(birthday);
            }
        });
        birthdayPicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Toast.makeText(MainActivity.this, birthday, Toast.LENGTH_SHORT).show();
            }
        });
        birthdayPicker.show();
```
#### 星座选择框
```java
        OptionPicker horoPicker = new OptionPicker(this, new String[]{
                "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座", "水瓶座", "双鱼座"
        });
        horoPicker.setOffset(2);
        horoPicker.setSelectedIndex(2);
        horoPicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(String option) {
                horoscope = option;
            }
        });
        horoPicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Toast.makeText(MainActivity.this, horoscope, Toast.LENGTH_SHORT).show();
            }
        });
        horoPicker.show();
```
## 效果预览
<div align="center">
	<img width="300" height="533" src="https://github.com/shenhuanet/AndroidOpen/blob/master/datepicker/screenshot/img_value_picker.png"/>
	<img width="300" height="533" src="https://github.com/shenhuanet/AndroidOpen/blob/master/datepicker/screenshot/img_date_picker.png"/>
	<img width="300" height="533" src="https://github.com/shenhuanet/AndroidOpen/blob/master/datepicker/screenshot/img_option_picker.png"/>
</div>

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