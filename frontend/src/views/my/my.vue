<template>
    <div class="body">
        <div class="wrap">
            <div class="box-top">
                <div class="image"><img :src="headportrait" alt=""></div>
                <el-button type="primary" class="button headPortraitChange" @click="alter('headPortrait')" v-if="!alterNickname && !alterPassword && !alterHeadPortrait">修改头像</el-button>
            </div>
            <div class="box-bottom">
                <h2>{{name}}</h2>
                <span>{{nickname}}</span>
            </div>
            <div class="box-bottom2">
                <ul v-if="!alterNickname && !alterPassword && !alterHeadPortrait">
                    <li>name:{{name}}</li>
                    <li>nickName:{{nickname}}</li>
                    <li>registerTime:{{registerTime.split('T')[0]}}</li>
                    <li>
                        <el-button type="primary" class="button" @click="alter('nickname')">修改信息</el-button>
                        <el-button type="primary" class="button" @click="alter('password')">修改密码</el-button>
                    </li>
                </ul>
                <ul v-if="alterNickname">
                    <li class="input-box">nickName：<el-input v-model="nickname" placeholder="请输入昵称"></el-input></li>
                    <li>
                        <el-button type="primary" class="button" @click="submit('nickname')">确定修改</el-button>
                        <el-button type="primary" class="button" @click="submit('back')">取消</el-button>
                    </li>
                </ul>
                <ul v-if="alterPassword">
                    <li class="input-box">oldPassword：<el-input v-model="oldPassword" placeholder="请输入原密码"></el-input></li>
                    <li class="input-box">newPassword：<el-input v-model="newPassword" placeholder="请输入新密码"></el-input></li>
                    <li>
                        <el-button type="primary" class="button" @click="submit('password')">确定修改</el-button>
                        <el-button type="primary" class="button" @click="submit('back')">取消</el-button>
                    </li>
                </ul>
                <ul v-if="alterHeadPortrait">
                    <li class="input-box">
                        upload：
                        <div class="upload-box">
                            <el-upload
                            action="#"
                            ref='pictureUpload'
                            list-type="picture-card"
                            :limit="limit"
                            :on-exceed="handleExceed"
                            :on-change="handleChange"
                            :auto-upload="false">
                                <i slot="default" class="el-icon-plus"></i>
                                <div slot="file" slot-scope="{file}">
                                    <img
                                        class="el-upload-list__item-thumbnail"
                                        :src="file.url" alt=""
                                    >
                                    <span class="el-upload-list__item-actions">
                                        <span
                                            v-if="!disabled"
                                            class="el-upload-list__item-delete"
                                            @click="handleRemove(file)"
                                        >
                                            <i class="el-icon-delete"></i>
                                        </span>
                                    </span>
                                </div>
                            </el-upload>
                        </div>
                    </li>
                    <li>
                        <el-button type="primary" class="button" @click="submit('headPortrait')">确定修改</el-button>
                        <el-button type="primary" class="button" @click="submit('back')">取消</el-button>
                    </li>
                </ul>
            </div>
        </div>
        <div class="log-box">
            <div class="title">识别日志</div>
            <div class="date" v-for="(item, i) in log" :key="i">
               <div class="item">
                <div class="item-date">
                    {{ item.detectTime }}
                </div>
                <img :src="item.picurl" alt="">
                <div class="item-res">
                    {{ item.result }}
                </div>
                </div>
                <!-- <div class="item" v-for="(item2, i2) in item" :key="i2">
                    <div class="item-date">
                        {{ item2.detectTime }}
                    </div>
                    <img :src="item2.picurl" alt="">
                    <div class="item-res">
                        {{ item2.finallist[0][0] }}
                        {{ item2.finallist[0][1] }}
                    </div>
                </div> -->
            </div>
            <div class="empty" v-if="JSON.stringify(log) === '{}'">
                <el-empty :description="tips"></el-empty>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
import { Message } from 'element-ui';

