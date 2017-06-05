## 1단계
### 아래의 클래스 패스 의존성을 프로젝트 수준 build.gradle 파일에 추가합니다.
```html
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath "io.realm:realm-gradle-plugin:3.1.1"
    }
}
```

## 2단계
### Realm-android 플러그인을 애플리케이션 수준 build.gradle 파일에서 적용시킵니다.
```html
apply plugin: 'realm-android'
```

## 3단계
### Realm 인스턴스들의 생명주기 관리하기
```html
// 애플리케이션에서 Realm 설정하기
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this)
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}

// 액티비티들을 전환하며 onCreate()/onDestroy()가 중첩되면 Activity 2의 onCreate가
// Activity 1의 onDestroy()보다 먼저 호출 됩니다.
public class MyActivity extends Activity {
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}

// 프래그먼트에서 onStart()/onStop()를 사용합니다.
// 프래그먼트의 onDestroy()는 호출되지 않을 수 있습니다.
public class MyFragment extends Fragment {
    private Realm realm;

    @Override
    public void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }
}
```
## 4단계
### AndroidManifest.xml 파일 Application 상속 정의하기
```html
<application
        android:name=".MyApplication"
```