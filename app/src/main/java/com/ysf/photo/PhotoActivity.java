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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yishangfei on 2017/3/30 0030.
 * 个人主页：http://yishangfei.me
 * Github:https://github.com/yishangfei
 */
public class PhotoActivity extends AppCompatActivity implements TakePhoto.TakeResultListener,InvokeListener {
    //没有继承TakePhotoActivity 所写
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;


    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;

    private List<TImage> selectMedia = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);  //没有继承TakePhotoActivity 所写
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        //设置RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        photoAdapter = new PhotoAdapter(this, onAddPicListener,onPicClickListener);
        photoAdapter.setSelectMax(3);
        recyclerView.setAdapter(photoAdapter);

    }

    //加号的点击事件
    private PhotoAdapter.onAddPicListener onAddPicListener = new PhotoAdapter.onAddPicListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            switch (type) {
                case 0:
                    new AlertView("上传图片", null, "取消", null,
                            new String[]{"拍照", "从相册中选择"},
                            PhotoActivity.this, AlertView.Style.ActionSheet, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            TakePhoto takePhoto = getTakePhoto();
                            //获取TakePhoto图片路径
                            File file = new File(Environment.getExternalStorageDirectory(), "/photo/" + System.currentTimeMillis() + ".jpg");
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
                    }).show();
                    break;
                case 1:
                    // 删除图片
                    selectMedia.remove(position);
                    photoAdapter.notifyItemRemoved(position);
                    break;
            }
        }
    };

    //图片点击事件
    private PhotoAdapter.onPicClickListener onPicClickListener =new PhotoAdapter.onPicClickListener() {
        @Override
        public void onPicClick(View view, int position) {
            startPhotoActivity(PhotoActivity.this, (ImageView) view);
        }
    };

    //图片点击所做的操作
    public void startPhotoActivity(Context context, ImageView imageView) {
        Intent intent = new Intent(context, DragPhotoActivity.class);
        int location[] = new int[2];
        imageView.getLocationOnScreen(location);
        intent.putExtra("left", location[0]);
        intent.putExtra("top", location[1]);
        intent.putExtra("height", imageView.getHeight());
        intent.putExtra("width", imageView.getWidth());

        intent.putExtra("path", (Serializable) selectMedia);
        context.startActivity(intent);
        overridePendingTransition(0,0);
    }

    @Override
    public void takeCancel() {
    }

    @Override
    public void takeFail(TResult result, String msg) {
    }

    @Override
    public void takeSuccess(TResult result) {
        showImg(result.getImages());
    }

    //图片成功后返回执行的方法
    private void showImg(ArrayList<TImage> images) {
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).getCompressPath() != null) {
                selectMedia.add(images.get(i));
            }
        }
        if (selectMedia != null) {
            photoAdapter.setList(selectMedia);
            photoAdapter.notifyDataSetChanged();
        }
    }

    //设置Takephoto 使用TakePhoto自带的相册   照片旋转角度纠正
    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setWithOwnGallery(true);
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());
    }

    //设置takephoto的照片使用压缩
    private void configCompress(TakePhoto takePhoto) {
        CompressConfig config;
        config = new CompressConfig.Builder()
                .setMaxSize(102400)
                .setMaxPixel(800)
                .enableReserveRaw(true)
                .create();
        takePhoto.onEnableCompress(config, false);
    }


    //没有继承TakePhotoActivity 所写
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    //没有继承TakePhotoActivity 所写
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    //没有继承TakePhotoActivity 所写
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type=PermissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
        PermissionManager.handlePermissionsResult(this,type,invokeParam,this);
    }

    /**
     *  获取TakePhoto实例
     *  没有继承TakePhotoActivity 所写
     */
    public TakePhoto getTakePhoto(){
        if (takePhoto==null){
            takePhoto= (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this,this));
        }
        return takePhoto;
    }

    //没有继承TakePhotoActivity 所写
    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type=PermissionManager.checkPermission(TContextWrap.of(this),invokeParam.getMethod());
        if(PermissionManager.TPermissionType.WAIT.equals(type)){
            this.invokeParam=invokeParam;
        }
        return type;
    }
}
