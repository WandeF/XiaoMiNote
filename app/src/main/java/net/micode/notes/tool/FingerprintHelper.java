package net.micode.notes.tool;

import android.content.Context;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

public class FingerprintHelper {

    private final Context mContext;
    private final BiometricPrompt mBiometricPrompt;
    private CancellationSignal mCancellationSignal;
    private CountDownLatch countDownLatch ;
    private FingerprintCallback callback;

    private int authenticationResult = -1;

    public FingerprintHelper(Context context, FingerprintCallback callback) {
        mContext = context;
        mBiometricPrompt = createBiometricPrompt();
        this.callback = callback;
    }

    private BiometricPrompt createBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(mContext);
        BiometricPrompt.AuthenticationCallback callback = createAuthenticationCallback();
        return new BiometricPrompt((FragmentActivity) mContext, executor, callback);
    }

    private BiometricPrompt.AuthenticationCallback createAuthenticationCallback() {
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                // 处理错误情况
                // 返回0表示认证失败
                authenticationResult(0);
                Log.i("finger", "onAuthenticationError");
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                // 处理识别成功情况
                // 返回1表示认证成功
                Log.i("finger", "onAuthenticationSucceeded");
                authenticationResult(1);
            }

            @Override
            public void onAuthenticationFailed() {
                // 处理识别失败情况
                // 返回0表示认证失败
                Log.i("finger", "onAuthenticationFailed");
                authenticationResult(0);
            }
        };
    }

    // 添加一个回调方法，用于返回认证结果


    public boolean startFingerprintAuthentication() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("指纹识别")
                .setSubtitle("使用指纹解锁")
                .setDescription("请使用您的指纹进行身份验证")
                .setNegativeButtonText("取消")
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mCancellationSignal = new CancellationSignal();
        }

        // 在调用 authenticate 之前先重置认证结果
        authenticationResult = -1;
        countDownLatch = new CountDownLatch(1);

        mBiometricPrompt.authenticate(promptInfo);

        // 使用 CountDownLatch 等待认证结果
//        try {
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // 返回认证结果
        return authenticationResult == 1;
    }

    private void authenticationResult(int result) {
        authenticationResult = result;

        if (callback != null) {
            callback.onAuthenticationResult(result == 1);
        }

        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }


    public void cancelFingerprintAuthentication() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (mCancellationSignal != null && !mCancellationSignal.isCanceled()) {
                mCancellationSignal.cancel();
            }
        }
    }
}
