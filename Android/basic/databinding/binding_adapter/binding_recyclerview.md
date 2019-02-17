###### 关联RecyclerView

> AccountMessageActivity
```
@Route(path = "${M.Router.group}AccountMessage")
class AccountMessageActivity : SimpleActivity<AccountMessageContract.ViewModel>(), AccountMessageContract.View {
    override fun layoutResId() = R.layout.module_account_message
    override fun createViewModel(): AccountMessageContract.ViewModel = AccountMessageViewModel(this)
    override fun onCreateData(savedInstanceState: Bundle) {
        viewModel.onCreateData(savedInstanceState)
    }

}
```

> AccountMessageViewModel
```
class AccountMessageViewModel(view: AccountMessageContract.View) :
        SimpleViewModel<AccountMessageContract.View, AccountMessageContract.Presenter>(view),
        AccountMessageContract.ViewModel {
    private lateinit var dataBinding: ModuleAccountMessageBinding
    private val adapter: AccountMessageAdapter = AccountMessageAdapter()
    override fun createPresenter(): AccountMessageContract.Presenter = AccountMessagePresenter(view)
    override fun bindView(activity: FragmentActivity, resourceProvider: AbsContract.ResourceProvider) {
        dataBinding = DataBindingUtil.setContentView<ModuleAccountMessageBinding>(activity, resourceProvider.layoutResId())
        dataBinding.accountMessageViewModel = this
    }

    override fun onCreateData(savedInstanceState: Bundle) {
        rvFun {
            layoutType = LayoutType.VLinearLayout
            dividerSize = 4F
            color = Color.parseColor("#F1F1F1")
            backgroundColor = Color.parseColor("#FFFFFF")
            attachToRecyclerView(dataBinding.recyclerView)
        }?.adapter = adapter
        presenter.getMessageList(GetAccountMessageReq(
                userId = SpUtil.getString(C.SpKey.userId)
        )).compose(RxHelper.rxLifecycleTransformer(view))
                .compose(RxHelper.addLifecycleTransformer(view))
                .subscribe {
                    adapter.setNewData(it)
                }
    }

    override fun onClickEvent(v: View) {

    }

}
```

> AccountMessageAdapter
```

class AccountMessageAdapter :
        BaseQuickAdapter<AccountMessageBean, ViewHolder>(R.layout.module_account_message_item) {
    override fun convert(helper: ViewHolder, item: AccountMessageBean) {
        val binding = helper.getBinding()
        binding.setVariable(BR.accountMessageBean, item)
        binding.executePendingBindings()
        Glide.with(mContext).load(item.thumb).into(helper.getView(R.id.imageView))
    }

    override fun getItemView(layoutResId: Int, parent: ViewGroup): View {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(mLayoutInflater, layoutResId, parent, false) ?: return super.getItemView(layoutResId, parent)
        val view = binding.root
        view.setTag(R.id.BaseQuickAdapter_databinding_support, binding)
        return view
    }
}

class ViewHolder(view: View) : BaseViewHolder(view) {
    fun getBinding(): ViewDataBinding {
        return itemView.getTag(R.id.BaseQuickAdapter_databinding_support) as ViewDataBinding
    }
}

```
> module_account_message_item
```
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        xmlns:app="http://schemas.android.com/apk/res-auto">

    <data>

        <variable
            name="accountMessageBean"
            type="com.alex.andfun.account.entity.AccountMessageBean"/>
    </data>

    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="80dp"
        tools:ignore="ContentDescription,HardcodedText,RtlHardcoded">

        <ImageView
            android:id="@+id/imageView"
            android:layout_width="60dp"
            android:layout_height="60dp"
            android:layout_centerVertical="true"
            android:layout_marginLeft="16dp"
            android:background="#EEEEEE"
            />

        <TextView
            android:id="@+id/textView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_alignParentRight="true"
            android:layout_toRightOf="@+id/imageView"
            android:gravity="center_vertical"
            android:paddingLeft="8dp"
            android:paddingRight="16dp"
            android:text="@{accountMessageBean.content}"
            tools:ignore="RtlHardcoded"/>

    </RelativeLayout>
</layout>
```

> module_account_message
```
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        xmlns:tools="http://schemas.android.com/tools"
        tools:ignore="HardcodedText">


    <data>


        <variable
            name="accountMessageViewModel"
            type="com.alex.andfun.account.message.AccountMessageViewModel"/>

    </data>

    <android.support.constraint.ConstraintLayout
        android:id="@+id/layoutRoot"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <org.alex.widget.SimpleTooBar
            android:id="@+id/simpleTooBar"
            android:layout_width="0dp"
            android:layout_height="@dimen/title_bar_height"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:stb_title="个人消息"/>

        <android.support.v7.widget.RecyclerView
            android:id="@+id/recyclerView"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:gravity="center"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toBottomOf="@+id/simpleTooBar"
            tools:ignore="HardcodedText"/>

    </android.support.constraint.ConstraintLayout>
</layout>
```