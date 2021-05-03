package org.tensorflow.lite.examples.classification;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.CustomViewHolder> {

    private ArrayList<MainItemData> arrayList;

    public MainAdapter(ArrayList<MainItemData> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MainAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.CustomViewHolder holder, int position) {
        holder.iv_main_item.setImageResource(arrayList.get(position).getImage());
        holder.tv_main_item.setText(arrayList.get(position).getName());

        // 클릭 이벤트 리스너
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailActivity.class);
                intent.putExtra("title", holder.tv_main_item.getText());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView iv_main_item;
        protected TextView tv_main_item;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_main_item = (ImageView) itemView.findViewById(R.id.iv_main_item);
            this.tv_main_item = (TextView) itemView.findViewById(R.id.tv_main_item);
        }
    }
}
