package com.blog.service.markdown;

/**
 * Markdown 渲染管线的可插拔后处理环：对渲染后的 HTML 做增强。
 * 实现类注册为 Spring Bean 即自动生效，按 {@link #order()} 升序执行。
 *
 * <p>示例：图片懒加载、外链新窗口、代码块复制按钮、敏感词过滤等。
 */
public interface HtmlPostProcessor {

    /** 处理渲染后的 HTML，返回处理结果 */
    String process(String html);

    /** 执行顺序，数字越小越先执行；默认 0 */
    default int order() {
        return 0;
    }
}
