package com.neurosyntek.ns7;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import junit.framework.Test;

public class MainActivity extends AppCompatActivity {
    public final static String PARAM_PINTENT = "pendingIntent";
    public final static String PARAM_QUESTION = "question";
    public final static String PARAM_ANSWER = "answer";
    public final static String PARAM_STATUS = "status";
    public final static int STATUS_SUCCESS = 100;
    public final static int STATUS_FAILURE = 200;

    private Button   mSendButton;
    private EditText mMessageField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSendButton = (Button) findViewById(R.id.sendButton);
        mMessageField = (EditText) findViewById(R.id.messageField);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = mMessageField.getText().toString();
                PendingIntent pi;
                Intent intent;

                //intent = new Intent(MainActivity.this, ApiAiService.class);
                intent = new Intent(MainActivity.this, ChatBotService.class);
                pi = createPendingResult(1, intent, 0);

                intent.putExtra(PARAM_PINTENT, pi);
                intent.putExtra(PARAM_QUESTION, question);

                startService(intent);
            }
        });

        startService(new Intent(MainActivity.this,SocketServerService.class));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result = data.getStringExtra(PARAM_ANSWER);
        String status;
        if (resultCode == STATUS_FAILURE){
            status = "FAILURE";
        }
        else
            status = "SUCCESS";

        Toast toast = Toast.makeText(getApplicationContext(), status +": "+result, Toast.LENGTH_SHORT);
        toast.show();

    }
}
