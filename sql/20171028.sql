create table pay_refund_order (
	id bigint(32) NOT NULL AUTO_INCREMENT,
	merchant_no varchar(50) not null default '' comment '商户编号',
	refund int not null comment '支付金额',
	ori_no varchar(50) not null comment '原交易号',
	refund_no varchar(50) not null comment '商户系统原退款单号',
	out_notify_url varchar(255) not null comment '支付结果通知的URL',
	nonce_str varchar(32) not null comment '随机字符串',
	sign_type varchar(10) not null default 'MD5' comment '取值：MD5默认：MD5',
	sign varchar(32) not null comment 'MD5签名结果',
	refund_type int not null default 0 comment '0:退款，1:撤销',
	status int not null default 0 comment '状态',
	version int not null default 1 comment '版本',
	create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  	update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  	errcode int comment '0为退款成功，1为验签失败，2参数错误，3退款失败，4退款中',
  	message varchar(200) comment '成功或错误描述',
  	no varchar(60) comment '富信通退款单号',
  	actual_refund_fee int comment '退款金额',
	PRIMARY KEY (`id`),
	UNIQUE KEY `unq_refund_no` (`refund_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `pay_trd_order` (
	`id` bigint(32) NOT NULL,
	`service` varchar(2) NOT NULL COMMENT '01微信公众号，02微信主扫，03微信反扫，04微信H5支付，05微信APP支付，06支付宝服务窗体，07支付宝主扫，08支付宝反扫',
	`merchant_no` varchar(50) NOT NULL COMMENT '商户编号',
	`outlet_no` varchar(50) NOT NULL COMMENT '门店编号',
	`total` int(50) NOT NULL COMMENT '转账金额',
	`name` varchar(50) NOT NULL COMMENT '订单描述',
	`remark` varchar(60) NOT NULL COMMENT '订单描述',
	`out_trade_no` varchar(50) NOT NULL COMMENT '订单号',
	`create_ip` varchar(16) NOT NULL COMMENT '支付发起ip',
	`out_notify_url` varchar(250) NOT NULL COMMENT '支付结果通知url',
	`sub_openid` varchar(60) NOT NULL COMMENT '微信open_id',
	`buyer_id` varchar(60) NOT NULL COMMENT '支付宝buyer_id',
	`authcode` varchar(32) NOT NULL COMMENT '支付宝授权码',
	`nonce_str` varchar(32) NOT NULL COMMENT '随机字符串',
	`sign_type` varchar(10) NOT NULL DEFAULT 'MD5' COMMENT '加密方式',
	`sign` varchar(32) NOT NULL COMMENT '签名',
	`status` int(5) NOT NULL DEFAULT '0' COMMENT '支付状态',
	`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	`version` int(11) NOT NULL DEFAULT '1' COMMENT '版本',
	PRIMARY KEY (`id`),
	UNIQUE KEY `unq_trd_no` (`out_trade_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table customer_info (
	id bigint(50) not null PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(100) not null,
	outlet_no VARCHAR(100) not null,
	cipher_code VARCHAR(100) not null,
	bank varchar(50) not null,
	bank_code varchar(50) NOT NULL ,
	status int(2) not null default 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table pay_proof
(
	id bigint(32) not null primary key AUTO_INCREMENT,
	merchant_no varchar(50) not null default '' comment '商户编号',
	status int(3) not null default 0,
	`outlet_no` varchar(50) NOT NULL COMMENT '门店编号',
	`total` int(50) NOT NULL COMMENT '转账金额',
	`out_trade_no` varchar(50) NOT NULL COMMENT '订单号',
	`errcode` int(11) DEFAULT NULL COMMENT '结果代码,0为下单成功或交易成功反扫时为交易成功,1为验签失败,2参数错误,3支付失败,4等待用户支付',
	`message` varchar(200) DEFAULT NULL COMMENT '成功或错误描述',
	`no` varchar(50) DEFAULT NULL COMMENT '富信通订单号',
	`code_url` varchar(200) DEFAULT NULL COMMENT '扫码service为02或07支付时才有',
	`pay_info` varchar(200) DEFAULT NULL COMMENT 'Jsapi支付调起参数',
	`actual_total` int(11) DEFAULT NULL COMMENT '支付成功的金额,分为单位.反扫service为03或08才有',
	`transaction_id` varchar(50) DEFAULT NULL COMMENT '上游的交易号,反扫service为03或08才有',
	`paytime` int(11) DEFAULT NULL COMMENT '交易成功的unix时间戳,反扫service为03或08才有',
	UNIQUE KEY `unq_trd_no` (`out_trade_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table pay_reverse_order
(
	id bigint not null primary key AUTO_INCREMENT,
	ext_id bigint not null default 0,
	merchant_no varchar(50) not null,
	no varchar(50) not null,
	out_trade_no varchar(50) not null,
	nonce_str varchar(32) not null ,
	sign_type varchar(10) not null,
	sign varchar(32) not null,
	status int(2) not null default 10,
	version varchar(8) not null,
	create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	UNIQUE KEY unq_trade_no(out_trade_no,ext_id),
	UNIQUE KEY unq_no(no,ext_id)
) ;

