<template>
  <div class="my-activity-container">
    <Navbar activeMenu="my" />
    
    <div class="main-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1>我的活动</h1>
        <p>查看和管理您创建的活动</p>
      </div>
      
      <!-- 活动发布按钮 -->
      <div class="publish-section">
        <button class="publish-btn" @click="openPublishModal">发布新活动</button>
      </div>
      
      <!-- 活动列表 -->
      <div class="activity-list-section">
        <div class="activity-list-header">
          <h2>我创建的活动</h2>
        </div>
        
        <div class="activity-table">
          <div class="table-header">
            <span class="col-name">活动名称</span>
            <span class="col-desc">活动描述</span>
            <span class="col-time">开始时间</span>
            <span class="col-status">状态</span>
            <span class="col-actions">操作</span>
          </div>
          
          <div class="table-body">
            <div 
              v-for="(activity, index) in myActivities" 
              :key="index"
              class="table-row"
            >
              <span class="col-name" @click="openActivityDetail(activity)">{{ activity.activityName }}</span>
              <span class="col-desc">{{ activity.activityDesc }}</span>
              <span class="col-time">{{ formatTime(activity.startTime) }}</span>
              <span class="col-status">
                <span class="status-badge" :class="getStatusClass(activity.status)">{{ getStatusText(activity.status) }}</span>
              </span>
              <span class="col-actions">
                <button class="action-button edit-button" @click="openEditModal(activity)">编辑</button>
                <button class="action-button delete-button" @click="confirmDelete(activity.id)">删除</button>
              </span>
            </div>
          </div>
          
          <div v-if="myActivities.length === 0" class="empty-state">
            <div class="empty-icon">📅</div>
            <h3>暂无活动</h3>
            <p>点击"发布新活动"按钮创建您的第一个活动</p>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 活动详情弹窗 -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
      <div class="modal-container detail-modal" @click.stop>
        <button class="modal-close-btn" @click="closeDetailModal">×</button>
        <div class="modal-content">
          <div class="modal-header">
            <h2>{{ selectedActivity?.activityName }}</h2>
          </div>
          <div class="activity-detail">
            <div class="detail-image" v-if="selectedActivity?.coverUrl">
              <img :src="selectedActivity.coverUrl" :alt="selectedActivity.activityName">
            </div>
            <div class="detail-info">
              <p><strong>活动描述：</strong>{{ selectedActivity?.activityDesc }}</p>
              <p><strong>开始时间：</strong>{{ formatTime(selectedActivity?.startTime) }}</p>
              <p><strong>结束时间：</strong>{{ formatTime(selectedActivity?.endTime) }}</p>
              <p><strong>状态：</strong><span class="status-badge" :class="getStatusClass(selectedActivity?.status)">{{ getStatusText(selectedActivity?.status) }}</span></p>
              <p><strong>创建者：</strong>{{ selectedActivity?.creator }}</p>
              <p><strong>创建时间：</strong>{{ formatTime(selectedActivity?.createTime) }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 发布活动弹窗 -->
    <div v-if="showPublishModal" class="modal-overlay" @click="closePublishModal">
      <div class="modal-container publish-modal" @click.stop>
        <button class="modal-close-btn" @click="closePublishModal">×</button>
        <div class="modal-content">
          <div class="modal-header">
            <h2>发布活动</h2>
          </div>
          <div class="publish-form">
            <div class="form-item">
              <label>活动名称：</label>
              <input type="text" v-model="publishForm.activityName" placeholder="请输入活动名称">
            </div>
            <div class="form-item">
              <label>活动描述：</label>
              <textarea v-model="publishForm.activityDesc" placeholder="请输入活动描述" rows="4"></textarea>
            </div>
            <div class="form-item">
              <label>封面图片：</label>
              <div class="image-upload">
                <input type="file" @change="handleImageUpload" accept="image/*" ref="imageInput">
                <div class="upload-preview" v-if="publishForm.coverUrl">
                  <img :src="publishForm.coverUrl" alt="封面图片">
                  <button class="remove-image" @click="removeImage">×</button>
                </div>
                <div class="upload-placeholder" v-else>
                  <span>点击上传图片</span>
                </div>
              </div>
            </div>
            <div class="form-item">
              <label>开始时间：</label>
              <input type="datetime-local" v-model="publishForm.startTime">
            </div>
            <div class="form-item">
              <label>结束时间：</label>
              <input type="datetime-local" v-model="publishForm.endTime">
            </div>
            <div class="form-actions">
              <button class="cancel-btn" @click="closePublishModal">取消</button>
              <button class="submit-btn" @click="submitPublish">发布</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 编辑活动弹窗 -->
    <div v-if="showEditModal" class="modal-overlay" @click="closeEditModal">
      <div class="modal-container publish-modal" @click.stop>
        <button class="modal-close-btn" @click="closeEditModal">×</button>
        <div class="modal-content">
          <div class="modal-header">
            <h2>编辑活动</h2>
          </div>
          <div class="publish-form">
            <div class="form-item">
              <label>活动名称：</label>
              <input type="text" v-model="editForm.activityName" placeholder="请输入活动名称">
            </div>
            <div class="form-item">
              <label>活动描述：</label>
              <textarea v-model="editForm.activityDesc" placeholder="请输入活动描述" rows="4"></textarea>
            </div>
            <div class="form-item">
              <label>封面图片：</label>
              <div class="image-upload">
                <input type="file" @change="handleEditImageUpload" accept="image/*" ref="editImageInput">
                <div class="upload-preview" v-if="editForm.coverUrl">
                  <img :src="editForm.coverUrl" alt="封面图片">
                  <button class="remove-image" @click="removeEditImage">×</button>
                </div>
                <div class="upload-placeholder" v-else>
                  <span>点击上传图片</span>
                </div>
              </div>
            </div>
            <div class="form-item">
              <label>开始时间：</label>
              <input type="datetime-local" v-model="editForm.startTime">
            </div>
            <div class="form-item">
              <label>结束时间：</label>
              <input type="datetime-local" v-model="editForm.endTime">
            </div>
            <div class="form-item">
              <label>活动状态：</label>
              <select v-model="editForm.status" class="status-select">
                <option value="0">即将开始</option>
                <option value="1">进行中</option>
                <option value="2">已结束</option>
              </select>
            </div>
            <div class="form-actions">
              <button class="cancel-btn" @click="closeEditModal">取消</button>
              <button class="submit-btn" @click="submitEdit">保存</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 删除确认对话框 -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click="closeDeleteConfirm">
      <div class="modal-container delete-modal" @click.stop>
        <div class="modal-content">
          <div class="modal-header">
            <h2>确认删除</h2>
          </div>
          <div class="delete-message">
            <p>确定要删除这个活动吗？此操作不可恢复。</p>
          </div>
          <div class="form-actions">
            <button class="cancel-btn" @click="closeDeleteConfirm">取消</button>
            <button class="delete-confirm-btn" @click="submitDelete">删除</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getAllActivities, createActivity, updateActivity, deleteActivity, uploadImage } from '../api/activity';
