<template>
  <div class="chat-window">
    <div class="chat-window__header">
      <div class="chat-window__title">{{ title }}</div>
      <div class="chat-window__sub" v-if="subtitle">{{ subtitle }}</div>
    </div>
    <div class="chat-window__body" ref="scrollArea">
      <div v-if="loading" class="chat-window__loading">
        <el-skeleton :rows="6" animated />
      </div>
      <div v-else>
        <div v-if="!messages.length" class="chat-window__empty">
          <el-empty description="暂无消息，向专家发一条吧" />
        </div>
        <div v-else class="chat-window__list">
          <div
            v-for="msg in orderedMessages"
            :key="msg.id || msg.createdAt + msg.content"
            class="chat-window__item"
            :class="{
              'chat-window__item--self': msg.senderRole === selfRole,
              'chat-window__item--other': msg.senderRole !== selfRole
            }"
          >
            <div class="chat-window__bubble">{{ msg.content }}</div>
            <div class="chat-window__time">{{ formatTime(msg.createdAt) }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChatWindow',
  props: {
    messages: {
      type: Array,
      default: () => []
    },
    selfRole: {
      type: String,
      default: 'USER'
    },
    title: {
      type: String,
      default: '专家对话'
    },
    subtitle: {
      type: String,
      default: ''
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    orderedMessages() {
      return [...this.messages].sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
    }
  },
  watch: {
    messages() {
      this.$nextTick(this.scrollToBottom)
    }
  },
  methods: {
    scrollToBottom() {
      const el = this.$refs.scrollArea
      if (!el) return
      el.scrollTop = el.scrollHeight
    },
    formatTime(val) {
      if (!val) return ''
      const date = new Date(val)
      return `${date.getHours().toString().padStart(2, '0')}:${date
        .getMinutes()
        .toString()
        .padStart(2, '0')}`
    }
  }
}
</script>

<style lang="scss" scoped>
.chat-window {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fdfdfd;
  &__header {
    padding: 12px 16px;
    border-bottom: 1px solid #eee;
  }
  &__title {
    font-weight: 600;
    font-size: 16px;
  }
  &__sub {
    color: #888;
    font-size: 12px;
    margin-top: 4px;
  }
  &__body {
    flex: 1;
    padding: 12px 16px;
    overflow-y: auto;
  }
  &__loading {
    padding: 12px 0;
  }
  &__list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
  &__item {
    max-width: 70%;
    display: flex;
    flex-direction: column;
    gap: 6px;
    &--self {
      align-self: flex-end;
    }
    &--other {
      align-self: flex-start;
    }
  }
  &__bubble {
    padding: 10px 12px;
    border-radius: 10px;
    background: #f0f4ff;
    line-height: 1.5;
    &::selection {
      background: #c9dcff;
    }
  }
  &__item--self &__bubble {
    background: #87b15f;
    color: #fff;
  }
  &__time {
    font-size: 11px;
    color: #999;
  }
  &__empty {
    margin-top: 40px;
  }
}
</style>
