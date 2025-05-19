package com.calmresponse.Utils;

import com.calmresponse.constant.UserConstant;
import com.calmresponse.model.DTO.UserDTO;
import com.calmresponse.model.domain.User;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 工具类
 *
 * @author yufeng
 * @since 2025/5/16 6:47
 */
public class Utils {
    public static boolean isAuth(HttpServletRequest request){
        //从session中取出存储的UserDTO数据
        Object attribute = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
        UserDTO userDTO =(UserDTO)attribute;
        //不是管理员
        if (userDTO.getPermission()!=1){
            return false;
        }
        return true;
    }
}
