package com.blog.service.markdown;

import org.springframework.stereotype.Component;

/**
 * 图片懒加载：为渲染后的 <img> 补充 loading="lazy" decoding="async"。
 * 正文图片较多时首屏只加载可视区图片，提升页面加载速度。
 */
@Component
public class LazyImageProcessor implements HtmlPostProcessor {

    private static final String IMG_TAG = "<img";

    @Override
    public String process(String html) {
        if (html == null || !html.contains(IMG_TAG)) {
            return html;
        }
        // 已带 loading 属性的 img 跳过，其余补 loading="lazy" decoding="async"
        return html.replaceAll("(?i)<img(?![^>]*\\bloading=)",
                "<img loading=\"lazy\" decoding=\"async\"");
    }
}
