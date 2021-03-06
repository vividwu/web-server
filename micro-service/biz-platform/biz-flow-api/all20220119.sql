#####engine#####
CREATE TABLE `wf_process` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `name` varchar(100) DEFAULT NULL COMMENT '流程名称',
  `display_name` varchar(200) DEFAULT NULL COMMENT '流程显示名称',
  `type` varchar(100) DEFAULT NULL COMMENT '流程类型',
  `instance_url` varchar(200) DEFAULT NULL COMMENT '实例url',
  `state` tinyint(1) DEFAULT NULL COMMENT '流程是否可用',
  `content` longtext COMMENT '流程模型定义',
  `version` int(2) DEFAULT NULL COMMENT '版本',
  `create_time` varchar(50) DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(50) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`),
  KEY `IDX_PROCESS_NAME` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='流程定义表';

-- wf_order definition

CREATE TABLE `wf_order` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `parent_id` varchar(36) DEFAULT NULL COMMENT '父流程ID',
  `process_id` varchar(36) NOT NULL COMMENT '流程定义ID',
  `creator` varchar(50) DEFAULT NULL COMMENT '发起人',
  `create_time` varchar(50) NOT NULL COMMENT '发起时间',
  `expire_time` varchar(50) DEFAULT NULL COMMENT '期望完成时间',
  `last_update_time` varchar(50) DEFAULT NULL COMMENT '上次更新时间',
  `last_updator` varchar(50) DEFAULT NULL COMMENT '上次更新人',
  `priority` tinyint(1) DEFAULT NULL COMMENT '优先级',
  `parent_node_name` varchar(100) DEFAULT NULL COMMENT '父流程依赖的节点名称',
  `order_no` varchar(50) DEFAULT NULL COMMENT '流程实例编号',
  `variable` varchar(2000) DEFAULT NULL COMMENT '附属变量json存储',
  `version` int(3) DEFAULT NULL COMMENT '版本',
  PRIMARY KEY (`id`),
  KEY `IDX_ORDER_PROCESSID` (`process_id`),
  KEY `IDX_ORDER_NO` (`order_no`),
  KEY `FK_ORDER_PARENTID` (`parent_id`),
  CONSTRAINT `FK_ORDER_PROCESSID` FOREIGN KEY (`process_Id`) REFERENCES `wf_process` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='流程实例表';

-- wf_task definition

CREATE TABLE `wf_task` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `order_id` varchar(36) NOT NULL COMMENT '流程实例ID',
  `task_name` varchar(100) NOT NULL COMMENT '任务名称',
  `display_name` varchar(200) NOT NULL COMMENT '任务显示名称',
  `task_type` varchar(16) NOT NULL COMMENT '任务类型',
  `perform_type` varchar(16) DEFAULT NULL COMMENT '参与类型',
  `operator` varchar(50) DEFAULT NULL COMMENT '任务处理人',
  `create_time` varchar(50) DEFAULT NULL COMMENT '任务创建时间',
  `finish_time` varchar(50) DEFAULT NULL COMMENT '任务完成时间',
  `expire_time` varchar(50) DEFAULT NULL COMMENT '任务期望完成时间',
  `action_url` varchar(200) DEFAULT NULL COMMENT '任务处理的url',
  `parent_task_id` varchar(36) DEFAULT NULL COMMENT '父任务ID',
  `variable` varchar(2000) DEFAULT NULL COMMENT '附属变量json存储',
  `version` tinyint(1) DEFAULT NULL COMMENT '版本',
  PRIMARY KEY (`id`),
  KEY `IDX_TASK_ORDER` (`order_id`),
  KEY `IDX_TASK_TASKNAME` (`task_name`),
  KEY `IDX_TASK_PARENTTASK` (`parent_task_id`),
  CONSTRAINT `FK_TASK_ORDERID` FOREIGN KEY (`order_Id`) REFERENCES `wf_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务表';

-- wf_task_actor definition

