package org.tensorflow.lite.examples.classification;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class SearchActivity extends AppCompatActivity {

    private ArrayList<SearchItemData> arrayList;
    private ArrayList<String> keyMetal, keyPlastic;
    private SearchAdapter searchAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private EditText et_searchBar;
    private Button btn_searchBar;
    private String str;

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
        keyMetal = new ArrayList<>();
        keyMetal.add("캔류");
        keyMetal.add("캔");

        keyPlastic = new ArrayList<>();
        keyPlastic.add("플라스틱류");
        keyPlastic.add("플라스틱");

        // 버튼을 눌렀을 때 + 엔터키를 눌렀을 때 intent가 실행
        btn_searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = classifier(et_searchBar.getText().toString());
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                intent.putExtra("str", str);
                startActivity(intent);
            }
        });

        et_searchBar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        str = classifier(et_searchBar.getText().toString());
                        Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                        intent.putExtra("str", str);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        // RecyclerView
        recyclerView = (RecyclerView)findViewById(R.id.rv_search);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();

        arrayList.add(new SearchItemData(R.drawable.ic_launcher_foreground, "플라스틱류"));
        arrayList.add(new SearchItemData(R.drawable.ic_launcher_foreground, "유리류"));
        arrayList.add(new SearchItemData(R.drawable.ic_launcher_foreground, "종이류"));


        searchAdapter = new SearchAdapter(arrayList);
        recyclerView.setAdapter(searchAdapter);


        SearchAdapter searchAdapter = new SearchAdapter(arrayList);
        RecyclerView recyclerView = findViewById(R.id.rv_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchAdapter);
    }

    // 검색 키워드를 특정 종류로 일반화하는 함수
    private String classifier(String keyword) {
        if (keyPlastic.contains(keyword)) {
            return "플라스틱류";
        } else {
            return "캔류";
        }
    }
}
