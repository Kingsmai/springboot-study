<script setup>
import {Lock, User, Message, EditPen} from "@element-plus/icons-vue";
import router from "@/router";
import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {post} from "@/net";

const formRef = ref()

const form = reactive({
    username: '',
    password: '',
    password_repeat: '',
    email: '',
    security_code: ''
})

// 用户名合法性验证
const validateUsername = (rule, value, callback) => {
    if (!/^[\u4e00-\u9fa5a-zA-Z0-9]+$/.test(value)) {
        // 不符合：包含中文英文的用户名不能有特殊字符 正则表达式
        callback(new Error('用户名不能包含特殊字符，只能中文 / 英文'))
    } else {
        // 通过
        callback()
    }
}

// 密码合法性校验
const validatePassword = (rule, value, callback) => {
    if (/[\u4e00-\u9fa5]+/.test(value)) {
        callback(new Error('密码中不能包含中文'))
    } else if (!/\d/.test(value)) {
        callback(new Error('密码至少包含一个数字'))
    } else if (!/[A-Z]/.test(value)) {
        callback(new Error('密码至少包含一个大写字母'))
    } else if (!/[!@#$%^&*]/.test(value)) {
        callback(new Error('密码至少包含一个标点符号'))
    } else {
        // 通过
        callback()
    }
}

const validatePasswordRepeat = (rule, value, callback) => {
    if (value !== form.password) {
        callback(new Error('两次输入的密码不一致'))
    } else {
        callback()
    }
}

const rules = {
    username: [
        {required: true, message: '请输入用户名', trigger: 'blur'},
        {validator: validateUsername, trigger: ['blur', 'change']},
        {min: 2, max: 16, message: '用户名必须在 2 - 16 字符以内', trigger: ['blur', 'change']}
    ],
    password: [
        {required: true, message: '请输入密码', trigger: 'blur'},
        {min: 8, max: 16, message: '密码必须在 8 - 16 字符以内', trigger: ['blur', 'change']},
        {validator: validatePassword, trigger: ['blur', 'change']}
    ],
    password_repeat: [
        {required: true, message: '请再次输入密码', trigger: 'blur'},
        {validator: validatePasswordRepeat, trigger: ['blur', 'change']}
    ],
    email: [
        {required: true, message: '请输入电子邮件', trigger: 'blur'},
        {type: 'email', message: '请输入正确的电子邮件', trigger: ['blur', 'change']}
    ],
    security_code: [
        {required: true, message: '请输入获取的验证码', trigger: 'blur'},
        {min: 6, max: 6, message: '验证码必须为 6 个字符', trigger: ['blur', 'change']},
    ]
}

// ===========================
// 在信息不完整之前，禁用获取验证码按钮
// ===========================
const isFieldsValid = reactive({
    username: false,
    email: false
})

const isSecurityCodeButtonDisabled = ref(true);
// 发送验证码冷却
const verificationCodeCoolDown = ref(0);

// 每次验证表单数据的时候，执行 OnValidate
const onValidate = (prop, isValid) => {
    if (prop === "username" || prop === "email") {
        isFieldsValid[prop] = isValid
    }
    // 当 username 和 email 都为 true，更新 isSecurityCodeButtonDisabled 为 true
    isSecurityCodeButtonDisabled.value = !(isFieldsValid.username && isFieldsValid.email);
}

// ===========================
// 发送验证码
// ===========================
const getVerificationCode = () => {
    verificationCodeCoolDown.value = 60;
    post("/api/auth/getRegisterVerificationCode", {
        email: form.email,
        username: form.username
    }, (message) => {
        ElMessage.success(message)
        setInterval(() => {
            verificationCodeCoolDown.value--
        }, 1000)
    }, () => {
        verificationCodeCoolDown.value = 0;
    })
}

// ===========================
// 注册
// ===========================
const register = () => {
    formRef.value.validate((isValid) => {
        if (isValid) {
            // 表单合法，访问后端请求
            post("/api/auth/register", {
                username: form.username,
                password: form.password,
                email: form.email,
                verificationCode: form.security_code
            }, (message) => {
                ElMessage.success(message);
                router.push("/");
            })
        } else {
            ElMessage.warning("请完整填写表单注册内容。")
        }
    })
}
</script>

<template>
    <div style="text-align: center; margin: 0 20px;">
        <div style="margin-top: 150px">
            <div>
                <div style="font-size: 1.5rem; font-weight: bold">注册新用户</div>
                <div style="font-size: 0.75rem; color: grey">欢迎注册我们的学习平台，请在下方填写相关信息</div>
            </div>
            <div style="margin-top: 50px">
                <el-form :model="form"
                         :rules="rules"
                         @validate="onValidate"
                         ref="formRef">
                    <el-form-item prop="username">
                        <el-input type="text" :maxlength="16" placeholder="用户名"
                                  v-model="form.username"
                                  :prefix-icon="User"/>
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input type="password" :maxlength="16" placeholder="密码"
                                  v-model="form.password"
                                  show-password
                                  :prefix-icon="Lock"/>
                    </el-form-item>
                    <el-form-item prop="password_repeat">
                        <el-input type="password" :maxlength="16" placeholder="重复密码"
                                  v-model="form.password_repeat"
                                  show-password
                                  :prefix-icon="Lock"/>
                    </el-form-item>
                    <el-form-item prop="email">
                        <el-input type="email" placeholder="电子邮件地址"
                                  v-model="form.email"
                                  :prefix-icon="Message"/>
                    </el-form-item>
                    <el-form-item prop="security_code">
                        <el-row :gutter=10>
                            <el-col :span="16">
                                <el-input type="text" :maxlength="6" placeholder="请输入验证码"
                                          v-model="form.security_code"
                                          :prefix-icon="EditPen"/>
                            </el-col>
                            <el-col :span="8">
                                <el-button style="width: 100%" type="primary"
                                           :disabled="isSecurityCodeButtonDisabled || verificationCodeCoolDown > 0"
                                           @click="getVerificationCode">
                                    {{
                                    verificationCodeCoolDown > 0 ? `请稍后（${verificationCodeCoolDown}秒）` : "获取验证码"
                                    }}
                                </el-button>
                            </el-col>
                        </el-row>
                    </el-form-item>
                </el-form>
            </div>
            <div style="margin-top: 40px">
                <el-button @click="register" style="width: 70%" type="success" plain>立即注册</el-button>
            </div>
            <div style="margin-top: 10px">
                <span style="color: grey; font-size: 14px; vertical-align: middle">已有账号？</span>
                <el-link @click="router.push('/')" type="primary" plain>账号登录</el-link>
            </div>
        </div>
    </div>
</template>

<style scoped>

</style>