import Navbar from './Navbar.vue';

// 用户名
const username = ref('');

// 我的活动数据
const myActivities = ref([]);
const loading = ref(false);

// 弹窗相关状态
const showDetailModal = ref(false);
const selectedActivity = ref(null);

// 发布活动相关状态
const showPublishModal = ref(false);
const publishForm = ref({
  id: 0,
  activityName: '',
  activityDesc: '',
  coverUrl: '',
  startTime: '',
  endTime: '',
  status: 0,
  hotStatus: 0,
  creator: '',
  createTime: '',
  updateTime: ''
});

// 编辑活动相关状态
const showEditModal = ref(false);
const editForm = ref({
  id: 0,
  activityName: '',
  activityDesc: '',
  coverUrl: '',
  startTime: '',
  endTime: '',
  status: 0,
  hotStatus: 0,
  creator: '',
  createTime: '',
  updateTime: ''
});

// 编辑图片文件
const editImageFile = ref(null);

// 删除确认相关状态
const showDeleteConfirm = ref(false);
const deleteActivityId = ref(null);

// 图片文件
const imageFile = ref(null);

// 格式化时间
const formatTime = (time) => {
  if (!time) return '无';
  const date = new Date(time);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '即将开始',
    1: '进行中',
    2: '已结束'
  };
  return statusMap[status] || '未知';
};

