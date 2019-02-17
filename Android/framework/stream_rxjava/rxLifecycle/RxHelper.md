```
package org.alex.rxjava;


import android.util.Log;

import com.alex.andfun.baselibrary.rxjava.ObtainWrap2DataFunction;
import com.alex.andfun.constant.C;
import com.alex.andfun.model.Wrapper;
import com.trello.rxlifecycle2.LifecycleTransformer;

import org.alex.model.ObtainDispatcher;
import org.alex.model.ObtainException;
import org.alex.retrofit.adapter.HttpTagReceiver;
import org.alex.rxjava.lifecycle.RxLifeCycleEvent;
import org.alex.rxjava.lifecycle.RxLifecycleView;
import org.alex.util.LogTrack;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者：Alex
 * 时间：2017/4/11 21:42
 * 简述：
 */
@SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef", "WeakerAccess", "unused"})
public class RxHelper {

    /**
     * 基本配置
     */
    @NonNull
    public static <T> ObservableTransformer<T, T> defaultTransformer(final RxLifecycleView view, final ObtainDispatcher obtainDispatcher) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .compose(RxHelper.<T>rxLifecycleTransformer(view))
                        .compose(RxHelper.<T>addLifecycleTransformer(view))
                        .compose(RxHelper.<T>schedulersTransformer())
                        .compose(RxHelper.<T>obtainStartFinishTransformer(obtainDispatcher))
                        ;
            }
        };
    }

    /**
     * 在 onDestroy 中 停止
     */
    public static <T> LifecycleTransformer<T> rxLifecycleTransformer(RxLifecycleView view) {
        return view.bindUntilEvent(RxLifeCycleEvent.DESTROY);
    }

    public static <T> ObservableTransformer<T, T> addLifecycleTransformer(final RxLifecycleView view) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        addDisposable(view, disposable);
                    }
                });
            }
        };
    }


    @NonNull
    public static <T> ObservableTransformer<T, T> schedulersTransformer() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .unsubscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }


    /**
     * 基本配置
     */
    @NonNull
    public static <T> ObservableTransformer<T, T> obtainStartFinishTransformer(final ObtainDispatcher obtainDispatcher) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.doOnSubscribe(RxHelper.obtainStartConsumer(obtainDispatcher))
                        .doOnTerminate(RxHelper.obtainTerminateAction(obtainDispatcher))
                        .doFinally(RxHelper.obtainFinishAction(obtainDispatcher));
            }
        };
    }

    public static <T> Consumer<T> obtainStartConsumer(final ObtainDispatcher obtainDispatcher) {
        return new Consumer<T>() {
            @Override
            public void accept(@NonNull T result) throws Exception {
                LogTrack.printMainContext(" 整条事件流开始了", Log.DEBUG);
                obtainStart(obtainDispatcher);
            }
        };
    }

    /**
     * 组合事件的 整体事件流结束的回调(包含 onError onCompleted)
     */
    public static Action obtainTerminateAction(final ObtainDispatcher obtainDispatcher) {
        return new Action() {
            @Override
            public void run() throws Exception {
                //LogTrack.w(" 整条事件流结束了（obtainTerminateAction）");
                obtainFinish(obtainDispatcher);
            }
        };
    }

    /**
     * 组合事件的 整体事件流结束的回调(包含 onError onCompleted)
     */
    public static Action obtainFinishAction(final ObtainDispatcher obtainDispatcher) {

        return new Action() {
            @Override
            public void run() throws Exception {
                LogTrack.d(" 整条事件流结束了（obtainFinishAction）");
                obtainFinish(obtainDispatcher);
            }
        };
    }

    /**
     * 统一 错误处理
     */
    public static Consumer<Throwable> obtainErrorConsumer(final ObtainDispatcher obtainDispatcher) {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogTrack.e(throwable);
                if (throwable instanceof ObtainException) {
                    obtainFail(obtainDispatcher, (ObtainException) throwable);
                } else {
                    obtainFail(obtainDispatcher, new ObtainException("404", "当前网络不稳定"));
                }
            }
        };
    }

    /**
     * 处理  Observable＜WrapperBean＜DataBean＞＞
     */
    @NonNull
    public static <T, R> ObservableTransformer<T, R> wrap2DataTransformer(final ObtainDispatcher obtainDispatcher) {
        return new ObservableTransformer<T, R>() {
            @Override
            public ObservableSource<R> apply(@NonNull Observable<T> tObservable) {
                return tObservable.filter(RxHelper.obtainErrorPredicate(obtainDispatcher))
                        .flatMap(new ObtainWrap2DataFunction<T, R>())
                        .doOnError(obtainErrorConsumer(obtainDispatcher))
                        .onErrorResumeNext(new ObtainErrorResumeFunction<R>());
            }
        };
    }

    /**
     * 处理  Observable＜WrapperBean＞
     */
    @NonNull
    public static <T> ObservableTransformer<T, T> wrapTransformer(final ObtainDispatcher obtainDispatcher) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> tObservable) {
                return tObservable.filter(RxHelper.obtainErrorPredicate(obtainDispatcher))
                        .doOnError(obtainErrorConsumer(obtainDispatcher))
                        .onErrorResumeNext(new ObtainErrorResumeFunction<T>());
            }
        };
    }

    private static <T> Predicate<T> obtainErrorPredicate(final ObtainDispatcher obtainDispatcher) {
        return new Predicate<T>() {
            @Override
            public boolean test(@NonNull T result) throws Exception {
                if (result instanceof HttpTagReceiver) {
                    HttpTagReceiver tagReceiver = (HttpTagReceiver) result;
                    if (HttpTagReceiver.HTTP_STATUS_ERROR.equals(tagReceiver.getResponseStatus())) {
                        ObtainException obtainException = new ObtainException(tagReceiver.getResponseStatus(), "网络状态不好");
                        obtainException.setRequestTag(tagReceiver.getRequestTag());
                        obtainFail(obtainDispatcher, obtainException);
                        return false;
                    }
                }
                if (result instanceof Wrapper) {
                    Wrapper wrapper = (Wrapper) result;
                    if (!C.CodeEnum.success.equalsIgnoreCase(wrapper.getCode())) {
                        obtainFail(obtainDispatcher, new ObtainException(wrapper.getCode(), wrapper.getMessage()));
                        return false;
                    }
                } else {
                    obtainFail(obtainDispatcher, new ObtainException("404", "网络状态不好"));
                    return false;
                }
                return true;
            }
        };
    }

    /**
     * 获取数据 出现异常
     */
    public static void obtainFail(ObtainDispatcher obtainDispatcher, ObtainException ex) {
        if (obtainDispatcher != null) {
            obtainDispatcher.onObtainFail(ex);
        }
    }

    /**
     * 获取数据 出现异常
     */
    public static void obtainStart(ObtainDispatcher obtainDispatcher) {
        if (obtainDispatcher != null) {
            obtainDispatcher.onObtainStart();
        }
    }

    /**
     * 获取数据 出现异常
     */
    public static void obtainFinish(ObtainDispatcher obtainDispatcher) {
        if (obtainDispatcher != null) {
            obtainDispatcher.onObtainFinish();
        }
    }

    public static void addDisposable(RxLifecycleView view, Disposable disposable) {
        if (view != null) {
            view.addDisposable(disposable);
        }
    }
}

```