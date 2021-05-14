package org.tensorflow.lite.examples.classification;

public interface OnItemClickListener {
    String onItemClick (String name);
    void onSortItemClick(int pos);
    void onResultItemClick (int pos);
}
