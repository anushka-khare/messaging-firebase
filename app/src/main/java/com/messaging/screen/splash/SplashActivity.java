package com.messaging.screen.splash;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.messaging.anushka.R;
import com.messaging.screen.chatWindow.ChatActivity;
import com.messaging.screen.common.AppConstants;
import com.messaging.screen.common.MessagingApp;
import com.messaging.screen.contacts.ContactsActivity;
import com.messaging.screen.contacts.model.MessagingUser;
import com.messaging.screen.util.AppUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.messaging.screen.common.AppConstants.RC_SIGN_IN;

public class SplashActivity extends AppCompatActivity {


    private String phoneNumber;
    private EditText edtPhone;
    Button registerBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                firebaseAuthentication();
            }
        },1000);*/

        edtPhone=findViewById(R.id.phone_number);
        registerBtn=findViewById(R.id.register);
        if(MessagingApp.preferences.getString(AppConstants.LOGIN_NUMBER,null)==null) {

        }
        else
        {
            startActivity(new Intent(this, ContactsActivity.class));
            finish();
        }
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber=edtPhone.getText().toString();

                if(!phoneNumber.equalsIgnoreCase(""))
                {
                    registerUserOnFireStore(phoneNumber);
                }
            }
        });



    }



    private void firebaseAuthentication()
    {
        if(MessagingApp.preferences.getString(AppConstants.LOGIN_NUMBER,null)==null) {

            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.PhoneBuilder().build());
// Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }
        else
        {
            startActivity(new Intent(this, ContactsActivity.class));
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
               phoneNumber=user.getPhoneNumber();
               registerUserOnFireStore(phoneNumber);


            } else {
                System.out.println("logged in failed");
                AppUtils.showToast(this,R.string.login_error);
            }
        }
    }


    private void registerUserOnFireStore(final String phoneNumber)
    {
        ((MessagingApp)getApplicationContext()).fireStoreDB.collection(AppConstants.MESSAGING_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean isAlreadyRegistered=false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.get(AppConstants.MESSAGING_USER_PHONE_NUMBER).toString().equalsIgnoreCase(phoneNumber))
                                {
                                    isAlreadyRegistered=true;
                                }
                            }
                            if(!isAlreadyRegistered)
                            {
                                MessagingUser messagingUser=new MessagingUser(phoneNumber);

                                ((MessagingApp)getApplicationContext()).fireStoreDB.collection(AppConstants.MESSAGING_USERS)
                                        .add(messagingUser.getMessagingUserMap())
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                System.out.println("user registered");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                System.out.println("user registration failed");

                                            }
                                        });
                            }
                        }
                    }
                });

        MessagingApp.preferences.edit().putString(AppConstants.LOGIN_NUMBER,phoneNumber).apply();
        startActivity(new Intent(this, ContactsActivity.class));
        finish();
    }
}
