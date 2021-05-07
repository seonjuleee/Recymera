package org.tensorflow.lite.examples.classification;

public class SearchItemData {
    private int image;
    private String name;
    private int count;

    public SearchItemData(int image, String name, int count) {
        this.image = image;
        this.name = name;
        this.count = count;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
