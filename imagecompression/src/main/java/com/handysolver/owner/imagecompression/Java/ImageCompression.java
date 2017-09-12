package com.handysolver.owner.imagecompression.Java;

/**
 * Created by OWNER on 11-09-2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.handysolver.owner.imagecompression.model.ImagePathContainer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by HP-HP on 03-07-2015.
 */
public class ImageCompression extends AsyncTask<String, Integer, ImagePathContainer> {

    private static String TAG="new";
    private Context context;
    private static final float maxHeight = 80.0f;
    private static final float maxWidth = 80.0f;
    private String realImageFolderPath;
    private String thumbnailImageFolderPath;

    private float realImageMaxWidth ;
    private  float realImageMaxHeight;
    private  float thumbnailImageMaxWidth ;
    private  float thumbnailImageMaxHeight ;
    private ImageCompress imageCompress;


    public ImageCompression(Context context){
        this.context=context;
    }

    @Override
    protected ImagePathContainer doInBackground(String... strings) {
        if(strings.length == 0 || strings[0] == null)
            return null;

        ImagePathContainer imagePathContainer=new ImagePathContainer();
        Log.d(TAG, "doInBackground: "+realImageMaxWidth+"   -   "+realImageMaxHeight );
        String realImagePath=compressImage(strings[0],realImageFolderPath,realImageMaxWidth,realImageMaxHeight);
        if(realImagePath!=null){
            imagePathContainer.setRealImagePath(realImagePath);
            String thumbnailImagePath=compressImage(strings[0],thumbnailImageFolderPath,thumbnailImageMaxWidth,thumbnailImageMaxHeight);
            if(thumbnailImagePath!=null){
                imagePathContainer.setThumbnailImagePath(thumbnailImagePath);
            }

        }
        return imagePathContainer;
    }
    protected void onPostExecute(ImagePathContainer imagePathContainer){
        Log.d(TAG, "onPostExecute: image path "+imagePathContainer.getThumbnailImagePath()+" ---- "+imagePathContainer.getRealImagePath());
        imageCompress.onSuccess(imagePathContainer);
        // imagePath is path of new compressed image.
    }


    public String compressImage(String imagePath, String folderPath, float maxWidth, float maxHeight) {
        Log.d(TAG, "compressImage before : "+imagePath);
        imagePath = getRealPathFromURI(imagePath);
        Log.d(TAG, "compressImage after : "+imagePath);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(imagePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        if(bmp!=null)
        {
            bmp.recycle();
        }

        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filepath = getFilename(folderPath);
        try {
            out = new FileOutputStream(filepath);

            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filepath;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public String getFilename(String folderPath) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/"+folderPath);

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            mediaStorageDir.mkdirs();
        }

        String mImageName="IMG_"+ String.valueOf(System.currentTimeMillis()) +".jpg";
        String uriString = (mediaStorageDir.getAbsolutePath() + "/"+ mImageName);;
        return uriString;

    }
    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }
    public String getRealImageFolderPath() {
        return realImageFolderPath;
    }

    public void setRealImageFolderPath(String realImageFolderPath) {
        this.realImageFolderPath = realImageFolderPath;
    }

    public String getThumbnailImageFolderPath() {
        return thumbnailImageFolderPath;
    }

    public void setThumbnailImageFolderPath(String thumbnailImageFolderPath) {
        this.thumbnailImageFolderPath = thumbnailImageFolderPath;
    }

    public float getRealImageMaxWidth() {
        return realImageMaxWidth;
    }

    public void setRealImageMaxWidth(float realImageMaxWidth) {
        this.realImageMaxWidth = realImageMaxWidth;
    }

    public float getRealImageMaxHeight() {
        return realImageMaxHeight;
    }

    public void setRealImageMaxHeight(float realImageMaxHeight) {
        this.realImageMaxHeight = realImageMaxHeight;
    }

    public float getThumbnailImageMaxWidth() {
        return thumbnailImageMaxWidth;
    }

    public void setThumbnailImageMaxWidth(float thumbnailImageMaxWidth) {
        this.thumbnailImageMaxWidth = thumbnailImageMaxWidth;
    }

    public float getThumbnailImageMaxHeight() {
        return thumbnailImageMaxHeight;
    }

    public void setThumbnailImageMaxHeight(float thumbnailImageMaxHeight) {
        this.thumbnailImageMaxHeight = thumbnailImageMaxHeight;
    }

    public ImageCompress getImageCompress() {
        return imageCompress;
    }

    public void setImageCompress(ImageCompress imageCompress) {
        this.imageCompress = imageCompress;
    }
}
