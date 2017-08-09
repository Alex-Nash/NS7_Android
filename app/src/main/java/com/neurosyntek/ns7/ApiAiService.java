package com.neurosyntek.ns7;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;

import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;


public class ApiAiService extends IntentService {
    private String ACCESS_TOKEN = "f8830007b0924f668df85dd6485af635";
    private AIDataService aiDataService;

    public ApiAiService() {
        super("ApiAiService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int status = MainActivity.STATUS_SUCCESS;

            final String question = intent.getStringExtra(MainActivity.PARAM_QUESTION);

            final AIRequest aiRequest = new AIRequest();


            AIResponse response = null;
            aiRequest.setQuery(question);
            try {
                response = aiDataService.request(aiRequest);
            } catch (AIServiceException e) {
                status = MainActivity.STATUS_FAILURE;
            }

            String res = "";
            if (null != response) {
                res = response.getResult().getFulfillment().getSpeech();
            }
            else {
                status = MainActivity.STATUS_FAILURE;
            }

            PendingIntent pi = intent.getParcelableExtra(MainActivity.PARAM_PINTENT);
            intent.putExtra(MainActivity.PARAM_ANSWER, res);
            intent.putExtra(MainActivity.PARAM_STATUS, status);
            try {
                pi.send(this.getBaseContext(),status,intent,null,null);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final AIConfiguration config = new AIConfiguration(ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiDataService = new ai.api.AIDataService(config);
    }

}
