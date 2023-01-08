package com.example.shakegraph;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Averages extends AppCompatActivity implements SensorEventListener {

    private TextView downstairsTextViewAvg;
    private TextView joggingTextViewAvg;
    private TextView sittingTextViewAvg;
    private TextView standingTextViewAvg;
    private TextView upstairsTextViewAvg;
    private TextView walkingTextViewAvg;

    private static final int N_SAMPLES = 200;
    private static List<Float> x;
    private static List<Float> y;
    private static List<Float> z;
    private ActivityClassifier classifier;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.averages);

        x = new ArrayList<>();
        y = new ArrayList<>();
        z = new ArrayList<>();
        classifier = new ActivityClassifier(getApplicationContext());

        downstairsTextViewAvg = (TextView) findViewById(R.id.downstairs_prob_averages);
        joggingTextViewAvg = (TextView) findViewById(R.id.jogging_prob_averages);
        sittingTextViewAvg = (TextView) findViewById(R.id.sitting_prob_averages);
        standingTextViewAvg = (TextView) findViewById(R.id.standing_prob_averages);
        upstairsTextViewAvg = (TextView) findViewById(R.id.upstairs_prob_averages);
        walkingTextViewAvg = (TextView) findViewById(R.id.walking_prob_averages);

        Button backButton = (Button) findViewById(R.id.back_main_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Averages.this, MainActivity.class);
                Averages.this.startActivity(intent1);
                setContentView(R.layout.activity_main);
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        activityPrediction();
        x.add(sensorEvent.values[0]);
        y.add(sensorEvent.values[1]);
        z.add(sensorEvent.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }


    public void activityPrediction() {
        if (x.size() == N_SAMPLES && y.size() == N_SAMPLES && z.size() == N_SAMPLES) {

            downstairsTextViewAvg.setText(Float.toString(round(PredictionService.val0, 2)));
            joggingTextViewAvg.setText(Float.toString(round(PredictionService.val1, 2)));
            sittingTextViewAvg.setText(Float.toString(round(PredictionService.val2, 2)));
            standingTextViewAvg.setText(Float.toString(round(PredictionService.val3, 2)));
            upstairsTextViewAvg.setText(Float.toString(round(PredictionService.val4, 2)));
            walkingTextViewAvg.setText(Float.toString(round(PredictionService.val5, 2)));

            x.clear();
            y.clear();
            z.clear();

        }
    }

    protected void onPause() {
        getSensorManager().unregisterListener(this);
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        getSensorManager().registerListener(this,
                getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
    }


    private SensorManager getSensorManager() {
        return (SensorManager) getSystemService(SENSOR_SERVICE);
    }
}