export default {

    mounted(){
        this.initial()
    },
    data(){
        return{
            // 头像
            headportrait: '',
            // 用户名
            name: '',
            // 昵称
            nickname: '',
            // 注册时间
            registerTime: '',
            // 原密码
            oldPassword: '',
            // 新密码
            newPassword: '',
            // 修改昵称状态
            alterNickname: false,
            // 修改密码状态
            alterPassword: false,
            // 修改头像状态
            alterHeadPortrait: false,
            ImageUrl: '',
            disabled: false,
            limit: 1,
            file: [],
            log: {},
            tips: '暂无识别记录'
        }
    },
    methods:{
        alter(e){
            if(e === 'nickname')return this.alterNickname = true
            if(e === 'password')return this.alterPassword = true
            if(e === 'headPortrait')return this.alterHeadPortrait = true
        },
        submit(e){
            if(e === 'nickname'){
                this.updateNickName()
            }
            if(e === 'password'){
                this.updatePassword()
            }
            if(e === 'headPortrait'){
                this.updateHeadPortrait()
            }
            if(e === 'back'){
                this.alterNickname = false
                this.alterPassword = false
                this.alterHeadPortrait = false
                Message.success('已取消')
            }
        },
        initial(){
            if(this.$store.state.username===""){
                this.$router.replace({path: '/login'});
            }
            this.$store.state.currentPlace = '/setting';
            this.headportrait = this.$store.state.headportrait;
            this.name = this.$store.state.username;
            this.nickname = this.$store.state.nickname;
            this.registerTime = this.$store.state.registerDate;
            this.getDetectLog()
        },
        updateNickName(){
            // 异常处理
            if(this.nickname==='')return Message.warning('请输入昵称')

            axios.post('/user/updateNickname',{
                username: this.name,
                nickname: this.nickname.trim()
            })
            .then(res => {
                if(res.data.data===1){
                    // 修改store中的nickname
                    this.$store.state.nickname = this.nickname
                    // 修改sessionStorage中的nickname
                    const userinfo = JSON.parse(sessionStorage.getItem('userinfo'))
                    userinfo.nickname = this.nickname
                    sessionStorage.setItem('userinfo',JSON.stringify(userinfo))
                    Message.success('修改成功')
                }
            })
            .catch(res => {
                console.log(res)
            })
            .finally(() => {
                this.alterNickname = false
            })
        },
        updatePassword(){
            // 异常处理
            if(this.oldPassword==='')return Message.warning('请输入原密码')
            if(this.newPassword==='')return Message.warning('请输入新密码')
            axios.post('/user/updatePassword',null,{
                params:{
                    username: this.name,
                    oldPassword: this.oldPassword,
                    newPassword: this.newPassword
                }
            })
            .then(res => {
                if(res.data.data === 0)return Message.error('原密码错误')
                Message.success('修改成功')
            })
            .catch(res => {
                console.log(res)
            })
            .finally(() => {
                this.alterPassword = false
            })
        },
        updateHeadPortrait(){
            if(this.file.length === 0)return Message.warning('请选择图片')
            const formData = new FormData();
            formData.append('file', this.file.raw);
            formData.append('username', this.$store.state.username);

            axios.post('/user/updateHeadportrait', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data;',
                    "token": this.$store.state.token
                },

            })
            .then(res => {
                if(res.data.code === 0){
                    // 修改store中的headPortrait
                    this.$store.commit('updateHeadPortrait',res.data.data)
                    // 修改显示的头像
                    this.headportrait = res.data.data;
                    // 修改sessionStorage中的nickname
                    const userinfo = JSON.parse(sessionStorage.getItem('userinfo'))
                    userinfo.headPortrait = res.data.data
                    sessionStorage.setItem('userinfo',JSON.stringify(userinfo))
                    Message.success('修改成功')
                }

            })
            .catch(err => {
                console.log(err);
            })
            .finally(() => {
                this.alterHeadPortrait = false
            })
        },
        handleExceed(){
            Message.error('超出数量了')
        },
        handleRemove(file) {
            let uploadFiles = this.$refs.pictureUpload.uploadFiles
            for (var i = 0; i < uploadFiles.length; i++) {
                if (uploadFiles[i]['url'] == file.url) {
                uploadFiles.splice(i, 1)
                }
            }
        },
        handleChange(file){
            this.file = file
        },
        getDetectLog(){
            axios.get('/detect/log',{
                params:{
                    userid: this.$store.state.userid
                }
            })
            .then(res => {
                this.log = res.data.data
                console.log(this.log)
            })
            .catch(res => {
                console.log(res)
            })
        }
    },
    watch:{
        "$store.state.username":{
            handler:function(newVal,oldVal){
                if(!newVal)return this.initial()
            }
        }
    },
    filters:{
        res(item){
            return item
        }
    }

}
</script>

