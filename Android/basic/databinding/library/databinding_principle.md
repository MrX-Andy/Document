假设调用了  LoginDataBindingImpl#setLoginRespEntity  
```
public void setLoginRespEntity(@Nullable LoginRespEntity LoginRespEntity) {
    this.mLoginRespEntity = LoginRespEntity;
    synchronized(this) {
        mDirtyFlags |= 0x10L;
    }
    notifyPropertyChanged(BR.loginRespEntity);
    super.requestRebind();
}
```

ViewDataBinding#requestRebind  
```
protected void requestRebind() {
    if (mContainingBinding != null) {
        mContainingBinding.requestRebind();
    } else {
        final LifecycleOwner owner = this.mLifecycleOwner;
        if (owner != null) {
            Lifecycle.State state = owner.getLifecycle().getCurrentState();
            if (!state.isAtLeast(Lifecycle.State.STARTED)) {
                return; // wait until lifecycle owner is started
            }
        }
        synchronized (this) {
            if (mPendingRebind) {
                return;
            }
            mPendingRebind = true;
        }
        if (USE_CHOREOGRAPHER) {
            mChoreographer.postFrameCallback(mFrameCallback);
        } else {
            mUIThreadHandler.post(mRebindRunnable);
        }
    }
}

private final Runnable mRebindRunnable = new Runnable() {
    @Override
    public void run() {
        synchronized (this) {
            mPendingRebind = false;
        }
        processReferenceQueue();

        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            // Nested so that we don't get a lint warning in IntelliJ
            if (!mRoot.isAttachedToWindow()) {
                // Don't execute the pending bindings until the View
                // is attached again.
                mRoot.removeOnAttachStateChangeListener(ROOT_REATTACHED_LISTENER);
                mRoot.addOnAttachStateChangeListener(ROOT_REATTACHED_LISTENER);
                return;
            }
        }
        executePendingBindings();
    }
};

public void executePendingBindings() {
    if (mContainingBinding == null) {
        executeBindingsInternal();
    } else {
        mContainingBinding.executePendingBindings();
    }
}
```
LoginDataBindingImpl#executeBindings  
```
@Override
protected void executeBindings() {
    long dirtyFlags = 0;
    synchronized(this) {
        dirtyFlags = mDirtyFlags;
        mDirtyFlags = 0;
    }
    androidx.databinding.ObservableField<java.lang.String> stuEntityPhoneField = null;
    java.lang.String stuEntityPhoneFieldGet = null;
    com.alex.andfun.abundant.experiment.beginner.login.entity.StuEntity stuEntity = mStuEntity;
    com.alex.andfun.abundant.experiment.beginner.login.contract.LoginRespEntity loginRespEntity = mLoginRespEntity;
    java.lang.String loginRespEntityName = null;
    java.lang.String userObservablePhone = null;
    com.alex.andfun.abundant.experiment.beginner.login.entity.UserObservable userObservable = mUserObservable;

    if ((dirtyFlags & 0x45L) != 0) {



            if (stuEntity != null) {
                // read stuEntity.phoneField
                stuEntityPhoneField = stuEntity.getPhoneField();
            }
            updateRegistration(0, stuEntityPhoneField);


            if (stuEntityPhoneField != null) {
                // read stuEntity.phoneField.get()
                stuEntityPhoneFieldGet = stuEntityPhoneField.get();
            }
    }
    if ((dirtyFlags & 0x50L) != 0) {



            if (loginRespEntity != null) {
                // read loginRespEntity.name
                loginRespEntityName = loginRespEntity.getName();
            }
    }
    if ((dirtyFlags & 0x62L) != 0) {



            if (userObservable != null) {
                // read userObservable.phone
                userObservablePhone = userObservable.getPhone();
            }
    }
    // batch finished
    if ((dirtyFlags & 0x62L) != 0) {
        // api target 1

        androidx.databinding.adapters.TextViewBindingAdapter.setText(this.etPhone, userObservablePhone);
        androidx.databinding.adapters.TextViewBindingAdapter.setText(this.tvContent, userObservablePhone);
    }
    if ((dirtyFlags & 0x40L) != 0) {
        // api target 1

        androidx.databinding.adapters.TextViewBindingAdapter.setTextWatcher(this.etPhone, (androidx.databinding.adapters.TextViewBindingAdapter.BeforeTextChanged)null, (androidx.databinding.adapters.TextViewBindingAdapter.OnTextChanged)null, (androidx.databinding.adapters.TextViewBindingAdapter.AfterTextChanged)null, etPhoneandroidTextAttrChanged);
        androidx.databinding.adapters.TextViewBindingAdapter.setTextWatcher(this.etPwd, (androidx.databinding.adapters.TextViewBindingAdapter.BeforeTextChanged)null, (androidx.databinding.adapters.TextViewBindingAdapter.OnTextChanged)null, (androidx.databinding.adapters.TextViewBindingAdapter.AfterTextChanged)null, etPwdandroidTextAttrChanged);
        androidx.databinding.adapters.TextViewBindingAdapter.setTextWatcher(this.tvMessage, (androidx.databinding.adapters.TextViewBindingAdapter.BeforeTextChanged)null, (androidx.databinding.adapters.TextViewBindingAdapter.OnTextChanged)null, (androidx.databinding.adapters.TextViewBindingAdapter.AfterTextChanged)null, tvMessageandroidTextAttrChanged);
    }
    if ((dirtyFlags & 0x45L) != 0) {
        // api target 1

        androidx.databinding.adapters.TextViewBindingAdapter.setText(this.etPwd, stuEntityPhoneFieldGet);
        androidx.databinding.adapters.TextViewBindingAdapter.setText(this.tvMessage, stuEntityPhoneFieldGet);
    }
    if ((dirtyFlags & 0x50L) != 0) {
        // api target 1

        androidx.databinding.adapters.TextViewBindingAdapter.setText(this.tvLogin, loginRespEntityName);
    }
}
```