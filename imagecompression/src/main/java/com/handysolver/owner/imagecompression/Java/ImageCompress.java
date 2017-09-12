package com.handysolver.owner.imagecompression.Java;

import com.handysolver.owner.imagecompression.model.ImagePathContainer;

/**
 * Created by OWNER on 12-09-2017.
 */

public interface ImageCompress {
    static final String FAILURE_MESSAGE_1="Please provide real and thumbnail image folder path";
    static final String FAILURE_MESSAGE_2="Please provide image path";
    void onFailure(String message);
    void onSuccess(ImagePathContainer imagePathContainer);
}
