package websale.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import websale.sale.biz.ClientLoginBiz;
import websale.sale.model.CartItem;
import websale.sale.model.Client;
import websale.sale.model.Item;
import websale.sale.service.CartService;
import websale.sale.service.ClientService;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

@Controller
public class ClientController {
    @Autowired
    private ClientLoginBiz clientLoginBiz;
    @Autowired
    private ClientService clientService;
    @Autowired
    private CartService cartService;

    @RequestMapping(path = "/index/{start}", method = RequestMethod.GET)
    public String index(
            @PathVariable("start") int start,
            Model model){
        List<Item> items=clientService.getItems(start);
        model.addAttribute("items",items);
        return "index";
    }

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String register(){
        return "register";
    }
    
    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String doRegister(Client client,Model model){
        int clientid=clientService.addClient(client);
        model.addAttribute("clientId",clientid);
        return "login";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String login(HttpServletRequest request) throws FileNotFoundException {
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
        Map<Item,Integer> items=cartService.getItems(clientId);
        model.addAttribute("itemmap",items);
        return "cart";
    }
}
