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


public class Employ extends AppCompatActivity {
    String title = "";      //취업공지 제목
    TextView textView;  //test
    TextView test;  //test
    ListView listView;  //취업공지 리스트뷰
    ListView kwListView;    //키워드 리스트뷰
    ArrayList<String> titles = new ArrayList<>();   //취업공지 제목들
    List<String> list = new ArrayList<>();      //취업공지 리스트
    List<String> kwList = new ArrayList<>();       //키워드 리스트
    //성신홈페이지 > 성신커뮤니티 > 공지사항 > 취업공지
    String url = "https://www.sungshin.ac.kr/main_kor/11116/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGbWFpbl9rb3IlMkYzMTgzJTJGYXJ0Y2xMaXN0LmRvJTNG";
    EditText kwinput;       //키워드 입력창
    Button addkw;       //키워드 등록버튼
    int count;
    TextView kwCount;
    String id;      // 로그인된 사용자 ID

    // 파이어베이스 데이터베이스 연동  //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;
    //private FirebaseDatabase mDatabase2;
    //private DatabaseReference mReference2;

    private ArrayList<String> UrlArrayList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employ);
        //액션바 숨김
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 사용자 id  받아오기
        SharedPreferences sharedPreferences= getSharedPreferences("test", MODE_PRIVATE);
        id = sharedPreferences.getString("id","");
        int indexes = id.indexOf("@");
        String ids = id.substring(0,indexes);

        //키워드 등록 - 파이어베이스 저장
        kwinput = (EditText) findViewById(R.id.kwinput);
        addkw = (Button) findViewById(R.id.addkw);
        kwCount = (TextView) findViewById(R.id.kwCount);

        ArrayAdapter kwadapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, kwList);
        //MyAdapter kwadapter = new MyAdapter();
        kwListView = (ListView) findViewById(R.id.kwList);
        kwListView.setAdapter(kwadapter);
        initDatabase();     //데이터 초기설정
        // 키워드 개수 출력
        count = kwadapter.getCount();
        kwCount.setText(count + "");
        mReference = mDatabase.getReference("waang/UserAccount/" + ids + "/취업키워드");
        //mReference2 = mDatabase2.getReference("취업키워드");
        //등록버튼 클릭
        addkw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count >= 10)
                    Toast.makeText(Employ.this, "키워드 등록은 10개까지만 가능합니다.", Toast.LENGTH_LONG).show();
                else if(count > -1){
                    String kw = kwinput.getText().toString();
                    mReference.child(kw).setValue(kw);
                    //mReference2.child(kw).setValue(kw);
                }
            }
        });
        // 데이터가 수정될 때마다 리스트뷰 수정
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                kwadapter.clear();
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String msg2 = messageData.getValue().toString();
                    kwadapter.add(msg2);
                    // 키워드 구독 - 푸시알림
                    FirebaseMessaging.getInstance().subscribeToTopic(msg2);
                }
                count = kwadapter.getCount();
                kwCount.setText(count + "");       // 키워드 총 갯수 출력
                kwadapter.notifyDataSetChanged();
                kwListView.setSelection(kwadapter.getCount() - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // 리스트뷰 삭제 - 길게 클릭
        kwListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> a_parent, View a_view, int a_position, long a_id) {
                String kw = (String) a_parent.getAdapter().getItem(a_position);
                mReference.child(kw).removeValue();
                kwadapter.remove(kw);
                kwadapter.notifyDataSetChanged();
                // 키워드 구독 해제
                FirebaseMessaging.getInstance().unsubscribeFromTopic(kw);

                Toast.makeText(Employ.this, "삭제 완료", Toast.LENGTH_LONG).show();
                // False 로 return 할 경우 long click event 후 click event 발생
                return true;
            }
        });


        //취업공지 리스트뷰
        textView = (TextView) findViewById(R.id.test);
        final Bundle bundle = new Bundle();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);

        new Thread(){
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(url).get();
                    Elements contents = doc.select(".artclLinkView strong");
                    Elements urls = doc.select("._artclTdTitle a");
                    for(Element buff : contents){
                        String save = buff.text();
                        titles.add(save);
                        list.add(save);
                    }
                    for(Element e : urls){
                        //noticeCrawlingArrayList.add(e.text());
                        UrlArrayList.add(e.attr("href"));
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
                String employTitle = (String) a_parent.getAdapter().getItem(a_position);
                String employUrl = UrlArrayList.get((int)a_id);
                Intent intent = new Intent(getApplicationContext(), EmployPage.class);
                intent.putExtra("url", employUrl);
                intent.putExtra("title", employTitle);
                startActivity(intent);

                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.sungshin.ac.kr" + UrlArrayList.get(a_position)));
                //startActivity(intent);
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
    /* 지우지 마삼!
    cMyAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.x), Name[i]);
     */
    private void initDatabase() {

        mDatabase = FirebaseDatabase.getInstance();

        mReference = mDatabase.getReference("log");
        mReference.child("log").setValue("check");

        mChild = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mReference.addChildEventListener(mChild);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReference.removeEventListener(mChild);
    }
}
