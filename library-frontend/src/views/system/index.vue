<template>
  <div class="system-container">
    <el-tabs v-model="activeTab">
      <!-- 角色管理 -->
      <el-tab-pane label="角色管理" name="role">
        <el-card>
          <div class="header">
            <h3>角色列表</h3>
            <el-button type="primary" @click="handleAddRole">新增角色</el-button>
          </div>

          <el-table :data="roles" v-loading="roleLoading" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="roleName" label="角色名称" />
            <el-table-column prop="description" label="描述" />
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button size="small" @click="handleEditRole(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDeleteRole(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination">
            <el-pagination
              v-model:current-page="rolePageNum"
              v-model:page-size="rolePageSize"
              :total="roleTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="fetchRoles"
              @size-change="fetchRoles"
            />
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 权限管理 -->
      <el-tab-pane label="权限管理" name="permission">
        <el-card>
          <div class="header">
            <h3>权限列表</h3>
            <el-button type="primary" @click="handleAddPermission">新增权限</el-button>
          </div>

          <el-table :data="permissions" v-loading="permissionLoading" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="permissionName" label="权限名称" />
            <el-table-column prop="permissionCode" label="权限代码" />
            <el-table-column prop="description" label="描述" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button size="small" @click="handleEditPermission(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDeletePermission(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination">
            <el-pagination
              v-model:current-page="permPageNum"
              v-model:page-size="permPageSize"
              :total="permTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="fetchPermissions"
              @size-change="fetchPermissions"
            />
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 角色编辑对话框 -->
    <el-dialog v-model="roleDialogVisible" :title="roleDialogTitle" width="500px">
      <el-form :model="roleForm" label-width="100px">
        <el-form-item label="角色名称">
          <el-input v-model="roleForm.roleName" placeholder="例如：ROLE_ADMIN" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="roleForm.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRole">确定</el-button>
      </template>
    </el-dialog>

    <!-- 权限编辑对话框 -->
    <el-dialog v-model="permissionDialogVisible" :title="permissionDialogTitle" width="500px">
      <el-form :model="permissionForm" label-width="100px">
        <el-form-item label="权限名称">
          <el-input v-model="permissionForm.permissionName" placeholder="例如：图书管理" />
        </el-form-item>
        <el-form-item label="权限代码">
          <el-input v-model="permissionForm.permissionCode" placeholder="例如：book:manage" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="permissionForm.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPermission">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getRoles, createRole, updateRole, deleteRole } from '@/api/role'
import { getPermissions, createPermission, updatePermission, deletePermission } from '@/api/permission'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('role')

// 角色相关
const roleLoading = ref(false)
const roles = ref([])
const roleTotal = ref(0)
const rolePageNum = ref(1)
const rolePageSize = ref(10)
const roleDialogVisible = ref(false)
const roleDialogTitle = ref('新增角色')
const roleForm = reactive({
  id: null,
  roleName: '',
  description: ''
})

// 权限相关
const permissionLoading = ref(false)
const permissions = ref([])
const permTotal = ref(0)
const permPageNum = ref(1)
const permPageSize = ref(10)
const permissionDialogVisible = ref(false)
const permissionDialogTitle = ref('新增权限')
const permissionForm = reactive({
  id: null,
  permissionName: '',
  permissionCode: '',
  description: ''
})

// 获取角色列表
const fetchRoles = async () => {
  roleLoading.value = true
  try {
    const res = await getRoles({ page: rolePageNum.value - 1, size: rolePageSize.value })
    roles.value = res.content
    roleTotal.value = res.totalElements
  } catch (error) {
    console.error(error)
  } finally {
    roleLoading.value = false
  }
}

// 获取权限列表
const fetchPermissions = async () => {
  permissionLoading.value = true
  try {
    const res = await getPermissions({ page: permPageNum.value - 1, size: permPageSize.value })
    permissions.value = res.content
    permTotal.value = res.totalElements
  } catch (error) {
    console.error(error)
  } finally {
    permissionLoading.value = false
  }
}

// 角色操作
const handleAddRole = () => {
  roleDialogTitle.value = '新增角色'
  roleForm.id = null
  roleForm.roleName = ''
  roleForm.description = ''
  roleDialogVisible.value = true
}

const handleEditRole = (row) => {
  roleDialogTitle.value = '编辑角色'
  roleForm.id = row.id
  roleForm.roleName = row.roleName
  roleForm.description = row.description
  roleDialogVisible.value = true
}

const submitRole = async () => {
  try {
    if (roleForm.id) {
      await updateRole(roleForm.id, roleForm)
      ElMessage.success('更新成功')
    } else {
      await createRole(roleForm)
      ElMessage.success('创建成功')
    }
    roleDialogVisible.value = false
    fetchRoles()
  } catch (error) {
    console.error(error)
  }
}

const handleDeleteRole = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该角色吗？', '提示', { type: 'warning' })
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    fetchRoles()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

// 权限操作
const handleAddPermission = () => {
  permissionDialogTitle.value = '新增权限'
  permissionForm.id = null
  permissionForm.permissionName = ''
  permissionForm.permissionCode = ''
  permissionForm.description = ''
  permissionDialogVisible.value = true
}

const handleEditPermission = (row) => {
  permissionDialogTitle.value = '编辑权限'
  permissionForm.id = row.id
  permissionForm.permissionName = row.permissionName
  permissionForm.permissionCode = row.permissionCode
  permissionForm.description = row.description
  permissionDialogVisible.value = true
}

const submitPermission = async () => {
  try {
    if (permissionForm.id) {
      await updatePermission(permissionForm.id, permissionForm)
      ElMessage.success('更新成功')
    } else {
      await createPermission(permissionForm)
      ElMessage.success('创建成功')
    }
    permissionDialogVisible.value = false
    fetchPermissions()
  } catch (error) {
    console.error(error)
  }
}

const handleDeletePermission = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该权限吗？', '提示', { type: 'warning' })
    await deletePermission(row.id)
    ElMessage.success('删除成功')
    fetchPermissions()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  fetchRoles()
  fetchPermissions()
})
</script>

<style scoped>
.system-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h3 {
  margin: 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
