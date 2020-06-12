package com.google.zxing.fragment;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.baza.qrcode.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.camera.CameraManager;
import com.google.zxing.decoding.CaptureActivityHandler;
import com.google.zxing.decoding.InactivityTimer;
import com.google.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by Vincent.Lei on 2017/11/3.
 * Title：
 * Note：
 */

public class CaptureFragment extends Fragment implements SurfaceHolder.Callback {
    //    public static final String INTENT_EXTRA_KEY_QR_SCAN = "qr_scan_result";
    private static final long VIBRATE_DURATION = 200L;
    //    private static final int REQUEST_CODE_SCAN_GALLERY = 100;
    private static final float BEEP_VOLUME = 0.10f;

    public interface IQRCodeResultListener {
        void onQRCodeResult(String result);
    }

    private SurfaceView surfaceView;
    private ViewfinderView viewfinderView;
    private MediaPlayer mMediaPlayer;

    private CaptureActivityHandler mHandler;
    private InactivityTimer mInactivityTimer;
    private Vector<BarcodeFormat> mDecodeFormats;
    private IQRCodeResultListener mQRCodeResultListener;
    //    private Bitmap mScanBitmap;
    private String mCharacterSet;
    //    private String mPhotoPath;
    private boolean mHasSurface;
    private boolean mPlayBeep;
    private boolean mVibrate;

    public void setQRCodeResultListener(IQRCodeResultListener qRCodeResultListener) {
        this.mQRCodeResultListener = qRCodeResultListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view_root = inflater.inflate(R.layout.fragment_scanner, null);
        viewfinderView = view_root.findViewById(R.id.viewfinder_content);
        surfaceView = view_root.findViewById(R.id.scanner_view);
        return view_root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CameraManager.init(getActivity().getApplication());
        mHasSurface = false;
        mInactivityTimer = new InactivityTimer(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (mHasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        mDecodeFormats = null;
        mCharacterSet = null;

        mPlayBeep = true;
        AudioManager audioService = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
            mPlayBeep = false;
        initBeepSound();
        mVibrate = true;
    }

    public void onPause() {
        super.onPause();
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        if (CameraManager.get() != null)
            CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        mInactivityTimer.shutdown();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        mMediaPlayer = null;
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!mHasSurface) {
            mHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHasSurface = false;
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (mHandler == null) {
            mHandler = new CaptureActivityHandler(this, mDecodeFormats, mCharacterSet);
        }
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (mPlayBeep && mMediaPlayer == null) {
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(beepListener);
            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mMediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                mMediaPlayer = null;
            }
        }
    }

    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    public void handleDecode(Result result, Bitmap barcode) {
        mInactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (mQRCodeResultListener != null)
            mQRCodeResultListener.onQRCodeResult(resultString);
//        //FIXME
//        if (TextUtils.isEmpty(resultString)) {
//            //Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
//        } else {
//            Intent resultIntent = new Intent();
//            Bundle bundle = new Bundle();
//            bundle.putString(INTENT_EXTRA_KEY_QR_SCAN, resultString);
//            // 不能使用Intent传递大于40kb的bitmap，可以使用一个单例对象存储这个bitmap
//            resultIntent.putExtras(bundle);
//            getActivity().setResult(Activity.RESULT_OK, resultIntent);
//        }
    }

    private void playBeepSoundAndVibrate() {
        if (mPlayBeep && mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        if (mVibrate) {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

//    public Result scanningImage(String path) {
//        if (TextUtils.isEmpty(path)) {
//            return null;
//        }
//        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
//        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true; // 先获取原大小
//        mScanBitmap = BitmapFactory.decodeFile(path, options);
//        options.inJustDecodeBounds = false; // 获取新的大小
//        int sampleSize = (int) (options.outHeight / (float) 200);
//        if (sampleSize <= 0)
//            sampleSize = 1;
//        options.inSampleSize = sampleSize;
//        mScanBitmap = BitmapFactory.decodeFile(path, options);
//        RGBLuminanceSource source = new RGBLuminanceSource(mScanBitmap);
//        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
//        QRCodeReader reader = new QRCodeReader();
//        try {
//            return reader.decode(bitmap1, hints);
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//        } catch (ChecksumException e) {
//            e.printStackTrace();
//        } catch (FormatException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
