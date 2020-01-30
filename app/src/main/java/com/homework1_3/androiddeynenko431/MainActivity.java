package com.homework1_3.androiddeynenko431;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final static String LOG_TAG = "sample";
    private final static String TITLE = "title";
    private final static String SUBTITLE = "subtitle";
    List<Map<String, String>> simpleAdapterContent;
    private ListView list;
    private String result;
    private String[] content;
    File fileExample;
    FileReader exReader;
    FileWriter exWriter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fileExample = new File(getExternalFilesDir(null), "anEx.docx");
        if (fileExample.getParentFile() != null && !fileExample.getParentFile().mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }

        if (!fileExample.exists()) {
            try {
                fileExample.createNewFile();
            } catch (IOException e) {
                Log.e(LOG_TAG, "File not created");
            }
        }

        // Закрываем ресурс с помощью try with resources
        try (BufferedWriter exWriter = new BufferedWriter(new OutputStreamWriter(new
                FileOutputStream(fileExample)))) {
            exWriter.append(getString(R.string.large_text));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error");
        }
        readFile();

        content = prepareContent();
        final BaseAdapter listContentAdapter = createAdapter(content);
        list.setAdapter(listContentAdapter);
        listContentAdapter.notifyDataSetChanged();


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position,
                                    long id) {
                ImageButton imageButtonDelete = findViewById(R.id.btnDelete);
                View.OnClickListener deleteBtn = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.animate().setDuration(20).alpha(0)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        simpleAdapterContent.remove(position);
                                        listContentAdapter.notifyDataSetChanged();
                                        view.setAlpha(1);
                                    }
                                });
                    }
                };
                imageButtonDelete.setOnClickListener(deleteBtn);

                Button btnAdd = findViewById(R.id.btnAdd);
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText newString = findViewById(R.id.editText);
                        try {
                            exWriter.append(newString.getText().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Map<String, String> row = new HashMap<>();
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

    private void readFile() {
        try (BufferedReader exReader = new BufferedReader(new InputStreamReader(new
                FileInputStream(fileExample)))) {
            String line;
            StringBuilder resultBuilder = new StringBuilder();
            while ((line = exReader.readLine()) != null) {
                resultBuilder.append(line);
                resultBuilder.append("\n");
            }
            result = resultBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateList() {
        readFile();
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