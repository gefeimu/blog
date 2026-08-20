package com.blog.service;

/**
 * Markdown 渲染管线门面。
 *
 * <p>渲染链路：读取 markdown 文件 → 解析渲染为 HTML → 依次经过 {@link com.blog.service.markdown.HtmlPostProcessor}
 * 后处理链（图片懒加载 / 外链新窗口 / 代码高亮等，可插拔）。
 * 以后新增能力（代码高亮、TOC、KaTeX、阅读时长）只需：
 * 1. 注册一个 HtmlPostProcessor（文本层面）或
 * 2. 在实现类中扩展 commonmark 的 Extension（语法层面）。
 */
public interface MarkdownService {

    /** 读取正文 markdown 原文（编辑/预览用，不做任何处理） */
    String read(Long articleId);

    /** 渲染正文：markdown → HTML → 后处理链（前台展示用） */
    String render(Long articleId);
}
