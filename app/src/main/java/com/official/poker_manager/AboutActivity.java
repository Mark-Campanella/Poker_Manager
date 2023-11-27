package com.official.poker_manager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class AboutActivity extends AppCompatActivity {
    public TextView text_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        text_about = findViewById(R.id.txt_about);
        //criando um objeto do tipo inputstream
        InputStream inputStream = this.getResources().openRawResource(R.raw.about);
        //criando um objeto de buffer
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        //criando o objeto de Buffer de String
        //passando do buffer para o bueffer de string
        String strData = "";
        StringBuffer stringBuffer = new StringBuffer();
        if (null != inputStream) try {
            while (null != (strData = bufferedReader.readLine())) {
                stringBuffer.append(strData + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        text_about.setText(stringBuffer);
    }
}