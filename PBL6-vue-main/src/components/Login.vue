<template>
  <div class="login-container">
    <div class="circle" v-for="n in 15" :key="n" :style="getCircleStyle(n)"></div>
    <div class="login-box" :class="{ 'active': isRegister }">
      <div class="left-box">
        <div v-if="!isRegister" class="face">
          <div class="eye left-eye">
            <div class="pupil"></div>
          </div>
          <div class="eye right-eye">
            <div class="pupil"></div>
          </div>
          <div class="mouth"></div>
        </div>
        
        <div v-else class="face">
          <div class="eye left-eye closed"></div>
          <div class="eye right-eye closed"></div>
          <div class="hand">✋</div>
          <div class="mouth"></div>
        </div>
      </div>
      <div class="right-box">
        <div class="form-container">
          <div v-if="!isRegister" class="form-content">
            <h2>{{ t('login.title') }}</h2>
            <form class="login-form" @submit="handleLogin">
              <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
              
              <div class="form-group">
                <label for="login-username">{{ t('login.username') }}</label>
                <input type="text" id="login-username" v-model="loginForm.username" :placeholder="t('login.pleaseEnterUsername')" required>
              </div>
              <div class="form-group">
                <label for="login-password">{{ t('login.password') }}</label>
                <input type="password" id="login-password" v-model="loginForm.password" :placeholder="t('login.pleaseEnterPassword')" required>
              </div>
              <div class="form-actions">
                <div class="remember-me">
                  <input type="checkbox" id="remember" v-model="loginForm.rememberMe">
                  <label for="remember">{{ t('login.rememberMe') }}</label>
                </div>
                <a href="#" class="register-link" @click.prevent="toggleForm">{{ t('login.register') }}</a>
              </div>
              <button type="submit" class="login-button" :disabled="loading">
                {{ loading ? t('login.loggingIn') : t('login.login') }}
              </button>
            </form>
          </div>
          
          <div v-else class="form-content">
            <h2>{{ t('login.registerTitle') }}</h2>
            <form class="login-form" @submit="handleRegister">
              <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
              
              <div class="form-group">
                <label for="register-username">{{ t('login.username') }}</label>
                <input type="text" id="register-username" v-model="registerForm.username" :placeholder="t('login.pleaseEnterUsername')" required>
              </div>
              <div class="form-group">
                <label for="register-password">{{ t('login.password') }}</label>
                <input type="password" id="register-password" v-model="registerForm.password" :placeholder="t('login.pleaseEnterPassword')" required>
              </div>
              <div class="form-actions">
                <div class="remember-me">
                  <a href="#" class="login-options">{{ t('login.moreRegister') }}</a>
                </div>
                <a href="#" class="register-link" @click.prevent="toggleForm">{{ t('login.backToLogin') }}</a>
              </div>
              <button type="submit" class="login-button" :disabled="loading">
                {{ loading ? t('login.registering') : t('login.register') }}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { login, register } from '../api/user';
import { t, initLocale } from '../locale';

const router = useRouter();

const getCircleStyle = (index) => {
  const seed = index * 12345 + 6789;

  const size = 50 + (seed % 150);

  const hashLeft = x => {
    let h = x * 371 + 123;
    h ^= h << 17;
    h ^= h >> 13;
    h ^= h << 5;
    return (h & 0x7fffffff) % 100;
  };

  const hashTop = x => {
    let h = x * 211 + 456;
    h ^= h << 15;
    h ^= h >> 11;
    h ^= h << 3;
    return (h & 0x7fffffff) % 100;
  };

  const left = hashLeft(seed);
  const top = hashTop(seed);

  const hue = (seed * 137) % 360;
  const saturation = 30 + (seed % 20);
  const lightness = 80 + (seed % 10);
  const color = `hsl(${hue}, ${saturation}%, ${lightness}%)`;

  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    top: `${top}%`,
    backgroundColor: color,
    position: 'absolute',
    borderRadius: '50%',
  };
};

const isRegister = ref(false);

const loginForm = ref({
  username: '',
  password: '',
  rememberMe: false
});

const registerForm = ref({
  username: '',
  password: ''
});

const loading = ref(false);

const errorMessage = ref('');

const toggleForm = () => {
  isRegister.value = !isRegister.value;
  errorMessage.value = '';
};

const handleLogin = async (e) => {
  e.preventDefault();
  loading.value = true;
  errorMessage.value = '';
  
  try {
    console.log('开始登录');
    const response = await login({
      username: loginForm.value.username,
      password: loginForm.value.password
    });
    
    console.log('登录响应:', response);
    
    if (response.code === 200 && response.data) {
      console.log('登录成功，保存 token');
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.user));
      
      setTimeout(() => {
        router.push('/index');
      }, 100);
    } else {
      console.log('登录失败，响应:', response);
      throw new Error(response.message || t('login.loginFailed'));
    }
  } catch (error) {
    console.error('登录失败:', error);
    errorMessage.value = error.response?.data?.message || error.message || t('login.loginFailed');
  } finally {
    loading.value = false;
  }
};

