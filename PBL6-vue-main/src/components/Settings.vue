<template>
  <div class="settings-container">
    <Navbar activeMenu="settings" />
    
    <div class="main-content">
      <div class="page-header">
        <h1>{{ t('settings.title') }}</h1>
        <p>{{ t('settings.subtitle') }}</p>
      </div>
      
      <div class="settings-tabs">
        <button 
          v-for="tab in tabs" 
          :key="tab.id"
          :class="['tab-button', { active: activeTab === tab.id }]"
          @click="activeTab = tab.id"
        >
          {{ t('settings.' + tab.name) }}
        </button>
      </div>
      
      <div class="settings-content">
        <div v-if="activeTab === 'profile'" class="settings-section">
          <h2>{{ t('settings.profile') }}</h2>
          <div class="settings-form">
            <div class="form-item">
              <label>{{ t('settings.username') }}</label>
              <input type="text" v-model="profileForm.username" :placeholder="t('settings.pleaseEnterUsername')">
            </div>
            <div class="form-item">
              <label>{{ t('settings.email') }}</label>
              <input type="email" v-model="profileForm.email" :placeholder="t('settings.pleaseEnterEmail')">
            </div>
            <div class="form-item">
              <label>{{ t('settings.phone') }}</label>
              <input type="tel" v-model="profileForm.phone" :placeholder="t('settings.pleaseEnterPhone')">
            </div>
            <div class="form-item">
              <label>{{ t('settings.avatar') }}</label>
              <div class="avatar-upload">
                <div class="avatar-preview">
                  <img :src="profileForm.avatar" alt="Avatar" v-if="profileForm.avatar">
                  <div class="avatar-placeholder" v-else>
                    <span>{{ t('settings.uploadAvatar') }}</span>
                  </div>
                </div>
                <input type="file" @change="handleAvatarUpload" accept="image/*" ref="avatarInput">
              </div>
            </div>
            <div class="form-actions">
              <button class="cancel-btn" @click="resetProfileForm">{{ t('settings.cancel') }}</button>
              <button class="submit-btn" @click="saveProfile">{{ t('settings.save') }}</button>
            </div>
          </div>
        </div>
        
        <div v-if="activeTab === 'password'" class="settings-section">
          <h2>{{ t('settings.password') }}</h2>
          <div class="settings-form">
            <div class="form-item">
              <label>{{ t('settings.currentPassword') }}</label>
              <input type="password" v-model="passwordForm.oldPassword" :placeholder="t('settings.pleaseEnterCurrentPassword')">
            </div>
            <div class="form-item">
              <label>{{ t('settings.newPassword') }}</label>
              <input type="password" v-model="passwordForm.newPassword" :placeholder="t('settings.pleaseEnterNewPassword')">
            </div>
            <div class="form-item">
              <label>{{ t('settings.confirmPassword') }}</label>
              <input type="password" v-model="passwordForm.confirmPassword" :placeholder="t('settings.pleaseConfirmPassword')">
            </div>
            <div class="form-actions">
              <button class="cancel-btn" @click="resetPasswordForm">{{ t('settings.cancel') }}</button>
              <button class="submit-btn" @click="changePassword">{{ t('settings.modifyPassword') }}</button>
            </div>
          </div>
        </div>
        
        <div v-if="activeTab === 'system'" class="settings-section">
          <h2>{{ t('settings.system') }}</h2>
          <div class="settings-form">
            <div class="form-item">
              <label>{{ t('settings.language') }}</label>
              <select v-model="systemForm.language" class="select-input" @change="handleLanguageChange">
                <option value="zh-CN">{{ t('settings.simplfiedChinese') }}</option>
                <option value="en-US">{{ t('settings.english') }}</option>
              </select>
            </div>
            <div class="form-item">
              <label>{{ t('settings.theme') }}</label>
              <div class="theme-options">
                <div 
                  v-for="theme in themes" 
                  :key="theme.value"
                  :class="['theme-option', { active: systemForm.theme === theme.value }]"
                  @click="systemForm.theme = theme.value"
                >
                  <div class="theme-color" :style="{ backgroundColor: theme.color }"></div>
                  <span>{{ t('settings.' + theme.name) }}</span>
                </div>
              </div>
            </div>
            <div class="form-item checkbox-item">
              <input type="checkbox" id="notifications" v-model="systemForm.notifications">
              <label for="notifications">{{ t('settings.notifications') }}</label>
            </div>
            <div class="form-item checkbox-item">
              <input type="checkbox" id="autoSave" v-model="systemForm.autoSave">
              <label for="autoSave">{{ t('settings.autoSave') }}</label>
            </div>
            <div class="form-actions">
              <button class="cancel-btn" @click="resetSystemForm">{{ t('settings.cancel') }}</button>
              <button class="submit-btn" @click="saveSystemSettings">{{ t('settings.save') }}</button>
            </div>
          </div>
        </div>
        
        <div v-if="activeTab === 'about'" class="settings-section">
          <h2>{{ t('settings.about') }}</h2>
          <div class="about-content">
            <div class="about-item">
              <label>{{ t('settings.version') }}</label>
              <span>1.0.0</span>
            </div>
            <div class="about-item">
              <label>{{ t('settings.developer') }}</label>
              <span>{{ t('settings.activityManagementSystem') }} {{ t('settings.team') }}</span>
            </div>
            <div class="about-item">
              <label>{{ t('settings.updateDate') }}</label>
              <span>2026-04-23</span>
            </div>
            <div class="about-item">
              <label>{{ t('settings.copyright') }}</label>
              <span>© 2026 {{ t('settings.activityManagementSystem') }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import Navbar from './Navbar.vue';
import { t, setLocale, initLocale } from '../locale';

const router = useRouter();

const tabs = [
  { id: 'profile', name: 'profile' },
  { id: 'password', name: 'password' },
  { id: 'system', name: 'system' },
  { id: 'about', name: 'about' }
];

const activeTab = ref('profile');

const profileForm = ref({
  username: '',
  email: '',
  phone: '',
  avatar: ''
});

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const systemForm = ref({
  language: 'zh-CN',
  theme: 'blue',
  notifications: true,
  autoSave: true
});

const themes = [
  { value: 'blue', name: 'blue', color: '#6b93c7' },
  { value: 'green', name: 'green', color: '#4caf50' },
  { value: 'purple', name: 'purple', color: '#9c27b0' },
  { value: 'orange', name: 'orange', color: '#ff9800' }
];

const loadUserInfo = () => {
  const user = localStorage.getItem('user');
  if (user) {
    try {
      const userObj = JSON.parse(user);
      profileForm.value.username = userObj.username || '';
      profileForm.value.email = userObj.email || '';
      profileForm.value.phone = userObj.phone || '';
      profileForm.value.avatar = userObj.avatar || '';
    } catch (error) {
      console.error('解析用户信息失败:', error);
    }
  }
};

const loadSystemSettings = () => {
  const settings = localStorage.getItem('systemSettings');
  if (settings) {
    try {
      const settingsObj = JSON.parse(settings);
      systemForm.value = { ...systemForm.value, ...settingsObj };
    } catch (error) {
      console.error('解析系统设置失败:', error);
    }
  }
  
  const locale = localStorage.getItem('locale');
  if (locale) {
    systemForm.value.language = locale;
  }
};

const handleAvatarUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      profileForm.value.avatar = e.target.result;
    };
    reader.readAsDataURL(file);
  }
};

