package cn.itrip.service.linkuser;

import cn.itrip.beans.pojo.ItripUserLinkUser;
import cn.itrip.beans.vo.order.ItripOrderLinkUserVo;
import cn.itrip.beans.vo.userinfo.ItripAddUserLinkUserVO;

import java.util.List;
import java.util.Map;

public interface LinkuserService {
    int addUserLinkuser(ItripUserLinkUser linkUser) throws Exception;
    List<Long> getLinkuser() throws Exception;
    int deleteLinkuser(Long[] ids) throws Exception;
    List<ItripUserLinkUser> queryUserLinkuser(Map map) throws Exception;
    int modifyUserLinkuser(ItripUserLinkUser linkUser) throws Exception;
}
