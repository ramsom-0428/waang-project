package com.waang.waang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class Mileage extends AppCompatActivity {

    ListView listView;
    TextView textView;
    String moneyurl = "https://www.sungshin.ac.kr/kilaw/11605/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGa2lsYXclMkYzMzUyJTJGYXJ0Y2xMaXN0LmRvJTNGYmJzQ2xTZXElM0QlMjZiYnNPcGVuV3JkU2VxJTNEJTI2aXNWaWV3TWluZSUzRGZhbHNlJTI2c3JjaENvbHVtbiUzRHNqJTI2c3JjaFdyZCUzRCVFQyU5RSVBNSVFRCU5NSU5OSUyNg%3D%3D";
    String mileageurl = "https://www.sungshin.ac.kr/inno/16106/subview.do";
    String title = "";      //공지 제목
    ArrayList<String> titles = new ArrayList<>();   //공지 제목들
    List<String> list = new ArrayList<>();      //공지 리스트
    private ArrayList<String> mileageUrlArrayList = new ArrayList<String>(); //마일리지 url
    private ArrayList<String> moneyUrlArrayList = new ArrayList<String>();  //장학금 url

    TextView btn1;
    TextView btn2;
    TextView mm;
    TextView mm2;
    TextView mm3;
    TextView mm4;
    TextView mm5;
    TextView ss;
    TextView ss2;
    TextView ss3;
    TextView ss4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mileage);
        //액션바 숨김
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        btn1 = (TextView) findViewById(R.id.btn1);
        btn2 = (TextView) findViewById(R.id.btn2);

        mm = (TextView) findViewById(R.id.mm);
        mm2 = (TextView) findViewById(R.id.mm2);
        mm3 = (TextView) findViewById(R.id.mm3);
        mm4 = (TextView) findViewById(R.id.mm4);
        mm5 = (TextView) findViewById(R.id.mm5);
        mm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), mileageClick.class);
                intent.putExtra("url", "https://rule.sungshin.ac.kr/service/law/lawView.do?seq=95&historySeq=0&gubun=cur&tree=part");
                startActivity(intent);
            }
        });
        mm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), mileageClick.class);
                intent.putExtra("url", "https://rule.sungshin.ac.kr/service/law/lawView.do?seq=259&historySeq=0&gubun=cur&tree=part");
                startActivity(intent);
            }
        });
        mm3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), mileageClick.class);
                intent.putExtra("url", "https://www.sungshin.ac.kr/main_kor/11031/subview.do");
                startActivity(intent);
            }
        });
        mm4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), mileageClick.class);
                intent.putExtra("url", "https://www.sungshin.ac.kr/main_kor/11032/subview.do");
                startActivity(intent);
            }
        });
        mm5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), mileageClick.class);
                intent.putExtra("url", "https://www.kosaf.go.kr/ko/main.do");
                startActivity(intent);
            }
        });
        ss = (TextView) findViewById(R.id.ss);
        ss2 = (TextView) findViewById(R.id.ss2);
        ss3 = (TextView) findViewById(R.id.ss3);
        ss4 = (TextView) findViewById(R.id.ss4);
        ss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), mileageClick.class);
                intent.putExtra("url", "https://www.sungshin.ac.kr/inno/16088/subview.do");
                startActivity(intent);
            }
        });
        ss2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), mileageClick.class);
                intent.putExtra("url", "https://www.sungshin.ac.kr/inno/16698/subview.do");
                startActivity(intent);
            }
        });
        ss3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), mileageClick.class);
                intent.putExtra("url", "https://www.sungshin.ac.kr/inno/16092/subview.do");
                startActivity(intent);
            }
        });
        ss4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), mileageClick.class);
                intent.putExtra("url", "https://www.sungshin.ac.kr/inno/16904/subview.do");
                startActivity(intent);
            }
        });

        //공지 리스트뷰
        textView = (TextView) findViewById(R.id.test);
        final Bundle bundle = new Bundle();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);

        //장학금 공지사항 버튼 클릭
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                btn1.setPressed(true);
                new Thread(){
                    @Override
                    public void run() {
                        Document doc = null;
                        try {
                            doc = Jsoup.connect(moneyurl).get();
                            Elements contents = doc.select(".artclLinkView strong");
                            Elements urls = doc.select("._artclTdTitle a");
                            for(Element buff : contents){
                                String save = buff.text();
                                titles.add(save);
                                list.add(save);
                            }
                            for(Element e : urls){
                                //noticeCrawlingArrayList.add(e.text());
                                moneyUrlArrayList.add(e.attr("href"));
                                System.out.println(e.attr("href"));
                            }
                            for(String strBuff : titles){
                                title += strBuff + "\n";
                            }


                            bundle.putString("title", title);
                            Message msg = handler.obtainMessage();
                            msg.setData(bundle);
                            handler.sendMessage(msg);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a_parent, View a_view, int a_position, long a_id) {
                        String moneyTitle = (String) a_parent.getAdapter().getItem(a_position);
                        String moneyUrl = moneyUrlArrayList.get((int)a_id);
                        Intent intent = new Intent(getApplicationContext(), MoneyPage.class);
                        intent.putExtra("url", moneyUrl);
                        intent.putExtra("title", moneyTitle);
                        startActivity(intent);

                        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.sungshin.ac.kr" + UrlArrayList.get(a_position)));
                        //startActivity(intent);
                    }
                });
            }
        });
        //S+마일리지 공지사항 버튼 클릭
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                btn2.setPressed(true);
                new Thread(){
                    @Override
                    public void run() {
                        Document doc = null;
                        try {
                            doc = Jsoup.connect(mileageurl).get();
                            Elements contents = doc.select(".artclLinkView strong");
                            Elements urls = doc.select("._artclTdTitle a");
                            for(Element buff : contents){
                                String save = buff.text();
                                titles.add(save);
                                list.add(save);
                            }
                            for(Element e : urls){
                                //noticeCrawlingArrayList.add(e.text());
                                mileageUrlArrayList.add(e.attr("href"));
                                System.out.println(e.attr("href"));
                            }
                            for(String strBuff : titles){
                                title += strBuff + "\n";
                            }


                            bundle.putString("title", title);
                            Message msg = handler.obtainMessage();
                            msg.setData(bundle);
                            handler.sendMessage(msg);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a_parent, View a_view, int a_position, long a_id) {
                        String mileageTitle = (String) a_parent.getAdapter().getItem(a_position);
                        String mileageUrl = mileageUrlArrayList.get((int)a_id);
                        Intent intent = new Intent(getApplicationContext(), MileagePage.class);
                        intent.putExtra("url", mileageUrl);
                        intent.putExtra("title", mileageTitle);
                        startActivity(intent);

                        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.sungshin.ac.kr" + UrlArrayList.get(a_position)));
                        //startActivity(intent);
                    }
                });
            }
        });

    }
    //oncreate 끝
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            textView.setText(bundle.getString("title"));
        }
    };
}

