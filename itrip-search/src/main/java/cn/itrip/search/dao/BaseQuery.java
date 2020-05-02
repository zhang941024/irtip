package cn.itrip.search.dao;

import cn.itrip.common.Constants;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.common.PropertiesUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
@Component
public class BaseQuery {
    private HttpSolrClient client;
    String url = PropertiesUtils.get("database.properties","baseurl");
    public BaseQuery(){
        client = new HttpSolrClient.Builder(url).withConnectionTimeout(10000).withSocketTimeout(60000).build();
        System.out.println(client);
    }

    public Page queryPage(SolrQuery query,Integer pageNo,Integer pageSize,Class clz) throws IOException, SolrServerException {
        //每页显示几条   空 10  非空
        int rows = EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        //当前页码  空 1
        int currPage = EmptyUtils.isEmpty(pageNo) ? Constants.DEFAULT_PAGE_NO : pageNo;
        //从第几条查
        int start = (currPage - 1)*rows;
        query.setRows(rows);
        query.setStart(start);
        //solr查询
        QueryResponse response = client.query(query);
        //获取数据
        List list = response.getBeans(clz);
        //获取总条数
        SolrDocumentList results = response.getResults();
        long total = results.getNumFound();
        //组装page
        Page page = new Page(currPage,rows,(int)total);
        page.setRows(list);
        return page;
    }

    public List queryList(SolrQuery query,Integer pageSize,Class clz) throws IOException, SolrServerException {
        int rows = EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        query.setStart(0);
        query.setRows(rows);
        QueryResponse response = client.query(query);
        List list = response.getBeans(clz);
        return list;
    }
}
