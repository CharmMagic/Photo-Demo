package com.ysf.photo;
////////////////////////////////////////////////////////////////////
//                          _ooOoo_                               //
//                         o8888888o                              //
//                         88" . "88                              //
//                         (| ^_^ |)                              //
//                         O\  =  /O                              //
//                      ____/`---'\____                           //
//                    .'  \\|     |//  `.                         //
//                   /  \\|||  :  |||//  \                        //
//                  /  _||||| -:- |||||-  \                       //
//                  |   | \\\  -  /// |   |                       //
//                  | \_|  ''\---/''  |   |                       //
//                  \  .-\__  `-`  ___/-. /                       //
//                ___`. .'  /--.--\  `. . ___                     //
//              ."" '<  `.___\_<|>_/___.'  >'"".                  //
//            | | :  `- \`.;`\ _ /`;.`/ - ` : | |                 //
//            \  \ `-.   \_ __\ /__ _/   .-` /  /                 //
//      ========`-.____`-.___\_____/___.-`____.-'========         //
//                           `=---='                              //
//      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //
//         佛祖保佑       永无BUG     永不修改                  //
////////////////////////////////////////////////////////////////////

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jph.takephoto.model.TImage;

import java.util.List;

/**
 * Created by yishangfei on 2017/3/14 0014.
 * 个人主页：http://yishangfei.me
 * Github:https://github.com/yishangfei
 */
public class RepairAdapter extends BaseQuickAdapter<TImage, BaseViewHolder> {
    public RepairAdapter(List<TImage> data) {
        super(R.layout.activity_repair_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TImage item) {
        Glide.with(mContext)
                .load(item.getCompressPath())
                .crossFade()
                .into((ImageView) helper.getView(R.id.repair_image));
        helper.addOnClickListener(R.id.repair_del);
    }

    @Override
    protected void setFullSpan(RecyclerView.ViewHolder holder) {
    }
}