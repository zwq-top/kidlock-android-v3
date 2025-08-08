
# KidLock (儿童锁) — 构建 APK 指南

## 一、用 Android Studio 本地打包（推荐）
1. 用 Android Studio 打开本工程。
2. 连接手机（或启动模拟器）。
3. 顶部菜单：**Build → Build APK(s)**，在 `app/build/outputs/apk/debug/` 找到 `app-debug.apk`（可直接安装）。
4. 或：**Build → Generate Signed Bundle / APK...**，选择 APK，创建/选择签名文件（.jks），生成 `app-release.apk`。

### 生成签名文件（Windows/macOS/Linux 通用）
```bash
keytool -genkeypair -v -keystore kidlock.jks -storepass 123456 -alias kidlock -keypass 123456 -keyalg RSA -keysize 2048 -validity 3650 -dname "CN=KidLock,O=YourOrg,C=CN"
```
在 Android Studio 的 **Build → Generate Signed Bundle / APK...** 里选择此 keystore。

### 命令行打包（需要有 Gradle Wrapper 或本地 Gradle）
```bash
# Debug（无需签名配置）
./gradlew assembleDebug

# Release（需在 app/build.gradle.kts 配置签名）
./gradlew assembleRelease
```

> 若没有 Gradle Wrapper，可直接用 Android Studio 构建，或使用下面的 GitHub Actions。

---

## 二、用 GitHub Actions 云端编译（免本地环境）
> 本仓库已提供 `.github/workflows/build.yml`。把工程推到 GitHub 后，会自动构建 Debug APK，并在 Actions 里生成下载的构建产物。

步骤：
1. 在 GitHub 新建空仓库（Private/Private均可）。
2. 把本工程全部文件推送：
   ```bash
   git init
   git remote add origin <你的仓库地址>
   git add .
   git commit -m "init kidlock"
   git push -u origin main
   ```
3. 打开 GitHub → **Actions** → 选择 `Android Debug APK` 工作流 → 运行。
4. 成功后在该工作流页面底部 **Artifacts** 下载 `app-debug.apk`。

> 如果要云端打 **签名的 Release APK**，请在仓库 **Settings → Secrets and variables → Actions → New repository secret** 添加：
> - `KEYSTORE_BASE64`：把 `kidlock.jks` 做 base64（`base64 kidlock.jks | pbcopy` 等）；
> - `KEYSTORE_PASSWORD`、`KEY_PASSWORD`、`KEY_ALIAS`；
> 并把 workflow 中对应注释段落解除注释。

---

## 三、安装 APK
- 打开手机 **设置 → 安全**，允许来自此来源的安装（不同品牌路径略有差异）。
- 用数据线 `adb install app-debug.apk`，或发送到手机后点击安装。

---

## 四、常见问题
- **提示缺少无障碍权限**：进入 **设置 → 无障碍** 启用“儿童锁”服务。
- **没有任何拦截效果**：检查“拦截应用”页是否勾选了要控制的 App；确保服务已开启。
- **超额仍可用**：配额按 *分钟* 结算，在前台切换时累加；保持 App 在前台不足 1 分钟的碎片化使用会在下一次切换时累计。

祝使用顺利！
