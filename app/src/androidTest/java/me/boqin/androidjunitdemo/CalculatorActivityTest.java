package me.boqin.androidjunitdemo;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class CalculatorActivityTest {

    @Rule
    public ActivityTestRule<CalculatorActivity> mActivityRule = new ActivityTestRule<>(
            CalculatorActivity.class);

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void onAdd() throws Exception {

    }

    @Test
    public void onSub() throws Exception {

    }

    @Test
    public void onDiv() throws Exception {

    }

    @Test
    public void onMul() throws Exception {

    }

}