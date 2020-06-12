注意：打生产环境安装包时，是会做代码混淆的，具体见app/build.gradle，
混淆配置详见app/proguard-rules.pro
请注意一些细节
例如当使用fastjson或者GSON序列化数据模型时，混淆后可能会出现属性名被更改的错误

签名文件：rcbox_keystore.jks
MD5: FD:BC:22:29:96:A2:39:9D:B3:64:90:12:40:6E:35:D1
SHA1: 70:5A:B7:EC:90:FB:C2:7F:C9:91:20:3D:EC:70:7C:FD:FE:16:68:C4
SHA256: ED:21:23:FF:D6:5A:E0:9C:DB:71:B2:51:70:62:5B:2E:7C:C5:E7:4F:3A:F1:E0:07:C8:B7:96:02:89:5D:58:27

adb shell settings put global policy_control immersive.full=*
adb shell monkey -p com.bznet.android.rcbox  --pct-syskeys 0 --pct-touch 100 -v 10000
adb shell settings put global policy_control null

keytool -v -list -keystore rcbox_keystore.jks//查看签名信息
gradlew assembleRelease//只构建Release渠道包
gradlew assemblemk_testDebug 编译并打mk_test的debug包，其他类似
gradlew assemblemk_testDebug

Users\Android\AppData\Local\Android\sdk\platform-tools


水波纹参考

<?xml version="1.0" encoding="utf-8"?>
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="@color/select">
            <item
                android:state_pressed="false">
                <shape>
                    <corners android:radius="0dp" />
                    <solid android:color="@android:color/white" />
                </shape>
            </item>
</ripple>



有时做开发的时候，用真机测试，总是看不到logcat信息 。原因是系统默认关闭了log，需要将其打开。
 解决办法如下：

   在拨号界面输入*#*#2846579#*#* ，然后系统会自动弹出一个菜单，选择工程菜单，进入后，点击背景设置，然后选择log设置，将默认的设置成开启状态，然后手机可能会自动重启，如果不自动重启，请手动重启。重启之后就行了。
http://stackoverflow.com/questions/6941710/unable-to-open-log-device-dev-log-main-no-such-file-or-directory


&#32; == 普通的英文半角空格

&#160; == &nbsp; == &#xA0; == no-break space （普通的英文半角空格但不换行）

&#12288; == 中文全角空格 （一个中文宽度）

&#8194; == &ensp; == en空格 （半个中文宽度）

&#8195; == &emsp; == em空格 （一个中文宽度）

&#8197; == 四分之一em空格 （四分之一中文宽度）

相比平时的空格（&#32;），nbsp拥有不间断（non-breaking）特性。即连续的nbsp会在同一行内显示。即使有100个连续的nbsp，浏览器也不会把它们拆成两行。

18675522196

jarsigner -digestalg SHA1 -sigalg MD5withRSA -keystore D:\git\BZW\baza.bzw.android\rcbox_keystore.jks -storepass rcb123456 -signedjar D:\git\BZW\signed.apk D:\git\BZW\unsign.apk rcbox

adb -s cee571b  shell dumpsys activity activities

javap -classpath app\build\intermediates\classes\mk_vivo\rc -c  com.baza.android.bzw.dao.AccountDao
javap -classpath app\build\intermediates\classes\mk_vivo\rc -s  com.baza.android.bzw.dao.ResumeUpdateDao
gradlew -q dependencies app:dependencies --configuration compile