import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'
import { parseJwt } from '@/utils/jwt'

class WebSocketService {
  constructor() {
    this.client = null
    this.jti = null
    this.subscriptions = []
    this.bus = null
  }

  init(token, bus) {
    const payload = parseJwt(token)
    const jti = payload.claims?.jti
    if (!jti) {
      console.error('无法解析出 jti，请检查 token 有效性 或 后端生成方式')
      return
    }

    this.bus = bus
    this.jti = jti

    // 如果已经有连接，先干掉它
    if (this.client && this.client.active) {
      this.client.deactivate()
      this.client = null
    }

    this.client = new Client({
      webSocketFactory: () => new SockJS('/ws-chat'),
      reconnectDelay: 5000,
      // 在 CONNECT 阶段带上 token
      connectHeaders: { Authorization: 'Bearer ' + token }
    })

    this.client.onConnect = () => {
      // 订阅 /user/{jti}/offline
      this._subscribeInternal('/user/queue/offline', msg => {
        const text = msg.body
        this.bus && this.bus.$emit('global-offline', text)
      })

      // 重放自定义订阅（聊天等）
      this.subscriptions
          .filter(s => s.destination !== '/user/queue/offline')
          .forEach(s => {
            s.stompSub = this.client.subscribe(s.destination, s.callback)
          })

      console.log('WebSocket 已连接')
    }

    this.client.onStompError = frame => {
      console.error('STOMP 错误 ', frame)
    }

    this.client.activate()
  }

  /**
   * 外部调用订阅，自动记录，断线重连后自动恢复
   */
  subscribe(destination, callback) {
    if (!destination || typeof callback !== 'function') return () => {}
    const record = { destination, callback, stompSub: null }
    this.subscriptions.push(record)
    if (this.client && this.client.connected) {
      record.stompSub = this.client.subscribe(destination, callback)
    }
    return () => {
      if (record.stompSub) {
        record.stompSub.unsubscribe()
      }
      this.subscriptions = this.subscriptions.filter(item => item !== record)
    }
  }

  /**
   * 发送 STOMP 消息
   */
  send(destination, body = {}) {
    if (this.client && this.client.connected) {
      this.client.publish({ destination, body: JSON.stringify(body) })
    } else {
      console.warn('WebSocket 未连接，无法发送消息', destination)
    }
  }

  disconnect() {
    this.subscriptions.forEach(item => item.stompSub && item.stompSub.unsubscribe())
    this.subscriptions = []
    this.jti = null
    if (this.client && this.client.active) {
      this.client.deactivate()
      this.client = null
      console.log('WebSocket 已断开')
    }
  }

  getJti() {
    return this.jti
  }

  _subscribeInternal(destination, callback) {
    // 内部使用，不写入 subscriptions，避免重复记录 offline 订阅
    if (this.client && this.client.connected) {
      return this.client.subscribe(destination, callback)
    }
    return null
  }
}

export default new WebSocketService()
