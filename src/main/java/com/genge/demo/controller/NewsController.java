package com.genge.demo.controller;

import com.genge.demo.model.HostHolder;
import com.genge.demo.model.News;
import com.genge.demo.service.NewsService;
import com.genge.demo.util.TouTiaoUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

@Controller
public class NewsController {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    HostHolder hostHolder;

    /**
     * 上传图片
     * @param file
     * @return
     */
    @RequestMapping(path = {"/uploadImage/"},method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file")MultipartFile file){
        try {
            String fileUrl = newsService.saveImage(file);
            if (fileUrl == null){
                return TouTiaoUtil.getJSONString(1,"上传图片失败");
            }
            return TouTiaoUtil.getJSONString(0,fileUrl);
        }catch (Exception e){
            LOGGER.error("上传图片失败"+e.getMessage());
            return TouTiaoUtil.getJSONString(1,"上传失败");
        }

    }

    /**
     * 用户下载图片
     * @param imageName
     * @param response
     */
    @RequestMapping(value = "/image",method = RequestMethod.GET)
    @ResponseBody
    public void getImage(@RequestParam("name")String imageName,
                         HttpServletResponse response){
        try {
            response.setContentType("image/jpeg");
            StreamUtils.copy(new FileInputStream(new File(TouTiaoUtil.IMAGE_DIR + imageName)),
                    response.getOutputStream());
        }catch (Exception e){
            LOGGER.error("读取图片错误"+e.getMessage());
        }
    }

    /**
     * 用户发布消息
     * @param image
     * @param title
     * @param link
     * @return
     */
    @RequestMapping(path = {"/addNews/"},method = {RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam("image")String image,
                          @RequestParam("title")String title,
                          @RequestParam("link")String link){
        try {
            News news = new News();
            if (hostHolder.getUser() != null){
                news.setUserId(hostHolder.getUser().getId());
            }else {
//                匿名
                news.setUserId(3);
            }
            news.setImage(image);
            news.setCreatedDate(new Date());
            news.setTitle(title);
            news.setLink(link);
            newsService.addNews(news);
            return TouTiaoUtil.getJSONString(0,"成功");
        }catch (Exception e){
            LOGGER.error("用户咨询上传失败"+e.getMessage());
            return TouTiaoUtil.getJSONString(1,"发布失败");
        }
    }
}