const saveProfile = () => {
  const user = localStorage.getItem('user');
  let userObj = {};
  if (user) {
    try {
      userObj = JSON.parse(user);
    } catch (error) {
      console.error('解析用户信息失败:', error);
    }
  }
  
  userObj = {
    ...userObj,
    username: profileForm.value.username,
    email: profileForm.value.email,
    phone: profileForm.value.phone,
    avatar: profileForm.value.avatar
  };
  
  localStorage.setItem('user', JSON.stringify(userObj));
  alert(t('settings.profileSaveSuccess'));
};

const resetProfileForm = () => {
  loadUserInfo();
};

const changePassword = () => {
  if (!passwordForm.value.oldPassword) {
    alert(t('settings.pleaseEnterCurrentPassword'));
    return;
  }
  
  if (!passwordForm.value.newPassword) {
    alert(t('settings.pleaseEnterNewPassword'));
    return;
  }
  
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    alert(t('settings.passwordNotMatch'));
    return;
  }
  
  alert(t('settings.passwordChangeSuccess'));
  
  localStorage.removeItem('user');
  localStorage.removeItem('token');
  
  router.push('/');
};

const resetPasswordForm = () => {
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  };
};

const handleLanguageChange = (event) => {
  const newLocale = event.target.value;
  setLocale(newLocale);
  
  alert(t('settings.systemSettingsSaveSuccess'));
  
  window.location.reload();
};

