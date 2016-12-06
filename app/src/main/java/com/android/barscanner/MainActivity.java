package com.android.barscanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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
                    MainActivity.this.startActivity(myIntent);}
            }
        });

        getSpBarcode();
        getSpCategory();

        mBarCodeList = (ListView) findViewById(R.id.list_barcodes);


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
            //            if(barcodeArray.contains(barcode.code)){
            //                convertView.findViewById(R.id.linearLayout).setBackgroundResource(R.color.exist);
            //            }
            return convertView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initListView();


    }

    private void initListView(){
        Log.d("MyLog", "Формируем ListView");

        ArrayAdapter<Barcode> adapter = new BarCodeAdapter(this, getAllValues());
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
                initListView();
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
}
