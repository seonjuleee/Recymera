package org.tensorflow.lite.examples.classification;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private ArrayList<SearchItemData> arrayList;
    private SearchAdapter searchAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
}
