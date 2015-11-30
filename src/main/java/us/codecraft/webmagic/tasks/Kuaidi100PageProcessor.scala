package us.codecraft.webmagic.tasks

import org.apache.http.HttpHost
import us.codecraft.webmagic.processor.PageProcessor
import us.codecraft.webmagic.selector.JsonPathSelector
import us.codecraft.webmagic.{Page, Site, Spider}
import us.codecraft.xsoup.Xsoup
import scala.collection.JavaConversions._


/**
  * 爬取中国所有城市快地点的信息
  */
object Kuaidi100PageProcessor extends PageProcessor {
  private var site: Site = null
  private val LIST_URL: String = "http://angularjs\\.cn/api/article/latest.*"
 // val dao = HibernateDao.getInstance();

  def process(page: Page) {
    if(page.getRequest.getUrl.equals("http://www.kuaidi100.com/network/clist.shtml")){
      val citys = Xsoup.compile("//dl[@class='dl-list']/dd/a/text()").evaluate(page.getHtml.getDocument).list()
    //  list.toList.foreach(println) //抽取所有城市
      //构建所有城市的全部快递信息

      page.addTargetRequest("http://www.kuaidi100.com/network/www/searchapi.do?" +
        "method=searchnetwork&" +
        "area="+"新疆"+
        "&company=0"+
        "&keyword=" +
        "&offset=0" +
        "&size=8" +
        "&from=null" +
        "&auditStatus=0")

//      citys.toList.foreach(city => {
//        page.addTargetRequest("http://www.kuaidi100.com/network/www/searchapi.do?" +
//          "method=searchnetwork&" +
//          "area="+"江西"+
//          "&company=0"+
//          "&keyword=" +
//          "&offset=0" +
//          "&size=8" +
//          "&from=null" +
//          "&auditStatus=0")
//      })
    }else if(page.getRequest.getUrl.contains("company=0")){ //全部快递信息
      //构建实际抽取的url,这里是返回json数据
      System.out.println(page.getRawText)
      val companyTotal = new JsonPathSelector("$.companyTotal").selectList(page.getRawText) //获取该城市的所有快递相关信息
      companyTotal.toList.foreach(company => {
        val count = new JsonPathSelector("$.count").select(company) //获取网点数目
        val companyNumber = new JsonPathSelector("$.companyNumber").select(company) //获取公司名称
        //构造新的url请求,爬取数据
        if(companyNumber.equals("suer") )
          page.addTargetRequest(page.getRequest.getUrl.replace("company=0","company="+companyNumber).replace("size=8","size="+count))
      })

    }else{
      System.out.println(page.toString)
      val netList = new JsonPathSelector("$.netList").selectList(page.getRawText.replaceAll("(?is)<.*?>",""));
      netList.toList.foreach(content => {
        val id = new JsonPathSelector("$.id").select(content) //唯一标识的id
        val tel = new JsonPathSelector("$.tel").select(content) //电话
        val address = new JsonPathSelector("$.address").select(content) //地址
        val xzqFullName = new JsonPathSelector("$.xzqFullName").select(content) //行政区
        val companyName = new JsonPathSelector("$.companyName").select(content) //公司名称
        val detailText = new JsonPathSelector("$.detailText").select(content) //详细信息

//        val kuadi = new Kuaidi();
//        kuadi.setId(id+xzqFullName+companyName);
//        kuadi.setAddress(address);
//        kuadi.setCompanyName(companyName);
//        kuadi.setDetailText(detailText);
//        kuadi.setTel(tel);
//        kuadi.setXzqFullName(xzqFullName)
//
//        dao.insert(kuadi);

        //println(address+ " "+tel)
      })
//      page.putField("title", new JsonPathSelector("$.data.title").select(page.getRawText))
//      page.putField("content", new JsonPathSelector("$.data.content").select(page.getRawText))
    }


  }

  def getSite: Site = {
    if (null == site) {
      //设置超时3秒
      site = Site.me.setDomain("www.kuaidi100.com").setSleepTime(3).
        setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    }
    return site
  }

  def main(args: Array[String]) {
    Spider.create(Kuaidi100PageProcessor)
      .thread(1)
      .addUrl("https://www.baidu.com/")
      .run
  }

}
