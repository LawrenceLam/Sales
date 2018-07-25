package com.platform.sales.controller;

import com.platform.sales.entity.BrandRepos;
import com.platform.sales.entity.Type;
import com.platform.sales.entity.Users;
import com.platform.sales.repository.BrandReposRepository;
import com.platform.sales.repository.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("brand")
public class BrandreposController {

        @Autowired
        BrandReposRepository brandReposRepository;
        @Autowired
        TypeRepository typeRepository;
        @GetMapping("/index")
        public String index(Model model) {
                List<BrandRepos> repository =  brandReposRepository.findAll();
                model.addAttribute("ListsFirst",  repository);
                return "brand/index";
        }

        @PostMapping("/index")
        public String index(String keyword, Model model) {
                List<BrandRepos> repository =  brandReposRepository.findBrand_reposByGoodName(keyword);
                model.addAttribute("Lists",  repository);
                model.addAttribute("Message", "关键字搜索");
                return "brand/index";
        }

        @GetMapping("/newgoods")
        public String newgoods(){
                return "brand/newgoods";
        }

        @PostMapping("/addgoods")
        public String addgoods(BrandRepos brand_repos){
                Users brand = new Users();
                brand.setUserId(1);
                brand_repos.setBrand(brand);            //default user_id
                brand_repos.setStatus("新入仓");       //default status

                Type type = typeRepository.findById(brand_repos.getType().getTypeId()).get();
                brand_repos.setType(type);

                brandReposRepository.save(brand_repos);
                return "redirect:/brand/index";
        }

        @GetMapping("/delgoods/{id}")
        public String delgoods(@PathVariable("id") Integer id){
                brandReposRepository.deleteById(id);
                return "redirect:/brand/index";
        }

        @GetMapping("/uptgoods/{id}")
        public String uptgoods(@PathVariable("id") Integer id, Model model){
                BrandRepos good = brandReposRepository.findById(id).get();
                model.addAttribute("good", good);
                return "brand/update";
        }

        @PostMapping("/doupdate/{id}")
        public String doupdate(@PathVariable("id") Integer id, BrandRepos good){
                good.setGoodId(id);

                Type type = typeRepository.findById(good.getType().getTypeId()).get();
                good.setType(type);

                brandReposRepository.save(good);
                return "redirect:/brand/index";
        }

        @GetMapping("/mainframe")
        public String mainframe(Model model) {
                List<BrandRepos> Lists = brandReposRepository.findBrand_reposByStatusNot("新入仓");
                model.addAttribute("Lists", Lists);
                return "brand/mainframe";
        }
        @PostMapping("/mainframe")
        public String mainframe(String keyword, Model model) {
                //find out all goods that is not newly added tot the repository
                List<BrandRepos> Lists = brandReposRepository.findBrand_reposByGoodNameAndStatusNot(keyword, "新入仓");
                model.addAttribute("Lists", Lists);
                return "brand/mainframe";
        }

        @GetMapping("/delframe/{id}")
        public String delframe(@PathVariable("id") Integer id){
                BrandRepos goods = brandReposRepository.findById(id).get();
                goods.setStatus("新入仓");
                brandReposRepository.save(goods);
                return "redirect:/brand/mainframe";
        }

        @GetMapping("/newframe")
        public String newframe(Model model){
                List<BrandRepos> Lists = brandReposRepository.findBrand_reposByStatusNot("新入仓");
                List<String> primaries = typeRepository.getPrimary();
                model.addAttribute("primaries", primaries);
                return "/brand/newframe";
        }

        @PostMapping("/addframe")
        public String addframe(BrandRepos goods){
                return "redirect:/brand/mainframe";
        }
}
