### FragmentManagerImpl  
INVALID_STATE = -1;   // Invalid state used as a null value.  
INITIALIZING = 0;     // Not yet created.  
CREATED = 1;          // Created.  
ACTIVITY_CREATED = 2; // The activity has finished its creation.  
STOPPED = 3;          // Fully created, not started.  
STARTED = 4;          // Created and started, not resumed.  
RESUMED = 5;          // Created started and resumed.  
分析：  
![Fragment状态变迁图](../ImageFiles/f_mState_001.jpg)  
activity.onCreate → fragmentManagerImpl.dispatchCreate  

第一次进来  dispatchCreate() f.mState = 0，newState = 1；  
第一次进来  dispatchActivityCreated() f.mState = 1，newState = 2；  
第一次进来  dispatchStart() f.mState = 2，newState = 4；  
第一次进来  dispatchResume() f.mState = 4，newState = 5；  

被回收时  dispatchPause() f.mState = 5，newState = 4；  
被回收时  dispatchStop() f.mState = 4，newState = 3；  
被回收时  dispatchDestroyView() f.mState = 3，newState = 1；  
被回收时  dispatchDestroy() f.mState = 1，newState = 0；  
  
重启二次进来  dispatchCreate() f.mState = 0，newState = 1；  
重启二次进来  dispatchActivityCreated() f.mState = 1，newState = 2；  
重启二次进来  dispatchStart() f.mState = 2，newState = 4；  
重启二次进来  dispatchResume() f.mState = 4，newState = 5；   
◑ FragmentManagerImpl#moveToState  
```
void moveToState(Fragment f, int newState, int transit, int transitionStyle, boolean keepActive) {
    // ... 首次创建 或者 内存重启
    if (f.mState < newState) {
        switch (f.mState) {  
            case Fragment.INITIALIZING:
                if (f.mSavedFragmentState != null) {
                    f.mSavedViewState = f.mSavedFragmentState.getSparseParcelableArray(FragmentManagerImpl.VIEW_STATE_TAG);
                    f.mTarget = getFragment(f.mSavedFragmentState,FragmentManagerImpl.TARGET_STATE_TAG);
                    f.mUserVisibleHint = f.mSavedFragmentState.getBoolean(FragmentManagerImpl.USER_VISIBLE_HINT_TAG, true);
                    if (!f.mUserVisibleHint) {
                        f.mDeferStart = true;
                        if (newState > Fragment.STOPPED) {
                            newState = Fragment.STOPPED;
                        }
                    }
                }
                f.mActivity = mActivity;
                f.mParentFragment = mParent;
                f.mCalled = false;
                f.onAttach(mActivity);
                if (f.mParentFragment == null) {
                    mActivity.onAttachFragment(f);
                }
                // 如果 fragment.setRetainInstance(true) ，则跳过 onCreate 方法
                if (!f.mRetaining) {
                    f.performCreate(f.mSavedFragmentState);
                }
                f.mRetaining = false;
                if (f.mFromLayout) {
                    f.mView = f.performCreateView(f.getLayoutInflater(f.mSavedFragmentState), null, f.mSavedFragmentState);
                    if (f.mView != null) {
                        f.mView.setSaveFromParentEnabled(false);
                        if (f.mHidden) f.mView.setVisibility(View.GONE);
                        f.onViewCreated(f.mView, f.mSavedFragmentState);
                    }
                }
            case Fragment.CREATED:
                if (newState > Fragment.CREATED) {
                    if (!f.mFromLayout) {
                        ViewGroup container = null;
                        if (f.mContainerId != 0) {
                            container = (ViewGroup)mContainer.findViewById(f.mContainerId);
                            if (container == null && !f.mRestored) {
                                throwException(new IllegalArgumentException();
                            }
                        }
                        f.mContainer = container;
                        f.mView = f.performCreateView(f.getLayoutInflater(f.mSavedFragmentState), container, f.mSavedFragmentState);
                        if (f.mView != null) {
                            f.onViewCreated(f.mView, f.mSavedFragmentState);
                        }
                    }
                    f.performActivityCreated(f.mSavedFragmentState);
                    if (f.mView != null) {
                        f.restoreViewState(f.mSavedFragmentState);
                    }
                    f.mSavedFragmentState = null;
                }
            case Fragment.ACTIVITY_CREATED:
            case Fragment.STOPPED:
                if (newState > Fragment.STOPPED) {
                    f.performStart();
                }
            case Fragment.STARTED:
                if (newState > Fragment.STARTED) {
                    f.mResumed = true;
                    f.performResume();
                    f.mSavedFragmentState = null;
                    f.mSavedViewState = null;
                }
        }
    } else if (f.mState > newState) {
        // ... 被回收
        switch (f.mState) {
            case Fragment.RESUMED:
                if (newState < Fragment.RESUMED) {
                    f.performPause();
                    f.mResumed = false;
                }
            case Fragment.STARTED:
                if (newState < Fragment.STARTED) {
                    f.performStop();
                }
            case Fragment.STOPPED:
            case Fragment.ACTIVITY_CREATED:
                if (newState < Fragment.ACTIVITY_CREATED) {
                    if (f.mView != null) {
                        if (!mActivity.isFinishing() && f.mSavedViewState == null) {
                            saveFragmentViewState(f);
                        }
                    }
                    f.performDestroyView();
                }
            case Fragment.CREATED:
                if (newState < Fragment.CREATED) {
                    if (f.mAnimatingAway != null) {
                        f.mStateAfterAnimating = newState;
                        newState = Fragment.CREATED;
                    } else {
                        if (!f.mRetaining) {
                            f.performDestroy();
                        }
                        f.mCalled = false;
                        f.onDetach();
                        if (!f.mCalled) {
                            throw new SuperNotCalledException();
                        }
                        if (!keepActive) {
                            if (!f.mRetaining) {
                                makeInactive(f);
                            } else {
                                f.mActivity = null;
                                f.mParentFragment = null;
                                f.mFragmentManager = null;
                                f.mChildFragmentManager = null;
                            }
                        }
                    }
                }
        }
    }
    f.mState = newState;
}
```


### FragmentHostCallback  
◑ final FragmentController mFragments = FragmentController.createController(new HostCallbacks());

◑ FragmentActivity.HostCallbacks  
```
class HostCallbacks extends FragmentHostCallback<FragmentActivity> {
    public HostCallbacks() {
        super(FragmentActivity.this /*fragmentActivity*/);
    }
}
```
◑ FragmentHostCallback#FragmentHostCallback    
```
FragmentHostCallback(Activity activity, Context context, Handler handler,
        int windowAnimations) {
    mActivity = activity;
    mContext = context;
    mHandler = handler;
    mWindowAnimations = windowAnimations;
}
```

