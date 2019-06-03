package websale.sale.dao;

import org.apache.ibatis.annotations.*;
import websale.sale.model.Manager;

@Mapper
public interface ManagerDao {

    @Insert("insert into manager(username,phoneNumber,password) values(#{username},#{phoneNumber},#{password})")
    @SelectKey(keyProperty = "id",resultType = int.class,before = false,statement = "SELECT LAST_INSERT_ID()")
    int insertManager(Manager manager);

    @Update("update manager set password=#{password} where phonenumber=#{phonebnumber}")
    void updateManagerPassword(String phoneNumber,String password);

    @Update("update manager set phonenumber=#{newphonenumber} where phonenumber=#{phonenumber}")
    void updateManagerPhoneNumber(String phoneNumber, String newPhoneNumber);

    @Select("select * from manager where phonenumber=#{phoneNumber}")
    Manager selectManager(String phoneNumber);
}
