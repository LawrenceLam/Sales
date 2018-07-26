package com.platform.sales.surface;

import com.platform.sales.entity.BrandRepos;
import com.platform.sales.entity.OrderInfo;

import java.util.List;

public interface BrandOrderService {

    List<OrderInfo> findByGoods(BrandRepos goods);

    List<OrderInfo> findByStatusAndGoods(String status, BrandRepos goods);
}