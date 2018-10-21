package com.android.kevin.shuizu.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.YYZJ
import com.android.kevin.shuizu.entities.YYZJInfo
import com.android.kevin.shuizu.entities.YYZJInfoListRes
import com.android.kevin.shuizu.entities.getInterface
import com.android.kevin.shuizu.ui.CallZJActivity
import com.android.kevin.shuizu.ui.LoginActivity
import com.android.kevin.shuizu.ui.MemoSetActivity
import com.android.kevin.shuizu.ui.ReservationDetailsActivity
import com.android.kevin.shuizu.utils.CardPagerAdapter
import com.android.kevin.shuizu.utils.ShadowTransformer
import com.android.shuizu.myutillibrary.fragment.BaseFragment
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_service.*
import org.jetbrains.anko.toast

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/17/017.
 */
class ServiceFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_service, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getYYZJ()
    }

    private fun initViews(list: ArrayList<YYZJInfo>) {
        val mCardAdapter = CardPagerAdapter()
        mCardAdapter.setSource(list)

        val mCardShadowTransformer = ShadowTransformer(serviceViewPager, mCardAdapter)
        mCardShadowTransformer.enableScaling(true)

        serviceViewPager.adapter = mCardAdapter
        serviceViewPager.setPageTransformer(false, mCardShadowTransformer)
        serviceViewPager.offscreenPageLimit = 3

        mCardAdapter.onCallDetails = object : CardPagerAdapter.OnCallDetails {
            override fun onCallDetails(item: YYZJInfo, view: View) {
                val intent = Intent(view.context, ReservationDetailsActivity::class.java)
                intent.putExtra("id", item.id)
                startActivity(intent)
            }

            override fun onCallZJ(item: YYZJInfo, view: View) {
                val intent = Intent(view.context, CallZJActivity::class.java)
                intent.putExtra("id", item.id)
                startActivity(intent)
            }

        }

        network.setOnClickListener {

        }

        memoSet.setOnClickListener {
            startActivity(Intent(it.context, MemoSetActivity::class.java))
        }
    }

    //获取养鱼专家列表
    private fun getYYZJ() {
        val map = mapOf(Pair("", ""))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val yYZJInfoListRes = Gson().fromJson(result, YYZJInfoListRes::class.java)
                initViews(yYZJInfoListRes.retRes)
            }

            override fun onError(context: Context, error: String) {
                context.toast(error)
            }

            override fun onLoginErr(context: Context) {
                context.LoginErrDialog(DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }, false).postRequest(activity as Context, YYZJ.getInterface(), map)
    }
}