package org.tensorflow.lite.examples.classification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<MainItemData> arrayList;
    private MainAdapter mainAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private Button btn_search, btn_camera;

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
    }
}