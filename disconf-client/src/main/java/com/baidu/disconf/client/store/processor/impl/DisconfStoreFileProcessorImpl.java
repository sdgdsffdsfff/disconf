package com.baidu.disconf.client.store.processor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.disconf.client.common.inter.IDisconfUpdate;
import com.baidu.disconf.client.common.model.DisConfCommonModel;
import com.baidu.disconf.client.common.model.DisconfCenterBaseModel;
import com.baidu.disconf.client.common.model.DisconfCenterFile;
import com.baidu.disconf.client.common.model.DisconfCenterFile.FileItemValue;
import com.baidu.disconf.client.store.DisconfStoreProcessor;
import com.baidu.disconf.client.store.inner.DisconfCenterStore;
import com.baidu.disconf.client.store.processor.model.DisconfValue;
import com.baidu.ub.common.utils.ClassUtils;

/**
 * 配置文件仓库实现器
 * 
 * @author liaoqiqi
 * @version 2014-8-4
 */
public class DisconfStoreFileProcessorImpl implements DisconfStoreProcessor {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(DisconfStoreFileProcessorImpl.class);

    /**
     * 
     */
    @Override
    public void addUpdateCallbackList(String keyName,
            List<IDisconfUpdate> iDisconfUpdateList) {

        if (DisconfCenterStore.getInstance().getConfFileMap()
                .containsKey(keyName)) {

            DisconfCenterStore.getInstance().getConfFileMap().get(keyName)
                    .getDisconfCommonCallbackModel().getDisconfConfUpdates()
                    .addAll(iDisconfUpdateList);
        }
    }

    /**
     * 
     */
    @Override
    public List<IDisconfUpdate> getUpdateCallbackList(String keyName) {

        if (DisconfCenterStore.getInstance().getConfFileMap()
                .containsKey(keyName)) {

            return DisconfCenterStore.getInstance().getConfFileMap()
                    .get(keyName).getDisconfCommonCallbackModel()
                    .getDisconfConfUpdates();
        }

        return new ArrayList<IDisconfUpdate>();
    }

    /**
     * 
     */
    @Override
    public DisConfCommonModel getCommonModel(String keyName) {

        DisconfCenterFile disconfCenterFile = DisconfCenterStore.getInstance()
                .getConfFileMap().get(keyName);

        // 校验是否存在
        if (disconfCenterFile == null) {
            LOGGER.error("canot find " + keyName + " in store....");
            return null;
        }

        return disconfCenterFile.getDisConfCommonModel();
    }

    /**
     * 
     */
    @Override
    public boolean hasThisConf(String keyName) {

        // 配置文件
        if (DisconfCenterStore.getInstance().getConfFileMap()
                .containsKey(keyName)) {
            return true;
        }

        return false;
    }

    /**
     * 
     */
    @Override
    public void inject2Instance(String fileName) {

        DisconfCenterFile disconfCenterFile = DisconfCenterStore.getInstance()
                .getConfFileMap().get(fileName);

        // 校验是否存在
        if (disconfCenterFile == null) {
            LOGGER.error("canot find " + fileName + " in store....");
            return;
        }

        // 无实例无值则 无法注入
        if (disconfCenterFile.getObject() == null) {
            LOGGER.warn(fileName + " 's oboject is null");
            return;
        }

        // 根据类型设置值
        //
        // 注入实体
        //
        Map<String, FileItemValue> keMap = disconfCenterFile.getKeyMaps();
        for (String fileItem : keMap.keySet()) {

            // 根据类型设置值
            try {

                // 默认值
                Object defaultValue = keMap.get(fileItem).getField()
                        .get(disconfCenterFile.getObject());

                if (keMap.get(fileItem).getValue() == null) {

                    // 如果值为空，则直接使用默认值
                    keMap.get(fileItem).getField()
                            .set(disconfCenterFile.getObject(), defaultValue);

                } else {

                    keMap.get(fileItem)
                            .getField()
                            .set(disconfCenterFile.getObject(),
                                    keMap.get(fileItem).getValue());
                }
            } catch (Exception e) {
                LOGGER.error(e.toString(), e);
            }
        }
    }

    /**
     * 
     */
    @Override
    public Object getConfig(String fileName, String keyName) {

        DisconfCenterFile disconfCenterFile = DisconfCenterStore.getInstance()
                .getConfFileMap().get(fileName);

        // 校验是否存在
        if (disconfCenterFile == null) {
            LOGGER.info("canot find " + fileName + " in store....");
            return null;
        }

        if (disconfCenterFile.getKeyMaps().get(keyName) == null) {
            LOGGER.info("canot find " + fileName + ", " + keyName
                    + " in store....");
            return null;
        }

        return disconfCenterFile.getKeyMaps().get(keyName).getValue();
    }

    /**
     * 
     */
    @Override
    public void inject2Store(String fileName, DisconfValue disconfValue) {

        DisconfCenterFile disconfCenterFile = DisconfCenterStore.getInstance()
                .getConfFileMap().get(fileName);

        // 校验是否存在
        if (disconfCenterFile == null) {
            LOGGER.error("canot find " + fileName + " in store....");
            return;
        }

        if (disconfValue == null || disconfValue.getProperties() == null) {
            LOGGER.error("value is null for {}", fileName);
            return;
        }

        // 存储
        Map<String, FileItemValue> keMap = disconfCenterFile.getKeyMaps();
        for (String fileItem : keMap.keySet()) {

            Object object = disconfValue.getProperties().get(fileItem);
            if (object == null) {
                continue;
            }

            // 根据类型设置值
            try {

                Object value = ClassUtils.getValeByType(keMap.get(fileItem)
                        .getField().getType(), (String) object);
                keMap.get(fileItem).setValue(value);

                // 如果Object非null,则顺便也注入
                if (disconfCenterFile.getObject() != null) {
                    keMap.get(fileItem)
                            .getField()
                            .set(disconfCenterFile.getObject(),
                                    keMap.get(fileItem).getValue());
                }

            } catch (Exception e) {
                LOGGER.error(e.toString(), e);
            }
        }
    }

    /**
     * 
     */
    @Override
    public void transformScanData(
            List<DisconfCenterBaseModel> disconfCenterBaseModels) {

        for (DisconfCenterBaseModel disconfCenterFile : disconfCenterBaseModels) {
            DisconfCenterStore.getInstance().storeOneFile(disconfCenterFile);
        }
    }

    /**
     * 
     */
    @Override
    public DisconfCenterBaseModel getConfData(String key) {

        if (DisconfCenterStore.getInstance().getConfFileMap().containsKey(key)) {

            return DisconfCenterStore.getInstance().getConfFileMap().get(key);

        } else {

            return null;
        }
    }

    /**
     * 
     */
    @Override
    public Set<String> getConfKeySet() {
        return DisconfCenterStore.getInstance().getConfFileMap().keySet();
    }

    /**
     * 
     */
    @Override
    public String confToString() {
        return DisconfCenterStore.getInstance().getConfFileMap().toString();
    }
}