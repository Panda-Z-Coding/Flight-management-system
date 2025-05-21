<template>
  <div id="app">

    <router-view></router-view>

  </div>
</template>

<script>
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
      const isLoginPage = currentPath === '/login' || currentPath === '/';

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
              },
              componentProps: {
                title: 'Coze测试',
              },
              auth: {
                type: 'token',
                token: 'pat_BM0XMiZcQwTA25eAgjiE8RBRx6uRRh2ifk0mdWID6z4ucOxnZ0RPKVl06dVt3IUI',
                onRefreshToken: function () {
                  return 'pat_BM0XMiZcQwTA25eAgjiE8RBRx6uRRh2ifk0mdWID6z4ucOxnZ0RPKVl06dVt3IUI';
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
