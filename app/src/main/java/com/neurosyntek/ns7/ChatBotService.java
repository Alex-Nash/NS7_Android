package com.neurosyntek.ns7;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Timer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ai.api.android.AIConfiguration;


public class ChatBotService extends IntentService {
    private static String SDPATH = "/ns7/bots/ns7";
    private static String CHATBOTNAME = "ns7";

    public Bot bot;
    public static Chat chat;

    @Override
    public void onCreate() {
        super.onCreate();

        //snipet
        //checking SD card availablility
        boolean a = isSDCARDAvailable();
//receiving the assets from the app directory
        AssetManager assets = getResources().getAssets();
        File jayDir = new File(Environment.getExternalStorageDirectory().toString() + SDPATH);
        deleteDirectory(jayDir);

        boolean b = jayDir.mkdirs();
        if (jayDir.exists()) {
            //Reading the file
            try {
                for (String dir : assets.list(CHATBOTNAME)) {
                    File subdir = new File(jayDir.getPath() + "/" + dir);
                    boolean subdir_check = subdir.mkdirs();
                    for (String file : assets.list(CHATBOTNAME + "/" + dir)) {
                        File f = new File(jayDir.getPath() + "/" + dir + "/" + file);
                        if (f.exists()) {
                            continue;
                        }
                        InputStream in = null;
                        OutputStream out = null;
                        in = assets.open(CHATBOTNAME + "/" + dir + "/" + file);
                        out = new FileOutputStream(jayDir.getPath() + "/" + dir + "/" + file);
                        //copy file from assets to the mobile's SD card or any secondary memory
                        copyFile(in, out);
                        in.close();
                        in = null;
                        out.flush();
                        out.close();
                        out = null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//get the working directory
        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/" + CHATBOTNAME;
        System.out.println("Working Directory = " + MagicStrings.root_path);
        AIMLProcessor.extension =  new PCAIMLProcessorExtension();
//Assign the AIML files to bot for processing
        bot = new Bot(CHATBOTNAME, MagicStrings.root_path, "chat");
        chat = new Chat(bot);
        String[] args = null;
        mainFunction(args);
    }

    //check SD card availability
    public static boolean isSDCARDAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)? true :false;
    }

    //Request and response of user and the bot
    public static void mainFunction (String[] args) {
        MagicBooleans.trace_mode = false;
        System.out.println("trace mode = " + MagicBooleans.trace_mode);
        Graphmaster.enableShortCuts = true;
        Timer timer = new Timer();
        String request = "Hello.";
        String response = chat.multisentenceRespond(request);

        System.out.println("Human: "+request);
        System.out.println("Robot: " + response);
    }


    //copying the file
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    Log.d("delete", files[i].getName() +" is delete:"+ String.valueOf(files[i].delete()));
                }
            }
        }
        return( path.delete() );
    }

    public ChatBotService() {
        super("ChatBotService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String request = intent.getStringExtra(MainActivity.PARAM_QUESTION);
            int status = MainActivity.STATUS_SUCCESS;
            String response = chat.multisentenceRespond(request);
            PendingIntent pi = intent.getParcelableExtra(MainActivity.PARAM_PINTENT);


            intent.putExtra(MainActivity.PARAM_ANSWER, response);
            intent.putExtra(MainActivity.PARAM_STATUS, status);
            try {
                pi.send(this.getBaseContext(),status,intent,null,null);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }


}
