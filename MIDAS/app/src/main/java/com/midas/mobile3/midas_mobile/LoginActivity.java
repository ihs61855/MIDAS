package com.midas.mobile3.midas_mobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midas.mobile3.midas_mobile.Model.Member;
import com.midas.mobile3.midas_mobile.Model.MemberToken;

public class LoginActivity extends AppCompatActivity {
    TextView text_signup, text_logout, textView;
    EditText login_id, login_pw;
    Button btn_login;
    String input_id, input_pw;

    static MemberToken token;
    final int LOGINPAGE_CODE = 100;

    DatabaseReference mReference;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = (Button) findViewById(R.id.login_btn);
        text_signup = (TextView) findViewById(R.id.signup);
        text_logout = (TextView) findViewById(R.id.logout);
        login_id = (EditText) findViewById(R.id.login_id);
        login_pw = (EditText) findViewById(R.id.login_pw);
        input_id = input_pw = "";
        textView = (TextView)findViewById(R.id.login_text);

        mReference = FirebaseDatabase.getInstance().getReference();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_id = login_id.getText().toString();
                input_pw = login_pw.getText().toString();
                isValidData();
            }
        });
        text_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
        text_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                token.removeToken();
                btn_login.setVisibility(View.VISIBLE);
                text_signup.setVisibility(View.VISIBLE);
                text_logout.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
                login_id.setVisibility(View.VISIBLE);
                login_pw.setVisibility(View.VISIBLE);
            }
        });
    }

    // 로그인 아이디 비밀번호 확인
    private void isValidData() {

        mReference.child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isValid = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Member member = snapshot.getValue(Member.class);

                    // db 데이터 중 로그인 아이디, 비밀번호가 모두 동일한 정보가 있는지 확인
                    if (member.getId().toString().equals(input_id)) {
                        if (member.getPw().toString().equals(input_pw)) {
                            isValid = true;
                            // 로그인 정보가 일치하는지 확인하고 화면에 띄워줌
                            checkLogin(isValid, member);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkLogin(boolean is_valid, Member member) {
        if (is_valid) {
            Member user = new Member(member.getId(), member.getPw(), member.getName(), member.getEmail());
            token.setToken(user);
            if (token.hasLoginToken()) {
                btn_login.setVisibility(View.INVISIBLE);
                text_signup.setVisibility(View.INVISIBLE);
                text_logout.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_LONG).show();
                textView.setText(token.getToken().getName() + "님 반갑습니다.");
                login_id.setVisibility(View.INVISIBLE);
                login_pw.setVisibility(View.INVISIBLE);
            }
        } else {
            Toast.makeText(getApplicationContext(), "정보가 일치하지 않습니다. 다시 로그인 해주세요.", Toast.LENGTH_LONG).show();
        }
        login_id.setText("");
        login_pw.setText("");
    }

}
