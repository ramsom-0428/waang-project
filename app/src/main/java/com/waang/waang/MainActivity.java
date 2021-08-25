package com.waang.waang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView userName2;
    private TextView majorshow;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private DatabaseReference majorReference;

    String id;
    String title = "";
    ArrayList<String> titles = new ArrayList<>();
    TextView noticeView;

    private String url = "https://waangsungshin.blogspot.com/search/label/%EA%B3%B5%EC%A7%80%EC%82%AC%ED%95%AD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        ImageView graduate;
        TextView graduateTxt;
        ImageView mileage;
        TextView mileageTxt;
        ImageView employ;
        TextView employTxt;
        ImageView mypage;
        TextView mypageTxt;

        graduate = (ImageView) findViewById(R.id.graduate);
        graduateTxt = (TextView) findViewById(R.id.graduateTxt);
        mileage = (ImageView) findViewById(R.id.mileage);
        mileageTxt = (TextView) findViewById(R.id.mileageTxt);
        employ = (ImageView) findViewById(R.id.employ);
        employTxt = (TextView) findViewById(R.id.employTxt);
        mypage = (ImageView) findViewById(R.id.mypage);
        mypageTxt = (TextView) findViewById(R.id.mypageTxt);
        noticeView = (TextView) findViewById(R.id.noticeView);

        userName2 = (TextView) findViewById(R.id.userName2);
        majorshow = (TextView) findViewById(R.id.majorshow);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String numberinfo = user.getEmail();
        int index = numberinfo.indexOf("@");
        id = numberinfo.substring(0, index);

        // 사용자 닉네임 출력
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("waang/UserAccount/" + id);
        mReference.child("names").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue().toString();
                userName2.setText(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //사용자 전공 출력
        majorReference = mDatabase.getReference("waang/UserAccount/" + id).child("전공");
        majorReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                String stmajor = snapshots.getValue(String.class);
                majorshow.setText(stmajor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        graduate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Graduate.class);
                startActivity(intent);
            }
        });
        graduateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Graduate.class);
                startActivity(intent);
            }
        });
        mileage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Mileage.class);
                startActivity(intent);
            }
        });
        mileageTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Mileage.class);
                startActivity(intent);
            }
        });
        employ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Employ.class);
                startActivity(intent);
            }
        });
        employTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Employ.class);
                startActivity(intent);
            }
        });
        mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                startActivity(intent);
            }
        });
        mypageTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                startActivity(intent);
            }
        });

        // notice 크롤링
        noticeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Notice.class);
                startActivity(intent);
            }
        });

        final Bundle bundle = new Bundle();
        new Thread(){
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(url).get();
                    Elements contents = doc.select("div.blog-posts.hfeed.container article.post-outer-container div.post-outer div.post h3.post-title.entry-title a");
                    for(Element buff : contents){
                        String save = buff.text();
                        titles.add(save);
                    }
                    title = titles.get(0);

                    bundle.putString("title", title);
                    Message msg = handler.obtainMessage();
                    msg.setData(bundle);
                    handler.sendMessage(msg);

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
            noticeView.setText(bundle.getString("title"));
        }
    };

}