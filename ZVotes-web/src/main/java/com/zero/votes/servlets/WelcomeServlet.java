package com.zero.votes.servlets;

//import com.zero.votes.cronjobs.TaskManager;
import com.zero.votes.cronjobs.TaskManager;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WelcomeServlet extends HttpServlet {
    
    @EJB
    private TaskManager taskManager;
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        taskManager.doTask();
        RequestDispatcher request_dispatcher = req.getRequestDispatcher("/index.xhtml");
        request_dispatcher.forward(req, resp);
    }
}
