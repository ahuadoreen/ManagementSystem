package com.example.ms.basic.service.impl;

import com.example.tools.entity.Log;
import com.example.ms.basic.mapper.LogMapper;
import com.example.ms.basic.service.LogService;
import com.example.ms.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class LogServiceImpl extends BaseServiceImpl<LogMapper, Log> implements LogService {
}
