package com.calmresponse.service.Impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmresponse.common.ErrorCode;
import com.calmresponse.constant.UserConstant;
import com.calmresponse.dto.UserDTO;
import com.calmresponse.entity.User;
import com.calmresponse.exception.BusinessException;
import com.calmresponse.mapper.LoginMapper;
import com.calmresponse.request.AdminLoginRequest;
import com.calmresponse.request.UserLoginRequest;
import com.calmresponse.result.Result;
import com.calmresponse.service.LoginService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@Service
public class LoginServicelmpl extends ServiceImpl<LoginMapper, User> implements LoginService {
    @Autowired
    private LoginMapper loginMapper;

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求对象
     * @param request          HttpServletRequest 对象
     * @return 登录结果
     */
    @Override
    public Result login(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String username = userLoginRequest.getUsername();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        // 筛选出permission为0的用户
        queryWrapper.eq(User::getUsername, username)
                .eq(User::getPermission, 0);
        // 账号长度校验
        if (username != null && username.length() > 0) {
            queryWrapper.eq(User::getUsername, username);
        }
        String password = userLoginRequest.getUserPassword();
        // 查询用户是否存在
        User user = this.getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        // 查询密码是否正确
        // 对密码进行加密
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        String md5Password = md5.digestHex(password).toString();

        if (!user.getPassword().equals(md5Password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        if (user.getStatus().equals(1)) { // 修正状态比较逻辑
            throw new BusinessException(ErrorCode.USER_STATUS_ERROR, "用户状态异常");
        }
        // 用户信息脱敏
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);

        // 登陆成功
        // 记录用户的登录态 session
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATUS, userDTO);
        return Result.success(userDTO);
    }

    /**
     * 管理员登录
     *
     * @param adminLoginRequest 管理员登录请求对象
     * @return 登录结果
     */
    @Override
    public Result login(AdminLoginRequest adminLoginRequest, HttpServletRequest request) {
        String username = adminLoginRequest.getUsername();
        String password = adminLoginRequest.getUserPassword();

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        // 筛选出权限为 1（管理员）的用户
        queryWrapper.eq(User::getUsername, username)
                .eq(User::getPermission, 1);

        // 查询管理员是否存在
        User admin = this.getOne(queryWrapper);
        if (admin == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "管理员不存在");
        }

        // 对密码进行加密
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        String md5Password = md5.digestHex(password).toString();

        // 验证密码是否正确
        if (!admin.getPassword().equals(md5Password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }

        // 检查管理员状态
        if (admin.getStatus().equals(1)) {
            throw new BusinessException(ErrorCode.USER_STATUS_ERROR, "管理员状态异常");
        }

        // 管理员信息脱敏
        UserDTO adminDTO = new UserDTO();
        BeanUtils.copyProperties(admin, adminDTO);

        // 记录管理员的登录态 session
        request.getSession().setAttribute(UserConstant.ADMIN_LOGIN_STATUS, adminDTO);
        return Result.success(adminDTO);
    }


    /**
     * 注册校验
     * @param username 账号
     * @param password 密码
     * @param checkPassword 校验密码
     * @return 用户id
     */
    @Override
    public long userRegister(String username, String password, String checkPassword){
        //1.注册校验
        //账号，密码，校验密码不为空
        if(StrUtil.isAllBlank(username,password,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号为空");
        }
        //账户不小于4位
        if (username.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号过短");
        }
        //密码不小于8位
        if (password.length()<8||checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }
        //密码与校验密码是否相同
        if(!password.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码与校验密码不同");
        }
        //账户不包含特殊字符
        String regex = "^[a-zA-Z0-9_]+$";
        Pattern pattern = Pattern.compile(regex);
        boolean isMatch = pattern.matcher(username).matches();
        if (!isMatch){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }
        //账户不能重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username);
        long count = this.count(userQueryWrapper);
        if (count>0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"已存在该账户");
        }

        //2.对密码进行加密
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        String cryptoPassword = md5.digestHex(password);

        //3.向数据库中插入用户数据
        User user = new User();
        user.setUsername(username);
        user.setPassword(cryptoPassword);
        boolean saveBaseResponse = this.save(user);
        if (!saveBaseResponse){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"插入数据失败");
        }

        return user.getId();

    }
}
