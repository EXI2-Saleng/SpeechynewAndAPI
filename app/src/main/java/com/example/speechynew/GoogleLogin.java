package com.example.speechynew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.speechynew.connectDB.User;
import com.example.speechynew.databinding.ActivityGoogleLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.speechynew.databinding.ActivityGoogleLoginBinding;

public class GoogleLogin extends AppCompatActivity {


    private ActivityGoogleLoginBinding binding;
    User user;
    int maxid = 0;
    FirebaseDatabase database;
    DatabaseReference ref;
    DatabaseReference ref_user;

    private  static  final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;

    private FirebaseAuth firebaseAuth;

    private static final String TAG = "GOOGLE_SIGN_IN_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoogleLoginBinding.inflate(getLayoutInflater());
        Button buttonskip = findViewById(R.id.skiplogin);

        setContentView(binding.getRoot());

        binding.skiplogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(GoogleLogin.this,MainActivity.class);
                //startActivity(i);
                startActivity(new Intent(GoogleLogin.this, MainActivity.class));
                finish();
            }
        });

        /*
        user=new User();
        ref = database.getInstance().getReference().child("user");


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    maxid = (int)snapshot.getChildrenCount();
                }
                else {}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

         */

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();


        binding.googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: begin Google SignIn");
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent,RC_SIGN_IN);

            }
        });

    }

    private void checkUser() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            Log.d(TAG, "checkUser: Already logged in");
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Log.d(TAG, "onActivityResult: Google SignIn intent result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);
            }
            catch (Exception e){
                Log.d(TAG, "onActivityResult: "+e.getMessage());
                Log.d(TAG, "onActivityResult: "+"FAIL");
                Log.d(TAG, "requestcode"+requestCode);
            }
        }
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin firebase auth with google account");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        Log.d(TAG, "onSuccess: Logged In");

                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        String uid = firebaseUser.getUid();
                        final String email = firebaseUser.getEmail();
                        String name = firebaseUser.getDisplayName();


                       /* String x=ref.child(String.valueOf(maxid)).getRoot().toString();
                        Log.d(TAG,"xDAB: "+x);
                        user.setUID(uid);
                        user.setMail(email);
                        user.setName(name);

                        ref_user = database.getInstance().getReference().child("user").child(name);
                        ref_user.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.child("mail").getValue().equals("testtfa2020@gmail.com")){
                                        Log.d(TAG,"OK");
                                    }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });*/

                        //ref.child(name).setValue(user);
                        //ref.child(String.valueOf(maxid + 1)).setValue(user);


                        Log.d(TAG, "onSuccess: Email: "+email);
                        Log.d(TAG, "onSuccess: UID: "+uid);

                        if(authResult.getAdditionalUserInfo().isNewUser()){
                            Log.d(TAG, "onSuccess: Account Created...\n"+email);
                            Toast.makeText(GoogleLogin.this, "Account Created...\n"+email, Toast.LENGTH_SHORT).show();
                        }
                        else{

                            Log.d(TAG, "onSuccess: Existing user...\n"+email);
                            Toast.makeText(GoogleLogin.this, "Existing user...\n"+email, Toast.LENGTH_SHORT).show();
                        }

                        startActivity(new Intent(GoogleLogin.this, MainActivity.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "onFailure: Loggin failed "+e.getMessage());
                    }
                });
    }
}