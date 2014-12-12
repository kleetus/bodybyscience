package org.kleetus.bodybyscience.tests.org.kleetus.bodybyscience.tests.functional;


import android.app.Instrumentation;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.google.android.apps.common.testing.ui.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.kleetus.bodybyscience.ExerciseActivity;
import org.kleetus.bodybyscience.R;

import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import static com.google.android.apps.common.testing.testrunner.util.Checks.checkNotNull;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.clearText;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.swipeLeft;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.swipeRight;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.RootMatchers.withDecorView;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDescendantOfA;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.core.Is.is;

public class ExpressoExerciseActivityTest extends ActivityInstrumentationTestCase2<ExerciseActivity> {

    public ExpressoExerciseActivityTest() {
        super(ExerciseActivity.class);
    }

    private static final int seed = 1000;

    @Override
    public void setUp() throws Exception {

        super.setUp();
        getActivity();

    }


    //test that we let the user know that they can't save an exercise result if the weight is 0 or the time is 0
    public void testErrorToastPopsUpWhenNoTimeOrWeightSpecified() {

        onView(allOf(withId(R.id.weight), isDisplayed())).perform(clearText());
        onView(allOf(withId(R.id.log), isDisplayed())).perform(click());
        onView(withText(R.string.exercise_not_saved))
                .inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    //test that when we have a weight and some time on the chronometer, we should be able to save the exercise
    public void testToastPopsUpWhenWeGetASuccessfullySavedRecord() {

        startChronoAndFillWeight();

        onView(allOf(withId(R.id.log), isDisplayed())).perform(click());

        onView(withText(R.string.exercise_saved))
                .inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

    }

    //test the incrementing of the weight
    public void testIncrementingTheWeightField() {

        int randInt = getRandomNumber(seed);
        onView(allOf(withId(R.id.weight), isDisplayed())).perform(clearText());
        onView(allOf(withId(R.id.weight), isDisplayed())).perform(typeText(String.valueOf(randInt)));
        onView(allOf(withId(R.id.increase_button), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.weight), isDisplayed())).check(matches(withText(String.valueOf(randInt + 5))));

    }


