const {defineConfig}=require('@vue/cli-service')
module.exports=defineConfig({transpileDependencies:true,productionSourceMap:false,devServer:{host:'127.0.0.1',port:18901,proxy:{'/api':{target:process.env.BACKEND_URL||'http://127.0.0.1:18900',pathRewrite:{'^/api':''}},'/ws-chat':{target:'http://127.0.0.1:18900',ws:true}}}})
