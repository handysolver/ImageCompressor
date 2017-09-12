package com.handysolver.owner.imagetest;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.handysolver.owner.imagecompression.Java.ImageCompress;
import com.handysolver.owner.imagecompression.Java.ImageCompressor;
import com.handysolver.owner.imagecompression.model.ImagePathContainer;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMG = 1888;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.imageView = (ImageView)this.findViewById(R.id.imageView1);
        Button photoButton = (Button) this.findViewById(R.id.button1);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });
    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                //String s = getRealPathFromURI(imageUri);
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                //Log.d(TAG, "onActivityResult: "+s);
                ImageCompressor.create(this)
                        .setImageUriPath(imageUri)
                        .setRealImageFolderPath("handysolver/real")
                        .setThumbnailImageFolderPath("handysolver/thumbnail")
                        .setRealImageMaxWidth(80.0f)
                        .setRealImageMaxHeight(80.0f)
                        .setThumbnailImageMaxHeight(612.0f)
                        .setThumbnailImageMaxWidth(816.0f)
                        .start(new ImageCompress() {

                            @Override
                            public void onFailure(String message) {
                                Log.d("error", "onFailure: "+message);
                            }

                            @Override
                            public void onSuccess(ImagePathContainer imagePathContainer) {
                                Log.d("getRealImagePath", "onSuccess: " +imagePathContainer.getRealImagePath());
                                Log.d("getThumbnailImagePath", "onSuccess: " +imagePathContainer.getRealImagePath());
                            }
                        });
                //imageView.setImageBitmap(decodeFile(imageStream));
                /*final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);*/
                /*imageView.setImageBitmap(
                        decodeSampledBitmapFromResource(getResources(), imageStream, 10, 10));*/
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(MainActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}
