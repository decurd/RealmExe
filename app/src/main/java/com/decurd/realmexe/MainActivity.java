package com.decurd.realmexe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements RealmChangeListener<Realm> {

    private EditText mEmail;
    private EditText mPassword;
    private EditText mNewPassword;
    private TextView mResultText;

    private Realm mRealm;
    private RealmResults<User> mResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = (EditText) findViewById(R.id.email_edit);
        mPassword = (EditText) findViewById(R.id.password_edit);
        mNewPassword = (EditText) findViewById(R.id.new_password_edit);
        mResultText = (TextView) findViewById(R.id.result_text);

        mRealm = Realm.getDefaultInstance();
        mRealm.addChangeListener(this); // 디비가 갱신 될때마다 호출

    }

    public void SignIn(View view) {
        if (mRealm.where(User.class)
                .equalTo("email", mEmail.getText().toString())
                .equalTo("password", mPassword.getText().toString())
                .count() > 0) {
            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show();
        }
    }

    public void SignUp(View view) {
        mRealm.executeTransaction(new Realm.Transaction() { // Insert or Update or Delete 시는 트랜잭션을 걸어주어야 한다
            @Override
            public void execute(Realm realm) {
                if (realm.where(User.class)
                        .equalTo("email", mEmail.getText().toString())
                        .count() == 0) {
                    User user = realm.createObject(User.class);
                    user.setEmail(mEmail.getText().toString());
                    user.setPassword(mPassword.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "중복된 이메일이 있습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updatePassword(View view) {
        if (mRealm.where(User.class)
                .equalTo("email", mEmail.getText().toString())
                .equalTo("password", mPassword.getText().toString())
                .count() > 0) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    User user = realm.where(User.class)
                            .equalTo("email", mEmail.getText().toString())
                            .findFirst();

                    user.setPassword(mNewPassword.getText().toString());

                    Toast.makeText(MainActivity.this, "비밀번호 수정완료", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(MainActivity.this, "이메일, 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAccount(View view) {
        if (mRealm.where(User.class)
                .equalTo("email", mEmail.getText().toString())
                .equalTo("password", mPassword.getText().toString())
                .count() > 0) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(User.class)
                            .equalTo("email", mEmail.getText().toString())
                            .findAll()
                            .deleteAllFromRealm();

                    Toast.makeText(MainActivity.this, "삭제완료", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(MainActivity.this, "이메일, 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void showResult() {
        mResults = mRealm.where(User.class).findAll();
        mResultText.setText(mResults.toString());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.removeAllChangeListeners(); // 액티비티를 닫기 전에 Realm에 추가된 모든 리스너를 제거해 준다
        mRealm.close();
    }

    @Override
    public void onChange(Realm element) {
        // 디비가 갱신될 때마다 호출
        showResult();
    }
}
