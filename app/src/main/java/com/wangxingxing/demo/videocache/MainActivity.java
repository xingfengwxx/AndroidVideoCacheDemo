package com.wangxingxing.demo.videocache;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "MainActivity";
    public static final String URL = "http://streamoc.music.tc.qq.com/F000002E3MtF0IAMMY.flac?vkey=006915D4380CF1C3E23A39937AF2D32CBDF98F34507B93EDF504E3FB5BFB77282B651AF31CA356AA34EA51B4BB340A65825AACE0950AD769&guid=1923292611&uid=1008611&fromtag=8";
    private String localUrl;
    private MediaPlayer mediaPlayer;
    private HttpProxyCacheServer mCacheServerProxy;

    private Button btnPlay;
    private Button btnPause;
    private Button btnReplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCacheServerProxy = BaseApplication.getProxy(this);
        encryptAndDecryptByDES();
        initView();
        initMediaPlayer();
    }

    private void initView() {
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnReplay = findViewById(R.id.btnReplay);

        btnPlay.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnReplay.setOnClickListener(this);
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "onPrepared:");
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.d(TAG, "onBufferingUpdate: " + percent);
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(TAG, String.format("what=%d, extra=%d", what, extra));
                return false;
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion: ");
            }
        });

    }

    private void play() {
        try {
            mCacheServerProxy.registerCacheListener(mCacheListener, URL);
            localUrl = mCacheServerProxy.getProxyUrl(URL);
            Log.i(TAG, "localUrl: " + localUrl);
            mediaPlayer.setDataSource(localUrl);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pause() {
        mediaPlayer.pause();
    }

    private void rePlay() {
        try {
            mediaPlayer.reset();
            mCacheServerProxy.registerCacheListener(mCacheListener, URL);
            localUrl = BaseApplication.getProxy(this).getProxyUrl(URL);
            Log.i(TAG, "localUrl: " + localUrl);
            mediaPlayer.setDataSource(localUrl);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlay:
                play();
                break;
            case R.id.btnPause:
                pause();
                break;
            case R.id.btnReplay:
                rePlay();
                break;
        }
    }

    //音频缓存进度更新
    private CacheListener mCacheListener = new CacheListener() {
        @Override
        public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
            Log.d(TAG, String.format("onCacheAvailable: percent: %d, filePath: %s, url: %s", percentsAvailable, cacheFile, url));
            //避免重复插入数据
            if (percentsAvailable == 100 && !cacheFile.getPath().endsWith(".download")) {
                //存储到缓存数据库
                Log.i(TAG, "写入数据到数据库");
            }
        }
    };

    private void encryptAndDecryptByDES() {
        String dataRes = "http://streamoc.music.tc.qq.com/F000002E3MtF0IAMMY.flac?vkey=F26090F52D9F1F683A84AD205CC34A0BD0E1D2C8F55D29EE5228A38DC9F0417899B86199CA34EC058506A3A2A532E1D03F16BBDA43AB9DA3&guid=2051337069&uid=1008611&fromtag=8";
//        String dataRes = "test";
        String keyRes = "ashd0303";
        byte[] bytesData = dataRes.getBytes();
        byte[] bytesKey = keyRes.getBytes();
        String enStr = EncryptUtils.encryptDES2HexString(bytesData, bytesKey, "DES/ECB/PKCS5Padding", null);
        Log.i(TAG, "enStr=" + enStr);
        String deStr = new String(EncryptUtils.decryptHexStringDES(enStr, bytesKey, "DES/ECB/PKCS5Padding", null));
        Log.i(TAG, "deStr=" + deStr);

        String base64EnStr = EncodeUtils.base64Encode2String(dataRes.getBytes());
        Log.i(TAG, "Base64Encode=" + base64EnStr);
        Log.i(TAG, "Base64Decode=" + new String(EncodeUtils.base64Decode(base64EnStr)));
    }
}
