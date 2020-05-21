package com.example.musicplayer;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText UserEmail, UserPassword, UserConfirmPassword;
    private Button CreateAccountButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();


        UserPassword = (EditText) findViewById(R.id.register_password);
        UserEmail = (EditText) findViewById(R.id.register_email);
        UserConfirmPassword = (EditText) findViewById(R.id.register_confirm_password);
        CreateAccountButton = (Button) findViewById(R.id.register_create_account);
        loadingBar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmPassword = UserConfirmPassword.getText().toString();

        //if the user doesn't enter anything
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please Write your Email", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Write your Password", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(this, "Please confirm your Password", Toast.LENGTH_SHORT).show();
        }else if((!password.equals(confirmPassword))){ //to check if the entered passwords match or not //here inside the condition p!=cp will not work as they are strings
            Toast.makeText(this, "The password's do not match", Toast.LENGTH_SHORT).show();
        }else {
            loadingBar.setTitle("Creating the Account");
            loadingBar.setMessage("Please wait, while we create your account..");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true); // this statement means this process will not get cancelled till any error occurs means in short it will not end even if user touches outside


            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        SendUserToSetupActivity();
                        Toast.makeText(RegisterActivity.this, "You are Authenticated successfully", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else{ //if already that email exist or anything like this problems then this message will be displayed
                        String message = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this,"Error Occured" + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this, Main2Activity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  //adding validations
        startActivity(setupIntent);
        finish(); // by this finish statement the user will not go back to register activity when he presses back button
    }
}
