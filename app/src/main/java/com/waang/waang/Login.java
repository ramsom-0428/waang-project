package com.waang.waang;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Login extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;  //파이어베이스 인증 처리
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    private EditText login_email;
    private EditText login_pw;
    Button button;
    TextView register;
    TextView findpw;

    ProgressDialog dialog;
    private long time = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        firebaseAuth = FirebaseAuth.getInstance();
        login_email = findViewById(R.id.login_email);
        login_pw = findViewById(R.id.login_pw);


        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mails = login_email.getText().toString();
                String pw = login_pw.getText().toString();

                // sharedPreferences로 아이디값 저장
                SharedPreferences sharedPreferences= getSharedPreferences("test", MODE_PRIVATE);    // test 이름의 기본모드 설정
                SharedPreferences.Editor editor= sharedPreferences.edit(); //sharedPreferences를 제어할 editor를 선언
                editor.putString("id", mails);
                editor.commit();


                if(TextUtils.isEmpty(mails) || TextUtils.isEmpty(pw)) {
                    if(TextUtils.isEmpty(mails)) {
                        Toast.makeText(Login.this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }else if(TextUtils.isEmpty(pw)) {
                        Toast.makeText(Login.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }else if(TextUtils.isEmpty(mails)&&TextUtils.isEmpty(pw)) {
                        Toast.makeText(Login.this, "이메일과 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                //로그인 요청
                firebaseAuth.signInWithEmailAndPassword(mails, pw).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(Login.this, "이메일과 비밀번호를 다시 한번 확인해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        register = (TextView) findViewById(R.id.textRegister);
        register.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        findpw = (TextView) findViewById(R.id.textfindpw);
        findpw.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(Login.this, PasswordFind.class);
                startActivity(intent);
            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // 이미 로그인 되어 있을 때
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }
}