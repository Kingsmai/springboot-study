<script setup>
</script>

<template>
    <div style="width: 100vw; height: 100vh; overflow: hidden; display: flex">
        <div style="flex: 1;">
            <el-image style="width: 100%; height: 100%;" fit="cover"
                      src="https://i.imgur.com/s2pAJU5.jpeg"/>
        </div>
        <div class="welcome-title">
            <div style="font-size: 2rem; font-weight: bold">欢迎来到我们的学习平台</div>
            <div style="margin-top: 10px">在这里你可以学习如何使用 Java，如何搭建网站，并且与 Java 之父密切交流。</div>
            <div style="margin-top: 5px">在这里你可以同性交友，因为都是男的，没有学 Java 的女生。</div>
        </div>
        <div style="width: 400px; background-color: white; display: flex; align-items: center;">
            <router-view/>
        </div>
    </div>
</template>

<style scoped>
.welcome-title {
    position: absolute;
    bottom: 30px;
    left: 30px;
    color: white;
    text-shadow: 0 0 10px black;
}
</style>