// 获取状态类名
const getStatusClass = (status) => {
  const classMap = {
    0: 'status-upcoming',
    1: 'status-active',
    2: 'status-ended'
  };
  return classMap[status] || 'status-unknown';
};

// 获取我的活动
const fetchMyActivities = async () => {
  loading.value = true;
  try {
    const response = await getAllActivities();
    if ((response.code === 200 || response.code === 0) && response.data && response.data.data) {
      // 筛选出当前用户创建的活动
      myActivities.value = response.data.data.filter(activity => activity.creator === username.value);
    } else {
      myActivities.value = [];
    }
  } catch (error) {
    console.error('获取活动列表失败:', error);
    myActivities.value = [];
  } finally {
    loading.value = false;
  }
};

// 打开活动详情
const openActivityDetail = (activity) => {
  selectedActivity.value = activity;
  showDetailModal.value = true;
};

// 关闭活动详情
const closeDetailModal = () => {
  showDetailModal.value = false;
  selectedActivity.value = null;
};

// 打开发布活动弹窗
const openPublishModal = () => {
  showPublishModal.value = true;
  publishForm.value = {
    id: 0,
    activityName: '',
    activityDesc: '',
    coverUrl: '',
    startTime: '',
    endTime: '',
    status: 0,
    hotStatus: 0,
    creator: username.value,
    createTime: '',
    updateTime: ''
  };
  imageFile.value = null;
};

// 关闭发布活动弹窗
const closePublishModal = () => {
  showPublishModal.value = false;
  publishForm.value = {
    id: 0,
    activityName: '',
    activityDesc: '',
    coverUrl: '',
    startTime: '',
    endTime: '',
    status: 0,
    hotStatus: 0,
    creator: '',
    createTime: '',
    updateTime: ''
  };
  imageFile.value = null;
};

// 处理图片上传
const handleImageUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    imageFile.value = file;
    const reader = new FileReader();
    reader.onload = (e) => {
      publishForm.value.coverUrl = e.target.result;
    };
    reader.readAsDataURL(file);
  }
};

// 移除图片
const removeImage = (event) => {
  publishForm.value.coverUrl = '';
  imageFile.value = null;
  if (event && event.target && event.target.tagName === 'BUTTON') {
    event.target.previousElementSibling.src = '';
  }
};

// 提交发布活动
const submitPublish = async () => {
  if (!publishForm.value.activityName || !publishForm.value.activityDesc) {
    alert('请填写活动名称和描述');
    return;
  }
  
  if (!publishForm.value.startTime || !publishForm.value.endTime) {
    alert('请选择开始时间和结束时间');
    return;
  }
  
  try {
    // 先上传图片，获取图片路径
    let coverUrl = '';
    if (imageFile.value) {
      try {
        const uploadResponse = await uploadImage(imageFile.value);
        if (uploadResponse.code === 200 || uploadResponse.code === 0) {
          coverUrl = uploadResponse.data;
        } else {
          alert('图片上传失败：' + uploadResponse.message);
          return;
        }
      } catch (error) {
        console.error('上传图片失败:', error);
        alert('图片上传失败，请稍后重试');
        return;
      }
    }
    
    // 构建活动对象
    const activity = {
      activityName: publishForm.value.activityName,
      activityDesc: publishForm.value.activityDesc,
      coverUrl: coverUrl,
      startTime: publishForm.value.startTime,
      endTime: publishForm.value.endTime,
      status: publishForm.value.status,
      hotStatus: publishForm.value.hotStatus,
      creator: publishForm.value.creator || username.value
    };
    
    // 调用创建活动接口
    const response = await createActivity(activity);
    if (response.code === 200 || response.code === 0) {
      alert('活动发布成功！');
      closePublishModal();
      await fetchMyActivities();
    } else {
      alert('活动发布失败：' + response.message);
    }
  } catch (error) {
    console.error('发布活动失败:', error);
    alert('活动发布失败，请稍后重试');
  }
};

