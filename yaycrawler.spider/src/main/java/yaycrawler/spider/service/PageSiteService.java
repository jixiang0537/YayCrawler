package yaycrawler.spider.service;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Site;
import yaycrawler.dao.domain.PageSite;
import yaycrawler.dao.domain.SiteCookie;
import yaycrawler.dao.repositories.PageSiteRepository;

import java.util.Map;

/**
 * Created by yuananyun on 2016/5/2.
 */
@Service
public class PageSiteService {
    @Autowired
    private PageSiteRepository siteRepository;

    public Site getSite(String domain) {
        return getSite(domain, false);
    }

    /**
     * 获取Site
     *
     * @param domain
     * @param needProxy 是否需要切换代理
     * @return
     */
    public Site getSite(String domain, boolean needProxy) {
        Site site = Site.me();
        PageSite pageSite = siteRepository.findByDomain(domain);
        if (pageSite != null) {
            site.setDomain(pageSite.getDomain());
            site.setCharset(pageSite.getCharset());
            site.setCycleRetryTimes(pageSite.getCycleRetryTimes());
            site.setRetryTimes(pageSite.getRetryTimes());
            site.setSleepTime((int) pageSite.getSleepTime());
            site.setUserAgent(pageSite.getUserAgent());
            site.setUseGzip(true);

            if (pageSite.getHeaders() != null) {
                Map<String, String> headMap = JSON.parseObject(pageSite.getHeaders(), Map.class);
                for (Map.Entry<String, String> entry : headMap.entrySet()) {
                    site.addHeader(entry.getKey(), entry.getValue());
                }
            }
            if (pageSite.getDefaultCookies() != null) {
                Map<String, String> cookiesMap = JSON.parseObject(pageSite.getDefaultCookies(), Map.class);
                for (Map.Entry<String, String> entry : cookiesMap.entrySet()) {
                    site.addCookie(domain, entry.getKey(), entry.getValue());
                }
            }
            //只设置一个有效Cookie即可
            if (pageSite.getCookieList() != null)
                for (SiteCookie cookie : pageSite.getCookieList())
                    if ("1".equals(cookie.getAvailable())) {
                        site.addHeader("Cookie", cookie.getCookie());
                        break;
                    }
        }
        return site;
    }
}
