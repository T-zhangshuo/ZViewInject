# ZViewInject
编译时注解库
## 使用示例
### 在Activity中。
    
    @ViewInject( value = R.id.main_txt,clickEvent = "addList")//无法传参，写方法名
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


### 在viewHolder，或 Fragment中

    @ViewInject(R.id.item_txt)
     TextView textView;
     ViewHolder(View itemView) {
        ViewInjector.inject(this, itemView);
     }
     
### 引入

projcet 的build.gradle 中添加

    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

moudle 的build.gradle 中添加

    dependencies {
	        compile 'com.github.T-zhangshuo:ZViewInject:v1.0'
	}
