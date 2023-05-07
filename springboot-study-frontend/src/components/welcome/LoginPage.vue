<script setup>
import {Lock, User} from "@element-plus/icons-vue";
</script>

<template>
    <div style="text-align: center; margin: 0 20px;">
        <div>
            <div style="font-size: 1.5rem; font-weight: bold">登录</div>
            <div style="font-size: 0.75rem; color: grey">在进入系统之前请先输入用户名和密码进行登录</div>
        </div>
        <div style="margin-top: 50px">
            <el-input type="text" placeholder="用户名 / 邮箱"
                      :prefix-icon="User"/>
            <el-input type="password" placeholder="密码"
                      style="margin-top: 10px"
                      :prefix-icon="Lock"/>
        </div>
        <el-row style="margin-top: 5px">
            <el-col :span="12" style="text-align: start">
                <el-checkbox v-model="rememberMe" label="记住我"/>
            </el-col>
            <el-col :span="12" style="text-align: end;">
                <el-link>忘记密码</el-link>
            </el-col>
        </el-row>
        <div style="margin-top: 40px">
            <el-button style="width: 70%" type="primary" plain>立即登录</el-button>
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