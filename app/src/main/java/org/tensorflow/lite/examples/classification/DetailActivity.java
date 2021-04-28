package org.tensorflow.lite.examples.classification;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getIncomingIntent();
    }

    private void getIncomingIntent() {
        if(getIntent().hasExtra("text_title")) {
            String title = getIntent().getStringExtra("text_title");
            setDetailTitle(title);
        }
    }

    private void setDetailTitle(String title) {
        TextView tvDetailTitle = findViewById(R.id.tv_detail_title);
        tvDetailTitle.setText(title);
    }
}