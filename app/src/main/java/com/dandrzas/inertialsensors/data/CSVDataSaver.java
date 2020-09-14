package com.dandrzas.inertialsensors.data;

import com.dandrzas.inertialsensors.ui.movement.MovementTraceViewModel;
import com.opencsv.CSVWriter;

import java.io.File;
import java.util.Locale;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorEvent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class CSVDataSaver implements Observer {
    private CSVWriter csvWriterAccelerometer;
    private String[] csvDataAccelerometer;
    private CSVWriter csvWriterGyroscope;
    private String[] csvDataGyroscope;
    private CSVWriter csvWriterMagnetometer;
    private String[] csvDataMagnetometer;
    private CSVWriter csvWriterOrientation;
    private String[] csvDataOrientation;
    private CSVWriter csvWriterMovement;
    private String[] csvDataMovement;
    private long startTime;
    private File filesCatalog;
    private Context context;
    private DataManager dataManager;
    private static CSVDataSaver ourInstance;

    /*
    CSVDataSaver(){
    }
*/


    private CSVDataSaver() {
    }

    public static CSVDataSaver getInstance() {
        if (ourInstance == null) {
            ourInstance = new CSVDataSaver();
        }
        return ourInstance;
    }

    public void init(Context context) throws IOException {
        this.context = context;
        // Utworzenie katalogu do zapisu danych
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 29) {
                filesCatalog = new File(context.getExternalFilesDir(null).getPath());
            } else {
                filesCatalog = new File(Environment.getExternalStorageDirectory() + "/InertialSensorsViewer/");
                if (!filesCatalog.exists()) filesCatalog.mkdir();
            }
        }
        dataManager = DataManager.getInstance();
        dataManager.getAlgorithmComplementaryInstance().addObserver(this);
        dataManager.getSystemAlgrithmInstance().addObserver(this);
        dataManager.getAlgorithmWithoutFusionInstance().addObserver(this);
        dataManager.getInertialTrackingAlgorithmInstance().addObserver(this);
        dataManager.getAlgorithmMadgwickFilter().addObserver(this);
        dataManager.getAlgorithmMahonyFilter().addObserver(this);
        dataManager.getAlgorithmKalmanFilter().addObserver(this);
    }

    public void saveDataAccelerometer(SensorEvent event) {

        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            csvDataAccelerometer = (((float) (event.timestamp - startTime) / 1000000000) + "#" + event.values[0] + "#" + event.values[1] + "#" + event.values[2]).split("#");
            csvWriterAccelerometer.writeNext(csvDataAccelerometer);
        }
    }

    public void saveDataGyroscope(SensorEvent event) {
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            csvDataGyroscope = (((float) (event.timestamp - startTime) / 1000000000) + "#" + event.values[0] + "#" + event.values[1] + "#" + event.values[2]).split("#");
            csvWriterGyroscope.writeNext(csvDataGyroscope);

        }
    }

    public void saveDataMagnetometer(SensorEvent event) {
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            csvDataMagnetometer = (((float) (event.timestamp - startTime) / 1000000000) + "#" + event.values[0] + "#" + event.values[1] + "#" + event.values[2]).split("#");
            csvWriterMagnetometer.writeNext(csvDataMagnetometer);
        }
    }

    private void saveDataOrientation(float[] values, long timestamp) {

        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            csvDataOrientation = (((float) (timestamp - startTime) / 1000000000) + "#" + values[0] + "#" + values[1] + "#" + values[2]).split("#");
            csvWriterOrientation.writeNext(csvDataOrientation);
        }
    }

    private void saveDataMovement(float[] values, long timestamp) {

        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            csvDataMovement = (((float) (timestamp - startTime) / 1000000000) + "#" + values[0] + "#" + values[1] + "#" + values[2]).split("#");
            csvWriterMovement.writeNext(csvDataMovement);
        }
    }

    public void createFile() throws IOException {

        // Pobranie aktualnej daty i godziny do zapisania jako nazwa pliku CSV
        startTime = SystemClock.elapsedRealtimeNanos();
        SimpleDateFormat dateSimpleFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.GERMANY);
        Date dateActual = new Date();
        String dateActualString = dateSimpleFormat.format(dateActual);

        csvWriterAccelerometer = new CSVWriter(new FileWriter(filesCatalog.getPath() + "/" + dateActualString + "_Accelerometer.csv"));
        csvDataAccelerometer = "Czas [sek]#Akcelerometr X [m/s2]#Akcelerometr Y [m/s2]Y#Akcelerometr Z [m/s2]".split("#");
        csvWriterAccelerometer.writeNext(csvDataAccelerometer);

        csvWriterGyroscope = new CSVWriter(new FileWriter(filesCatalog.getPath() + "/" + dateActualString + "_Gyroscope.csv"));
        csvDataGyroscope = "Czas [sek]#Zyroskop X [rad/s]#Zyroskop Y[rad/s]Y#Zyroskop Z [rad/s]".split("#");
        csvWriterGyroscope.writeNext(csvDataGyroscope);

        csvWriterMagnetometer = new CSVWriter(new FileWriter(filesCatalog.getPath() + "/" + dateActualString + "_Magnetometer.csv"));
        csvDataMagnetometer = "Czas [sek]#Magnetometr X [uT]#Magnetometr Y [uT]#Magnetometr Z [uT]".split("#");
        csvWriterMagnetometer.writeNext(csvDataMagnetometer);

        csvWriterOrientation = new CSVWriter(new FileWriter(filesCatalog.getPath() + "/" + dateActualString + "_Orientation.csv"));
        csvDataOrientation = "Czas [sek]#Rotacja X [°]#Rotacja Y [°]Y#Rotacja Z [°]".split("#");
        csvWriterOrientation.writeNext(csvDataOrientation);

        csvWriterMovement = new CSVWriter(new FileWriter(filesCatalog.getPath() + "/" + dateActualString + "_Movement.csv"));
        csvDataMovement = "Czas [sek]#Przemieszczenie X [m]#Przemieszczenie Y [m]Y#Przemieszczenie Z [m]".split("#");
        csvWriterMovement.writeNext(csvDataMovement);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof IFOrientationAlgorithm) {
            if (arg.equals(dataManager.getSelectedAlgorithm())) {
                saveDataOrientation(((IFOrientationAlgorithm) o).getRollPitchYaw(false), ((IFOrientationAlgorithm) o).getSampleTime());
            }
        }
        else if (o instanceof InertialTrackingAlgorithm) {
                saveDataMovement(((InertialTrackingAlgorithm) o).getCalculatedMovement(), (((InertialTrackingAlgorithm) o).getActualSampleTime()));
        }
    }
}
