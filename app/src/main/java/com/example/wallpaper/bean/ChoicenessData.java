package com.example.wallpaper.bean;

/**
 * 精选
 */
public class ChoicenessData {
    private String id;//图片id
    private String thumb;//缩略图
    private String img;//预览图

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "ChoicenessData{" +
                "id='" + id + '\'' +
                ", thumb='" + thumb + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
