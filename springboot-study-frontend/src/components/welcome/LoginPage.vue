<script setup>
import {Lock, User} from "@element-plus/icons-vue";
import {reactive} from "vue";
import {ElMessage} from "element-plus";
import {post} from "@/net";
import router from "@/router";

const form = reactive({
    username: '',
    password: '',
    remember: false
})

const login = () => {
    // 非空判断
    if (!form.username || !form.password) {
        ElMessage.warning('请填写用户名和密码');
    } else {
        post('/api/auth/login', {
            username: form.username,
            password: form.password,
            remember: form.remember
        }, (message) => {
            ElMessage.success(message);
            router.push('/index');
        })
    }
}
</script>

<template>
    <div style="text-align: center; margin: 0 20px;">
        <div>
            <div style="font-size: 1.5rem; font-weight: bold">登录</div>
            <div style="font-size: 0.75rem; color: grey">在进入系统之前请先输入用户名和密码进行登录</div>
        </div>
        <div style="margin-top: 50px">
            <el-input type="text" placeholder="用户名 / 邮箱"
                      v-model="form.username"
                      :prefix-icon="User"/>
            <el-input type="password" placeholder="密码"
                      v-model="form.password"
                      style="margin-top: 10px"
                      :prefix-icon="Lock"/>
        </div>
        <el-row style="margin-top: 5px">
            <el-col :span="12" style="text-align: start">
                <el-checkbox v-model="form.remember" label="七天免登录"/>
            </el-col>
            <el-col :span="12" style="text-align: end;">
                <el-link>忘记密码</el-link>
            </el-col>
        </el-row>
        <div style="margin-top: 40px">
            <el-button @click="login()" style="width: 70%" type="primary" plain>立即登录</el-button>
        </div>
        <el-divider>
            <span style="color: grey; font-size: 0.75rem">没有账号？</span>
        </el-divider>
        <div>
            <el-button style="width: 70%" type="danger" plain>注册账号</el-button>
        </div>
    </div>
</template>

<style scoped>

</style>