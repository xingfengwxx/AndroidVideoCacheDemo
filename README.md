# AndroidVideoCache框架使用
代理地址加密，支持取消缓存任务。
==============================
## 1、添加支持ape和https的ijk so文件
## 2、AndroidVideoCache添加取消缓存任务的方法
   在做音乐播放器时，会有个问题：当前播放的歌曲正在缓存时，切歌后会导致多个缓存任务同时进行，
   会导致切歌后需要很久才开始播放。这时就需要取消之前的缓存任务。