const handleRegister = async (e) => {
  e.preventDefault();
  loading.value = true;
  errorMessage.value = '';
  
  try {
    const response = await register({
      userType: 0,
      username: registerForm.value.username,
      password: registerForm.value.password,
      realName: '',
      gender: 0,
      phone: '',
      email: '',
      schoolId: 0,
      college: '',
      major: '',
      grade: '',
      className: ''
    });
    
    if (response.code === 200) {
      console.log('注册成功:', response);
      alert(t('login.registerSuccess'));
      toggleForm();
    } else {
      throw new Error(response.message || t('login.registerFailed'));
    }
  } catch (error) {
    console.error('注册失败:', error);
    errorMessage.value = error.response?.data?.message || error.message || t('login.registerFailed');
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  initLocale();
  
  const handleMouseMove = (e) => {
    const pupils = document.querySelectorAll('.pupil');
    if (pupils.length > 0) {
      const x = e.clientX;
      const y = e.clientY;
      
      pupils.forEach(pupil => {
        const eyeX = (x - window.innerWidth / 2) / 30;
        const eyeY = (y - window.innerHeight / 2) / 30;
        
        const maxMove = 10;
        const limitedX = Math.min(maxMove, Math.max(-maxMove, eyeX));
        const limitedY = Math.min(maxMove, Math.max(-maxMove, eyeY));
        
        pupil.style.transform = `translate(calc(-50% + ${limitedX}px), calc(-50% + ${limitedY}px))`;
      });
    }
    
    const hand = document.querySelector('.hand');
    if (hand) {
      const x = (e.clientX - window.innerWidth / 2) / 25;
      const y = (e.clientY - window.innerHeight / 2) / 25;
      
      const limit = 20;
      const moveX = Math.max(-limit, Math.min(limit, x));
      const moveY = Math.max(-limit, Math.min(limit, y));
      
      hand.style.transform = `translate(calc(-50% + ${moveX}px), ${moveY}px) rotate(45deg)`;
    }
  };
  
  document.addEventListener('mousemove', handleMouseMove);
  
  onUnmounted(() => {
    document.removeEventListener('mousemove', handleMouseMove);
  });
});
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  width: 100%;
  background-color: #e4ebfa;
  position: relative;
  overflow: hidden; 
  margin: 0;
}

.circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.7;
  z-index: 0;
}

.login-box {
  position: relative;
  display: flex;
  width: 800px;
  height: 450px;
  background-color: #ffffff;
  border: 1px solid #dddddd;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin: 20px;
  z-index: 1;
}

.left-box {
  position: absolute;
  top: 0;
  left: 0;
  width: 50%;
  height: 100%;
  background-image: url('../img/login.jpg');
  background-size: cover;
  background-position: center;
  z-index: 1;
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateX(0);
  display: flex;
  justify-content: center;
  align-items: center;
}

.face {
  width: 200px;
  height: 200px;
  background: #ffd700;
  border-radius: 50%;
  position: relative;
  box-shadow: 0 0 20px rgba(0,0,0,0.2);
}

.eye {
  width: 40px;
  height: 40px;
  background: white;
  border-radius: 50%;
  position: absolute;
  top: 60px;
  overflow: hidden;
}

.eye.closed {
  width: 40px;
  height: 10px;
  background: transparent;
  border-top: 5px solid #2c3e50;
  border-radius: 50%;
  top: 70px;
}

.left-eye {
  left: 40px;
}

.right-eye {
  right: 40px;
}

.pupil {
  width: 15px;
  height: 15px;
  background: #2c3e50;
  border-radius: 50%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.mouth {
  width: 80px;
  height: 40px;
  border-bottom: 8px solid #2c3e50;
  border-radius: 0 0 80px 80px;
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
}

.hand {
  position: absolute;
  top: 20px;
  left: 50%;
  font-size: 120px;
  z-index: 10;
  line-height: 1;
  transform-origin: center;
  transform: translateX(-50%) rotate(45deg);
}

.right-box {
  position: absolute;
  top: 0;
  right: 0;
  width: 50%;
  height: 100%;
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: stretch;
  z-index: 2;
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateX(0);
  box-sizing: border-box;
}

.login-box.active .left-box {
  z-index: 2;
  transform: translateX(100%);
}

.login-box.active .right-box {
  z-index: 1;
  transform: translateX(-100%);
}

.form-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 100%;
  box-sizing: border-box;
}

.form-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 100%;
  box-sizing: border-box;
}

h2 {
  margin-bottom: 20px;
  color: #333333;
  font-size: 24px;
  text-align: center;
  flex-shrink: 0;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  flex: 1;
  justify-content: center;
  box-sizing: border-box;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: #666666;
  font-size: 14px;
}

.form-group input {
  padding: 12px 16px;
  border: 1px solid #dddddd;
  border-radius: 6px;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 2px rgba(66, 153, 225, 0.1);
}

.login-options {
  font-size: 12px;
  color: #4299e1;
  text-decoration: none;
  transition: color 0.3s ease;
}

.login-options:hover {
  color: #3182ce;
  text-decoration: underline;
}

.error-message {
  color: #e53e3e;
  font-size: 14px;
  margin-bottom: 16px;
  padding: 8px 12px;
  background-color: #fed7d7;
  border-radius: 4px;
  text-align: center;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 8px;
}

.remember-me label {
  color: #666666;
  font-size: 14px;
  cursor: pointer;
}

.register-link {
  color: #4299e1;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.3s ease;
  cursor: pointer;
}

.register-link:hover {
  color: #3182ce;
  text-decoration: underline;
}

.login-button {
  margin-top: 20px;
  padding: 14px;
  background-color: #4299e1;
  color: white;
  border: none;
  border-radius: 9999px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.login-button:hover {
  background-color: #3182ce;
}

.login-button:active {
  transform: translateY(1px);
}

@media (max-width: 768px) {
  .login-box {
    width: 90%;
    height: auto;
    flex-direction: column;
  }
  
  .login-box.active {
    flex-direction: column;
  }
  
  .left-box {
    display: none;
  }
  
  .right-box {
    padding: 40px 30px;
  }
}
</style>
