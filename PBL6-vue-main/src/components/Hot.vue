<template>
  <div class="hot-page-container">
    <!-- 左侧导航栏 -->
    <Navbar :activeMenu="activeMenu" />
    
    <!-- 右侧主内容区 -->
    <div class="main-content">
      <div class="hot-header">
        <h2>热门活动</h2>
      </div>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="hotActivities.length > 0" class="hot-list">
        <div class="hot-item" v-for="(activity, index) in hotActivities" :key="index">
          <div class="item-image">
            <img :src="activity.coverUrl || defaultImage" :alt="activity.activityName">
          </div>
          <div class="item-content">
            <h3 class="item-name">{{ activity.activityName }}</h3>
            <p class="item-desc">{{ activity.activityDesc }}</p>
            <div class="item-time-row">
              <div class="item-time">
                <span class="time-label">开始时间：</span>
                <span class="time-value">{{ formatTime(activity.startTime) }}</span>
              </div>
              <div class="item-time">
                <span class="time-label">结束时间：</span>
                <span class="time-value">{{ formatTime(activity.endTime) }}</span>
              </div>
              <div class="item-status">
                <span class="status-badge" :class="getStatusClass(activity.status)">{{ getStatusText(activity.status) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="empty">暂无热门活动</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getHotActivities } from '../api/activity';
import Navbar from './Navbar.vue';

// 全局配置
const IMG_DOMAIN = 'http://localhost:8080';

const router = useRouter();
const activeMenu = ref('hot');
const loading = ref(false);
const hotActivities = ref([]);
const defaultImage = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20event%20image&image_size=landscape_4_3';

const fetchHotActivities = async () => {
  loading.value = true;
  try {
    const response = await getHotActivities();
    if (response.code === 200 && response.data) {
      // 处理分页接口返回的数据结构
      const responseData = response.data.data || response.data;
      // 为 coverUrl 字段添加域名
      hotActivities.value = responseData.map(activity => ({
        ...activity,
        coverUrl: activity.coverUrl ? IMG_DOMAIN + activity.coverUrl : activity.coverUrl
      }));
    }
  } catch (error) {
    console.error('获取热门活动失败:', error);
  } finally {
    loading.value = false;
  }
};

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

const getStatusClass = (status) => {
  switch (status) {
    case 0: return 'upcoming';
    case 1: return 'ongoing';
    case 2: return 'ended';
    default: return 'upcoming';
  }
};

const getStatusText = (status) => {
  switch (status) {
    case 0: return '即将开始';
    case 1: return '进行中';
    case 2: return '已结束';
    default: return '即将开始';
  }
};

onMounted(() => {
  fetchHotActivities();
});
</script>

<style scoped>
.hot-page-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 右侧主内容区 */
.main-content {
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f5f5;
  height: 100vh;
  box-sizing: border-box;
  width: calc(100% - 260px);
  position: absolute;
  top: 0;
  right: 0;
  margin-left: 260px;
  transition: all 0.3s ease;
}

/* 当导航栏折叠时 */
.sidebar.collapsed + .main-content {
  margin-left: 70px;
  width: calc(100% - 70px);
}

.hot-header {
  margin-bottom: 30px;
  width: 100%;
}

.hot-header h2 {
  margin: 0;
  color: #333333;
  font-size: 24px;
  font-weight: 600;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #666666;
  font-size: 16px;
}

.hot-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
}

.hot-item {
  display: flex;
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  width: 100%;
}

.hot-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.item-image {
  flex-shrink: 0;
  width: 200px;
  height: 150px;
  overflow: hidden;
  border-radius: 12px 0 0 12px;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-content {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.item-name {
  margin: 0;
  color: #333333;
  font-size: 18px;
  font-weight: 600;
}

.item-desc {
  margin: 0;
  color: #666666;
  font-size: 14px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-time-row {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-top: auto;
}

.item-time {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666666;
}

.time-label {
  font-weight: 500;
  color: #999999;
}

.time-value {
  color: #333333;
}

.item-status {
  margin-left: auto;
}

.status-badge {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 14px;
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

.empty {
  text-align: center;
  padding: 40px;
  color: #999999;
  font-size: 16px;
  width: 100%;
}

@media (max-width: 768px) {
  .hot-item {
    flex-direction: column;
  }
  
  .item-image {
    width: 100%;
    height: 200px;
    border-radius: 12px 12px 0 0;
  }
  
  .item-content {
    padding: 15px;
  }
  
  .item-name {
    font-size: 16px;
  }
  
  .item-desc {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .hot-page-container {
    padding: 15px;
  }
  
  .hot-header h2 {
    font-size: 20px;
  }
  
  .item-image {
    height: 180px;
  }
  
  .item-content {
    padding: 12px;
    gap: 10px;
  }
  
  .item-name {
    font-size: 15px;
  }
  
  .item-desc {
    font-size: 12px;
  }
  
  .item-time {
    font-size: 13px;
  }
}
</style>