<script setup>
import {Lock, User, Message, EditPen} from "@element-plus/icons-vue";
import router from "@/router";
import {reactive} from "vue";

const form = reactive({
    username: '',
    password: '',
    password_repeat: '',
    email: '',
    security_code: ''
})
</script>

<template>
    <div style="text-align: center; margin: 0 20px;">
        <div>
            <div style="font-size: 1.5rem; font-weight: bold">注册新用户</div>
            <div style="font-size: 0.75rem; color: grey">欢迎注册我们的学习平台，请在下方填写相关信息</div>
        </div>
        <div style="margin-top: 50px">
            <el-input type="text" placeholder="用户名"
                      v-model="form.username"
                      :prefix-icon="User"/>
            <el-input type="password" placeholder="密码"
                      v-model="form.password"
                      style="margin-top: 10px"
                      :prefix-icon="Lock"/>
            <el-input type="password" placeholder="重复密码"
                      v-model="form.password_repeat"
                      style="margin-top: 10px"
                      :prefix-icon="Lock"/>
            <el-input type="email" placeholder="电子邮件地址"
                      v-model="form.email"
                      style="margin-top: 10px"
                      :prefix-icon="Message"/>
            <el-row style="margin-top: 10px" gutter="10">
                <el-col :span="16">
                    <el-input type="text" placeholder="请输入验证码"
                              v-model="form.security_code"
                              :prefix-icon="EditPen"/>
                </el-col>
                <el-col :span="8">
                    <el-button style="width: 100%" type="primary">获取验证码</el-button>
                </el-col>
            </el-row>
        </div>
        <div style="margin-top: 40px">
            <el-button style="width: 70%" type="success" plain>立即注册</el-button>
        </div>
        <div style="margin-top: 10px">
            <span style="color: grey; font-size: 14px; vertical-align: middle">已有账号？</span>
            <el-link @click="router.push('/')" type="primary" plain>账号登录</el-link>
        </div>
    </div>
</template>

<style scoped>

</style>