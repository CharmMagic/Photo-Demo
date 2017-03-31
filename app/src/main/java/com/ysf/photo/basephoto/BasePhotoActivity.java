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

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.ysf.photo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yishangfei on 2017/3/31 0031.
 * 个人主页：http://yishangfei.me
 * Github:https://github.com/yishangfei
 */
public class BasePhotoActivity  extends TakePhotoActivity implements OnItemClickListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private BasePhotoAdapter basePhotoAdapter;
    private List<TImage> selectMedia = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        recyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        basePhotoAdapter = new BasePhotoAdapter(selectMedia);
        View footerView = getLayoutInflater().inflate(R.layout.footer_view, (ViewGroup) recyclerView.getParent(), false);
        footerView.setOnClickListener(this);
        basePhotoAdapter.addFooterView(footerView, 0);
        recyclerView.setAdapter(basePhotoAdapter);
        recyclerView.addOnItemTouchListener(listener);
    }

    @Override
    public void onItemClick(Object o, int position) {
        TakePhoto takePhoto = getTakePhoto();
        //获取TakePhoto图片路径
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);

        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);
        switch (position) {
            case 0:
                takePhoto.onPickFromCapture(imageUri);
                break;
            case 1:
                //设置最多几张
                takePhoto.onPickMultiple(3);
                break;
        }
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        showImg(result.getImages());
    }

    private void showImg(ArrayList<TImage> images) {
        selectMedia = images;
        if (images.size() < 3) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            basePhotoAdapter = new BasePhotoAdapter(images);
            View footerView = getLayoutInflater().inflate(R.layout.footer_view, (ViewGroup) recyclerView.getParent(), false);
            footerView.setOnClickListener(this);
            basePhotoAdapter.addFooterView(footerView, 0);
            recyclerView.setAdapter(basePhotoAdapter);
            recyclerView.addOnItemTouchListener(listener);
        } else {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            basePhotoAdapter = new BasePhotoAdapter(images);
            recyclerView.setAdapter(basePhotoAdapter);
            recyclerView.addOnItemTouchListener(listener);
        }


    }

    //加号点击事件
    @Override
    public void onClick(View v) {
        new AlertView("上传图片", null, "取消", null,
                new String[]{"拍照", "从相册中选择"},
                BasePhotoActivity.this, AlertView.Style.ActionSheet, this).show();
    }


    //删除按钮事件
    private OnItemChildClickListener listener = new OnItemChildClickListener() {
        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            Toast.makeText(BasePhotoActivity.this, "", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSimpleItemChildClick(final BaseQuickAdapter adapter, View view, final int position) {
            switch (view.getId()){
                case R.id.photo_del:
                    selectMedia.remove(position);
                    Toast.makeText(BasePhotoActivity.this, "111", Toast.LENGTH_SHORT).show();
                    basePhotoAdapter.notifyItemRemoved(position);
                    break;
            }
        }
    };

    //takephoto配置
    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setWithOwnGallery(true);
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());
    }

    private void configCompress(TakePhoto takePhoto) {
        CompressConfig config;
        config = new CompressConfig.Builder()
                .setMaxSize(102400)
                .setMaxPixel(800)
                .enableReserveRaw(true)
                .create();
        takePhoto.onEnableCompress(config, false);
    }
}

