package org.tensorflow.lite.examples.classification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class DetailActivity extends AppCompatActivity {

    private ArrayList<DetailItemData> arrayList;
    private DetailAdapter detailAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        recyclerView = findViewById(R.id.rv_detail);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        // RecyclerView
        arrayList = new ArrayList<>();


        getIncomingIntent();
        detailAdapter = new DetailAdapter(arrayList);
        recyclerView.setAdapter(detailAdapter);
    }

    // intent에서 값 가져오기
   private void getIncomingIntent() {
        if(getIntent().hasExtra("title")) {
            String title = getIntent().getStringExtra("title");
            setDetailTitle(title);
            setDetailContent(title);
        }
    }

    // activity_detail.xml 파일에서 title 설정
    private void setDetailTitle(String title) {
        TextView tv_title = findViewById(R.id.detail_title);
        tv_title.setText(title);
    }

    private void setDetailContent(String title) {
        // string-resource
        Resources res = getResources();
        ArrayList<String> items = new ArrayList<>();
        switch (title) {
            case "종이류":
                Collections.addAll(items, res.getStringArray(R.array.paper));
                break;
            case "플라스틱류":
                Collections.addAll(items, res.getStringArray(R.array.plastic));
                break;
            case "캔류":
                Collections.addAll(items, res.getStringArray(R.array.metal));
                break;
            case "유리류":
                Collections.addAll(items, res.getStringArray(R.array.glass));
                break;
            case "의류":
                Collections.addAll(items, res.getStringArray(R.array.clothes));
                break;
            case "폐건전지":
                Collections.addAll(items, res.getStringArray(R.array.battery));
                break;
            case "일반쓰레기":
                Collections.addAll(items, res.getStringArray(R.array.trash));
                break;
        }
        System.out.println(1);
        for (String i : items) {
            // title과 content로 나누기
            System.out.println(2);
            String[] result = i.split(";");
            arrayList.add(new DetailItemData(result[0].toString(), result[1].toString()));
        }
    }

}