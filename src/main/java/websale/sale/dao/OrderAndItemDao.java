package websale.sale.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import websale.sale.model.OrderAndItem;

import java.util.List;

@Mapper
public interface OrderAndItemDao {

    /*
    TODO
     */
    int insertOrderAndItems(List<OrderAndItem> orderAndItem);

    @Insert("insert into orderanditem (orderid,itemid,number) values (#{orderId},#{itemId},#{number})")
    int insertOrderAndItem(OrderAndItem orderAndItem);

    @Select("select itemid,number from orderanditem where orderid=#{orderId}")
    List<OrderAndItem> selectOederAndItems(int orderId);

    @Delete("delete from orderanditem where itemid=#{id}")
    void deleteItem(int id);
}