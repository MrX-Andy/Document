```
package org.alex.baselibrary.andview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.alex.rxjava.LimitedCompositeDisposable;
import org.alex.rxjava.lifecycle.LifeCycleEvent;
import org.alex.rxjava.lifecycle.LifecycleHelper;
import org.alex.rxjava.lifecycle.RxLifeCycleEvent;
import org.alex.rxjava.lifecycle.RxLifecycleView;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * 作者：Alex
 * 时间：2017/12/9 20:42
 * 简述：
 * 引入 mStateSaved  参考  http://toughcoder.net/blog/2016/11/28/fear-android-fragment-state-loss-no-more/
 */
public abstract class AndBaseActivity extends AppCompatActivity implements
        LifecycleProvider<LifeCycleEvent>,
        RxLifecycleView {
    protected Context context;
    protected Activity activity;
    protected BehaviorSubject<LifeCycleEvent> lifecycleSubject = BehaviorSubject.create();
    private LimitedCompositeDisposable compositeDisposable;
    private List<Subscription> subscriptionList;
    private boolean mStateSaved;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState == null ? new Bundle() : savedInstanceState);
        context = this;
        activity = this;
        mStateSaved = false;
        lifecycleSubject.onNext(RxLifeCycleEvent.CREATE);
    }

    /**
     * 获取上个页面 传递的数据
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null) {
            onGetIntentData(intent);
        }
    }

    /**
     * 获取启动者通过Intent传递过来的 数据
     */
    @SuppressWarnings("UnusedParameters")
    protected void onGetIntentData(Intent intent) {

    }

    @Override
    protected void onStart() {
        lifecycleSubject.onNext(RxLifeCycleEvent.START);
        mStateSaved = false;
        super.onStart();
    }

    @Override
    protected void onResume() {
        lifecycleSubject.onNext(RxLifeCycleEvent.RESUME);
        mStateSaved = false;
        super.onResume();
    }

    @Override
    protected void onPause() {
        lifecycleSubject.onNext(RxLifeCycleEvent.PAUSE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        lifecycleSubject.onNext(RxLifeCycleEvent.STOP);
        mStateSaved = true;
        super.onStop();
    }

    @Override
    public AndroidLifecycleScopeProvider androidLifecycleScopeProvide() {
        return AndroidLifecycleScopeProvider.from(this);
    }

    @Override
    public AndroidLifecycleScopeProvider androidLifecycleScopeProvide(LifecycleOwner owner) {
        return AndroidLifecycleScopeProvider.from(owner);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Observable<LifeCycleEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(LifeCycleEvent event) {
        return com.trello.rxlifecycle2.RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return com.trello.rxlifecycle2.RxLifecycle.bind(lifecycleSubject, LifecycleHelper.activityLifecycle());
    }

    /**
     * 管理  rx 产生的 订阅信息
     */
    @Override
    public void addDisposable(Disposable disposable) {
        compositeDisposable = (compositeDisposable == null) ? new LimitedCompositeDisposable() : compositeDisposable;
        compositeDisposable.add(disposable);
        compositeDisposable.removeDisposed();
    }

    @Override
    public void addDisposable(Subscription subscription) {
        subscriptionList = (subscriptionList == null) ? new ArrayList<Subscription>() : subscriptionList;
        subscriptionList.add(subscription);
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mStateSaved = true;
        }
    }

    @SuppressWarnings({"SingleStatementInBlock", "SimplifiableIfStatement"})
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!mStateSaved) {
            return super.onKeyDown(keyCode, event);
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (!mStateSaved) {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onDestroy() {
        activity = null;
        context = null;
        lifecycleSubject.onNext(RxLifeCycleEvent.DESTROY);
        if (compositeDisposable != null) {
            compositeDisposable.clear();
            compositeDisposable = null;
        }
        if (subscriptionList != null) {
            for (int i = 0; i < subscriptionList.size(); i++) {
                subscriptionList.get(i).cancel();
            }
            subscriptionList.clear();
            subscriptionList = null;
        }
        super.onDestroy();
    }
}

```