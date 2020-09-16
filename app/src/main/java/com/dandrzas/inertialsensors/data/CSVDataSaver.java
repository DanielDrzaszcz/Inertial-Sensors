package com.dandrzas.inertialsensors.data;

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
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;

import androidx.core.content.ContextCompat;

public class CSVDataSaver implements Observer {
    private CSVWriter csvWriterAccelerometer;
    private CSVWriter csvWriterGyroscope;
    private CSVWriter csvWriterMagnetometer;
    private CSVWriter csvWriterAccelerometerRaw;
    private CSVWriter csvWriterMagnetometerRaw;
    private CSVWriter csvWriterOrientSystemAlgorithm;
    private CSVWriter csvWriterOrientNoFusionAlgorithm;
    private CSVWriter csvWriterOrientComplementaryAlgorithm;
    private CSVWriter csvWriterOrientKalmanAlgorithm;
    private CSVWriter csvWriterOrientMahonyAlgorithm;
    private CSVWriter csvWriterOrientMadgwickAlgorithm;
    private CSVWriter csvWriterMovement;
    private CSVWriter csvWriterStepDetectAlgorithm;
    private long startTime;
    private File filesCatalog;
    private Context context;
    private DataManager dataManager;
    private static CSVDataSaver ourInstance;


    public static CSVDataSaver getInstance() {
        if (ourInstance == null) {
            ourInstance = new CSVDataSaver();
        }
        return ourInstance;
    }

    public void init(Context context) throws IOException {
        this.context = context;
        // Creating folder
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
        dataManager.getStepDetectAlgorithm().addObserver(this);
    }

    public void createFile() throws IOException {

        // Getting date&time to use as a name of a file
        startTime = SystemClock.elapsedRealtimeNanos();
        SimpleDateFormat dateSimpleFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.GERMANY);
        Date dateActual = new Date();
        String dateActualString = dateSimpleFormat.format(dateActual);

        //Creating csv files
        csvWriterAccelerometer = buildCSVWriter(dateActualString + "_Accelerometer.csv");
        csvWriterAccelerometer.writeNext("Czas [sek]#Akcelerometr X [m/s2]#Akcelerometr Y [m/s2]Y#Akcelerometr Z [m/s2]".split("#"));

        csvWriterGyroscope = buildCSVWriter(dateActualString + "_Gyroscope.csv");
        csvWriterGyroscope.writeNext("Czas [sek]#Zyroskop X [rad/s]#Zyroskop Y[rad/s]Y#Zyroskop Z [rad/s]".split("#"));

        csvWriterMagnetometer = buildCSVWriter(dateActualString + "_Magnetometer.csv");
        csvWriterMagnetometer.writeNext("Czas [sek]#Magnetometr X [uT]#Magnetometr Y [uT]#Magnetometr Z [uT]".split("#"));

        csvWriterAccelerometerRaw = buildCSVWriter(dateActualString + "_Accelerometer_Raw.csv");
        csvWriterAccelerometerRaw.writeNext("Czas [sek]#Akcelerometr X [m/s2]#Akcelerometr Y [m/s2]Y#Akcelerometr Z [m/s2]".split("#"));

        csvWriterMagnetometerRaw = buildCSVWriter(dateActualString + "_Magnetometer_Raw.csv");
        csvWriterMagnetometerRaw.writeNext("Czas [sek]#Magnetometr X [uT]#Magnetometr Y [uT]#Magnetometr Z [uT]".split("#"));

        csvWriterOrientSystemAlgorithm = buildCSVWriter(dateActualString + "_Orientation_System_Algorithm.csv");
        csvWriterOrientSystemAlgorithm.writeNext(getOrientationColumnsTitle());

        csvWriterOrientNoFusionAlgorithm = buildCSVWriter(dateActualString + "_Orientation_No_Fusion_Algorithm.csv");
        csvWriterOrientNoFusionAlgorithm.writeNext(getOrientationColumnsTitle());

