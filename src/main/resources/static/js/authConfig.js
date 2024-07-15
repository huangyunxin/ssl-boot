//根据枚举类型获取服务配置
function getAuthConfigListByType(type) {
    return new Promise((resolve, reject) => {
        let authConfigType = "";

        switch (type) {
            //证书管理枚举
            case "ALI_AUTHORITY":
                authConfigType = "ALI";
                break;
            case "TENCENT_DNS":
                authConfigType = "TENCENT";
                break;
            //证书部署枚举
            case "ALI_OSS":
            case "ALI_CDN":
                authConfigType = "ALI";
                break;
            case "QINIU_CDN":
                authConfigType = "QINIU";
                break;
            case "SERVER_SSH":
                authConfigType = "SERVER_SSH";
                break;
            //证书监控枚举
            case "FEISHU_BOT_HOOK":
                authConfigType = "FEISHU_BOT_HOOK";
                break;
        }

        if (authConfigType) {
            $.ajax({
                url: '/authConfig?current=1&size=10&type=' + authConfigType, // 替换为你的API端点
                type: 'GET',
                async: false,
                success: function (res) {
                    if (res.success && res.data.records) {
                        resolve(res.data.records);
                    } else {
                        layer.msg("服务配置获取失败");
                        reject("服务配置获取失败");
                    }
                }
            });
        }
    })
}
