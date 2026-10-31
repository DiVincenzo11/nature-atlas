package com.keshihua.ksh.service.serviceImpl;

import com.keshihua.ksh.mapper.TuiwenMapper;
import com.keshihua.ksh.pojo.Tuiwen;
import com.keshihua.ksh.service.TuiwenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TuiwenServiceimpl implements TuiwenService {
    @Autowired
    private TuiwenMapper tuiwenMapper;
    public List<Tuiwen> gettuiwen(){
        return tuiwenMapper.gettuiwen();
    }
}
