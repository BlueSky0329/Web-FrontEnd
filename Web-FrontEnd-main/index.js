const Koa = require('koa');
const static = require('koa-static');
const { koaBody } = require('koa-body');
const router = require('./api')

const app = new Koa();

// 注册koa-body中间件
app.use(koaBody())

// 注册路由
app.use(router.routes())

// 设置静态目录
app.use(static(__dirname + '/public'));

// 启动服务器
app.listen(3000, () => {
  console.log('Server is running on http://localhost:3000/login.html');
});