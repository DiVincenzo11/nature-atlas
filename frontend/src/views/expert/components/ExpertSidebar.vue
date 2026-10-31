<template>
  <div class="expert-sidebar">
    <div class="expert-sidebar__header">
      <div class="expert-sidebar__title">专家列表</div>
      <el-tag size="mini" type="success" v-if="!loading">{{ experts.length }} 位</el-tag>
      <el-skeleton v-else :rows="1" animated />
    </div>
    <div class="expert-sidebar__list" v-if="experts.length">
      <div
        v-for="item in experts"
        :key="item.expertId"
        class="expert-sidebar__item"
        :class="{ 'expert-sidebar__item--active': item.expertId === activeExpertId }"
        @click="$emit('select', item)"
      >
        <el-avatar :size="42" :src="item.avatar" class="expert-sidebar__avatar" />
        <div class="expert-sidebar__meta">
          <div class="expert-sidebar__name">{{ item.name || '未命名专家' }}</div>
          <div class="expert-sidebar__desc">{{ item.specialty || '擅长领域待完善' }}</div>
        </div>
        <el-tag size="mini" :type="item.online ? 'success' : 'info'">
          {{ item.online ? '在线' : '离线' }}
        </el-tag>
      </div>
    </div>
    <div class="expert-sidebar__empty" v-else>
      <el-empty description="暂无专家" />
    </div>
  </div>
</template>

<script>
export default {
  name: 'ExpertSidebar',
  props: {
    experts: {
      type: Array,
      default: () => []
    },
    activeExpertId: {
      type: Number,
      default: null
    },
    loading: {
      type: Boolean,
      default: false
    }
  }
}
</script>

<style lang="scss" scoped>
.expert-sidebar {
  width: 280px;
  height: 100%;
  padding: 16px;
  box-sizing: border-box;
  border-right: 1px solid #e5e5e5;
  background: #fff;
  &__header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;
  }
  &__title {
    font-weight: 600;
    font-size: 16px;
  }
  &__list {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  &__item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px;
    border: 1px solid #f0f0f0;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s ease;
    &:hover {
      border-color: #a2d16c;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
    }
    &--active {
      border-color: #87b15f;
      background: #f5fbef;
    }
  }
  &__avatar {
    flex-shrink: 0;
  }
  &__meta {
    flex: 1;
    overflow: hidden;
  }
  &__name {
    font-weight: 600;
    color: #333;
  }
  &__desc {
    font-size: 12px;
    color: #888;
    margin-top: 4px;
  }
  &__empty {
    margin-top: 24px;
  }
}
</style>
