package com.blog.service;

import com.blog.service.markdown.HtmlPostProcessor;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * 基于 commonmark-java 的 Markdown 渲染管线实现。
 * 当前启用 GFM 表格 + 标题锚点扩展，渲染完成后跑一遍注入的 HtmlPostProcessor 链。
 */
@Service
public class CommonMarkdownService implements MarkdownService {

    private final FileStorageService fileStorageService;
    private final List<HtmlPostProcessor> postProcessors;
    private final Parser parser;
    private final HtmlRenderer renderer;

    public CommonMarkdownService(FileStorageService fileStorageService,
                                 List<HtmlPostProcessor> postProcessors) {
        this.fileStorageService = fileStorageService;
        // 按 order 排序，保证处理链顺序确定
        this.postProcessors = postProcessors.stream()
                .sorted(Comparator.comparingInt(HtmlPostProcessor::order))
                .toList();
        List<Extension> extensions = List.of(
                TablesExtension.create(),
                HeadingAnchorExtension.create());
        this.parser = Parser.builder().extensions(extensions).build();
        this.renderer = HtmlRenderer.builder().extensions(extensions).build();
    }

    @Override
    public String read(Long articleId) {
        return fileStorageService.readMarkdown(articleId);
    }

    @Override
    public String render(Long articleId) {
        String markdown = fileStorageService.readMarkdown(articleId);
        if (markdown == null || markdown.isBlank()) {
            return "";
        }
        Node document = parser.parse(markdown);
        String html = renderer.render(document);
        for (HtmlPostProcessor processor : postProcessors) {
            html = processor.process(html);
        }
        return html;
    }
}
