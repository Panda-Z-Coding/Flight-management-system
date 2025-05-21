// 公告初始化服务
import AnnouncementService from './announcement.js';

// 初始化系统公告
const initAnnouncements = () => {
  // 添加系统默认公告
  AnnouncementService.addAnnouncement({
    title: '系统公告🤩',
    content: '欢迎使用航空订票系统',
    createTime: new Date().toLocaleString()
  });

  AnnouncementService.addAnnouncement({
    title: '优惠活动❤️',
    content: '新用户注册即可获得100元优惠券',
    createTime: new Date().toLocaleString()
  });

  AnnouncementService.addAnnouncement({
    title: '系统维护🫡',
    content: '系统将于每周日凌晨2点至4点进行例行维护',
    createTime: new Date().toLocaleString()
  });
};

export default initAnnouncements;