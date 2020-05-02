package com.test;

import cn.itrip.search.dao.BaseQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/applicationContext.xml")
public class TestSolr {
    @Resource
    private BaseQuery query;
    @Test
    public void test01(){

    }
}
