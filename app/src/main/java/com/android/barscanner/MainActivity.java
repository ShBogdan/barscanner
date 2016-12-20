package com.android.barscanner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private final String BARCODES = "BARCODES";
    private final String SERVER_BARCODES = "SERVER_BARCODES";
    private final String SERVER_CATEGORY = "SERVER_CATEGORY";
    private final Integer CAMERA_PERMISSION = 55;
    public static final String BASE_URL = "http://77.123.129.26/barcodeserver/";

    private SharedPreferences spBc;
    private SharedPreferences spServerBc;
    private SharedPreferences.Editor mEditorServerBc;
    private SharedPreferences spServerCat;
    private SharedPreferences.Editor mEditorServerCat;
    private ListView mBarCodeList;
    private Menu optionsMenu;

    public static ArrayList<String> categoryArray;
    public static ArrayList<String> barcodeArray;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(categoryArray.size()==0){
                    Toast.makeText(getApplication(), "Вы не синхронизированы с базой", Toast.LENGTH_LONG).show();
                }else{
                    Intent myIntent = new Intent(MainActivity.this, ScanerActivity.class);
                    startActivity(myIntent);}

            }
        });

        getSpBarcode();
        getSpCategory();
        checkSDKSBuild();


        //        Intent intent = new Intent(getBaseContext(), BarcodActivity.class);
        //        startActivity(intent);
    }


    private List<Barcode> getAllValues() {
        spBc = getSharedPreferences ( BARCODES, Context.MODE_PRIVATE );
        Map<String, ?> values = spBc.getAll();
        List<Barcode> barcodesList = new ArrayList<>();
        for (Map.Entry<String, ?> entry : values.entrySet()) {
            Barcode barcodes = new Barcode(entry.getKey(), entry.getValue().toString());
            //            Log.d("MyLog", "getAllValues:");
            //            Log.d("MyLog", entry.getKey() + " " + entry.getValue().toString());
            barcodesList.add(barcodes);
        }
        barcodesList.add(new Barcode("11","gger"));
        barcodesList.add(new Barcode("12","gerg"));
        barcodesList.add(new Barcode("13","gerg"));
        barcodesList.add(new Barcode("14","gdfgg"));
        barcodesList.add(new Barcode("15","gdfgg"));
        barcodesList.add(new Barcode("16","dfggg"));
        barcodesList.add(new Barcode("17","bvgg"));
        barcodesList.add(new Barcode("18","gvcg"));
        barcodesList.add(new Barcode("19","gvcbg"));
        barcodesList.add(new Barcode("10","gcvbg"));
        barcodesList.add(new Barcode("112","gcbg"));
        barcodesList.add(new Barcode("114","gcbbg"));
        barcodesList.add(new Barcode("116","gerg"));

        return barcodesList;
    }

    private class Barcode {
        public final String code;
        public final String cat;

        public Barcode(String code, String cat) {
            this.code = code;
            this.cat = cat;
        }
    }

    private class BarCodeAdapter extends ArrayAdapter<Barcode> {

        public BarCodeAdapter(Context context, List<Barcode> list) {
            super(context, android.R.layout.simple_list_item_1, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Barcode barcode = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item, null);
            }
            ((TextView) convertView.findViewById(R.id.text1))
                    .setText(barcode.code);
            ((TextView) convertView.findViewById(R.id.text2))
                    .setText(barcode.cat);
            if(barcodeArray.contains(barcode.code)){
                convertView.findViewById(R.id.linearLayout).setBackgroundResource(R.color.exist);
            }
            return convertView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        initListView();
        initRecycleView();


    }

    private void initListView(){
        Log.d("MyLog", "Формируем ListView");

        ArrayAdapter<Barcode> adapter = new BarCodeAdapter(this, getAllValues());
        mBarCodeList = (ListView) findViewById(R.id.list_barcodes);
        mBarCodeList.setAdapter(adapter);
        mBarCodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    TextView t1 = (TextView) view.findViewById(R.id.text1);
                                                    TextView t2 = (TextView) view.findViewById(R.id.text2);
                                                    String code = t1.getText().toString();
                                                    String cat = t2.getText().toString();
                                                    spServerCat = getSharedPreferences(SERVER_CATEGORY, Context.MODE_PRIVATE);
                                                    String catId = spServerCat.getString(cat, "");

                                                    Intent intent = new Intent(getBaseContext(), BarcodActivity.class);
                                                    intent.putExtra("EXTRA_CODE", code);
                                                    intent.putExtra("EXTRA_CAT", cat);
                                                    intent.putExtra("EXTRA_CAT_ID", catId);
                                                    startActivity(intent);
                                                }
                                            }
        );
    }

    private void initRecycleView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(getAllValues());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getSpBarcode(){
        Log.d("MyLog", "Загрузка баркодов с sp");
        barcodeArray = new ArrayList<>();
        spServerBc = getSharedPreferences(SERVER_BARCODES, Context.MODE_PRIVATE);
        Map<String, ?> values = spServerBc.getAll();
        for (Map.Entry<String, ?> entry : values.entrySet()) {
            barcodeArray.add(entry.getKey());
        }
    }

    private void getSpCategory(){
        Log.d("MyLog", "Загрузка затегорий с sp");
        categoryArray = new ArrayList<>();
        spServerCat = getSharedPreferences(SERVER_CATEGORY, Context.MODE_PRIVATE);
        Map<String, ?> values = spServerCat.getAll();
        for (Map.Entry<String, ?> entry : values.entrySet()) {
            categoryArray.add(entry.getKey());
        }
        Collections.sort(categoryArray);
    }

    void getRetrofitBarcodes() {
        Log.d("MyLog", "Грузим баркоды с сервера и сохраняем в sp");

        spServerBc = getSharedPreferences(SERVER_BARCODES, Context.MODE_PRIVATE);
        mEditorServerBc = spServerBc.edit();
        mEditorServerBc.clear();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface service = retrofit.create(RequestInterface.class);

        Call<Result> call = service.getBarcodeList();

        call.enqueue(new Callback<Result>() {

            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                barcodeArray = new ArrayList<>();

                Result r = response.body();
                List<List<String>> s = r.getBarcodes();
                for (int i = 0; i < s.size(); i++) {
                    barcodeArray.add(s.get(i).get(0));
                    mEditorServerBc.putString(s.get(i).get(0), "");
                }
                mEditorServerBc.commit();
//                initListView();
                initRecycleView();
                Toast.makeText(getApplication(), "Коды обновлены", Toast.LENGTH_SHORT).show();
                setRefreshActionButtonState(false);

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("MyLog", "error не удалось подключится к серверу" + t.toString());
                Toast.makeText(getApplication(), "Не удалось подключится к серверу", Toast.LENGTH_SHORT).show();
                setRefreshActionButtonState(false);

            }
        });

    }

    void getRetrofitCategoty() {
        Log.d("MyLog", "Грузим категории с сервера и сохраняем в sp");

        spServerCat = getSharedPreferences(SERVER_CATEGORY, Context.MODE_PRIVATE);
        mEditorServerCat = spServerCat.edit();
        mEditorServerCat.clear();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface service = retrofit.create(RequestInterface.class);

        Call<Result> call = service.getCatList();

        call.enqueue(new Callback<Result>() {

            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                categoryArray = new ArrayList<>();


                Result r = response.body();
                List<List<String>> s = r.getCategorys();
                for (int i = 0; i < s.size(); i++) {
                    categoryArray.add(s.get(i).get(1));
                    mEditorServerCat.putString(s.get(i).get(1), s.get(i).get(0));
                }
                mEditorServerCat.commit();
                Toast.makeText(getApplication(), "Кактегории обновлены", Toast.LENGTH_SHORT).show();
                setRefreshActionButtonState(false);
                Collections.sort(categoryArray);
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("MyLog", "error не удалось подключится к серверу" + t.toString());
                Toast.makeText(getApplication(), "Не удалось подключится к серверу", Toast.LENGTH_SHORT).show();
                setRefreshActionButtonState(false);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.airport_menuRefresh:

                Log.d("MyLog","pressed");
                setRefreshActionButtonState(true);

                getRetrofitBarcodes();
                getRetrofitCategoty();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu
                    .findItem(R.id.airport_menuRefresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 55: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MyLog", "Permission has been granted by user");
//
//                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                        startActivityForResult(intent, 1);

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
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
//            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkSDKSBuild(){
        if (Build.VERSION.SDK_INT >= 23) {
            askForPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION);
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements View.OnClickListener {
        private ArrayList<Barcode> myDataset;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextViewCode;
            public TextView mTextViewCat;
            public CardView mColorLL;
            public ViewHolder(View v) {
                super(v);
                mTextViewCode = ((TextView) v.findViewById(R.id.text1));
                mTextViewCat =((TextView) v.findViewById(R.id.text2));
                mColorLL = (CardView) v.findViewById(R.id.card_view);
            }
        }

        public MyAdapter(List<Barcode> mDataset) {
            myDataset = (ArrayList<Barcode>) mDataset;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Barcode barcode = myDataset.get(position);
            holder.mTextViewCode.setText(barcode.code);
            holder.mTextViewCat.setText(barcode.cat);;
            if(barcodeArray.contains(barcode.code)){
                holder.mColorLL.setCardBackgroundColor(R.color.exist);
            }

            holder.mColorLL.setOnClickListener(this);
        }
        @Override
        public int getItemCount() {
            return myDataset.size();
        }

        @Override
        public void onClick(View view) {
            TextView t1 = (TextView) view.findViewById(R.id.text1);
            TextView t2 = (TextView) view.findViewById(R.id.text2);
            String code = t1.getText().toString();
            String cat = t2.getText().toString();
            spServerCat = getSharedPreferences(SERVER_CATEGORY, Context.MODE_PRIVATE);
            String catId = spServerCat.getString(cat, "");

            Intent intent = new Intent(getBaseContext(), BarcodActivity.class);
            intent.putExtra("EXTRA_CODE", code);
            intent.putExtra("EXTRA_CAT", cat);
            intent.putExtra("EXTRA_CAT_ID", catId);
            startActivity(intent);
        }
    }
}
