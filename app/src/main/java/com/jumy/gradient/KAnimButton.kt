package com.jumy.gradient

//import com.jumy.animadddelbutton.AnimButton
//import com.jumy.animadddelbutton.IOnClickListener

/**
 * Created by Jumy on 17/1/9 14:57.
 * Copyright (c) 2016, yygutn@gmail.com All Rights Reserved.
 */
//class KClickListener : IOnClickListener {
//    private var _onAddSuccess: ((count: Int) -> Unit)? = null
//    private var _onDelSuccess: ((count: Int) -> Unit)? = null
//    private var _onAddFailed: ((count: Int, type: IOnClickListener.FailType) -> Unit)? = null
//    private var _onDelFailed: ((count: Int, type: IOnClickListener.FailType) -> Unit)? = null
//    override fun onAddSuccess(count: Int) {
//        _onAddSuccess?.invoke(count)
//    }
//
//    override fun onAddFailed(count: Int, failType: IOnClickListener.FailType) {
//        _onAddFailed?.invoke(count, failType)
//    }
//
//    override fun onDelSuccess(count: Int) {
//        _onDelSuccess?.invoke(count)
//    }
//
//    override fun onDelFailed(count: Int, failType: IOnClickListener.FailType) {
//        _onDelFailed?.invoke(count, failType)
//    }
//
//    fun onAddSuccess(listener: (count: Int) -> Unit) {
//        _onAddSuccess = listener
//    }
//
//    fun onAddFailed(listener: (count: Int, type: IOnClickListener.FailType) -> Unit) {
//        _onAddFailed = listener
//    }
//
//    fun onDelSuccess(listener: (count: Int) -> Unit) {
//        _onDelSuccess = listener
//    }
//
//    fun onDelFailed(listener: (count: Int, type: IOnClickListener.FailType) -> Unit) {
//        _onDelFailed = listener
//    }
//}
//
//fun AnimButton.clickWatcher(init: KClickListener.() -> Unit) {
//    setOnClickListener(KClickListener().apply(init))
//}