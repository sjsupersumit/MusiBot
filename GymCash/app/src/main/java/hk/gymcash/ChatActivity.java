package hk.gymcash;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import hk.gymcash.chatboat.MusiBot;
import hk.gymcash.db.DBHelper;
import hk.gymcash.db.DownloadStatus;

import static android.R.attr.data;

public class ChatActivity extends ActionBarActivity {

    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;

    private DBHelper mydb ;

    MusiBot musiBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        musiBot= new MusiBot();
        mydb = new DBHelper(this);
        initControls();
    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        companionLabel.setText("");// Hard Coded
        loadDummyHistory();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(122);//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);

                messageET.setText("");

                displayMessage1(chatMessage);
            }
        });
    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();

       // getMessage1(message.getMessage());
    }
    public void displayMessage1(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();

        getMessage1(message.getMessage());
    }

    private void getMessage1(final String message) {

        ChatMessage msg = new ChatMessage();
        msg.setId(1);
        msg.setMe(false);
        msg.setMessage(musiBot.startChat(message));
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        displayMessage(msg);

      final  String songId = UUID.randomUUID().toString();
        mydb.insertSong(message, "", DownloadStatus.IN_PROGRESS, null,songId );

        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadSong(message, songId);
            }
        }).start();


    }

    private void downloadSong(String message, String songId) {


        try {
            String url = "http://192.168.90.238:5000/musicbot/download/song/" + URLEncoder.encode(message, "UTF-8");
            downloadFile(url, message, songId);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    public  void downloadFile(String fileURL,  String keyword, String songId) throws IOException {

        System.out.println(fileURL);
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestProperty("Accept-Encoding", "identity");
        /*httpConn.setDoOutput(true);*/
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {

            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            String fileName="";
            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }



            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = UUID.randomUUID().toString()+".mp3";

            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, saveFilePath);

            FileOutputStream fileOutput = new FileOutputStream(file);
          //  InputStream inputStream = urlConnection.getInputStream();

            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();

            inputStream.close();
//String searchkeyword, String songName, DownloadStatus songstatus, String fileLoc

            mydb.updateSong(songId, fileName, DownloadStatus.DONE, saveFilePath);
//            mydb.insertSong(keyword, fileName, DownloadStatus.DONE, saveFilePath, songId);
            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }
    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadDummyHistory(){
        try {
            chatHistory = new ArrayList<ChatMessage>();



            AssetManager assetManager = getApplicationContext().getAssets();

            InputStream inputStream = getResources().openRawResource(R.raw.data);

            ChatMessage msg = new ChatMessage();
            msg.setId(1);
            msg.setMe(false);
            msg.setMessage(musiBot.startChat(inputStream));
            msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
            chatHistory.add(msg);


            adapter = new ChatAdapter(ChatActivity.this, new ArrayList<
                    ChatMessage>());
            messagesContainer.setAdapter(adapter);

            for (int i = 0; i < chatHistory.size(); i++) {
                ChatMessage message = chatHistory.get(i);

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}