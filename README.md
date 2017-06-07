# 前言
　　在维基百科中，给出了如下定义：
>在计算机编程中，单元测试（英语：Unit Testing）又称为模块测试, 是针对程序模块（软件设计的最小单位）来进行正确性检验的测试工作。程序单元是应用的最小可测试部件。在过程化编程中，一个单元就是单个程序、函数、过程等；对于面向对象编程，最小单元就是方法，包括基类（超类）、抽象类、或者派生类（子类）中的方法。  

　　android中的单元测试基于JUnit，可分为本地测试和instrumented测试，在项目中对应module-name/src/test/java/.和module-name/src/androidTest/java/.目录，两者主要的区别在于是否需要android环境的依赖。
# 单元测试
android sdk中提供了JUnit和AndroidJUnitRunner
android 组件，生命周期等
定位错误
Hamcrest
which lets you create more flexible assertions using the Hamcrest matcher APIs.

## JUnit
　　只在jvm环境生效，不需要设备（真机或模拟器）的支持

## 条件覆盖
无限条件

要验证程序正确性，必然要给出所有可能的条件（极限编程），并验证其行为或结果，才算是100%覆盖条件。实际项目中，验证边界条件和一般条件就OK了。

还是上面那个例子，只给出两个条件：a=2,b=1和a=2,b=0，a=2,b=1是一般条件，b=0是边界条件，还有一些边界条件a=NaN，b=NaN等。要验证除法正确性，恐怕得给出无限的条件，实际上，只要验证几个边界条件和一般条件，基本认为代码是正确了。

有限条件

再举个例子：stateA='a0'、'a1', stateB='b0'、'b1'、'b2'，根据stateA、stateB不同组合输出不同结果，例如a0b0输出a0b0，a0b1输出a0b1，所以，共2*3=6种情况。这时，并不存在边界条件，所以条件都是特定条件，并且条件有限。

这种情况在项目中很常见，以笔者经验，建议单元测试时把所有情况都验证一遍，确保没有遗漏。

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

# android中的单元测试
由于android中的大量代码依赖android系统的库，
1.强依赖关系，如在Activity，Service等组件中的方法，其特点是大部分为private方法，并且与其生命周期相关，无法进行本地的JUnit测试，可以进行Ecspreso等UI测试。
2.部分依赖，即依赖注入，该类需要依赖Context等android对象的依赖，可以通过Mock或在androidJunit中获生成对象完成单元测试。
3.纯java代码，不存在对android库的依赖，可以进行单元测试

# 问题
JUnit中涉及到Context或Activity等Android依赖怎么办
网络相关，异步请求