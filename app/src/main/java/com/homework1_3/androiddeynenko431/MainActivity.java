package com.homework1_3.androiddeynenko431;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final static String LOG_TAG = "sample";
    private final static String TITLE = "title";
    private final static String SUBTITLE = "subtitle";
    private final static String TEXT = "text";
    private final static String PREF = "pref";
    List<Map<String, String>> simpleAdapterContent;
    private SharedPreferences sharedPref;
    private ListView list;
    private String result;
    private String[] content;
    File fileExample;
    FileReader exReader;
    FileWriter exWriter;
    Map<String, String> row;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Можноли вместо getApplicationContext() , писать this
        try {
            fileExample = new File(getApplicationContext().getExternalFilesDir(null), "anEx.docx");
            if(!fileExample.mkdirs()){
                Log.e(LOG_TAG,"Directory not created");
            }
            exWriter = new FileWriter(fileExample);
            exWriter.append(getString(R.string.large_text));

            exReader = new FileReader(fileExample);
             result = exReader.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG,"Error");
        }


      //  Bitmap bitmap = BitmapFactory.decodeFile(fileExample.getAbsolutePath());

        content = prepareContent();
        final BaseAdapter listContentAdapter = createAdapter(content);
        list.setAdapter(listContentAdapter);
        listContentAdapter.notifyDataSetChanged();


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                //final View item = (TextView) parent.getItemAtPosition(position);

                ImageButton imageButtonDelete = findViewById(R.id.btnDelete);
                View.OnClickListener deleteBtn = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.animate().setDuration(20).alpha(0)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        // list.removeView(item);
                                        simpleAdapterContent.remove(position);
                                        listContentAdapter.notifyDataSetChanged();
                                        view.setAlpha(1);
                                    }
                                });
                    }
                };
                imageButtonDelete.setOnClickListener(deleteBtn);

                Button btnAdd =findViewById(R.id.btnAdd);
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText newString = findViewById(R.id.editText);
                        try {
                            exWriter.append(newString.getText().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        row.put(TITLE, newString.getText().toString());
                        row.put(SUBTITLE, String.valueOf(newString.length()));
                        simpleAdapterContent.add(row);
                        listContentAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        final SwipeRefreshLayout swipeLayout = findViewById(R.id.swiperefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            // Будет вызван, когда пользователь потянет список вниз
            @Override
            public void onRefresh() {
                updateList();
                swipeLayout.setRefreshing(false);
            }
        });
    }

    private void updateList() {
        result = exReader.toString();
        content = prepareContent();
    }

    @NonNull
    private BaseAdapter createAdapter(String[] values) {
        list = findViewById(R.id.list);
        simpleAdapterContent = new ArrayList<>();

        for (String value : values) {
            Map<String, String> row = new HashMap<>();
            row.put(TITLE, value);
            row.put(SUBTITLE, String.valueOf(value.length()));
            simpleAdapterContent.add(row);
        }
        return new SimpleAdapter(
                this,
                simpleAdapterContent,
                R.layout.list_item,
                new String[]{TITLE, SUBTITLE},
                new int[]{R.id.textItem1, R.id.textItem2}
        );
    }

    @NonNull
    private String[] prepareContent() {
        return result.split("\n\n");
    }
}