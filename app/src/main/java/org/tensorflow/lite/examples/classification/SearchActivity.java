package org.tensorflow.lite.examples.classification;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class SearchActivity extends AppCompatActivity {

    private ArrayList<SearchItemData> arrayList;
    ArrayList<String> keyBattery, keyClothes, keyGlass, keyMetal, keyPaper, keyPlastic, keyTrash;
    private SearchAdapter searchAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private EditText et_searchBar;
    private Button btn_searchBar;
    private String str;
    private static final int REQUEST_CODE = 26;  // detailActivity와 연결을 위한 임의의 상수 값을 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        et_searchBar = findViewById(R.id.et_searchbar);
        btn_searchBar = findViewById(R.id.btn_searchbar);

        // editText가 활성화 되었을 때 버튼 생성
        et_searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                btn_searchBar.setVisibility(View.VISIBLE);
            }
        });

        // 검색 키워드를 정의
        keyBattery = new ArrayList<>();
        keyBattery.add("건전지");

        keyClothes = new ArrayList<>();
        keyClothes.add("가방");
        keyClothes.add("커튼");
        keyClothes.add("면티");

        keyGlass = new ArrayList<>();
        keyGlass.add("주스병");
        keyGlass.add("콜라병");

        keyMetal = new ArrayList<>();
        keyMetal.add("부탄가스");
        keyMetal.add("음료수캔");
        keyMetal.add("철사");
        keyMetal.add("못");

        keyPaper = new ArrayList<>();
        keyPaper.add("우유팩");
        keyPaper.add("신문");
        keyPaper.add("공책");
        keyPaper.add("종이컵");
        keyPaper.add("상자");

        keyPlastic = new ArrayList<>();
        keyPlastic.add("페트병");
        keyPlastic.add("플라스틱용기");

        keyTrash = new ArrayList<>();
        keyTrash.add("가위");
        keyTrash.add("거울");
        keyTrash.add("깨진유리");


        // 버튼을 눌렀을 때 + 엔터키를 눌렀을 때 intent가 실행
        btn_searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = et_searchBar.getText().toString().replace(" ","");
                // 빈 문자열인지 체크
                if (str.length() > 0) {
                    str = classifier(str);
                    Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                    intent.putExtra("title", str);
                    //startActivity(intent);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    // editText를 초기화
                    et_searchBar.getText().clear();
                }
            }
        });

        et_searchBar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    str = et_searchBar.getText().toString().replace(" ","");
                    if (str.length() > 0) {
                        str = classifier(str);
                        Intent intent = new Intent(SearchActivity.this, DetailActivity.class);

                        intent.putExtra("title", str);
                        //startActivity(intent);
                        startActivityForResult(intent, REQUEST_CODE);
                    } else {
                        // editText를 초기화
                        et_searchBar.getText().clear();
                    }
                }
                return true;
            }
        });

        // RecyclerView
        recyclerView = (RecyclerView)findViewById(R.id.rv_search);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();

        arrayList.add(new SearchItemData(R.drawable.icon_plastic, "플라스틱류"));
        arrayList.add(new SearchItemData(R.drawable.icon_glass, "유리류"));
        arrayList.add(new SearchItemData(R.drawable.icon_paper, "종이류"));


        searchAdapter = new SearchAdapter(arrayList);
        recyclerView.setAdapter(searchAdapter);


        SearchAdapter searchAdapter = new SearchAdapter(arrayList);
        RecyclerView recyclerView = findViewById(R.id.rv_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchAdapter);
    }

    // 검색 키워드를 특정 종류로 일반화하는 함수
    private String classifier(String keyword) {
        if (keyBattery.contains(keyword)) {
            return "폐건전지";
        } else if (keyClothes.contains(keyword)) {
            return "의류";
        } else if (keyGlass.contains(keyword)) {
            return "유리류";
        } else if (keyMetal.contains(keyword)) {
            return "캔류";
        } else if (keyPaper.contains(keyword)) {
            return "종이류";
        } else if (keyPlastic.contains(keyword)) {
            return "플라스틱류";
        } else {
            return "일반쓰레기";
        }
     }

     // detailAcitivy에서 돌아왔을 때 실행되는 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            et_searchBar.getText().clear();
            et_searchBar.clearFocus();
        }
    }
}
