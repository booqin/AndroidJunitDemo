# 前言
　　本文将介绍在Android Studio中，android单元测试的介绍和实现。相关代码托管在github上的[AndroidJunitDemo](https://github.com/booqin/AndroidJunitDemo)中，涉及到的用例代码收集于google官方提供的测试用例[android-testing](https://github.com/googlesamples/android-testing)，同时进行了简化和修改。你可以从该demo中学习单元测试简单的使用。在工程中，包含两个模块，一个实现计算器功能的CalculationActivity，另外一个是PersonlInfoActivity，该类可以编辑姓名，邮箱和生日等信息，并保存到SharePreferences中，同时提供了两个模块的单元测试。

# 单元测试
　　关于单元测试，在维基百科中，给出了如下定义：
>在计算机编程中，单元测试（英语：Unit Testing）又称为模块测试, 是针对程序模块（软件设计的最小单位）来进行正确性检验的测试工作。程序单元是应用的最小可测试部件。在过程化编程中，一个单元就是单个程序、函数、过程等；对于面向对象编程，最小单元就是方法，包括基类（超类）、抽象类、或者派生类（子类）中的方法。  

　　android中的单元测试基于JUnit，可分为本地测试和instrumented测试，在项目中对应
- module-name/src/test/java/.  
　　该目录下的代码运行在本地JVM上，其优点是速度快，不需要设备或模拟器的支持，但是无法直接运行含有android系统API引用的测试代码。
- module-name/src/androidTest/java/.  
　　该目录下的测试代码需要运行在android设备或模拟器下面，因此可以使用android系统的API，速度较慢。
　　

![目录](https://github.com/booqin/AndroidJunitDemo/raw/master/capture/test-types.png)

以上分别执行在JUnit和AndroidJUnitRunner的测试运行环境，两者主要的区别在于是否需要android系统API的依赖。  
　　在实际开发过程中，我们应该尽量用JUnit实现本地JVM的单元测试，而项目中的代码大致可分为以下三类：
- 1.强依赖关系，如在Activity，Service等组件中的方法，其特点是大部分为private方法，并且与其生命周期相关，无法直接进行单元测试，可以进行Ecspreso等UI测试。
- 2.部分依赖，代码实现依赖注入，该类需要依赖Context等android对象的依赖，可以通过Mock或其它第三方框架实现JUnit单元测试或使用androidJunitRunner进行单元测试。
- 3.纯java代码，不存在对android库的依赖，可以进行JUnit单元测试

## 常用的测试框架
　　在android测试框架中，常用的有以下几个框架和工具类：
- JUnit4
- AndroidJUnitRunner
- Mockito
- Espresso
　　

关于单元测试框架的选择，可以参考下图：

![android单元测试](https://github.com/booqin/AndroidJunitDemo/raw/master/capture/android_unit_test.png)

### JUnit4
　　JUnit4是一套基于注解的单元测试框架。在android studio中，编写在test目录下的测试类都是基于该框架实现，该目录下的测试代码运行在本地的JVM上，不需要设备（真机或模拟器）的支持。  
　　JUnit4中常用的几个注解：
- @BeforeClass 测试类里所有用例运行之前，运行一次这个方法。方法必须是public static void
- @AfterClass 与BeforeClass对应
- @Before 在每个用测试例运行之前都运行一次。
- @After 与Before对应
- @Test 指定该方法为测试方法，方法必须是public void
- @RunWith 测试类名之前，用来确定这个类的测试运行器
　　

对于其它的注解，可以通过查看[junit4官网](http://junit.org/junit4/)来进一步学习。  
　　在test下添加测试类，对于需要进行测试的方法添加@Test注解，在该方法中使用assert进行判断，为了使assert更加直观，方便，可以使用[Hamcrest library](https://github.com/hamcrest)，通过使用hamcrest的匹配工具，可以让你更灵活的进行测试。 以下是一个最简单的测试类_CalculatorTest_的实现：
```java
public class CalculatorTest {

    /** 计算功能类 */
    private Calculator mCalculator;

    @Before
    public void setUp() {
        mCalculator = new Calculator();
    }

    /**
     * 方法的命名尽量描述详细
     * 测试两个数相加
     */
    @Test
    public void addTwoNumbers() {
        double resultAdd = mCalculator.add(1d, 1d);
        //使用hamcrest进行assert，直观，易读
        assertThat(resultAdd, is(equalTo(2d)));
    }
    ……
}
```
　　当需要传入多个参数进行条件，即条件覆盖时，可以使用@Parameters来进行单个方法的多次不同参数的测试，对应Demo中的_CalculatorWithParameterizedTest_测试类，使用该方法需要如下步骤：

- 1.在测试类上添加@RunWith(Parameterized.class)注解。
- 2.添加构造方法，并将测试的参数作为其构造参数。
- 3.添加获取参数集合的static方法，并在该方法上添加@Parameters注解。
- 4.在需要测试的方法中直接使用成员变量，该变量由JUnit通过构造方法生成。

```java
@RunWith(Parameterized.class)
public class CalculatorWithParameterizedTest {

    /** 参数的变量 */
    private final double mOperandOne;
    private final double mOperandTwo;
    /** 期待值 */
    private final double mExpectedResult;
    /** 计算类 */
    private Calculator mCalculator;

    /**
     * 构造方法，框架可以自动填充参数
     */
    public CalculatorWithParameterizedTest(double operandOne, double operandTwo,
            double expectedResult){
        mOperandOne = operandOne;
        mOperandTwo = operandTwo;
        mExpectedResult = expectedResult;
    }

    /**
     * 需要测试的参数和对应结果
     */
    @Parameterized.Parameters
    public static Collection<Object[]> initData(){
        return Arrays.asList(new Object[][]{
                {0, 0, 0},
                {0, -1, -1},
                {2, 2, 4},
                {8, 8, 16},
                {16, 16, 32},
                {32, 0, 32},
                {64, 64, 128}});
    }


    @Before
    public void setUp() {
        mCalculator = new Calculator();
    }

    /**
     * 使用参数组测试加的相关操作
     */
    @Test
    public void testAdd_TwoNumbers() {
        double resultAdd = mCalculator.add(mOperandOne, mOperandTwo);
        assertThat(resultAdd, is(equalTo(mExpectedResult)));
    }

}
```

现在目录下存在如下两个Test类：

![多个测试类](https://github.com/booqin/AndroidJunitDemo/raw/master/capture/junit_suite.png)

如果我们需要同时运行两个或多个Test类怎么办？JUnit提供了Suite注解，在对应的测试目录下创建一个空Test类，如Demo里的_UnitTestSuite_，该类上添加如下注解：

- @RunWith(Suite.class)：配置Runner运行环境。
- @Suite.SuiteClasses({A.class, B.class})：添加需要一起运行的测试类。

```java
	@RunWith(Suite.class)
	@Suite.SuiteClasses({CalculatorTest.class, CalculatorWithParameterizedTest.class})
	public class UnitTestSuite {
	}
```
　　目前为止已经可以完成简单的单元测试了，但在android中，方法中使用到android系统api是一件司空见惯的事，比如Context，Parcelable，SharedPreferences等等。而在本地JVM中无法调用这些接口，因此，我们就需要使用__AndroidJUnitRunner__来完成这些方法的测试
### AndroidJUnitRunner
　　当单元测试中涉及到大量的android系统库的调用时，你可以通过该方案类完成测试。使用方法是在androidTest目录下创建测试类，在该类上添加@RunWith(AndroidJUnit4.class)注解。  
　　在Demo中androidTest目录下的SharedPreferencesHelperTest测试类，该类对SharedPreferencesHelper进行了单元测试，其方法内部涉及到了SharedPreferences，该类属于android系统的api，因此无法直接在test中运行。部分实现代码如下：
```java
@RunWith(AndroidJUnit4.class)
public class SharedPreferencesHelperTest {

    private static final String TEST_NAME = "Test name";

    private static final String TEST_EMAIL = "test@email.com";

    private static final Calendar TEST_DATE_OF_BIRTH = Calendar.getInstance();

    private SharedPreferenceEntry mSharedPreferenceEntry;

    private SharedPreferencesHelper mSharedPreferencesHelper;

    private SharedPreferences mSharePreferences;

    /** 上下文 */
    private Context mContext;
    ……
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

    ……
}
@RunWith(AndroidJUnit4.class)
public class SharedPreferencesHelperTest {

    private static final String TEST_NAME = "Test name";

    private static final String TEST_EMAIL = "test@email.com";

    private static final Calendar TEST_DATE_OF_BIRTH = Calendar.getInstance();

    private SharedPreferenceEntry mSharedPreferenceEntry;

    private SharedPreferencesHelper mSharedPreferencesHelper;

    private SharedPreferences mSharePreferences;

    /** 上下文 */
    private Context mContext;
    ……
    @Before
    public void setUp() throws Exception {
        //获取application的context
        mContext = InstrumentationRegistry.getTargetContext();
        //实例化SharedPreferences
        mSharePreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        mSharedPreferenceEntry = new SharedPreferenceEntry(TEST_NAME, TEST_DATE_OF_BIRTH, TEST_EMAIL);
        //实例化SharedPreferencesHelper，依赖注入SharePreferences
        mSharedPreferencesHelper = new SharedPreferencesHelper(mSharePreferences);

        //以下是在mock的相关操作，模拟commit失败
        mMockSharePreferences = Mockito.mock(SharedPreferences.class);
        mMockBrokenEditor = Mockito.mock(SharedPreferences.Editor.class);
        when(mMockSharePreferences.edit()).thenReturn(mMockBrokenEditor);
        when(mMockBrokenEditor.commit()).thenReturn(false);
        mMockSharedPreferencesHelper = new SharedPreferencesHelper(mMockSharePreferences);
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
    ……
}
```
　　在AndroidJUnitRunner中，通过InstrumentationRegistry来获取Context，并实例化SharedPreferences，然后通过依赖注入来完成SharedPreferencesHelper对象的生成。对于AndroidJUnitRunner更详细的介绍，可以参考android官方文档[测试支持库](https://developer.android.google.cn/topic/libraries/testing-support-library/index.html#AndroidJUnitRunner)。
　　使用AndroidJUnitRunner最大的缺点在于无法在本地JVM运行，直接的结果就是测试速度慢，同时无法执行覆盖测试。所以出现了很多替代方案，比如在设计合理，依赖注入实现的代码，可以使用Mockito来进行本地测试，或者使用第三方测试框架Robolectric等。

### Mockito
　　涉及到android依赖的方法的测试，除了在androidTest使用，还可以通过mock来执行本地测试。使用Mock的目的主要有以下两点：
- 验证这个对象的某些方法的调用情况，调用了多少次，参数是什么等等
- 指定这个对象的某些方法的行为，返回特定的值，或者是执行特定的动作

Mockito是优秀的mock框架之一，使用该框架可以使mock的操作更加简单，直观。  
　　要使用Mockito，需要添加如下依赖：
```java
    dependencies {
        testCompile 'junit:junit:4.12'
        // 如果要使用Mockito，你需要添加此条依赖库
        testCompile 'org.mockito:mockito-core:1.+'
        // 如果你要使用Mockito 用于 Android instrumentation tests，那么需要你添加以下三条依赖库
        androidTestCompile 'org.mockito:mockito-core:1.+'
        androidTestCompile "com.google.dexmaker:dexmaker:1.2"
        androidTestCompile "com.google.dexmaker:dexmaker-mockito:1.2"
    }
```
　　在__AndroidJUnitRunner__介绍中的对于SharedPreferencesHelper的测试，由于其依赖注入的设计，我们可以方便的去mock一个SharePreferences来执行本地的测试。在Demo中的test目录下的SharedPreferencesHelperWithMockTest类即通过mockito来完成测试的，主要代码如下：
```java
@RunWith(MockitoJUnitRunner.class)
public class SharedPreferencesHelperWithMockTest {

    private static final String TEST_NAME = "Test name";
    private static final String TEST_EMAIL = "test@email.com";
    private static final Calendar TEST_DATE_OF_BIRTH = Calendar.getInstance();
    private SharedPreferencesHelper mSharedPreferencesHelper;
    private SharedPreferenceEntry mSharedPreferenceEntry;
    ……
    @Mock
    SharedPreferences mMockSharedPreferences;
    @Mock
    SharedPreferences.Editor mMockEditor;
    ……
    @Before
    public void setUp() throws Exception {
        mSharedPreferenceEntry = new SharedPreferenceEntry(TEST_NAME, TEST_DATE_OF_BIRTH, TEST_EMAIL);
        mSharedPreferencesHelper = new SharedPreferencesHelper(mockSharePreferences());
        ……
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
    ……
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
    ……
}
```

### Espresso
　　在Demo中，除了单元测试的用例，还提供了一个CalculatorInstrumentationTest测试类，该类使用Espresso，一个官方提供了UI测试框架。注意，UI测试不属于单元测试的范畴。通过Espresso的使用，可以编写简洁、运行可靠的自动化UI测试。详细的使用可以参考[测试支持库](https://developer.android.google.cn/topic/libraries/testing-support-library/index.html#UIAutomator)中关于Espresso的使用介绍。
```java
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CalculatorInstrumentationTest {

    /**
     * 在测试中运行Activity
     * A JUnit {@link Rule @Rule} to launch your activity under test. This is a replacement
     * for {@link ActivityInstrumentationTestCase2}.
     * <p>
     * Rules are interceptors which are executed for each test method and will run before
     * any of your setup code in the {@link Before @Before} method.
     * <p>
     * {@link ActivityTestRule} will create and launch of the activity for you and also expose
     * the activity under test. To get a reference to the activity you can use
     * the {@link ActivityTestRule#getActivity()} method.
     */
    @Rule
    public ActivityTestRule<CalculatorActivity> mActivityRule = new ActivityTestRule<>(
            CalculatorActivity.class);
    ……
    private void performOperation(int btnOperationResId, String operandOne,
            String operandTwo, String expectedResult) {
        // 指定输入框中输入文本，同时关闭键盘
        onView(withId(R.id.operand_one_edit_text)).perform(typeText(operandOne),
                closeSoftKeyboard());
        onView(withId(R.id.operand_two_edit_text)).perform(typeText(operandTwo),
                closeSoftKeyboard());

        // 获取特定按钮执行点击事件
        onView(withId(btnOperationResId)).perform(click());

        // 获取文本框中显示的结果
        onView(withId(R.id.operation_result_text_view)).check(matches(withText(expectedResult)));
    }

}
```
你可以运行CalculatorInstrumentationTest测试类，会有一个直观的认识。
## 运行单元测试
　　在Android Studio中，可以通过以下两种方式运行单元测试：
- 手动运行
- 通过指令运行

### 1.手动运行
　　在Android Studio中，对指定的测试类右键，选择对应的RUN或DEBUG操作选项即可运行，如下图：

![运行选项](https://github.com/booqin/AndroidJunitDemo/raw/master/capture/run_junit_test0.png)

　　图中第三个为覆盖测试，即运行所有的test下的单元测试，并显示单元测试的覆盖率。如果需要保存测试结果，可以在结果框中点击Export Test Results按钮:

![运行选项](https://github.com/booqin/AndroidJunitDemo/raw/master/capture/run_junit_test1.png)

　　结果会被保存到项目的目录下，可以通过浏览器打开查看：

![运行选项](https://github.com/booqin/AndroidJunitDemo/raw/master/capture/run_junit_test2.png)

### 2.指令运行
　　在Terminal输入_gradle testDebugUnitTest_或_gradle testReleaseUnitTest_指令来分别运行debug和release版本的unit testing，在执行的结果可以在_xxx\project\app\build\reports\tests\testReleaseUnitTest_中查看：

![运行选项](https://github.com/booqin/AndroidJunitDemo/raw/master/capture/run_junit_test3.png)

# 其它

## 关于异步操作的单元测试
　　在实际的android开发过程中，经常涉及到异步操作，比如网络请求，Rxjava的线程调度等。在单元测试中，往往测试方法执行往了，异步操作还没介绍，这就导致了无法顺利的执行单元测试操作。其解决方法可以提供CountDownLatch类来阻塞测试方法的线程，当异步操作完成后（通过回调）来唤醒继续执行测试，获取结果。其实对于网络请求这种操作应该使用Mock来替代，因为你的单元测试的结果不应受网络的影响，不需要关注网络是否正常，服务器是否崩溃，而应该把关注点放在单元本身的操作。
## 单元测试，集成测试，UI测试
- UI测试是测试到交互和视觉，以及操作的结果是否符合预期。可以通过Espresso，UI Automator等框架，或者人工测试。
- 集成测试是基于单元测试，将多个单元测试组装起来进行测试，实际测试往往会运行慢，依赖过多导致集成测试非常费时。
- 单元测试仅针对最小单元，在面向对象中，单元指的是方法，包括基类（超类）、抽象类、或者派生类（子类）中的方法。

三者的在实际应用中可以通过Test Pyramid（Martin Fowler的总结）来衡量：

![Test Pyramid](https://github.com/booqin/AndroidJunitDemo/raw/master/capture/test-pyramid.png)

　　所以对于测试，在开放过程中，我们（开发者）需要把更多的精力放在单元测试上。

# 扩展阅读
- android关于测试的官方文档[Testing Apps on Android](https://developer.android.google.cn/training/testing/index.html)
- 对于UI测试，google还提供了一个[UI Automator](https://developer.android.google.cn/training/testing/ui-testing/uiautomator-testing.html)测试框架
- 关于单元测试，[小创作](http://chriszou.com/) 的系列文章可以帮助你更好的学习和使用相关技术
- [Android单元测试 - 如何开始？](https://segmentfault.com/a/1190000006811141)该文简单的介绍了单元测试相关的几个概念。
