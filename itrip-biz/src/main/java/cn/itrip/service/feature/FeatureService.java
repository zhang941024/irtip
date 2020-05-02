package cn.itrip.service.feature;

import cn.itrip.beans.pojo.ItripLabelDic;

import java.util.List;

public interface FeatureService {
    List<ItripLabelDic> queryDetailsHotel(Long id) throws Exception;
}
