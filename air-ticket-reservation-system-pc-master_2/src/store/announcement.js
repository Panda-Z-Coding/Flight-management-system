// 公告管理服务

const AnnouncementService = {
  // 存储公告列表
  announcements: [],

  // 添加公告
  addAnnouncement(announcement) {
    // 确保公告有标题、内容和时间
    const newAnnouncement = {
      title: announcement.title || '系统通知',
      content: announcement.content || '',
      createTime: announcement.createTime || new Date().toLocaleString()
    };
    
    // 将新公告添加到列表开头
    this.announcements.unshift(newAnnouncement);
    
    // 如果需要，可以限制公告数量
    if (this.announcements.length > 10) {
      this.announcements.pop();
    }
    
    // 触发存储事件，通知其他组件公告已更新
    this.notifyUpdate();
    
    return newAnnouncement;
  },

  // 获取所有公告
  getAnnouncements() {
    return this.announcements;
  },

  // 清空公告
  clearAnnouncements() {
    this.announcements = [];
    this.notifyUpdate();
  },

  // 通知更新
  notifyUpdate() {
    // 使用自定义事件通知组件公告已更新
    const event = new CustomEvent('announcements-updated', {
      detail: { announcements: this.announcements }
    });
    document.dispatchEvent(event);
  }
};

export default AnnouncementService;