        csvWriterOrientComplementaryAlgorithm = buildCSVWriter(dateActualString + "_Orientation_Complementary_Filter.csv");
        csvWriterOrientComplementaryAlgorithm.writeNext(getOrientationColumnsTitle());

        csvWriterOrientKalmanAlgorithm = buildCSVWriter(dateActualString + "_Orientation_Kalman_Filter.csv");
        csvWriterOrientKalmanAlgorithm.writeNext(getOrientationColumnsTitle());

        csvWriterOrientMahonyAlgorithm = buildCSVWriter(dateActualString + "_Orientation_Mahony_Filter.csv");
        csvWriterOrientMahonyAlgorithm.writeNext(getOrientationColumnsTitle());

        csvWriterOrientMadgwickAlgorithm = buildCSVWriter(dateActualString + "_Orientation_Madgwick_Filter.csv");
        csvWriterOrientMadgwickAlgorithm.writeNext(getOrientationColumnsTitle());

        csvWriterMovement = buildCSVWriter(dateActualString + "_Movement.csv");
        csvWriterMovement.writeNext("Czas [sek]#Przyspieszenie X[m/s2]#Przyspieszenie Y[m/s2]#Przyspieszenie Z[m/s2]#Predkosc X[m/s]#Predkosc Y[m/s]#Predkosc Z[m/s]#Przemieszczenie X[m]#Przemieszczenie Y[m]Y#Przemieszczenie Z[m]".split("#"));

