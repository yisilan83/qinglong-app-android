# 🐉 青龙面板 Android 客户端

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple?logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-Material%203-blue?logo=jetpackcompose)](https://developer.android.com/compose)
[![Hilt](https://img.shields.io/badge/DI-Hilt-orange?logo=dagger)](https://dagger.dev/hilt/)
[![Retrofit](https://img.shields.io/badge/HTTP-Retrofit-green?logo=square)](https://square.github.io/retrofit/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

基于 [青龙面板 API](https://github.com/whyour/qinglong) 的原生 Android 客户端，使用 **Kotlin + Jetpack Compose + Material 3** 构建。

## ✨ 特性

- 🎨 **Material You** 动态配色（Light / Dark 主题）
- 🔐 **两步验证 (2FA)** 内嵌登录界面
- 🏗️ **Clean Architecture** + MVVM 架构
- 💉 **Hilt** 依赖注入
- 🌐 **Retrofit** 网络层（自签名证书信任）
- 📦 **DataStore** 本地凭证持久化
- 🧭 **类型安全导航** (`@Serializable` routes)

## 🏗️ 架构

```
app/                        ← 入口 + DI
├── core/
│   ├── model/              ← 纯 Kotlin 领域模型
│   ├── data/               ← Repository + DataSource + Retrofit
│   ├── domain/             ← UseCase + Repository 接口
│   └── ui/                 ← 共享 Compose 组件 + Theme
└── feature/
    └── login/              ← 登录 + 两步验证
```

## 🚀 快速开始

1. **克隆项目**
```bash
git clone https://github.com/yisilan83/qinglong-app-android.git
```

2. **用 Android Studio 打开**（Hedgehog+ 推荐）

3. **构建 & 运行**
```bash
./gradlew :app:assembleDebug
```

## 🔑 登录流程

```
用户输入 Host + 用户名 + 密码
       │
       ▼
POST /api/user/login ───── code=200 ──→ 登录成功，获取 Token
       │
       │ code=420
       ▼
┌─────────────────────────┐
│   两步验证界面（内嵌）    │
│   输入 6 位验证码         │
└─────────────────────────┘
       │
       ▼
PUT /api/user/two-factor/login ──→ 验证成功，获取 Token
```

## 📋 开发计划

- [x] **阶段一：项目基础设施** — 架构、DI、网络层、主题
- [ ] **阶段二：核心基础设施** — HTTP 层完善、多账户管理
- [ ] **阶段三：登录模块** — ViewModel + UI 实现
- [ ] **阶段四：导航 & 主框架** — 自适应布局
- [ ] **阶段五：功能模块** — 定时任务、环境变量、脚本管理
- [ ] **阶段六：测试** — Unit / Integration / UI 测试

## 📄 License

MIT License
