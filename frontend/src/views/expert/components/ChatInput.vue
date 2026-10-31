<template>
  <div class="chat-input">
    <el-input
      class="chat-input__textarea"
      type="textarea"
      v-model="innerValue"
      :rows="3"
      placeholder="请输入要发送给专家的内容"
      @keyup.enter.native.prevent="handleSend"
    />
    <div class="chat-input__footer">
      <div class="chat-input__tip">按 Enter 发送，Shift+Enter 换行</div>
      <el-button type="primary" size="mini" @click="handleSend" :loading="loading">发送</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChatInput',
  props: {
    value: {
      type: String,
      default: ''
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      innerValue: this.value
    }
  },
  watch: {
    value(val) {
      this.innerValue = val
    }
  },
  methods: {
    handleSend() {
      const text = this.innerValue && this.innerValue.trim()
      if (!text) return
      this.$emit('input', '')
      this.innerValue = ''
      this.$emit('send', text)
    }
  }
}
</script>

<style lang="scss" scoped>
.chat-input {
  padding: 10px;
  border-top: 1px solid #eee;
  background: #fafafa;
  &__textarea {
    margin-bottom: 8px;
  }
  &__footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  &__tip {
    font-size: 12px;
    color: #888;
  }
}
</style>
