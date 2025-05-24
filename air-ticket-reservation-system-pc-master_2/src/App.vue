<template>
  <div id="app">

    <router-view></router-view>

  </div>
</template>

<script>
import AnnouncementService from '@/store/announcement.js';

export default {
  name: 'App',
  data() {
    return {
      cozeClient: null,
    };
  },
  watch: {
    '$route'(to, from) {
      this.updateCozeChatVisibility(to.path);
    }
  },
  mounted() {
    this.waitForSDKAndInitialize();
  },
  methods: {
    waitForSDKAndInitialize() {
      if (window.CozeWebSDK) {
        this.updateCozeChatVisibility(this.$route.path);
      } else {
        let attempts = 0;
        const intervalId = setInterval(() => {
          attempts++;
          if (window.CozeWebSDK) {
            clearInterval(intervalId);
            this.updateCozeChatVisibility(this.$route.path);
          } else if (attempts > 30) { // Try for 3 seconds (30 * 100ms)
            clearInterval(intervalId);
            console.error('Coze SDK (CozeWebSDK) not found on window object after waiting. Ensure the SDK script is loaded correctly in index.html.');
          }
        }, 100);
      }
    },
    updateCozeChatVisibility(currentPath) {
      const isLoginPage = currentPath === '/login' || currentPath === '/' || currentPath === '/register';

      if (isLoginPage) {
        if (this.cozeClient) {
          let destroyed = false;
          if (typeof this.cozeClient.destroy === 'function') {
            try {
              this.cozeClient.destroy();
              console.log('Coze SDK client destroyed via method.');
              destroyed = true;
            } catch (e) {
              console.error('Error calling cozeClient.destroy():', e);
            }
          }
          
          const cozeWidgetElement = document.getElementById('coze-web-chat');
          if (cozeWidgetElement && cozeWidgetElement.parentNode) {
            cozeWidgetElement.parentNode.removeChild(cozeWidgetElement);
            console.log('Coze widget DOM element (id: coze-web-chat) removed.');
            destroyed = true;
          }

          if (destroyed) {
            this.cozeClient = null;
          } else if (!(this.cozeClient.destroy)) { 
             console.warn('Coze SDK client may still be active on login page. Could not find a destroy method or known DOM element to remove.');
          }
        }
      } else {
        if (!this.cozeClient && window.CozeWebSDK) {
          try {
            this.cozeClient = new CozeWebSDK.WebChatClient({
              config: {
                bot_id: '7503108659334037544',
                hide_entry: true // 隐藏聊天入口
              },
              componentProps: {
                title: '智能航班助手',
                visible: false // 默认不显示聊天窗口
              },
              auth: {
                type: 'token',
                token: 'pat_LPhxYa5Yj7BV0WGHycQUcjvqMuTo7oa5co87JGQDEPM8ExewC2BIOUgHGsMbrLdp',
                onRefreshToken: function () {
                  return 'pat_LPhxYa5Yj7BV0WGHycQUcjvqMuTo7oa5co87JGQDEPM8ExewC2BIOUgHGsMbrLdp';
                }
              },
              callbacks: {
                onMessageReceived: (message) => {
                  // 将机器人消息添加到公告系统
                  if (message && message.content) {
                    AnnouncementService.addAnnouncement({
                      title: 'AI智能助手',
                      content: message.content,
                      createTime: new Date().toLocaleString()
                    });
                  }
                }
              }
            });
            console.log('Coze SDK client initialized.');
          } catch (error) {
            console.error('Error initializing Coze SDK:', error);
            this.cozeClient = null;
          }
        } else if (!window.CozeWebSDK) {
            console.warn('Attempted to initialize Coze SDK, but CozeWebSDK is not available on window.');
        }
      }
    }
  },
  beforeDestroy() {
    if (this.cozeClient && typeof this.cozeClient.destroy === 'function') {
      try {
        this.cozeClient.destroy();
      } catch (e) {
        console.error('Error destroying Coze client in beforeDestroy:', e);
      }
    }
    this.cozeClient = null;
  }
};
</script>

<!--//设置中占满全屏幕，外边距设为0-->
<style>
html,body,#app{
  height: 100%;
  margin: 0;
}
</style>
