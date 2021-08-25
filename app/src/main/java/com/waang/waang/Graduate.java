package com.waang.waang;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

import jxl.Sheet;
import jxl.Workbook;


public class Graduate extends AppCompatActivity {

    //레이아웃 미완성
    Button goCredit;
    TextView info_name;
    TextView core1,core2, core3;
    LinearLayout invisibleLayout;

    // 교양파트
    TextView e1, e2, e3, e4;    // 공통, 핵심, 일반, 진로소양, 총합
    TextView elNotice; // 공교 알림
    TextView elNum; //핵교 알림
    // 전공파트
    TextView cm, im, graduatemajor; // 핵전, 심전, 총합
    TextView majNotice; // 전공 알림

    TextView el1, el2, el3, el4, el5, el6;   //핵심교양 영역 6가지

    // 영역별 수강학점 출력 (임시)
    TextView myMajor;
    // 복/부/심전 출력
    TextView whatMajor;
    TextView Dmajor;
    TextView mustScore;
    TextView scoreA;
    TextView scoreB;
    TextView myScoreA;
    TextView myScoreB;
    TextView myScoreT;
    LinearLayout invisible1;
    LinearLayout invisible2;

    // 학점 비교
    TextView score1, score2, score3, score4;
    TextView score11, score12, score13, score14;
    TextView score22, score23, score24;
    String a,b;
    String id;
    SQLiteDatabase db;
    DatabaseHelper dh;
    ContentValues values = new ContentValues();
    // 파이어베이스 데이터베이스 연동
    private FirebaseAuth firebaseAuth;  //파이어베이스 인증 처리
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graduate);
        //액션바 숨김
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // CreditMain 가는 버튼
        goCredit = (Button) findViewById(R.id.goCredit);
        goCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreditMain.class);
                startActivity(intent);
            }
        });

        myMajor = (TextView) findViewById(R.id.myMajor);
        info_name = (TextView) findViewById(R.id.info_name);
        whatMajor = (TextView) findViewById(R.id.whatMajor);
        Dmajor = (TextView) findViewById(R.id.Dmajor);
        mustScore = (TextView) findViewById(R.id.mustScore);
        core1 = (TextView) findViewById(R.id.core1);
        core2 = (TextView) findViewById(R.id.core2);
        core3 = (TextView) findViewById(R.id.core3);
        score1 = (TextView) findViewById(R.id.score1);
        score2 = (TextView) findViewById(R.id.score2);
        score3 = (TextView) findViewById(R.id.score3);
        score4 = (TextView) findViewById(R.id.score4);
        score11 = (TextView) findViewById(R.id.score11);
        score12 = (TextView) findViewById(R.id.score12);
        score13 = (TextView) findViewById(R.id.score13);
        score14 = (TextView) findViewById(R.id.score14);
        score22 = (TextView) findViewById(R.id.score22);
        score23 = (TextView) findViewById(R.id.score23);
        score24 = (TextView) findViewById(R.id.score24);
        scoreA = (TextView) findViewById(R.id.scoreA);
        scoreB = (TextView) findViewById(R.id.scoreB);
        myScoreA = (TextView) findViewById(R.id.myScoreA);
        myScoreB = (TextView) findViewById(R.id.myScoreB);
        myScoreT = (TextView) findViewById(R.id.myScoreT);
        invisible1 = (LinearLayout) findViewById(R.id.invisible1);
        invisible2 = (LinearLayout) findViewById(R.id.invisible2);
        el1 = (TextView) findViewById(R.id.el1);
        el2 = (TextView) findViewById(R.id.el2);
        el3 = (TextView) findViewById(R.id.el3);
        el4 = (TextView) findViewById(R.id.el4);
        el5 = (TextView) findViewById(R.id.el5);
        el6 = (TextView) findViewById(R.id.el6);
        elNotice = (TextView) findViewById(R.id.elNotice);
        elNum = (TextView) findViewById(R.id.elNum);
        majNotice = (TextView) findViewById(R.id.majNotice);
        invisibleLayout = (LinearLayout) findViewById(R.id.invisibleLayout);


        //교양 학점
        e1 = (TextView) findViewById(R.id.e1);
        e2 = (TextView) findViewById(R.id.e2);
        e3 = (TextView) findViewById(R.id.e3);
        e4 = (TextView) findViewById(R.id.e4);

        // 내전공 학점
        cm = (TextView) findViewById(R.id.cm);
        im = (TextView) findViewById(R.id.im);
        graduatemajor = (TextView) findViewById(R.id.graduatemajor);


        //id 가져오기
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String numberinfo = user.getEmail();
        int idIndex = numberinfo.indexOf("@");
        id = numberinfo.substring(0, idIndex);
        // 사용자 닉네임 출력
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("waang/UserAccount/" + id);
        mReference.child("names").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue().toString();
                info_name.setText(username);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("waang/UserAccount/" + id);
        //학점 받아오기 및 계산

        mReference.child("총 수강학점").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getKey().contains("핵심교양") && snapshot.getValue(Double.class)!=0.0) {
                        String tempstr = snapshot.getKey().substring(4, 9);
                        if(tempstr.equals("공학과기술")) el1.setTextColor(Color.parseColor("#FFBA95F1"));
                        else if(tempstr.equals("문학과예술")) el2.setTextColor(Color.parseColor("#FFBA95F1"));
                        else if(tempstr.equals("사회의이해")) el3.setTextColor(Color.parseColor("#FFBA95F1"));
                        else if(tempstr.equals("역사의해석")) el4.setTextColor(Color.parseColor("#FFBA95F1"));
                        else if(tempstr.equals("인식과가치")) el5.setTextColor(Color.parseColor("#FFBA95F1"));
                        else if(tempstr.equals("자연의설명")) el6.setTextColor(Color.parseColor("#FFBA95F1"));
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //교양 학점 출력 (e1, e2, e3, e4, e5) (공통 핵심 일반 진로소양 총합)
        mReference = mDatabase.getReference("waang/UserAccount/" + id);
        mReference.child("총 수강학점").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double e1sum = 0.0;
                Double e2sum = 0.0;
                Double e3sum = 0.0;
                Double e4sum = 0.0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getKey().contains("공통교양")) {
                        Double credit = snapshot.getValue(Double.class);
                        e1sum += credit;
                    }else if(snapshot.getKey().contains("핵심교양")) {
                        Double credit = snapshot.getValue(Double.class);
                        e2sum += credit;
                    }else if(snapshot.getKey().contains("진로소양")) {
                        Double credit = snapshot.getValue(Double.class);
                        e3sum += credit;
                    }
                }
                e1.setText(e1sum+"");
                e2.setText(e2sum+"");
                e3.setText(e3sum+"");

                e4sum = e1sum + e2sum + e3sum + e4sum;
                e4.setText(e4sum+"");

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // 전공 학점
        mReference = mDatabase.getReference("waang/UserAccount/" + id);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String tempstr = snapshot.getValue().toString();
                    list.add(tempstr);
                }
                // 내 전공
                String major = list.get(6);
                // 교양 알림
                if(major.equals("바이오신약의과학부") || major.equals("바이오헬스융합학부")) {
                    elNotice.setText("공통교양은 필수과목 6학점을 포함하여 총 15학점을 이수하여야 하며, 자연과학대학, 지식서비스공과대학, 바이오신약의과학부," +
                            " 바이오헬스융합학부는 필수과목 6학점과 SW문해영역 및 경험적·수리적추리영역을 필수 이수하여야 한다. " +
                            "단, 사범 대학은 필수과목 6학점을 포함하여 총 9학점을 이수하여야 한다. [교양교육과정 세부내용 졸업가이드 19~26페이지 확인 필수]");
                }
                if(major.equals("간호대학") || major.equals("음악대학") || major.equals("융합문화예술대학"))
                    elNum.setText("핵심교양(3영역) | ");
                else if(major.equals("화학・에너지융합학부") || major.equals("청정융합에너지공학과") || major.equals("바이오식품공학과")
                        || major.equals("바이오생명공학과") || major.equals("바이오신약의과학부") || major.equals("바이오헬스융합학부") )
                    elNum.setText("핵심교양(3영역/자연의설명 포함) | ");
                if(major.equals("융합문화예술대학"))
                    majNotice.setText("융합문화예술대학은 전공 84학점 이상을 이수함에 있어 융합문화예술대학 기초전공과목 12학점, " +
                            "소속학과 주전공과목 48학 점, 융합전공과목 24학점을 충족해야 한다. 단, 4학년으로 편입학한 자는 기초전공과목 3학점 이상, " +
                            "소속학과 주전공과목 12 학점 이상, 융합전공과목 6학점 이상을 이수하여야 하고, 전적대학 인정학점을 포함하여 전공 84학점을 충족해야 한다.");
                else majNotice.setVisibility(View.GONE);

                // 복/부/심전 출력
                String minorstr = list.get(4);
                int indexA = minorstr.indexOf("=");
                int indexB = minorstr.indexOf("}");
                String whatMinor = minorstr.substring(1, indexA);
                String minor = minorstr.substring(indexA+1, indexB);
                whatMajor.setText(whatMinor);
                if(whatMinor.equals("부전공")) {
                    invisible1.setVisibility(View.INVISIBLE);
                    invisible2.setVisibility(View.INVISIBLE);
                    mustScore.setText("필수이수학점 : ");
                    myScoreT.setText("학생개인학점 : ");
                } else if(whatMinor.equals("심화전공"))
                    invisibleLayout.setVisibility(View.INVISIBLE);

                // 핵심전공, 심화전공 학점 구하기
                String ArrayList [] = list.get(5).split("classSection=");
                Double CM = 0.0;    Double IM = 0.0;    // 내전공의 핵전, 심전
                Double minorCM = 0.0; Double minorIM = 0.0;     // 복\부\심전의 핵전, 심전

                for(int i=0; i<ArrayList.length; i++) {
                    // majorCourse 구하기
                    int index1 = ArrayList[i].indexOf("majorCourse=");
                    int index12 = ArrayList[i].indexOf("}");
                    if(index1<0) continue;
                    String majorCourse = ArrayList[i].substring(index1+12, index12);
                    // classification 구하기
                    int index2 = ArrayList[i].indexOf("classClassification=");
                    int index3 = ArrayList[i].indexOf(", className=");
                    String classification = ArrayList[i].substring(index2+20, index3);
                    // classSection 구하기
                    //int index4 = ArrayList[i].indexOf("classSection=");
                    int index5 = ArrayList[i].indexOf(", classCredit=");
                    String classSection = ArrayList[i].substring(0, index5);
                    // className 구하기
                    int index6 = ArrayList[i].indexOf("className=");
                    int index7 = ArrayList[i].indexOf(", majorCourse=");
                    String className = ArrayList[i].substring(index6+10, index7);
                    // classCredit 구하기
                    int index8 = ArrayList[i].indexOf("classCredit=");
                    int index9 = ArrayList[i].indexOf(", classClassification=");
                    Double classCredit = Double.parseDouble(ArrayList[i].substring(index8+12, index9));

                    if(majorCourse.equals(major)) {     // 주전공일 때
                        if(classification.equals("핵심전공"))   CM += classCredit;
                        else if(classification.equals("심화전공"))  IM += classCredit;
                    } else if(majorCourse.equals(minor)) {      // 복전.심전.부전공일 때
                        if(classification.equals("핵심전공"))   minorCM += classCredit;
                        else if(classification.equals("심화전공"))  minorIM += classCredit;
                    }

                }
                cm.setText(CM+"");
                im.setText(IM+"");
                graduatemajor.setText(CM+IM+"");

                // 복|부|심전 학점계산 - 프랑스, 일본어, 중국어 는 안됨;
                if(whatMinor.equals("부전공")) myScoreA.setText(minorCM+minorIM+"");
                else if(whatMinor.equals("복수전공") || whatMinor.equals("심화전공")) {
                    myScoreA.setText(minorCM+"");
                    myScoreB.setText(minorIM+"");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Graduate();
        ReadGraduate();

    }

    public void ReadGraduate() {
        //테이블 graduate를 읽음
        db = dh.getReadableDatabase();
        Cursor c = db.query("graduate", null, null, null, null, null, null);


        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String majors = snapshot.child("전공").getValue(String.class);
                myMajor.setText(majors);

                for (DataSnapshot dataSnapshot : snapshot.child("복|부|심전").getChildren()) {
                    a = dataSnapshot.getValue(String.class);
                    b = dataSnapshot.getKey();

                    while(c.moveToNext()) {
                        String majored = c.getString(c.getColumnIndex("전공"));
                        Integer minorss = c.getInt(c.getColumnIndex("부전공"));
                        Integer corecore = c.getInt(c.getColumnIndex("복수전공핵심"));
                        Integer intensescores = c.getInt(c.getColumnIndex("복수전공심화"));


                        //사용자의 부전/복수전공이 db내의 학과 값과 같을 때, 계열이 복수전공이면
                        if(majored.equals(a) ){
                            if(b.equals("복수전공")){
                                scoreA.setText(String.valueOf(corecore));
                                scoreB.setText(String.valueOf(intensescores));
                            }
                            else if(b.equals("부전공"))   {
                                scoreA.setText(String.valueOf(minorss));
                            }
                            break;
                        }
                    }
                    Dmajor.setText(a);
                }
                c.moveToFirst();
                while (c.moveToNext()) {
                    String major = c.getString(c.getColumnIndex("전공"));
                    Integer es1 = c.getInt(c.getColumnIndex("공통교양"));
                    Integer es2 = c.getInt(c.getColumnIndex("핵심교양"));
                    Integer es3 = c.getInt(c.getColumnIndex("진로소양"));
                    Integer es4 = c.getInt(c.getColumnIndex("교양합"));
                    Integer majorscore = c.getInt(c.getColumnIndex("핵심전공"));
                    Integer intensescore = c.getInt(c.getColumnIndex("심화전공"));
                    Integer totalmajor = c.getInt(c.getColumnIndex("전공합"));
                    Integer majormajor = c.getInt(c.getColumnIndex("심화전공핵심"));
                    Integer majorintense = c.getInt(c.getColumnIndex("심화전공심화"));
                    Integer addmajormajor = c.getInt(c.getColumnIndex("전공총합"));

                    // 교양 학점 계산
                    Double e1sum = 0.0;
                    Double e2sum = 0.0;
                    Double e3sum = 0.0;
                    Double e4sum = 0.0;
                    for (DataSnapshot dataSnapshot : snapshot.child("총 수강학점").getChildren()) {
                        if(dataSnapshot.getKey().contains("공통교양")) {
                            Double credit = dataSnapshot.getValue(Double.class);
                            e1sum += credit;
                        }else if(dataSnapshot.getKey().contains("핵심교양")) {
                            Double credit = dataSnapshot.getValue(Double.class);
                            e2sum += credit;
                        }else if(dataSnapshot.getKey().contains("진로소양")) {
                            Double credit = dataSnapshot.getValue(Double.class);
                            e3sum += credit;
                        }
                    }
                    e4sum = e1sum + e2sum + e3sum + e4sum;
                    if(es1 - e1sum <= 0) {
                        score11.setBackgroundColor(Color.parseColor("#E9DCFB"));
                        score11.setText("");
                    }else {
                        score11.setText(es1-e1sum+"");
                        score11.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        score11.setTextColor(Color.parseColor("#FF82B8"));
                    }
                    if(es2 - e2sum <= 0) {
                        score12.setBackgroundColor(Color.parseColor("#E9DCFB"));
                        score12.setText("");
                    }else {
                        score12.setText(es2-e2sum+"");
                        score12.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        score12.setTextColor(Color.parseColor("#FF82B8"));
                    }
                    if(es3 - e3sum <= 0) {
                        score13.setBackgroundColor(Color.parseColor("#E9DCFB"));
                        score13.setText("");
                    } else {
                        score13.setText(es3-e3sum+"");
                        score13.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        score13.setTextColor(Color.parseColor("#FF82B8"));
                    }
                    if(es4 - e4sum <= 0) {
                        score14.setBackgroundColor(Color.parseColor("#E9DCFB"));
                        score14.setText("");
                    }else {
                        score14.setText(es4-e4sum+"");
                        score14.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        score14.setTextColor(Color.parseColor("#FF82B8"));
                    }

                    int addmajors= majorscore + majormajor;
                    int addintense= intensescore + majorintense;

                    // major: 파일에서 불러오는 전공이름, majors: 내전공
                    if (major.equals(majors)) {
                        if(a.equals(majors)) {
                            score1.setText(String.valueOf(es1));
                            score2.setText(String.valueOf(es2));
                            score3.setText(String.valueOf(es3));
                            score4.setText(String.valueOf(es4));
                            core1.setText(String.valueOf(addmajors));
                            core2.setText(String.valueOf(addintense));
                            core3.setText(String.valueOf(addmajormajor));
                            // 심전 계산 가즈아
                            ArrayList<String> list = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String tempstr = dataSnapshot.getValue().toString();
                                list.add(tempstr);
                            }
                            String ArrayList [] = list.get(5).split("classSection=");
                            Double CM = 0.0;    Double IM = 0.0;    // 주전공의 핵전, 심전
                            for(int i=0; i<ArrayList.length; i++) {
                                // majorCourse 구하기
                                int index1 = ArrayList[i].indexOf("majorCourse=");
                                int index12 = ArrayList[i].indexOf("}");
                                if(index1<0) continue;
                                String majorCourse = ArrayList[i].substring(index1+12, index12);
                                // classification 구하기
                                int index2 = ArrayList[i].indexOf("classClassification=");
                                int index3 = ArrayList[i].indexOf(", className=");
                                String classification = ArrayList[i].substring(index2+20, index3);
                                // classCredit 구하기
                                int index8 = ArrayList[i].indexOf("classCredit=");
                                int index9 = ArrayList[i].indexOf(", classClassification=");
                                Double classCredit = Double.parseDouble(ArrayList[i].substring(index8+12, index9));

                                if(majorCourse.equals(majors)) {     // 주전공일 때
                                    if(classification.equals("핵심전공"))   CM += classCredit;
                                    else if(classification.equals("심화전공"))  IM += classCredit;
                                }
                            }
                            if(addmajors - CM <= 0) {
                                score22.setBackgroundColor(Color.parseColor("#E9DCFB"));
                                score22.setText("");
                            }else {
                                score22.setText(addmajors - CM +"");
                                score22.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                score22.setTextColor(Color.parseColor("#FF82B8"));
                            }
                            if(addintense - IM <= 0) {
                                score23.setBackgroundColor(Color.parseColor("#E9DCFB"));
                                score23.setText("");
                            }else {
                                score23.setText(addintense - IM +"");
                                score23.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                score23.setTextColor(Color.parseColor("#FF82B8"));
                            }
                            if((addmajors+addmajors) - (CM+IM) <= 0) {
                                score24.setBackgroundColor(Color.parseColor("#E9DCFB"));
                                score24.setText("");
                            }else {
                                score24.setText((addmajors+addintense) - (CM+IM) +"");
                                score24.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                score24.setTextColor(Color.parseColor("#FF82B8"));
                            }

                        }
                        else {
                            score1.setText(String.valueOf(es1));
                            score2.setText(String.valueOf(es2));
                            score3.setText(String.valueOf(es3));
                            score4.setText(String.valueOf(es4));
                            core1.setText(String.valueOf(majorscore));
                            core2.setText(String.valueOf(intensescore));
                            core3.setText(String.valueOf(totalmajor));

                            ArrayList<String> list = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String tempstr = dataSnapshot.getValue().toString();
                                list.add(tempstr);
                            }
                            String ArrayList [] = list.get(5).split("classSection=");
                            Double CM = 0.0;    Double IM = 0.0;    // 주전공의 핵전, 심전
                            for(int i=0; i<ArrayList.length; i++) {
                                // majorCourse 구하기
                                int index1 = ArrayList[i].indexOf("majorCourse=");
                                int index12 = ArrayList[i].indexOf("}");
                                if(index1<0) continue;
                                String majorCourse = ArrayList[i].substring(index1+12, index12);
                                // classification 구하기
                                int index2 = ArrayList[i].indexOf("classClassification=");
                                int index3 = ArrayList[i].indexOf(", className=");
                                String classification = ArrayList[i].substring(index2+20, index3);
                                // classCredit 구하기
                                int index8 = ArrayList[i].indexOf("classCredit=");
                                int index9 = ArrayList[i].indexOf(", classClassification=");
                                Double classCredit = Double.parseDouble(ArrayList[i].substring(index8+12, index9));

                                if(majorCourse.equals(majors)) {     // 주전공일 때
                                    if(classification.equals("핵심전공"))   CM += classCredit;
                                    else if(classification.equals("심화전공"))  IM += classCredit;
                                }
                            }

                            if(majorscore - CM <= 0) {
                                score22.setBackgroundColor(Color.parseColor("#E9DCFB"));
                                score22.setText("");
                            }else {
                                score22.setText(majorscore - CM +"");
                                score22.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                score22.setTextColor(Color.parseColor("#FF82B8"));
                            }
                            if(intensescore - IM <= 0) {
                                score23.setBackgroundColor(Color.parseColor("#E9DCFB"));
                                score23.setText("");
                            }else {
                                score23.setText(intensescore - IM +"");
                                score23.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                score23.setTextColor(Color.parseColor("#FF82B8"));
                            }
                            if(totalmajor - (CM+IM) <= 0) {
                                score24.setBackgroundColor(Color.parseColor("#E9DCFB"));
                                score24.setText("");
                            }else {
                                score24.setText(totalmajor - (CM+IM) +"");
                                score24.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                score24.setTextColor(Color.parseColor("#FF82B8"));
                            }
                        }
                        break;
                    }


                    // 주전공 학점 계산 - score22, 23, 24 (핵심, 심화, 총합)
                    ArrayList<String> list = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String tempstr = dataSnapshot.getValue().toString();
                        list.add(tempstr);
                    }
                    String ArrayList [] = list.get(5).split("classSection=");
                    Double CM = 0.0;    Double IM = 0.0;    // 주전공의 핵전, 심전
                    for(int i=0; i<ArrayList.length; i++) {
                        // majorCourse 구하기
                        int index1 = ArrayList[i].indexOf("majorCourse=");
                        int index12 = ArrayList[i].indexOf("}");
                        if(index1<0) continue;
                        String majorCourse = ArrayList[i].substring(index1+12, index12);
                        // classification 구하기
                        int index2 = ArrayList[i].indexOf("classClassification=");
                        int index3 = ArrayList[i].indexOf(", className=");
                        String classification = ArrayList[i].substring(index2+20, index3);
                        // classSection 구하기
                        //int index4 = ArrayList[i].indexOf("classSection=");
                        int index5 = ArrayList[i].indexOf(", classCredit=");
                        String classSection = ArrayList[i].substring(0, index5);
                        // className 구하기
                        int index6 = ArrayList[i].indexOf("className=");
                        int index7 = ArrayList[i].indexOf(", majorCourse=");
                        String className = ArrayList[i].substring(index6+10, index7);
                        // classCredit 구하기
                        int index8 = ArrayList[i].indexOf("classCredit=");
                        int index9 = ArrayList[i].indexOf(", classClassification=");
                        Double classCredit = Double.parseDouble(ArrayList[i].substring(index8+12, index9));

                        if(majorCourse.equals(majors)) {     // 주전공일 때
                            if(classification.equals("핵심전공"))   CM += classCredit;
                            else if(classification.equals("심화전공"))  IM += classCredit;
                        }
                    }

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    public void Graduate() {

        Workbook workbook = null;
        Sheet sheet = null;

        dh = new DatabaseHelper(Graduate.this, "graduate.db", null, 1);
        db = dh.getWritableDatabase();


//파일 불러와서 db에 저장하기
        try {
            InputStream is = getBaseContext().getResources().getAssets().open("Graduate.xls");
            workbook = Workbook.getWorkbook(is);

            if (workbook != null) {
                sheet = workbook.getSheet(0);

                if (sheet != null) {
                    int colTotal = sheet.getColumns();
                    int rowIndexStart = 1;
                    int rowTotal = sheet.getColumn(colTotal - 1).length;
                    StringBuilder sb;
                    for (int row = rowIndexStart; row < rowTotal; row++) {

                        for (int col = 0; col < colTotal; col++) {
                            String contents = sheet.getCell(col, row).getContents();

                            switch (col) {
                                case 0:
                                    values.put("전공", contents);
                                    break;
                                case 1:
                                    values.put("공통교양", contents);
                                    break;
                                case 2:
                                    values.put("핵심교양", contents);
                                    break;
                                case 3:
                                    values.put("진로소양", contents);
                                    break;
                                case 4:
                                    values.put("교양합", contents);
                                    break;
                                case 5:
                                    values.put("핵심전공", contents);
                                    break;
                                case 6:
                                    values.put("심화전공", contents);
                                    break;
                                case 7:
                                    values.put("전공합", contents);
                                    break;
                                case 8:
                                    values.put("심화전공핵심", contents);
                                    break;
                                case 9:
                                    values.put("심화전공심화", contents);
                                    break;
                                case 10:
                                    values.put("심화전공합", contents);
                                    break;
                                case 11:
                                    values.put("전공총합", contents);
                                    break;
                                case 12:
                                    values.put("부전공", contents);
                                    break;
                                case 13:
                                    values.put("복수전공핵심", contents);
                                    break;
                                case 14:
                                    values.put("복수전공심화", contents);
                                    break;
                                case 15:
                                    values.put("복수전공합", contents);
                                    break;
                                case 16:
                                    values.put("교직", contents);
                                    break;
                                case 17:
                                    values.put("총합", contents);
                                    break;
                            }
                        }
                        db.insert("graduate", null, values);
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}