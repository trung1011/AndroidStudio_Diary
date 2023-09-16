package com.a51703210_diary.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.a51703210_diary.R;
import com.a51703210_diary.models.Diary;
import com.a51703210_diary.models.UndoRedoHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ViewDiaryActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://diary-da96f-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference myRef = database.getReference("diary");
    String mode;
    final Calendar myCalendar= Calendar.getInstance();
    Diary diary;
    EditText etDate, etTitle, etContent;
    Button btnShare, btnSave, btnCancel, btnDelete, btnSpeak, btnUndo, btnRedo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.Theme_DarkMode);
        }
        else {
            setTheme(R.style.Theme_Light);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_diary);
        init();
        etDate.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateLabel();
            }

        };
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ViewDiaryActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Bundle bundle = getIntent().getExtras();
        mode = bundle.getString("diary_mode");
        if (mode.equals("viewDetail")) {
            diary.setId(bundle.getString("diary_id"));
            diary.setTitle(bundle.getString("diary_title"));
            diary.setDate(bundle.getString("diary_date"));
            diary.setContent(bundle.getString("diary_content"));
            etDate.setText(diary.getDate().toString());
            etTitle.setText(diary.getTitle().toString());
            etContent.setText(diary.getContent().toString());
        }
        else {

        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diary == null || diary.getId()==null || diary.getId().isEmpty() ){
                    Log.d("V", "NUll");
                    diary = new Diary();
                    diary.setId(myRef.push().getKey());
                    diary.setDate(String.valueOf(etDate.getText()));
                    diary.setTitle(String.valueOf(etTitle.getText()));
                    diary.setContent(String.valueOf(etContent.getText()));
                    myRef.child(diary.getId()).setValue(diary);
                    finish();
                }
                else{
                    Log.d("V", "Not NUll");
                    diary.setDate(String.valueOf(etDate.getText()));
                    diary.setTitle(String.valueOf(etTitle.getText()));
                    diary.setContent(String.valueOf(etContent.getText()));
                    myRef.child(diary.getId()).setValue(diary);
                    finish();
                }

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diary != null && diary.getId()!=null &&  !diary.getId().isEmpty()){
                    myRef.child(diary.getId()).removeValue();
                    finish();
                }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "On " + etDate.getText() + "\n\n" + etTitle.getText() + "\n\n" + etContent.getText();
                Intent mShareIntent = new Intent(Intent.ACTION_SEND);
                mShareIntent.setType("text/plain");
                mShareIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(mShareIntent, "Share diary via"));
            }
        });

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTitle.hasFocus() || etContent.hasFocus())
                    speak();
            }
        });
        UndoRedoHelper undoRedoTitle = new UndoRedoHelper(etTitle);
        UndoRedoHelper undoRedoContent = new UndoRedoHelper(etContent);


        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTitle.hasFocus()) {
                    if (undoRedoTitle.getCanUndo()) {
                        undoRedoTitle.undo();
                    }
                }
                if (etContent.hasFocus()) {
                    if (undoRedoContent.getCanUndo()) {
                        undoRedoContent.undo();
                    }
                }

            }
        });

        btnRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTitle.hasFocus()) {
                    if (undoRedoTitle.getCanRedo()) {
                        undoRedoTitle.redo();
                    }
                }
                if (etContent.hasFocus()) {
                    if (undoRedoContent.getCanRedo()) {
                        undoRedoContent.redo();
                    }
                }

            }
        });

    }

    private void speak(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi, speak something");

        try{
            startActivityForResult(intent,1000);
        }
        catch (Exception e){
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1000:{
                if (resultCode == RESULT_OK && null!=data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (etTitle.hasFocus())
                        etTitle.setText(etTitle.getText()+result.get(0));
                    if (etContent.hasFocus())
                        etContent.setText(etContent.getText()+result.get(0));
                }
                break;
            }
        }
    }

    private void updateLabel(){
        String myFormat="dd-MM-yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        etDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void init(){
        diary = new Diary();
        etDate = findViewById(R.id.etDate);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnShare = findViewById(R.id.btnShare);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnDelete = findViewById(R.id.btnDelete);
        btnSpeak = findViewById(R.id.btnSpeak);
        btnUndo = findViewById(R.id.btnUndo);
        btnRedo = findViewById(R.id.btnRedo);
    }

}