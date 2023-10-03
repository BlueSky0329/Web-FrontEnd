const Router = require('koa-router');
const query = require('./utils/mysql');
const md5 = require('md5')

const router = new Router({
  prefix: '/app'
});

// 注册
router.post('/user/register', async (ctx) => {
  const { username, email, password } = ctx.request.body;
  if(!username || !email || !password) {
    ctx.body = {
      code: 200,
      msg: '请填写完整信息！'
    }
    return;
  }
  const res = await query(`select * from base_sys_user where username = '${username}' or email = '${email}'`)
  if(res.length > 0) {
    ctx.body = {
      code: 100,
      msg: '当前用户已存在！'
    }
  }else {
    const sql = `INSERT INTO base_sys_user (username, email, password) VALUES ('${username}', '${email}', '${md5(password)}')`
    await query(sql);
    ctx.body = {
      code: 0,
      msg: '注册成功！'
    }
  }
});

// 登陆
router.post('/user/login', async (ctx) => {
  const { email, password } = ctx.request.body
  if(!email || !password) {
    ctx.body = {
      code: 200,
      msg: '请填写完整信息！'
    }
    return;
  }
  const sql = `select * from base_sys_user where email = '${email}' and password = '${md5(password)}'`;
  const result = await query(sql);
  if(result.length > 0) {
    ctx.body= {
      code: 0,
      msg: '登录成功！'
    }
  }else {
    ctx.body= {
      code: 100,
      msg: '邮箱或密码错误！'
    }
  }
});

// 获取餐馆列表
router.post('/getRestaurantList', async (ctx) => {
  const restaurant = ctx.request.body?.restaurant;
  let sql;
  sql = restaurant ? `select title, description, mainImage, location, openDay from restaurant_info where title = '${restaurant}'` : 'select title, description, mainImage, location, openDay from restaurant_info';
  const result = await query(sql);
  ctx.body = {
    code: 0,
    msg: '获取餐馆列表成功',
    data: result
  }
});

// 预定餐馆位置
router.post('/submitReservations', async (ctx) => {
  const { name, date, guests } = ctx.request.body;
  const sql = `INSERT INTO reservations_info (remark, numberOfGuests, reservationDate, status, userId, restaurantId, reservationTime) VALUES ('${name}', '${guests}', '${date}', 1, 1, 10, 3)`
  await query(sql);
  ctx.body = {
    code: 0,
    msg: '预订成功！',
  }
});

module.exports = router;