package com.handysolver.owner.imagecompression.model;

/**
 * Created by OWNER on 11-09-2017.
 */

public class ImagePathContainer {
    private String realImagePath, thumbnailImagePath;
    public ImagePathContainer(){}

    public String getRealImagePath() {
        return realImagePath;
    }

    public void setRealImagePath(String realImagePath) {
        this.realImagePath = realImagePath;
    }

    public String getThumbnailImagePath() {
        return thumbnailImagePath;
    }

    public void setThumbnailImagePath(String thumbnailImagePath) {
        this.thumbnailImagePath = thumbnailImagePath;
    }
}
