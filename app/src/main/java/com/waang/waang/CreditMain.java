package com.waang.waang;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class CreditMain extends AppCompatActivity {

    ArrayList<ClassData> classDataList;
    ClassAdapter classAdapter;
    Button addBtn;
    String id;
    private SwipeRefreshLayout refresh;

    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseAuth firebaseAuth;
    private ChildEventListener mChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //id 가져오기
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String numberinfo = user.getEmail();
        int idIndex = numberinfo.indexOf("@");
        id = numberinfo.substring(0, idIndex);

        // 파이어베이스 연동
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("waang/UserAccount/" + id);

        this.InitializeClassData();

        ListView listView = (ListView)findViewById(R.id.classListView);
        classAdapter = new ClassAdapter(this, classDataList);
        listView.setAdapter(classAdapter);

        addBtn = (Button)findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreditSearchAndAdd.class);
                startActivity(intent);
            }
        });

        refresh = (SwipeRefreshLayout)findViewById(R.id.creditSwiper);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //id 가져오기
                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String numberinfo = user.getEmail();
                int idIndex = numberinfo.indexOf("@");
                id = numberinfo.substring(0, idIndex);

                // 파이어베이스 연동
                mDatabase = FirebaseDatabase.getInstance();
                mReference = mDatabase.getReference("waang/UserAccount/" + id);

                classDataList.clear();

                mReference = mDatabase.getReference("waang/UserAccount/" + id);
                mReference.child("수강한 과목").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String temp = snapshot.getValue().toString();
                            System.out.println(temp);
                            String ArrayList [] = temp.split("classSection=");
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

                                ClassData classdata = snapshot.child(classification+classSection).child(className).getValue(ClassData.class);
                                classDataList.add(classdata);
                                classAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                refresh.setRefreshing(false);
            }
        });


    }

    public void InitializeClassData(){
        classDataList = new ArrayList<ClassData>();
        //수강 과목 불러오기 코드 추가
        mReference = mDatabase.getReference("waang/UserAccount/" + id);
        mReference.child("수강한 과목").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String temp = snapshot.getValue().toString();
                    System.out.println(temp);
                    String ArrayList [] = temp.split("classSection=");
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

                        ClassData classdata = snapshot.child(classification+classSection).child(className).getValue(ClassData.class);
                        classDataList.add(classdata);
                        classAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

class ClassData {
    private String majorCourse;
    private String className;
    private String classClassification;
    private String classSection;
    private Double classCredit;

    public ClassData(){ }

    public ClassData(String majorCourse, String className, String classClassification, String classSection, Double classCredit){
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

class ClassAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    private ArrayList<ClassData> data;

    private TextView majorCourseTextView;
    private TextView classNameTextView;
    private TextView classClassificationTextView;
    private TextView classSemesterSectionTextView;
    private TextView classCreditTextView;
    private Button deleteClass;
    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseAuth firebaseAuth;
    String id;

    // 이수구분+영역 array
    String classifiList [] = {"공통교양경험적수리적추리", "공통교양SW문해", "공통교양영역없음", "공통교양영어", "공통교양제2외국어",
            "일반교양", "진로소양도전과실천", "진로소양자유선택", "교직영역없음",
            "핵심교양자연의설명", "핵심교양인식과가치", "핵심교양문학과예술", "핵심교양역사의해석", "핵심교양사회의이해", "핵심교양공학과기술",
             };

    public ClassAdapter(Context context, ArrayList<ClassData> dataArray){
        mContext = context;
        data = dataArray;
        mLayoutInflater = LayoutInflater.from(mContext);
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
    public ClassData getItem(int position){
        return data.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent){
        View view = mLayoutInflater.inflate(R.layout.credit_listview_custom, null);

        //id 가져오기
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String numberinfo = user.getEmail();
        int idIndex = numberinfo.indexOf("@");
        id = numberinfo.substring(0, idIndex);
        // 파이어베이스 연동
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("waang/UserAccount/" + id);

        deleteClass = (Button)view.findViewById(R.id.deleteClass);
        deleteClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String majorCourse = data.get(position).getMajorCourse();
                String className = data.get(position).getClassName();
                String classClassification = data.get(position).getClassClassification();
                String classSection = data.get(position).getClassSection();
                Double classCredit = data.get(position).getClassCredit();
                // 수강 과목 삭제 구현 코드 추가
                mReference = mDatabase.getReference("waang/UserAccount/" + id);
                String str = classClassification + classSection;
                for(int i=0; i<classifiList.length; i++) {
                    if(str.equals(classifiList[i])) {
                        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Double temp = dataSnapshot.child("총 수강학점").child(str).getValue(Double.class);
                                Double credit = temp - classCredit;
                                mReference = mDatabase.getReference("waang/UserAccount/" + id);
                                mReference.child("총 수강학점").child(str).setValue(credit);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                mReference = mDatabase.getReference("waang/UserAccount/" + id + "/수강한 과목/" + majorCourse);
                String classifiList2 [] = {"공통교양경험적수리적추리", "공통교양SW문해", "공통교양영역없음", "공통교양영어", "공통교양제2외국어",
                        "일반교양", "진로소양도전과실천", "진로소양자유선택", "교직영역없음",
                        "핵심교양자연의설명", "핵심교양인식과가치", "핵심교양문학과예술", "핵심교양역사의해석", "핵심교양사회의이해", "핵심교양공학과기술",
                        "핵심전공1영역", "핵심전공2영역", "핵심전공3영역", "핵심전공4영역",
                        "심화전공1영역", "심화전공2영역", "심화전공3영역", "심화전공4영역"
                };
                for(int i=0; i<classifiList2.length; i++) {
                    if(str.equals(classifiList2[i]))
                        mReference.child(str).child(className).removeValue();
                }
                data.remove(position);
                notifyDataSetChanged();
            }
        });
        majorCourseTextView = (TextView) view.findViewById(R.id.majorCourse);
        majorCourseTextView.setText(data.get(position).getMajorCourse());
        classNameTextView = (TextView)view.findViewById(R.id.classNameCus);
        classNameTextView.setText(data.get(position).getClassName());
        classClassificationTextView = (TextView)view.findViewById(R.id.classClassification);
        classClassificationTextView.setText(data.get(position).getClassClassification());
        classSemesterSectionTextView = (TextView)view.findViewById(R.id.classSection);
        classSemesterSectionTextView.setText(data.get(position).getClassSection());
        classCreditTextView = (TextView)view.findViewById(R.id.classCreditCus);
        classCreditTextView.setText(Double.toString(data.get(position).getClassCredit())+"");

        return view;
    }
}