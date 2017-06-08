package me.boqin.androidjunitdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import me.boqin.androidjunitdemo.activity.CalculatorActivity;
import me.boqin.androidjunitdemo.activity.PersonalInfoActivity;

/**
 * Created by Boqin on 2017/6/6.
 * Modified by Boqin
 *
 * @Version
 */
public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(
            @Nullable
                    Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.calculator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CalculatorActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        findViewById(R.id.personal_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PersonalInfoActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }


}
