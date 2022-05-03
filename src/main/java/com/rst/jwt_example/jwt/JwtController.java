package com.rst.jwt_example.jwt;

import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.stereotype.Controller;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.io.IOException;

@Controller
public class JwtController {

    private Logger logger = LoggerFactory.getLogger(JwtController.class);
    private JwtService service;

    @Autowired
    public JwtController(JwtService service){
        this.service = service;
    }

    @GetMapping("/")
    public String homePage(){
        return "home";
    }

    @GetMapping("/login")
    public String loginPage(@ModelAttribute LoginForm loginForm, Model model){
        model.addAttribute("loginForm", loginForm);
        return "login";
    }
    /**
     * https://www.baeldung.com/spring-request-response-body
     * request body 어노테이션은 request body json 객체를 특정 객체의 형식으로 만들어 반환해준다.
     *
     * https://stackoverflow.com/questions/34782025/http-post-request-with-content-type-application-x-www-form-urlencoded-not-workin/38252762#38252762
     * spring에서 application/x-www-form-urlencoded 해석 오류
     * @param
     * @return
     */
    @PostMapping("/login")
    public void loginAction(HttpServletRequest req, HttpServletResponse res) throws IOException{
        LoginForm loginForm = new LoginForm(
                req.getParameter("username"),
                req.getParameter("password")
        );

        if(service.loginAction(loginForm)){
            Cookie jwtCookie = new Cookie("jwt"
                    ,service.getUsernameJwt(loginForm.getUsername()).toString());
            res.addCookie(jwtCookie);
            res.sendRedirect("/secret");
        }else{
            res.sendRedirect("/login");
        }
    }

    @GetMapping("/logout")
    public String logoutAction(HttpServletResponse res){
        Cookie jwtCookie = new Cookie("jwt", "");
        jwtCookie.setMaxAge(0);
        res.addCookie(jwtCookie);

        logger.info("logout action");
        return "login";
    }

    @GetMapping("/secret")
    public String secretPage(HttpServletRequest req, Model model){
        Cookie jwtCookie = null;
        for(Cookie ck :req.getCookies()){
            if(ck.getName().equals("jwt")){jwtCookie = ck; break;}
        }

        if(jwtCookie == null){logger.info("로그인하지 않은 시도"); return "login";}

        try{
            String username = new UsernameJwt(jwtCookie.getValue()).parseUsername(service.getSecretKey());
            model.addAttribute("username", username);
            return "secret";
        }catch (JwtException ex){
            logger.info(ex.getMessage());
            return "login";
        }
    }
}
