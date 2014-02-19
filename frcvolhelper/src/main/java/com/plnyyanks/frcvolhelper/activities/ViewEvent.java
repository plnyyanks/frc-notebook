package com.plnyyanks.frcvolhelper.activities;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.plnyyanks.frcvolhelper.R;
import com.plnyyanks.frcvolhelper.datatypes.Event;
import com.plnyyanks.frcvolhelper.datatypes.Team;

import java.util.ArrayList;

public class ViewEvent extends Activity implements ActionBar.TabListener {

    private static String key;
    private static Event event;
    protected static Context context;

    public static void setEvent(String eventKey){
        key = eventKey;
        event = StartActivity.db.getEvent(key);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().commit();
        }

        context = getApplicationContext();

        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setTitle(event.getEventName());
        bar.setSubtitle("#"+key);

        //tab for team list
        ActionBar.Tab teamListTab = bar.newTab();
        teamListTab.setText("Teams Attending");
        teamListTab.setTabListener(this);
        bar.addTab(teamListTab);

        //tab for match schedule
        ActionBar.Tab scheduleTab = bar.newTab();
        scheduleTab.setText("Match Schedule");
        scheduleTab.setTabListener(this);
        bar.addTab(scheduleTab);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Fragment f;
        switch(tab.getPosition()){
            case 0:
            default:
                f = new EventTeamListFragment(); break;
            case 1:
                f = new EventScheduleFragment(); break;
        }

        getFragmentManager().beginTransaction().replace(R.id.event_view,f).commit();

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public static class EventTeamListFragment extends Fragment{

        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_event_team_list, null);

            LinearLayout eventList = (LinearLayout) v.findViewById(R.id.team_list);
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            ArrayList<Team> teams = StartActivity.db.getAllTeamAtEvent(key);
            for(Team team:teams){
                TextView t = new TextView(context);
                t.setText("• " + team.getTeamNumber());
                t.setTextSize(15);
                eventList.addView(t);
            }

            return v;
        }
    }

    public static class EventScheduleFragment extends Fragment{

        int index;

        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_event_schedule, null);
            return v;
        }
    }

}