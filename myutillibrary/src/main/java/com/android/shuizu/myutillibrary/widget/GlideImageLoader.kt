package com.android.shuizu.myutillibrary.widget

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.youth.banner.loader.ImageLoader
import java.io.File

/**
 * YuMeiChaYin
 * Created by 蔡雨峰 on 2017/9/9/009.
 */

class GlideImageLoader : ImageLoader() {

    override fun displayImage(context: Context, path: Any, imageView: ImageView) {
        /**
         * 注意：
         * 1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
         * 2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
         * 传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
         * 切记不要胡乱强转！
         */
        //        VolleyUtil volleyUtil = VolleyUtil.getInstance(context);
        //        volleyUtil.getImage(imageView, (String) path);
        when (path) {
            is Int -> Picasso.with(context).load(path).into(imageView)
            is String -> {
                if (path.contains("http")) {
                    Picasso.with(context).load(path).into(imageView)
                } else {
                    Picasso.with(context).load(File(path)).into(imageView)
                }
            }
        }
    }

    //    //提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
    //    @Override
    //    public ImageView createImageView(Context context) {
    //        //使用fresco，需要创建它提供的ImageView，当然你也可以用自己自定义的具有图片加载功能的ImageView
    //        NetworkImageView networkImageView = new NetworkImageView(context);
    //        return networkImageView;
    //    }
}
