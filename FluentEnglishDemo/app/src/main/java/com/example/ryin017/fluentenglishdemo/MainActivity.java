package com.example.ryin017.fluentenglishdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    FluentScoreView mFluentScoreView;
    Button mBtnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFluentScoreView = (FluentScoreView) findViewById(R.id.fluentScoreView);
        mBtnTest = (Button) findViewById(R.id.btn_test);
        mBtnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFluentScoreView.setScore(6023);
            }
        });
    }
}
