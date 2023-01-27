package com.example.quicknotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccount extends AppCompatActivity {
    EditText emailEditText,passwordEditText,confirmPasswordEditText;
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEditText = findViewById(R.id.Email_EditText);
        passwordEditText = findViewById(R.id.Password_EditText);
        confirmPasswordEditText = findViewById(R.id.confirmPassword_EditText);
        createAccountBtn = findViewById(R.id.create_account_btn);
        progressBar = findViewById( R.id.Progressbar);
        loginBtnTextView = findViewById(R.id.login_text_view_btn);

        createAccountBtn.setOnClickListener(v-> createAccountMethod());
//        loginBtnTextView.setOnClickListener(v-> finish());
        loginBtnTextView.setOnClickListener((v -> startActivity(new Intent(CreateAccount.this,LoginActivity.class))));
    }
    void createAccountMethod(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        boolean isValidated = validateData(email,password,confirmPassword);

        if (!isValidated){
            return;
        }
        createAccountInFireBase(email,password);
    }
    void createAccountInFireBase(String email,String password){
        changeInProgress(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccount.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()){
                    //creating account is done
                    Utility.showToast(CreateAccount.this, "Successfully created account, Check email to verify");
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    firebaseAuth.signOut();
                    finish();
                }
                else {
                    //Failure in account creation
                    Utility.showToast(CreateAccount.this, "Error! " + task.getException().getLocalizedMessage());
                }
            }
        });

    }

    void changeInProgress(boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }
    boolean validateData(String email, String password, String confirmPassword){
        //validate the data that are input by user

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return  false;
        }
        if(password.length()<6){
            passwordEditText.setError("Password Length should be grater than 6");
            return false;
        }
        if (!password.equals(confirmPassword)){
            confirmPasswordEditText.setError("Password not matched");
            return  false;
        }
        return  true;
    }

}