package me.boqin.androidjunitdemo.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import me.boqin.androidjunitdemo.CalculatorTest;
import me.boqin.androidjunitdemo.CalculatorWithParameterizedTest;

/**
 * 通过Suite来运行多个Test类
 * Created by Boqin on 2017/6/5.
 * Modified by Boqin
 *
 * @Version
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({CalculatorTest.class, CalculatorWithParameterizedTest.class})
public class UnitTestSuite {
}
