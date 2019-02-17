### Activity、Fragment 生命周期与状态保存  

默认 情况下，Activity 和 Fragment 会自动保存 View的状态，前提是View自己实现了 onSaveInstanceState、onRestoreInstanceState；  


然而我们注意到，Fragment只有：onActivityCreated、onSaveInstanceState、onViewStateRestored方法，并没有onRestoreInstanceState方法；  


### 结论  



### 参考  
https://inthecheesefactory.com/blog/fragment-state-saving-best-practices/en  
https://github.com/nuuneoi/StatedFragment  

