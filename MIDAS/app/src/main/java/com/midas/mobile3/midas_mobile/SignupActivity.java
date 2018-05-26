package com.midas.mobile3.midas_mobile;

import android.support.annotation.Nullable;
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


public class SignupActivity extends AppCompatActivity {

    private EditText text_id, text_pw, text_name, text_email;
    Button btn_singup;
    TextView check_id_overlap;
    boolean isOverlap;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initView();
        isOverlap = false;
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        btn_singup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Member member = new Member(text_id.getText().toString(), text_pw.getText().toString(),
                        text_name.getText().toString(), text_email.getText().toString());
                mReference.child("members").push().setValue(member);
                Toast.makeText(getApplicationContext(), "회원가입 되었습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void initView(){
        text_id = (EditText)findViewById(R.id.editText_id);
        text_pw = (EditText)findViewById(R.id.editText_pw);
        text_name = (EditText)findViewById(R.id.editText_name);
        text_email = (EditText)findViewById(R.id.editText_email);
        btn_singup = (Button)findViewById(R.id.button4);
        check_id_overlap = (TextView)findViewById(R.id.textView2);
    }

    // DB에서 아이디 중복여부 확인 후 isOverlap 값 설정
    private void checkIDOverlap(){
        mReference.child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Member member = snapshot.getValue(Member.class);

                    // db 데이터 중 동일한 아이디가 있다면
                    if(member.getId().toString().equals(text_name.getText().toString()))
                        isOverlap = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}