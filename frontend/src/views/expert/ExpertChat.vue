<template>
  <div class="expert-chat">
    <expert-sidebar
      :experts="experts"
      :active-expert-id="activeExpert && activeExpert.expertId"
      :loading="loadingExperts"
      @select="handleSelectExpert"
    />
    <div class="expert-chat__content">
      <chat-window
        :messages="messages"
        :self-role="selfRole"
        :title="activeExpert ? activeExpert.name : '请选择专家'"
        :subtitle="activeExpert ? activeExpert.specialty : ''"
        :loading="loadingMessages"
      />
      <chat-input
        v-if="activeExpert"
        v-model="draft"
        :loading="sending"
        @send="handleSend"
      />
      <div v-else class="expert-chat__placeholder">
        <el-empty description="请选择一位专家开始对话" />
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { Message } from 'element-ui'
import ExpertSidebar from './components/ExpertSidebar.vue'
import ChatWindow from './components/ChatWindow.vue'
import ChatInput from './components/ChatInput.vue'
import wsService from '@/utils/websocket'

export default {
  name: 'ExpertChat',
  components: { ExpertSidebar, ChatWindow, ChatInput },
  data() {
    return {
      experts: [],
      activeExpert: null,
      messages: [],
      loadingExperts: false,
      loadingMessages: false,
      sending: false,
      draft: '',
      unsubChat: null
    }
  },
  computed: {
    selfRole() {
      // 普通用户视角
      if (!this.activeExpert) return 'USER'
      const currentUserId = this.$store.state.userid
      return currentUserId === this.activeExpert.userId ? 'EXPERT' : 'USER'
    }
  },
  async mounted() {
    const token = sessionStorage.getItem('token')
    if (!token) {
      Message.warning('请先登录后再使用专家沟通功能')
      return this.$router.replace({ path: '/login' })
    }
    await this.fetchExperts()
    this.setupSubscription()
  },
  beforeDestroy() {
    this.teardownSubscription()
  },
  methods: {
    async fetchExperts() {
      this.loadingExperts = true
      try {
        const res = await axios.get('/expert/list')
        if (res && res.data && res.data.code === 0) {
          this.experts = res.data.data || []
        } else {
          Message.error(res?.data?.mseeage || '专家列表获取失败')
        }
      } catch (e) {

        Message.error('专家列表获取失败')
      } finally {
        this.loadingExperts = false
      }
    },
    async handleSelectExpert(expert) {
      this.activeExpert = expert
      await this.fetchMessages()
    },
    async fetchMessages() {
      if (!this.activeExpert) return
      this.loadingMessages = true
      try {
        const token = sessionStorage.getItem('token')
        const res = await axios.get('/expert/messages', {
          params: { expertId: this.activeExpert.expertId, page: 1, size: 50 },
          headers: { Authorization: 'Bearer ' + token }
        })
        if (res && res.data && res.data.code === 0) {
          this.messages = res.data.data || []
        } else {
          Message.error(res?.data?.mseeage || '消息记录拉取失败')
        }
      } catch (e) {
        Message.error('消息记录拉取失败')

      } finally {
        this.loadingMessages = false
      }
    },
    setupSubscription() {
      // 订阅自己的消息队列
      const jti = wsService.getJti()
      if (!jti) return
      this.unsubChat = wsService.subscribe('/user/queue/expert/chat', frame => {
        try {
          const payload = JSON.parse(frame.body)
          // 只处理当前专家会话的消息
          if (this.activeExpert && payload.expertId === this.activeExpert.expertId) {
            this.messages = [...this.messages, payload]
          }
        } catch (e) {
          console.warn('解析聊天消息失败', e)
        }
      })
    },
    teardownSubscription() {
      if (this.unsubChat) {
        this.unsubChat()
        this.unsubChat = null
      }
    },
    async handleSend(text) {
      if (!this.activeExpert) return Message.warning('请先选择专家')
      this.sending = true
      try {
        wsService.send('/app/expert/send', {
          expertId: this.activeExpert.expertId,
          content: text,
          contentType: 'TEXT'
        })
      } catch (e) {
        Message.error('消息发送失败，请稍后重试')
      } finally {
        this.sending = false
      }
    }
  }
}
</script>

<style scoped lang="scss">
.expert-chat {
  display: flex;
  height: calc(100vh - 80px);
  background: #f6f6f6;
  &__content {
    flex: 1;
    display: flex;
    flex-direction: column;
    background: #fff;
  }
  &__placeholder {
    padding: 40px;
    border-top: 1px solid #eee;
  }
}
</style>
