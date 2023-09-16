package com.a51703210_diary.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.a51703210_diary.R;
import com.a51703210_diary.adapters.DiariesAdapter;
import com.a51703210_diary.models.Diary;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://diary-da96f-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference myRef = database.getReference("diary");
    RecyclerView rvDiary;
    List<Diary> listDiary = new ArrayList<>();
    ArrayList<String> mKeys = new ArrayList<>();
    DiariesAdapter adapter;
    boolean isChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.Theme_DarkMode);
        }
        else {
            setTheme(R.style.Theme_Light);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvDiary = findViewById(R.id.rvDiary);
        getData();
        rvDiary.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DiariesAdapter(this, listDiary);
        rvDiary.setAdapter(adapter);



//        Diary diary = new Diary();
//        diary.setId(myRef.push().getKey());
//        diary.setDate(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
//        diary.setTitle("testTitle");
//        diary.setContent("testContent");
//        myRef.child(diary.getId()).setValue(diary);
//        myRef.child("testId").removeValue();
    }
    public void getData() {
        listDiary = new ArrayList<>();
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Diary diary = snapshot.getValue(Diary.class);
                if (diary != null) {
                    listDiary.add(diary);
                    String key = snapshot.getKey();
                    mKeys.add(key);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Diary diary = snapshot.getValue(Diary.class);
                if (diary == null || listDiary == null || listDiary.isEmpty())
                    return;

                String key = snapshot.getKey();
                int index = mKeys.indexOf(key);
                listDiary.set(index, diary);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Diary p = snapshot.getValue(Diary.class);
                if (p == null || listDiary == null || listDiary.isEmpty())
                    return;
                String key = snapshot.getKey();
                int index = mKeys.indexOf(key);
                if (index != -1) {
                    listDiary.remove(index);
                    mKeys.remove(index);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkable = menu.findItem(R.id.menuSwitch);
        checkable.setChecked(isChecked);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuAdd:
                Intent i = new Intent(this, ViewDiaryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("diary_mode","addDiary");
                i.putExtras(bundle);
                startActivity(i);
                break;

            case R.id.menuSwitch:
                isChecked = !item.isChecked();
                item.setChecked(isChecked);
                if (isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    setTheme(R.style.Theme_DarkMode);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    setTheme(R.style.Theme_Light);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null)
            adapter.release();
    }
}