package com.handysolver.owner.imagecompression.Java;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.handysolver.owner.imagecompression.GlobalConstant;
import com.handysolver.owner.imagecompression.ui.MainActivity;

/**
 * Created by OWNER on 11-09-2017.
 */

public abstract class ImageCompressor {
    private static String TAG="ImageCompression";
    private String realImageFolderPath;
    private String thumbnailImageFolderPath;
    private String imageStringPath;

    private float realImageMaxWidth ;
    private  float realImageMaxHeight;
    private  float thumbnailImageMaxWidth ;
    private  float thumbnailImageMaxHeight ;

    private Uri imageUriPath;

    public abstract void start(ImageCompress imageCompress);
    public void init(Activity activity) {

    }
    public Intent getIntent(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        return intent;
    }
    public static ImageCompressor.CompressActivity create(Activity activity) {
        return new ImageCompressor.CompressActivity(activity);
    }
    public static class CompressActivity extends ImageCompressor {
        private Activity activity;
        private MarshMallowPermission marshMallowPermission;
        public CompressActivity(Activity activity) {
            this.activity = activity;
            this.init(activity);
        }

        public void start(ImageCompress imageCompress) {
            if(getRealImageFolderPath()!=null && getThumbnailImageFolderPath()!=null){
                if(getRealImageMaxHeight()==0.0f){
                    setRealImageMaxHeight(GlobalConstant.realImageMaxHeight);
                }
                if(getRealImageMaxWidth()==0.0f){
                    setRealImageMaxWidth(GlobalConstant.realImageMaxWidth);
                }
                if(getThumbnailImageMaxHeight()==0.0f){
                    setThumbnailImageMaxHeight(GlobalConstant.thumbnailImageMaxHeight);
                }
                if(getThumbnailImageMaxWidth()==0.0f){
                    setThumbnailImageMaxWidth(GlobalConstant.thumbnailImageMaxWidth);
                }

                // check if string path exist
                String imagePath=null;
                if(getImageStringPath()!=null){
                    imagePath=getImageStringPath();
                }else if(getImageUriPath()!=null){
                    imagePath=getRealPathFromURI(getImageUriPath());
                }

                if(imagePath==null){
                    imageCompress.onFailure(ImageCompress.FAILURE_MESSAGE_2);
                    return;
                }
                // call image compression
                ImageCompression imageCompression=new ImageCompression(activity);
                imageCompression.setRealImageMaxHeight(getRealImageMaxHeight());
                imageCompression.setRealImageMaxWidth(getRealImageMaxWidth());
                imageCompression.setThumbnailImageMaxHeight(getThumbnailImageMaxHeight());
                imageCompression.setThumbnailImageMaxWidth(getThumbnailImageMaxWidth());
                imageCompression.setThumbnailImageFolderPath(getThumbnailImageFolderPath());
                imageCompression.setRealImageFolderPath(getRealImageFolderPath());
                imageCompression.setImageCompress(imageCompress);

                imageCompression.execute(imagePath);
            }else{
                Log.d(TAG, "start: ");
                imageCompress.onFailure(ImageCompress.FAILURE_MESSAGE_1);
            }

            checkPermission();
        }
        public String getRealPathFromURI(Uri uri) {
            String[] projection = { MediaStore.Images.Media.DATA };
            @SuppressWarnings("deprecation")
            Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        public void checkPermission(){
            marshMallowPermission=new MarshMallowPermission(activity);
            if(marshMallowPermission.checkPermissionForExternalStorage()){
                Log.d("permission access","permission for call access");

            }else{
                Intent intent = this.getIntent(this.activity);
                this.activity.startActivityForResult(intent, GlobalConstant.externalStoragePermissionRequestCode);
                Log.d("permission access","permission for call denied");
            }

        }
    }

    public String getRealImageFolderPath() {
        return realImageFolderPath;
    }

    public ImageCompressor setRealImageFolderPath(String realImageFolderPath) {
        this.realImageFolderPath = realImageFolderPath;
        return this;
    }

    public String getThumbnailImageFolderPath() {
        return thumbnailImageFolderPath;
    }

    public ImageCompressor setThumbnailImageFolderPath(String thumbnailImageFolderPath) {
        this.thumbnailImageFolderPath = thumbnailImageFolderPath;
        return this;
    }

    public float getRealImageMaxWidth() {
        return realImageMaxWidth;
    }

    public ImageCompressor setRealImageMaxWidth(float realImageMaxWidth) {
        this.realImageMaxWidth = realImageMaxWidth;
        return this;
    }

    public float getRealImageMaxHeight() {
        return realImageMaxHeight;
    }

    public ImageCompressor setRealImageMaxHeight(float realImageMaxHeight) {
        this.realImageMaxHeight = realImageMaxHeight;
        return this;
    }

    public float getThumbnailImageMaxWidth() {
        return thumbnailImageMaxWidth;
    }

    public ImageCompressor setThumbnailImageMaxWidth(float thumbnailImageMaxWidth) {
        this.thumbnailImageMaxWidth = thumbnailImageMaxWidth;
        return this;
    }

    public float getThumbnailImageMaxHeight() {
        return thumbnailImageMaxHeight;
    }

    public ImageCompressor setThumbnailImageMaxHeight(float thumbnailImageMaxHeight) {
        this.thumbnailImageMaxHeight = thumbnailImageMaxHeight;
        return this;
    }

    public String getImageStringPath() {
        return imageStringPath;
    }

    public ImageCompressor setImageStringPath(String imageStringPath) {
        this.imageStringPath = imageStringPath;
        return this;
    }

    public Uri getImageUriPath() {
        return imageUriPath;
    }

    public ImageCompressor setImageUriPath(Uri imageUriPath) {
        this.imageUriPath = imageUriPath;
        return this;
    }
}
