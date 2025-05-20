package com.calmresponse.utils;



import com.calmresponse.constant.UserConstant;
import com.calmresponse.dto.UserDTO;

import javax.servlet.http.HttpServletRequest;

/**
 * 工具类
 *判断是否为管理员
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
