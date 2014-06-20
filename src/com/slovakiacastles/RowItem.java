package com.slovakiacastles;

public class RowItem {
    private int imageId;
    private String title;
    private String desc;
    private String dist;
 
    public RowItem(int imageId, String title, String dist) {
        this.imageId = imageId;
        this.title = title;
        this.desc = desc;
        this.dist = dist;
    }
    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public String getDesc() {
        return desc;
    }
    public String getDist() {
        return dist;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDist(String dist) {
        this.dist = dist;
    }
    @Override
    public String toString() {
        return title + "\n" + desc;
    }
}