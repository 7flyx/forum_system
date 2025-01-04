
model 包：实体对象，根据数据库和生成，还有一些和业务相关的属性
dao 包：数据库访问，和数据库交互
services 包：业务处理相关的接口与实现，所有业务都在services中实现
controller 包：提供URL映射，用来接收参数并做校验，调用services中的业务代码，返回执行结果
src/main/resources/mapper 目录：Mybatis映射文件，配置数据库 实体与类之间的映射关系
src/main/resources/static 目录：前端资源