package com.android.barscanner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanerActivity extends BaseScannerActivity implements MessageDialogFragment.MessageDialogListener,
        ZXingScannerView.ResultHandler, FormatSelectorDialogFragment.FormatSelectorDialogListener,
        CameraSelectorDialogFragment.CameraSelectorDialogListener {
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private final String BARCODES = "BARCODES";
    private final String LAST_CATEGORY = "LAST_CATEGORY";
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;
    private String lastCat;
    String barcode;
    private SharedPreferences spBc;
    private SharedPreferences spCs;
    private SharedPreferences spLCs;
    private SharedPreferences.Editor editorBc;
    private SharedPreferences.Editor editorCs;
    private SharedPreferences.Editor editorLCs;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if(state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
        }

        setContentView(R.layout.activity_simple_scanner);
        setupToolbar();

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        setupFormats();
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem;

        if(mFlash) {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);


        if(mAutoFocus) {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

//                menuItem = menu.add(Menu.NONE, R.id.menu_formats, 0, R.string.formats);
//                MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        //        menuItem = menu.add(Menu.NONE, R.id.menu_camera_selector, 0, R.string.select_camera);
        //        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_flash:
                mFlash = !mFlash;
                if(mFlash) {
                    item.setTitle(R.string.flash_on);
                } else {
                    item.setTitle(R.string.flash_off);
                }
                mScannerView.setFlash(mFlash);
                return true;
            case R.id.menu_auto_focus:
                mAutoFocus = !mAutoFocus;
                if(mAutoFocus) {
                    item.setTitle(R.string.auto_focus_on);
                } else {
                    item.setTitle(R.string.auto_focus_off);
                }
                mScannerView.setAutoFocus(mAutoFocus);
                return true;
            //            case R.id.menu_formats:
            //                DialogFragment fragment = FormatSelectorDialogFragment.newInstance(this, mSelectedIndices);
            //                fragment.show(getSupportFragmentManager(), "format_selector");
            //                return true;
            //            case R.id.menu_camera_selector:
            //                mScannerView.stopCamera();
            //                DialogFragment cFragment = CameraSelectorDialogFragment.newInstance(this, mCameraId);
            //                cFragment.show(getSupportFragmentManager(), "camera_selector");
            //                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        barcode = rawResult.getText();
        //        String format = rawResult.getBarcodeFormat().toString();
        if(MainActivity.barcodeArray.contains(barcode)){
            dialogBCExist();
        }else{
            dialogBCCreate();}
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // Resume the camera
        mScannerView.resumeCameraPreview(this);

    }

    @Override
    public void onFormatsSaved(ArrayList<Integer> selectedIndices) {
        mSelectedIndices = selectedIndices;
        setupFormats();
    }

    @Override
    public void onCameraSelected(int cameraId) {
        mCameraId = cameraId;
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }
    //****Устанавливаем необходимый формат кода
    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        if(mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<Integer>();
//            for(int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
//                mSelectedIndices.add(i);
//            }
            mSelectedIndices.add(2);
            mSelectedIndices.add(3);
        }

        for(int index : mSelectedIndices) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if(mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    private void dialogBCExist(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Такой код уже занят");
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }
    private void dialogBCCreate(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(barcode);

        final Spinner spinner = new Spinner(this);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, MainActivity.categoryArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        builder.setView(spinner);

        spBc = getSharedPreferences(BARCODES, Context.MODE_PRIVATE);
        spLCs = getSharedPreferences(LAST_CATEGORY, Context.MODE_PRIVATE);
//        spCs = getSharedPreferences(CATEGORYS, Context.MODE_PRIVATE);

        if(spLCs.contains("LAST_CATEGORY")) {
            lastCat = (spLCs.getString("LAST_CATEGORY", ""));
            for(int i = 0; i < spinner.getCount(); i++){
                if(spinner.getItemAtPosition(i).toString().equals(lastCat)){
                    spinner.setSelection(i);
                    break;
                }
            }
        }

        // Set up the buttons
        builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editorBc = spBc.edit();
                editorLCs = spLCs.edit();
                editorBc.putString(barcode, spinner.getSelectedItem().toString());
                editorLCs.putString("LAST_CATEGORY", spinner.getSelectedItem().toString());
                editorBc.commit();
                editorLCs.commit();

                Intent intent = new Intent(getBaseContext(), BarcodActivity.class);
                intent.putExtra("EXTRA_CODE", barcode);
                intent.putExtra("EXTRA_CAT", spinner.getSelectedItem().toString());
                startActivity(intent);

                finish();
            }
        });

        builder.show();
    }
}
