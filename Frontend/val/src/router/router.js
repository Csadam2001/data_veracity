import { createRouter, createWebHistory } from 'vue-router';
import ConsumerView from '../components/Consumer.vue';
import ProducerView from '../components/Producer.vue';
import ValidateView from '../components/Validate.vue';

const routes = [
  { path: '/', redirect: '/producer' }, 
  { path: '/consumer', component: ConsumerView  },
  { path: '/producer', component: ProducerView  },
  { path: '/validate', component: ValidateView  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
