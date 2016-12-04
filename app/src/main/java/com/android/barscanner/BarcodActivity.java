package com.android.barscanner;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class BarcodActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String ROOT_URL = "http://77.87.144.159:8080/";
    private final String BARCODES = "BARCODES";
    private final Integer CAMERA_PERMISSION = 55;
    private final Integer READ_EXST = 56;
    private final Integer WRITE_EXST = 57;
    private Button mTakePictureButton_1, mTakePictureButton_2, mTakePictureButton_3, mTakePictureButton_4, mTakePictureButton_5, mRemoveButton, mSendButton;
    private ImageView mImageView_1, mImageView_2, mImageView_3,mImageView_4, mImageView_5;
    private TextView mCodeTv, mCatTv;
    private ProgressBar mProgressBar;
    private Context mContext;
    private String mCode, mCat;
    private Uri mFile;
    private Intent photoPickerIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcod);
        Log.d("MyLog", "onCreate");

        mContext = getApplication();
        showCodeInfo();
        initButtons();
        initView();
        checkSDKSBuild();



    }

    private void showCodeInfo(){
        mCode = getIntent().getStringExtra("EXTRA_CODE");
        mCat = getIntent().getStringExtra("EXTRA_CAT");
        //        mCode = "2313123";
        //        mCat = "123123123";
        mCodeTv = (TextView) findViewById(R.id.code);
        mCatTv = (TextView) findViewById(R.id.cat);
        mCodeTv.setText(mCode);
        mCatTv.setText(mCat);
    }

    private void initButtons(){
        mTakePictureButton_1 = (Button) findViewById(R.id.button1);
        mTakePictureButton_1.setOnClickListener(this);

        mTakePictureButton_2 = (Button) findViewById(R.id.button2);
        mTakePictureButton_2.setOnClickListener(this);

        mTakePictureButton_3 = (Button) findViewById(R.id.button3);
        mTakePictureButton_3.setOnClickListener(this);

        mTakePictureButton_4 = (Button) findViewById(R.id.button4);
        mTakePictureButton_4.setOnClickListener(this);

        mTakePictureButton_5 = (Button) findViewById(R.id.button5);
        mTakePictureButton_5.setOnClickListener(this);

        mRemoveButton = (Button) findViewById(R.id.remove);
        mRemoveButton.setOnClickListener(this);

        mSendButton = (Button) findViewById(R.id.send);
        mSendButton.setOnClickListener(this);



    }

    private void initView(){
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mImageView_1 = (ImageView) findViewById(R.id.imageView1);
        mImageView_1.setOnClickListener(this);
        if(getOutputMediaFilePath(mCode+"_1",true).exists()){
            mImageView_1.setImageURI(Uri.fromFile(getOutputMediaFilePath(mCode+"_1",true)));}

        mImageView_2 = (ImageView) findViewById(R.id.imageView2);
        mImageView_2.setOnClickListener(this);
        if(getOutputMediaFilePath(mCode+"_2",true).exists()){
            mImageView_2.setImageURI(Uri.fromFile(getOutputMediaFilePath(mCode+"_2",true)));}

        mImageView_3 = (ImageView) findViewById(R.id.imageView3);
        mImageView_3.setOnClickListener(this);
        if(getOutputMediaFilePath(mCode+"_3",true).exists()){
            mImageView_3.setImageURI(Uri.fromFile(getOutputMediaFilePath(mCode+"_3",true)));}

        mImageView_4 = (ImageView) findViewById(R.id.imageView4);
        mImageView_4.setOnClickListener(this);
        if(getOutputMediaFilePath(mCode+"_4",true).exists()){
            mImageView_4.setImageURI(Uri.fromFile(getOutputMediaFilePath(mCode+"_4",true)));}

        mImageView_5 = (ImageView) findViewById(R.id.imageView5);
        mImageView_5.setOnClickListener(this);
        if(getOutputMediaFilePath(mCode+"_5",true).exists()){
            mImageView_5.setImageURI(Uri.fromFile(getOutputMediaFilePath(mCode+"_5",true)));}
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button1:
                openCamera(getOutputMediaFilePath(mCode+"_1", false), mImageView_1, 101);
                break;

            case R.id.button2:
                openCamera(getOutputMediaFilePath(mCode+"_2", false), mImageView_2, 102);
                break;

            case R.id.button3:
                openCamera(getOutputMediaFilePath(mCode+"_3", false), mImageView_3, 103);
                break;

            case R.id.button4:
                openCamera(getOutputMediaFilePath(mCode+"_4", false), mImageView_4, 104);
                break;

            case R.id.button5:
                openCamera(getOutputMediaFilePath(mCode+"_5", false), mImageView_5, 105);
                break;

            case R.id.imageView1:
                photoPickerIntent = new Intent(Intent.ACTION_VIEW);
                photoPickerIntent.setDataAndType(Uri.fromFile(getOutputMediaFilePath(mCode+"_1",true)), "image/jpeg");
                startActivity(photoPickerIntent );
                break;

            case R.id.imageView2:
                photoPickerIntent = new Intent(Intent.ACTION_VIEW);
                photoPickerIntent.setDataAndType(Uri.fromFile(getOutputMediaFilePath(mCode+"_2",true)), "image/jpeg");
                startActivity(photoPickerIntent );
                break;

            case R.id.imageView3:
                photoPickerIntent = new Intent(Intent.ACTION_VIEW);
                photoPickerIntent.setDataAndType(Uri.fromFile(getOutputMediaFilePath(mCode+"_3",true)), "image/jpeg");
                startActivity(photoPickerIntent );
                break;

            case R.id.imageView4:
                photoPickerIntent = new Intent(Intent.ACTION_VIEW);
                photoPickerIntent.setDataAndType(Uri.fromFile(getOutputMediaFilePath(mCode+"_4",true)), "image/jpeg");
                startActivity(photoPickerIntent );
                break;

            case R.id.imageView5:
                photoPickerIntent = new Intent(Intent.ACTION_VIEW);
                photoPickerIntent.setDataAndType(Uri.fromFile(getOutputMediaFilePath(mCode+"_5",true)), "image/jpeg");
                startActivity(photoPickerIntent );
                break;

            case R.id.remove:
                removeItem();
                break;

            case R.id.send:
                int i=0;
                while (i++ <= 4) {
                    File f = getOutputMediaFilePath(mCode + "_"+i, true);
                    if(f.exists()){
                        uploadImage(getOutputMediaFilePath(mCode + "_"+i, true));
                    }
                }
                break;

            default:
                break;
        }
    }
    private void removeItem(){
        SharedPreferences spBc = getSharedPreferences (BARCODES, Context.MODE_PRIVATE );
        SharedPreferences.Editor editBc = spBc.edit();
        editBc.remove(mCode);
        editBc.apply();
        int i=0;
        while (i++ <= 4){
            try{
                getOutputMediaFilePath(mCode+"_"+i, true).delete();
            }catch (NullPointerException e){
                Log.e("MyLog", "file does not exist");
            }
        }
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Запись удалена", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 101:
                if(resultCode == RESULT_OK) mImageView_1.setImageURI(mFile);;
                break;

            case 102:
                if(resultCode == RESULT_OK) mImageView_2.setImageURI(mFile);;
                break;

            case 103:
                if(resultCode == RESULT_OK) mImageView_3.setImageURI(mFile);;
                break;

            case 104:
                if(resultCode == RESULT_OK) mImageView_4.setImageURI(mFile);;
                break;

            case 105:
                if(resultCode == RESULT_OK) mImageView_5.setImageURI(mFile);;
                break;
        }
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private static File getOutputMediaFilePath(String fileName, boolean isLoad){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/Barcode");
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }
        File file = new File(mediaStorageDir.getPath() + File.separator +fileName+ ".jpg");
        //if isLoad get true do nothing
        if(file.exists() && !isLoad){
            file.delete();
            file = new File(mediaStorageDir.getPath() + File.separator +fileName+ ".jpg");
        }
        return file;
    }

    private void openCamera(File path, ImageView v, Integer actResult){
        mProgressBar.setVisibility(View.VISIBLE);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mFile = Uri.fromFile(path);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mFile);
        startActivityForResult(intent, actResult);
        try{
            v.setImageResource(0);
            v.setImageResource(android.R.color.transparent);
        }catch (NullPointerException e){
            Log.e("MyLog", "photo does not exist");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 55: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MyLog", "Permission has been granted by user");

                    //                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //                    startActivityForResult(intent, 1);

                } else {
                    Log.d("MyLog", "Permission has been denied by user");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(getApplication(), permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(BarcodActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(BarcodActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(BarcodActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkSDKSBuild(){
        if (Build.VERSION.SDK_INT >= 23) {
            askForPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION);
            askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXST);
            askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXST);
        }
    }

    private void uploadImage(File file) {
        /**
         * Progressbar to Display if you need
         */
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(BarcodActivity.this);
        progressDialog.setMessage("Загрузка фото...");
        progressDialog.show();

        //Create Upload Server Client
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface service = retrofit.create(RequestInterface.class);

        //File creating from selected URL

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        Call<ResponseBody> resultCall = service.uploadImage(body);

        // finally, execute the request
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();

                // Response Success or Fail
                if (response.isSuccessful()) {
                    Log.d("MyLog", "sucsses");
                } else {
                    Log.d("MyLog", "error");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("MyLog", "onFailure");
                progressDialog.dismiss();
            }
        });
    }

}
