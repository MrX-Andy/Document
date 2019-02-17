```
package org.alex.rxjava.lifecycle;

import com.trello.rxlifecycle2.OutsideLifecycleException;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.uber.autodispose.LifecycleEndedException;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 作者：Alex
 * 时间：2017/8/15 23:00
 * 简述：
 */
public class LifecycleHelper {

    public static final Function<LifeCycleEvent, LifeCycleEvent> CORRESPONDING_EVENTS =
            new Function<LifeCycleEvent, LifeCycleEvent>() {
                @Override
                public LifeCycleEvent apply(LifeCycleEvent activityEvent) throws Exception {
                    if (RxLifeCycleEvent.CREATE == activityEvent) {
                        return RxLifeCycleEvent.DESTROY;
                    }
                    if (RxLifeCycleEvent.START == activityEvent) {
                        return RxLifeCycleEvent.STOP;
                    }
                    if (RxLifeCycleEvent.RESUME == activityEvent) {
                        return RxLifeCycleEvent.PAUSE;
                    }
                    if (RxLifeCycleEvent.PAUSE == activityEvent) {
                        return RxLifeCycleEvent.STOP;
                    }
                    if (RxLifeCycleEvent.STOP == activityEvent) {
                        return RxLifeCycleEvent.DESTROY;
                    }
                    throw new LifecycleEndedException("Cannot bind to Activity lifecycle after destroy.");
                }

            };

    public static Function<LifeCycleEvent, LifeCycleEvent> activityLifecycle() {
        return ACTIVITY_LIFECYCLE;
    }

    /**
     * FragmentEvent提供
     */
    public static Function<LifeCycleEvent, LifeCycleEvent> fragmentLifecycle() {
        return FRAGMENT_LIFECYCLE;
    }

    private static final Function<LifeCycleEvent, LifeCycleEvent> ACTIVITY_LIFECYCLE =
            new Function<LifeCycleEvent, LifeCycleEvent>() {
                @Override
                public LifeCycleEvent apply(@NonNull LifeCycleEvent lastEvent) throws Exception {
                    if (lastEvent.equals(RxLifeCycleEvent.CREATE) || lastEvent.equals(RxLifeCycleEvent.STOP)) {
                        return RxLifeCycleEvent.DESTROY;
                    } else if (lastEvent.equals(RxLifeCycleEvent.START) || lastEvent.equals(RxLifeCycleEvent.PAUSE)) {
                        return RxLifeCycleEvent.STOP;
                    } else if (lastEvent.equals(RxLifeCycleEvent.RESUME)) {
                        return RxLifeCycleEvent.PAUSE;
                    } else if (lastEvent.equals(RxLifeCycleEvent.DESTROY)) {
                        throw new OutsideLifecycleException("Cannot bind to Activity lifecycle when outside of it.");
                    } else {
                        throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
                    }
                }
            };
    /**
     * 仿RxLifecycle对FragmentEvent的定义
     */
    private static final Function<LifeCycleEvent, LifeCycleEvent> FRAGMENT_LIFECYCLE =
            new Function<LifeCycleEvent, LifeCycleEvent>() {
                @Override
                public LifeCycleEvent apply(LifeCycleEvent lastEvent) throws Exception {

                    if (lastEvent.equals(RxLifeCycleEvent.ATTACH) || lastEvent.equals(RxLifeCycleEvent.DESTROY)) {
                        return RxLifeCycleEvent.DETACH;
                    } else if (lastEvent.equals(RxLifeCycleEvent.CREATE) || lastEvent.equals(RxLifeCycleEvent.DESTROY_VIEW)) {
                        return RxLifeCycleEvent.DESTROY;
                    } else if (lastEvent.equals(RxLifeCycleEvent.CREATE_VIEW) || lastEvent.equals(RxLifeCycleEvent.STOP)) {
                        return RxLifeCycleEvent.DESTROY_VIEW;
                    } else if (lastEvent.equals(RxLifeCycleEvent.START) || lastEvent.equals(RxLifeCycleEvent.PAUSE)) {
                        return RxLifeCycleEvent.STOP;
                    } else if (lastEvent.equals(RxLifeCycleEvent.RESUME)) {
                        return RxLifeCycleEvent.PAUSE;
                    } else if (lastEvent.equals(RxLifeCycleEvent.DETACH)) {
                        throw new OutsideLifecycleException("Cannot bind to Fragment lifecycle when outside of it.");
                    } else {
                        throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
                    }
                }
            };
}

```