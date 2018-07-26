package com.platform.sales.controller;

import com.platform.sales.entity.Account;
import com.platform.sales.entity.Record;
import com.platform.sales.entity.SellerInfo;
import com.platform.sales.entity.Users;
import com.platform.sales.surface.BrandAccountService;
import com.platform.sales.surface.BrandRecordService;
import com.platform.sales.surface.SellerinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Controller
@RequestMapping("seller")
public class SellerinfoController {
    @Autowired
    SellerinfoService sellerinfoService;
    //账户服务
    @Autowired
    BrandAccountService accountService;
    //流水服务
    @Autowired
    BrandRecordService recordService ;
    //需要将借卖方注册时的user_id添加到页面属性中作为此方法的参数
    @GetMapping("/getInfo/{seller_id}")
    public String getInfo(@PathVariable("seller_id")Integer id, Model model){
        List<SellerInfo> sellers = sellerinfoService.findById(id);//根据表数据中的外键查询结果
        SellerInfo seller_info;
        if(sellers.size()==0){//查询结果为null，说明借卖方的信息还未初始化，所有像都为空
            seller_info = new SellerInfo();
            model.addAttribute("id",-1);//将id的值赋予-1写到前端属性中，方便判断接下来提交的修改操作是添加新项还是修改已有项
            seller_info.setMail("");
            seller_info.setUserName("");
            seller_info.setPhone("");
            Users user = new Users();
            user.setUserId(id);
            seller_info.setUser(user);
        }else {
            seller_info = sellers.get(0);
            model.addAttribute("id",seller_info.getSellerId());//查询到结果则将结果填到页面中去
        }
        model.addAttribute("sellerinfo",seller_info);
        return "seller/info";
    }
    //将输入的修改数据更新到数据库里面
    @PostMapping("/getInfo/update")
    public String updateMessage(SellerInfo seller_info){
        if(seller_info.getSellerId()==-1)seller_info.setUser(sellerinfoService.getUser(seller_info.getUser().getUserId()).get(0));
        sellerinfoService.updateSeller(seller_info);
        return "redirect:/seller/getInfo/"+seller_info.getUser().getUserId();
    }
    //借卖方的钱包查看
    @GetMapping("/sellerAccount")
    public String sellerAccount(Model model,HttpSession session){
        //根据session的内容判断钱包是否已经初始化
        Users user = (Users)session.getAttribute("user");
        Account account = accountService.findByUserId(user.getUserId());
        if(account == null){
            Account newAccount = new Account();
            newAccount.setBalance(new Float(0));
            newAccount.setPayPwd("");
            newAccount.setUser(user);
            account = accountService.update(newAccount);
        }
        if(account.getPayPwd().equals("") && account.getBalance() == 0){
            model.addAttribute("account",account);
            return "/seller/newAccount";
        }else{
            model.addAttribute("selleraccount",account);
            return "/seller/sellerAccount";
        }
    }
    @PostMapping("/sellerAccount")
    public String updateAccount(Account account, Model model){
        Account newaccount = accountService.update(account);
        model.addAttribute("selleraccount",newaccount);
        return "/seller/sellerAccount";
    }
    @GetMapping("/withdraw")
    //访问提现页面
    public String getWithdraw(Model model,HttpSession session){
        //查找账户的信息
        Users user = (Users)session.getAttribute("user");
        Account account = accountService.findByUserId(user.getUserId());
        model.addAttribute("account",account);
        return "/seller/withdraw";
    }
    @PostMapping("/withdraw")
    public String doWithdraw(Account account,Model model,HttpSession session){
        Account acnt = accountService.findByUserId(account.getUser().getUserId());
        if(acnt.getPayPwd().equals(account.getPayPwd())){
            if(acnt.getBalance() >= account.getBalance() && account.getBalance() >= 0){
                if(account.getBalance() > 0){
                    //创建流水单
                    Record record = new Record();
                    record.setUsers(acnt.getUser());
                    record.setOp(acnt.getUser());
                    record.setMoney(account.getBalance());
                    record.setTime(new Date());
                    record.setStatus("待审核");
                    record.setType("提现");
                    recordService.create(record);
                    return "redirect:/seller/sellerAccount";
                }else{
                    model.addAttribute("error","提现金额必须大于0！");
                    return "/seller/withdraw";
                }
            }else{
                model.addAttribute("error","余额不足！");
                return "/seller/withdraw";
            }
        }else {
            if(account.getPayPwd().equals(""))
                model.addAttribute("error","密码不能为空！");
            else
                model.addAttribute("error","密码错误！");
            return "/seller/withdraw";
        }
    }
    @GetMapping("/recharge")
    //访问充值页面
    public String getRecharge(Model model,HttpSession session){
        //查找账户的信息
        Users user = (Users)session.getAttribute("user");
        Account account = accountService.findByUserId(user.getUserId());
        model.addAttribute("account",account);
        return "/seller/recharge";
    }
    @PostMapping("/recharge")
    public String doRecharge(Account account,Model model,HttpSession session){
        Account acnt = accountService.findByUserId(account.getUser().getUserId());
        if(acnt.getPayPwd().equals(account.getPayPwd())){
                if(account.getBalance() > 0){
                    //创建流水单
                    Record record = new Record();
                    record.setUsers(acnt.getUser());
                    record.setOp(acnt.getUser());
                    record.setMoney(account.getBalance());
                    record.setTime(new Date());
                    record.setStatus("待审核");
                    record.setType("充值");
                    recordService.create(record);
                    return "redirect:/seller/sellerAccount";
                }else{
                    model.addAttribute("error","充值金额必须大于0！");
                    return "/seller/recharge";
                }
        }else {
            if(account.getPayPwd().equals(""))
                model.addAttribute("error","密码不能为空！");
            else
                model.addAttribute("error","密码错误！");
            return "/seller/recharge";
        }
    }
    //流水表
    @GetMapping("/record")
    public String withdrawRecord(HttpSession session, Model model){
        Users users = (Users) session.getAttribute("user");
        List<Record> records = recordService.findAllByUser_UserId(users.getUserId());
        if(records.isEmpty())
            model.addAttribute("empty","无");
        model.addAttribute("id", users.getUserId());
        model.addAttribute("records", records);
        return "/seller/record";
    }
}
