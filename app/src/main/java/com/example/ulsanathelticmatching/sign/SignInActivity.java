package com.example.ulsanathelticmatching.sign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ulsanathelticmatching.main.MainActivity;
import com.example.ulsanathelticmatching.R;
import com.example.ulsanathelticmatching.model.UserModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.usermgmt.response.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final int RC_SIGN_IN = 10;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    LoginButton loginButton;
    CallbackManager callbackManager;
    final boolean checked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();

        //////////////////////페북 로그인/////////////////////////
        if(user == null){
            setContentView(R.layout.activity_sign_in);
            FacebookSdk.sdkInitialize(getApplicationContext());
            loginButton = (LoginButton)findViewById(R.id.SignInActivity_FB_login_button);

            callbackManager = CallbackManager.Factory.create();
            loginButton.setReadPermissions(Arrays.asList("email"));
        }else{
            Intent myintent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(myintent);
        }

        ///////////////////////////////////////////////////////////////////////////////////////
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton button = (SignInButton)findViewById(R.id.SignInActivity_Goolg_login_button); //구글 버튼 클릭 부분
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //생명주기 onStart로 로그인 세션이 남아 있는지 확인을 한다.
        //로그인이 되어 있으면 getCurrentUser();가 null이 아니다.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Log.d("애러 내용", "로그인 안되어 있음 세션 없음");
        }else{
            finish();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }

        private boolean checkNewRegister(){
        //신규인지 확인하는 함수
        // Firebase에서 인증과 DB를 따로 저장하기 때문에
        //DB에서 users테이블을 꺼내 식별자 uid가 같은게 있는지 확인을 한다
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final boolean[] checked1 = {false};//같은게 없으면 false를 리턴
        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel.uid.equals(myUid)) { //DB에 uid가 같으면
                        checked1[0] = true; //true리턴
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return checked1[0];
    }

    public void makeUsersTable(){
        //DB에 회원정보를 저장하는 함수
        UserModel userModel = new UserModel(); //UserModel객체에 저장을 한다
        //mAuth.getCurrentUser()에는 인증을 한 사이트의 회원 정보가 담겨 있다
        String uid = mAuth.getCurrentUser().getUid();
        userModel.uid = uid; //인증 식별자 uid
        userModel.profileImageUrl = String.valueOf(mAuth.getCurrentUser().getPhotoUrl()); //프로필사진
        userModel.userName = mAuth.getCurrentUser().getDisplayName();//사용자 이름

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {//DB에 저장을 하고 성공을 하였을 경우 메인화면으로 간다.
                Intent i = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void buttonclickLoginFb(View v){
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(SignInActivity.this, "페북 로그인 취소 되었습니다", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SignInActivity.this, "페북 로그인 에러 되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(mAuth.getCurrentUser().getDisplayName()).build();
                            task.getResult().getUser().updateProfile(userProfileChangeRequest);
                            if(checkNewRegister()){//기존에 회원인지 확인을 합니다 회원일 경우 true
                                Intent i = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }else{//기존 회원이 아니면 false
                                makeUsersTable();//DB에 회원정보를 저장을 하는 함수 호출
                            }
                        }else{
                            Toast.makeText(SignInActivity.this, "파이어베이스에 등록 되지 않습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    ////////////////////구글 가입 부분 ///////////////////////////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                //구글 사용자가 맞으면 일로 간다
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(mAuth.getCurrentUser().getDisplayName()).build();
                            task.getResult().getUser().updateProfile(userProfileChangeRequest);
                            if(checkNewRegister()){//기존에 회원인지 확인을 합니다 회원일 경우 true
                                    Intent i = new Intent(SignInActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                            }else{//기존 회원이 아니면 false
                                makeUsersTable();//DB에 회원정보를 저장을 하는 함수 호출
                            }
                        } else {
                            Toast.makeText(SignInActivity.this, "구글 로그인 실패 되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}