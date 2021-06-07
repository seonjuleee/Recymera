package org.tensorflow.lite.examples.classification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.CustomViewHolder> {

    private ArrayList<DetailItemData> arrayList;
    private int deviderColor;

    public DetailAdapter(ArrayList<DetailItemData> arrayList, int deviderColor) {
        this.arrayList = arrayList;
        this.deviderColor = deviderColor;
    }

    @NonNull
    @Override
    public DetailAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);
        holder.iv_devider.setImageResource(deviderColor);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DetailAdapter.CustomViewHolder holder, int position) {
        holder.tv_title.setText(arrayList.get(position).getTitle());
        holder.tv_content.setText(arrayList.get(position).getContent());

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public void remove(int position) {
        try {
            arrayList.remove(position);
            //삭제하고, arrayList를 리프레쉬한다
            notifyItemRemoved(position);
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView iv_devider;
        protected TextView tv_title, tv_content;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.detail_item_title);
            this.tv_content = itemView.findViewById(R.id.detail_item_content);
            this.iv_devider = itemView.findViewById(R.id.iv_devider);
        }
    }
}