const saveSystemSettings = () => {
  localStorage.setItem('systemSettings', JSON.stringify(systemForm.value));
  setLocale(systemForm.value.language);
  alert(t('settings.systemSettingsSaveSuccess'));
};

const resetSystemForm = () => {
  loadSystemSettings();
};

onMounted(() => {
  initLocale();
  loadUserInfo();
  loadSystemSettings();
});
</script>

<style scoped>
.settings-container {
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

.settings-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  background: white;
  padding: 10px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.tab-button {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  background: #f8f9fa;
  color: #333333;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  flex: 1;
  text-align: center;
}

.tab-button:hover {
  background: #e9ecef;
  transform: translateY(-2px);
}

.tab-button.active {
  background: #6b93c7;
  color: white;
  box-shadow: 0 4px 12px rgba(107, 147, 199, 0.3);
}

.settings-content {
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  padding: 30px;
  min-height: 500px;
}

.settings-section {
  margin-bottom: 30px;
}

.settings-section h2 {
  margin: 0 0 20px 0;
  font-size: 20px;
  font-weight: 600;
  color: #333333;
  padding-bottom: 10px;
  border-bottom: 1px solid #e9ecef;
}

.settings-form {
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

.form-item input {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s ease;
}

.form-item input:focus {
  border-color: #6b93c7;
  box-shadow: 0 0 0 2px rgba(107, 147, 199, 0.1);
}

.avatar-upload {
  position: relative;
  width: 120px;
  height: 120px;
  cursor: pointer;
}

.avatar-preview {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  overflow: hidden;
  border: 2px dashed #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.avatar-preview:hover {
  border-color: #6b93c7;
  background-color: #f8f9fa;
}

.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  text-align: center;
  color: #666666;
  font-size: 14px;
}

.avatar-upload input[type="file"] {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

.select-input {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s ease;
  cursor: pointer;
}

.select-input:focus {
  border-color: #6b93c7;
  box-shadow: 0 0 0 2px rgba(107, 147, 199, 0.1);
}

.theme-options {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.theme-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 10px;
  border-radius: 8px;
  transition: all 0.3s ease;
  width: 80px;
}

.theme-option:hover {
  background-color: #f8f9fa;
}

.theme-option.active {
  background-color: #e9ecef;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.theme-color {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid #ddd;
}

.theme-option span {
  font-size: 12px;
  color: #333333;
  font-weight: 500;
}

.checkbox-item {
  flex-direction: row;
  align-items: center;
  gap: 10px;
}

.checkbox-item input[type="checkbox"] {
  width: auto;
  height: auto;
  cursor: pointer;
}

.checkbox-item label {
  margin: 0;
  cursor: pointer;
}

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
  background-color: #6b93c7;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  background-color: #5a7fb8;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(107, 147, 199, 0.3);
}

.about-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.about-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.about-item label {
  font-weight: 600;
  color: #333333;
  font-size: 14px;
}

.about-item span {
  color: #666666;
  font-size: 14px;
}

@media (max-width: 768px) {
  .main-content {
    margin-left: 70px;
    padding: 20px;
  }
  
  .page-header h1 {
    font-size: 24px;
  }
  
  .settings-content {
    padding: 20px;
  }
  
  .settings-tabs {
    flex-direction: column;
  }
  
  .tab-button {
    padding: 10px 16px;
  }
  
  .theme-options {
    justify-content: center;
  }
  
  .about-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .cancel-btn,
  .submit-btn {
    width: 100%;
  }
}
</style>
