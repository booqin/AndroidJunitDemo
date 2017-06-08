package me.boqin.androidjunitdemo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import android.content.SharedPreferences;

/**
 * 在单元测试中使用Mock，依赖Mockito开源库，Mock可以让依赖Android库的类通过模拟数据或行为实现在本地JVM上运行单元测试。
 * 但由于需要编写Mock数据，所以会增加代码量
 * Created by Boqin on 2017/6/6.
 * Modified by Boqin
 *
 * @Version
 */
@RunWith(MockitoJUnitRunner.class)
public class SharedPreferencesHelperWithMockTest {


    private static final String TEST_NAME = "Test name";

    private static final String TEST_EMAIL = "test@email.com";

    private static final Calendar TEST_DATE_OF_BIRTH = Calendar.getInstance();

    private SharedPreferencesHelper mSharedPreferencesHelper;
    private SharedPreferencesHelper mBrokenSharedPreferencesHelper;
    private SharedPreferenceEntry mSharedPreferenceEntry;


    /** Mock变量，使用@Mock注解完成实例化 */
    @Mock
    SharedPreferences mMockSharedPreferences;

    @Mock
    SharedPreferences.Editor mMockEditor;

    @Mock
    SharedPreferences mMockBrokenSharedPreferences;

    @Mock
    SharedPreferences.Editor mMockBrokenEditor;

    @Before
    public void setUp() throws Exception {
        mSharedPreferenceEntry = new SharedPreferenceEntry(TEST_NAME, TEST_DATE_OF_BIRTH, TEST_EMAIL);
        mSharedPreferencesHelper = new SharedPreferencesHelper(mockSharePreferences());
        mBrokenSharedPreferencesHelper = new SharedPreferencesHelper(mockBrokenMockSharedPreference());
    }

    /**
     * 测试保存数据是否成功
     */
    @Test
    public void sharedPreferencesHelper_SavePersonalInformation() throws Exception {
        assertThat(mSharedPreferencesHelper.savePersonalInfo(mSharedPreferenceEntry), is(true));
    }
    /**
     * 测试保存数据，然后获取数据是否成功
     */
    @Test
    public void sharedPreferencesHelper_SaveAndReadPersonalInformation() throws Exception {
        mSharedPreferencesHelper.savePersonalInfo(mSharedPreferenceEntry);
        SharedPreferenceEntry sharedPreferenceEntry = mSharedPreferencesHelper.getPersonalInfo();
        assertThat(isEquals(mSharedPreferenceEntry, sharedPreferenceEntry), is(true));
    }

    /**
     * 保存数据失败的相关操作
     */
    @Test
    public void sharedPreferencesHelper_SavePersonalInformationFailed_ReturnsFalse() {
        // Read personal information from a broken SharedPreferencesHelper
        boolean success =
                mBrokenSharedPreferencesHelper.savePersonalInfo(mSharedPreferenceEntry);
        assertThat("Makes sure writing to a broken SharedPreferencesHelper returns false", success,
                is(false));

    }
    /**
     * 编写Mock相关代码，代码中mock了SharedPreferences类的getXxx的相关操作，
     * 均返回SharedPreferenceEntry对象的值，同时在代码中使用到了commit和edit，都需要在方法中进行mock实现
     * Creates a mocked SharedPreferences.
     */
    private SharedPreferences mockSharePreferences(){
        when(mMockSharedPreferences.getString(eq(SharedPreferencesHelper.KEY_NAME), anyString()))
                .thenReturn(mSharedPreferenceEntry.getName());
        when(mMockSharedPreferences.getString(eq(SharedPreferencesHelper.KEY_EMAIL), anyString()))
                .thenReturn(mSharedPreferenceEntry.getEmail());
        when(mMockSharedPreferences.getLong(eq(SharedPreferencesHelper.KEY_DOB), anyLong()))
                .thenReturn(mSharedPreferenceEntry.getDateOfBirth().getTimeInMillis());

        when(mMockEditor.commit()).thenReturn(true);

        when(mMockSharedPreferences.edit()).thenReturn(mMockEditor);
        return mMockSharedPreferences;
    }

    /**
     * Creates a mocked SharedPreferences that fails when writing.
     */
    private SharedPreferences mockBrokenMockSharedPreference() {
        // Mocking a commit that fails.
        when(mMockBrokenEditor.commit()).thenReturn(false);

        // Return the broken MockEditor when requesting it.
        when(mMockBrokenSharedPreferences.edit()).thenReturn(mMockBrokenEditor);

        return mMockBrokenSharedPreferences;
    }

    private boolean isEquals(SharedPreferenceEntry sharedPreferenceEntry, SharedPreferenceEntry target){
        return sharedPreferenceEntry.getName().equals(target.getName())&&sharedPreferenceEntry.getEmail().equals(target.getEmail())&&sharedPreferenceEntry.getDateOfBirth().equals(target.getDateOfBirth());
    }



}