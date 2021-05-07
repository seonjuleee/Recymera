package org.tensorflow.lite.examples.classification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.CustomViewHolder>{

    private ArrayList<SearchItemData> arrayList;

    public SearchAdapter(ArrayList<SearchItemData> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public SearchAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        SearchAdapter.CustomViewHolder holder = new SearchAdapter.CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.CustomViewHolder holder, int position) {
        holder.iv_search_item.setImageResource(arrayList.get(position).getImage());
        holder.tv_search_item.setText(arrayList.get(position).getName());

        // 클릭 이벤트 리스너
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = arrayList.get(position).getCount();
                arrayList.get(position).setCount(++count);
                String curName = holder.tv_search_item.getText().toString();
                Toast.makeText(v.getContext(), curName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView iv_search_item;
        protected TextView tv_search_item;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_search_item = (ImageView) itemView.findViewById(R.id.iv_search_item);
            this.tv_search_item = (TextView) itemView.findViewById(R.id.tv_search_item);

        }
    }
}
