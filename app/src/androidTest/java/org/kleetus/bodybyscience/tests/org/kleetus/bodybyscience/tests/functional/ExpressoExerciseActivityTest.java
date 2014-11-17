package org.kleetus.bodybyscience.tests.org.kleetus.bodybyscience.tests.functional;


import android.os.Handler;
import android.test.ActivityInstrumentationTestCase2;

import org.hamcrest.Matchers;
import org.kleetus.bodybyscience.ExerciseActivity;
import org.kleetus.bodybyscience.R;
import org.kleetus.bodybyscience.SummaryFragment;
import org.kleetus.bodybyscience.SummaryItemDialogFragment;

import java.util.Random;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.clearText;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.swipeLeft;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.swipeRight;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.RootMatchers.withDecorView;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.Is.is;

public class ExpressoExerciseActivityTest extends ActivityInstrumentationTestCase2<ExerciseActivity> {

    public ExpressoExerciseActivityTest() {
        super(ExerciseActivity.class);
    }


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

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                onView(allOf(withId(R.id.log), isDisplayed())).perform(click());

                onView(withText(R.string.exercise_saved))
                        .inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView()))))
                        .check(matches(isDisplayed()));
            }
        };

        delayedForChrono(runnable);

    }

    //test the incrementing of the weight
    public void testIncrementingTheWeightField() {

        int randInt = getRandomNumber(100);
        onView(allOf(withId(R.id.weight), isDisplayed())).perform(clearText());
        onView(allOf(withId(R.id.weight), isDisplayed())).perform(typeText(String.valueOf(randInt)));
        onView(allOf(withId(R.id.increase_button), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.weight), isDisplayed())).check(matches(withText(String.valueOf(randInt + 5))));

    }


    //test the decerementing of the weight
    public void testDecrementingTheWeightField() {

        int randInt = getRandomNumber(200);
        onView(allOf(withId(R.id.weight), isDisplayed())).perform(clearText());
        onView(allOf(withId(R.id.weight), isDisplayed())).perform(typeText(String.valueOf(randInt)));
        onView(allOf(withId(R.id.decrease_button), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.weight), isDisplayed())).check(matches(withText(String.valueOf(randInt - 5))));

    }

    //test the reset button when there is time on the clock but the clock is stopped
    public void testResetOfChronoWithTimeStopped() {

        onView(allOf(withId(R.id.start), isDisplayed())).perform(click());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                onView(allOf(withId(R.id.start), isDisplayed())).perform(click());
                onView(allOf(withId(R.id.timer), isDisplayed())).check(matches(not(withText("0:00"))));
                onView(allOf(withId(R.id.reset), isDisplayed())).perform(click());
                onView(allOf(withId(R.id.timer), isDisplayed())).check(matches(withText("0:00")));
            }
        };

        delayedForChrono(runnable);

    }

    //test that reset button resets the timer back to zero, but the timer continues to increment
    public void testResetOfChronoWithTimeIncrementing() {

        onView(allOf(withId(R.id.start), isDisplayed())).perform(click());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                onView(allOf(withId(R.id.reset), isDisplayed())).perform(click());
                onView(allOf(withId(R.id.timer), isDisplayed())).check(matches(withText("0:00")));
            }
        };

        delayedForChrono(runnable);

        Runnable afterResetRunnable = new Runnable() {
            @Override
            public void run() {
                onView(allOf(withId(R.id.timer), isDisplayed())).check(matches(not(withText("0:00"))));
            }
        };

        delayedForChrono(afterResetRunnable);
    }

    //test that when you save an exercise, you should see it in the summary
    public void testSeeingYourSavedExerciseInSummarySection() {

        final int randInt = startChronoAndFillWeight();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                onView(allOf(withId(R.id.log), isDisplayed())).perform(click());

                onView(withText(R.string.exercise_saved))
                        .inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView()))))
                        .check(matches(isDisplayed()));

                onView(withId(R.id.pager)).perform(swipeLeft());
                onData(allOf(is(instanceOf(SummaryFragment.class))))
                        .inAdapterView(withId(android.R.id.list))
                        .atPosition(0).onChildView(withId(R.id.workout_number))
                        .perform(click());

                onData(allOf(is(Matchers.instanceOf(SummaryItemDialogFragment.class))))
                        .inAdapterView(withId(R.id.summary_list_view))
                        .atPosition(0).onChildView(withId(R.id.weight_summary_item))
                        .check(matches(withText(String.valueOf(randInt))));

            }
        };

        delayedForChrono(runnable);

    }

    //test that exercise tab is filled in with last logged exercise weight
    public void testWeightFilledInOnSubsequentLoads() {

        final int randInt = startChronoAndFillWeight();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                onView(allOf(withId(R.id.log), isDisplayed())).perform(click());

                onView(withText(R.string.exercise_saved))
                        .inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView()))))
                        .check(matches(isDisplayed()));

                onView(allOf(withId(R.id.weight), isDisplayed())).perform(clearText());

                onView(withId(R.id.pager)).perform(swipeLeft());

                onView(withId(R.id.pager)).perform(swipeRight());

                onView(allOf(withId(R.id.weight), isDisplayed())).check(matches(withText(randInt)));

            }
        };

        delayedForChrono(runnable);

    }

    //test that time on the clock does not reset if the view pager kills the fragment
    public void testThatTheTimerDoesNotChangeWhenTabsChanges() {

        onView(withId(R.id.pager)).perform(swipeRight());
        onView(allOf(withId(R.id.start), isDisplayed())).perform(click());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                onView(allOf(withId(R.id.start), isDisplayed())).perform(click());
                onView(withId(R.id.pager)).perform(swipeLeft());
                onView(withId(R.id.pager)).perform(swipeRight());

                onView(allOf(withId(R.id.timer), isDisplayed())).check(matches(withText("0:01")));
            }
        };

        delayedForChrono(runnable);

    }


    private int getRandomNumber(int seed) {

        return new Random().nextInt(seed);

    }


    private int startChronoAndFillWeight() {

        int randInt = getRandomNumber(1000);
        onView(withId(R.id.pager)).perform(swipeRight());

        onView(allOf(withId(R.id.weight), isDisplayed())).perform(clearText());
        onView(allOf(withId(R.id.weight), isDisplayed())).perform(typeText(String.valueOf(randInt)));
        onView(allOf(withId(R.id.start), isDisplayed())).perform(click());
        return randInt;

    }

    private void delayedForChrono(Runnable runnable) {

        Handler handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }


}