    //test the decerementing of the weight
    public void testDecrementingTheWeightField() {

        int randInt = getRandomNumber(seed);
        onView(allOf(withId(R.id.weight), isDisplayed())).perform(clearText());
        onView(allOf(withId(R.id.weight), isDisplayed())).perform(typeText(String.valueOf(randInt)));
        onView(allOf(withId(R.id.decrease_button), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.weight), isDisplayed())).check(matches(withText(String.valueOf(randInt - 5))));

    }

    //test the reset button when there is time on the clock but the clock is stopped
    public void testResetOfChronoWithTimeStopped() {

        startChronoAndFillWeight();

        onView(allOf(withId(R.id.start), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.timer), isDisplayed())).check(matches(not(withText("0:00"))));
        onView(allOf(withId(R.id.reset), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.timer), isDisplayed())).check(matches(withText("0:00")));

    }

    //test that exercise tab is filled in with last logged exercise weight
    public void testWeightFilledInOnSubsequentLoads() {

        final int randInt = startChronoAndFillWeight();

        onView(allOf(withId(R.id.log), isDisplayed())).perform(click());

        onView(withText(R.string.exercise_saved))
                .inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.weight), isDisplayed())).perform(clearText());

        onView(withId(R.id.pager)).perform(swipeLeft());

        onView(withId(R.id.pager)).perform(swipeRight());

        onView(allOf(withId(R.id.weight), isDisplayed())).check(matches(withText(randInt)));

    }

    //test that time on the clock does not reset if the view pager kills the fragment
    public void testThatTheTimerDoesNotChangeWhenTabsChanges() {

        startChronoAndFillWeight();

        onView(allOf(withId(R.id.start), isDisplayed())).perform(click());
        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(withId(R.id.pager)).perform(swipeLeft());

        onView(withId(R.id.pager)).perform(swipeRight());
        onView(withId(R.id.pager)).perform(swipeRight());
        onView(withId(R.id.pager)).perform(swipeRight());

        onView(allOf(withId(R.id.timer), isDisplayed())).check(matches(withText("0:01")));

    }

    //test that a logged exercise appears in the summary section
    public void testThatLoggedExerciseAppearsInTheSummarySection() {


//        onView(withId(R.id.pager)).perform(swipeLeft());
//        onView(withId(R.id.pager)).perform(swipeLeft());
//        onView(withId(R.id.pager)).perform(swipeLeft());
//        onView(withId(R.id.pager)).perform(swipeLeft());

        final int randInt = startChronoAndFillWeight();

        onView(allOf(withId(R.id.log), isDisplayed())).perform(click());

//        onView(withText(R.string.exercise_saved))
//                .inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView()))))
//                .check(matches(isDisplayed()));

        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(withId(R.id.pager)).perform(swipeLeft());

        onView(withId(android.R.id.list))
                .check(matches(withAdaptedData(withItemContent("Chest Press"))));


    }

    //test that workout number is incremented in the title
    public void testTheWorkoutNumberIsIncrementedInTheTitle() {

        String newTitle = bumpTitleString();
        onView(allOf(withId(R.id.action_new_workout), isDisplayed())).perform(click());
        onView(allOf(withText(R.string.ok), isDisplayed())).perform(click());

        onView(allOf(isDescendantOfA(withResourceName("android:id/action_bar_container")), withText(newTitle)))
                .check(matches(withText(newTitle)));

    }

    //test that the newly created workout number is used to create a log entry for the workout
    public void testThatATheNewWorkoutNumberIsUsedToSaveAnExercise() {

        int workoutNumber = bumpTitleInt();



    }


    private java.util.regex.Matcher findDigits(String str) {

        Pattern pattern = Pattern.compile("\\d+");
        return pattern.matcher(str);

    }

    private String bumpTitleString() {

        java.util.regex.Matcher matcher = findDigits(getActivity().getTitle().toString());
        matcher.find();
        String workoutNumber = matcher.group();

        int newWorkoutNumber = Integer.parseInt(workoutNumber) + 1;
        return matcher.replaceFirst(String.valueOf(newWorkoutNumber));

    }

    private int bumpTitleInt() {

        java.util.regex.Matcher matcher = findDigits(getActivity().getTitle().toString());
        matcher.find();
        String workoutNumber = matcher.group();

        return Integer.parseInt(workoutNumber) + 1;

    }

    private int getRandomNumber(int seed) {

        return new Random().nextInt(seed);

    }

    private int startChronoAndFillWeight() {

        int randInt = getRandomNumber(seed);
        onView(withId(R.id.pager)).perform(swipeRight());

        onView(allOf(withId(R.id.weight), isDisplayed())).perform(clearText());
        onView(allOf(withId(R.id.weight), isDisplayed())).perform(typeText(String.valueOf(randInt)));

        Instrumentation.ActivityMonitor monitor =
                getInstrumentation().addMonitor(ExerciseActivity.class.getName(), null, false);

        onView(allOf(withId(R.id.start), isDisplayed())).perform(click());

        monitor.waitForActivityWithTimeout(1000);

        return randInt;

    }


    public static Matcher<View> withResourceName(String resourceName) {
        return withResourceName(is(resourceName));
    }


    public static Matcher<View> withResourceName(final Matcher<String> resourceNameMatcher) {
        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {

                description.appendText("with resource name: ");
                resourceNameMatcher.describeTo(description);

            }

            @Override
            public boolean matchesSafely(View view) {

                int id = view.getId();
                return id != View.NO_ID && id != 0 && view.getResources() != null
                        && resourceNameMatcher.matches(view.getResources().getResourceName(id));

            }
        };
    }

    public static Matcher<Object> withItemContent(String expectedText) {

        checkNotNull(expectedText);
        return withItemContent(equalTo(expectedText));

    }

    public static Matcher<Object> withItemContent(final Matcher<String> itemTextMatcher) {

        checkNotNull(itemTextMatcher);

        return new BoundedMatcher<Object, SQLiteCursor>(SQLiteCursor.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("with item content: ");
                itemTextMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(SQLiteCursor sqLiteCursor) {
                return false;
            }
        };
    }

    private static Matcher<View> withAdaptedData(final Matcher<Object> dataMatcher) {
        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("with class name: ");
                dataMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof AdapterView)) {
                    return false;
                }
                @SuppressWarnings("rawtypes")
                Adapter adapter = ((AdapterView) view).getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (dataMatcher.matches(adapter.getItem(i))) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

}
