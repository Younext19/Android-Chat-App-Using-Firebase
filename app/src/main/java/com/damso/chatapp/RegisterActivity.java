package com.damso.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private Button CreateAccountButton;
    private EditText UserPseudo,UserEmail, UserPassword;
    private TextView AlreadyHaveAccountLink;
    CheckBox ShowPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    private DatabaseReference RootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initialise
        initialise();


        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();





        //________When Click On Already Have An Account_______GO TO LOGIN ACT
        AlreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });
        //---------FINISHED ALREADY HAVE AN ACCOUNT---------------


        //________________SHOW/ HIDE PASSWORD_________________
        ShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    UserPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    UserPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        //--------------FINISHED SHOW/HIDE PW----------------




        //____________________WHen Click on CREATE A NEW ACCOUNT__________________
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
        //-------------------FINISHED ON CREATE NEW ACCOUNT----------------------

    }


    private void initialise() {
        CreateAccountButton=findViewById(R.id.register_final);
        UserEmail=findViewById(R.id.register_mail);
        //UserPseudo=findViewById(R.id.Pseudo);
        UserPassword = findViewById(R.id.register_password);
        AlreadyHaveAccountLink = findViewById(R.id.haveit);
        ShowPassword = findViewById(R.id.Password_Checkbox);
        loadingbar=new ProgressDialog(this);
    }

    private void CreateNewAccount() {
        String email = UserEmail.getText().toString();
        String Password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please Enter Mail", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(Password)){
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingbar.setTitle("Creating New Account");
            loadingbar.setMessage("Please Wait");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();

            mAuth.createUserWithEmailAndPassword(email, Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String currentUserID = mAuth.getCurrentUser().getUid();
                        RootRef.child("Users").child(currentUserID).setValue("");



                        SendUserToMainActivity();
                        Toast.makeText(RegisterActivity.this, "Created Successfully", Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }
                    else
                    {
                        String message=task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }
                }
            });
        }
    }

    private void SendUserToMainActivity() {
        Intent MainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }

    private void SendUserToLoginActivity() {

        Intent LoginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(LoginIntent);
    }

    //______________________________PASSWORD VALIDATION________________________
    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }
    //-------------------------FINISHED PASSWORD VALIDATION----------------------


}
