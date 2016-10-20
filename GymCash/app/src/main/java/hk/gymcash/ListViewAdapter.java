package hk.gymcash;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static hk.gymcash.MainActivity.FIRST_COLUMN;
import static hk.gymcash.MainActivity.FOURTH_COLUMN;
import static hk.gymcash.MainActivity.SECOND_COLUMN;
import static hk.gymcash.MainActivity.THIRD_COLUMN;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import hk.gymcash.db.DBHelper;
import hk.gymcash.db.DownloadStatus;
import hk.gymcash.db.SongInfo;

/**
 * Created by mahender.yadav on 10/20/2016.
 */
public class ListViewAdapter extends BaseAdapter{




        public ArrayList<HashMap<String, String>> list;
       final Activity activity;
        TextView txtFirst;
        TextView txtSecond;
        TextView txtThird;
        TextView txtFourth;
    List<SongInfo> songInfos;

    private DBHelper mydb ;
        public ListViewAdapter(Activity activity, ArrayList<HashMap<String, String>> list, List<SongInfo> songInfos, DBHelper mydb){
            super();
            this.activity=activity;
            this.list=list;
            this.songInfos=songInfos;

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater=activity.getLayoutInflater();

            if(mydb==null)
            {
                mydb = new DBHelper(activity);
            }

            if(convertView == null){

                convertView=inflater.inflate(R.layout.colmn_row, null);

                txtFirst=(TextView) convertView.findViewById(R.id.name);
                txtSecond=(TextView) convertView.findViewById(R.id.gender);
                txtThird=(TextView) convertView.findViewById(R.id.age);
                txtFourth=(TextView) convertView.findViewById(R.id.status);



            }

            HashMap<String, String> map=list.get(position);
            txtFirst.setText(map.get(FIRST_COLUMN));
            txtSecond.setText(map.get(SECOND_COLUMN));
            txtThird.setText("Listen Song");
            txtFourth.setText(map.get(FOURTH_COLUMN));

            if(position>0) {

                if(songInfos !=null && songInfos.size()>0) {

                   final SongInfo songInfo = songInfos.get(position - 1);
                    txtFirst.setText(map.get(FIRST_COLUMN));
                    txtSecond.setText("-");
                    txtThird.setText("Downloading..");
                    txtFourth.setText("Remove");
                    final String path = songInfo.getPath();

                    txtFourth.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mydb.removeSong(songInfo.getId());
                        }
                    });


                    if (songInfo.getStatus() == DownloadStatus.DONE) {
                        txtThird.setText("Listen Song");
                        txtThird.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(android.content.Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                File sdcard = Environment.getExternalStorageDirectory();
                                File file = new File(sdcard, songInfo.getPath());
                            //    File file = new File(activity.getApplicationContext().getFilesDir(), songInfo.getPath());

                                Boolean isFileExists = file.exists();

                              Long size =   file.getTotalSpace();
                                intent.setDataAndType(Uri.fromFile(file), "audio/*");
                                activity.startActivity(intent);
                            }
                        });
                    }
                }
            }
            return convertView;
        }
}