// 打开编辑活动弹窗
const openEditModal = (activity) => {
  showEditModal.value = true;
  editForm.value = {
    ...activity,
    // 转换时间格式为 datetime-local 支持的格式
    startTime: activity.startTime ? new Date(activity.startTime).toISOString().slice(0, 16) : '',
    endTime: activity.endTime ? new Date(activity.endTime).toISOString().slice(0, 16) : ''
  };
  editImageFile.value = null;
};

// 关闭编辑活动弹窗
const closeEditModal = () => {
  showEditModal.value = false;
  editForm.value = {
    id: 0,
    activityName: '',
    activityDesc: '',
    coverUrl: '',
    startTime: '',
    endTime: '',
    status: 0,
    hotStatus: 0,
    creator: '',
    createTime: '',
    updateTime: ''
  };
  editImageFile.value = null;
};

// 处理编辑图片上传
const handleEditImageUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    editImageFile.value = file;
    const reader = new FileReader();
    reader.onload = (e) => {
      editForm.value.coverUrl = e.target.result;
    };
    reader.readAsDataURL(file);
  }
};

// 移除编辑图片
const removeEditImage = () => {
  editForm.value.coverUrl = '';
  editImageFile.value = null;
};

// 提交编辑活动
const submitEdit = async () => {
  if (!editForm.value.activityName || !editForm.value.activityDesc) {
    alert('请填写活动名称和描述');
    return;
  }
  
  if (!editForm.value.startTime || !editForm.value.endTime) {
    alert('请选择开始时间和结束时间');
    return;
  }
  
  try {
    // 先上传图片，获取图片路径
    let coverUrl = editForm.value.coverUrl;
    if (editImageFile.value) {
      try {
        const uploadResponse = await uploadImage(editImageFile.value);
        if (uploadResponse.code === 200 || uploadResponse.code === 0) {
          coverUrl = uploadResponse.data;
        } else {
          alert('图片上传失败：' + uploadResponse.message);
          return;
        }
      } catch (error) {
        console.error('上传图片失败:', error);
        alert('图片上传失败，请稍后重试');
        return;
      }
    }
    
    // 构建活动对象
    const activity = {
      id: editForm.value.id,
      activityName: editForm.value.activityName,
      activityDesc: editForm.value.activityDesc,
      coverUrl: coverUrl,
      startTime: editForm.value.startTime,
      endTime: editForm.value.endTime,
      status: editForm.value.status,
      hotStatus: editForm.value.hotStatus,
      creator: editForm.value.creator
    };
    
    // 调用更新活动接口
    const response = await updateActivity(activity);
    if (response.code === 200 || response.code === 0) {
      alert('活动编辑成功！');
      closeEditModal();
      await fetchMyActivities();
    } else {
      alert('活动编辑失败：' + response.message);
    }
  } catch (error) {
    console.error('编辑活动失败:', error);
    alert('活动编辑失败，请稍后重试');
  }
};

// 确认删除
const confirmDelete = (id) => {
  deleteActivityId.value = id;
  showDeleteConfirm.value = true;
};

