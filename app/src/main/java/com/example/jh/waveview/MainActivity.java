package com.example.jh.waveview;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * 关于水波纹的制作。自定义view
 */
public class MainActivity extends AppCompatActivity {

    private WaveView mWaveView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWaveView = (WaveView) findViewById(R.id.wave_view);
        button = (Button) findViewById(R.id.search);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置初始半径
//                mWaveView.setInitialRadius(100);
                mWaveView.setDuration(5000);
                mWaveView.setStyle(Paint.Style.FILL);
                mWaveView.setColor(Color.RED);
                mWaveView.setInterpolator(new LinearOutSlowInInterpolator());
                mWaveView.start();

                mWaveView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWaveView.stop();
                    }
                }, 5000);
            }
        });



/*        mWaveView.setDuration(5000);
        mWaveView.setStyle(Paint.Style.STROKE);
        mWaveView.setSpeed(400);
        mWaveView.setColor(Color.RED);
        mWaveView.setInterpolator(new AccelerateInterpolator(1.2f));
        mWaveView.start();*/




    }
}