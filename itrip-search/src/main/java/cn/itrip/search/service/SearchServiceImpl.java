package cn.itrip.search.service;

import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.search.bean.ItripHotelVO;
import cn.itrip.search.dao.BaseQuery;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class SearchServiceImpl implements SearchService {
    @Resource
    private BaseQuery baseQuery;
    @Override
    public Page<ItripHotelVO> queryHotelPage(SearchHotelVO vo, Integer pageNo, Integer pageSize) throws Exception {
        SolrQuery query = new SolrQuery("*:*");
        StringBuffer tempQuery = new StringBuffer();
        int tempFlag = 0;
        if (EmptyUtils.isNotEmpty(vo)) {
            // destination : "" and  keyword :""
            //keyword :""
            if (EmptyUtils.isNotEmpty(vo.getDestination())) {
                tempQuery.append(" destination :" + vo.getDestination());
                tempFlag = 1;
            }
            if (EmptyUtils.isNotEmpty(vo.getHotelLevel())) {
                query.addFilterQuery("hotelLevel:" + vo.getHotelLevel() + "");
            }
            if (EmptyUtils.isNotEmpty(vo.getKeywords())) {
                if (tempFlag == 1) {
                    tempQuery.append(" AND keyword :" + vo.getKeywords());
                } else {
                    tempQuery.append(" keyword :" + vo.getKeywords());
                }
            }
            if (EmptyUtils.isNotEmpty(vo.getFeatureIds())) {
                StringBuffer buffer = new StringBuffer("(");
                int flag = 0;
                String featureIdArray[] = vo.getFeatureIds().split(",");
                for (String featureId : featureIdArray) {
                    if (flag == 0) {
                        buffer.append(" featureIds:" + "*," + featureId + ",*");
                    } else {
                        buffer.append(" OR featureIds:" + "*," + featureId + ",*");
                    }
                    flag++;
                }
                buffer.append(")");
                query.addFilterQuery(buffer.toString());
            }
            if (EmptyUtils.isNotEmpty(vo.getTradeAreaIds())) {
                StringBuffer buffer = new StringBuffer("(");
                int flag = 0;
                String tradeAreaIdArray[] = vo.getTradeAreaIds().split(",");
                for (String tradeAreaId : tradeAreaIdArray) {
                    if (flag == 0) {
                        buffer.append(" tradingAreaIds:" + "*," + tradeAreaId + ",*");
                    } else {
                        buffer.append(" OR tradingAreaIds:" + "*," + tradeAreaId + ",*");
                    }
                    flag++;
                }
                buffer.append(")");
                query.addFilterQuery(buffer.toString());
            }
            if (EmptyUtils.isNotEmpty(vo.getMaxPrice())) {
                query.addFilterQuery("maxPrice:" + "[* TO " + vo.getMaxPrice() + "]");
            }
            if (EmptyUtils.isNotEmpty(vo.getMinPrice())) {
                query.addFilterQuery("minPrice:" + "[" + vo.getMinPrice() + " TO *]");
            }

            if (EmptyUtils.isNotEmpty(vo.getAscSort())) {
                query.addSort(vo.getAscSort(), SolrQuery.ORDER.asc);
            }

            if (EmptyUtils.isNotEmpty(vo.getDescSort())) {
                query.addSort(vo.getDescSort(), SolrQuery.ORDER.desc);
            }
        }
        if (EmptyUtils.isNotEmpty(tempQuery.toString())) {
            query.setQuery(tempQuery.toString());
        }
        Page<ItripHotelVO> page = baseQuery.queryPage(query, pageNo, pageSize, ItripHotelVO.class);
        return page;
    }

    @Override
    public List<ItripHotelVO> queryHotCity(Integer cityId,Integer count) throws Exception {
        SolrQuery query = new SolrQuery("*:*");
        if(EmptyUtils.isNotEmpty(cityId)){
            query.addFilterQuery("cityId:"+cityId);
        }else{
            throw new Exception("城市id不能为空");
        }
        List<ItripHotelVO> list = baseQuery.queryList(query, count, ItripHotelVO.class);
        return list;
    }
}
