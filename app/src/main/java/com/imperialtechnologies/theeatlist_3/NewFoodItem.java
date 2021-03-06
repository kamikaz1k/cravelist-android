package com.imperialtechnologies.theeatlist_3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class NewFoodItem extends ActionBarActivity {

    public final String TAG = "NewFoodItem";

    public static String DEFAULT_SPINNER_ENTRY = "What type of food is it?";
    static final int REQUEST_IMAGE_GET = 1;
    public static String NO_PICTURE_TAG = "";
    public static String THUMBNAIL_DIR = "thumbnails";
    public static Integer LOAD_URI = 1;
    public static Integer LOAD_PATH = 2;

    //Image storage options
    public static final int IMAGE_WIDTH = 1920;
    public static final int IMAGE_HEIGHT = 1080;

    // Widget globals
    EditText foodItemNameEditText;
    EditText foodItemLocationEditText;
    RatingBar foodItemRatingBar;
    EditText foodItemReviewEditText;
    ImageView foodItemImageView;
    CheckBox foodItemEatenCheckBox;
    Spinner foodTypeSpinner;

    // Spinner+FoodType relevant globals
    ArrayList<String> foodTypeItemList = new ArrayList<String>();
    String [] foodTypeItemArray;

    DBTools dbTools = new DBTools(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        initializeToolbar();

        initializeWidgetReferences();

        setKeyListeners();

    }

    private void initializeToolbar(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void initializeWidgetReferences(){

        //Initialize the references Views in the layout
        foodTypeSpinner = (Spinner) findViewById(R.id.foodTypeSpinner);
        foodItemNameEditText = (EditText) findViewById(R.id.foodItemNameEditText);
        foodItemLocationEditText = (EditText) findViewById(R.id.foodItemLocationEditText);
        foodItemRatingBar = (RatingBar) findViewById(R.id.foodItemRatingBar);
        foodItemReviewEditText = (EditText) findViewById(R.id.foodItemReviewEditText);
        foodItemImageView = (ImageView) findViewById(R.id.foodItemImageView);
        foodItemEatenCheckBox = (CheckBox) findViewById(R.id.foodItemEatenCheckBox);

        Log.i(TAG, "View references intialized.");

        initializeSpinner();

    }

    private void initializeSpinner(){

        //Clear the list and then add all the items
        //Update to use a loop when ready
        foodTypeItemList.clear();

        foodTypeItemList.add("Asian");
        foodTypeItemList.add("Thai");
        foodTypeItemList.add("Japanese");
        foodTypeItemList.add("Chinese");
        foodTypeItemList.add("American");
        foodTypeItemList.add("Indian");
        foodTypeItemList.add("Dessert");
        foodTypeItemList.add("Beverages");
        foodTypeItemList.add("Pizza");
        foodTypeItemList.add("French");
        foodTypeItemList.add("Italian");
        foodTypeItemList.add("Ramen");
        foodTypeItemList.add("Noodles");
        foodTypeItemList.add("Sushi");

        //This is so the default value is always at the top.
        //Since the stock widget doesn't allow a default/unselected option
        foodTypeItemList.remove(DEFAULT_SPINNER_ENTRY);

        Collections.sort(foodTypeItemList);
        foodTypeItemList.add(0,DEFAULT_SPINNER_ENTRY);

        //Create Array to use in adapter
        foodTypeItemArray = new String[foodTypeItemList.size()];
        foodTypeItemArray = foodTypeItemList.toArray(foodTypeItemArray);

        ArrayAdapter<String> foodTypeSpinnerAdapter = new ArrayAdapter<String>( NewFoodItem.this,
                R.layout.food_type_spinner_item, R.id.foodTypeSpinnerItemTextView, foodTypeItemArray);
        foodTypeSpinner.setAdapter(foodTypeSpinnerAdapter);

        Log.i(TAG, "Spinner adapter initialized and Spinner populated");

    }

    private void setKeyListeners(){
        TextView.OnEditorActionListener closeSoftKeyboard = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Toast.makeText(getApplicationContext(),"Input Hidden!",Toast.LENGTH_LONG).show();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(foodItemLocationEditText.getWindowToken(),0);
                    return true;
                } else {
                    return false;
                }
            }
        };

        //Set listeners to the EditText views
        foodItemNameEditText.setOnEditorActionListener(closeSoftKeyboard);
        foodItemLocationEditText.setOnEditorActionListener(closeSoftKeyboard);
        foodItemReviewEditText.setOnEditorActionListener(closeSoftKeyboard);

        foodItemRatingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    foodRatingPopup(v);
                }
                return true;
            }
        });
    }

    public void foodRatingPopup(View view){

        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.food_rating_popup, null);

        //Setup the ratingBar parameters
        //ImageView image = (ImageView) layout.findViewById(R.id.foodPopupImageView);
        final RatingBar bigRatingBar = (RatingBar) layout.findViewById(R.id.popupFoodRatingBar);
        bigRatingBar.setStepSize(1.0f);
        float rating = foodItemRatingBar.getRating();
        Log.d(TAG,"Small Rating Bar value: " + Float.toString(rating));

        final TextView popupTextView = (TextView) layout.findViewById(R.id.ratingTextView);

        RatingBar.OnRatingBarChangeListener ratingListener = new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                switch (Math.round(rating)){
                    case 1:
                        popupTextView.setText(getString(R.string.one_star));
                        break;
                    case 2:
                        popupTextView.setText(getString(R.string.two_star));
                        break;
                    case 3:
                        popupTextView.setText(getString(R.string.three_star));
                        break;
                    case 4:
                        popupTextView.setText(getString(R.string.four_star));
                        break;
                    case 5:
                        popupTextView.setText(getString(R.string.five_star));
                        break;
                    default:
                        Log.wtf(TAG,"Got a rating value of: " + Float.toString(rating));
                        break;
                }
            }
        };

        bigRatingBar.setOnRatingBarChangeListener(ratingListener);
        bigRatingBar.setRating(rating);

        imageDialog.setView(layout);
        imageDialog.setTitle(foodItemNameEditText.getText().toString());
        imageDialog.setPositiveButton("Rate!", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Big Rating Bar value: " + Float.toString(bigRatingBar.getRating()));
                foodItemRatingBar.setRating(bigRatingBar.getRating());
                dialog.dismiss();
            }

        });
        imageDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        imageDialog.create();
        imageDialog.show();

    }

    public void saveFoodItem(View view) {

        //Check for empty values
        if (foodItemNameEditText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(),"What is it called though!?",Toast.LENGTH_SHORT).show();
            return;
        }

        if (foodItemLocationEditText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(),"Where is it though!?",Toast.LENGTH_SHORT).show();
            return;
        }

        // Will hold the HashMap of values
        HashMap<String, String> queryValuesMap =  new HashMap<String, String>();

        // Get the values from the EditText boxes
        queryValuesMap.put("foodItemName", foodItemNameEditText.getText().toString());
        queryValuesMap.put("foodItemLocation", foodItemLocationEditText.getText().toString());
        queryValuesMap.put("foodItemRating", String.valueOf(foodItemRatingBar.getRating()));
        queryValuesMap.put("foodItemReview", foodItemReviewEditText.getText().toString());
        queryValuesMap.put("foodItemPicture", foodItemImageView.getTag().toString());
        queryValuesMap.put("foodItemEaten", eatenString(foodItemEatenCheckBox.isChecked()));
        queryValuesMap.put("foodType", foodTypeSelectionCheck(foodTypeSpinner.getSelectedItem().toString()));

        // Call for the HashMap to be added to the database
        dbTools.insertFoodItem(queryValuesMap);

        // Call for MainActivity to execute
        this.callMainActivity(view);
    }

    /**
     * loads bitmap using an AsyncTask. ImageView tag must have URI in string form
     */
    public void loadBitmap(int resId, ImageView imageView) {
        //Trying with Activity context, instead of application, so that dialogue can use it too
        //ref: http://stackoverflow.com/a/7034656/4765841
        BitmapLoaderBackgroundTask task = new BitmapLoaderBackgroundTask(this, imageView);
        //BitmapLoaderBackgroundTask task = new BitmapLoaderBackgroundTask(getApplicationContext(),imageView);
        task.execute(resId);

    }

    /** Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /** Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private String eatenString(boolean eaten){

        if (eaten) { return "eaten"; } else return "";

    }

    public String foodTypeSelectionCheck(String foodType){

        if (foodType.equals(NewFoodItem.DEFAULT_SPINNER_ENTRY)) { return ""; }
        else { return foodType; }

    }

    //All Image Related Functions
    public void selectImage(View view) {
        Log.i(TAG, "selectImage triggered by ImageView tap");

        if (!foodItemImageView.getTag().toString().startsWith("/")){
            Log.d(TAG,"Empty ImageView.getTag: " + foodItemImageView.getTag().toString());
            selectImage();
        } else {
            Log.i(TAG, "Launching foodPicturePopup");
            Log.d(TAG, "ImageView.getTag: " + foodItemImageView.getTag().toString());
            foodPicturePopup();
            //TODO - after picture is selected, but not saved
        }
    }

    public void selectImage() {
        Log.d(TAG, "starting selectImage");

        //TODO - Image is being cropped upon selection
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        if (intent.resolveActivity(getPackageManager()) != null) {
            Log.d(TAG, "starting ActivityForResult REQUEST_IMAGE_GET");
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    private void foodPicturePopup(){

        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.food_picture_popup, null);
        ImageView image = (ImageView) layout.findViewById(R.id.foodPopupImageView);
        String foodImage = foodItemImageView.getTag().toString();

        // image.setImageDrawable(Drawable.createFromPath(foodImage));
        //trying the loader:
        image.setTag(foodImage);
        loadBitmap(LOAD_PATH, image);

        imageDialog.setView(layout);
        imageDialog.setTitle(foodItemNameEditText.getText().toString());
        imageDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });
        imageDialog.setNegativeButton("Choose New", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                selectImage();
            }
        });

        imageDialog.setNeutralButton("Camera", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Still working on it!",
                        Toast.LENGTH_SHORT).show();
            }

        });

        imageDialog.create();
        imageDialog.show();

    }

    public void onNewImageSelected(final Uri imageUri, final ImageView imageView){
        // Uses AsyncTask to save selected image into thumbnail folder, and then loads into the thumbail imageview

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                //Pass the imageView and the savePath
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                InputStream inputStreamSample = null, inputStream = null;
                try {
                    //Inputstream cannot be used twice so make one for each call.
                    inputStreamSample = getContentResolver().openInputStream(imageUri);
                    Log.d(TAG, "inputStreamSample path: " + inputStreamSample.toString());
                    inputStream = getContentResolver().openInputStream(imageUri);
                    Log.d(TAG, "inputStream path: " + inputStream.toString());
                } catch (FileNotFoundException e) {
                    //TODO - Throw an exception in this case
                    e.printStackTrace();
                }

                //get image details;
                BitmapFactory.decodeStream(inputStreamSample, null, options);
                Log.d(TAG,"decodePreview height: " + Integer.toString(options.outWidth)
                        + " width: " + Integer.toString(options.outHeight));

                //get compression ratio
                options.inSampleSize = calculateInSampleSize(options,IMAGE_WIDTH,IMAGE_HEIGHT);
                Log.d(TAG,"inSampleSize: " + Integer.toString(options.inSampleSize));

                options.inJustDecodeBounds = false;

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream,null,options);
                Log.d(TAG,"bitmap made " + Integer.toString(options.outHeight));
                //Bitmap bitmap = BitmapFactory.decodeFile(imageUri.toString(),options);

                if (bitmap == null) { Log.wtf(TAG,"bitmap decoded in AsyncTask was null."); }

                //saveAppToThumnailFolder
                String thumbnailPath = saveToAppThumbnailFolder(bitmap);
                Log.d(TAG, "Thumbnail file saved to thumbnail folder");

                if (imageView != null) {
                    imageView.setTag(thumbnailPath);
                    Log.d(TAG, "Image URI set successfully");

                    //assign bitmap to ImageView
                    loadBitmap(LOAD_PATH, imageView);

                }

                //TODO - rememeber to set the share intent
                //and save values to db
                return null;
            }

        }.execute();

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private String saveToAppThumbnailFolder(Bitmap bitmapImage){
        Log.i(TAG, "started saveToAppThumbnailFolder");
        Log.d(TAG, "Starting bitmap parsing...");

        Log.d(TAG, "Image height: " + Integer.toString(bitmapImage.getHeight()));
        Log.d(TAG, "Image width: " + Integer.toString(bitmapImage.getWidth()));


        if ((bitmapImage.getHeight() > IMAGE_WIDTH) || (bitmapImage.getWidth() > IMAGE_HEIGHT)){

            float scaleValue;
            int originalHeight = bitmapImage.getHeight();
            int originalWidth = bitmapImage.getWidth();

            //TODO-Rethink scaling logic
            if (originalHeight > originalWidth) {
                scaleValue = (float) originalHeight/IMAGE_WIDTH;
                Log.d(TAG, "Tall image condition, scale : " + Float.toString(scaleValue));
            } else {
                scaleValue = (float) originalWidth/IMAGE_HEIGHT;
                Log.d(TAG, "Wide image condition, scale : " + Float.toString(scaleValue));
            }

            float scaledWidth = originalWidth/scaleValue, scaledHeight = originalHeight/scaleValue;

            Log.d(TAG, "OriginalHeight: " + Integer.toString(originalHeight)
                    + "OriginalWidth: " + Integer.toString(originalWidth));

            Log.d(TAG, "Scale ratio: " + Float.toString(scaleValue)
                    + "ScaledHeight: " + Float.toString(scaledHeight)
                    + "ScaledWidth: " + Float.toString(scaledWidth));

            //Create the Matrix used for resizing the bitmap
            Matrix matrix = new Matrix();
            matrix.postScale(scaleValue, scaleValue);

            Bitmap resizedBitmap = Bitmap.createBitmap(bitmapImage, 0, 0,
                    originalWidth, originalHeight,
                    matrix, false);

            Log.d(TAG, "resizedBitmap created - height: " + Integer.toString(resizedBitmap.getHeight())
                    + "width: " + Integer.toString(resizedBitmap.getWidth()));

            bitmapImage.recycle();
            bitmapImage = resizedBitmap;
            Log.i(TAG, "Bitmap reassigned");
            Log.i(TAG, "Bitmap resize completed");

        }

        Calendar calendar = Calendar.getInstance();

        // TIMESTAMPs are not correct....why?
        String year = String.format("%04d", calendar.get(Calendar.YEAR));
        Log.i("TIMESTAMP", "calendar.YEAR: " + Integer.toString(Calendar.YEAR));

        String month = String.format("%02d", calendar.get(Calendar.MONTH));
        String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
        String minute = String.format("%02d", calendar.get(Calendar.MINUTE));
        String second = String.format("%02d", calendar.get(Calendar.SECOND));
        String timeStamp = year+month+day+hour+minute+second;

        Log.d(TAG, "Timestamp string: " + timeStamp);

        if (!isExternalStorageWritable()) {
            return "Storage was busy...";
        }

        // path to /sdcard/Android/data/
        File directory = getAlbumStorageDir(getApplicationContext(),THUMBNAIL_DIR);
        if (directory==null){
            return "Directory wasn't created...";
        }

        File myPath = new File(directory, "IMG_" + timeStamp + ".png");
        Log.d(TAG, "Directory and file name created");
        Log.d(TAG, "filepath: " + myPath.getAbsolutePath().toString());

        FileOutputStream fos = null;

        try {

            fos = new FileOutputStream(myPath);
            Log.d(TAG, "FileOutputStream set");
            //Compressed bitmap to an image file
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Log.i(TAG, "PNG created from Bitmap");

            fos.close();
            bitmapImage.recycle();

        } catch (Exception e) {
            Log.e(TAG, "FileOutputStream/Compression failed");
            e.printStackTrace();
        }

        return myPath.getAbsolutePath();

    }

    public File getAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(TAG, "Thumbnail directory not created");
        }
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //retrieve selected Image URI, and turn it into something useable
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            Log.i(TAG, "Image selected from Gallery.");
            Log.d(TAG, "Starting URI data parsing");

            // take original image, turn it into bitmap, and convert to a compressed png thumbnail
            Uri fullPhotoUri = data.getData();
            Log.d(TAG, "Gallery Selected photo URI: " + fullPhotoUri.toString());

            try {

                onNewImageSelected(fullPhotoUri,foodItemImageView);
                //catch Exception from onNewImageSelected
                //using AsyncTask instead of this garbage now

            } catch (Exception e) {
                Log.w(TAG, "There should be no error...");
                e.printStackTrace();

            }

        }
    }

    public void callMainActivity(View view) {

        Intent incomingIntent = getIntent();
        int activeTab = incomingIntent.getIntExtra(MainActivity.ACTIVE_TAB, 0);

        Log.i(TAG, "Returning Intent from NewFoodItem");

        Intent returnIntent = new Intent();
        returnIntent.putExtra(MainActivity.ACTIVE_TAB,activeTab);
        setResult(RESULT_OK,returnIntent);
        finish();

        overridePendingTransition(R.animator.fade_rise,R.animator.push_right_out);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_food, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.animator.fade_rise, R.animator.push_right_out);
                return true;
            case R.id.action_save:
                saveFoodItem(findViewById(R.id.action_save));
                return true;
            case R.id.action_help:
                Toast.makeText(getApplicationContext(), "No.", Toast.LENGTH_SHORT).show();
                //showAddFoodHelpDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.fade_rise, R.animator.push_right_out);
    }

    @Override
    protected void onDestroy() {
        dbTools.close();
        super.onDestroy();
    }
}
