<template>
	<div class="container">
		<div class="right">
			<div class="picture">
				<h1>查询结果</h1>
				<div class="pic">
					<div class="image-container" v-for="(item, index) in pageData" @click="getdetail(item.name,item.typeName)">
						<el-image
							class="image"
							:src="item.picurl"
							lazy
						>
						</el-image>
						<span class="zhong-span">{{ item.name }}</span>
					</div>
					<el-pagination
						class="pagination"
						background
						:page-size="pageSize"
						:current-page="page"
						@current-change="change"
						layout="prev, pager, next"
						:total="totalPage">
					</el-pagination>
				</div>
			</div>
		</div>
		<el-backtop target=".container"></el-backtop>
	</div>
</template>

<script>
import axios from 'axios';
export default {
    mounted() {
        this.initial()
    },
	data(){
		return {
			detaildata: [],
			key: '',
			test: 'https://so1.360tres.com/t01fde7a3188f2ed4b2.jpg',
			page: 1,
			pageSize: 50,
			totalPage: 0,
			pageData: [],
		}
	},
	methods:{
		initial(){
			this.$store.state.currentPlace = '/search';
			this.key = this.$route.query.key
			if(this.key !== ''){
				this.getSearchDate()
			}
		},
		getSearchDate(){
			axios.get('/search',{
				params:{
					key: this.key
				}
			})
			.then(res => {
				this.detaildata = res.data.data
				this.pageData = this.detaildata.slice(this.pageSize*(this.page-1),this.pageSize*this.page)
				this.totalPage = this.detaildata.length
			})
			.catch(res => {
				console.log(res)
			})
		},
		change(e){
			this.page = e
			this.pageData = this.detaildata.slice(this.pageSize*(this.page-1),this.pageSize*this.page)
		},
		getdetail(name,type) {
			if(type==='鸟')return(
				this.$router.push({
					path: "/tujian/birddetail",
					query: {
						name: name,
					},
				})
			)
			if(type==='猫')return(
				this.$router.push({
					path: "/tujian/catdetail",
					query: {
						name: name,
					},
				})
			)

			if(type==='狗')return(
				this.$router.push({
					path: "/tujian/dogdetail",
					query: {
						name: name,
					},
				})
			)
			if(type==='两栖')return(
				this.$router.push({
					path: "/tujian/liangqidetail",
					query: {
						name: name,
					},
				})
			)
			if(type==='爬行')return(
				this.$router.push({
					path: "/tujian/turtledetail",
					query: {
						name: name,
					},
				})
			)
        },
	}
}

</script>


<style lang="less" scoped>
.container {
    display: flex;
    position: relative; /* 添加相对定位以使绝对定位的元素相对于父容器定位 */
}

.right {
  flex: 1; /* 右边图片区域可伸缩 */
  position: absolute;
  top: 0;
	bottom: 0;
	right: 0;
	left: 0;
  max-height: calc(100vh - 20px); /* 右边图片区域最大高度为视口高度减去一定空间 */
}

.picture h1{
    font-family: '黑体';
    font-size: 22px;
    margin-left: 0%;
    margin-top: 10px;
    margin-bottom: 15px;
}

.picture {
	display: flex;
    flex-direction: column;
    position: relative;
    width: 100%;
    text-align: center; /* 如果需要图片居中显示，可以添加这一行 */
	align-items: center;
	padding-bottom: 80px;
}

.pic {
    text-align: center;
    display: flex;
    flex-wrap: wrap; /* 允许换行 */
	width: 1000px;
	min-height: 400px;
	padding-bottom: 10px;
	border: 2px solid rosybrown;
}
.image-container {
    display: flex;
    flex-direction: column;
	width: 200px;
	height: 180px;
	margin: 10px 0;
    align-items: center;
}
.image{
	height: 150px;
	width: 150px;
	border-radius: 5px;
}
.zhong-span {
    text-align: center; /* 文字居中对齐 */
    margin-top: 10px; /* 适当的上边距 */
}
.pagination{
	position: absolute;
	bottom: 30px;
	left: 50%;
	transform: translate(-50%);
}
</style>
