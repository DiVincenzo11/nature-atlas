-- DDL from the supplied archive; all user/account/history records replaced.
SET NAMES utf8mb4;
CREATE TABLE `alldata` (
  `name` text,
  `mouth` text,
  `crest` text,
  `neck` text,
  `back` text,
  `leg` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `birdlog` (
  `id` int NOT NULL AUTO_INCREMENT,
  `picurl` varchar(225) DEFAULT NULL,
  `result` text,
  `detectTime` datetime DEFAULT CURRENT_TIMESTAMP,
  `userid` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `cat_picurl` (
  `name` text,
  `picurl` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `cat_tujian` (
  `name` text,
  `introduction` text,
  `history` text,
  `owner` text,
  `notice` text,
  `image_url` text,
  `knowledge` text,
  `size` text,
  `mao_long` text,
  `nextname` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `chinesebird` (
  `name` text,
  `introduction` text,
  `GrowthAndDistribution` text,
  `characteristic` text,
  `LivingHabit` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `detectlog` (
  `userid` int DEFAULT NULL COMMENT '用户id',
  `picurl` text COMMENT '识别图片url',
  `result` text COMMENT '识别结果',
  `detectTime` datetime DEFAULT CURRENT_TIMESTAMP,
  `category` int DEFAULT NULL COMMENT '识别种类（植物1/鸟类0）',
  `id` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='识别记录';
CREATE TABLE `dog_picurl` (
  `name` text,
  `picurl` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `dog_tujian` (
  `name` text,
  `image_url` text,
  `knowledge` text,
  `introduction` text,
  `history` text,
  `owner` text,
  `notice` text,
  `size` text,
  `mao_long` text,
  `nextname` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `liangqi_picurl` (
  `name` text,
  `picurl` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `liangqi_tujian` (
  `ke` text,
  `shu` text,
  `shu_eng` text,
  `zhong` text,
  `zhong_eng` text,
  `picurl` text,
  `name` text,
  `characterdata` text,
  `natural_history_info` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `picurl` (
  `mu` text,
  `name` text,
  `picurl` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `plant` (
  `name` text,
  `picurl` text,
  `introduction` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `plant2` (
  `name` text,
  `picurl` text,
  `introduction` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `tuiwen` (
  `introduction` text,
  `url` text,
  `picurl` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `tujiandata_final` (
  `mu` text,
  `mu_eng` text,
  `ke` text,
  `ke_eng` text,
  `name` text,
  `name_latin` text,
  `picurl` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `turtle_picurl` (
  `name` text,
  `picurl` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `turtle_tujian` (
  `name` text,
  `image_url` text,
  `knowledge` text,
  `introduction` text,
  `history` text,
  `owner` text,
  `notice` text,
  `size` text,
  `nextname` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `user` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL COMMENT '账号',
  `password` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(30) DEFAULT '小蓝鸟' COMMENT '别名（可修改）',
  `registerDate` datetime DEFAULT (now()) COMMENT '注册日期',
  `headPortrait` varchar(100) DEFAULT '/image/avatar.svg',
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `video` (
  `cover` text,
  `title` text,
  `url` text,
  `id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `voicedata` (
  `Name` text,
  `voiceURL` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
INSERT INTO `tujiandata_final` (`mu`,`mu_eng`,`ke`,`ke_eng`,`name`,`name_latin`,`picurl`) VALUES('雀形目','Passeriformes','绣眼鸟科','Zosteropidae','暗绿绣眼鸟','Zosterops japonicas','/image/bird.png');
INSERT INTO `alldata` (`name`,`mouth`,`crest`,`neck`,`back`,`leg`) VALUES('暗绿绣眼鸟','非黄褐色','不具羽冠','脖纯色','背纯色','腿黑色');
INSERT INTO `picurl` (`mu`,`name`,`picurl`) VALUES('雀形目','暗绿绣眼鸟','/image/bird.png');
INSERT INTO `chinesebird` (`name`,`introduction`,`GrowthAndDistribution`,`characteristic`,`LivingHabit`) VALUES('暗绿绣眼鸟','演示图鉴条目：用于展示分类、详情和特征检索流程。封面为原包提供的鸟类插画，不是该物种实拍。','本演示不提供权威分布信息。','非黄褐色 / 不具羽冠 / 脖纯色 / 背纯色 / 腿黑色','请结合权威图鉴核实物种信息。');
INSERT INTO `tujiandata_final` (`mu`,`mu_eng`,`ke`,`ke_eng`,`name`,`name_latin`,`picurl`) VALUES('雀形目','Passeriformes','椋鸟科','Sturnidae','八哥','Acridotheres cristatellus','/image/bird.png');
INSERT INTO `alldata` (`name`,`mouth`,`crest`,`neck`,`back`,`leg`) VALUES('八哥','黄褐色','不具羽冠','脖纯色','背纯色','腿非黑色');
INSERT INTO `picurl` (`mu`,`name`,`picurl`) VALUES('雀形目','八哥','/image/bird.png');
INSERT INTO `chinesebird` (`name`,`introduction`,`GrowthAndDistribution`,`characteristic`,`LivingHabit`) VALUES('八哥','演示图鉴条目：用于展示分类、详情和特征检索流程。封面为原包提供的鸟类插画，不是该物种实拍。','本演示不提供权威分布信息。','黄褐色 / 不具羽冠 / 脖纯色 / 背纯色 / 腿非黑色','请结合权威图鉴核实物种信息。');
INSERT INTO `tujiandata_final` (`mu`,`mu_eng`,`ke`,`ke_eng`,`name`,`name_latin`,`picurl`) VALUES('鹃形目','Cuculiformes','杜鹃科','Cuculidae','八声杜鹃','Cacomantis merulinus','/image/bird.png');
INSERT INTO `alldata` (`name`,`mouth`,`crest`,`neck`,`back`,`leg`) VALUES('八声杜鹃','非黄褐色','不具羽冠','脖非纯色','背非纯色','腿非黑色');
INSERT INTO `picurl` (`mu`,`name`,`picurl`) VALUES('鹃形目','八声杜鹃','/image/bird.png');
INSERT INTO `chinesebird` (`name`,`introduction`,`GrowthAndDistribution`,`characteristic`,`LivingHabit`) VALUES('八声杜鹃','演示图鉴条目：用于展示分类、详情和特征检索流程。封面为原包提供的鸟类插画，不是该物种实拍。','本演示不提供权威分布信息。','非黄褐色 / 不具羽冠 / 脖非纯色 / 背非纯色 / 腿非黑色','请结合权威图鉴核实物种信息。');
INSERT INTO `tujiandata_final` (`mu`,`mu_eng`,`ke`,`ke_eng`,`name`,`name_latin`,`picurl`) VALUES('雀形目','Passeriformes','鸫科','Turdidae','白喉短翅鸫','Brachypteryx leucophris','/image/bird.png');
INSERT INTO `alldata` (`name`,`mouth`,`crest`,`neck`,`back`,`leg`) VALUES('白喉短翅鸫','非黄褐色','不具羽冠','脖非纯色','背非纯色','腿非黑色');
INSERT INTO `picurl` (`mu`,`name`,`picurl`) VALUES('雀形目','白喉短翅鸫','/image/bird.png');
INSERT INTO `chinesebird` (`name`,`introduction`,`GrowthAndDistribution`,`characteristic`,`LivingHabit`) VALUES('白喉短翅鸫','演示图鉴条目：用于展示分类、详情和特征检索流程。封面为原包提供的鸟类插画，不是该物种实拍。','本演示不提供权威分布信息。','非黄褐色 / 不具羽冠 / 脖非纯色 / 背非纯色 / 腿非黑色','请结合权威图鉴核实物种信息。');
INSERT INTO `tujiandata_final` (`mu`,`mu_eng`,`ke`,`ke_eng`,`name`,`name_latin`,`picurl`) VALUES('雀形目','Passeriformes','鹎科','Pycnonotidae','白喉红臀鹎','Pycnonotus aurigaster','/image/bird.png');
INSERT INTO `alldata` (`name`,`mouth`,`crest`,`neck`,`back`,`leg`) VALUES('白喉红臀鹎','非黄褐色','具羽冠','脖非纯色','背纯色','腿黑色');
INSERT INTO `picurl` (`mu`,`name`,`picurl`) VALUES('雀形目','白喉红臀鹎','/image/bird.png');
INSERT INTO `chinesebird` (`name`,`introduction`,`GrowthAndDistribution`,`characteristic`,`LivingHabit`) VALUES('白喉红臀鹎','演示图鉴条目：用于展示分类、详情和特征检索流程。封面为原包提供的鸟类插画，不是该物种实拍。','本演示不提供权威分布信息。','非黄褐色 / 具羽冠 / 脖非纯色 / 背纯色 / 腿黑色','请结合权威图鉴核实物种信息。');
INSERT INTO `tujiandata_final` (`mu`,`mu_eng`,`ke`,`ke_eng`,`name`,`name_latin`,`picurl`) VALUES('雀形目','Passeriformes','鹡鸰科','Motacillidae','白鹡鸰','Motacilla albaLinnaeus','/image/bird.png');
INSERT INTO `alldata` (`name`,`mouth`,`crest`,`neck`,`back`,`leg`) VALUES('白鹡鸰','非黄褐色','不具羽冠','脖非纯色','背纯色','腿黑色');
INSERT INTO `picurl` (`mu`,`name`,`picurl`) VALUES('雀形目','白鹡鸰','/image/bird.png');
INSERT INTO `chinesebird` (`name`,`introduction`,`GrowthAndDistribution`,`characteristic`,`LivingHabit`) VALUES('白鹡鸰','演示图鉴条目：用于展示分类、详情和特征检索流程。封面为原包提供的鸟类插画，不是该物种实拍。','本演示不提供权威分布信息。','非黄褐色 / 不具羽冠 / 脖非纯色 / 背纯色 / 腿黑色','请结合权威图鉴核实物种信息。');
INSERT INTO `tujiandata_final` (`mu`,`mu_eng`,`ke`,`ke_eng`,`name`,`name_latin`,`picurl`) VALUES('雀形目','Passeriformes','鹀科','Emberizidae','白眉鹀','Emberiza tristrami','/image/bird.png');
INSERT INTO `alldata` (`name`,`mouth`,`crest`,`neck`,`back`,`leg`) VALUES('白眉鹀','非黄褐色','不具羽冠','脖非纯色','背非纯色','腿非黑色');
INSERT INTO `picurl` (`mu`,`name`,`picurl`) VALUES('雀形目','白眉鹀','/image/bird.png');
INSERT INTO `chinesebird` (`name`,`introduction`,`GrowthAndDistribution`,`characteristic`,`LivingHabit`) VALUES('白眉鹀','演示图鉴条目：用于展示分类、详情和特征检索流程。封面为原包提供的鸟类插画，不是该物种实拍。','本演示不提供权威分布信息。','非黄褐色 / 不具羽冠 / 脖非纯色 / 背非纯色 / 腿非黑色','请结合权威图鉴核实物种信息。');
INSERT INTO `tujiandata_final` (`mu`,`mu_eng`,`ke`,`ke_eng`,`name`,`name_latin`,`picurl`) VALUES('雀形目','Passeriformes','鹎科','Pycnonotidae','白头鹎','Pycnonotus sinensis','/image/bird.png');
INSERT INTO `alldata` (`name`,`mouth`,`crest`,`neck`,`back`,`leg`) VALUES('白头鹎','非黄褐色','具羽冠','脖非纯色','背纯色','腿黑色');
INSERT INTO `picurl` (`mu`,`name`,`picurl`) VALUES('雀形目','白头鹎','/image/bird.png');
INSERT INTO `chinesebird` (`name`,`introduction`,`GrowthAndDistribution`,`characteristic`,`LivingHabit`) VALUES('白头鹎','演示图鉴条目：用于展示分类、详情和特征检索流程。封面为原包提供的鸟类插画，不是该物种实拍。','本演示不提供权威分布信息。','非黄褐色 / 具羽冠 / 脖非纯色 / 背纯色 / 腿黑色','请结合权威图鉴核实物种信息。');
INSERT INTO `cat_tujian` (`name`,`introduction`,`history`,`owner`,`notice`,`image_url`,`knowledge`,`size`,`mao_long`,`nextname`) VALUES('金渐层','渐层猫同蓝猫一样，只不过是英短系猫的颜色的叫法，并非是一个品种，包括现在我们常见的，银色渐层猫，银点渐层，金色渐层，黑金渐层，蓝金渐层，蓝点渐层，金点渐层。
金色渐层猫底层绒毛为浅蜜黄色到亮杏黄色。背部、两肋、头部和尾巴的被毛的毛尖被充分地染成黑色，增强了外观呈现的金黄色。四肢和尾巴端部有毛尖染色阴影。面颊、耳簇、腹部和胸部是协调的淡色，色调上比底层绒毛还浅','一般国内的金渐层的出现有两种方式，一种是在银渐层繁育过程中中了彩票了，会生出金渐层来，但是几率很低，而且客观说，两只银渐层出来的金渐层的品相相对不如金渐层和金渐层繁育出来的好。
','金色渐层系英短非常喜欢亲近主人，喜欢乖乖地趴在你的膝盖上睡觉，陪主人玩耍。然而猫咪长期不做运动易会发胖，而体型过胖的猫咪，身体状况也会出现问题。所以，你每天至少要陪它做游戏半个小时左右。','金渐层的肠胃的消化功能相对来说比较的脆弱，特别是主人突然的更换食粮的时候，由于不能很快的适应就会出现呕吐腹泻的情况。因此，主人不要随意的更换猫咪的口粮，并且在更换的时候，不要一次性全部更换掉，也循序渐进逐步的进行更换，每次更换食物的一点，让它有一个适应的过程。喜欢和人亲近接触，也比较的喜欢运动。运动是生命中的比不可少的一项工作，运动可以强健猫咪的体魄，同时也','/image/cat.jpg','中文名:金渐层
别名:英短金渐层
原产地:英国
体型:中型
体重:34-43kg
身高:55-56cm
寿命:13-14年
尾部特征:尾巴根部粗壮,尾巴尖端逐渐变成圆形
眼睛特征:又大又圆，双眼要在同一水平面线上
耳朵特征:耳距要宽,两只耳朵大小适中,且耳尖呈圆形
毛色:金色渐层，黑金渐层，蓝金渐层，蓝点渐层，金点渐层
毛长:短毛
','中型','短毛','英短金渐层');
INSERT INTO `cat_tujian` (`name`,`introduction`,`history`,`owner`,`notice`,`image_url`,`knowledge`,`size`,`mao_long`,`nextname`) VALUES('银渐层','首先说银渐层，本身是指一种毛色，也叫银色阴影色，底毛是白色，毛尖上色，对比鲜明，充满立体感的生动，均匀得是大自然的一张素描，在阳光下，雪地里，都会给人营造一种误入凡尘的感觉。事实上也不止一位繁育人，很多海外科普贴，甚至海外猫舍，都会将银渐层跟童话联系到一起。那既然银渐层是一种毛色，是不是除了英短，还有其他品种有银渐层色呢，答案是肯定的。比如，苏格兰折耳，比如','在2004年前后，俄罗斯金融危机，于是俄罗斯以很低的价格大量向中国出口各种商品，其中也包括宠物，当时有一只被毛颜色奇怪的猫随着商人来到了北京，那个时候最火的猫是美国短毛猫和暹罗猫，而这个奇怪的猫无人问津。一天一个电话打到了叫张琦的年轻人那里，内容是“有一只猫挺有意思，你要不要来看看”。张琦过去了，眼前一亮，这只猫应该就是所谓的英短银渐层，于是张琦以几千元的价','英短银渐层大胆好奇，但非常温柔，适应能力也很强，也不会乱发脾气、更不会乱吵乱叫，它只会尽量爬到比较高的地方，低着头瞪着那双圆圆的大眼睛面带微笑地俯视着你。所以饲养英短银渐层做宠物的人自然也很多，人们也都喜欢它安静的性格，最适合陪伴了~','猫咪喜欢脱毛，因此在喂养的过程中，要加强英它的毛发护理。还要定期的给猫咪喂食吐毛球膏，帮助猫咪清理肠胃的毛球，护理猫咪的肠胃。','/image/cat.jpg','中文名:银渐层
别名:异国短尾猫
原产地:英国
体型:中型
体重:4-8KG
身高:25-35CM
寿命:12年
尾部特征:细长、毛多、蓬松
眼睛特征:蓝绿、黄绿
耳朵特征:耳簇毛为纯白色
毛色:整根毛除了毛尖部分为黑色、色，其他部分都是白色
毛长:短毛
','中型','短毛','异国短尾猫');
INSERT INTO `dog_tujian` (`name`,`image_url`,`knowledge`,`introduction`,`history`,`owner`,`notice`,`size`,`mao_long`,`nextname`) VALUES('贵宾犬','/image/bird.png','中文名:贵宾犬
别名:泰迪犬
原产地:欧洲
体型:小型犬
体重:4-8公斤
身高:22-38厘米
寿命:12-15年
尾部特征:短尾
眼睛特征:棕色
耳朵特征:短耳
毛色:棕色、白色
毛长:短毛
功能:伴侣犬
','贵宾犬，别名：贵妇犬、卷毛狗，泰迪犬是贵宾犬的 “泰迪装 ”，因为这个狗狗造型受到了许多人的喜爱，成为了一种美容潮流，所以将美容成“泰迪装 ”的贵宾犬称之为“泰迪犬”。它智商很高，在犬里排名第二，寿命一般为10-15年。
贵宾犬分为标准型，迷你型，玩具犬三种，外貌区别不大，只是体型的大小不同。
','贵宾犬的产地目前还存在争议，虽然很多人认为贵宾犬原产于法国，并且它一直作为法国的国犬，但德国、前苏联、意大利等国也认为有些品种的贵宾犬产于他们的国家，如白毛品种以法国居多，棕毛品种多产于德国，黑毛品种以前苏联为多，茶褐色品种则以意大利为多。因此有些史学家觉得在贵宾犬的发展历史中，德国、前苏联、法国等这几个国家的推动也必不可少 。但可以确定的是贵宾犬起源于欧洲','贵宾犬最适合城市中的家庭，因为贵宾犬的美容需求在城市地区会更加方便，它活泼、友善、对主人忠诚的性格和特点非常适合作为宠物犬来饲养，是现代家庭生活中优秀的伴侣。','1. 幼犬大概在出生后6个星期左右就断奶。从断奶到犬龄三个月左右，可在早晨7点，中午12点，下午5点和晚上10点共4次喂食，每次狗粮颗粒不超过20粒。2. 幼犬在出生2～3个月时，因为牙齿和下颚还没有完全发育好，应该把肉类切成细块再喂，能够更好的帮助幼犬消化食物。3. 2出生第4个月到第8个月，在早、中、晚喂食，一共三次，这段时间内给幼犬的喂食量大概是成犬的','小型犬','短毛','泰迪犬');
INSERT INTO `dog_tujian` (`name`,`image_url`,`knowledge`,`introduction`,`history`,`owner`,`notice`,`size`,`mao_long`,`nextname`) VALUES('比熊','/image/bird.png','中文名:比熊
别名:比熊
原产地:法国
体型:小型犬
体重:3-7kg
身高:23-30厘米
寿命:14-15年
尾部特征:尾巴被毛丰富，尾根与背中线水平
眼睛特征:眼睛圆，为黑色或是深褐色
耳朵特征:下垂，隐藏在长而流动的毛发中
毛色:白色
毛长:长毛
功能:伴侣犬
','比熊犬(法语：Bichon Frisé，意指“白色卷毛的玩赏用小狗”)，原称巴比熊犬，后缩为比熊犬。是一种小型犬品种。
比熊犬主要分布在欧洲，但近几年来，一些亚洲国家的人们也开始乐于饲养这类乖巧的小型犬，分布范围逐渐扩大。
','比熊犬原产地中海地区，怀疑是巴比特犬和水猎融犬的后裔。该犬最早在加那利群岛 ( CanaryIsland ) 的特纳利夫岛 ( Tene-riffe ) 进行繁衍生息的。
13世纪意大利的水手发现了这种小狗，然后将它们带到了欧洲大陆上，进而逐渐成为了贵族阶层的宠物。而且特别受到西班牙人的青睐。14世纪晚期，西班牙水手在航行中常把它作为交易品，最终在一个名为塔','比熊犬性情开朗、活泼、聪明、勇敢、机警、感情丰富，有较强的适应能力。不太会掉毛，活动空间小，非常适合作为伴侣犬、家庭犬饲养。不过比熊犬对居住环境的要求很高，又比较粘主人，经常需要有人陪伴，繁忙的上班族和学生是不适合养比熊犬的。','比熊犬因为毛较长，所以喝水时常常弄脏嘴边的毛，清理比较不容易。建议用普通水瓶装水，在瓶盖上戳几个小洞，然后倒吊在距离地面较近位置或是挂笼子上的专用装水的容器，最好能让比熊抬头就可以喝到。只要让比熊接触到瓶盖，就可以舔出水来喝，且不会滴出水也不回弄脏嘴边的毛。比熊犬的毛发很独特，软毛里有很硬的螺旋状卷毛，所以很蓬松，摸起来很软，可是这样的毛很容易打结，特别是使','小型犬','长毛','比熊');
INSERT INTO `turtle_tujian` (`name`,`image_url`,`knowledge`,`introduction`,`history`,`owner`,`notice`,`size`,`nextname`) VALUES('丽纹龙蜥','/image/bird.png','中文名:丽纹龙蜥
别名:丽纹攀蜥
原产地:中国
体型:小型犬
体重:小型
身高:小型
寿命:5年
','丽纹攀蜥，近期分类厘定后应改称为丽纹龙蜥，主要分布于我国长江流域地区，如云南，四川，重庆及湖北。该物种雌雄性二态明显，雄性颈鬣发达，背部绿色纵纹边界清晰规则，尾根较雌性膨大；而雌性颈鬣较弱，背部绿色纵纹间有明显深色横纹，绿色纵纹边界不清晰，且尾根部较细。
','该种为中国特产，分布于华中及西南地区。','不建议饲养','人工饲养的丽纹攀蜥主要以蟋蟀、面包虫、大麦虫和蟑螂为食，换句话说就是只要是体型大小都适合当食物的节肢动物它都吃。在人工饲养的条件下应该给于一定得生存空间，刚到家的丽纹攀蜥一定要先静养，否则容易拒食而亡！饲养箱以木箱和玻璃箱为主，但是一定注意，丽纹攀蜥是一种很怕闷热的蜥蜴，千万不能被闷到，否则性命难保。箱子里摆放沉木和杜鹃根给它爬，它喜欢到处爬的。温度在22-','小型','丽纹攀蜥');
INSERT INTO `turtle_tujian` (`name`,`image_url`,`knowledge`,`introduction`,`history`,`owner`,`notice`,`size`,`nextname`) VALUES('黑眉锦蛇','/image/bird.png','中文名:黑眉锦蛇
别名:眉蛇，家蛇，锦蛇，称星蛇，花广蛇
原产地:中国
体型:小型犬
体重:小型
身高:小型
寿命:3年
','黑眉锦蛇（拉丁名：Elaphe taeniura）是大型无毒蛇，全长可达2米左右。黑眉锦蛇别名家蛇﹑秤星蛇﹑菜花蛇（虽王锦蛇也称菜花蛇，但四川、贵州一带称黑眉锦蛇为菜花蛇）﹑黄颌蛇﹑枸皮蛇﹑黄喉蛇﹑慈鳗﹑黄长虫﹑广蛇等。
','黑眉锦蛇分布在我国河北、山西、陕西、甘肃、西藏、四川以东的广大地区以及海南、台湾等岛屿。在东北地区，较早的科学文献中曾记载辽宁有分布，但数十年来一直未采到标本，因而《辽宁动物志》一书没有将它列入其中，后来的《中国动物志》有关卷册也没记录辽宁省有其分布,而在山东也有很少量分布。
其它国家分布：朝鲜、越南、马来半岛北半部，老挝、缅甸、印度（大吉岭，阿萨姆）、日本','不建议饲养','黑眉锦蛇的最适生长发育期是5～10月，其中6～9月是生长盛期。其中由于幼蛇代谢旺盛，又喜活动，因此幼蛇的耗损量最高。 [5]温湿度的变化与黑眉锦蛇的生长发育有密切关系.温度低于18℃时，摄食减少，甚至停止取食，蛇体的生长几乎处于停滞状态;温度高于32℃时，活动减弱，捕食力降低，生长减慢，其生长的最适温度为24-28℃，在22-30℃的适温范围内，随着温度的升','小型','眉蛇，家蛇，锦蛇，称星蛇，花广蛇');
INSERT INTO `liangqi_tujian` (`ke`,`shu`,`shu_eng`,`zhong`,`zhong_eng`,`picurl`,`name`,`characterdata`,`natural_history_info`) VALUES('鱼螈科 Ichthyophiidae','鱼螈属','Ichthyophis','版纳鱼螈','Ichthyophis kohtaoensis','/image/bird.png','同物异名：Ichthyophis bannanica Ichthyophis bannanicus','全长 :雄螈头体长（吻端至肛前缘）309-317mm，雌螈头体长345-411mm
体型 :呈近圆柱形
头 :头小而扁平，头长大于头宽
吻 :吻端圆超出下颌
鼻孔 :鼻孔位于吻端两侧
眼 :眼甚小，隐蔽在胶膜下，眼间距为鼻间距的2倍
上、下颌 :上颌齿与犁腭齿各一列，二者均排列呈“∩”形
舌 :舌呈椭圆形，前端微缺，粘连于口腔底部
颈褶 :颈部腹面有三条颈沟','生境：该螈生活于海拔100-900m植物茂密的热带和亚热带的潮湿地区，常栖息于溪流、小河及其附近的水坑、池塘、沼泽和田边石缝、土洞内或树根下。
习性：白天多伏于石缝、土洞内或树根下，夜间外出觅食蚯蚓等。气温15℃以下即进入冬眠，春天气温在20℃以上时出外活动。4-5月在溪旁近水边的岸上筑巢，雌螈产卵30-62粒，有护卵行为。曾见到幼体栖于铺满卵石的山溪缓流内');
INSERT INTO `liangqi_tujian` (`ke`,`shu`,`shu_eng`,`zhong`,`zhong_eng`,`picurl`,`name`,`characterdata`,`natural_history_info`) VALUES('隐鳃鲵科 Cryptobranchidae','大鲵属','Andrias','大鲵','Andrias davidianus','/image/bird.png','同物异名：Sieboldia davidiana Sieboldia davidi Megalobatrachus davidianus Cryptobranchus davidianus 
俗名：娃娃鱼、人鱼、孩儿鱼、狗鱼、脚鱼、啼鱼、腊狗
曾用名：中国大鲵','全长 :一般全长1m左右，大者可达2m以上
头 :头大，扁平而宽阔，头长略大于头宽
吻 :吻端圆
鼻孔 :外鼻孔小，近吻端，鼻间距为眼间距的1/3或1/2
眼 :眼小，位背侧，无眼睑，眼间距宽，视力极差
唇褶 :口后缘上唇唇褶清晰
舌 :大而圆，与口腔底部粘连，四周略游离
犁骨齿 :犁骨齿列甚长，位于犁腭骨前缘，左右相连，相连处微凹，与上颌齿平行排列呈一弧形','生境：大鲵多栖息于海拔100-1200m的水流较急而清凉的溪河中。
习性：成鲵常栖息于溪河深潭内的岩洞、石穴中，以滩口上下的洞穴内较为常见，很少外出活动，夏秋季节，也有白天上岸觅食或晒太阳的习性。大鲵一般夜出晨归，常住一个洞穴，捕食主要在夜间进行，常守候在滩口乱石间，发现可猎动物经过，突然张嘴捕食，其食量很大，食性甚广，主要以蟹，蛙，鱼，虾以及水生昆虫等为食');
INSERT INTO `user` (`userId`,`username`,`password`,`nickname`,`headPortrait`) VALUES('1','demo','bde410eff40bd16cc6a07d2f57e7043b','演示观察员','/image/avatar.svg');
INSERT INTO `user` (`userId`,`username`,`password`,`nickname`,`headPortrait`) VALUES('2','expert_demo','bde410eff40bd16cc6a07d2f57e7043b','演示专家','/image/avatar.svg');
ALTER TABLE `user` ADD UNIQUE KEY uk_username(username);
CREATE TABLE expert_info(expert_id INT PRIMARY KEY AUTO_INCREMENT,user_id INT,name VARCHAR(64),avatar VARCHAR(255),specialty VARCHAR(255),title VARCHAR(128),description TEXT,contact VARCHAR(128),online BOOLEAN DEFAULT FALSE);
CREATE TABLE expert_message(id BIGINT PRIMARY KEY AUTO_INCREMENT,session_id VARCHAR(64),user_id INT,expert_user_id INT,sender_role VARCHAR(16),content_type VARCHAR(16),content TEXT,created_at DATETIME,INDEX idx_session_time(session_id,created_at));
INSERT INTO expert_info VALUES(1,2,'演示专家','/image/avatar.svg','鸟类观察与特征检索','虚构演示角色','用于展示专家列表与历史消息，不是真实专家服务。','',FALSE);
INSERT INTO expert_message(session_id,user_id,expert_user_id,sender_role,content_type,content,created_at) VALUES('1-2',1,2,'EXPERT','TEXT','欢迎使用本地演示。可以结合嘴色、羽冠和背部特征缩小候选范围。',NOW());
CREATE VIEW combined_tujian AS SELECT name,picurl,'鸟类' AS typeName FROM tujiandata_final UNION ALL SELECT name,image_url,'猫类' FROM cat_tujian UNION ALL SELECT name,image_url,'犬类' FROM dog_tujian UNION ALL SELECT name,image_url,'爬行类' FROM turtle_tujian UNION ALL SELECT zhong,picurl,'两栖类' FROM liangqi_tujian;
