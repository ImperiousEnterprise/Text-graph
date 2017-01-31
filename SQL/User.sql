CREATE DATABASE IF NOT EXISTS Visual;

ALTER DATABASE Visual CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

Use Visual;

CREATE TABLE IF NOT EXISTS `ChatWorkUser` (

  `id` int(11) NOT NULL auto_increment,
  `user_name` varchar(250)  NOT NULL default '' ,
  `chatwork_id`  int(11) NULL,
  PRIMARY KEY  (`id`)

);

ALTER TABLE Visual.ChatWorkUser CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `Group` (

  `id` int(11) NOT NULL auto_increment,
  `group_name` varchar(250)  NOT NULL default '',
  `room_id` int(11) NOT NULL,
  PRIMARY KEY  (`id`)
);
ALTER TABLE Visual.Group CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `Message` (

  `id` int(11) NOT NULL auto_increment,
  `group_id`  int(11) NOT NULL DEFAULT 0,
  `by_user_id` int(11)  NOT NULL default 0,
  `to_user_id` int(11)  NOT NULL default 0,
  `date` DATETIME  NULL,
  `message_id` int(11) NOT NULL default 0,
  PRIMARY KEY  (`id`)
);

ALTER TABLE Visual.Message CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `AppUser` (

  `id` varchar(250)  NOT NULL default '',
  `firstName` varchar(250)  NOT NULL default '',
  `lastName` varchar(250)  NOT NULL default '',
  `fullName` varchar(250)  NOT NULL default '',
  `email` varchar(250)  NOT NULL default '',
  PRIMARY KEY  (`id`)
);

ALTER TABLE Visual.AppUser CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `Logininfo` (

  `id` BIGINT NOT NULL auto_increment,
  `providerID` varchar(250)  NOT NULL default '',
  `providerKey` varchar(250)  NOT NULL default '',
  PRIMARY KEY  (`id`)
);

ALTER TABLE Visual.Logininfo CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `Userlogininfo` (

  `AppUserID` varchar(250)  NOT NULL,
  `loginInfoId` BIGINT NOT NULL DEFAULT 0
);

ALTER TABLE Visual.Userlogininfo CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `Passwordinfo` (

  `hasher` varchar(250)  NOT NULL default '',
  `password` varchar(250)  NOT NULL default '',
  `salt` varchar(250) default '',
  `loginInfoId` BIGINT NOT NULL DEFAULT 0
);
ALTER TABLE Visual.Passwordinfo CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `Oauth1info` (

  `id` BIGINT NOT NULL auto_increment,
  `token`  varchar(250)  NOT NULL default '',
  `secret` varchar(250)  NOT NULL default '',
  `loginInfoId` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY  (`id`)
);

ALTER TABLE Visual.Oauth1info CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `Oauth2info` (

  `id` BIGINT NOT NULL auto_increment,
  `accesstoken`  varchar(250)  NOT NULL default '',
  `tokentype` varchar(250)  NOT NULL default '',
  `expiresin`  varchar(250)  NOT NULL default '',
  `refreshtoken` varchar(250)  NOT NULL default '',
  `logininfoId` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY  (`id`)
);

ALTER TABLE Visual.Oauth2info CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;