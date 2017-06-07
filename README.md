# 前言
　　关于单元测试，在维基百科中，给出了如下定义：
>在计算机编程中，单元测试（英语：Unit Testing）又称为模块测试, 是针对程序模块（软件设计的最小单位）来进行正确性检验的测试工作。程序单元是应用的最小可测试部件。在过程化编程中，一个单元就是单个程序、函数、过程等；对于面向对象编程，最小单元就是方法，包括基类（超类）、抽象类、或者派生类（子类）中的方法。  

# 单元测试
　　android中的单元测试基于JUnit，可分为本地测试和instrumented测试，在项目中对应
- module-name/src/test/java/.  
　　该目录下的代码运行在本地JVM上，其优点是速度快，不需要设备或模拟器的支持，但是无法直接运行含有android系统API引用的测试代码。
- module-name/src/androidTest/java/.  
　　该目录下的测试代码需要运行在android设备或模拟器下面，因此可以使用android系统的API，速度较慢。  
　　

以上两个目录分别执行在JUnit和AndroidJUnitRunner的测试运行环境，两者主要的区别在于是否需要android系统API的依赖。
　　由于android中的大量代码依赖android系统的库，
1.强依赖关系，如在Activity，Service等组件中的方法，其特点是大部分为private方法，并且与其生命周期相关，无法进行本地的JUnit测试，可以进行Ecspreso等UI测试。
2.部分依赖，即依赖注入，该类需要依赖Context等android对象的依赖，可以通过Mock或在androidJunit中获生成对象完成单元测试。
3.纯java代码，不存在对android库的依赖，可以进行单元测试

android sdk中提供了JUnit和AndroidJUnitRunner
android 组件，生命周期等
定位错误
Hamcrest
which lets you create more flexible assertions using the Hamcrest matcher APIs.
## 常用的测试框架和工具
　　在android测试框架中，常用的有以下几个框架和工具类：
- JUnit4
- AndroidJUnitRunner
- Mockito
- Espresso
- Hamcrest

### JUnit4
　　JUnit4是一套基于注解的单元测试框架。在android studio中，编写在test目录下的测试类都是基于该框架实现，该目录下的测试代码运行在本地的JVM上，不需要设备（真机或模拟器）的支持。
　　JUnit4中常用的几个注解：
- BeforeClass
- AfterClass
- Before
- After
- Test
- RunWith
- Suit
　　

对于其它的注解，可以通过查看其[junit4官网](http://junit.org/junit4/)来进一步学习。  
　　在test下添加测试类，对于需要进行测试的方法添加@Test注解，在该方法中使用assert进行判断，为了使assert更加直观，方便，可以使用[Hamcrest library](https://github.com/hamcrest)，通过使用hamcrest的匹配工具，可以让你更灵活的进行测试。 以下是一个最简单的测试类实现：
```java
public class CalculatorTest {

    private Calculator mCalculator;

    @Before
    public void setUp() {
        mCalculator = new Calculator();
    }

    //方法的命名尽量描述详细
    @Test
    public void addTwoNumbers() {
        double resultAdd = mCalculator.add(1d, 1d);
        //使用hamcrest进行assert，直观，易读
        assertThat(resultAdd, is(equalTo(2d)));
    }
}
```
　　当需要传入多个参数进行条件，即__条件覆盖__(参考关于条件的覆盖的说明)时，可以使用@Parameters来进行单个方法的多次不同参数的测试。使用该方法需要如下步骤：

- 1.在测试类上添加@RunWith(Parameterized.class)注解。
- 2.添加构造方法，并将测试的参数作为其构造参数。
- 3.添加获取参数集合的static方法，并在该方法上添加@Parameters注解。
- 4.在需要测试的方法中直接使用成员变量，该变量由JUnit通过构造方法生成。

```java
@RunWith(Parameterized.class)
public class CalculatorWithParameterizedTest {

    private final double mOperandOne;
    private final double mOperandTwo;
    private final double mExpectedResult;

    private Calculator mCalculator;

    public CalculatorWithParameterizedTest(double operandOne, double operandTwo,
            double expectedResult){
        mOperandOne = operandOne;
        mOperandTwo = operandTwo;
        mExpectedResult = expectedResult;
    }

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

    @Test
    public void testAdd_TwoNumbers() {
        double resultAdd = mCalculator.add(mOperandOne, mOperandTwo);
        assertThat(resultAdd, is(equalTo(mExpectedResult)));
    }

}
```

现在目录下存在如下两个Test类：

![多个测试类](https://github.com/booqin/AndroidJunitDemo/raw/master/capture/junit_suite.png)

如果我们需要同时运行两个或多个Test类怎么办？JUnit提供了Suite注解，在对应的测试目录下创建一个空Test类，该类上添加如下注解：

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
```
　　在AndroidJUnitRunner中，通过InstrumentationRegistry来获取Context，并实例化SharedPreferences，然后通过依赖注入来完成SharedPreferencesHelper对象的生成。对于AndroidJUnitRunner更详细的介绍，可以参考android官方文档[测试支持库](https://developer.android.google.cn/topic/libraries/testing-support-library/index.html#AndroidJUnitRunner)。
　　使用AndroidJUnitRunner最大的缺点在于无法在本地JVM运行，直接的结果就是测试速度慢，同时无法执行覆盖测试。所以出现了很多替代方案，比如在设计合理，依赖注入实现的代码，可以使用Mockito来进行本地测试，或者使用第三方测试框架Robolectric等。

### Mockito
　　涉及到android依赖的方法的测试，除了在androidTest使用，还可以通过mock来执行本地测试。使用Mock的目的主要有以下两点：
- 验证这个对象的某些方法的调用情况，调用了多少次，参数是什么等等
- 指定这个对象的某些方法的行为，返回特定的值，或者是执行特定的动作

Mockito是优秀的mock框架之一，使用该框架可以使mock的操作更加简单，直观。  
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
     * 编写Mock相关代码
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

## 单元测试与集成测试

## 关于条件的覆盖
-无限条件

要验证程序正确性，必然要给出所有可能的条件（极限编程），并验证其行为或结果，才算是100%覆盖条件。实际项目中，验证边界条件和一般条件就OK了。

还是上面那个例子，只给出两个条件：a=2,b=1和a=2,b=0，a=2,b=1是一般条件，b=0是边界条件，还有一些边界条件a=NaN，b=NaN等。要验证除法正确性，恐怕得给出无限的条件，实际上，只要验证几个边界条件和一般条件，基本认为代码是正确了。

-有限条件

再举个例子：stateA='a0'、'a1', stateB='b0'、'b1'、'b2'，根据stateA、stateB不同组合输出不同结果，例如a0b0输出a0b0，a0b1输出a0b1，所以，共2*3=6种情况。这时，并不存在边界条件，所以条件都是特定条件，并且条件有限。

这种情况在项目中很常见，以笔者经验，建议单元测试时把所有情况都验证一遍，确保没有遗漏。

## 扩展知识


# 问题
JUnit中涉及到Context或Activity等Android依赖怎么办
网络相关，异步请求