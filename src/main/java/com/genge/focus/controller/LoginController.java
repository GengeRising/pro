package com.genge.focus.controller;

import com.genge.focus.async.EventModel;
import com.genge.focus.async.EventProducer;
import com.genge.focus.async.EventType;
import com.genge.focus.dao.LoginTicketDAO;
import com.genge.focus.service.UserService;
import com.genge.focus.util.TouTiaoUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@Controller
public class LoginController {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    LoginTicketDAO loginTicketDAO;
    /**
     * 注册
     * @param model
     * @param username
     * @param password
     * @param remember
     * @return
     */
    @RequestMapping(path = "/reg/",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String reg(Model model, @RequestParam("username") String username, @RequestParam("password")String password,
                      @RequestParam(value = "rember",defaultValue="0") int remember,
                      HttpServletResponse response){

        try {
            Map<String,Object> map = userService.register(username,password);
//            注册成功后会写入ticket
            if (map.containsKey("ticket")){
//              将ticket写入浏览器cookie
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
//              全路径有效
                cookie.setPath("/");
//              若设置记得，则加长有效期
                if (remember > 0){
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                response.addCookie(cookie);
                return TouTiaoUtil.getJSONString(0,"注册成功");
            }else {
                return TouTiaoUtil.getJSONString(1,map);
            }
        }catch (Exception e){
            LOGGER.error("注册异常"+e.getMessage());
            return TouTiaoUtil.getJSONString(1,"注册异常");
        }
    }


    /**
     * 登陆
     * @param model
     * @param username
     * @param password
     * @param remember
     * @return
     */
    @RequestMapping(path = "/login/",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String login(Model model, @RequestParam("username") String username,@RequestParam("password")String password,
                      @RequestParam(value = "rember",defaultValue="0") int remember,
                        HttpServletResponse response){

        try {
            Map<String,Object> map = userService.login(username,password);
            if (map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                System.out.println("ticket="+map.get("ticket").toString());
//              全路径有效
                cookie.setPath("/");
//              若设置记得，则加长有效期
                if (remember > 0){
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                response.addCookie(cookie);
                eventProducer.fireEvent(new EventModel(EventType.LOGIN).setActorId(loginTicketDAO.selectByTicket(map.get("ticket").toString()).getUserId()));
                return TouTiaoUtil.getJSONString(0,"登陆成功");
            }else {
                return TouTiaoUtil.getJSONString(1,map);
            }
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("登陆异常"+e.getMessage());
            return TouTiaoUtil.getJSONString(1,"登陆异常");
        }
    }

    /**
     * 登出
     * @param ticket
     * @return
     */
    @RequestMapping(path = "/logout/",method = {RequestMethod.GET,RequestMethod.POST})
    public String logout(@CookieValue("ticket")String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }
}
