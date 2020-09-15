package com.dandrzas.inertialsensors;

import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.dandrzas.inertialsensors.data.Constants;
import com.dandrzas.inertialsensors.data.DataManager;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            ListPreference selectedAlgorithmPreference = findPreference("selected_algorithm");
            if (selectedAlgorithmPreference != null) {
                selectedAlgorithmPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                            switch ((String) newValue) {
                                case "system_default_algorithm":
                                    DataManager.getInstance().setSelectedAlgorithm(Constants.SYSTEM_ALGORITHM_ID);
                                    break;
                                case "orientation_without_fusion":
                                    DataManager.getInstance().setSelectedAlgorithm(Constants.ORIENTATION_WITHOUT_FUSION);
                                    break;
                                case "complementary_filter":
                                    DataManager.getInstance().setSelectedAlgorithm(Constants.COMPLEMENTARY_FILTER_ID);
                                    break;
                                case "kalman_filter":
                                    DataManager.getInstance().setSelectedAlgorithm(Constants.KALMAN_FILTER_ID);
                                    break;
                                case "mahony_filter":
                                    DataManager.getInstance().setSelectedAlgorithm(Constants.MAHONY_FILTER_ID);
                                    break;
                                case "madgwick_filter":
                                    DataManager.getInstance().setSelectedAlgorithm(Constants.MADGWICK_FILTER_ID);
                                    break;
                            }
                            return true;
                        }

                );
            }

            // Complementary filter config
            EditTextPreference compl_filter_par_alfa = findPreference("compl_filter_par_alfa");
            if (compl_filter_par_alfa != null) {
                compl_filter_par_alfa.setOnBindEditTextListener(
                        new EditTextPreference.OnBindEditTextListener() {
                            @Override
                            public void onBindEditText(@NonNull EditText editText) {
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            }
                        });
            }
            compl_filter_par_alfa.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    DataManager.getInstance().getAlgorithmComplementaryInstance().setParamAlfa(Float.parseFloat(newValue.toString()));
                    return true;
                }
            });


            // Kalman filter config
            EditTextPreference kalman_par_q_angle = findPreference("kalman_par_q_angle");
            if (kalman_par_q_angle != null) {
                kalman_par_q_angle.setOnBindEditTextListener(
                        new EditTextPreference.OnBindEditTextListener() {
                            @Override
                            public void onBindEditText(@NonNull EditText editText) {
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            }
                        });
            }
            kalman_par_q_angle.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    DataManager.getInstance().getAlgorithmKalmanFilter().setParQAngle(Float.parseFloat(newValue.toString()));
                    return true;
                }
            });
            EditTextPreference kalman_par_q_bias = findPreference("kalman_par_q_bias");
            if (kalman_par_q_bias != null) {
                kalman_par_q_bias.setOnBindEditTextListener(
                        new EditTextPreference.OnBindEditTextListener() {
                            @Override
                            public void onBindEditText(@NonNull EditText editText) {
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            }
                        });
            }
            kalman_par_q_bias.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    DataManager.getInstance().getAlgorithmKalmanFilter().setParQBias(Float.parseFloat(newValue.toString()));
                    return true;
                }
            });
            EditTextPreference kalman_par_r = findPreference("kalman_par_r");
            if (kalman_par_r != null) {
                kalman_par_r.setOnBindEditTextListener(
                        new EditTextPreference.OnBindEditTextListener() {
                            @Override
                            public void onBindEditText(@NonNull EditText editText) {
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            }
                        });
            }
            kalman_par_r.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    DataManager.getInstance().getAlgorithmKalmanFilter().setParRMeasure(Float.parseFloat(newValue.toString()));
                    return true;
                }
            });

            // Madgwick filter config
            EditTextPreference madgwick_par_beta = findPreference("madgwick_par_beta");
            if (madgwick_par_beta != null) {
                madgwick_par_beta.setOnBindEditTextListener(
                        new EditTextPreference.OnBindEditTextListener() {
                            @Override
                            public void onBindEditText(@NonNull EditText editText) {
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            }
                        });
            }
            madgwick_par_beta.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    DataManager.getInstance().getAlgorithmMadgwickFilter().setParBeta(Float.parseFloat(newValue.toString()));
                    return true;
                }
            });


            // Mahony filter config
            EditTextPreference mahony_par_ki = findPreference("mahony_par_ki");
            if (mahony_par_ki != null) {
                mahony_par_ki.setOnBindEditTextListener(
                        new EditTextPreference.OnBindEditTextListener() {
                            @Override
                            public void onBindEditText(@NonNull EditText editText) {
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            }
                        });
            }
            mahony_par_ki.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    DataManager.getInstance().getAlgorithmMahonyFilter().setParKi(Float.parseFloat(newValue.toString()));
                    return true;
                }
            });
            EditTextPreference mahony_par_kp = findPreference("mahony_par_kp");
            if (mahony_par_kp != null) {
                mahony_par_kp.setOnBindEditTextListener(
                        new EditTextPreference.OnBindEditTextListener() {
                            @Override
                            public void onBindEditText(@NonNull EditText editText) {
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            }
                        });
            }
            mahony_par_kp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    DataManager.getInstance().getAlgorithmMahonyFilter().setParKp(Float.parseFloat(newValue.toString()));
                    return true;
                }
            });


            // Accelerometer filter enable field config
            SwitchPreference accelerometerFilterEnable = findPreference("accelerometer_filter_enable");
            if (accelerometerFilterEnable != null) {
                accelerometerFilterEnable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        DataManager.getInstance().getAccelerometer().setFilterLowPassEnable(Boolean.parseBoolean(newValue.toString()));
                        return true;
                    }
                });
            }

            // Accelerometer filter gain field config
            EditTextPreference accelerometerFilterGain = findPreference("accelerometer_low_pass_gain");
            if (accelerometerFilterGain != null) {
                accelerometerFilterGain.setOnBindEditTextListener(
                        editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                );
                accelerometerFilterGain.setOnPreferenceChangeListener((preference, newValue) -> {
                    DataManager.getInstance().getAccelerometer().setFilterLowPassGain(Float.parseFloat(newValue.toString()));
                    return true;
                });
            }

            // Magnetomter filter enable field config
            SwitchPreference magnetometerFilterEnable = findPreference("magnetometer_filter_enable");
            if (magnetometerFilterEnable != null) {
                magnetometerFilterEnable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        DataManager.getInstance().getMagnetometer().setFilterLowPassEnable(Boolean.parseBoolean(newValue.toString()));
                        return true;
                    }
                });
            }

            // Magnetometer filter gain field config
            EditTextPreference magnetometerFilterGain = findPreference("magnetometer_low_pass_gain");
            if (magnetometerFilterGain != null) {
                magnetometerFilterGain.setOnBindEditTextListener(
                        editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                );
                magnetometerFilterGain.setOnPreferenceChangeListener((preference, newValue) -> {
                    DataManager.getInstance().getMagnetometer().setFilterLowPassGain(Float.parseFloat(newValue.toString()));
                    return true;
                });
            }


            /////////////////
            // Step detect algorithm parameters

            // Step detect algorithm window width
            EditTextPreference stepDetectWindowWidth = findPreference("step_detect_algorithm_window_width");
            if (stepDetectWindowWidth != null) {
                stepDetectWindowWidth.setOnBindEditTextListener(
                        editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
            }
            stepDetectWindowWidth.setOnPreferenceChangeListener((preference, newValue) -> {
                DataManager.getInstance().getStepDetectAlgorithm().setParWindowSize(Integer.parseInt(newValue.toString()));
                return true;
            });

            // Step detect algorithm threshold 1
            EditTextPreference stepDetectThreshold1 = findPreference("step_detect_algorithm_threshold_1");
            if (stepDetectThreshold1 != null) {
                stepDetectThreshold1.setOnBindEditTextListener(
                        editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL));
            }
            stepDetectThreshold1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    DataManager.getInstance().getStepDetectAlgorithm().setParThreshold1(Float.parseFloat(newValue.toString()));
                    return true;
                }
            });

            // Step detect algorithm threshold 2
            EditTextPreference stepDetectThreshold2 = findPreference("step_detect_algorithm_threshold_2");
            if (stepDetectThreshold2 != null) {
                stepDetectThreshold2.setOnBindEditTextListener(
                        editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL));
            }
            stepDetectThreshold2.setOnPreferenceChangeListener((preference, newValue) -> {
                DataManager.getInstance().getStepDetectAlgorithm().setParThreshold2(Float.parseFloat(newValue.toString()));
                return true;
            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id== android.R.id.home ){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}