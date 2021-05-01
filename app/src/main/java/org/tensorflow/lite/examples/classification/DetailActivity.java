package org.tensorflow.lite.examples.classification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ArrayList<DetailItemData> arrayList;
    private DetailAdapter detailAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getIncomingIntent();

        recyclerView = findViewById(R.id.rv_detail);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        // RecyclerView
        arrayList = new ArrayList<>();

        arrayList.add(new DetailItemData("신문", "물기에 젖지 않도록 하고 반득하게 펴서 쌓은 후 묶어서 배출한다"));
        arrayList.add(new DetailItemData("책자, 노트", "다른 재질 부분(플라스틱 표지, 스프링 등)은 가급적 제거하여 배출한다"));
        arrayList.add(new DetailItemData("상자", "상자에 붙어있는 테이프,철핀,택배영수증 등 이물질을 제거한 후 배출한다"));

        detailAdapter = new DetailAdapter(arrayList);
        recyclerView.setAdapter(detailAdapter);
    }

    private void getIncomingIntent() {
        if(getIntent().hasExtra("text_title")) {
            String title = getIntent().getStringExtra("text_title");
            setDetailTitle(title);
        }
    }

    private void setDetailTitle(String title) {
        TextView tv_title = findViewById(R.id.detail_title);
        tv_title.setText(title);
    }

}