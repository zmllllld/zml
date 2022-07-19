package com.ishang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ishang.entity.Admin;
import com.ishang.form.AdminForm;
import com.ishang.mapper.AdminMapper;
import com.ishang.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ishang.vo.AdminVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zml
 * @since 2022-04-17
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {



}