// 关闭删除确认
const closeDeleteConfirm = () => {
  showDeleteConfirm.value = false;
  deleteActivityId.value = null;
};

// 提交删除
const submitDelete = async () => {
  if (!deleteActivityId.value) {
    return;
  }
  
  try {
    const response = await deleteActivity(deleteActivityId.value);
    if (response.code === 200 || response.code === 0) {
      alert('活动删除成功！');
      closeDeleteConfirm();
      await fetchMyActivities();
    } else {
      alert('活动删除失败：' + response.message);
    }
  } catch (error) {
    console.error('删除活动失败:', error);
    alert('活动删除失败，请稍后重试');
  }
};

// 组件挂载时的初始化
onMounted(() => {
  // 获取用户信息
  const user = localStorage.getItem('user');
  if (user) {
    try {
      const userObj = JSON.parse(user);
      username.value = userObj.username || '用户';
    } catch (error) {
      console.error('解析用户信息失败:', error);
      username.value = '用户';
    }
  } else {
    username.value = '用户';
  }
  
  // 获取我的活动
  fetchMyActivities();
});
</script>

<style scoped>
.my-activity-container {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-attachment: fixed;
}

.main-content {
  flex: 1;
  margin-left: 260px;
  padding: 40px;
  transition: margin-left 0.3s ease;
  min-height: 100vh;
  background-color: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 30px 0;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.page-header h1 {
  margin: 0 0 10px 0;
  font-size: 32px;
  font-weight: 700;
  color: #333333;
}

.page-header p {
  margin: 0;
  font-size: 16px;
  color: #666666;
}

/* 发布活动按钮 */
.publish-section {
  margin-bottom: 30px;
  text-align: right;
}

.publish-btn {
  padding: 12px 24px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.publish-btn:hover {
  background: #2980b9;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

/* 活动列表区域 */
.activity-list-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 30px;
  width: 100%;
}

.activity-list-header {
  margin-bottom: 20px;
}

.activity-list-header h2 {
  margin: 0;
  color: #333333;
  font-size: 20px;
  font-weight: 600;
}

/* 活动表格 */
.activity-table {
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 3fr 2fr 1fr 1fr;
  padding: 15px 20px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  font-weight: 600;
  color: #495057;
  font-size: 14px;
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 3fr 2fr 1fr 1fr;
  padding: 15px 20px;
  border-bottom: 1px solid #e9ecef;
  transition: background-color 0.3s ease;
  font-size: 14px;
  color: #333333;
}

.table-row:hover {
  background-color: #f8f9fa;
}

.col-name {
  font-weight: 500;
  cursor: pointer;
  transition: color 0.3s ease;
}

.col-name:hover {
  color: #3498db;
}

.col-desc {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #666666;
}

.col-time {
  color: #666666;
}

.col-status {
  display: flex;
  align-items: center;
}

/* 状态徽章 */
.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  text-transform: capitalize;
}

.status-upcoming {
  background-color: #e3f2fd;
  color: #1976d2;
}

.status-active {
  background-color: #e8f5e8;
  color: #2e7d32;
}

.status-ended {
  background-color: #f5f5f5;
  color: #616161;
}

.status-unknown {
  background-color: #ffebee;
  color: #c62828;
}

/* 操作列 */
.col-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.action-button {
  padding: 4px 12px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.edit-button {
  background-color: #3498db;
  color: white;
}

.edit-button:hover {
  background-color: #2980b9;
}

.delete-button {
  background-color: #e74c3c;
  color: white;
}

.delete-button:hover {
  background-color: #c0392b;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #666666;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-state h3 {
  margin: 0 0 10px 0;
  font-size: 20px;
  color: #333333;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-container {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  position: relative;
  max-height: 90vh;
  overflow-y: auto;
}

.detail-modal {
  width: 800px;
  max-width: 90%;
  height: 600px;
  max-height: 90vh;
}

.publish-modal {
  width: 600px;
  max-width: 90%;
  height: auto;
  max-height: 90vh;
}

.delete-modal {
  width: 400px;
  max-width: 90%;
  height: auto;
  max-height: 90vh;
}

.modal-close-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666666;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
  z-index: 10;
}

.modal-close-btn:hover {
  background-color: #f0f0f0;
  color: #333333;
}

.modal-content {
  padding: 30px;
}

.modal-header {
  margin-bottom: 20px;
  text-align: center;
}

.modal-header h2 {
  margin: 0;
  color: #333333;
  font-size: 20px;
  font-weight: 600;
}

/* 活动详情 */
.activity-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-image {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.detail-image img {
  width: 100%;
  height: 300px;
  object-fit: cover;
}

.detail-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-info p {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
}

.detail-info strong {
  color: #333333;
  font-weight: 600;
}

/* 发布表单 */
.publish-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-weight: 500;
  color: #333333;
  font-size: 14px;
}

.form-item input,
.form-item textarea {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s ease;
}

.form-item input:focus,
.form-item textarea:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.1);
}

