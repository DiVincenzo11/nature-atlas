import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    userid: '',
    username: '',
    nickname: '',
    registerDate: '',
    token: '',
    headportrait: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
    currentPlace: '/', // 记录用户的当前位置
    category: '' //记录详情页当前浏览物种
  },
  getters: {
  },
  mutations: {
    getuserinfo(state, data){
      state.userid = data.userId;
      state.username = data.username;
      state.nickname = data.nickname;
      state.registerDate = data.registerDate;
      state.headportrait = data.headPortrait?data.headPortrait:'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
    },
    print(state){
      console.log(state);
    },
    logout(state){
      state.userid = '';
      state.username = '';
      state.nickname = '';
      state.registerDate = '';
      state.headportrait = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
      state.token = ''
    },
    updateHeadPortrait(state,data){
      state.headportrait = data
      // console.log(data)
    }
  },
  actions: {
  },
  modules: {
  }
})
