package com.android.kevin.shuizu.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.kevin.shuizu.R
import com.android.shuizu.myutillibrary.fragment.BaseFragment

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/15/015.
 */
class StoreFragment : BaseFragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_store, container, false)
    }
}