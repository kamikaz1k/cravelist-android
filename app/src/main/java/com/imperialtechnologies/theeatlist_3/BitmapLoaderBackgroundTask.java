package com.imperialtechnologies.theeatlist_3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by kdandang on 4/10/2015.
 */
public class BitmapLoaderBackgroundTask extends AsyncTask<Integer, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private Uri fullPhotoUri;
    private Context applicationContext;
    public final int imageViewWidth = 900;
    public final int imageViewHeight = 900;

    public final String TAG = "BitmapLoader";

    public BitmapLoaderBackgroundTask(Context context, ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        fullPhotoUri = Uri.parse(imageView.getTag().toString());
        applicationContext = context;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        //data is the parameters sent in execute(parameters)

        //TODO change picture to something that implies "loading image"

        /*BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize=4;*/

        Bitmap selectedPicture = null;

        Log.d("BitmapLoaderTask", "Async file decode starting...");
        Log.d(TAG, "Uri path: " + fullPhotoUri.toString());
        selectedPicture = BitmapFactory.decodeFile(fullPhotoUri.toString());//, options);

        // should be using this one:
        /*Bitmap selectedPicture = null;
        try {
            selectedPicture = MediaStore.Images.Media.getBitmap(
                    applicationContext.getContentResolver(), (fullPhotoUri));
        } catch (IOException e) {
            e.printStackTrace();
            Log.wtf(TAG,"FILE NOT FOUND!?");
        }*/

        if (selectedPicture == null) new Throwable("Selected Picture resource was not resolved");


        Log.d("BitmapLoaderTask", "Async file decode complete");
        return selectedPicture;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                resizeBitmapForImageView(imageView, bitmap);
            }
        }
        Log.i("BitmapLoader", "BitmapLoaderBackgroundTask completed");
    }

    private void resizeBitmapForImageView(ImageView imageView, Bitmap thumbnail) {

        //TODO - exception handling for width/height = 0 or a null
        //TODO - exception handling for width/height < imageView
        int width = thumbnail.getWidth();
        int height = thumbnail.getHeight();

        int resizeHeight;
        int resizeWidth;

        Log.d("BitmapLoader", "ImageView Width: " + imageView.getWidth()
                + " Height: " + imageView.getHeight());

        if (imageView.getHeight() == 0) {
            resizeHeight = imageViewHeight;
            resizeWidth = imageViewWidth;
        } else {
            resizeHeight = imageView.getHeight();
            resizeWidth = imageView.getWidth();
        }

        Log.d("BitmapLoader - tnail", "Width: " + width + " Height: " + height);
        Log.d("BitmapLoader - imgView", "Width: " + imageView.getWidth() + " Height: " + imageView.getHeight());

        float resizeRatio = (float) resizeWidth / resizeHeight;
        Log.d(TAG, "resizeRatio: " + Float.toString(resizeRatio));
        int startX = 0, startY = 0;

        //Assuming NOTHING!

        /* find out ImageView width/height
        find it out if it tall or wide

        if its tall then:
            crop height to => width divided by width/height ratio
            center the cropping vertically

        if its wide then:
            crop the width to height * width/height ratio
            center the cropping horizontally */

        if (height > width) {
            /*if its tall then:
            crop height to => width divided by width/height ratio
            center the cropping vertically */
            height = (int) (((float) width) / resizeRatio);

            //crop(bitmap, height, height.value);
            startY = (thumbnail.getHeight() - height) / 2;

            Log.d(TAG, "Tall image condition > newHeight: " + Integer.toString(height) + " startY: " + Integer.toString(startY));

        } else {
            /*if its wide then:
            crop the width to height * width/height ratio */
            width = (int) (height * resizeRatio);

            // center the cropping horizontally
            startX = (thumbnail.getWidth() - width) / 2;
            Log.d(TAG, "Wide image condition > newWidth: " + Integer.toString(width) + " startX: " + Integer.toString(startX));

        }

        float scaleWidth = ((float) resizeWidth) / width;
        float scaleHeight = ((float) resizeHeight) / height;
        Log.d(TAG, "scaleWidth: " + Float.toString(scaleWidth)
                + "scaleHeight: " + Float.toString(scaleHeight));

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();

        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(thumbnail, startX, startY, width, height, matrix, false);
        Log.d("BitmapLoader", "source Bitmap height: " + Integer.toString(height)
                + " width: " + Integer.toString(width));

        Log.d("BitmapLoader", "resized Bitmap height: " + Integer.toString(resizedBitmap.getHeight())
                + " width: " + Integer.toString(resizedBitmap.getWidth()));

        imageView.setImageBitmap(resizedBitmap);
        Log.i("BitmapLoader", "Thumbnail Bitmap scaled and set for imageView");

        thumbnail.recycle();
        //bitmap memory management - https://developer.android.com/training/displaying-bitmaps/manage-memory.html

        //TODO - not able to resize correctly

    }
}