package com.waang.waang;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PasswordFind extends AppCompatActivity {


    private EditText input_email;
    private FirebaseAuth firebaseAuth;  //파이어베이스 인증 처리
    private DatabaseReference DatabaseRef; // 실시간 데이터베이스

    private Button resetbtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseRef = FirebaseDatabase.getInstance().getReference("waang");

        input_email = findViewById(R.id.input_email);
        resetbtn = findViewById(R.id.resetbtn);

        resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(input_email.getText())) {
                    Toast.makeText(PasswordFind.this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                String remail = input_email.getText().toString();

                firebaseAuth.sendPasswordResetEmail(remail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PasswordFind.this,  remail + "로 메일을 전송했습니다. \n 메일함을 확인해주세요! ", Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(PasswordFind.this, "존재하지 않는 이메일입니다. \n 다시 한번 확인해주세요", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
}
