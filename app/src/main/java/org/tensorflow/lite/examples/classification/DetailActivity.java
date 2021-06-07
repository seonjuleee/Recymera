package org.tensorflow.lite.examples.classification;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class DetailActivity extends AppCompatActivity {

    private ArrayList<DetailItemData> arrayList;
    private DetailAdapter detailAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private int deviderColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        recyclerView = findViewById(R.id.rv_detail);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        // RecyclerView
        arrayList = new ArrayList<>();

        deviderColor = R.drawable.detail_devider;
        getIncomingIntent();
        detailAdapter = new DetailAdapter(arrayList, deviderColor);
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
        LinearLayout detailLayout = findViewById(R.id.detail_layout);
        switch (title) {
            case "종이류":
                Collections.addAll(items, res.getStringArray(R.array.paper));
                detailLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.detail_bg_green));
                deviderColor = R.drawable.detail_devider_green;
                break;
            case "플라스틱류":
                Collections.addAll(items, res.getStringArray(R.array.plastic));
                detailLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.detail_bg_blue));
                deviderColor = R.drawable.detail_devider_blue;
                break;
            case "캔류":
                Collections.addAll(items, res.getStringArray(R.array.metal));
                detailLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.detail_bg_gray));
                deviderColor = R.drawable.detail_devider_gray;
                break;
            case "유리류":
                Collections.addAll(items, res.getStringArray(R.array.glass));
                detailLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.detail_bg_orange));
                deviderColor = R.drawable.detail_devider_orange;
                break;
            case "의류":
                Collections.addAll(items, res.getStringArray(R.array.clothes));
                detailLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.detail_bg_pink));
                deviderColor = R.drawable.detail_devider_pink;
                break;
            case "폐건전지류":
                Collections.addAll(items, res.getStringArray(R.array.battery));
                detailLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.detail_bg_purple));
                deviderColor = R.drawable.detail_devider_purple;
                break;
            case "일반쓰레기":
                Collections.addAll(items, res.getStringArray(R.array.trash));
                detailLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.detail_bg_brown));
                deviderColor = R.drawable.detail_devider_brown;
                break;
        }
        for (String i : items) {
            // title과 content로 나누기
            String[] result = i.split(";");
            arrayList.add(new DetailItemData(result[0].toString(), result[1].toString()));
        }
    }

}