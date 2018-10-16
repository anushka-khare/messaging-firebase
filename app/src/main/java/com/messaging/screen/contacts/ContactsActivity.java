package com.messaging.screen.contacts;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.messaging.anushka.R;
import com.messaging.screen.chatWindow.ChatActivity;
import com.messaging.screen.common.AppConstants;
import com.messaging.screen.common.MessagingApp;
import com.messaging.screen.contacts.model.MessagingUser;
import com.messaging.screen.splash.SplashActivity;
import com.messaging.screen.util.AppUtils;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    TextView userTxt;
    RecyclerView contactsList;
    ArrayList<MessagingUser> contactNames=new ArrayList<>();
    ContactsAdapter contactsAdapter;
    ProgressBar progressBar;
    String myNumber;
    private static final String[] PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
    public static final String PHONE_REGEX = "^(\\+\\d{1,3}[- ]?)?\\d{10,14}$";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contacts);

        userTxt=findViewById(R.id.user);
        contactsList=findViewById(R.id.contacts_list);
        progressBar=findViewById(R.id.progressBar);
        myNumber=MessagingApp.preferences.getString(AppConstants.LOGIN_NUMBER,"");
        if(AppUtils.isPermissionAllowed(this, Manifest.permission.READ_CONTACTS)) {
            initialiseView();

        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, AppConstants.READ_CONTACTS_REQUEST_CODE);

        }

    }


    private void initialiseView()
    {
        progressBar.setVisibility(View.VISIBLE);
        contactsAdapter = new ContactsAdapter(contactNames,this);
        userTxt.setText("Hello "+ MessagingApp.preferences.getString(AppConstants.LOGIN_NUMBER,""));

            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll(" ","");
                System.out.println("contact "+name+" "+phoneNumber);

                findContactsOnMessaging(phoneNumber,name);
            }
            phones.close();
        progressBar.setVisibility(View.GONE);

            contactsList.setLayoutManager(new LinearLayoutManager(this));
            contactsList.setAdapter(contactsAdapter);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==AppConstants.READ_CONTACTS_REQUEST_CODE)
        {
            if (grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                initialiseView();
            }
        }
    }


    private void findContactsOnMessaging(final String phoneNo, final String name)
    {
        ((MessagingApp)getApplicationContext()).fireStoreDB.collection(AppConstants.MESSAGING_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String readNumber=document.get(AppConstants.MESSAGING_USER_PHONE_NUMBER).toString();
                                String phoneNumber = phoneNo;
                                if(phoneNumber.length()>10) {
                                    int phDiff=phoneNumber.length()-10;
                                    phoneNumber=phoneNumber.substring(phDiff);
                                }
                                if(readNumber.length()>10) {
                                    int readDiff=readNumber.length()-10;
                                    readNumber = readNumber.substring(readDiff);
                                }
                                if(readNumber.equalsIgnoreCase(phoneNumber) && !readNumber.equalsIgnoreCase(myNumber))
                                {
                                    contactNames.add(new MessagingUser(phoneNumber,name));
                                    contactsAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }

                        }
                    }
                });
    }
}
