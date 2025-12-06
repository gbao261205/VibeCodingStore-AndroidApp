package com.vibecoding.flowerstore.Model;

public class SlideItem {
    // Chúng ta chỉ cần một trường để giữ resource ID của ảnh
    private int image;

    public SlideItem(int image) {
        this.image = image;
    }

    public int getImage() {
        return image;
    }
}
