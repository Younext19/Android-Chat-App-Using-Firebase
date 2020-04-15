package com.damso.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private ViewPager mviewpager;
    private TabLayout mtablayout;
    private TabAccessorAdapter mtabaccessoradapter;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference Rootref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Rootref = FirebaseDatabase.getInstance().getReference();

        //call initialise function
        initialise();


        //________TOOLBAR__________
        setSupportActionBar(mtoolbar);

        //________FRAGMENTS__________
        mtabaccessoradapter = new TabAccessorAdapter(getSupportFragmentManager());
        mviewpager.setAdapter(mtabaccessoradapter);
        mtablayout.setupWithViewPager(mviewpager);

    }

    //________________________Initialisations______________________________
    public void initialise(){
        mtoolbar=findViewById(R.id.main_page_toolbar);
        mviewpager=findViewById(R.id.main_tabs_pager);
        mtablayout=findViewById(R.id.main_tabs);
    }
    //------------------------FINISHED INITIALISATIONS--------------------


    //__________________When Start APP______if User exists or no______________
    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser==null){
            //Go To Login Activity
            SendUserToLoginActivity();
        }
        else{
            VerifyUserExistance();
        }
    }
    //-----------------FINISHED WHEN START APP-------------------



    //________________________CHECK IF USER Already LOGGED IN_____________
    private void VerifyUserExistance() {
        String currentUserID= mAuth.getCurrentUser().getUid();
        Rootref.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists())){
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else{
                    SendUserToSettingsActivity();
                    //Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //-----------------------FINISHED CHECKING...---------------------
    //____________________Go To Settings Activity_________________________
    private void SendUserToSettingsActivity() {
        Intent Settingsintent = new Intent(MainActivity.this, SettingsActivity.class);
        Settingsintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(Settingsintent);
        finish();
    }
    //------------------FINISHED SETTINGS ACTIVITY------------------------


    //_____________________Go To Login Activity___________________
    private void SendUserToLoginActivity() {
        Intent loginintent = new Intent(MainActivity.this, LoginActivity.class);
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginintent);
        finish();
    }
    //-------------------FINISHED LOGIN ACTIVITY--------------------------


    //__________CREATE OPTIONS MENU______________
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }


    //____________MENU OPTIONS ITEM SELECTED__________________
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()== R.id.main_Logout_option){
            mAuth.signOut();
            SendUserToLoginActivity();
        }

        if(item.getItemId()== R.id.main_settings_option){
            SendUserToSettingsActivity();
        }
        if(item.getItemId()== R.id.main_create_group_option){
            RequestNewGroup();
        }
        if(item.getItemId()== R.id.main_find_friends_option){

        }
        return true;
    }
    //-------------------FINISHED MENU OPTIONS---------------


    //____________DIALOG INTERFACE TO ENTER GROUP NAME AND CREATE THE GROUP____________________
    private void RequestNewGroup() {
        //ALERT DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);

        //ALERT DIALOG TITLE
        builder.setTitle("Enter Group Name : ");

        //EDIT TEXT
        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("EXAMPLE : PORN GROUP");
        builder.setView(groupNameField);


        //POSITIVE == CREATE
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                String groupName=groupNameField.getText().toString();

                //if String == NULL
                if(TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, "Please Write Group Name...", Toast.LENGTH_SHORT).show();
                }
                //String != NULL
                else
                {
                    CreateGroupName(groupName);
                }
            }
        });
        //NEGATIVE == CANCEL
        builder.setNegativeButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();

    }
    //----------------------FINISHED DIALOG INTERFACE------------------------------------------


    //____________________CREATE A GROUP & Goes To DB___________________
    private void CreateGroupName(final String groupName) {
        Rootref.child("Groups").child("groupName").setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, groupName+" Is created Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }

                });





    }
    //--------------------FINISHED CREAte A GROUP-----------------------


}