        csvWriterStepDetectAlgorithm = buildCSVWriter(dateActualString + "_Step_detection_algorithm.csv");
        csvWriterStepDetectAlgorithm.writeNext("Czas [sek]#Przyspieszenie[m/s2]#Przyspieszenie srednia[m/s2]#Przyspieszenie wariancja[m/s2]#Prog 1#Prog 2# Krok".split("#"));


    }

    @Override
    public void update(Observable o, Object arg) {
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            if (o instanceof IFOrientationAlgorithm) {
                CSVWriter csvWriter;
                switch ((int) arg) {
                    case Constants.SYSTEM_ALGORITHM_ID:
                        csvWriter = csvWriterOrientSystemAlgorithm;
                        break;
                    case Constants.ORIENTATION_WITHOUT_FUSION:
                        csvWriter = csvWriterOrientNoFusionAlgorithm;
                        break;
                    case Constants.COMPLEMENTARY_FILTER_ID:
                        csvWriter = csvWriterOrientComplementaryAlgorithm;
                        break;
                    case Constants.KALMAN_FILTER_ID:
                        csvWriter = csvWriterOrientKalmanAlgorithm;
                        break;
                    case Constants.MAHONY_FILTER_ID:
                        csvWriter = csvWriterOrientMahonyAlgorithm;
                        break;
                    case Constants.MADGWICK_FILTER_ID:
                        csvWriter = csvWriterOrientMadgwickAlgorithm;
                        break;
                    default:
                        csvWriter = csvWriterOrientSystemAlgorithm;
                }
                long sampleTime = ((IFOrientationAlgorithm) o).getSampleTime();
                float[] orientationValue = ((IFOrientationAlgorithm) o).getRollPitchYaw(false);
                String[] csvData = (((float) (sampleTime - startTime) / 1000000000) + "#" + orientationValue[0] + "#" + orientationValue[1] + "#" + orientationValue[2]).split("#");
                csvWriter.writeNext(csvData);

            } else if (o instanceof InertialTrackingAlgorithm) {
                InertialTrackingAlgorithm instance = (InertialTrackingAlgorithm) o;
                float[] acceleration = instance.getLinearAcceleration();
                float[] velocity = instance.getCalculatedVelocity();
                float[] movement = instance.getCalculatedMovement();
                long sampleTime = instance.getActualSampleTime();
                String[] csvData = (((float) (sampleTime - startTime) / 1000000000) + "#" + acceleration[0] + "#" + acceleration[1] + "#" + acceleration[2] + "#" + velocity[0] + "#" + velocity[1] + "#" + velocity[2] + "#" + movement[0] + "#" + movement[1] + "#" + movement[2]).split("#");
                csvWriterMovement.writeNext(csvData);

            } else if (o instanceof StepDetectAlgorithm) {
                StepDetectAlgorithm instance = (StepDetectAlgorithm) o;
                float accMagnitude = instance.getAccelerationMagnitude();
                float accAverage = instance.getAccelerationAverage();
                float accVariance = instance.getAccelerationVariance();
                float treshold1 = instance.getThreshold1Value();
                float treshold2 = instance.getThreshold2Value();
                float steps = instance.getStepsCounter();
                long sampleTime = instance.getActualSampleTime();
                String[] csvData = (((float) (sampleTime - startTime) / 1000000000) + "#" + accMagnitude + "#" + accAverage + "#" + accVariance + "#" + treshold1 + "#" + treshold2 + "#" + steps).split("#");
                csvWriterStepDetectAlgorithm.writeNext(csvData);
            }
        }
    }

    public void saveDataAccelerometer(float[] values, float[] valuesRaw, long sampleTime) {
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            String[] csvData = (((float) (sampleTime - startTime) / 1000000000) + "#" + values[0] + "#" + values[1] + "#" + values[2]).split("#");
            String[] csvDataRaw = (((float) (sampleTime - startTime) / 1000000000) + "#" + valuesRaw[0] + "#" + valuesRaw[1] + "#" + valuesRaw[2]).split("#");
            csvWriterAccelerometer.writeNext(csvData);
            csvWriterAccelerometerRaw.writeNext(csvDataRaw);
        }
    }

    public void saveDataGyroscope(float[] values, long sampleTime) {
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            String[] csvData = (((float) (sampleTime - startTime) / 1000000000) + "#" + values[0] + "#" + values[1] + "#" + values[2]).split("#");
            csvWriterGyroscope.writeNext(csvData);
        }
    }

    public void saveDataMagnetometer(float[] values, float[] valuesRaw, long sampleTime) {
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            String[] csvData = (((float) (sampleTime - startTime) / 1000000000) + "#" + values[0] + "#" + values[1] + "#" + values[2]).split("#");
            String[] csvDataRaw = (((float) (sampleTime - startTime) / 1000000000) + "#" + valuesRaw[0] + "#" + valuesRaw[1] + "#" + valuesRaw[2]).split("#");
            csvWriterMagnetometer.writeNext(csvData);
            csvWriterMagnetometerRaw.writeNext(csvDataRaw);
        }
    }

    public void closeFiles()throws IOException{
        csvWriterAccelerometer.close();
        csvWriterGyroscope.close();
        csvWriterMagnetometer.close();
        csvWriterAccelerometerRaw.close();
        csvWriterMagnetometerRaw.close();
        csvWriterOrientSystemAlgorithm.close();
        csvWriterOrientNoFusionAlgorithm.close();
        csvWriterOrientComplementaryAlgorithm.close();
        csvWriterOrientKalmanAlgorithm.close();
        csvWriterOrientMahonyAlgorithm.close();
        csvWriterOrientMadgwickAlgorithm.close();
        csvWriterMovement.close();
        csvWriterStepDetectAlgorithm.close();
    }

    private void saveOrientationData(float[] values, long timestamp, CSVWriter csvWriter) {
         String[] csvData = (((float) (timestamp - startTime) / 1000000000) + "#" + values[0] + "#" + values[1] + "#" + values[2]).split("#");
         csvWriter.writeNext(csvData);
    }

    private String[] getOrientationColumnsTitle(){
        return "Czas [sek]#Rotacja X[deg]#Rotacja Y[deg]#Rotacja Z[deg]".split("#");
    }

    private CSVWriter buildCSVWriter(String fileTitle)throws IOException{
        return new CSVWriter(new FileWriter(filesCatalog.getPath() + "/" + fileTitle));
    }

}