CREATE TABLE `wf_task_actor` (
  `task_id` varchar(36) NOT NULL COMMENT '任务ID',
  `actor_id` varchar(50) NOT NULL COMMENT '参与者ID',
  `surrogated_id` varchar(36) DEFAULT NULL,
  KEY `IDX_TASKACTOR_TASK` (`task_id`),
  CONSTRAINT `FK_TASK_ACTOR_TASKID` FOREIGN KEY (`task_id`) REFERENCES `wf_task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务参与者表';

-- wf_surrogate definition

CREATE TABLE `wf_surrogate` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `process_name` varchar(100) DEFAULT NULL COMMENT '流程名称',
  `operator` varchar(50) DEFAULT NULL COMMENT '授权人',
  `surrogate` varchar(50) DEFAULT NULL COMMENT '代理人',
  `odate` datetime DEFAULT NULL COMMENT '操作时间',
  `sdate` datetime DEFAULT NULL COMMENT '开始时间',
  `edate` datetime DEFAULT NULL COMMENT '结束时间',
  `state` tinyint(1) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  KEY `IDX_SURROGATE_OPERATOR` (`operator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='委托代理表';

-- wf_cc_order definition

CREATE TABLE `wf_cc_order` (
  `order_Id` varchar(32) DEFAULT NULL COMMENT '流程实例ID',
  `actor_Id` varchar(50) DEFAULT NULL COMMENT '参与者ID',
  `creator` varchar(50) DEFAULT NULL COMMENT '发起人',
  `create_Time` varchar(50) DEFAULT NULL COMMENT '抄送时间',
  `finish_Time` varchar(50) DEFAULT NULL COMMENT '完成时间',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态',
  KEY `IDX_CCORDER_ORDER` (`order_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='抄送实例表';

-- wf_hist_order definition

CREATE TABLE `wf_hist_order` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `process_id` varchar(36) NOT NULL COMMENT '流程定义ID',
  `order_state` tinyint(1) NOT NULL COMMENT '状态',
  `creator` varchar(50) DEFAULT NULL COMMENT '发起人',
  `create_time` varchar(50) NOT NULL COMMENT '发起时间',
  `end_time` varchar(50) DEFAULT NULL COMMENT '完成时间',
  `expire_time` varchar(50) DEFAULT NULL COMMENT '期望完成时间',
  `priority` tinyint(1) DEFAULT NULL COMMENT '优先级',
  `parent_id` varchar(36) DEFAULT NULL COMMENT '父流程ID',
  `order_no` varchar(50) DEFAULT NULL COMMENT '流程实例编号',
  `variable` varchar(2000) DEFAULT NULL COMMENT '附属变量json存储',
  PRIMARY KEY (`id`),
  KEY `IDX_HIST_ORDER_PROCESSID` (`process_id`),
  KEY `IDX_HIST_ORDER_NO` (`order_no`),
  KEY `FK_HIST_ORDER_PARENTID` (`parent_id`),
  CONSTRAINT `FK_HIST_ORDER_PROCESSID` FOREIGN KEY (`process_Id`) REFERENCES `wf_process` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='历史流程实例表';

-- wf_hist_task definition

CREATE TABLE `wf_hist_task` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `order_id` varchar(36) NOT NULL COMMENT '流程实例ID',
  `task_name` varchar(100) NOT NULL COMMENT '任务名称',
  `display_name` varchar(200) NOT NULL COMMENT '任务显示名称',
  `task_type` varchar(16) NOT NULL COMMENT '任务类型',
  `perform_type` varchar(16) DEFAULT NULL COMMENT '参与类型',
  `task_state` tinyint(1) NOT NULL COMMENT '任务状态',
  `operator` varchar(50) DEFAULT NULL COMMENT '任务处理人',
  `create_time` varchar(50) NOT NULL COMMENT '任务创建时间',
  `finish_time` varchar(50) DEFAULT NULL COMMENT '任务完成时间',
  `expire_time` varchar(50) DEFAULT NULL COMMENT '任务期望完成时间',
  `action_url` varchar(200) DEFAULT NULL COMMENT '任务处理url',
  `parent_task_id` varchar(36) DEFAULT NULL COMMENT '父任务ID',
  `variable` varchar(2000) DEFAULT NULL COMMENT '附属变量json存储',
  PRIMARY KEY (`id`),
  KEY `IDX_HIST_TASK_ORDER` (`order_id`),
  KEY `IDX_HIST_TASK_TASKNAME` (`task_name`),
  KEY `IDX_HIST_TASK_PARENTTASK` (`parent_task_id`),
  CONSTRAINT `FK_HIST_TASK_ORDERID` FOREIGN KEY (`order_id`) REFERENCES `wf_hist_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='历史任务表';

-- wf_hist_task_actor definition

CREATE TABLE `wf_hist_task_actor` (
  `task_id` varchar(36) NOT NULL COMMENT '任务ID',
  `actor_id` varchar(50) NOT NULL COMMENT '参与者ID',
  `surrogated_id` varchar(36) DEFAULT NULL,
  KEY `IDX_HIST_TASKACTOR_TASK` (`task_id`),
  CONSTRAINT `FK_HIST_TASKACTOR` FOREIGN KEY (`task_id`) REFERENCES `wf_hist_task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='历史任务参与者表';

#####config#####

-- wc_billcode_gen definition

CREATE TABLE `wc_billcode_gen` (
  `process_name` varchar(64) NOT NULL,
  `gen_date` datetime NOT NULL,
  `num` int(11) NOT NULL,
  PRIMARY KEY (`num`,`gen_date`,`process_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- wc_common_code definition

CREATE TABLE `wc_common_code` (
  `code` varchar(36) DEFAULT NULL,
  `parent_code` varchar(36) DEFAULT NULL,
  `text` varchar(255) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `category` varchar(36) DEFAULT NULL,
  `flag` varchar(36) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `deleted` varchar(8) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- wc_dic_conf definition

CREATE TABLE `wc_dic_conf` (
  `app_id` varchar(36) DEFAULT NULL,
  `query_code` varchar(36) DEFAULT NULL,
  `ds_name` varchar(255) DEFAULT '' COMMENT '数据源名称：tb_info/http://a.com/b?=1',
  `ds_type` varchar(36) DEFAULT NULL COMMENT '数据源类型：web/mysql',
  `fetch_type` varchar(36) DEFAULT NULL COMMENT '获取方式：select/get/post',
  `params` varchar(255) DEFAULT NULL COMMENT '参数：web使用（{header:[{}],body:{}}）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- wc_file_upload definition

CREATE TABLE `wc_file_upload` (
  `file_id` varchar(64) NOT NULL,
  `biz_id` varchar(36) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `file_size` double DEFAULT NULL,
  `file_type` varchar(8) DEFAULT NULL,
  `dir_name` varchar(64) DEFAULT NULL,
  `uploader` varchar(36) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- wc_flow_history definition

CREATE TABLE `wc_flow_history` (
  `process_id` varchar(64) DEFAULT NULL,
  `process_display_name` varchar(64) DEFAULT NULL,
  `order_id` varchar(64) DEFAULT NULL,
  `order_no` varchar(64) DEFAULT NULL,
  `task_id` varchar(64) DEFAULT NULL,
  `task_key` varchar(64) DEFAULT NULL,
  `task_name` varchar(64) DEFAULT NULL,
  `action` varchar(16) DEFAULT NULL,
  `operator` varchar(64) DEFAULT NULL,
  `operator_view` varchar(255) DEFAULT NULL,
  `create_time` varchar(32) DEFAULT NULL,
  `operator_reason` varchar(255) DEFAULT NULL,
  `memo` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- wc_flow_status definition

CREATE TABLE `wc_flow_status` (
  `order_id` varchar(36) DEFAULT NULL,
  `order_no` varchar(36) DEFAULT NULL,
  `status` varchar(16) DEFAULT NULL,
  `creator` varchar(36) DEFAULT NULL,
  `creator_view` varchar(36) DEFAULT NULL,
  `operator` varchar(36) DEFAULT NULL,
  `operator_view` varchar(36) DEFAULT NULL,
  `memo` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- wc_process_config definition

CREATE TABLE `wc_process_config` (
  `process_id` varchar(64) DEFAULT NULL,
  `process_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `step` int(11) DEFAULT NULL,
  `flow_design_xml` longtext,
  `flow_design_layout` longtext,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `status` int(8) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- wc_process_form_config definition

CREATE TABLE `wc_process_form_config` (
  `process_id` varchar(36) DEFAULT NULL,
  `process_name` varchar(36) DEFAULT NULL,
  `form_code` varchar(36) DEFAULT NULL,
  `form_memo` varchar(255) DEFAULT NULL,
  `content` text,
  `config` text,
  `status` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `operation` varchar(255) DEFAULT NULL COMMENT '操作类型：save,submit,reject,agree'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- wc_process_script definition

CREATE TABLE `wc_process_script` (
  `process_id` varchar(36) DEFAULT NULL,
  `process_name` varchar(32) DEFAULT NULL,
  `category` varchar(8) DEFAULT NULL,
  `script_name` varchar(64) DEFAULT NULL,
  `script_content` varchar(2048) DEFAULT NULL,
  `test_content` varchar(512) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `status` varchar(8) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- wc_process_table_link definition

CREATE TABLE `wc_process_table_link` (
  `table_id` varchar(64) DEFAULT NULL,
  `table_name` varchar(64) DEFAULT NULL,
  `process_name` varchar(64) DEFAULT NULL,
  `category` varchar(8) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- wc_table_fields definition

CREATE TABLE `wc_table_fields` (
  `id` varchar(64) DEFAULT NULL,
  `table_name` varchar(255) DEFAULT NULL,
  `field_name` varchar(255) DEFAULT NULL,
  `data_type` varchar(16) DEFAULT NULL,
  `required` char(1) DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `memo` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- wc_table_info definition

CREATE TABLE `wc_table_info` (
  `id` varchar(64) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `memo` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#####ou#####
-- ou_dept_info definition

CREATE TABLE `ou_dept_info` (
  `id` int(11) NOT NULL COMMENT '不是自增，用来构建full id',
  `name` varchar(255) DEFAULT NULL,
  `parent_id` varchar(255) DEFAULT NULL,
  `full_path_id` varchar(255) DEFAULT NULL,
  `full_path_name` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ou_post_info definition

CREATE TABLE `ou_post_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `code` varchar(16) DEFAULT NULL,
  `flag` varchar(16) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ou_user_dept_post definition

CREATE TABLE `ou_user_dept_post` (
  `user_id` int(11) DEFAULT NULL,
  `dept_id` int(11) DEFAULT NULL,
  `post_code` varchar(16) DEFAULT NULL,
  `status` varchar(8) DEFAULT NULL,
  UNIQUE KEY `idx` (`user_id`,`dept_id`,`post_code`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ou_user_info definition

CREATE TABLE `ou_user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL,
  `num` varchar(32) DEFAULT NULL,
  `display_name` varchar(32) DEFAULT NULL,
  `gender` char(1) DEFAULT NULL COMMENT 'M/F',
  `mail` varchar(128) DEFAULT NULL,
  `mobile` varchar(32) DEFAULT NULL,
  `remark` varchar(1024) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `ext_c1` varchar(255) DEFAULT NULL,
  `ext_c2` varchar(255) DEFAULT NULL,
  `ext_c3` varchar(255) DEFAULT NULL,
  `ext_i1` int(11) DEFAULT NULL,
  `ext_i2` int(11) DEFAULT NULL,
  `ext_i3` int(11) DEFAULT NULL,
  `ext_d1` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

#####privilege#####

#####biz######

-- bz_dept_hrbp definition

CREATE TABLE `bz_dept_hrbp` (
  `dept_id` int(11) DEFAULT NULL,
  `hrbp_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##############

#####init data#####
###################
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('1', '流程天下', '0', '1', '流程天下', NULL, NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('2', 'CFO', '1', '1/2', '流程天下/CFO', NULL, NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('3', 'COO', '1', '1/3', '流程天下/COO', NULL, NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('4', 'CDO', '1', '1/4', '流程天下/CDO', NULL, NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('5', '业务服务体系', '3', '1/3/5', '流程天下/COO/业务服务体系', NULL, NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('6', '企业信息化中心', '5', '1/3/5/6', '流程天下/COO/业务服务体系/企业信息化中心', NULL, NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('7', '开发部', '6', '1/3/5/6/7', '流程天下/COO/业务服务体系/企业信息化中心/开发部', NULL, NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('8', '产品部', '6', '1/3/5/6/8', '流程天下/COO/业务服务体系/企业信息化中心/产品部', NULL, NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('9', 'TL工作室', '4', '1/4/9', '流程天下/CDO/TL工作室', NULL, NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('10', 'DJ项目', '9', '1/4/9/10', '流程天下/CDO/TL工作室/DL项目', NULL, NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('11', '程序中部', '10', '1/4/9/10/11', '流程天下/CDO/TL工作室/DJ项目/程序中部', NULL, NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('12', '财经管理中心', '2', '1/2/12', '流程天下/CFO/财经管理中心', NULL, NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('13', '财务管理部', '12', '1/2/12/13', '流程天下/CFO/财经管理中心/财务管理部', NULL, NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('14', '法律部', '2', '1/2/14', '流程天下/CFO/法律部', '2021-05-24 10:27:31', NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('15', '核算组', '13', '1/2/12/13/15', '流程天下/CFO/财经管理中心/财务管理部/核算组', '2021-05-27 14:03:03', NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('16', '综合服务中心', '3', '1/3/16', '流程天下/COO/综合服务中心', '2021-10-27 10:35:44', NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('17', 'CEO', '1', '1/17', '流程天下/CEO', '2021-11-02 17:45:32', NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('18', '人力资源管理中心', '17', '1/17/18', '流程天下/CEO/人力资源管理中心', '2021-11-02 17:54:14', NULL);
INSERT INTO ou_dept_info (id, name, parent_id, full_path_id, full_path_name, create_time, remark) VALUES ('19', 'HRBP管理部', '18', '1/17/18/19', '流程天下/CEO/人力资源管理中心/HRBP管理部', '2021-11-02 17:55:46', NULL);



INSERT INTO ou_user_info (id, name, num, display_name, gender, mail, mobile, remark, password, create_time, update_time, ext_c1, ext_c2, ext_c3, ext_i1, ext_i2, ext_i3, ext_d1) VALUES ('1', 'test1', 'V000001', '测试1', 'M', 'test1@v.com', '138888888', NULL, '$2a$10$aP4cpPrmqyBYlWRbJFlBzeHGD3EbcWgyxToFgx8b0jv3Un7iioWDy', '2021-01-15 10:26:11', '2021-01-15 10:26:13', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ou_user_info (id, name, num, display_name, gender, mail, mobile, remark, password, create_time, update_time, ext_c1, ext_c2, ext_c3, ext_i1, ext_i2, ext_i3, ext_d1) VALUES ('2', 'test2', 'V000002', '测试2', 'F', 'test2@v.com', '135555555', NULL, '$2a$10$T0rUwe0cCPY6O4uh2WqEOOYH8n9Bsq9cZ6Pu9Cj73/NXZDvtI8hQa', '2021-01-20 19:01:02', '2021-01-20 19:01:07', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ou_user_info (id, name, num, display_name, gender, mail, mobile, remark, password, create_time, update_time, ext_c1, ext_c2, ext_c3, ext_i1, ext_i2, ext_i3, ext_d1) VALUES ('3', 'test3', 'V000003', '测试3', 'M', 'test3@v.com', '135555666', NULL, '$2a$10$T0rUwe0cCPY6O4uh2WqEOOYH8n9Bsq9cZ6Pu9Cj73/NXZDvtI8hQa', '2021-01-21 17:41:38', '2021-01-21 17:41:38', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ou_user_info (id, name, num, display_name, gender, mail, mobile, remark, password, create_time, update_time, ext_c1, ext_c2, ext_c3, ext_i1, ext_i2, ext_i3, ext_d1) VALUES ('4', 'test4', 'V000004', 'ceshi4', 'F', 'test4@v.vom', '139999999', NULL, '$2a$10$T0rUwe0cCPY6O4uh2WqEOOYH8n9Bsq9cZ6Pu9Cj73/NXZDvtI8hQa', '2021-05-13 14:23:14', '2021-05-13 14:23:18', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ou_user_info (id, name, num, display_name, gender, mail, mobile, remark, password, create_time, update_time, ext_c1, ext_c2, ext_c3, ext_i1, ext_i2, ext_i3, ext_d1) VALUES ('5', 'wuwei2_m', 'CY012746', '吴伟2', 'M', NULL, NULL, NULL, '$2a$10$1HZe/18kKP.HRXvQ.QWdxOxql5HlIxg.jnAHIw80p7uv64BRrsTj.', '2021-05-21 16:55:53', '2021-05-24 16:17:43', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ou_user_info (id, name, num, display_name, gender, mail, mobile, remark, password, create_time, update_time, ext_c1, ext_c2, ext_c3, ext_i1, ext_i2, ext_i3, ext_d1) VALUES ('6', 'liangguangjie', 'CY007419', '梁某某', 'M', NULL, NULL, NULL, '$2a$10$CHzcnnrLLrUNFQyBFXa9XeNkiinjjmnGfJ0JlAosVYyAfDiRgReHK', '2021-05-21 17:02:34', '2021-05-21 17:02:34', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ou_user_info (id, name, num, display_name, gender, mail, mobile, remark, password, create_time, update_time, ext_c1, ext_c2, ext_c3, ext_i1, ext_i2, ext_i3, ext_d1) VALUES ('7', 'lujinmei', 'CY011996', '卢某某', 'M', NULL, NULL, NULL, '$2a$10$n6Ve9cpGnPthdUqiTBQtLOIjcBDDQ2x8ss5mnARFQj4LV84tcijOm', '2021-05-24 10:48:27', '2021-05-24 10:48:27', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ou_user_info (id, name, num, display_name, gender, mail, mobile, remark, password, create_time, update_time, ext_c1, ext_c2, ext_c3, ext_i1, ext_i2, ext_i3, ext_d1) VALUES ('8', 'hongxiaojian', 'CY000003', '洪某某', 'M', NULL, NULL, NULL, '$2a$10$3aS6dOb1YvTBOjEHpOwlrekfNb/HASQekZfgmNY26bnbaCJD.ZR3C', '2021-05-24 16:23:03', '2021-05-24 16:23:03', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ou_user_info (id, name, num, display_name, gender, mail, mobile, remark, password, create_time, update_time, ext_c1, ext_c2, ext_c3, ext_i1, ext_i2, ext_i3, ext_d1) VALUES ('9', 'liyating', 'CY012506', '李雅婷', 'M', NULL, NULL, NULL, '$2a$10$ouVuzTtf7u3YDPTE4cOGDODoC7N92rYwNbNHUtpa8JZkaPwrFFmvy', '2021-05-27 14:03:35', '2021-05-27 14:03:35', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ou_user_info (id, name, num, display_name, gender, mail, mobile, remark, password, create_time, update_time, ext_c1, ext_c2, ext_c3, ext_i1, ext_i2, ext_i3, ext_d1) VALUES ('10', 'fengyunlong', 'CY016665', '冯某某', 'M', NULL, NULL, NULL, '$2a$10$h/hWVaJnAw0gfzYV0HKRZuc87MnFwDcNJ.y1mTgtStmKPFhuD4f1S', '2021-07-05 11:17:40', '2021-07-05 11:17:40', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ou_user_info (id, name, num, display_name, gender, mail, mobile, remark, password, create_time, update_time, ext_c1, ext_c2, ext_c3, ext_i1, ext_i2, ext_i3, ext_d1) VALUES ('11', 'liuxiaofeng', 'CY004180', '刘某某', 'M', NULL, NULL, NULL, '$2a$10$260V02VCU33vCP1UKU4V7.VD3yYru/IvadoOnFDKvlWh/R3R8x89G', '2021-07-05 11:18:49', '2021-10-27 09:41:24', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ou_user_info (id, name, num, display_name, gender, mail, mobile, remark, password, create_time, update_time, ext_c1, ext_c2, ext_c3, ext_i1, ext_i2, ext_i3, ext_d1) VALUES ('12', 'songlei', 'CY001580', '宋某某', 'M', NULL, NULL, NULL, '$2a$10$mtcWCstbbyV7tVgAj62WmuA439AHIGLPcVeOVmlMm9wwzORzzonCS', '2021-07-05 11:19:48', '2021-07-05 11:19:48', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ou_user_info (id, name, num, display_name, gender, mail, mobile, remark, password, create_time, update_time, ext_c1, ext_c2, ext_c3, ext_i1, ext_i2, ext_i3, ext_d1) VALUES ('13', 'yuecaiying', 'CY016050', '岳某某', 'M', NULL, NULL, NULL, '$2a$10$bFDNMOJgQ1F2Ui2KzoVHeOelMoWNc3XU3CEshwqV9DJWdXFbFRrHe', '2021-10-27 11:09:29', '2021-10-27 11:09:29', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ou_user_info (id, name, num, display_name, gender, mail, mobile, remark, password, create_time, update_time, ext_c1, ext_c2, ext_c3, ext_i1, ext_i2, ext_i3, ext_d1) VALUES ('14', 'xiechen', 'CY000189', '谢某', 'M', NULL, NULL, NULL, '$2a$10$pIrswfUiXzGUfqDxD3Ew3OV.LVymSZMBEnFMfcwRAVOHew5Leq9Qy', '2021-11-03 09:45:01', '2021-11-03 14:13:35', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ou_user_info (id, name, num, display_name, gender, mail, mobile, remark, password, create_time, update_time, ext_c1, ext_c2, ext_c3, ext_i1, ext_i2, ext_i3, ext_d1) VALUES ('15', 'weiqing', 'CY000243', '韦某', 'M', NULL, NULL, NULL, '$2a$10$j65PvVDl53Fz4g1CVHyG/urlg.NegsAlG2HodWVetN5p8PQVuuk4m', '2021-11-22 14:29:12', '2021-11-22 14:29:12', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ou_user_info (id, name, num, display_name, gender, mail, mobile, remark, password, create_time, update_time, ext_c1, ext_c2, ext_c3, ext_i1, ext_i2, ext_i3, ext_d1) VALUES ('16', 'liye', 'CY000437', '李某', 'M', NULL, NULL, NULL, '$2a$10$HPdaNPVhh4IRSHhfyql9buT2eBugEECl6QdVhC.4UrA0RYM./Exi6', '2021-11-22 14:30:56', '2021-11-22 14:30:56', NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO ou_user_dept_post
(user_id, dept_id, post_code, status)
VALUES(1, 11, 'dev_java', '0');
INSERT INTO ou_user_dept_post
(user_id, dept_id, post_code, status)
VALUES(2, 11, 'dev_java', '0');
INSERT INTO ou_user_dept_post
(user_id, dept_id, post_code, status)
VALUES(3, 11, 'dev_java', '0');
INSERT INTO ou_user_dept_post
(user_id, dept_id, post_code, status)
VALUES(5, 7, 'dev_leader', '0');
INSERT INTO ou_user_dept_post
(user_id, dept_id, post_code, status)
VALUES(6, 6, 'dept_leader', '0');
INSERT INTO ou_user_dept_post
(user_id, dept_id, post_code, status)
VALUES(7, 7, 'dev_java', '0');
INSERT INTO ou_user_dept_post
(user_id, dept_id, post_code, status)
VALUES(8, 3, 'coo', '0');
INSERT INTO ou_user_dept_post
(user_id, dept_id, post_code, status)
VALUES(9, 15, 'finance_staff', '0');
INSERT INTO ou_user_dept_post
(user_id, dept_id, post_code, status)
VALUES(10, 11, 'dev_java', '0');
INSERT INTO ou_user_dept_post
(user_id, dept_id, post_code, status)
VALUES(11, 11, 'dev_leader', '0');
INSERT INTO ou_user_dept_post
(user_id, dept_id, post_code, status)
VALUES(12, 10, 'dept_leader', '0');
INSERT INTO ou_user_dept_post
(user_id, dept_id, post_code, status)
VALUES(13, 13, 'finance_staff', '0');
INSERT INTO ou_user_dept_post
(user_id, dept_id, post_code, status)
VALUES(14, 19, 'hrbp', '0');
INSERT INTO ou_user_dept_post
(user_id, dept_id, post_code, status)
VALUES(8, 5, 'coo', '0');
INSERT INTO ou_user_dept_post
(user_id, dept_id, post_code, status)
VALUES(15, 4, 'cdo', '0');
INSERT INTO ou_user_dept_post
(user_id, dept_id, post_code, status)
VALUES(16, 9, 'dept_leader', '0');


INSERT INTO ou_post_info
(id, name, code, flag, create_time)
VALUES(1, '开发主管', 'dev_leader', 'leader', '2021-01-20 14:49:31');
INSERT INTO ou_post_info
(id, name, code, flag, create_time)
VALUES(2, 'Java工程师', 'dev_java', NULL, '2021-01-20 14:49:45');
INSERT INTO ou_post_info
(id, name, code, flag, create_time)
VALUES(3, '部门经理', 'dept_leader', 'leader', '2021-01-20 14:49:45');
INSERT INTO ou_post_info
(id, name, code, flag, create_time)
VALUES(4, 'COO', 'coo', 'leader', '2021-01-20 16:22:11');
INSERT INTO ou_post_info
(id, name, code, flag, create_time)
VALUES(5, '财务专员', 'finance_staff', 'finance', '2021-01-20 16:22:11');
INSERT INTO ou_post_info
(id, name, code, flag, create_time)
VALUES(6, 'hrbp', 'hrbp', 'hrbp', '2021-11-02 17:58:15');
INSERT INTO ou_post_info
(id, name, code, flag, create_time)
VALUES(7, 'CDO', 'cdo', 'leader', '2021-01-20 16:22:11');


INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(1, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(2, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(3, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(4, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(5, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(6, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(7, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(8, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(9, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(10, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(11, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(12, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(13, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(14, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(15, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(16, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(17, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(18, 14);
INSERT INTO bz_dept_hrbp
(dept_id, hrbp_id)
VALUES(19, 14);
