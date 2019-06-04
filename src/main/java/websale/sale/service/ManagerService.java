package websale.sale.service;

import org.springframework.stereotype.Service;
import websale.sale.dao.ItemDao;
import websale.sale.dao.ManagerDao;
import websale.sale.dao.StoreAndItemDao;
import websale.sale.dao.StoreDao;
import websale.sale.model.Item;
import websale.sale.model.Manager;
import websale.sale.model.Store;
import websale.sale.model.StoreAndItem;
import websale.sale.utils.MD5;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ManagerService {
    @Resource
    ManagerDao managerDao;

    @Resource
    ItemDao itemDao;

    @Resource
    StoreAndItemDao storeAndItemDao;
    @Resource
    StoreDao storeDao;

    public List<Item> getItems(int storeId){
        return itemDao.selectItemsByStoreId(storeId);
    }

    public int createStore(Store store){
        return storeDao.insertStore(store);
    }

    public int createItem(int storeId,Item item,int number){
        item.setInventory(number);
        int clientId=itemDao.insertItem(item);
        item.setId(clientId);
        StoreAndItem storeAndItem=new StoreAndItem();
        storeAndItem.setItemId(clientId);
        storeAndItem.setStoreId(storeId);
        storeAndItemDao.insertStoreAndItem(storeAndItem);
        return clientId;
    }

    public void addItem(int itemId,int number){
        itemDao.updateItemInventory(itemId,number);
    }

    public int addManager(Manager manager){

        manager.setPassword(MD5.next(manager.getPassword()));
        return managerDao.insertManager(manager);
    }

    public Manager getManager(String phoneNumber){
        return managerDao.selectManager(phoneNumber);
    }


    public void updateManagerPassword(String phoneNumber,String password){
        managerDao.updateManagerPassword(phoneNumber,password);
    }

    public void updateManagerPhoneNumber(String phoneNumber, String newPhoneNumber){
        managerDao.updateManagerPhoneNumber(phoneNumber,newPhoneNumber);
    }
}
