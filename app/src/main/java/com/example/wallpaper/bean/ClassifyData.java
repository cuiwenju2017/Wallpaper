package com.example.wallpaper.bean;

/**
 * 分类
 */
public class ClassifyData {
    private String id;//图片id
    private String name;//中文名
    private String cover;//封面

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Override
    public String toString() {
        return "ClassifyData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", cover='" + cover + '\'' +
                '}';
    }
}
