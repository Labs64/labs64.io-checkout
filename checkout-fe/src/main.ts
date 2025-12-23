// styles
import './assets/main.css';

import { createApp } from 'vue';
import { createPinia } from 'pinia';
import { createHead } from '@unhead/vue/client';

import App from './views/App.vue';
import router from './router';
import i18n from './i18n';

import veeValidate from '@/plugins/veeValidate';
import persistPlugin from '@/stores/plugins/persistPlugin';

const app = createApp(App);

const pinia = createPinia();
pinia.use(persistPlugin);

app.use(pinia);
app.use(i18n);
app.use(router);
app.use(veeValidate);
app.use(createHead());

app.mount('#app');
