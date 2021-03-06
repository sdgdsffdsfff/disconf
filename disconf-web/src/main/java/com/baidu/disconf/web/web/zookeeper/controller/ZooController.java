package com.baidu.disconf.web.web.zookeeper.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.core.common.json.ValueVo;
import com.baidu.disconf.web.service.zookeeper.config.ZooConfig;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;

/**
 * 
 * Zoo API
 * 
 * @author liaoqiqi
 * @version 2014-1-20
 */
@Controller
@RequestMapping(WebConstants.API_PREFIX + "/zoo")
public class ZooController extends BaseController {

    protected static final Logger LOG = LoggerFactory
            .getLogger(ZooController.class);

    @Autowired
    private ZooConfig zooConfig;

    /**
     * 获取Zookeeper地址
     * 
     * @param demoUserId
     * @return
     */
    @RequestMapping(value = "/hosts", method = RequestMethod.GET)
    @ResponseBody
    public ValueVo getHosts() {

        ValueVo confItemVo = new ValueVo();
        confItemVo.setStatus(Constants.OK);
        confItemVo.setValue(zooConfig.getZooHosts());

        return confItemVo;
    }

    /**
     * 获取ZK prefix
     * 
     * @param demoUserId
     * @return
     */
    @RequestMapping(value = "/prefix", method = RequestMethod.GET)
    @ResponseBody
    public ValueVo getPrefixUrl() {

        ValueVo confItemVo = new ValueVo();
        confItemVo.setStatus(Constants.OK);
        confItemVo.setValue(zooConfig.getZookeeperUrlPrefix());

        return confItemVo;
    }

}