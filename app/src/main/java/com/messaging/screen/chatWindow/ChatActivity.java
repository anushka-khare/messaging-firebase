package com.messaging.screen.chatWindow;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.messaging.anushka.R;
import com.messaging.screen.chatWindow.model.Chat;
import com.messaging.screen.common.AppConstants;
import com.messaging.screen.common.MessagingApp;
import com.messaging.screen.util.AppUtils;

import java.util.ArrayList;
import java.util.Collections;

public class ChatActivity extends AppCompatActivity  {

    EditText messageEdt;
    Button sendBtn;
    String toPhoneNumber;
    String toName;
    String myPhoneNumber;
    TextView usertxt;
    RecyclerView messageList;
    ArrayList<Chat> chatArrayList = new ArrayList<>();
    ProgressBar progressBar;
    ChatAdapter chatAdapter;
    private CollectionReference messagesCollectionReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        messageEdt = findViewById(R.id.messageEdt);
        sendBtn = findViewById(R.id.sendBtn);
        usertxt = findViewById(R.id.user);
        messageList = findViewById(R.id.messageList);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        toPhoneNumber = getIntent().getExtras().getString(AppConstants.CHAT_PERSON_NUMBER, "");
        toName = getIntent().getExtras().getString(AppConstants.CHAT_PERSON_NAME, "");
        myPhoneNumber = MessagingApp.preferences.getString(AppConstants.LOGIN_NUMBER, "");
        if(myPhoneNumber.length()>10) {
            int readDiff=myPhoneNumber.length()-10;
            myPhoneNumber = myPhoneNumber.substring(readDiff);
        }
        usertxt.setText(toName);
        chatAdapter = new ChatAdapter(chatArrayList);
        messageList.setLayoutManager(new LinearLayoutManager(this));
        messageList.setAdapter(chatAdapter);
        readMessagesFromFireStore();
        progressBar.setVisibility(View.GONE);

        Collections.sort(chatArrayList);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessageToFireStore(messageEdt.getText().toString());
                messageEdt.setText("");
                messageList.scrollToPosition(chatArrayList.size() - 1);
            }
        });




    }

    private void sendMessageToFireStore(String message) {
        if(message!=null && !message.equalsIgnoreCase("")) {
            Chat sent = new Chat(message, AppConstants.SENT, System.currentTimeMillis());
            Chat received = new Chat(message, AppConstants.RECEIVED, System.currentTimeMillis());

            ((MessagingApp) getApplicationContext()).fireStoreDB
                    .collection(AppConstants.CHATS)
                    .document(myPhoneNumber)
                    .collection(AppConstants.WITH)
                    .document(toPhoneNumber)
                    .collection(AppConstants.MESSAGES)
                    .document(String.valueOf(System.currentTimeMillis()))
                    .set(sent.getChatMap());

            ((MessagingApp) getApplicationContext()).fireStoreDB
                    .collection(AppConstants.CHATS)
                    .document(toPhoneNumber)
                    .collection(AppConstants.WITH)
                    .document(myPhoneNumber)
                    .collection(AppConstants.MESSAGES)
                    .document(String.valueOf(System.currentTimeMillis()))
                    .set(received.getChatMap());
        }

    }

    private void readMessagesFromFireStore() {
        messagesCollectionReference = ((MessagingApp) getApplicationContext()).fireStoreDB
                .collection(AppConstants.CHATS)
                .document(myPhoneNumber)
                .collection(AppConstants.WITH)
                .document(toPhoneNumber)
                .collection(AppConstants.MESSAGES);
        /*messagesCollectionReference.collection(AppConstants.MESSAGES).get().addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String message=document.get(AppConstants.MESSAGE).toString();
                        String dateTime= AppUtils.getDate(Long.parseLong(document.get(AppConstants.AT_TIME).toString()));
                        String status=document.get(AppConstants.STATUS).toString();
                        Chat chat=new Chat(message,status,dateTime);
                        chatArrayList.add(chat);
                        chatAdapter.notifyDataSetChanged();
                    }
                }

            }
        });*/

       /* messagesCollectionReference.get().addOnSuccessListener(ChatActivity.this,
                new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String message = document.get(AppConstants.MESSAGE).toString();
                            String dateTime = AppUtils.getDate(Long.parseLong(document.get(AppConstants.AT_TIME).toString()));
                            String status = document.get(AppConstants.STATUS).toString();
                            Chat chat = new Chat(message, status, dateTime);
                            chatArrayList.add(chat);
                            chatAdapter.notifyDataSetChanged();
                        }

                    }
                });*/

        messagesCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (DocumentChange document : value.getDocumentChanges()) {
                    String message = document.getDocument().get(AppConstants.MESSAGE).toString();
                    String dateTime = AppUtils.getDate(Long.parseLong(document.getDocument().get(AppConstants.AT_TIME).toString()));
                    String status = document.getDocument().get(AppConstants.STATUS).toString();
                    Chat chat = new Chat(message, status, dateTime);
                    chatArrayList.add(chat);
                    chatAdapter.notifyDataSetChanged();
                    /*Intent intent=new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra(AppConstants.CHAT_PERSON_NUMBER,toPhoneNumber);
                    intent.putExtra(AppConstants.CHAT_PERSON_NAME,toName);
                    PendingIntent pendingIntent =
                            PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                    AppUtils.generateNotification(getApplicationContext(),AppConstants.CHANNEL_ID,getString(R.string.app_name),message,pendingIntent);*/
                }
            }
        });

    }
}
