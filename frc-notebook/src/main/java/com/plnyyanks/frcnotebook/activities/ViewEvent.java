package com.plnyyanks.frcnotebook.activities;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.datatypes.Team;

import java.util.ArrayList;
import java.util.Collections;

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

        context = this;

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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
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

    public static class EventTeamListFragment extends Fragment implements View.OnClickListener {

        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_event_team_list, null);

            LinearLayout eventList = (LinearLayout) v.findViewById(R.id.team_list);
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            ArrayList<Team> teams = StartActivity.db.getAllTeamAtEvent(key);
            Collections.sort(teams);
            for(Team team:teams){
                TextView t = new TextView(context);
                t.setText("• " + team.getTeamNumber());
                t.setTextSize(20);
                t.setTextColor(0xFF000000);
                t.setTag(team.getTeamKey());
                t.setOnClickListener(this);
                eventList.addView(t);
            }

            return v;
        }

        @Override
        public void onClick(View view) {
            String teamKey = (String)view.getTag();
            ViewTeam.setTeam(teamKey);
            Intent intent = new Intent(context, ViewTeam.class);
            startActivity(intent);
        }
    }

    public static class EventScheduleFragment extends Fragment{
        View view;
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_event_schedule, null);
            view = v;
            loadMatchList();
            return v;
        }

        @Override
        public void onResume() {
            super.onResume();
            loadMatchList();
        }

        private void loadMatchList(){
            final LinearLayout qualList = (LinearLayout) view.findViewById(R.id.qual_matches);
            final LinearLayout elimList = (LinearLayout) view.findViewById(R.id.elim_matches);

            ArrayList<Match>    allMatches = StartActivity.db.getAllMatches(key),
                    qualMatches,qfMatches,sfMatches,fMatches;
            event.sortMatches(allMatches);
            qualMatches = new ArrayList<Match>();
            qualMatches = event.getQuals();
            qfMatches = event.getQuarterFinals();
            sfMatches = event.getSemiFinals();
            fMatches = event.getFinals();

            ArrayList<Match> elimMatches = new ArrayList<Match>();
            elimMatches.addAll(qfMatches);
            elimMatches.addAll(sfMatches);
            elimMatches.addAll(fMatches);

            if(qualMatches.size()>0){
                qualList.removeAllViews();
                qualList.setVisibility(View.GONE);
                TextView qualHeader = (TextView)view.findViewById(R.id.quals_header);
                qualHeader.setText("Qualification Matches ("+qualMatches.size()+")");
                qualHeader.setClickable(true);
                qualHeader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(qualList.getVisibility() == View.GONE){
                            qualList.setVisibility(View.VISIBLE);
                        }else{
                            qualList.setVisibility(View.GONE);
                        }
                    }
                });
            }
            for(Match m:qualMatches){
                TextView tv = new TextView(context);
                tv.setLayoutParams(Constants.lparams);
                tv.setText(m.getMatchKey());
                tv.setTag(m.getMatchKey());
                tv.setTextColor(0xFF000000);
                tv.setOnClickListener(new MatchClickHandler());
                qualList.addView(tv);
            }

            if(elimMatches.size()>0){
                elimList.removeAllViews();
                elimList.setVisibility(View.GONE);
                TextView elimHeader = (TextView)view.findViewById(R.id.elims_header);
                elimHeader.setText("Elimination Matches ("+elimMatches.size()+")");
                elimHeader.setClickable(true);
                elimHeader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(elimList.getVisibility() == View.GONE){
                            elimList.setVisibility(View.VISIBLE);
                        }else{
                            elimList.setVisibility(View.GONE);
                        }
                    }
                });
            }
            for(Match m:elimMatches){
                TextView tv = new TextView(context);
                tv.setLayoutParams(Constants.lparams);
                tv.setText(m.getMatchKey());
                tv.setTag(m.getMatchKey());
                tv.setTextColor(0xFF000000);
                tv.setOnClickListener(new MatchClickHandler());
                elimList.addView(tv);
            }
        }

        class MatchClickHandler implements View.OnClickListener{

            @Override
            public void onClick(View view) {
                ViewMatch.setMatchKey((String)view.getTag());

                Intent intent = new Intent(context, ViewMatch.class);
                startActivity(intent);
            }
        }
    }

}