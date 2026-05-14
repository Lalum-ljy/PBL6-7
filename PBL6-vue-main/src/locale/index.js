import zhCN from './zh-CN';
import enUS from './en-US';

const locales = {
  'zh-CN': zhCN,
  'en-US': enUS
};

let currentLocale = 'zh-CN';

export const setLocale = (locale) => {
  if (locales[locale]) {
    currentLocale = locale;
    localStorage.setItem('locale', locale);
  }
};

export const getLocale = () => {
  return currentLocale;
};

export const t = (key) => {
  const keys = key.split('.');
  let result = locales[currentLocale];
  
  for (const k of keys) {
    if (result && typeof result === 'object' && k in result) {
      result = result[k];
    } else {
      return key;
    }
  }
  
  return result || key;
};

export const initLocale = () => {
  const savedLocale = localStorage.getItem('locale');
  if (savedLocale && locales[savedLocale]) {
    currentLocale = savedLocale;
  }
};

export const availableLocales = [
  { value: 'zh-CN', label: '简体中文' },
  { value: 'en-US', label: 'English' }
];
