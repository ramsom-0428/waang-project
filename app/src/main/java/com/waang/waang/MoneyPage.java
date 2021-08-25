package com.waang.waang;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MoneyPage extends AppCompatActivity {

    String employUrl;
    String employTitle;
    TextView empTit;
    TextView employContents;
    TextView employWhen;
    TextView link;
    ArrayList<String> contents = new ArrayList<>();
    String content = "";
    ArrayList<String> whens = new ArrayList<>();
    String when = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mileage_page);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        empTit = (TextView) findViewById(R.id.employTitle);
        employContents = (TextView) findViewById(R.id.employContents);
        employWhen = (TextView) findViewById(R.id.left);
        link = (TextView) findViewById(R.id.link);

        Intent intent = getIntent();
        employTitle = intent.getStringExtra("title");
        employUrl = intent.getStringExtra("url");

        System.out.println("접속 url: " + employUrl);
        empTit.setText(employTitle);

        //링크 이동 버튼
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goHTML= new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.sungshin.ac.kr" + employUrl));
                startActivity(goHTML);
            }
        });
        //crawling
        final Bundle bundle = new Bundle();
        new Thread(){
            @Override
            public void run() {
                Document doc = null;
                try {
                    //https://www.sungshin.ac.kr 앞에 추가
                    doc = Jsoup.connect("https://www.sungshin.ac.kr" + employUrl).get();
                    Elements e_contents = doc.select(".artclView p");
                    Elements e_when = doc.select(".left dl");
                    for(Element buff : e_contents){
                        String save = buff.text();
                        contents.add(save);
                    }
                    for(String strBuff : contents){
                        content += strBuff + "\n";
                    }
                    for(Element buff : e_when){
                        String save = buff.text();
                        whens.add(save);
                    }
                    for(String strBuff : whens){
                        when += strBuff + "\t\t";
                    }

                    bundle.putString("content", content);
                    Message msg = handler.obtainMessage();
                    msg.setData(bundle);
                    handler.sendMessage(msg);

                    bundle.putString("when", when);
                    Message msg2 = handler.obtainMessage();
                    msg2.setData(bundle);
                    handler.sendMessage(msg2);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            employContents.setText(bundle.getString("content"));
            employWhen.setText(bundle.getString("when"));
        }
    };
}
