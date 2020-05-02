package cn.itrip.service.comment;

import cn.itrip.beans.pojo.ItripComment;
import cn.itrip.beans.pojo.ItripImage;
import cn.itrip.beans.vo.ItripLabelDicVO;
import cn.itrip.beans.vo.comment.ItripListCommentVO;
import cn.itrip.beans.vo.comment.ItripScoreCommentVO;
import cn.itrip.common.Page;
import cn.itrip.dao.comment.ItripCommentMapper;
import cn.itrip.dao.hotelorder.ItripHotelOrderMapper;
import cn.itrip.dao.image.ItripImageMapper;
import cn.itrip.dao.labeldic.ItripLabelDicMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
    @Resource
    private ItripLabelDicMapper dicMapper;
    @Resource
    private ItripCommentMapper commentMapper;
    @Resource
    private ItripImageMapper imageMapper;
    @Resource
    private ItripHotelOrderMapper orderMapper;
    @Override
    public List<ItripLabelDicVO> getTravelType() throws Exception {
        return dicMapper.getItripLabelDicByParentId(107L);
    }

    @Override
    public void addComment(ItripComment comment, List<ItripImage> imageList) throws Exception {
       int sum = comment.getFacilitiesScore()+comment.getHygieneScore()+comment.getPositionScore()+comment.getServiceScore();
       int score = (int) Math.round(sum * 1.0 / 4);
       comment.setScore(score);
       if(commentMapper.insertItripComment(comment) > 0){

           if(imageList != null && imageList.size() > 0){
               for (ItripImage image : imageList){
                   image.setTargetId(comment.getId());
                   imageMapper.insertItripImage(image);
               }
               orderMapper.updateHotelOrderStatus(comment.getOrderId(),comment.getCreatedBy());
           }
       }

    }

    @Override
    public Page<ItripListCommentVO> getCommentList(Map map) throws Exception {
        Integer beginPos = ((Integer) map.get("pageNo")-1)*(Integer)map.get("pageSize");
        map.put("beginPos",beginPos);
        List list = commentMapper.getItripCommentListByMap(map);
        Integer total = commentMapper.getItripCommentCountByMap(map);

        Integer pageNo = (Integer) map.get("pageNo");
        Integer pageSize = (Integer) map.get("pageSize");
        Page page = new Page(pageNo,pageSize,total);
        page.setRows(list);
        return page;
    }

    @Override
    public ItripScoreCommentVO getHotelScore(Long id) throws Exception {
        return commentMapper.getCommentAvgScore(id);
    }

    @Override
    public Integer getCount(Map map) throws Exception {
        return commentMapper.getItripCommentCountByMap(map);
    }


}
