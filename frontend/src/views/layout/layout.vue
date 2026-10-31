
import { Menu } from 'element-ui';

import { Menu } from 'element-ui';

import { component } from 'vue/types/umd';
<template>
    
    <div class="layout">
        <!--上侧导航-->
        <div class="menu-top">
            <Menu></Menu>
        </div>
        <!--中间内容-->
        <div class="content">
            <Content></Content>
        </div>
    </div>
    
</template>

<script>
import Content from './content/content.vue'
import Menu from './menu/menu.vue'
import Bottom from './bottom/bottom.vue'
export default {
    components: {
        Menu,
        Content,
    },
    created(){
        const userinfo = JSON.parse(sessionStorage.getItem('userinfo'))
        if(this.$store.state.username==='' && userinfo!==null){
            this.$store.commit('getuserinfo', userinfo)
            this.$store.state.token = sessionStorage.getItem('token')
        }
    }
}
</script>

<style lang="less" scoped>
.layout{
    display: flex;
    .menu-top{
        position: fixed;
        left: 0;
        top: 0;
        right: 0;
        z-index: 999;
    }
    .menu{
        width: 200px;
        background: #324157;
        position: fixed;
        left: 0;
        top: 0;
        bottom: 0;
    }
    .content{
        padding-top: 60px;
        flex: 1;
    }
    .bottom{
        bottom: 0;
        position: absolute;
        height: 50px;
        background: gray;
        width: 100%;
    }

}

</style>