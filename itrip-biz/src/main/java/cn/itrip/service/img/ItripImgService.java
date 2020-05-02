package cn.itrip.service.img;

import cn.itrip.beans.pojo.ItripImage;
import cn.itrip.beans.vo.ItripImageVO;

import java.util.List;
import java.util.Map;

public interface ItripImgService {
    List<ItripImageVO> getImgByTargetId(Map map) throws Exception;
    int insertImg(ItripImage itripImage) throws Exception;
}
