<script setup>
import {EditPen, Lock, Message} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import {post} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";

const currentStep = ref(0);

const formRef = ref();

const form = reactive({
    email: '',
    security_code: '',
    password: '',
    password_repeat: '',
})

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
    email: [
        {required: true, message: '请输入电子邮件', trigger: 'blur'},
        {type: 'email', message: '请输入正确的电子邮件', trigger: ['blur', 'change']}
    ],
    security_code: [
        {required: true, message: '请输入获取的验证码', trigger: 'blur'},
        {min: 6, max: 6, message: '验证码必须为 6 个字符', trigger: ['blur', 'change']},
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
};

const onValidate = (prop, isValid) => {
    if (prop === "email") {
        isSecurityCodeButtonDisabled.value = !isValid;
    }
}

const isSecurityCodeButtonDisabled = ref(true);
// 发送验证码冷却
const verificationCodeCoolDown = ref(0);

const getVerificationCode = () => {
    post("/api/auth/getForgetPasswordVerificationCode", {
        email: form.email
    }, (message) => {
        ElMessage.success(message)
        verificationCodeCoolDown.value = 60;
        setInterval(() => {
            verificationCodeCoolDown.value--
        }, 1000)
    })
}

const emailVerify = () => {
    formRef.value.validate((isValid) => {
        if (isValid) {
            post("/api/auth/checkVerificationCode", {
                email: form.email,
                verificationCode: form.security_code
            }, (message) => {
                ElMessage.success(message)
                currentStep.value = 1;
            })
        } else {
            ElMessage.warning("请完整填写表单内容");
        }
    })
}

const resetPassword = () => {
    formRef.value.validate((isValid) => {
        if (isValid) {
            post("/api/auth/resetPassword", {
                password: form.password
            }, (message) => {
                ElMessage.success(message)
                currentStep.value = 2;
            })
        } else {
            ElMessage.warning("请完整填写表单内容");
        }
    })
}
</script>

<template>
    <div style="text-align:center; margin: 0 20px;">
        <div style="margin-top: 100px">
            <el-steps :active="currentStep" finish-status="success" align-center>
                <el-step title="邮箱验证"/>
                <el-step title="重置密码"/>
            </el-steps>
        </div>
        <div style="margin-top: 50px">
            <!-- 当步骤为 0 时，显示电子邮箱验证界面 -->
            <div v-if="currentStep === 0">
                <div style="font-size: 1.5rem; font-weight: bold">邮箱验证</div>
                <div style="font-size: 0.75rem; color: grey">请先输入需要找回的邮箱账号</div>
                <div style="margin-top: 50px">
                    <el-form :model="form"
                             :rules="rules"
                             @validate="onValidate"
                             ref="formRef">
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
                    <div style="margin-top: 40px">
                        <el-button @click="emailVerify" style="width: 70%" type="danger">验证邮箱</el-button>
                    </div>
                </div>
            </div>
            <!-- 当步骤为 1 时，显示重置密码界面 -->
            <div v-else-if="currentStep === 1">
                <div style="font-size: 1.5rem; font-weight: bold">重置密码</div>
                <div style="font-size: 0.75rem; color: grey">请输入你的新密码，请务必牢记</div>
                <div style="margin-top: 50px">
                    <el-form :model="form"
                             :rules="rules"
                             @validate="onValidate"
                             ref="formRef">
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
                    </el-form>
                    <div style="margin-top: 40px">
                        <el-button @click="resetPassword" style="width: 70%" type="danger">立即重置密码</el-button>
                    </div>
                </div>
            </div>
            <!-- 当步骤为 2 时，显示重置成功界面 -->
            <div v-else-if="currentStep === 2">
                <div style="font-size: 1.5rem; font-weight: bold">重置密码成功</div>
                <div style="font-size: 0.75rem; color: grey">现在你可以用你的新密码登录本系统啦！</div>
                <div style="margin-top: 40px">
                    <el-button @click="router.push('/')" style="width: 70%">返回登录页面</el-button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>

</style>