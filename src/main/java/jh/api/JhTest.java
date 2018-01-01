package jh.api;

import com.google.gson.Gson;
import org.glassfish.jersey.server.model.Suspendable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class JhTest extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getHeader("code"));
        System.out.println(req.getParameter("code"));
        System.out.println(req.getParameter("msg"));
        System.out.println(req.getParameter("out_trade_no"));
        System.out.println(req.getParameter("total"));
        System.out.println(req.getParameter("fee"));
        System.out.println(req.getParameter("trade_type"));
        System.out.println(req.getParameter("sign_type"));
        System.out.println(req.getParameter("sign"));

        Map<String,String[]> map = req.getParameterMap();
        for(String key:map.keySet()) {
            System.out.println(new Gson().toJson(map.get(key)));
        }
        resp.getWriter().write("SUCCESS");
    }
}
