package com.blog.service.markdown;

import org.springframework.stereotype.Component;

/**
 * 外链新窗口：正文中指向站外的 <a>（http/https 开头）自动补
 * target="_blank" rel="noopener nofollow"，防止当前页跳走并规避 tabnabbing。
 * 站内链接（/uploads、# 锚点等）不受影响。
 */
@Component
public class ExternalLinkProcessor implements HtmlPostProcessor {

    @Override
    public String process(String html) {
        if (html == null || !html.contains("<a ")) {
            return html;
        }
        // 匹配 <a href="http(s)://...">，插入 target/rel；已带 target 的不重复加
        return html.replaceAll(
                "(?i)<a(?![^>]*\\btarget=)([^>]*\\bhref=\"(https?://)[^\"]*\")",
                "<a target=\"_blank\" rel=\"noopener nofollow\"$1");
    }
}
