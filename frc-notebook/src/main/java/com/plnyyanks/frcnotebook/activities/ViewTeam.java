package com.plnyyanks.frcnotebook.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.background.GetNotesForTeam;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.Team;
import com.plnyyanks.frcnotebook.dialogs.AddNoteDialog;

import java.util.ArrayList;

/**
 * File created by phil on 3/1/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class ViewTeam extends Activity implements ActionBar.TabListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    protected static String teamKey, eventName;
    protected static int teamNumber;
    protected static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceHandler.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_team);

        activity = this;

        ActionBar bar = getActionBar();
        bar.setTitle(teamNumber!=-1?"Team "+teamNumber:"All Data");

        //tab for team overview
        ActionBar.Tab teamOverviewTab = bar.newTab();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        teamOverviewTab.setText("All Notes");
        teamOverviewTab.setTag("all");
        teamOverviewTab.setTabListener(this);
        bar.addTab(teamOverviewTab);
        bar.setDisplayHomeAsUpEnabled(true);

        //add an actionbar tab for every event the team is competing at
        ArrayList<String> events;
        if(teamNumber == -1){
            events = StartActivity.db.getAllEventKeys();
        }else {
            Team team = StartActivity.db.getTeam(teamKey);
            events = team.getTeamEvents();
        }
        for(String eventKey:events){
            Log.d(Constants.LOG_TAG, "Making AB Tab for " + eventKey);
            Event event = StartActivity.db.getEvent(eventKey);
            if(event == null) continue;
            ActionBar.Tab eventTab = bar.newTab();
            eventTab.setTag(event.getEventKey());
            eventTab.setText(event.getShortName());
            eventTab.setTabListener(this);
            bar.addTab(eventTab);
        }

        if(savedInstanceState!=null) {
            bar.setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM,0));
        }else{
            bar.setSelectedNavigationItem(0);
        }

    }

    @Override
    protected void onResume() {
        StartActivity.checkThemeChanged(ViewTeam.class);
        super.onResume();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            try {
                getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
            }catch(IllegalStateException e){
                Log.w(Constants.LOG_TAG,"Failed restoring action bar navegition state on resume. Oh well...");
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
                .getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_team, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_add_note:

                new AddNoteDialog(getActionBar().getSelectedTab().getTag().toString(),GetNotesForTeam.getAdapter())
                        .show(getFragmentManager(), "Add Note");
                return true;
            case R.id.action_view_tba:
                String year;
                ActionBar.Tab selectedTab = getActionBar().getSelectedTab();
                if(selectedTab.getPosition()==0){
                    year = getString(R.string.current_year);
                }else{
                    year = selectedTab.getTag().toString().substring(0,4);
                }
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://thebluealliance.com/team/"+teamNumber+"/"+year)));
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void setTeam(String key){
        teamKey = key;
        try {
            teamNumber = Integer.parseInt(key.substring(3));
        }catch(Exception ex){
            teamNumber = -1;
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        eventName = tab.getTag().toString();
       getFragmentManager().beginTransaction().replace(R.id.team_view, new EventFragment((String) tab.getTag())).commit();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public static class EventFragment extends Fragment{

        private static String eventKey;
        private static View thisView;

        public EventFragment(String key){
            super();
            eventKey = key;
        }

        public EventFragment(){

        }

        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View v = inflater.inflate(R.layout.fragment_event_tab, null);
            thisView = v;
            new GetNotesForTeam(activity).execute(teamKey,eventKey,eventName);
            return v;
        }
    }
}
