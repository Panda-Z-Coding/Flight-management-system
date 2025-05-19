package com.calmresponse.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmresponse.common.BaseResponse;
import com.calmresponse.common.ErrorCode;
import com.calmresponse.common.ResultUtils;
import com.calmresponse.constant.UserConstant;
import com.calmresponse.exception.BusinessException;
import com.calmresponse.mapper.UserMapper;
import com.calmresponse.model.DTO.UserDTO;
import com.calmresponse.model.VO.UserVO;
import com.calmresponse.model.domain.User;
import com.calmresponse.model.request.UserLoginRequest;
import com.calmresponse.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
* @author yufeng
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2025-05-15 18:45:44
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Autowired
    private UserMapper userMapper;
    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return
     */
    @Override
    public BaseResponse login(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String username = userLoginRequest.getUsername();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //账号长度校验
        if(username != null && username.length() > 0){
            queryWrapper.eq(User::getUsername, username);
        }
        String password = userLoginRequest.getUserPassword();
        //查询用户是否存在
        List<User> userList = this.list(queryWrapper);
        if (userList == null || userList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        User user = userList.get(0);
        //查询密码是否正确
        // 对密码进行加密
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        String md5Password = md5.digestHex(password).toString();

        if(!user.getPassword().equals(md5Password)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
        }
        if(user.getStatus().equals("0")){
            throw new BusinessException(ErrorCode.USER_STATUS_ERROR,"用户状态异常");
        }
        //用户信息脱敏
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user,userDTO);

        //登陆成功
        //记录用户的登录态session
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATUS,userDTO);
//        //将UserDTO转换为Map集合，并做一些字段的筛选和过滤
//        //转换成Map的目的是存入redis
//        Map<String, Object> userMap = BeanUtil.beanToMap(
//                userDTO, new HashMap<>(), CopyOptions.create().
//                        setIgnoreNullValue(true).
//                        setFieldValueEditor((fieldName, fieldValue) -> {
//                            if (fieldValue == null) {
//                                fieldValue = "0";
//                            } else {
//                                fieldValue = fieldValue + "";
//                            }
//                            return fieldValue;
//                        }));
//        //将token存入redis
//        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY + token, userMap);
//        //设置过期时间
//        stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.MINUTES);
        return ResultUtils.success(userDTO);
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

    //鉴权
    @Override
    public List<UserVO> selectAll() {
        //查询所有权限为0的用户
        List<User> userList=this.list();
        List<UserVO> userVOList = new ArrayList<>();
        for (User user : userList) {
            if(user.getPermission()==0){
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user,userVO);
                userVOList.add(userVO);
            }
        }
        return userVOList;
    }

    //鉴权
    // 启用/禁用用户
    @Override
    public boolean startOrStop(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        return this.updateById(user);
    }


}




