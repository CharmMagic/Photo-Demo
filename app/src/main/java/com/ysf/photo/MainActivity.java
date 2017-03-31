package com.ysf.photo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ysf.photo.basephoto.BasePhotoActivity;
import com.ysf.photo.photo.PhotoActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button button,button1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=(Button) findViewById(R.id.button);
        button1=(Button) findViewById(R.id.button1);
        button.setOnClickListener(this);
        button1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.button:
               Intent button =new Intent(this, PhotoActivity.class);
               startActivity(button);
               break;
           case R.id.button1:
               Intent button1 =new Intent(this, BasePhotoActivity.class);
               startActivity(button1);
               break;
       }
    }
}