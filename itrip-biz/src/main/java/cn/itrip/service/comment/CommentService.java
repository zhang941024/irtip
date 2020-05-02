package cn.itrip.service.comment;


import cn.itrip.beans.pojo.ItripComment;
import cn.itrip.beans.pojo.ItripHotel;
import cn.itrip.beans.pojo.ItripImage;
import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.beans.vo.ItripLabelDicVO;
import cn.itrip.beans.vo.comment.ItripHotelDescVO;
import cn.itrip.beans.vo.comment.ItripListCommentVO;
import cn.itrip.beans.vo.comment.ItripScoreCommentVO;
import cn.itrip.beans.vo.comment.ItripSearchCommentVO;
import cn.itrip.common.Page;

import java.util.List;
import java.util.Map;

public interface CommentService {
    List<ItripLabelDicVO> getTravelType() throws Exception;
    void addComment(ItripComment comment, List<ItripImage> imageList) throws Exception;
    Page<ItripListCommentVO> getCommentList(Map map) throws Exception;
    ItripScoreCommentVO getHotelScore(Long id) throws Exception;
    Integer getCount(Map map) throws Exception;
}
