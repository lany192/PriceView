[![](https://jitpack.io/v/lany192/PriceView.svg)](https://jitpack.io/#lany192/PriceView)

# PriceView
init

### Add it in your root build.gradle at the end of repositories:

    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

### Add the dependency

	dependencies {
	        implementation 'com.github.lany192:PriceView:1.0.0'
	}

### Usage

    <com.lany.priceview.PriceView
        android:id="@+id/price_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:decimals_size="14sp"
        app:integer_size="20sp"
        app:integer_thousands="true"
        app:point_padding_left="5dp"
        app:point_padding_right="5dp"
        app:prefix_color="@android:color/holo_blue_light"
        app:prefix_size="30sp"
        app:prefix_text="¥"
        app:value_color="@android:color/holo_red_light"
        app:value_text="123456.78" />
        
### attribute

|Attributes|format|describe
|---|---|---|
|value_text| string|金额
|value_color| color|金额颜色
|integer_size| dimension|整数部分大小
|integer_thousands| boolean|整数部分是否使用千分符
|decimals_size| dimension|小数部分大小
|prefix_text| string|前缀文本
|prefix_size| dimension|前缀大小
|prefix_color| color|前缀颜色
|prefix_padding| dimension|前缀右边距
|point_padding_left| dimension|小数点左边距
|point_padding_right| dimension |小数点右边距
        
### Preview

![image](https://github.com/lany192/PriceView/raw/master/preview/pic1.png)