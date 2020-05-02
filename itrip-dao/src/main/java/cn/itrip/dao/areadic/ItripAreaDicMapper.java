package cn.itrip.dao.areadic;

import cn.itrip.beans.pojo.ItripAreaDic;

import java.util.List;
import java.util.Map;

public interface ItripAreaDicMapper {

	public ItripAreaDic getItripAreaDicById(Long id)throws Exception;

	public List<ItripAreaDic>	getItripAreaDicListByMap(Map<String, Object> param)throws Exception;

	public Integer getItripAreaDicCountByMap(Map<String, Object> param)throws Exception;

	public Integer insertItripAreaDic(ItripAreaDic itripAreaDic)throws Exception;

	public Integer updateItripAreaDic(ItripAreaDic itripAreaDic)throws Exception;

	public Integer deleteItripAreaDicById(Long id)throws Exception;

}
