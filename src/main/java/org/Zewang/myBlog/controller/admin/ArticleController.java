package org.Zewang.myBlog.controller.admin;


import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.Zewang.myBlog.dto.CreateArticleDTO;
import org.Zewang.myBlog.model.Article;
import org.Zewang.myBlog.service.article.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 文章的控制器，处理以/article开头的请求
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/21 23:15
 */

@Controller
@RequestMapping("/article") // 表示这个控制器处理的请求路径，所有以/article 开头的请求都会由它处理
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService; // 注入ArticleService

    /**
     * 显示文章列表
     * @param model 模型
     * @return 文章列表
     */
    @GetMapping("/list") // 表示这个方法处理GET请求
    public String list(Model model) { // 返回list.html页面;model 用于传递数据给页面
        List<Article> articles = articleService.getAllArticles();
        model.addAttribute("articles", articles); // 将文章列表添加到模型中
        return "admin/list";
    }

    /**
     *  参数： @PathVariable("id")：从 URL 中提取 {id} 的值
     *  Model model：把文章数据传给前端
     *  return "article/detail"：找 templates/article/detail.html
     */

    @GetMapping("/{id}")
    public String viewArticle(@PathVariable("id") Long id, Model model) {
        System.out.println("请求查看文章 ID: " + id);
        return articleService.getById(id)
            .map(article -> {
                System.out.println("找到文章: " + article.getTitle());
                model.addAttribute("article", article);
                System.out.println("返回视图: admin/article/detail");
                return "admin/article/detail";
            })
            .orElseGet(() -> {
                System.out.println("文章未找到，返回错误页面");
                model.addAttribute("msg", "文章不存在或已被删除");
                return "admin/article/not_exists_error";
            });
    }


    /**
     * 显示新建文章的表单
     * @param model 模型
     * @return 新建文章的表单
     */
    @GetMapping("/new")
    public String showNewArticleForm(Model model) { // 通过model传递给前端
        model.addAttribute("article", new CreateArticleDTO("","",""));
        model.addAttribute("isEdit", false);
        return "admin/article/form";
    }

    /**
     * 创建文章
     * +  @ModelAttribute：把表单字段绑定到 CreateArticleDTO，从前端接收数据
     * +  RedirectAttributes：用于重定向时传消息（闪现消息）
     * +  重定向避免重复提交（Post-Redirect-Get 模式）
     * @param dto 创建文章的参数
     * @param redirectAttributes 重定向属性
     * @return 重定向到文章列表页
     */

    @PostMapping("/new")
    public String createArticle(
        @Valid @ModelAttribute("article") CreateArticleDTO dto, // @Valid: 数据验证, @ModelAttribute：把表单字段绑定到 CreateArticleDTO，从前端接收数据
        BindingResult bindingResult, // BindingResult：数据验证结果
        RedirectAttributes redirectAttributes // RedirectAttributes：用于重定向时传消息（闪现消息）
        ) {

        if (bindingResult.hasErrors()) { // 如果有错误，返回表单页
            redirectAttributes.addFlashAttribute("error", "标题不能为空");
            return "redirect:article/new"; // 重定向到新建文章的表单
        }

        try {
            articleService.createArticle(dto);
            redirectAttributes.addFlashAttribute("success", "发布成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "发布失败" + e.getMessage());
            return "redirect:/article/new"; // 重定向到新建文章的表单
        }

        return "redirect:/article/list"; // 成功后重定向到文章列表页
    }

    @GetMapping("edit/{id}")
    public String showEditArticleForm(@PathVariable("id") Long id, Model model) {
        return articleService.getById(id)
            .map(article -> {
                CreateArticleDTO dto = new CreateArticleDTO(
                    article.getTitle(),
                    article.getContent(),
                    article.getAuthor()
                );

                model.addAttribute("article", dto);
                model.addAttribute("isEdit", true);
                model.addAttribute("articleId", id);
                return "admin/article/form";
            })
            .orElseGet(() -> {
                model.addAttribute("msg", "文章不存在或已被删除");
                return "admin/article/not_exists_error";
            });
    }

    @PostMapping("/edit/{id}")
    public String updateArticle(
        @PathVariable("id") Long id,
        @Valid @ModelAttribute("article") CreateArticleDTO dto,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "标题不能为空");
            return "redirect:/article/edit/" + id;
        }

        try {
            articleService.updateArticle(id, dto);
            redirectAttributes.addFlashAttribute("success", "更新成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "更新失败" + e.getMessage());
            return "redirect:/article/edit/" + id;
        }

        return "redirect:/article/" + id;
    }

    @PostMapping("/delete/{id}")
    public String deleteArticle(
        @PathVariable("id") Long id,
        RedirectAttributes redirectAttributes
    ) {
        try {
            articleService.deleteArticle(id);
            redirectAttributes.addFlashAttribute("success", "删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除失败" + e.getMessage());
        }

        return "redirect:/article/list";
    }
}


