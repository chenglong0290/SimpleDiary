# 日迹 SimpleDiary

极简本地日记 App（Android 原生：Kotlin + Jetpack Compose + Room）。

> **零基础建议先读：** [docs/新手入门与架构说明.md](docs/新手入门与架构说明.md)  
> 含整体架构、目录说明、启动链路、保存规则、文件速查表。

## 功能

- 日记列表：仅显示年月日 + 左侧心情色标
- 右下角 `+` 快速新建；编辑页直接写正文，返回自动保存
- 正文与媒体皆空时不保存
- 支持图片 / 视频 / 音频附件预览与播放
- 回顾 Tab：那年今日 / 那月今日 / 那周今日
- 心情颜色：编辑页右下角与列表右侧均可设置

## 打开工程

1. 安装 [Android Studio](https://developer.android.com/studio)（需 JDK 17）
2. **Open** 本目录 `SimpleDiary/`
3. 等待 Gradle Sync
4. 连接安卓手机或启动模拟器，点击 Run

## 包名

`com.simplediary.app`

## 说明

- 纯本地，无账号、无后端
- 媒体文件保存在应用私有目录 `files/media/`
