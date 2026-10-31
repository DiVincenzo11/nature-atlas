<template>
    <div class="home">
        <section id="img-one" class="img">
                <h1>动悉物语</h1>
            </section>
            <section class="content-one">
                <div class="text-box">
                    <div class="cnt">
                        <h2>风吹芳兰折，日没鸟雀喧。</h2>
                        <p>
                            ——李白《送裴十八图南归嵩山二首》
                        </p>
                        <p>
                        <a href="#" class="btn">TAKE <strong>CARE OF </strong><em>YOURSELF</em></a>
                        </p>
                    </div>
                </div>
            </section>
            <section class="content-two">
                <div class="text-box">
                <div class="cnt">
                    <h2>榆柳荫后檐，桃李罗堂前。</h2>
                    <p>
                        ——陶渊明《归园田居·其一》
                    </p>
                    <p>
                    <a href="#" class="btn">BEST <strong>WISHES FOR </strong><em>YOU</em></a>
                    </p>
                </div>
                </div>
            </section><section class="content-three">
            <div class="text-box">
                <div class="cnt">
                <h2>游蕲水清泉寺，寺临兰溪，溪水西流。</h2>
                <p>
                    ——苏轼《浣溪沙·游蕲水清泉寺》
                </p>
                <p>
                    <a href="#" class="btn">TAKE <strong>CARE OF </strong><em>YOURSELF</em></a>
                </p>
                </div>
            </div>
            </section>
            <section id="img-two" class="img">
                <h1>THOUGHTS</h1>
            </section>
            <section id="footer">
              <!-- FOOTER -->
              <div class="footer" v-if="true">
                动悉物语 · 本地演示版
              </div>
              <div class="footer">
                <span>项目归档整理，使用演示账号与本地图鉴样例</span>
              </div>
            </section>
    </div>
</template>

<script>
export default {
    mounted() {
        this.$store.state.currentPlace = '/';
    }
}

</script>


<style lang="less" scoped>
h1{
    display: block;
    font-size: 2em;
    margin-block-start: 0.67em;
    margin-block-end: 0.6em;
    margin-inline-start: 0px;
    margin-inline-end: 0px;
    font-weight: bold;
}
h2{
    display: block;
    font-size: 1.5em;
    margin-block-start: 0.67em;
    margin-block-end: 0.6em;
    margin-inline-start: 0px;
    margin-inline-end: 0px;
    font-weight: bold;
}
.home {
  width: 100vw;
  height: auto;
}
section,
#footer {
  height: 100vh;
  position: relative;
  color: rgba(255, 255, 255, .8);
  font-size: 10vh;
  font-family: 'modern_no._20regular',serif;
  text-shadow: 4px 4px rgb(46, 43, 43);
  display: flex;
  justify-content: center;
  align-items: center;
}
#footer {
  background-color: #c0b5aa;
  height: 50vh;
  color: #fff;
  font-size: 20vh;
  .footer{
    color: #fff;
    font-size: 3vh;
    text-shadow: 0px 0px rgb(46, 43, 43);
  }
}
.img {
  overflow: hidden;
  background-repeat: no-repeat;
  background-position: center;
  background-attachment: fixed;
  background-size: cover;
  user-select: none;
}
#img-one {
    background-image: url(../../../public/image/11.png);
  background-size: cover;
}
#img-two {
    background-image: url(../../../public/image/12.png);
}
.content-one {
    background: url(../../../public/image/13.png) no-repeat fixed center;
  background-size: cover;
}
.content-two {
    background: url(../../../public/image/14.png) no-repeat fixed center;
  background-size: cover;
}
.content-three {
    background: url(../../../public/image/15.png) no-repeat fixed center;
  background-size: cover;
}
.text-box {
  width: 100%;
  height: 50%;
  position: absolute;
  bottom: 25%;
  text-align: center;
  background-color: rgba(0, 0, 0, .3);
  transform-origin: center bottom;
  transform: skew(0, -8deg);
}
.text-box .cnt {
  display: inline-block;
  text-align: right;
  margin: 40px 0 10px;
  width: 90%;
  max-width: 990px;
  transform-origin: right bottom;
  transform: skew(0, 8deg);
}
.cnt h2 {
  margin-top: 100px;
  font-size: 36px;
  font-family: 'modern_no._20regular',serif;
  text-shadow: 1px 0px 0px #323232;
  letter-spacing: 0.3em;
  text-transform: uppercase;
  color: #fff;
  margin-bottom: 5px;
}
.cnt p {
  font-size: 20px;
  line-height: 1.7;
  margin-bottom: 10px;
  color: #fff;
}
.cnt a.btn {
  font-size: 90%;
  font-family: 'modern_no._20regular',serif;
  letter-spacing: 0.3em;
  text-shadow: 1px 0px 0px black;
  line-height: 2.8;
  display: inline-block;
  background-color: #eaeaea;
  color: #000;
  border: 3px solid #000;
  white-space: nowrap;
  text-decoration: none;
  padding: 0 20px;
}
</style>