<style lang="less" scoped>
*{
    margin: 0;
    padding: 0;
    list-style: none;
}
.body{
    // background-color: rgb(250, 220, 245);
    height: 100vh;
    display: flex;
    // justify-content: center;
    // align-items: center;
    background-image: url('../../../public/image/16.png');
    background-repeat: no-repeat;
    background-position: center;
    background-attachment: fixed;
    background-size: cover;
}
.wrap{
    position: relative;
    top: 50px;
    left: 50px;
    width: 330px;
    height: 330px;
    background-color: rgb(255, 255, 255);
    box-shadow: 0 0 10px rgb(0, 0, 0,.5);
    overflow: hidden;
    border-radius: 0 0 30px 30px;
}
.box-top{
    position: relative;
    width: 330px;
    height: 130px;
    background-color: rgb(180, 180, 210);
    display: flex;
    justify-content: center;
    transition: .5s;
}
.box-top::after{
    content: '';
    width: 40px;
    height: 40px;
    clip-path: polygon(50% 40%,0 0,100% 0);
    background-color: rgb(180, 180, 210);
    position: absolute;
    left: 50%;
    transform: translate(-50%);
    bottom: -38px;
    z-index: 999;
}
.image{
    position: absolute;
    top: 20px;
    width: 90px;
    height: 90px;
    background-size: cover;
    border: 5px solid rgb(140, 140, 170);
    transition: .5s;
    img{
        width: 100%;
        height: 100%;
    }
}
.box-bottom,.box-bottom2{
    position: absolute;
    top: 130px;
    width: 330px;
    height: 130px;
    text-align: center;
    transition: .5s;
}
.box-bottom h2{
    display: block;
    margin: 30px 0 10px 0;
    font: 800 28px 'Hobo Std';
}
.box-bottom span{
    font: 400 24px 'Giddyup Std';
}
.box-bottom h2::after{
    content: '';
    display: block;
    width: 200px;
    height: 2px;
    background-color: #000;
    position: absolute;
    left: 50%;
    transform: translate(-50%,4px);
}
.box-bottom2{
    top: 310px;
    height: 220px;
}
.box-bottom2 li{
    margin: 25px;
    font: 800 20px 'OCR A Std';
    transition: .2s;
}
.wrap:hover .box-top{
    height: 50px;
}
.wrap:hover .image{
    width: 35px;
    height: 35px;
    border-width: 3px;
    transform: translate(-140px,-15px);
}
.wrap:hover .box-bottom{
    top: 300px;
}
.wrap:hover .box-bottom2{
    top: 50px;
}
.wrap:hover .headPortraitChange{
    right: 10px;
}
li:hover{
    color: rgb(100, 160, 160);
}
.button{
    padding: 5px 10px;
}
.headPortraitChange{
    position: absolute;
    top: 10px;
    right: -100px;
    transition: .5s;
}
.input-box{
    display: flex;
    align-items: center;
}
.upload-box{
    height: 148px;
    overflow: hidden;
}
.log-box{
    position: relative;
    top: 40px;
    left: 100px;
    height: 80vh;
    width: 60vw;
    border: 5px solid sandybrown;
    border-radius: 20px 0 0 20px;
    background-color: white;
    overflow-y: scroll;
}
.item{
    display: flex;
    align-items: center;
    height: 100px;
    border: 1px dashed lightgray;
    padding: 0 10px;
    img{
        height: 80px;
        width: 80px;
        border-radius: 10px;
    }
}
.item-date{
    height: 80px;
    width: 120px;
    text-align: center;
    line-height: 80px;
    font-size: 16px;
    font-weight: 600;
    font-family: '宋体';
    margin-right: 10px;
}
.item-res{
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 80px;
    font-family: '宋体';
    font-size: 16px;
    margin-left: 10px;
}
.title{
    height: 50px;
    font-size: 25px;
    font-weight: 800;
    text-align: center;
    line-height: 50px;
    font-family: '黑体';
    border-bottom: 1px dashed lightgray;
}
</style>
