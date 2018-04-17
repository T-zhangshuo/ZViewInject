# ZViewInject
编译时注解库,替换findViewById,和setOnClicklistener
## 使用示例
### 在Activity中

```java
    @ViewInject( value = R.id.main_txt,clickEvent = "addList")//暂无法传参，此处写方法名
    TextView textView;

    @ViewInject(R.id.main_listview)
    ListView listView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewInjector.inject(this);
      
    }
    //执行方法
    public void addList(){
    
    }
```

### 在viewHolder，或 Fragment中
```java
    @ViewInject(R.id.item_txt)
     TextView textView;
     ViewHolder(View itemView) {
        ViewInjector.inject(this, itemView);
     }
```   
### 引入

projcet 的build.gradle 中添加

```java
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
moudle 的build.gradle 中添加
```java
    dependencies {
	       api 'com.github.T-zhangshuo.ZViewInject:zapi:2.1.5'
    	       annotationProcessor 'com.github.T-zhangshuo.ZViewInject:zcompiler:2.1.5'
	}
```
### 需要多模块支持引入
project的 build.gradle中添加
```java
buildscript {

    repositories {
      	...
        maven { url "https://jitpack.io" }
    }
    dependencies {
       	...
        classpath 'com.github.T-zhangshuo.ZViewInject:zgradle:2.1.5'
    }
}

```
基础modlue中添加依赖
```java
 api 'com.github.T-zhangshuo.ZViewInject:zapi:2.1.5'
 ```
 
 其他业务module的build.gradle中添加
 ```java
 apply plugin: 'injectplugin'
 ```
 
 其他业务module中添加依赖
 ```
 annotationProcessor 'com.github.T-zhangshuo.ZViewInject:zcompiler:2.1.5'
 ```
