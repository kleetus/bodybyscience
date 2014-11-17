package org.kleetus.bodybyscience;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import static junit.framework.Assert.assertNotNull;

@SuppressWarnings("deprecation") //still wanted to use the tab bar for now
public class ExerciseActivity extends Activity {

    ExerciseFragmentPagerAdapter exerciseFragmentPagerAdapter;
    ViewPager viewPager;
    final String[] exercises = new String[]{"Chest Press", "Lat Pull-Downs", "Leg Press", "Seated Row", "Overhead Press", "Summary"};

    protected void buildTabs() {

        final ActionBar actionBar = getActionBar();
        assertNotNull(actionBar);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        for (String exercise : exercises) {
            ActionBar.Tab tab = actionBar.newTab()
                    .setText(exercise);

            Bundle bundle = new Bundle();
            bundle.putString(Constants.ACTIVE_TAB, exercise);

            if (exercise.equalsIgnoreCase("Summary")) {
                exerciseFragmentPagerAdapter.addTab(tab, SummaryFragment.class, bundle);
            } else {
                exerciseFragmentPagerAdapter.addTab(tab, ExerciseFragment.class, bundle);
            }

        }

    }

    public ExerciseFragmentPagerAdapter getExerciseFragmentPagerAdapter() {
        return exerciseFragmentPagerAdapter;
    }

    @Override
    protected void onCreate(Bundle state) {

        super.onCreate(state);
        setContentView(R.layout.main);

        viewPager = (ViewPager) findViewById(R.id.pager);

        exerciseFragmentPagerAdapter = new ExerciseFragmentPagerAdapter(this, viewPager, exercises);

        viewPager.setAdapter(exerciseFragmentPagerAdapter);

        buildTabs();

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

        public ViewPager getViewPager() {
            return viewPager;
        }

        private final ViewPager viewPager;
        private final ArrayList<TabInfo> tabs = new ArrayList<>();
        private String activeTab;
        private String[] exercises;


        public ExerciseFragmentPagerAdapter(Activity activity, ViewPager pager, String[] exercises) {

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

            public Bundle getArgs() {
                return args;
            }

            private final Bundle args;

            TabInfo(Class<?> klass, Bundle args) {
                this.klass = klass;
                this.args = args;
            }

        }

        public ArrayList<TabInfo> getTabs() {
            return tabs;
        }

        public String[] getExercises() {
            return exercises;
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

            ((Activity) context).setTitle("BBS - " + activeTab);

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

            SharedPreferences prefs = context.getSharedPreferences("org.kleetus.bodybyscience", Context.MODE_PRIVATE);
            prefs.edit().putString(Constants.ACTIVE_TAB, activeTab).apply();

        }


        public void selectSavedTab() {

            SharedPreferences prefs = context.getSharedPreferences("org.kleetus.bodybyscience", Context.MODE_PRIVATE);
            activeTab = prefs.getString(Constants.ACTIVE_TAB, exercises[0]);

            ((ExerciseActivity) context).setTitle("BBS - " + activeTab);

            for (int i = 0; i < exercises.length; i++) {

                if (exercises[i].equalsIgnoreCase(activeTab)) {

                    viewPager.setCurrentItem(i, true);
                    return;
                }
            }

            viewPager.setCurrentItem(0, true);

        }
    }

}
