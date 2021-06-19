package org.tensorflow.lite.examples.classification;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity{

    private ArrayList<MainItemData> arrayList;
    private MainAdapter mainAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private Button btn_search, btn_camera;

    // shake event 변수
    private SensorManager sensorManager;
    private float accel;
    private float accelCurrent;
    private float accelLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_search = findViewById(R.id.btn_search) ;
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        btn_camera = findViewById(R.id.btn_camera) ;
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ClassifierActivity.class);
                startActivity(intent);
            }
        });

        // RecyclerView
        recyclerView = (RecyclerView)findViewById(R.id.rv_main);
//        linearLayoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        arrayList = new ArrayList<>();

        arrayList.add(new MainItemData(R.drawable.icon_plastic, "플라스틱류"));
        arrayList.add(new MainItemData(R.drawable.icon_glass, "유리류"));
        arrayList.add(new MainItemData(R.drawable.icon_paper, "종이류"));
        arrayList.add(new MainItemData(R.drawable.icon_can, "캔류"));
        arrayList.add(new MainItemData(R.drawable.icon_tshirt, "의류"));
        arrayList.add(new MainItemData(R.drawable.icon_battery, "폐건전지류"));
        arrayList.add(new MainItemData(R.drawable.icon_trash, "일반쓰레기"));


        mainAdapter = new MainAdapter(arrayList);
        recyclerView.setAdapter(mainAdapter);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(sensorManager).registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        accel = 10f;
        accelCurrent = SensorManager.GRAVITY_EARTH; //GRAVITY_EARTH : 지구 중력
        accelLast = SensorManager.GRAVITY_EARTH;
    }

    // 센서 정보가 변하면 실행된다.
    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            //Sensor값 가져오기 : 중력가속도 값
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            accelLast = accelCurrent;
            //각속도를 계산
            accelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            //업데이트 된 속도를 구하기 위해서 delta 값 구함
            float delta = accelCurrent - accelLast;
            //delta를 현재 속도와 계산
            accel = accel * 0.9f + delta;
            //x,y,z축의 가속도 값이 12이면 흔들렸다고 판단
            if (accel > 12) {
                Intent intent = new Intent(getApplicationContext(), ClassifierActivity.class);
                startActivity(intent);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    @Override
    protected void onResume() {
        //SensorEventListener 등록
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        //SensorEventListener 해제 : 배터리 소모를 절약한다
        sensorManager.unregisterListener(sensorListener);
        super.onPause();
    }
}