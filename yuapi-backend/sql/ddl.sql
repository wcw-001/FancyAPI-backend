-- 创建库
create database if not exists yuapi;

-- 切换库
use yuapi;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userName     varchar(256)                           null comment '用户昵称',
    userAccount  varchar(256)                           not null comment '账号',
    userAvatar   varchar(1024)                          null comment '用户头像',
    gender       tinyint                                null comment '性别',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user / admin',
    userPassword varchar(512)                           not null comment '密码',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    constraint uni_userAccount
        unique (userAccount)
) comment '用户';

-- ----------------------------
DROP TABLE IF EXISTS `t_ai_intelligent`;
CREATE TABLE `t_ai_intelligent`  (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                     `input_message` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户输入信息',
                                     `ai_result` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'AI生成结果',
                                     `user_id` bigint(20) NULL DEFAULT NULL,
                                     `create_time` datetime NULL DEFAULT NULL,
                                     `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1736624313104711683 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_interface_info
-- ----------------------------
DROP TABLE IF EXISTS `t_user_interface_info`;
CREATE TABLE `t_user_interface_info`  (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                          `user_id` bigint(20) NOT NULL COMMENT '用户id或管理员id',
                                          `interface_id` bigint(20) NOT NULL COMMENT '1 表示AI聊天接口 2表示智能分析接口 ',
                                          `total_num` int(11) NOT NULL DEFAULT 0 COMMENT '总共调用接口次数\r\n',
                                          `left_num` int(11) NOT NULL DEFAULT 0 COMMENT '剩余接口可用次数',
                                          `create_time` datetime NOT NULL COMMENT '创建时间',
                                          `update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;
