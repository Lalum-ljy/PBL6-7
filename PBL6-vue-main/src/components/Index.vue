<template>
  <div class="index-container">
    <!-- 左侧导航栏 -->
    <Navbar :activeMenu="activeMenu" />
    
    <!-- 右侧主内容区 -->
    <div class="main-content">
      <!-- 顶部搜索栏和筛选 -->
      <div class="search-bar">
        <div class="search-input-container">
          <input type="text" v-model="searchKeyword" placeholder="搜索活动..." @keyup.enter="handleSearch">
          <button v-if="searchKeyword" class="clear-button" @click="clearSearch">×</button>
        </div>
        <button class="search-button" @click="handleSearch">搜索</button>
        <div class="filter-container">
          <select v-model="statusFilter" class="filter-select" @change="handleStatusChange">
            <option value="-1">全部状态</option>
            <option value="0">即将开始</option>
            <option value="1">进行中</option>
            <option value="2">已结束</option>
          </select>
          <button class="filter-button" @click="showDateFilter = !showDateFilter">时间筛选</button>
          <div v-if="showDateFilter" class="date-filter">
            <input type="datetime-local" v-model="startDate" class="date-input">
            <span>至</span>
            <input type="datetime-local" v-model="endDate" class="date-input">
            <button class="apply-button" @click="applyDateFilter">应用</button>
          </div>
        </div>
        <button class="publish-button" @click="openPublishModal">+发布活动</button>
      </div>
      
      <!-- 图片轮播和通知展示区域 -->
      <div v-if="!isSearching" class="carousel-section">
        <!-- 图片轮播区域 -->
        <div class="carousel">
          <div class="carousel-container" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
            <div class="carousel-slide" v-for="(slide, index) in carouselSlides" :key="index">
              <img :src="slide.image" :alt="slide.title">
              <div class="carousel-caption">
                <h3>{{ slide.title }}</h3>
                <p>{{ slide.description }}</p>
              </div>
            </div>
          </div>
          <div class="carousel-indicators">
            <span 
              v-for="(slide, index) in carouselSlides" 
              :key="index"
              :class="{ active: currentSlide === index }"
              @click="currentSlide = index"
            ></span>
          </div>
        </div>
        
        <!-- 通知展示区域 -->
        <div class="notification-area">
          <div class="notification-header">
            <h3>通知公告</h3>
          </div>
          <div class="notification-list">
            <div class="notification-item" v-for="(notice, index) in notifications" :key="index">
              <span class="notification-dot"></span>
              <span class="notification-title">{{ notice.title }}</span>
              <span class="notification-time">{{ formatNoticeTime(notice.createTime) }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 热门活动 -->
      <div v-if="!isSearching" class="hot-activities">
        <div class="hot-activities-header">
          <h2>热门活动</h2>
          <router-link to="/hot" class="view-more">查看更多</router-link>
        </div>
        <div class="hot-activity-cards">
          <div class="hot-activity-card" v-for="(activity, index) in hotActivities.slice(0, 4)" :key="index" @click="openActivityDetail(activity)">
            <div class="hot-card-image">
              <img :src="activity.image" :alt="activity.title">
            </div>
            <div class="hot-card-content">
              <h3>{{ activity.title }}</h3>
              <p class="hot-card-description">{{ activity.description }}</p>
              <div class="hot-card-footer">
                <span class="activity-date">{{ activity.date }}</span>
                <span class="activity-status" :class="activity.statusTextClass">{{ activity.statusText }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 活动列表 -->
      <div class="activity-list-section">
        <div class="activity-list-header">
          <h2>所有活动</h2>
        </div>
        <div class="activity-table">
          <div class="table-header">
            <span class="col-name">活动名称</span>
            <span class="col-desc">活动描述</span>
            <span class="col-time">开始时间</span>
            <span class="col-status">状态</span>
            <span class="col-creator">创建者</span>
          </div>
          <div class="table-row" v-for="(activity, index) in paginatedActivities" :key="index" @click="openActivityDetail(activity)">
            <span class="col-name">{{ activity.activityName }}</span>
            <span class="col-desc">{{ activity.activityDesc }}</span>
            <span class="col-time">{{ formatTime(activity.startTime) }}</span>
            <span class="col-status">
              <span class="status-badge" :class="getStatusClass(activity.status)">{{ getStatusText(activity.status) }}</span>
            </span>
            <span class="col-creator">{{ activity.creator }}</span>
          </div>
        </div>
        <!-- 分页 -->
        <div class="pagination" v-if="allActivities.length > 0">
          <button class="page-btn" :disabled="currentPage === 1" @click="handlePageChange(currentPage - 1)">上一页</button>
          <span class="page-info">第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
          <button class="page-btn" :disabled="currentPage >= totalPages" @click="handlePageChange(currentPage + 1)">下一页</button>
        </div>
        <div v-if="loading" class="loading">加载中...</div>
      </div>
    </div>
    
    <!-- 活动详情弹窗 -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
      <div class="modal-container" @click.stop>
        <button class="modal-close-btn" @click="closeDetailModal">×</button>
        <div class="modal-content">
          <div class="modal-header">
            <h2>{{ selectedActivity?.activityName || selectedActivity?.title }}</h2>
            <span class="modal-status" :class="getStatusClass(selectedActivity?.status)">
              {{ getStatusText(selectedActivity?.status) }}
            </span>
          </div>
          <div class="modal-image" v-if="selectedActivity?.coverUrl || selectedActivity?.image">
            <img :src="selectedActivity?.coverUrl || selectedActivity?.image" :alt="selectedActivity?.activityName || selectedActivity?.title">
          </div>
          <div class="modal-info">
            <div class="info-item">
              <label>活动描述：</label>
              <p>{{ selectedActivity?.activityDesc || selectedActivity?.description }}</p>
            </div>
            <div class="info-item">
              <label>开始时间：</label>
              <p>{{ formatTime(selectedActivity?.startTime) }}</p>
            </div>
            <div class="info-item">
              <label>结束时间：</label>
              <p>{{ formatTime(selectedActivity?.endTime) }}</p>
            </div>
            <div class="info-item">
              <label>创建者：</label>
              <p>{{ selectedActivity?.creator }}</p>
            </div>
            <div class="info-item">
              <label>创建时间：</label>
              <p>{{ formatTime(selectedActivity?.createTime) }}</p>
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { getAllActivities, getHotActivities, getActivitiesByStatus, getActivitiesByTimeRange, searchActivities, createActivity, getNotices, uploadImage } from '../api/activity';
import Navbar from './Navbar.vue';

// 全局配置
const IMG_DOMAIN = 'http://localhost:8080';

// 导航菜单状态
const activeMenu = ref('home');

// 搜索关键词
const searchKeyword = ref('');

// 用户名
const username = ref('');

// 路由
const router = useRouter();

// 加载状态
const loading = ref(false);

// 筛选相关状态
const statusFilter = ref('-1');
const showDateFilter = ref(false);
const startDate = ref('');
const endDate = ref('');

// 搜索和筛选状态
const isSearching = ref(false);

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

// 图片文件
const imageFile = ref(null);

// 轮播图数据
const currentSlide = ref(0);
const carouselSlides = ref([
  {
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=colorful%20event%20banner%20with%20people%20celebrating&image_size=landscape_16_9',
    title: '夏季音乐节',
    description: '一场视听盛宴，不容错过！'
  },
  {
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=tech%20conference%20with%20speakers%20and%20audience&image_size=landscape_16_9',
    title: '科技创新峰会',
    description: '探索未来科技趋势'
  },
  {
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=sports%20event%20with%20athletes%20competing&image_size=landscape_16_9',
    title: '城市马拉松',
    description: '挑战自我，超越极限'
  }
]);

// 通知数据（从接口获取）
const notifications = ref([]);

// 热门活动数据（从接口获取前4个）
const hotActivities = ref([]);

// 所有活动数据（从接口获取）
const allActivities = ref([]);

// 分页相关
const currentPage = ref(1);
const pageSize = 10;
const totalPages = ref(1);

// 计算当前页的活动数据
const paginatedActivities = computed(() => {
  return allActivities.value;
});

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '';
  const date = new Date(timeStr);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 获取状态样式类
const getStatusClass = (status) => {
  switch (status) {
    case 0: return 'upcoming';
    case 1: return 'ongoing';
    case 2: return 'ended';
    default: return 'upcoming';
  }
};

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 0: return '即将开始';
    case 1: return '进行中';
    case 2: return '已结束';
    default: return '即将开始';
  }
};

