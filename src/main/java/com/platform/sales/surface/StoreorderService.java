package com.platform.sales.surface;

import java.util.List;

public interface StoreorderService {
    //根据借卖方的ID查找所有订单
    public List<StoreOrder> findAllBySeller_UserId(Integer id);
}
