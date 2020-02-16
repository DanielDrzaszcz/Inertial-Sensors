package com.dandrzas.inertialsensorsviewer;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.SimpleDateFormat;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.content.ContextCompat;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

public class SensorsDataReadService extends IntentService implements SensorEventListener {
    private static final String ACTION_START = "com.dandrzas.inertialsensorsviewer.action.START";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private SensorsDataRepository sensorsDataRepository;
    private Sensor mMagnetometer;
    private Sensor mGyroscope;
    private CSVWriter csvWriterAccelerometer;
    private String[] csvDataAccelerometer;
    private CSVWriter csvWriterGyroscope;
    private String[] csvDataGyroscope;
    private CSVWriter csvWriterMagnetometer;
    private String[] csvDataMagnetometer;
    private long startTime;
    public static volatile boolean isEnable =  false;
    public static volatile boolean shouldContinueService = true;

    public SensorsDataReadService() {
        super("SensorsDataReadService");
    }

    public static void start(Context context) {
        shouldContinueService = true;
        Intent intent = new Intent(context, SensorsDataReadService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    public static void stop(Context context)
    {
        shouldContinueService = false;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                try {
                    handleActionStart();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void handleActionStart() throws IOException {

        isEnable = true;
        SimpleDateFormat dateSimpleFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.GERMANY);
        Date dateActual = new Date();
        String dateActualString = dateSimpleFormat.format(dateActual);

       if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
        {
            File filesCatalog = new File(Environment.getExternalStorageDirectory() + "/InertialSensorsViewer/");
            if (!filesCatalog.exists()) filesCatalog.mkdir();

            csvWriterAccelerometer = new CSVWriter(new FileWriter(filesCatalog.getPath() + "/" + dateActualString + "_Accelerometer.csv"));
            csvDataAccelerometer = "Czas [sek]#Akcelerometr X [m/s2]#Akcelerometr Y [m/s2]Y#Akcelerometr Z [m/s2]".split("#");
            csvWriterAccelerometer.writeNext(csvDataAccelerometer);

            csvWriterGyroscope = new CSVWriter(new FileWriter(filesCatalog.getPath() + "/" + dateActualString + "_Gyroscope.csv"));
            csvDataGyroscope = "Czas [sek]#Zyroskop X [rad/s]#Zyroskop Y[rad/s]Y#Zyroskop Z [rad/s]".split("#");
            csvWriterGyroscope.writeNext(csvDataGyroscope);

            csvWriterMagnetometer = new CSVWriter(new FileWriter(filesCatalog.getPath() + "/" + dateActualString + "_Magnetometer.csv"));
            csvDataMagnetometer = "Czas [sek]#Magnetometr X [uT]#Magnetometr Y [uT]#Magnetometr Z [uT]".split("#");
            csvWriterMagnetometer.writeNext(csvDataMagnetometer);
        }

        sensorsDataRepository = SensorsDataRepository.getInstance();
        mSensorManager = (SensorManager) getApplication().getSystemService(Context.SENSOR_SERVICE);
        startTime = SystemClock.elapsedRealtimeNanos();

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
       if (mAccelerometer!=null)
       {
           sensorsDataRepository.setMinDelayAccelerometer(mAccelerometer.getMinDelay()/1000);
           mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
       }

        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(mGyroscope!=null)
        {
            sensorsDataRepository.setMinDelayGyroscope(mGyroscope.getMinDelay()/1000);
            mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        }


        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(mMagnetometer!=null)
        {
            sensorsDataRepository.setMinDelayMagnetometer(mMagnetometer.getMinDelay()/1000);
            mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        }

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(sensorsDataRepository!=null) {
            if (event.sensor == mAccelerometer) {
                sensorsDataRepository.setAccelerometerValue(event.values);
                csvDataAccelerometer = (((float)(event.timestamp-startTime)/1000000000)+"#"+event.values[0]+"#"+event.values[1]+"#"+event.values[2]).split("#");
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    csvWriterAccelerometer.writeNext(csvDataAccelerometer);
                }
            } else if (event.sensor == mGyroscope) {
                sensorsDataRepository.setGyroscopeValue(event.values);
                csvDataGyroscope = (((float)(event.timestamp-startTime)/1000000000)+"#"+event.values[0]+"#"+event.values[1]+"#"+event.values[2]).split("#");
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    csvWriterGyroscope.writeNext(csvDataGyroscope);
                }

            } else if (event.sensor == mMagnetometer) {
                sensorsDataRepository.setMagnetometerValue(event.values);
                Log.d("MagnetometerTest: ", Float.toString(event.values[0]) + " "+ Float.toString(event.values[1]) + " "+Float.toString(event.values[2]));

                csvDataMagnetometer = (((float)(event.timestamp-startTime)/1000000000)+"#"+event.values[0]+"#"+event.values[1]+"#"+event.values[2]).split("#");
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    csvWriterMagnetometer.writeNext(csvDataMagnetometer);
                }
            }
        }
        if(!shouldContinueService) {
            mSensorManager.unregisterListener(this);
            isEnable = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public static boolean isEnable() {
        return isEnable;
    }

}
