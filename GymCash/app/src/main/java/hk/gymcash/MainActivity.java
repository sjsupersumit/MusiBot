package hk.gymcash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hk.gymcash.db.DBHelper;
import hk.gymcash.db.DownloadStatus;
import hk.gymcash.db.SongInfo;

import static android.R.id.list;

public class MainActivity extends  AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        {
            private ArrayList<HashMap<String, String>> list;

            public static final String FIRST_COLUMN="First";
            public static final String SECOND_COLUMN="Second";
            public static final String THIRD_COLUMN="Third";
            public static final String FOURTH_COLUMN="Fourth";

            private DBHelper mydb ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb = new DBHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });


        ListView listView=(ListView)findViewById(R.id.listView1);

        list=new ArrayList<HashMap<String,String>>();

       List<SongInfo> songInfoList= mydb.getAllSongs();

        HashMap<String,String> temp=new HashMap<String, String>();
        temp.put(FIRST_COLUMN, "Track Name");
        temp.put(SECOND_COLUMN, "Song Name");
        temp.put(THIRD_COLUMN, "Listen");
        temp.put(FOURTH_COLUMN, "Remove");
        list.add(temp);

        if(songInfoList!=null && songInfoList.size()>0)
        {
           for(SongInfo songInfo : songInfoList)
           {
               temp=new HashMap<String, String>();
               temp.put(FIRST_COLUMN, songInfo.getKeyword());
               temp.put(SECOND_COLUMN, songInfo.getSongName());
               temp.put(THIRD_COLUMN, songInfo.getPath());
               if(songInfo.getStatus()== DownloadStatus.DONE) {
                   temp.put(FOURTH_COLUMN, "Remove");
               }
               list.add(temp);

           }
        }




        ListViewAdapter adapter=new ListViewAdapter(this, list, songInfoList, mydb);
        listView.setAdapter(adapter);

       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
            {
                int pos=position+1;
                Toast.makeText(MainActivity.this, Integer.toString(pos)+" Clicked", Toast.LENGTH_SHORT).show();
            }

        });*/

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        }
