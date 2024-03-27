package com.bonger.ys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.color.DynamicColors;

import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity {

    private int randomNumber;  //随机数
    private int guessCount = 1;  //用户答题次数
    private boolean isTrue = false; //是否答题正确

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建通知渠道
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        SecureRandom secureRandom = new SecureRandom();
        String number = String.valueOf(secureRandom.nextInt(100) + 1);   //生成一个随机数
        randomNumber = Integer.parseInt(number);   //将该随机数转化为整数类型
        Button proceed = findViewById(R.id.proceed);
        proceed.setOnClickListener(view -> {
            checkGuess();   //在按钮点击事件中调用检查猜测的方法
        });
        Button reset = findViewById(R.id.reset);
        reset.setOnClickListener(view -> {
            resetGame();    //通过重置按钮重置游戏
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
            if (guessCount == 1) {
                Toast.makeText(this, "错误：你输入的数不在范围内", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "错误：你输入的数不在范围内，还剩" + (5 - guessCount) + "次机会", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (num == randomNumber) {
            Toast.makeText(this, "你猜对了，共猜了" + guessCount + "次", Toast.LENGTH_SHORT).show();
            isTrue = true;
            resetGame();    //猜对后自动重置
            return;
        } else if (num < randomNumber) {
            Toast.makeText(this, "你猜的数小了，还剩" + (5 - guessCount) + "次机会", Toast.LENGTH_SHORT).show();
        } else if (num > randomNumber) {
            Toast.makeText(this, "你猜的数大了，还剩" + (5 - guessCount) + "次机会", Toast.LENGTH_SHORT).show();
        }
        if (guessCount == 5 && !isTrue) {
            Toast.makeText(this, "已回答" + guessCount + "次，答题失败！\n正在下载原神……", Toast.LENGTH_SHORT).show();
            resetGame();
            Uri uri = Uri.parse("https://ys-api.mihoyo.com/event/download_porter/link/ys_cn/official/android_default");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else {
            guessCount++;    //增加猜测次数
        }
    }

    private void resetGame() {
        SecureRandom secureRandom = new SecureRandom();
        String number = String.valueOf(secureRandom.nextInt(100) + 1);   //再次生成一个随机数
        randomNumber = Integer.parseInt(number);   //将该随机数转化为整数类型
        guessCount = 1;  //重置猜测次数
        isTrue = false; //重置猜中标记
        TextView textView = findViewById(R.id.editText);    //将EditText强制向下转化为TextView
        textView.setText("");
        Toast.makeText(this, "已重置", Toast.LENGTH_SHORT).show();
        sendNotification(1, "重置成功");
    }

    public void sendNotification(int notificationId, String text) {
        // 创建NotificationCompat.Builder对象
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id").setContentText(text).setSmallIcon(R.mipmap.ic_launcher);
        // 获取NotificationManager实例
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 构建并发送通知
        notificationManager.notify(notificationId, builder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mune_about) {
            about();
        } else if (item.getItemId() == R.id.mune_setting) {
            Toast.makeText(this, "点击了设置~", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "点击其他选项", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public static class MyApplication extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            DynamicColors.applyToActivitiesIfAvailable(this);   //尝试使用莫奈主题
        }
    }

    public static class AboutDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.app_name)
                    .setMessage(R.string.dialog_about_message);
            return builder.create();
        }

    }

    public void about() {
        DialogFragment newFragment = new AboutDialogFragment();
        newFragment.show(getSupportFragmentManager(), "about");
    }


}