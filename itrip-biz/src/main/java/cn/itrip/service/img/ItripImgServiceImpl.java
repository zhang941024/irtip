package cn.itrip.service.img;

import cn.itrip.beans.pojo.ItripImage;
import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.dao.image.ItripImageMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ItripImgServiceImpl implements ItripImgService {
    @Resource
    private ItripImageMapper imageMapper;
    @Override
    public List<ItripImageVO> getImgByTargetId(Map map) throws Exception {
        return imageMapper.getItripImageListByMap(map);
    }

    @Override
    @Transactional
    public int insertImg(ItripImage itripImage) throws Exception {
        return imageMapper.insertItripImage(itripImage);
    }
}
