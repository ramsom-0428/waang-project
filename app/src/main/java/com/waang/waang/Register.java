package com.waang.waang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    //기능 구현 이후 삭제 필요
    private EditText register_email;
    private EditText register_pw;
    private EditText login_name;
    private TextInputLayout pw1;
    Button register;
    private Spinner major;
    private Spinner minor;
    private Spinner choice;
    private FirebaseAuth firebaseAuth;  //파이어베이스 인증 처리
    private DatabaseReference DatabaseRef; // 실시간 데이터베이스


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseRef = FirebaseDatabase.getInstance().getReference("waang");

        register_email= findViewById(R.id.register_email);
        register_pw = findViewById(R.id.register_pw);
        login_name = findViewById(R.id.login_name);
        register = (Button) findViewById(R.id.registerbtn);
        pw1 = findViewById(R.id.PW1);
        major = (Spinner) findViewById(R.id.majorspinner);
        minor = (Spinner) findViewById(R.id.minorspinner);
        choice = (Spinner) findViewById(R.id.choicepinner);


        major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> majorparent, View view, int majorp, long mjid) {

                String majors = majorparent.getItemAtPosition(majorp).toString();

                choice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> choiceparent, View view, int choicep, long choiceid) {
                        String choices = choiceparent.getItemAtPosition(choicep).toString();

                        minor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override public void onItemSelected(AdapterView<?> minorparent, View view, int minorp, long minorid) {

                                String minors = minorparent.getItemAtPosition(minorp).toString();

                                register.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {

                                        String mail = register_email.getText().toString();
                                        int index = mail.indexOf("@");
                                        String emails = mail.substring(0, index);
                                        String pw = register_pw.getText().toString();
                                        String name = login_name.getText().toString();

                                        if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(pw)) {
                                            if(TextUtils.isEmpty(mail)) {
                                                Toast.makeText(Register.this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                                                return;
                                            } else if (TextUtils.isEmpty(pw)){
                                                Toast.makeText(Register.this, "비밀번호를 입력해주세요!", Toast.LENGTH_SHORT).show();
                                                return;
                                            } else {
                                                Toast.makeText(Register.this, "이메일과 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }

                                        if (pw.length() < 6)
                                            pw1.setError("비밀번호를 6자리 이상 입력해주세요!");
                                        else
                                            pw1.setError(null);



                                        if(choices.equals("복수전공") && majors.equals(minors)) {
                                            Toast.makeText(Register.this, "다시 선택해주세요", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        if(choices.equals("부전공") && majors.equals(minors)) {
                                            Toast.makeText(Register.this, "다시 선택해주세요", Toast.LENGTH_SHORT).show();
                                            return;
                                        }


                                        firebaseAuth.createUserWithEmailAndPassword(mail, pw).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                //가입이 성공했을 때
                                                if (task.isSuccessful()) {
                                                    //현재의 유저를 가지고 옴
                                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                                    UserAccount account = new UserAccount();
                                                    account.setIdToken(firebaseUser.getUid());
                                                    account.setEmail(firebaseUser.getEmail());
                                                    account.setPwinfo(pw);
                                                    account.setNames(name);

                                                    // database에 insert
                                                    DatabaseRef.child("UserAccount").child(emails).setValue(account);



                                                    DatabaseRef.child("UserAccount").child(emails).child("전공").setValue(majors);


                                                    if(choices.equals("복수전공")) {
                                                        DatabaseRef.child("UserAccount").child(emails).child("복|부|심전").child("복수전공").setValue(minors);

                                                    }
                                                    else if(choices.equals("부전공")) {
                                                        DatabaseRef.child("UserAccount").child(emails).child("복|부|심전").child("부전공").setValue(minors);
                                                    }
                                                    else {
                                                        if(choices.equals("심화전공")) {
                                                            DatabaseRef.child("UserAccount").child(emails).child("복|부|심전").child("심화전공").setValue(minors);

                                                        }
                                                    }

                                                    Toast.makeText(Register.this, "환영합니다 " + name + " 수정님!", Toast.LENGTH_SHORT).show();
                                                    // 총 수강학점 초기화
                                                    String classifiList [] = {"공통교양경험적수리적추리", "공통교양SW문해", "공통교양영역없음", "공통교양영어", "공통교양제2외국어",
                                                            "일반교양", "진로소양도전과실천", "진로소양자유선택", "교직영역없음",
                                                            "핵심교양자연의설명", "핵심교양인식과가치", "핵심교양문학과예술", "핵심교양역사의해석", "핵심교양사회의이해", "핵심교양공학과기술",
                                                    };
                                                    for(int i=0; i<classifiList.length; i++)
                                                        DatabaseRef.child("UserAccount").child(emails).child("총 수강학점").child(classifiList[i]).setValue(0.0);

                                                    Intent intent = new Intent(getApplicationContext(), ConsentRegister.class);
                                                    startActivity(intent);

                                                } else {

                                                }
                                            }
                                        });
                                    }
                                });
                            }
                            @Override

                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}

