import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/views/layout/layout.vue'
import Login from '@/views/login/login.vue'
import Home from '@/views/home/home.vue'
import Test from '@/views/test/test.vue'
const Register = ()=>import('@/views/register/register.vue')
const Birdscan = ()=>import('@/views/scan/birdscan/birdscan.vue')
const Plantscan = ()=>import('@/views/scan/plantscan/plantscan.vue')
const Bird = ()=>import('@/views/tujian/bird/bird.vue')
const Cat = ()=>import('@/views/tujian/cat/cat.vue')
const Dog = ()=>import('@/views/tujian/dog/dog.vue')
const Turtle = ()=>import('@/views/tujian/turtle/turtle.vue')
const Liangqi = ()=>import('@/views/tujian/liangqi/liangqi.vue')
const Character = ()=>import('@/views/character/character.vue')
const My = ()=>import('@/views/my/my.vue')
const Tuiwen = ()=>import('@/views/lesson/tuiwen/tuiwen.vue')
const Video = ()=>import('@/views/lesson/video/video.vue')
const Birddetail = ()=>import('@/views/tujian/bird/birddetail.vue')
const Catdetail = ()=>import('@/views/tujian/cat/catdetail.vue')
const Dogdetail = ()=>import('@/views/tujian/dog/dogdetail.vue')
const Liangqidetail = ()=>import('@/views/tujian/liangqi/liangqidetail.vue')
const Turtledetail = ()=>import('@/views/tujian/turtle/turtledetail.vue')
const Search = () => import('@/views/search/search.vue')
const ExpertChat = () => import('@/views/expert/ExpertChat.vue')

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: '/',
        name: 'home',
        component: Home
      },
      {
        path:'/scan/birdscan',
        name: 'birdscan',
        component: Birdscan
      },
      {
        path:'/scan/plantscan',
        name: 'plantscan',
        component: Plantscan
      },
      {
        path:'/tujian/bird',
        name: 'bird',
        component: Bird,
      },
      {
        path:'/tujian/cat',
        name: 'cat',
        component: Cat
      },
      {
        path:'/tujian/dog',
        name: 'dog',
        component: Dog
      },
      {
        path:'/tujian/turtle',
        name: 'turtle',
        component: Turtle
      },
      {
        path:'/tujian/liangqi',
        name: 'liangqi',
        component: Liangqi
      },
      
      
      {
        path:'/character',
        name: 'character',
        component: Character
      },
      {
        path: '/setting',
        name: 'setting',
        component: My
      },
      {
        path: '/lesson/tuiwen',
        name: 'tuiwen',
        component: Tuiwen
      },
      {
        path: '/lesson/video',
        name: 'video',
        component: Video
      },
      {
        path: '/tujian/birddetail',
        name: 'birddetail',
        component: Birddetail
      },
      {
        path: '/tujian/catdetail',
        name: 'catdetail',
        component: Catdetail
      },
      {
        path: '/tujian/dogdetail',
        name: 'dogdetail',
        component: Dogdetail
      },
      {
        path: '/tujian/liangqidetail',
        name: 'liangqidetail',
        component: Liangqidetail
      },
      {
        path: '/tujian/turtledetail',
        name: 'turtledetail',
        component: Turtledetail
      },
      {
        path: '/search',
        name: 'search',
        component: Search
      },
      {
        path: '/expert/chat',
        name: 'expertChat',
        component: ExpertChat
      }

    ]
  },
  {
    path: '/login',
    name: 'login',
    component: Login
  },
  {
    path: '/register',
    name: 'register',
    component: Register
  },
  {
    path: '/test',
    name: 'test',
    component: Test
  }

]

const router = new VueRouter({
  mode: "history",
  routes
})

export default router