// 格式化通知时间
const formatNoticeTime = (timeStr) => {
  if (!timeStr) return '';
  const date = new Date(timeStr);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 获取活动列表
const fetchActivities = async (status = -1, startTime = null, endTime = null) => {
  loading.value = true;
  try {
    let allResponse;
    if (status !== -1) {
      allResponse = await getActivitiesByStatus(status);
    } else if (startTime && endTime) {
      allResponse = await getActivitiesByTimeRange(startTime, endTime);
    } else {
      allResponse = await getAllActivities(currentPage.value, 10);
    }
    
    const hotResponse = await getHotActivities();
    const noticeResponse = await getNotices();
    
    if ((allResponse.code === 200 || allResponse.code === 0) && allResponse.data) {
      // 处理分页接口返回的数据结构
      const responseData = allResponse.data.data || allResponse.data;
      // 为 coverUrl 字段添加域名
      allActivities.value = responseData.map(activity => ({
        ...activity,
        coverUrl: activity.coverUrl ? IMG_DOMAIN + activity.coverUrl : activity.coverUrl
      }));
      // 假设接口返回总页数，如果没有返回，可以根据总数据量和每页大小计算
      // 这里暂时假设总页数为 1，实际应该从接口返回数据中获取
      // 示例：如果接口返回 { data: { data: [...], totalPages: 5 } }，则：
      // totalPages.value = allResponse.data.totalPages || 1;
      totalPages.value = allResponse.data.totalPages || Math.ceil(responseData.length / pageSize) || 1;
    }
    
    if ((hotResponse.code === 200 || hotResponse.code === 0) && hotResponse.data) {
      const hotData = hotResponse.data.data || hotResponse.data;
      if (hotData && hotData.length > 0) {
        hotActivities.value = hotData.map(item => ({
          id: item.id,
          image: item.coverUrl ? IMG_DOMAIN + item.coverUrl : 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20event%20image&image_size=landscape_4_3',
          title: item.activityName,
          description: item.activityDesc,
          date: formatTime(item.startTime),
          status: getStatusClass(item.status),
          statusText: getStatusText(item.status),
          statusTextClass: getStatusClass(item.status),
          activityName: item.activityName,
          activityDesc: item.activityDesc,
          coverUrl: item.coverUrl ? IMG_DOMAIN + item.coverUrl : item.coverUrl,
          startTime: item.startTime,
          endTime: item.endTime,
          status: item.status,
          creator: item.creator,
          createTime: item.createTime
        }));
      } else {
        hotActivities.value = [];
      }
    }
    
    if ((noticeResponse.code === 200 || noticeResponse.code === 0) && noticeResponse.data) {
      const noticeData = noticeResponse.data.data || noticeResponse.data;
      if (noticeData && noticeData.length > 0) {
        notifications.value = noticeData;
      } else {
        notifications.value = [];
      }
    }
  } catch (error) {
    console.error('获取活动列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 搜索活动
const handleSearch = async () => {
  const keyword = (searchKeyword.value || '').trim();

  // 清空搜索：恢复默认列表
  if (!keyword) {
    isSearching.value = false;
    currentPage.value = 1;
    statusFilter.value = '-1';
    showDateFilter.value = false;
    startDate.value = '';
    endDate.value = '';
    await fetchActivities();
    return;
  }

  isSearching.value = true;
  loading.value = true;
  try {
    const res = await searchActivities(keyword);
    if ((res.code === 200 || res.code === 0) && res.data) {
      allActivities.value = res.data;
      currentPage.value = 1;
      statusFilter.value = '-1';
      showDateFilter.value = false;
      startDate.value = '';
      endDate.value = '';
    } else {
      allActivities.value = [];
      currentPage.value = 1;
    }
  } catch (error) {
    console.error('搜索活动失败:', error);
  } finally {
    loading.value = false;
  }
};

// 清空搜索
const clearSearch = async () => {
  searchKeyword.value = '';
  isSearching.value = false;
  currentPage.value = 1;
  statusFilter.value = '-1';
  showDateFilter.value = false;
  startDate.value = '';
  endDate.value = '';
  await fetchActivities();
};

// 处理分页变化
const handlePageChange = async (page) => {
  currentPage.value = page;
  await fetchActivities();
};

// 打开活动详情弹窗
const openActivityDetail = (activity) => {
  selectedActivity.value = activity;
  showDetailModal.value = true;
};

// 关闭活动详情弹窗
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
    creator: username.value || '',
    createTime: '',
    updateTime: ''
  };
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
  
  console.log('发布活动数据:', publishForm.value);
  
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
    
    // 调用创建活动接口（使用 JSON 格式）
    const response = await createActivity(activity);
    console.log('发布活动响应:', response);
    if (response.code === 200 || response.code === 0) {
      alert('活动发布成功！');
      closePublishModal();
      await fetchActivities();
    } else {
      alert('活动发布失败：' + response.message);
    }
  } catch (error) {
    console.error('发布活动失败:', error);
    alert('活动发布失败，请稍后重试');
  }
};

// 应用时间筛选
const applyDateFilter = () => {
  if (startDate.value && endDate.value) {
    // 转换为 ISO 格式的时间字符串
    const startTime = new Date(startDate.value).toISOString();
    const endTime = new Date(endDate.value).toISOString();
    isSearching.value = true;
    currentPage.value = 1;
    fetchActivities(-1, startTime, endTime);
  }
  showDateFilter.value = false;
};

// 监听状态筛选变化
const handleStatusChange = () => {
  isSearching.value = statusFilter.value !== '-1';
  currentPage.value = 1;
  fetchActivities(parseInt(statusFilter.value));
};

// 自动轮播
onMounted(() => {
  // 从本地存储获取用户信息
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
  
  // 获取活动列表
  fetchActivities();
  
  // 启动轮播
  setInterval(() => {
    currentSlide.value = (currentSlide.value + 1) % carouselSlides.value.length;
  }, 3000);
});
</script>

<style scoped>
.index-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 右侧主内容区 */
.main-content {
  flex: 1;
  margin-left: 260px;
  padding: 20px 20px 20px 20px;
  overflow-y: auto;
  background-color: #f5f5f5;
  height: 100vh;
  box-sizing: border-box;
  width: calc(100% - 260px);
  position: absolute;
  top: 0;
  right: 0;
  transition: all 0.3s ease;
}

/* 当导航栏折叠时 */
.sidebar.collapsed + .main-content {
  margin-left: 70px;
  width: calc(100% - 70px);
}

/* 热门活动 */
.hot-activities {
  margin-bottom: 30px;
  width: 100%;
}

.hot-activities-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.hot-activities h2 {
  margin: 0;
  color: #333333;
  font-size: 20px;
}

.view-more {
  color: #4299e1;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

.view-more:hover {
  color: #3182ce;
  text-decoration: underline;
}

.hot-activity-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  width: 100%;
}

.hot-activity-card {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.hot-activity-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.hot-card-image {
  height: 120px;
  overflow: hidden;
}

.hot-card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hot-card-content {
  padding: 12px;
}

.hot-card-content h3 {
  margin: 0 0 8px 0;
  color: #333333;
  font-size: 14px;
}

.hot-card-description {
  margin: 0 0 10px 0;
  color: #666666;
  font-size: 12px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.hot-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
}

.hot-card-footer .activity-date {
  color: #999999;
}

/* 活动列表区域 */
.activity-list-section {
  margin-bottom: 30px;
  width: 100%;
}

.activity-list-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 20px;
}

.activity-list-section h2 {
  margin: 0;
  color: #333333;
  font-size: 20px;
}

.activity-table {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 100%;
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
  cursor: pointer;
}

.table-row:hover {
  background-color: #f8f9fa;
}

.table-row:last-child {
  border-bottom: none;
}

.col-name {
  font-weight: 500;
  color: #333333;
}

.col-desc {
  color: #666666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.col-time {
  color: #666666;
}

.col-status {
  display: flex;
  align-items: center;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.upcoming {
  background-color: #e3f2fd;
  color: #1976d2;
}

.status-badge.ongoing {
  background-color: #e8f5e8;
  color: #388e3c;
}

.status-badge.ended {
  background-color: #ffebee;
  color: #d32f2f;
}

.col-creator {
  color: #666666;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 20px;
  padding: 15px;
}

.page-btn {
  padding: 8px 16px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background-color: #2980b9;
}

.page-btn:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666666;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #666666;
  font-size: 14px;
}

/* 搜索栏 */
.search-bar {
  display: flex;
  margin-bottom: 20px;
  gap: 10px;
  width: 100%;
  box-sizing: border-box;
  align-items: center;
  flex-wrap: wrap;
}

.search-input-container {
  position: relative;
  flex: 1;
}

.search-input-container input {
  width: 100%;
  padding: 12px 16px;
  padding-right: 40px;
  border: 1px solid #dddddd;
  border-radius: 6px;
  font-size: 16px;
  box-sizing: border-box;
}

.clear-button {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  font-size: 20px;
  color: #999;
  cursor: pointer;
  padding: 0 5px;
  line-height: 1;
  transition: color 0.3s ease;
}

.clear-button:hover {
  color: #333;
}

.search-button {
  padding: 12px 24px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  box-sizing: border-box;
}

.search-button:hover {
  background-color: #2980b9;
}

.filter-container {
  display: flex;
  align-items: center;
  gap: 10px;
  position: relative;
}

.filter-select {
  padding: 12px 16px;
  border: 1px solid #dddddd;
  border-radius: 6px;
  font-size: 16px;
  outline: none;
  cursor: pointer;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.filter-select:focus {
  border-color: #3498db;
}

.filter-button {
  padding: 12px 24px;
  background-color: #95a5a6;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  box-sizing: border-box;
}

.filter-button:hover {
  background-color: #7f8c8d;
}

.date-filter {
  position: absolute;
  top: 100%;
  left: 0;
  background-color: white;
  padding: 15px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
}

.date-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s ease;
}

.date-input:focus {
  border-color: #3498db;
}

.apply-button {
  padding: 8px 16px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s ease;
}

.apply-button:hover {
  background-color: #2980b9;
}

.publish-button {
  padding: 12px 24px;
  background-color: #27ae60;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  box-sizing: border-box;
}

.publish-button:hover {
  background-color: #219a52;
}

/* 轮播图和通知区域容器 */
.carousel-section {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  width: 100%;
}

/* 通知展示区域 */
.notification-area {
  width: 280px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  height: 300px;
  box-sizing: border-box;
}

.notification-header {
  border-bottom: 2px solid #e8e8e8;
  padding-bottom: 12px;
  margin-bottom: 15px;
}

.notification-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.notification-list {
  flex: 1;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item:hover {
  background-color: #f8f9fa;
  padding-left: 8px;
}

.notification-dot {
  width: 8px;
  height: 8px;
  background-color: #4299e1;
  border-radius: 50%;
  margin-right: 10px;
  flex-shrink: 0;
}

.notification-title {
  flex: 1;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notification-time {
  font-size: 12px;
  color: #999;
  margin-left: 10px;
  flex-shrink: 0;
}

/* 轮播图 */
.carousel {
  position: relative;
  flex: 1;
  height: 300px;
  overflow: hidden;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
}

.carousel-container {
  display: flex;
  height: 100%;
  transition: transform 0.5s ease;
}

.carousel-slide {
  flex: 0 0 100%;
  position: relative;
  height: 100%;
}

.carousel-slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-caption {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
  color: white;
  padding: 30px;
}

.carousel-caption h3 {
  margin: 0 0 10px 0;
  font-size: 24px;
}

.carousel-caption p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.carousel-indicators {
  position: absolute;
  bottom: 15px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}

.carousel-indicators span {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s ease;
}

.carousel-indicators span.active {
  background-color: white;
  width: 30px;
  border-radius: 6px;
}

/* 活动列表 */
.activity-list {
  margin-bottom: 30px;
}

.activity-list h2 {
  margin-bottom: 20px;
  color: #333333;
  font-size: 20px;
}

.activity-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.activity-card {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.activity-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.card-image {
  height: 180px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.activity-card:hover .card-image img {
  transform: scale(1.05);
}

.card-content {
  padding: 15px;
}

.card-content h3 {
  margin: 0 0 10px 0;
  color: #333333;
  font-size: 18px;
}

.card-description {
  margin: 0 0 15px 0;
  color: #666666;
  font-size: 14px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999999;
}

.activity-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.activity-status.upcoming {
  background-color: #e3f2fd;
  color: #1976d2;
}

.activity-status.ongoing {
  background-color: #e8f5e8;
  color: #388e3c;
}

.activity-status.ended {
  background-color: #ffebee;
  color: #d32f2f;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 200px;
  }
  
  .main-content {
    margin-left: 200px;
    padding: 15px;
  }
  
  .carousel {
    height: 200px;
  }
  
  .activity-cards {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .sidebar {
    width: 100%;
    height: auto;
    position: relative;
  }
  
  .main-content {
    margin-left: 0;
    height: calc(100vh - 120px);
  }
  
  .index-container {
    flex-direction: column;
  }
  
  .nav-menu ul {
    display: flex;
    overflow-x: auto;
  }
  
  .nav-menu li {
    white-space: nowrap;
    border-right: 1px solid rgba(255, 255, 255, 0.1);
  }
}

/* 活动详情弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.modal-container {
  width: 70%;
  height: 70%;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-close-btn {
  position: absolute;
  top: 15px;
  left: 15px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.1);
  border: none;
  font-size: 28px;
  color: #333;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  z-index: 10;
}

.modal-close-btn:hover {
  background-color: rgba(0, 0, 0, 0.2);
  transform: scale(1.1);
}

.modal-content {
  padding: 30px;
  padding-top: 70px;
  overflow-y: auto;
  height: 100%;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.modal-header h2 {
  margin: 0;
  color: #333;
  font-size: 28px;
}

.modal-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.modal-status.upcoming {
  background-color: #e3f2fd;
  color: #1976d2;
}

.modal-status.ongoing {
  background-color: #e8f5e8;
  color: #388e3c;
}

.modal-status.ended {
  background-color: #ffebee;
  color: #d32f2f;
}

.modal-image {
  width: 100%;
  height: 300px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 30px;
}

.modal-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.modal-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  align-items: start;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item label {
  font-weight: 600;
  color: #666;
  font-size: 14px;
}

.info-item p {
  margin: 0;
  color: #333;
  font-size: 16px;
  line-height: 1.6;
}

/* 发布活动弹窗样式 */
.publish-modal {
  width: 600px;
  max-width: 90%;
  height: auto;
  max-height: 90vh;
}

.publish-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-weight: 600;
  color: #666;
  font-size: 14px;
}

.form-item input,
.form-item textarea,
.form-item select {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s ease;
}

.form-item input:focus,
.form-item textarea:focus,
.form-item select:focus {
  border-color: #3498db;
}

.form-item textarea {
  resize: vertical;
  min-height: 80px;
}

.image-upload {
  position: relative;
  width: 100%;
  height: 200px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.image-upload:hover {
  border-color: #3498db;
}

.image-upload input[type="file"] {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

.upload-preview {
  position: relative;
  width: 100%;
  height: 100%;
}

.upload-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}

.remove-image {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  border: none;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s ease;
}

.remove-image:hover {
  background-color: rgba(0, 0, 0, 0.9);
}

.upload-placeholder {
  color: #999;
  font-size: 14px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-btn,
.submit-btn {
  padding: 10px 24px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #666;
  border: none;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}

.submit-btn {
  background-color: #3498db;
  color: white;
  border: none;
}

.submit-btn:hover {
  background-color: #2980b9;
}
</style>