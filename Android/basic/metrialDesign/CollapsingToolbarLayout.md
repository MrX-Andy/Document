### CollapsingToolbarLayout  
```
<declare-styleable name="CollapsingToolbarLayout_Layout">
	<attr name="layout_collapseMode">    
		<enum name="none" value="0"/>
		<enum name="pin" value="1"/>
		<enum name="parallax" value="2"/>
	</attr>
	<attr format="float" name="layout_collapseParallaxMultiplier"/>
</declare-styleable>

```
Collapsing title(可折叠的title)  
当CollapsingToolbarLayout全屏没有折叠时，title比较大，title显示的是大字体，  
在折叠的过程中，title不断变小到一定大小的效果。你可以调用setTitle(CharSequence)方法设置title。  

你可以通过setTitle(CharSequence)设置title.title的样式可以通过collapsedTextAppearance和expandedTextAppearance属性调整。  
Content scrim.(内容粗布)  
当滚动位置达到一个阈值时，一个满血的粗布会显示或者隐藏。你可以通过setContentScrim(Drawable)方法改变这个粗布。  
5.0以上的设备起作用  
Parallax scrolling children(子view的滚动视差)  
子view可以选择是否被滚动在一个layout中形成特有的视差效果。参见COLLAPSE_MODE_PARALLAX和setParallaxMultiplier  
Pinned position children(子view的固定位置)  
这里使用了CollapsingToolbarLayout的app:layout_collapseMode="pin" 来确保Toolbar在view折叠的时候仍然被固定在屏幕的顶部。还可以做到更好的效果，当你让CollapsingToolbarLayout和Toolbar在一起使用的时候，title会在展开的时候自动变得大些，而在折叠的时候让字体过渡到默认值。  
可以通过属性app:layout_collapseParallaxMultiplier="0.6"改变。值de的范围[0.0,1.0]，值越大视察越大。  
"pin"：固定模式，在折叠的时候最后固定在顶端；  
"parallax"：视差模式，在折叠的时候会有个视差折叠的效果。我们可以在布局中使用属性app:layout_collapseMode="parallax"来改变。  


有一件事情必须注意，那就是CoordinatorLayout并不知道FloatingActionButton或者AppBarLayout的内部工作原理，它只是以Coordinator.Behavior的形式提供了额外的API，该API可以使子View更好的控制触摸事件与手势以及声明它们之间的依赖，并通过onDependentViewChanged()接收回调。  

可以使用CoordinatorLayout.DefaultBehavior(你的View.Behavior.class)注解或者在布局中使用app:layout_behavior=”com.example.app.你的View$Behavior”属性来定义view的默认行为。framework让任意View和CoordinatorLayout结合在一起成为了可能。  

