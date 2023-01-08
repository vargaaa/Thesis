package com.example.shakegraph;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.hardware.SensorManager;
import android.os.Build;

import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class PredictionService extends Service implements SensorEventListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final int N_SAMPLES = 200;
    private static List<Float> x;
    private static List<Float> y;
    private static List<Float> z;
    private float[] results;
    private float[] activity_count = new float[6];
    private ActivityClassifier classifier;
    public static float val0, val1, val2, val3, val4, val5;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        x = new ArrayList<>();
        y = new ArrayList<>();
        z = new ArrayList<>();
        classifier = new ActivityClassifier(getApplicationContext());

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        activityPrediction();
        x.add(event.values[0]);
        y.add(event.values[1]);
        z.add(event.values[2]);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        getSensorManager().registerListener(this,
                getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);

        createNotificationChannel();

        Intent intent1 = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                intent1,
                PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, "ChannelId1")
                .setContentTitle("Human Activity Recognition")
                .setContentText("App Running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();

        startForeground(1, notification);

        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();

    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    "ChannelId1", "Foreground Notification", NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        startService(new Intent(this, PredictionService.class));
        super.onTaskRemoved(rootIntent);
    }


    public void activityPrediction() {

        if (x.size() == N_SAMPLES && y.size() == N_SAMPLES && z.size() == N_SAMPLES) {

            List<Float> data = new ArrayList<>();
            data.addAll(x);
            data.addAll(y);
            data.addAll(z);

            results = classifier.predictProbabilities(toFloatArray(data));

            float max = (float) 0.1;
            for (int i = 0; i < results.length; i++) {
                if (results[i] > max) {
                    max = results[i];
                    activity_count[i]++;
                    Login.totalCounts++;
                }
            }

            val0 = (activity_count[0] / Login.totalCounts) * 100;
            val1 = (activity_count[1] / Login.totalCounts) * 100;
            val2 = (activity_count[2] / Login.totalCounts) * 100;
            val3 = (activity_count[3] / Login.totalCounts) * 100;
            val4 = (activity_count[4] / Login.totalCounts) * 100;
            val5 = (activity_count[5] / Login.totalCounts) * 100;

            databaseWriting();

            x.clear();
            y.clear();
            z.clear();

            databaseWriting();
        }
    }


    public void databaseWriting() {
        DatabaseReference ref1 = database.getReference("Results").child(MainActivity.currentUser).child("Current values").child("Downstairs");
        ref1.setValue(String.valueOf(round(results[0], 2)));
        DatabaseReference ref2 = database.getReference("Results").child(MainActivity.currentUser).child("Current values").child("Jogging");
        ref2.setValue(String.valueOf(round(results[1], 2)));
        DatabaseReference ref3 = database.getReference("Results").child(MainActivity.currentUser).child("Current values").child("Sitting");
        ref3.setValue(String.valueOf(round(results[2], 2)));
        DatabaseReference ref4 = database.getReference("Results").child(MainActivity.currentUser).child("Current values").child("Standing");
        ref4.setValue(String.valueOf(round(results[3], 2)));
        DatabaseReference ref5 = database.getReference("Results").child(MainActivity.currentUser).child("Current values").child("Upstairs");
        ref5.setValue(String.valueOf(round(results[4], 2)));
        DatabaseReference ref6 = database.getReference("Results").child(MainActivity.currentUser).child("Current values").child("Walking");
        ref6.setValue(String.valueOf(round(results[5], 2)));

        // AVERAGES
        DatabaseReference ref7 = database.getReference("Results").child(MainActivity.currentUser).child("Averages").child("Downstairs");
        ref7.setValue(String.valueOf(round(val0, 2)));
        DatabaseReference ref8 = database.getReference("Results").child(MainActivity.currentUser).child("Averages").child("Jogging");
        ref8.setValue(String.valueOf(round(val1, 2)));
        DatabaseReference ref9 = database.getReference("Results").child(MainActivity.currentUser).child("Averages").child("Sitting");
        ref9.setValue(String.valueOf(round(val2, 2)));
        DatabaseReference ref10 = database.getReference("Results").child(MainActivity.currentUser).child("Averages").child("Standing");
        ref10.setValue(String.valueOf(round(val3, 2)));
        DatabaseReference ref11 = database.getReference("Results").child(MainActivity.currentUser).child("Averages").child("Upstairs");
        ref11.setValue(String.valueOf(round(val4, 2)));
        DatabaseReference ref12 = database.getReference("Results").child(MainActivity.currentUser).child("Averages").child("Walking");
        ref12.setValue(String.valueOf(round(val5, 2)));

    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private float[] toFloatArray(List<Float> list) {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }

    private SensorManager getSensorManager() {
        return (SensorManager) getSystemService(SENSOR_SERVICE);
    }


}
