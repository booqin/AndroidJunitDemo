/*
 * Copyright 2015, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.boqin.androidjunitdemo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * 一个带参方法使用多组参数进行测试，使用Parameterized，进行条件覆盖
 * Created by Boqin on 2017/6/5.
 * Modified by Boqin
 *
 * @Version
 */
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
