package org.tensorflow.lite.examples.classification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchActivity extends AppCompatActivity implements OnItemClickListener {

    private ArrayList<SearchItemData> arrayList, totalList, resultList, sortList;
    private ArrayList<String> keyBattery, keyClothes, keyGlass, keyMetal, keyPaper, keyPlastic, keyTrash;
    private SearchAdapter searchAdapter, resultAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private EditText et_searchBar;
    private Button btn_searchBar;
    private String str;
    private TextView tv_title, tv_title_result;
    private static final int REQUEST_CODE = 26;  // detailActivity와 연결을 위한 임의의 상수 값을 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        et_searchBar = findViewById(R.id.et_searchbar);
        btn_searchBar = findViewById(R.id.btn_searchbar);
        tv_title = findViewById(R.id.tv_searh);
        tv_title_result = findViewById(R.id.tv_searh_result);

        keyBattery = new ArrayList<>();
        keyClothes = new ArrayList<>();
        keyGlass = new ArrayList<>();
        keyMetal = new ArrayList<>();
        keyPaper = new ArrayList<>();
        keyPlastic = new ArrayList<>();

        // 검색 키워드를 정의
        Resources res = getResources();
        Collections.addAll(keyBattery, res.getStringArray(R.array.key_battery));
        Collections.addAll(keyClothes, res.getStringArray(R.array.key_clothes));
        Collections.addAll(keyGlass, res.getStringArray(R.array.key_glass));
        Collections.addAll(keyMetal, res.getStringArray(R.array.key_metal));
        Collections.addAll(keyPaper, res.getStringArray(R.array.key_paper));
        Collections.addAll(keyPlastic, res.getStringArray(R.array.key_plastic));


        keyTrash = new ArrayList<>();
        keyTrash.add("가위");
        keyTrash.add("거울");
//        keyTrash.add("깨진유리");

        // totalList 초기화
        totalList = new ArrayList<>();
        for (int i = 0; i < keyTrash.size(); i++) {
            totalList.add(new SearchItemData(R.drawable.icon_trash, keyTrash.get(i), 0));
        }
        for (int i = 0; i < keyPlastic.size(); i++) {
            totalList.add(new SearchItemData(R.drawable.icon_plastic, keyPlastic.get(i), 0));
        }
        for (int i = 0; i < keyPaper.size(); i++) {
            totalList.add(new SearchItemData(R.drawable.icon_paper, keyPaper.get(i), 0));
        }
        for (int i = 0; i < keyMetal.size(); i++) {
            totalList.add(new SearchItemData(R.drawable.icon_can, keyMetal.get(i), 0));
        }
        for (int i = 0; i < keyGlass.size(); i++) {
            totalList.add(new SearchItemData(R.drawable.icon_glass, keyGlass.get(i), 0));
        }
        for (int i = 0; i < keyClothes.size(); i++) {
            totalList.add(new SearchItemData(R.drawable.icon_tshirt, keyClothes.get(i), 0));
        }
        for (int i = 0; i < keyBattery.size(); i++) {
            totalList.add(new SearchItemData(R.drawable.icon_battery, keyBattery.get(i), 0));
        }

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
                // 이후 모든 키 이벤트를 실행시키지 않음
                return false;
            }
        });

        // RecyclerView
        recyclerView = (RecyclerView)findViewById(R.id.rv_search);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        arrayList = readSharedPreferences();

        showSortAdapter(arrayList);

        resultList = new ArrayList<>();
        resultAdapter = new SearchAdapter(resultList, SearchActivity.this, 1);

        et_searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {  // text에 변화가 있을 때마다
                str = et_searchBar.getText().toString();
                search(str);
            }
        });

    }

    public void search(String keyword) {
        if (keyword.length() == 0) {
            // 1. 검색 버튼 삭제
//            btn_searchBar.setVisibility(View.INVISIBLE);

            // 2. 출력될 정보의 제목 변경
            tv_title.setVisibility(TextView.VISIBLE);
            tv_title_result.setVisibility(TextView.GONE);
            showSortAdapter(arrayList);
        } else {
            resultList.clear();
            // 1. 검색 버튼 생성
//            btn_searchBar.setVisibility(View.VISIBLE);

            // 2. 출력될 정보의 제목 변경
            tv_title.setVisibility(TextView.GONE);
            tv_title_result.setVisibility(TextView.VISIBLE);

            // 3. resultList 만들기
            for (int i = 0; i < totalList.size(); i++) {
                if (totalList.get(i).getName().contains(str)) {
                    resultList.add(totalList.get(i));
                }
            }
            // result 내용을 갱신
            resultAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(resultAdapter);
        }
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
        } else if (requestCode == 25) {
            showSortAdapter(arrayList);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveSharedPreferences(arrayList);
    }

    private void saveSharedPreferences(ArrayList<SearchItemData> list) {
        // SharedPreferences로 데이터 save
        // JSON 파싱해서 다시 저장
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("SearchObjectList", json);
        editor.commit();
    }

    private ArrayList<SearchItemData> readSharedPreferences() {
        // SharedPreferences로 데이터 read
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("SearchObjectList", "");
        // JSON to object list
        Type type = new TypeToken<ArrayList<SearchItemData>>(){}.getType();
        ArrayList<SearchItemData> searchItemList = gson.fromJson(json, type);
        if (searchItemList == null) return new ArrayList<>();
        else return searchItemList;
    }

    private void showSortAdapter(ArrayList<SearchItemData> arrayList) {
        if (!arrayList.isEmpty()) {
            sortList = sortSearchItemData(arrayList);
        }
        else sortList = new ArrayList<>();
        searchAdapter = new SearchAdapter(sortList, this, 0);
        recyclerView.setAdapter(searchAdapter);
    }

    private ArrayList<SearchItemData> sortSearchItemData(ArrayList<SearchItemData> list) {
        // sort
        Collections.sort(list, new Comparator<SearchItemData>() {
            @Override
            public int compare(SearchItemData o1, SearchItemData o2) {
                return o2.getCount() - o1.getCount();
            }
        });

        // 5개만 뽑아서 저장하기
        ArrayList<SearchItemData> result = new ArrayList<>();
        if (list.size() > 5) {
            for (int i=0; i<5; i++) {
                result.add(list.get(i));
            }
        } else {
            for (SearchItemData item : list) {
                result.add(item);
            }
        }

        return result;
    }

    @Override
    public void onSortItemClick(int pos) {
        // sortList에서 클릭한 경우
        SearchItemData obj = sortList.get(pos);
        int index = findIndexByName(obj.getName(), arrayList);
        arrayList.get(index).setCount(arrayList.get(index).getCount() + 1);
    }

    @Override
    public void onResultItemClick (int pos) {
        // resultList에서 클릭한 경우
        SearchItemData obj = resultList.get(pos);
        if (arrayList.isEmpty()) {
            obj.setCount(1);
            arrayList.add(obj);
            return;
        }
        int index = findIndexByName(obj.getName(), arrayList);
        if (index != -1) {
            arrayList.get(index).setCount(arrayList.get(index).getCount() + 1);
        } else {
            obj.setCount(1);
            arrayList.add(obj);
        }
    }

    @Override
    public String onItemClick (String name) {
        return classifier(name);
    }

    // name으로 해당하는 인덱스 찾기
    public int findIndexByName(String name, ArrayList<SearchItemData> list) {
        for (int i=0; i<list.size(); i++) {
            if (list.get(i).getName().equals(name)) return i;
        }
        return -1;
    }
}

