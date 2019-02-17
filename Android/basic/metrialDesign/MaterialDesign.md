### MaterialDesign  
```
CoordinatorLayout
CoordinatorLayout 是一个ViewGroup
给CoordinatorLayout用的属性
<declare-styleable name="CoordinatorLayout">
	<attr format="reference" name="keylines"/>
	<attr format="reference" name="statusBarBackground"/>
</declare-styleable>
给CoordinatorLayout的子控件 用的属性
<declare-styleable name="CoordinatorLayout_Layout">
	<attr name="android:layout_gravity"/>
	<attr format="string" name="layout_behavior"/>
	<attr format="reference" name="layout_anchor"/>
	<attr format="integer" name="layout_keyline"/>
	<attr name="layout_anchorGravity">    
		<flag name="top" value="0x30"/>
		<flag name="bottom" value="0x50"/>
		<flag name="left" value="0x03"/>
		<flag name="right" value="0x05"/>
		<flag name="center_vertical" value="0x10"/>
		<flag name="fill_vertical" value="0x70"/>
		<flag name="center_horizontal" value="0x01"/>
		<flag name="fill_horizontal" value="0x07"/>
		<flag name="center" value="0x11"/>
		<flag name="fill" value="0x77"/>
		<flag name="clip_vertical" value="0x80"/>
		<flag name="clip_horizontal" value="0x08"/>
		<flag name="start" value="0x00800003"/>
		<flag name="end" value="0x00800005"/>
	</attr>
</declare-styleable>
AppBarLayout
AppBarLayout 是一个线性布局
给AppBarLayout用的属性
<declare-styleable name="AppBarLayout">
	<attr name="elevation"/>
	<attr name="android:background"/>
	<attr format="boolean" name="expanded"/>
</declare-styleable>
给AppBarLayout的子控件 用的属性
<declare-styleable name="AppBarLayout_Layout">
<attr name="layout_scrollFlags">
		<flag name="scroll" value="0x1"/>
		<flag name="exitUntilCollapsed" value="0x2"/>
		<flag name="enterAlways" value="0x4"/>
		<flag name="enterAlwaysCollapsed" value="0x8"/>
		<flag name="snap" value="0x10"/>
	</attr>
	<attr format="reference" name="layout_scrollInterpolator"/>
</declare-styleable>
app:layout_scrollFlags
没有设置这个flag的View将被固定在屏幕顶部。 
enterAlways：这个flag让任意向下的滚动都会导致该View变为可见，启用快速“返回模式”。 
enterAlwaysCollapsed：当你的视图已经设置minHeight属性又使用此标志时，你的视图只能已最小高度进入，只有当滚动视图到达顶部时才扩大到完整高度。
exitUntilCollapsed：this flag causes the view to scroll off until it is ‘collapsed’ (its minHeight) before exiting。 
特别注意：所有使用scroll flag的View都必须定义在没有使用scroll flag的View前面，这样才能确保所有的View从顶部退出，留下固定的元素。


CollapsingToolbarLayout
CollapsingToolbarLayout是一个帧布局
给CollapsingToolbarLayout用的属性
<declare-styleable name="CollapsingToolbarLayout">
	<attr format="dimension" name="expandedTitleMargin"/>
	<attr format="dimension" name="expandedTitleMarginStart"/>
	<attr format="dimension" name="expandedTitleMarginTop"/>
	<attr format="dimension" name="expandedTitleMarginEnd"/>
	<attr format="dimension" name="expandedTitleMarginBottom"/>
	<attr format="reference" name="expandedTitleTextAppearance"/>
	<attr format="reference" name="collapsedTitleTextAppearance"/>

	<attr format="color" name="contentScrim"/>
	<attr format="color" name="statusBarScrim"/>
	<attr format="reference" name="toolbarId"/>
	<attr format="dimension" name="scrimVisibleHeightTrigger"/>
	<attr format="integer" name="scrimAnimationDuration"/>
	<attr name="collapsedTitleGravity">
		<flag name="top" value="0x30"/>
		<flag name="bottom" value="0x50"/>
		<flag name="left" value="0x03"/>
		<flag name="right" value="0x05"/>
		<flag name="center_vertical" value="0x10"/>
		<flag name="fill_vertical" value="0x70"/>
		<flag name="center_horizontal" value="0x01"/>
		<flag name="center" value="0x11"/>
		<flag name="start" value="0x00800003"/>
		<flag name="end" value="0x00800005"/>
	</attr>
	<attr name="expandedTitleGravity">
		<flag name="top" value="0x30"/>
		<flag name="bottom" value="0x50"/>
		<flag name="left" value="0x03"/>
		<flag name="right" value="0x05"/>
		<flag name="center_vertical" value="0x10"/>
		<flag name="fill_vertical" value="0x70"/>
		<flag name="center_horizontal" value="0x01"/>
		<flag name="center" value="0x11"/>
		<flag name="start" value="0x00800003"/>
		<flag name="end" value="0x00800005"/>
	</attr>
	<attr format="boolean" name="titleEnabled"/>
	<attr name="title"/>
</declare-styleable> 
给CollapsingToolbarLayout的 子控件 用的属性
<declare-styleable name="CollapsingToolbarLayout_Layout">
	<attr name="layout_collapseMode">    
		<enum name="none" value="0"/>
		<enum name="pin" value="1"/>
		<enum name="parallax" value="2"/>
	</attr>
	<attr format="float" name="layout_collapseParallaxMultiplier"/>
</declare-styleable>
```