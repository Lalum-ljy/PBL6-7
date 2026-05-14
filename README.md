# 这是第七次提交 4.21
## 项目前端请可访问https://github.com/Lalum-ljy/PBL6-vue    （有更新：第四次&第五次提交 4.21）
### 关于本次提交
- 优化了部分代码的错误日志，增加了一些日志打印区，后期有时间会替换为Logback / Log4j2实现
- 另外移除了主分支中的测试文档，追踪文档啥的，主分支不是垃圾场
- 没了，更新主要在前端

# 这是第六次提交 4.14
## 项目前端请可访问https://github.com/Lalum-ljy/PBL6-vue
### 关于本次提交
- 将数据库建库方式优化为脚本式，杜绝明文查询
- 删除了原本md文档中的数据库代码片段
- 使用方式；
  - 1.命令行
    - 进入项目文件夹，如cd e:\IDE\PBL6，输入mysql -u root -p < database.sql
  - 2.springboot自动配置
    - 修改application.properties文件 ，添加以下配置：spring.sql.init.schema-locations=classpath:database.sql \n spring.sql.init.mode=always
    - 将database.sql文件移动到resources目录,启动后自动配置


# 这是第五次提交 4.1
## 项目前端请可访问https://github.com/Lalum-ljy/PBL6-vue （有更新：第三次提交 4.1）
### 关于本次提交
- 新增图片上传接口
  - 现在新增活动会调用POST/api/activity和POST/api/updown/img两个接口实现创建活动时的图片上传
  - 图片上传路径是src/mian/resources/static/img
  - (这个功能竟然写了一天才完全写好)


**新增mcp板块（重大更新）**
 - 引入mcp大模型功能实现了指令化了增加活动和查询活动操作
 - 正常对话和具体操作时两个业务逻辑板块（ModelEngineService和SmartAssistantService）
 - 两个业务逻辑使用同一个接口POST/api/chat
 - 实现逻辑是先利用大模型的注意力机制将口语化对话转译为固定指令，在分析指令并操作



# 这是第四次提交 3.28
## 项目前端请可访问https://github.com/Lalum-ljy/PBL6-vue （有更新：第二次提交 3.38）
### 关于本次提交
- 新增sys_notice表
  - 该表的5个接口
   - 增
   - 删（按id）
   - 改
   - 查（按id）
   - 获取所有公告
- 该表表结构（已于4.14删除明文查询，表结构请运行脚本）





# 这是第三次提交 3.26
## 项目选题是校园活动发布平台
### 关于本次提交
- **增加了前端部分（vue）**
  - 考虑到项目解耦性，前端仓库与本仓库分开，前端具体可访问https://github.com/Lalum-ljy/PBL6-vue
- 后端更新了2个接口
  - 根据标题和内容搜索（替换了原本的根据内容搜索）
  - 根据是否为热门活动筛选
- 修改了sys_activity表的结构
  - 增加了hot_status字段
  - 让该表的主键（id）可以复用
  - 修改后的数据库查询语句（已于4.14删除明文查询，表结构请运行脚本）
 - 彻底跑通了消息队列，现在消息队列经过测试，完全可用


# 这是第二次提交 3.24
## 项目选题是校园活动发布平台
### 关于本次提交
**本次提交包括：**
- 增加了3个接口
  - 根据标题搜索
  - 根据内容搜索
  - 根据发起人搜索（两表联合）
  - 以上接口均支持模糊搜索，均通过本地测试



# 这是第一次提交 3.23
## 项目选题是校园活动发布平台
### 关于本次提交
**本次提交包括：**
- 完成了项目后端基本架构
- 完成项目后端基本依赖引入
  - mybatis
  - Redis
  - rabbitMQ
  - swagger
- SQL中两表
  - sys_user
  - sys_activity 
- 8个接口
  - sys_user
    - 登录接口
    - 注册接口（设计哈希加密工具类）
  - sys_activity
     - CRUD四个
     - 按时间筛选
     - 按状态（0：未开始 1：进行中 2：已结束 3：已取消）筛选

**须知**
 - 接口使用swagger统一管理，访问http://localhost:8080/swagger-ui/index.html （本机）查看接口api
 - Redis当前未正式调用，本应存入的token暂时使用try catch包裹
 - rabbitMQ用在活动表的状态切换，自动开始/结束任务，**未经测试**
 - **前端建议使用axios统一管理接口**

**表设计**
- 用户表
（已于4.14删除明文查询，表结构请运行脚本）
- **活动表**
（已于4.14删除明文查询，表结构请运行脚本）
