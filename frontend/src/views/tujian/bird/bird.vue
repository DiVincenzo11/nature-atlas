<template>
  <div class="container">
    <div class="guide">
      <div id="navbar">
        <a v-for="(item, index) in navItems" :key="index" @click="scrollToCategory(item.id)">{{ item.name }}</a>
        <!-- <div @click="test">123dsa54das65</div> -->
      </div>
    </div>
    <div class="birds-container">
      <div v-for="(categoryData, categoryIndex) in birddata" :key="categoryIndex" class="category-container"
           :ref="categoryData[0]">
        <el-tag
            :key="categoryData[0]"
            effect="dark"
            class="category-tag"
            :id="categoryData[0]">
          {{ categoryData[0] }}

        </el-tag>

        <div v-for="(kecategoryData, itemIndex) in categoryData[1]" :key="itemIndex" class="family-container">
          <el-divider>
            <el-tag
                :key="kecategoryData[0]"
                effect="plain"
                class="category-tag">

              {{ kecategoryData[0] }}
            </el-tag>
          </el-divider>


          <div class="species-container">
            <div v-for="(item, zhongIndex) in kecategoryData[1]" :key="zhongIndex" class="species-item"
                 @click="getdetail(item[0])">
              <div class="image-container">
                <div>
                  <el-image
                      style="width: 150px; height: 150px"
                      :src="item[1]"
                      lazy
                  ></el-image>
                </div>
                <span class="zhong-span">{{ item[0] }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<script>
import axios from "axios";
// import axios from '../../../utils/request'
export default {
  name: "birdtujian",
  data() {
    return {
      fits: ["fill"],
      urls: [],
      birddata: [],
      navItems: [],
    };
  },
  async mounted() {
    try {
      this.$store.state.currentPlace = '/tujian/bird';
      const response = await axios.get("/bird/allbirddata");
      console.log(response);
      const data = response.data.data;
      this.birddata = data;
      const categories = data.map(item => item[0]);

      // 创建 Element UI 导航菜单的数据数组
      this.navItems = categories.map(category => ({
        id: category,
        name: category,
      }));

      this.urls = data.reduce((acc, category) => {
        category[1].forEach(family => {
          family[1].forEach(species => {
            acc.push(species[1]); // Assuming species[1] holds the image URL
          });
        });
        return acc;
      }, []);
    } catch (error) {
      console.error("Error fetching data:", error);
      // 处理错误情况，比如显示错误提示
    }
  },
  methods: {

    getdetail(name) {
      this.$router.push({
        path: "/tujian/birddetail",
        query: {
          name: name,
        },
      });
    },

    scrollToCategory(id) {
      const targetElement = document.getElementById(id);
      if (targetElement) {
        targetElement.scrollIntoView({behavior: 'smooth', block: 'start'});
      }
    }
  },
};

</script>

<style lang="less" scoped>
.container {
  display: flex;
  position: relative;
}

.guide {
  position: fixed;
  top: 75px;
  left: 25px;
  width: 150px;
  height: auto;
  z-index: 1000;
  overflow-y: auto; /* 显示垂直滚动条 */
  max-height: 85vh;
  background-color: #e2dfbd;
}

/* 自定义滚动条样式 */
.guide::-webkit-scrollbar {
  width: 8px; /* 设置滚动条宽度 */
}

.guide::-webkit-scrollbar-thumb {
  background-color: #888; /* 设置滚动条拖动部分的背景颜色 */
  border-radius: 4px; /* 设置滚动条拖动部分的圆角 */
}

.guide::-webkit-scrollbar-track {
  background-color: #f5f5f5; /* 设置滚动条轨道的背景颜色 */
  border-radius: 4px; /* 设置滚动条轨道的圆角 */
}

.birds-container {
  padding-left: 200px;
  display: flex;
  flex-wrap: wrap;
  position: relative;
}

.category-container {
  width: 100%;
  margin-bottom: 20px;
  margin-top: 20px; /* Add margin-top to create space between header and first tag */
}

.category-tag {
  margin-bottom: 10px; /* Add margin-bottom to create space between tag and content */
  font-size: 16px;
}

.family-container {
  margin-bottom: 10px;
  margin-top: 10px;
}

.species-container {
  display: flex;
  flex-wrap: wrap;
}

.species-item {
  width: calc(20% - 20px);
  margin-right: 10px;
  margin-bottom: 10px;
  box-sizing: border-box;
}

.image-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;

}

.zhong-span {
  font-size: 14px;
  font-family: '黑体';
  margin-top: 5px;
}

#navbar {
  background-color: #7c7575;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
}

#navbar a {
  float: left;
  display: block;
  color: white;
  text-align: center;
  padding: 14px 0px;
  width: 100%;
  text-decoration: none;
  cursor: pointer;
  font-size: 14px;
}

#navbar a:hover {
  background-color: #ddd;
  color: black;
}
</style>
