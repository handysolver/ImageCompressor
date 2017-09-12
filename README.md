ImageCompressor
========

A simple library that compresses image with user defined height and width and save it to user defined external storage folder path.



Download
--------

Add it in your root build.gradle at the end of repositories
```java
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

Add the dependency
```java
dependencies {
    compile 'com.github.handysolver:ImageCompressor:0.1.0'
}
```

Usage
--------

### Start ImageCompressor
```java
 ImageCompressor.create(this)
                .setImageUriPath(imageUri)                            // set image Uri path
                .setImageStringPath(imageString)                      // or you can set image String path
                .setRealImageFolderPath("handysolver/real")           // set folder name where Compressed image will be saved
                .setThumbnailImageFolderPath("handysolver/thumbnail") // set folder name where Compressed thumbnail image will be saved
                .setRealImageMaxWidth(80.0f)                          // set width of image to be Compressed. default is 80.0f [optional]
                .setRealImageMaxHeight(80.0f)                         // set height of image to be Compressed. default is 80.0f [optional]
                .setThumbnailImageMaxHeight(612.0f)                   // set height of thumbnail image to be Compressed. default is 612.0f [optional]
                .setThumbnailImageMaxWidth(816.0f)                    // set width of thumbnail image to be Compressed. default is 816.0f [optional]
                .start(new ImageCompress() {
    
                    @Override
                    public void onFailure(String message) {
                        Log.d("error", "onFailure: "+message);   // error message
                    }
    
                    @Override
                    public void onSuccess(ImagePathContainer imagePathContainer) {
                          // on success you will receive path[External storage path] of two image saved in storage
                        Log.d("getRealImagePath", "onSuccess: " +imagePathContainer.getRealImagePath());
                        Log.d("getThumbnailImagePath", "onSuccess: " +imagePathContainer.getRealImagePath());
                    }
                });                      //  Start ImagePicker    
```

