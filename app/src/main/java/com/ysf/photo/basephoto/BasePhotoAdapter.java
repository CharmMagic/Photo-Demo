package com.ysf.photo.basephoto;
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
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jph.takephoto.model.TImage;
import com.orhanobut.logger.Logger;
import com.ysf.photo.R;

import java.util.List;

/**
 * Created by yishangfei on 2017/3/14 0014.
 * 个人主页：http://yishangfei.me
 * Github:https://github.com/yishangfei
 */
public class BasePhotoAdapter extends BaseQuickAdapter<TImage, BaseViewHolder> {
    private int selectMax;
    public final int TYPE_CAMERA = 1;
    public final int TYPE_PICTURE = 2;


    public BasePhotoAdapter(List<TImage> data,int selectMax) {
        super(R.layout.activity_photo_item, data);
        this.selectMax=selectMax;
    }

    @Override
    public int getItemCount() {
        if (mData.size() < selectMax) {
            return mData.size() + 1;
        } else {
            return mData.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAddItem(position)) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PICTURE;
        }
    }

    private boolean isShowAddItem(int position) {
        int size = mData.size() == 0 ? 0 : mData.size();
        return position == size;
    }


    @Override
    protected void convert(BaseViewHolder helper, TImage item) {
        Logger.d(helper.getLayoutPosition());
        Logger.d(helper.getAdapterPosition());
        if (getItemViewType(helper.getAdapterPosition()) == TYPE_CAMERA) {
            helper.setImageResource(R.id.photo_image,R.mipmap.icon_addpic)
            .addOnClickListener(R.id.photo_image);
            helper.getView(R.id.photo_del).setVisibility(View.INVISIBLE);
        } else {
            helper.getView(R.id.photo_del).setVisibility(View.VISIBLE);
            helper.addOnClickListener(R.id.photo_image)
                    .addOnClickListener(R.id.photo_del);
            Glide.with(mContext)
                    .load(item.getCompressPath())
                    .crossFade()
                    .into((ImageView) helper.getView(R.id.photo_image));
        }
    }
}