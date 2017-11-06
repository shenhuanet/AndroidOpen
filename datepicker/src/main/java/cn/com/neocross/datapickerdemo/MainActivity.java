package cn.com.neocross.datapickerdemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.NumberPicker;
import android.widget.Toast;

import cn.com.neocross.datapickerdemo.view.DatePicker;

public class MainActivity extends AppCompatActivity {

    String birthday;
    String horoscope;
    public NumberPicker valuePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        valuePicker = (NumberPicker) findViewById(R.id.valuepicker);
//        String[] city = {"立水桥", "霍营", "回龙观", "龙泽", "西二旗", "上地"};
//        valuePicker.setDisplayedValues(city);
//        valuePicker.setMinValue(0);
//        valuePicker.setWrapSelectorWheel(false);
//        valuePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
//        valuePicker.setMaxValue(city.length - 1);
//        valuePicker.setValue(4);

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

//        OptionPicker horoPicker = new OptionPicker(this, new String[]{
//                "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座", "水瓶座", "双鱼座"
//        });
//        horoPicker.setOffset(2);
//        horoPicker.setSelectedIndex(2);
//        horoPicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
//            @Override
//            public void onOptionPicked(String option) {
//                horoscope = option;
//            }
//        });
//        horoPicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                Toast.makeText(MainActivity.this, horoscope, Toast.LENGTH_SHORT).show();
//            }
//        });
//        horoPicker.show();

    }

}
