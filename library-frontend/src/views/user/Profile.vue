<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 娑擃亙姹夋穱鈩冧紖閸楋紕澧?-->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>娑擃亙姹夋穱鈩冧紖</span>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="閻劍鍩涢崥?>{{ userInfo.username }}</el-descriptions-item>
            <el-descriptions-item label="闁喚顔?>{{ userInfo.email || '閺堫亣顔曠純? }}</el-descriptions-item>
            <el-descriptions-item label="閻絻鐦?>{{ userInfo.phone || '閺堫亣顔曠純? }}</el-descriptions-item>
            <el-descriptions-item label="鐟欐帟澹?>
              <el-tag v-for="role in userInfo.roles" :key="role" size="small" style="margin-right: 5px">
                {{ role }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="閻樿埖鈧?>
              <el-tag v-if="userInfo.status === 1" type="success">濮濓絽鐖?/el-tag>
              <el-tag v-else type="danger">缁備胶鏁?/el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <!-- 娣囶喗鏁肩€靛棛鐖?-->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>娣囶喗鏁肩€靛棛鐖?/span>
            </div>
          </template>
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
            <el-form-item label="閺冄冪槕閻? prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="閺傛澘鐦戦惍? prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="绾喛顓荤€靛棛鐖? prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword" :loading="submitLoading">
                娣囶喗鏁肩€靛棛鐖?              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCurrentUser } from '@/api/auth'

const submitLoading = ref(false)
const passwordFormRef = ref(null)
const userInfo = ref({
  username: '',
  email: '',
  phone: '',
  roles: [],
  status: 1,
  isAdmin: false
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('娑撱倖顐兼潏鎾冲弳閻ㄥ嫬鐦戦惍浣风瑝娑撯偓閼?))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '鐠囩柉绶崗銉︽＋鐎靛棛鐖?, trigger: 'blur' }],
  newPassword: [
    { required: true, message: '鐠囩柉绶崗銉︽煀鐎靛棛鐖?, trigger: 'blur' },
    { min: 6, message: '鐎靛棛鐖滈梹鍨娑撳秷鍏樼亸鎴滅艾6娑擃亜鐡х粭?, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '鐠囬鈥樼拋銈嗘煀鐎靛棛鐖?, trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const fetchUserInfo = async () => {
  try {
    const res = await getCurrentUser()
    userInfo.value = res
    // 閺囧瓨鏌?sessionStorage 娑擃厾娈戦悽銊﹀煕娣団剝浼?    sessionStorage.setItem('username', res.username)
    sessionStorage.setItem('isAdmin', String(res.isAdmin))
  } catch (error) {
    console.error('閼惧嘲褰囬悽銊﹀煕娣団剝浼呮径杈Е:', error)
  }
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        // TODO: 对接后端修改密码 API
        ElMessage.success('密码修改成功，请重新登录')
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
      } catch (error) {
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

