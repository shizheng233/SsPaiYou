# SsPaiYou
 第三方的少数派Android App，适配Material You。

## Cookie / Token 本地加密

`LocalTokenManager` 使用 Android Keystore 中不可导出的 AES-256 密钥，以
`AES/GCM/NoPadding` 加密 JWT 和 `cookieSaver()` 保存的其他 Cookie。
每次加密由系统生成新的 96 位 IV，使用 128 位认证标签，并将 Cookie 名称绑定为
AAD，防止密文被篡改或移到另一个 Cookie 名下。`SharedPreferences` 只保存
`v1:` 前缀及 Base64 编码的 IV、密文和认证标签；用户名 slug 属于普通账号元数据。

对于本项目（最低 Android 10、需要自动登录）的本地凭据，直接使用 Android
Keystore 是合适的方案。它负责保管密钥，Cookie 密文仍保存在应用私有目录。
不强制 StrongBox 或每次使用密钥时进行生物认证，以兼容设备并支持自动登录。
AndroidX Security Crypto 的 `EncryptedSharedPreferences` / `MasterKey` 已弃用，
官方建议使用平台 API 和 Android Keystore，因此这里不引入该依赖。

- 第一次读取或保存 Cookie 时，将旧 `TOKEN_MANAGER` 中所有明文 Cookie 一次性加密，
  与版本标记一起提交；迁移后的读取不再接受明文回退。
- 密文损坏、密钥丢失或失效时，丢弃无法解密的 Cookie；JWT 失效时同时清除 slug，
  返回未登录状态。新登录可重新创建密钥，加密失败不会降级保存明文。
- Android 10 备份、Android 12+ 云备份和设备迁移均排除 `TOKEN_MANAGER.xml`
  以及 WebView 数据目录，避免迁移无法解密的凭据或复制 WebView 会话。
- HTTP 日志中的 `Authorization`、`Cookie`、`Set-Cookie` 头均脱敏。
- `saveToken()` 已在 IO 线程执行；同步 Cookie 接口的调用方应在 IO 线程执行读写。

此处保护的是 `LocalTokenManager` 的落盘凭据；登录 WebView 自身的 Cookie
存储由 Android WebView 管理。卸载、清除应用数据或换机后需要重新登录。

参考：[Android Keystore](https://developer.android.com/privacy-and-security/keystore)、
[Security Crypto 弃用说明](https://developer.android.com/jetpack/androidx/releases/security#1.1.0-alpha07)。
