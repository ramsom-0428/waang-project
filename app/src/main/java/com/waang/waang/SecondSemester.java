package com.waang.waang;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;

public class SecondSemester extends AppCompatActivity {

    SQLiteDatabase db;
    DatabaseHelper dh;

    ArrayList<SecondSemesterData> secondSemesterDataList;
    SecondAdapter secondAdapter;

    TextView yearAndSemester2;

    private ArrayList<SecondSemesterData> arraylist;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        String year = intent.getBundleExtra("myBundle").getString("year");
        String semester = intent.getBundleExtra("myBundle").getString("semester");
        System.out.println("year and semester : " + year + " , " + semester);

        yearAndSemester2 = (TextView)findViewById(R.id.yearAndSemester2);
        yearAndSemester2.setText(year + "년도 " + semester + "학기 개설강좌");



        SecondSemesters();
        this.InitializeClassData();

        arraylist = new ArrayList<SecondSemesterData>();
        arraylist.addAll(secondSemesterDataList);

        System.out.println(arraylist.size());

        ListView listView = (ListView)findViewById(R.id.secondSemesterList);
        secondAdapter = new SecondAdapter(this, secondSemesterDataList);

        listView.setAdapter(secondAdapter);

        EditText secondSearch = (EditText) findViewById(R.id.secondSearch);
        secondSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String text = secondSearch.getText().toString();
                search(text);
            }
        });
    }

    public void search(String charText){

        secondSemesterDataList.clear();

        if(charText.length() == 0){
            secondSemesterDataList.addAll(arraylist);
        }
        else{
            for(int i = 0; i < arraylist.size(); i++){
                if(arraylist.get(i).getClassName().toLowerCase().contains(charText)){
                    secondSemesterDataList.add(arraylist.get(i));
                }
            }
        }
        secondAdapter.notifyDataSetChanged();
    }

    public void SecondSemesters() {

        ContentValues values = new ContentValues();
        Workbook workbook = null;
        Sheet sheet = null;

        //db파일 생성
        dh = new DatabaseHelper(SecondSemester.this, "secondsemester.db", null, 1);
        db = dh.getWritableDatabase();

//파일 불러와서 db에 저장하기
        try {
            InputStream is = getBaseContext().getResources().getAssets().open("secondsem.xls");
            workbook = Workbook.getWorkbook(is);

            if (workbook != null) {
                sheet = workbook.getSheet(0);

                if (sheet != null) {

                    int colTotal = sheet.getColumns();
                    int rowIndexStart = 1;
                    int rowTotal = sheet.getColumn(colTotal - 1).length;

                    for (int row = rowIndexStart; row < rowTotal; row++) {

                        for (int col = 0; col < colTotal; col++) {
                            String contents = sheet.getCell(col, row).getContents();

                            switch (col) {
                                case 0:
                                    values.put("순번", contents);
                                    break;
                                case 1:
                                    values.put("개설학과전공", contents);
                                    break;
                                case 2:
                                    values.put("학수번호", contents);
                                    break;
                                case 3:
                                    values.put("교과목명", contents);
                                    break;
                                case 4:
                                    values.put("분반", contents);
                                    break;
                                case 5:
                                    values.put("이수구분", contents);
                                    break;
                                case 6 :
                                    values.put("영역",contents);
                                    break;
                                case 7 :
                                    values.put("학점",contents);
                                    break;
                                case 8 :
                                    values.put("수강정원",contents);
                                    break;
                                case 9 :
                                    values.put("시간표",contents);
                                    break;
                                case 10 :
                                    values.put("강의실",contents);
                                    break;
                                case 11 :
                                    values.put("담당교수",contents);
                                    break;
                                case 12 :
                                    values.put("캠퍼스",contents);
                                    break;
                                case 13 :
                                    values.put("수업유형",contents);
                                    break;
                                case 14 :
                                    values.put("평가등급유형",contents);
                                    break;
                                case 15 :
                                    values.put("수강안내사항",contents);
                                    break;
                                case 16 :
                                    values.put("대상자지정내용",contents);
                                    break;
                            }
                        }
                        //db의 테이블들에 각각 values 값 넣어줌
                        db.insert("secondsemester", null, values);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void InitializeClassData(){
        secondSemesterDataList = new ArrayList<SecondSemesterData>();

        db = dh.getReadableDatabase();
        Cursor c = db.query("secondsemester", null, null, null, null, null, null);

        secondSemesterDataList.clear();
        while(c.moveToNext()) {

            String coursemajor = c.getString(c.getColumnIndex("개설학과전공"));
            String subjectnames = c.getString(c.getColumnIndex("교과목명"));
            String compare = c.getString(c.getColumnIndex("이수구분"));
            String section = c.getString(c.getColumnIndex("영역"));
            Double scores = c.getDouble(c.getColumnIndex("학점"));

            secondSemesterDataList.add(new SecondSemesterData(coursemajor, subjectnames, compare,section, scores));
            if(secondSemesterDataList.size() >= 2473) break;
        }
    }
}


class SecondSemesterData{
    private String majorCourse;
    private String className;
    private String classClassification;
    private String classSection;
    private Double classCredit;

    public SecondSemesterData(){

    }

    public SecondSemesterData(String majorCourse, String className, String classClassification, String classSection, Double classCredit){
        this.majorCourse = majorCourse;
        this.className = className;
        this.classClassification = classClassification;
        this.classSection = classSection;
        this.classCredit = classCredit;
    }

    public String getMajorCourse() {return this.majorCourse;}
    public String getClassName(){
        return this.className;
    }
    public String getClassClassification(){
        return this.classClassification;
    }
    public String getClassSection() { return this.classSection; }
    public Double getClassCredit() {
        return this.classCredit;
    }
}



class SecondAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    private ArrayList<SecondSemesterData> data;
    private TextView secondMajorTextView;
    private TextView classSemesterNameTextView;
    private TextView classSemesterClassificationTextView;
    private TextView classSemesterSectionTextView;
    private TextView classSemesterCreditTextView;
    private Button addClass;

    // 이수구분+영역 array
    String classifiList [] = {"공통교양경험적수리적추리", "공통교양SW문해", "공통교양영역없음", "공통교양영어", "공통교양제2외국어",
            "일반교양", "진로소양도전과실천", "진로소양자유선택", "교직영역없음",
            "핵심교양자연의설명", "핵심교양인식과가치", "핵심교양문학과예술", "핵심교양역사의해석", "핵심교양사회의이해", "핵심교양공학과기술",
    };

    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseAuth firebaseAuth;
    String id;

    public SecondAdapter(Context context, ArrayList<SecondSemesterData> dataArray){
        mContext = context;
        data = dataArray;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void addItem(SecondSemesterData item){
        data.add(item);
    }

    @Override
    public int getCount(){
        return data.size();
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public SecondSemesterData getItem(int position){
        return data.get(position);
    }


    public View getView(int position, View converView, ViewGroup parent){
        View view = mLayoutInflater.inflate(R.layout.second_listview_custom, null);

        //id 가져오기
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String numberinfo = user.getEmail();
        int idIndex = numberinfo.indexOf("@");
        id = numberinfo.substring(0, idIndex);
        // 파이어베이스 연동
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("waang/UserAccount/" + id);

        addClass = (Button)view.findViewById(R.id.addSecondClass);
        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 수강 과목 추가 구현 코드 추가
                String majorCourse = data.get(position).getMajorCourse();
                String className = data.get(position).getClassName();
                String classClassification = data.get(position).getClassClassification();
                String classSection = data.get(position).getClassSection();
                Double classCredit = data.get(position).getClassCredit();
                // 파베에 넣기 _ 예시) id - 수강한과목 - 과이름 - 핵심교양1영역 - [수업이름] - [강좌이름/학점/이수구분/영역]
                mReference.child("수강한 과목").child(majorCourse).child(classClassification + classSection).child(className).setValue(data.get(position));
                mReference = mDatabase.getReference("waang/UserAccount/" + id);
                String str = classClassification + classSection;
                for(int i=0; i<classifiList.length; i++) {
                    if(str.equals(classifiList[i])) {
                        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Double temp = dataSnapshot.child("총 수강학점").child(str).getValue(Double.class);
                                Double credit = temp + classCredit;
                                mReference.child("총 수강학점").child(str).setValue(credit);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
        });

        secondMajorTextView = (TextView) view.findViewById(R.id.SecondSemesterMajorName);
        secondMajorTextView.setText(data.get(position).getMajorCourse());

        classSemesterNameTextView = (TextView)view.findViewById(R.id.SecondclassSemesterNameCus);
        classSemesterNameTextView.setText(data.get(position).getClassName());
        classSemesterClassificationTextView = (TextView)view.findViewById(R.id.SecondclassSemesterClassification);
        classSemesterClassificationTextView.setText(data.get(position).getClassClassification());
        classSemesterSectionTextView = (TextView)view.findViewById(R.id.SecondclassSection);
        classSemesterSectionTextView.setText(data.get(position).getClassSection());
        classSemesterCreditTextView = (TextView)view.findViewById(R.id.SecondclassSemesterCreditCus);
        classSemesterCreditTextView.setText(data.get(position).getClassCredit()+"");

        return view;
    }
}