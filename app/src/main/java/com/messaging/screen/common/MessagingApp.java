package com.messaging.screen.common;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.messaging.anushka.R;
import com.messaging.screen.chatWindow.ChatActivity;
import com.messaging.screen.chatWindow.model.Chat;
import com.messaging.screen.splash.SplashActivity;
import com.messaging.screen.util.AppUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MessagingApp extends Application {


    public static SharedPreferences preferences;
    public FirebaseFirestore fireStoreDB;
    CollectionReference collectionReference;
    String message;
    private String myPhoneNumber;
    ListenerRegistration registration;
    private CollectionReference chatReference;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        preferences = this.getSharedPreferences(AppConstants.USER_PREFERENCE_FILE, MODE_PRIVATE);

        fireStoreDB = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        fireStoreDB.setFirestoreSettings(settings);

        myPhoneNumber = MessagingApp.preferences.getString(AppConstants.LOGIN_NUMBER, null);

        System.out.println("my phone number " + myPhoneNumber);
        if (myPhoneNumber != null) {
            if (myPhoneNumber.length() > 10) {
                int readDiff = myPhoneNumber.length() - 10;
                myPhoneNumber = myPhoneNumber.substring(readDiff);
            }
            collectionReference = ((MessagingApp) getApplicationContext()).fireStoreDB
                    .collection(AppConstants.CHATS)
                    .document(myPhoneNumber)
                    .collection(AppConstants.WITH);
            System.out.println("collection path " + collectionReference.getPath());

            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    for (final DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        final String fromUserNumber = documentSnapshot.getId();
                        final String fromUserName = AppUtils.getContactName(fromUserNumber, getApplicationContext());
                        chatReference = documentSnapshot.getReference().collection(AppConstants.MESSAGES);
                        System.out.println("collection path " + chatReference.getPath());

                        registration = chatReference.addSnapshotListener(Executors.newCachedThreadPool(), new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {

                                    message = documentChange.getDocument().get(AppConstants.MESSAGE).toString();
                                    System.out.println("message " + message);
                                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                    intent.putExtra(AppConstants.CHAT_PERSON_NUMBER, fromUserNumber);
                                    intent.putExtra(AppConstants.CHAT_PERSON_NAME, fromUserName);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    PendingIntent pendingIntent =
                                            PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                                    AppUtils.generateNotification(getApplicationContext(), AppConstants.CHANNEL_ID, fromUserName, message, pendingIntent);
                                }
                            }
                        });

                    }
                }
            });


            /*collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    for (final DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments())
                    {
                        final String fromUserNumber=documentSnapshot.getId();
                        final String fromUserName=AppUtils.getContactName(fromUserNumber,getApplicationContext());
                         chatReference = documentSnapshot.getReference().collection(AppConstants.MESSAGES);
                        System.out.println("collection path " + chatReference.getPath());

                        registration=chatReference.addSnapshotListener(Executors.newCachedThreadPool(),new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {

                                    message = documentChange.getDocument().get(AppConstants.MESSAGE).toString();
                                    System.out.println("message " + message);
                                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                    intent.putExtra(AppConstants.CHAT_PERSON_NUMBER,fromUserNumber);
                                    intent.putExtra(AppConstants.CHAT_PERSON_NAME,fromUserName);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    PendingIntent pendingIntent =
                                            PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                                    AppUtils.generateNotification(getApplicationContext(), AppConstants.CHANNEL_ID, fromUserName, message, pendingIntent);
                                }
                            }
                        });

                    }

                }
            });*/
            /*collectionReference.get().addOnCompleteListener(
                    new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful())
                    {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            System.out.println("document size " + task.getResult().size());

                            CollectionReference collectionReference = documentSnapshot.getReference().collection(AppConstants.MESSAGES);
                            System.out.println("collection path " + collectionReference.getPath());

                            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {

                                        message = documentChange.getDocument().get(AppConstants.MESSAGE).toString();
                                        System.out.println("message " + message);
                                        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                                        PendingIntent pendingIntent =
                                                PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                                        AppUtils.generateNotification(getApplicationContext(), AppConstants.CHANNEL_ID, getString(R.string.app_name), message, pendingIntent);
                                    }
                                }
                            });
                        }
                    }
                }
            });*/

        }
    }
}
