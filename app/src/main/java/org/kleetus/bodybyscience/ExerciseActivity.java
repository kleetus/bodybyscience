package org.kleetus.bodybyscience;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;

import static junit.framework.Assert.assertNotNull;

@SuppressWarnings("deprecation") //still wanted to use the tab bar for now
public class ExerciseActivity extends Activity {


    ExerciseFragmentPagerAdapter exerciseFragmentPagerAdapter;
    ViewPager viewPager;
    final int[] exercises = new int[]{R.string.chest_press,
            R.string.lat_pull_downs,
            R.string.leg_press,
            R.string.seated_row,
            R.string.overhead_press,
            R.string.summary};

    int workoutNumber = 0;
    private String workoutStartTime;


    @Override
    protected void onCreate(Bundle state) {

        super.onCreate(state);
        setContentView(R.layout.main);

        SharedPreferences prefs = getSharedPreferences(Constants.DATABASE_NAME, MODE_PRIVATE);
        workoutNumber = prefs.getInt(Constants.WORKOUT_NUMBER_COLUMN, 1);
        workoutStartTime = prefs.getString(Constants.DATETIME_COLUMN, getCurrentDay());

        setTitle(getCurrentTitle());

        viewPager = (ViewPager) findViewById(R.id.pager);

        exerciseFragmentPagerAdapter = new ExerciseFragmentPagerAdapter(this, viewPager, exercises);

        viewPager.setAdapter(exerciseFragmentPagerAdapter);

        buildTabs();

    }

    private String getCurrentDay() {
        Calendar cal = Calendar.getInstance();
        if (!LocaleManager.getInstance().useMetric()) {
            return (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH);
        } else {
            return (cal.get(Calendar.DAY_OF_MONTH)) + "-" + (cal.get(Calendar.MONTH) + 1);
        }

    }

    public int getWorkoutNumber() {

        return workoutNumber;
    }


    private void setNewWorkout() {

        AlertDialog.Builder confirmNewWorkout = new AlertDialog.Builder(this);
        confirmNewWorkout.setTitle(getResources().getString(R.string.new_workout));

        confirmNewWorkout.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                workoutNumber++;

                SharedPreferences prefs = getSharedPreferences(Constants.DATABASE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt(Constants.WORKOUT_NUMBER_COLUMN, workoutNumber);
                edit.apply();

                setTitle(getCurrentTitle());

            }


        });

        confirmNewWorkout.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.cancel();

            }
        });

        confirmNewWorkout.show();

    }

    private String getCurrentTitle() {
        return getString(R.string.number_sign) + workoutNumber + " " + workoutStartTime;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.action_new_workout:
                setNewWorkout();
                break;
        }

        return super.onOptionsItemSelected(menuItem);

    }

    protected void buildTabs() {

        final ActionBar actionBar = getActionBar();
        assertNotNull(actionBar);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        for (int exercise : exercises) {
            ActionBar.Tab tab = actionBar.newTab()
                    .setText(exercise);

            Bundle bundle = new Bundle();
            String exerciseString = getResources().getString(exercise);
            bundle.putString(Constants.ACTIVE_TAB, exerciseString);

            if (exerciseString.equalsIgnoreCase(getString(R.string.summary))) {
                exerciseFragmentPagerAdapter.addTab(tab, SummaryFragment.class, bundle);
            } else {
                exerciseFragmentPagerAdapter.addTab(tab, ExerciseFragment.class, bundle);
            }

        }

    }

    @Override
    protected void onPause() {

        super.onPause();
        exerciseFragmentPagerAdapter.saveActiveTab();

    }

    @Override
    protected void onResume() {

        super.onResume();
        exerciseFragmentPagerAdapter.selectSavedTab();

    }

    public static class ExerciseFragmentPagerAdapter extends FragmentPagerAdapter
            implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

        private final Context context;
        private final ActionBar actionBar;

        private final ViewPager viewPager;
        private final ArrayList<TabInfo> tabs = new ArrayList<>();
        private int activeTab;
        private int[] exercises;


        public ExerciseFragmentPagerAdapter(Activity activity, ViewPager pager, int[] exercises) {

            super(activity.getFragmentManager());
            context = activity;
            actionBar = activity.getActionBar();
            viewPager = pager;
            viewPager.setAdapter(this);
            viewPager.setOnPageChangeListener(this);
            this.exercises = exercises;

        }

        public static final class TabInfo {
            private final Class<?> klass;

            private final Bundle args;

            TabInfo(Class<?> klass, Bundle args) {
                this.klass = klass;
                this.args = args;
            }

        }

        @Override
        public void onPageScrolled(int i, float v, int i2) {
        }

        @Override
        public void onPageSelected(int i) {

            actionBar.setSelectedNavigationItem(i);

        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            activeTab = exercises[(tab.getPosition())];

            viewPager.setCurrentItem(tab.getPosition(), true);
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }

        public void addTab(ActionBar.Tab tab, Class<?> klass, Bundle args) {

            TabInfo info = new TabInfo(klass, args);
            tab.setTag(info);
            tab.setTabListener(this);
            tabs.add(info);
            actionBar.addTab(tab);
            notifyDataSetChanged();

        }


        @Override
        public Fragment getItem(int i) {

            TabInfo info = tabs.get(i);
            return Fragment.instantiate(context, info.klass.getName(), info.args);

        }

        @Override
        public int getCount() {

            return tabs.size();

        }

        public void saveActiveTab() {

            SharedPreferences prefs = context.getSharedPreferences(Constants.DATABASE_NAME, Context.MODE_PRIVATE);
            prefs.edit().putInt(Constants.ACTIVE_TAB, activeTab).apply();

        }


        public void selectSavedTab() {

            SharedPreferences prefs = context.getSharedPreferences(Constants.DATABASE_NAME, Context.MODE_PRIVATE);
            activeTab = prefs.getInt(Constants.ACTIVE_TAB, exercises[0]);

            for (int i = 0; i < exercises.length; i++) {

                if (exercises[i] == (activeTab)) {

                    viewPager.setCurrentItem(i, true);
                    return;
                }
            }

            viewPager.setCurrentItem(0, true);

        }
    }

}