.form-item textarea {
  resize: vertical;
  min-height: 100px;
}

/* 图片上传 */
.image-upload {
  position: relative;
  width: 100%;
  min-height: 100px;
  border: 2px dashed #ddd;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.image-upload:hover {
  border-color: #3498db;
  background-color: #f8f9fa;
}

.image-upload input[type="file"] {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
  z-index: 1;
}

.upload-preview {
  position: relative;
  width: 100%;
  max-height: 200px;
  overflow: hidden;
  border-radius: 6px;
}

.upload-preview img {
  width: 100%;
  height: auto;
  object-fit: cover;
}

.remove-image {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 16px;
  color: #333;
  transition: all 0.2s ease;
  z-index: 2;
}

.remove-image:hover {
  background: white;
  color: #e74c3c;
  transform: scale(1.1);
}

.upload-placeholder {
  text-align: center;
  color: #666666;
  font-size: 14px;
  padding: 20px;
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 20px;
}

.cancel-btn {
  padding: 10px 24px;
  background-color: #f8f9fa;
  color: #333333;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background-color: #e9ecef;
  border-color: #ced4da;
}

.submit-btn {
  padding: 10px 24px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  background-color: #2980b9;
}

/* 删除确认对话框样式 */
.delete-message {
  padding: 20px;
  text-align: center;
}

.delete-message p {
  margin: 0;
  font-size: 16px;
  color: #333;
  line-height: 1.6;
}

.delete-confirm-btn {
  padding: 10px 24px;
  background-color: #e74c3c;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.delete-confirm-btn:hover {
  background-color: #c0392b;
}

/* 状态选择样式 */
.status-select {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s ease;
  cursor: pointer;
}

.status-select:focus {
  border-color: #3498db;
}

/* 响应式 */
@media (max-width: 768px) {
  .main-content {
    margin-left: 70px;
    padding: 20px;
  }
  
  .page-header h1 {
    font-size: 24px;
  }
  
  .activity-list-section {
    padding: 20px;
  }
  
  .table-header {
    grid-template-columns: 1fr 1fr 1fr 1fr 1fr;
    font-size: 12px;
    padding: 10px 15px;
  }
  
  .table-row {
    grid-template-columns: 1fr 1fr 1fr 1fr 1fr;
    font-size: 12px;
    padding: 10px 15px;
  }
  
  .col-desc {
    display: none;
  }
  
  .col-actions {
    flex-direction: column;
    gap: 4px;
  }
  
  .action-button {
    padding: 2px 8px;
    font-size: 10px;
  }
  
  .detail-modal {
    width: 95vw;
    max-width: 95vw;
    height: 80vh;
  }
  
  .publish-modal {
    width: 95vw;
    max-width: 95vw;
  }
  
  .modal-content {
    padding: 20px;
  }
}
</style>