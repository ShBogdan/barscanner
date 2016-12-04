package com.android.barscanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

import static com.android.barscanner.R.id.getPhoto;
import static com.android.barscanner.R.id.imageView;


public class GetPhoto extends AppCompatActivity {
    private ImageView mImageView;
    private Button takePictureButton;
    private Uri file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_phopo);

        takePictureButton = (Button) findViewById(getPhoto);
        mImageView = (ImageView) findViewById(imageView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView.setImageResource(0);
                mImageView.setImageResource(android.R.color.transparent);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = Uri.fromFile(getOutputMediaFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
//                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, file);
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                mImageView.setImageURI(file);
            }
        }
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/Barcode");
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }
        File file = new File(mediaStorageDir.getPath() + File.separator +"IMG1111111111_" + "_1.jpg");

        if(file.exists()){
            file.delete();
        }
        file = new File(mediaStorageDir.getPath() + File.separator +"IMG1111111111_" + "_1.jpg");
        return file;
    }
}
