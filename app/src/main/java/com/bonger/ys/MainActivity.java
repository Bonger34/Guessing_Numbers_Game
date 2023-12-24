package com.bonger.ys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity {

    private int n;
    private int i = 1;
    private boolean isTrue = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SecureRandom secureRandom = new SecureRandom();
        String number = String.valueOf(secureRandom.nextInt(100) + 1);   //生成一个随机数
        n = Integer.parseInt(number);   //将该随机数转化为整数类型
        Button proceed = findViewById(R.id.proceed);
        proceed.setOnClickListener(view -> {
            checkGuess();   //在按钮点击事件中调用检查猜测的方法
        });
        Button reset = findViewById(R.id.reset);
        reset.setOnClickListener(view -> {
            resetGame();    //通过重置按钮重置游戏
        });
    }

    public void checkGuess() {
        TextView textView = findViewById(R.id.editText);    //将EditText强制向下转化为TextView
        String text = textView.getText().toString();    //获取转化后的TextView的值
        if (text.isEmpty()) {   // 对用户输入进行空值检查
            Toast.makeText(this, "错误：请输入数字", Toast.LENGTH_SHORT).show();
            return;
        }
        int num = Integer.parseInt(text);   //再将值转化为整数类型
        if (num < 1 || num > 100) {
            if (i == 1) {
                Toast.makeText(this, "错误：你输入的数不在范围内", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "错误：你输入的数不在范围内，还剩" + (5-i) + "次机会", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (num == n) {
            Toast.makeText(this, "你猜对了，共猜了" + i + "次", Toast.LENGTH_SHORT).show();
            isTrue = true;
            resetGame();    //猜对后自动重置
            return;
        } else if (num < n) {
            Toast.makeText(this, "你猜的数小了，还剩" + (5-i) + "次机会", Toast.LENGTH_SHORT).show();
        } else if (num > n) {
            Toast.makeText(this, "你猜的数大了，还剩" + (5-i) + "次机会", Toast.LENGTH_SHORT).show();
        }
        if (i == 5 && !isTrue) {
            Toast.makeText(this, "已回答" + i + "次，答题失败！\n正在下载原神……", Toast.LENGTH_SHORT).show();
            resetGame();
            Uri uri = Uri.parse("https://ys-api.mihoyo.com/event/download_porter/link/ys_cn/official/android_default");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else {
            i++;    //增加猜测次数
        }
    }

    private void resetGame() {
        SecureRandom secureRandom = new SecureRandom();
        String number = String.valueOf(secureRandom.nextInt(100) + 1);   //再次生成一个随机数
        n = Integer.parseInt(number);   //将该随机数转化为整数类型
        i = 1;  //重置猜测次数
        isTrue = false; //重置猜中标记
        TextView textView = findViewById(R.id.editText);    //将EditText强制向下转化为TextView
        textView.setText("");
        Toast.makeText(this, "已重置", Toast.LENGTH_SHORT).show();
    }
}