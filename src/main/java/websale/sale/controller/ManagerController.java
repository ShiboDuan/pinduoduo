package websale.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import websale.sale.biz.ManagerLoginBiz;
import websale.sale.model.Item;
import websale.sale.model.Manager;
import websale.sale.model.Store;
import websale.sale.service.ManagerService;
import websale.sale.utils.BasePhotoUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
public class ManagerController {
    @Resource
    ManagerService managerService;

    @Autowired
    ManagerLoginBiz managerLoginBiz;

    @RequestMapping(path = "/manager/login",method = RequestMethod.GET)
    public String login(){
        return "login";
    }

    @RequestMapping(path = "/manager/login",method = RequestMethod.POST)
    public String doLogin(
            @RequestParam String phoneNumber,
            @RequestParam String password,
            Model model,
            HttpServletRequest request
    ){
        try{
            Manager manager= managerLoginBiz.login(phoneNumber,password);
            Integer storeid=managerService.getStoreId(manager.getId());
            if (storeid==null){
                request.setAttribute("storeid",0);
            }else {
                request.getSession().setAttribute("storeid",storeid);
            }
            request.getSession().setAttribute("mid",manager.getId());
            request.getSession().setAttribute("username",manager.getUserName());
        }catch (Exception e){
            model.addAttribute("error",e.getMessage());
            return "/store";
        }
        return "redirect:/index/0";
    }

    @RequestMapping(path = "/manager/register",method = RequestMethod.GET)
    public String register(){
        return "register";
    }

    @RequestMapping(path = "/manager/register",method = RequestMethod.POST)
    public String doRegister(Manager manager){
        managerService.addManager(manager);
        return "redirect:/login";
    }

    @RequestMapping(path = "/manager/logout")
    public String logout(HttpServletRequest request){
        request.getSession().removeAttribute("mid");
        request.getSession().removeAttribute("username");
        request.getSession().removeAttribute("storeid");
        return "index";
    }

    @RequestMapping(path = "/create/item",method = RequestMethod.POST)
    public String createItem(
            @RequestParam("file") MultipartFile file,
            @RequestParam("number")  int number,
            Item item,
            HttpServletRequest request
    ){

        int storeId=(Integer) request.getSession().getAttribute("storeid");
        try {
            item.setPhoto(file.getBytes());
        }catch (IOException e){
            e.printStackTrace();
        }
        //String path=ImageUtils.saveFile(file,request);
        //item.setImagePath(path);
        managerService.createItem(storeId,item,number);
        return "redirect:/index/0";
    }

    @RequestMapping(path = "/create/item" ,method = RequestMethod.GET)
    public String getItemPage(){
        return "item_post_test";
    }

    @RequestMapping(path = "/create/store",method = RequestMethod.POST)
    public String createStore(
            Store store,
            HttpServletRequest request,
            Model model
    ){
        int mid=(Integer) request.getSession().getAttribute("mid");
        int storeId=managerService.createStore(mid,store);
        store.setId(storeId);
        model.addAttribute("storeid",storeId);
        return "redirect:/store";
    }

    @RequestMapping(path = "/create/store",method = RequestMethod.GET)
    public String getCreateStore(
    ){
        return "store_post_test";
    }

    @RequestMapping(path ="/store")
    public String getStore(
            HttpServletRequest request,
            Model model
    ){
        int managerId=(Integer) request.getSession().getAttribute("mid");
        List<Item> items=managerService.getItems(managerId);
        List<String> strings=BasePhotoUtil.encodes(items);
        int storeid=(Integer) request.getSession().getAttribute("storeid");
        model.addAttribute("store",managerService.getStore(storeid));
        model.addAttribute("photos",strings);
        model.addAttribute("items",items);
        return "store";
    }

    @RequestMapping(path = "/store/stock")
    @ResponseBody
    public int addItemInventory(
            @RequestParam("itemid") int itemId,
            @RequestParam("number") int number
    ){
        managerService.addItem(itemId,number);
        return number;
    }
}