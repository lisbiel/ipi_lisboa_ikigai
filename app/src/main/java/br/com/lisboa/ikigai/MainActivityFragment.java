package br.com.lisboa.ikigai;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivityFragment extends Fragment {

    private DoodleView doodleView;

    private float acceleration;
    private float lastAcceleration;
    private float currentAcceleration;

    private boolean dialogOnScreen = false;

    private static final int ACCELERATION_THRESHOLD = 1000000;

    private static final int SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View raiz =
                inflater.inflate (
                        R.layout.fragment_main,
                        container,
                        false
                );
        setHasOptionsMenu(true);
        doodleView = raiz.findViewById(R.id.doodleView);
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;
        acceleration = 0.0f;
        return raiz;
    }

    private SensorEventListener listener =
            new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    lastAcceleration = currentAcceleration;
                    currentAcceleration =
                            x * x + y * y + z * z;

                    acceleration = currentAcceleration *
                            (currentAcceleration - lastAcceleration);

                    if (acceleration > ACCELERATION_THRESHOLD){
                        confirmErase();
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };

    private void confirmErase(){
        EraseImageDialogFragment eraseImageDialogFragment =
                new EraseImageDialogFragment();
        eraseImageDialogFragment.show(getFragmentManager(), "erase dialog");
    }

    private void enableAccelerometerListening(){
        SensorManager sensorManager = (SensorManager)
                getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.
                registerListener(
                        listener,
                        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_NORMAL
                );
    }

    private void disableAccelerometerListener (){
        SensorManager sensorManager = (SensorManager)
                getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(
                listener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        disableAccelerometerListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableAccelerometerListening();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.doodle_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.color:
                ColorDialogFragment colorDialogFragment
                        = new ColorDialogFragment();
                colorDialogFragment.show(getFragmentManager(), "color dialog");
                return true;
            case R.id.auto_draw:
                doodleView.drawIkigai();
                return true;
            case R.id.delete_drawing:
                confirmErase();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImage(){
        if (getContext().
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.permission_explanation);
                builder.setPositiveButton(android.R.string.ok, (d, w)->{
                    requestPermissions(
                            new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            SAVE_IMAGE_PERMISSION_REQUEST_CODE
                    );
                });
                builder.create().show();
            }
            else{
                requestPermissions(
                        new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        SAVE_IMAGE_PERMISSION_REQUEST_CODE
                );
            }
        }
        else{
            //doodleView.saveImage();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case SAVE_IMAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //doodleView.saveImage();
                }
                else{
                    Toast.makeText(getActivity(),
                            getString(R.string.permission_explanation),
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    public DoodleView getDoodleView (){
        return doodleView;
    }

    public void setDialogOnScreen(boolean dialogOnScreen) {
        this.dialogOnScreen = dialogOnScreen;
    }
}

