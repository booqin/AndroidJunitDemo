package me.boqin.androidjunitdemo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.Calendar;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;

/**
 * 在androidTest中测试需要android库支持的单元
 * Created by Boqin on 2017/6/6.
 * Modified by Boqin
 *
 * @Version
 */
public class SharedPreferencesHelperTest {

    private static final String TEST_NAME = "Test name";

    private static final String TEST_EMAIL = "test@email.com";

    private static final Calendar TEST_DATE_OF_BIRTH = Calendar.getInstance();

    private SharedPreferenceEntry mSharedPreferenceEntry;

    private SharedPreferencesHelper mSharedPreferencesHelper;

    private SharedPreferences mSharePreferences;

    /** 上下文 */
    private Context mContext;

    //如果需要扩展类的行为，可以通过mock来实现
    private SharedPreferencesHelper mMockSharedPreferencesHelper;

    @Mock
    SharedPreferences mMockSharePreferences;

    @Mock
    SharedPreferences.Editor mMockBrokenEditor;

    @Before
    public void setUp() throws Exception {
        //获取application的context
        mContext = InstrumentationRegistry.getTargetContext();
        mSharePreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        mSharedPreferenceEntry = new SharedPreferenceEntry(TEST_NAME, TEST_DATE_OF_BIRTH, TEST_EMAIL);
        mSharedPreferencesHelper = new SharedPreferencesHelper(mSharePreferences);
//        mBrokenSharedPreferencesHelper = new SharedPreferencesHelper(mockBrokenMockSharedPreference());

//        mMockSharePreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mMockSharePreferences = Mockito.mock(SharedPreferences.class);
        mMockBrokenEditor = Mockito.mock(SharedPreferences.Editor.class);
        when(mMockSharePreferences.edit()).thenReturn(mMockBrokenEditor);
        when(mMockBrokenEditor.commit()).thenReturn(false);
        mMockSharedPreferencesHelper = new SharedPreferencesHelper(mMockSharePreferences);
    }

    @Test
    public void sharedPreferencesHelper_SavePersonalInformation() throws Exception {
        assertThat(mSharedPreferencesHelper.savePersonalInfo(mSharedPreferenceEntry), is(true));
    }

    @Test
    public void sharedPreferencesHelper_SaveAndReadPersonalInformation() throws Exception {
        mSharedPreferencesHelper.savePersonalInfo(mSharedPreferenceEntry);
        SharedPreferenceEntry sharedPreferenceEntry = mSharedPreferencesHelper.getPersonalInfo();
        assertThat(isEquals(mSharedPreferenceEntry, sharedPreferenceEntry), is(true));
    }

    /**
     * 该方法的测试需要修改SharedPreference的部分行为，所以需要用到mock
     */
    @Test
    public void sharedPreferencesHelper_SavePersonalInformationFailed_ReturnsFalse() {
        // Read personal information from a broken SharedPreferencesHelper
        boolean success =
                mMockSharedPreferencesHelper.savePersonalInfo(mSharedPreferenceEntry);
        assertThat("Makes sure writing to a broken SharedPreferencesHelper returns false", success,
                is(false));

    }

    private boolean isEquals(SharedPreferenceEntry sharedPreferenceEntry, SharedPreferenceEntry target){
        return sharedPreferenceEntry.getName().equals(target.getName())&&sharedPreferenceEntry.getEmail().equals(target.getEmail())&&sharedPreferenceEntry.getDateOfBirth().equals(target.getDateOfBirth());
    }

}