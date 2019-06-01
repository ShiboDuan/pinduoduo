package websale.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import websale.sale.biz.ClientLoginBiz;
import websale.sale.model.CartItem;
import websale.sale.model.Client;
import websale.sale.model.Item;
import websale.sale.service.CartService;
import websale.sale.service.ClientService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ClientController {
    @Autowired
    private ClientLoginBiz clientLoginBiz;
    @Autowired
    private ClientService clientService;
    @Autowired
    private CartService cartService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String index(){

        return "index";
    }

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String register(){
        return "register";
    }
    
    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String doRegister(Client client){
        clientService.addClient(client);
        return "login";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String login(){
        return "login";
    }

    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("password") String password,
            Model model,
            HttpServletRequest request
    ){
        try{
            int clientId= clientLoginBiz.login(phoneNumber,password);
            request.getSession().setAttribute("id",clientId);
        }catch (Exception e){
            model.addAttribute("error",e.getMessage());
            return "404";
        }
        return "redirect:/index";
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(HttpServletRequest request){
        
        request.getSession().removeAttribute("id");
        return "index";
    }

    //当点击商品时，进入单品显示
    @RequestMapping(path = "/item/{id}",method = RequestMethod.GET)
    public String getItem(
            @PathVariable("id") int id,
            Model model){
        model.addAttribute("item",clientService.getItem(id));
        return "item";
    }

    //在主页或者商店添加商品到购物车
    @RequestMapping(path = "/cart/additem",method = RequestMethod.POST)
    public String addItemToCart(
            @RequestParam("id") int id,
            HttpServletRequest request
    ){
        int clientId=(Integer) request.getSession().getAttribute("id");
        CartItem cartItem=new CartItem();
        cartItem.setClientId(clientId);
        cartItem.setItemId(id);
        cartItem.setNumber(1);
        cartService.addCartItem(cartItem);
        return "200";
    }

    //在购物车界面增加商品数量
    @RequestMapping(path = "/cart/increase")
    public String increaseItemToCart(
            @RequestParam("id") int id,
            @RequestParam("number") int number,
            HttpServletRequest request
    ){
        int clientId=(Integer) request.getSession().getAttribute("id");
        cartService.updateItem(clientId,id,number);
        return "200";
    }

    //用户进入购物车
    @RequestMapping(path = "/cart",method = RequestMethod.GET)
    public String getCart(
            Model model,
            HttpServletRequest request
    ){
        int clientId=(Integer) request.getSession().getAttribute("id");
        //查询商品
        List<Item> items=cartService.getItems(clientId);
        return "cart";
    }
}
