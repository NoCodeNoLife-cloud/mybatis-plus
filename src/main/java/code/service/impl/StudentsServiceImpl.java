package code.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import code.entity.Students;
import code.service.StudentsService;
import code.mapper.StudentsMapper;
import org.springframework.stereotype.Service;

/**
 * @author ethereal
 */
@Service
public class StudentsServiceImpl extends ServiceImpl<StudentsMapper, Students> implements StudentsService {}
