package com.yydcdut.note.presenters.login.impl;

import android.app.Activity;
import android.content.Context;

import com.yydcdut.note.R;
import com.yydcdut.note.entity.user.IUser;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.rx.RxUser;
import com.yydcdut.note.presenters.login.IUserCenterPresenter;
import com.yydcdut.note.utils.NetworkUtils;
import com.yydcdut.note.utils.YLog;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.login.IUserCenterView;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 15/11/16.
 */
public class UserCenterPresenterImpl implements IUserCenterPresenter {
    private IUserCenterView mUserCenterView;
    private Context mContext;
    private Activity mActivity;

    private RxUser mRxUser;
    private boolean[] mInitState;

    @Inject
    public UserCenterPresenterImpl(Activity activity, @ContextLife("Activity") Context context, RxUser rxUser) {
        mActivity = activity;
        mContext = context;
        mInitState = new boolean[2];
        mRxUser = rxUser;
        mRxUser.isLoginQQ()
                .subscribe(aBoolean -> mInitState[0] = aBoolean, (throwable -> YLog.e(throwable)));
        mRxUser.isLoginEvernote()
                .subscribe(aBoolean -> mInitState[1] = aBoolean, (throwable -> YLog.e(throwable)));

    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void attachView(IView iView) {
        mUserCenterView = (IUserCenterView) iView;
        initQQ();
        initEvernote();
    }

    @Override
    public void detachView() {

    }

    @Override
    public boolean checkInternet() {
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            //没有网络
            mUserCenterView.showSnackBar(mContext.getResources().getString(R.string.toast_no_connection));
            return false;
        }
        return true;
    }

    @Override
    public void loginQQ() {
        mRxUser.isLoginQQ()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (!aBoolean) {
                        mRxUser.loginQQ(mActivity)
                                .doOnSubscribe(() -> mUserCenterView.showProgressBar())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<IUser>() {
                                    @Override
                                    public void onCompleted() {
                                        mUserCenterView.hideProgressBar();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        mUserCenterView.showSnackBarWithAction(mContext.getResources().getString(R.string.toast_fail),
                                                mContext.getResources().getString(R.string.toast_retry), (() -> loginQQ()));
                                    }

                                    @Override
                                    public void onNext(IUser iUser) {
                                        initQQ();
                                        mUserCenterView.showQQInfoInFrag(iUser.getName());
                                        mUserCenterView.showSnackBar(mContext.getResources().getString(R.string.toast_success));
                                    }
                                });
                    }
                }, (throwable -> YLog.e(throwable)));
    }

    @Override
    public void loginEvernote() {
        mRxUser.isLoginEvernote()
                .subscribe(aBoolean -> {
                    if (!aBoolean) {
                        mRxUser.loginEvernote(mActivity).subscribe();
                    }
                }, (throwable -> YLog.e(throwable)));
    }

    @Override
    public void onEvernoteLoginFinished(boolean successful) {
        if (successful) {
            initEvernote();
            mRxUser.saveEvernote()
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(iUser -> {
                        if (iUser != null) {
                            mUserCenterView.showEvernoteInFrag(true, iUser.getName());
                        } else {
                            mUserCenterView.showEvernoteInFrag(true, mContext.getResources().getString(R.string.user_failed));
                        }
                        mUserCenterView.showSnackBar(mContext.getResources().getString(R.string.toast_success));
                    }, (throwable -> YLog.e(throwable)));
        } else {
            mUserCenterView.showSnackBarWithAction(mContext.getResources().getString(R.string.toast_fail),
                    mContext.getResources().getString(R.string.toast_retry), (() -> mRxUser.loginEvernote(mActivity).subscribe((aBoolean -> {
                    }), (throwable -> YLog.e(throwable)))));
        }
    }

    @Override
    public void initQQ() {
        mRxUser.getQQ()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(iUser -> mUserCenterView.showQQInfo(iUser.getName(), iUser.getImagePath()),
                        (throwable -> YLog.e(throwable)));
    }

    @Override
    public void initEvernote() {
        mRxUser.isLoginEvernote()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> mUserCenterView.showEvernote(aBoolean), (throwable -> YLog.e(throwable)));
    }

    @Override
    public void finish() {
        mRxUser.isLoginQQ()
                .subscribe(aBoolean -> {
                    if (mInitState[0] != aBoolean) {
                        mUserCenterView.finishActivityWithResult(RESULT_DATA_USER);
                    } else {
                        mRxUser.isLoginEvernote()
                                .subscribe(aBoolean1 -> {
                                    if (mInitState[1] != aBoolean) {
                                        mUserCenterView.finishActivityWithResult(RESULT_DATA_USER);
                                    } else {
                                        mUserCenterView.finishActivityWithResult(-1);
                                    }
                                });
                    }
                }, (throwable -> YLog.e(throwable)));
    }
}
