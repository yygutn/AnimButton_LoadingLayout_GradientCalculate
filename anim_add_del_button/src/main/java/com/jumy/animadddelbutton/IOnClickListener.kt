package com.jumy.animadddelbutton

/**
 * Created by Jumy on 17/1/9 11:31.
 * Copyright (c) 2016, yygutn@gmail.com All Rights Reserved.
 */
interface IOnClickListener {
    enum class FailType {
        COUNT_MAX,
        COUNT_MIN
    }

    fun onAddSuccess(count: Int)
    fun onAddFailed(count: Int, failType: FailType)
    fun onDelSuccess(count: Int)
    fun onDelFailed(count: Int, failType: FailType)
}