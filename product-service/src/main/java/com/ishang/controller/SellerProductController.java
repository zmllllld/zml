package com.ishang.controller;


import com.alibaba.excel.EasyExcel;
import com.ishang.entity.ProductInfo;
import com.ishang.form.SellerProductInfoForm;
import com.ishang.form.SellerProductInfoUpdateForm;
import com.ishang.handler.CustomCellWriteHandler;
import com.ishang.service.ProductCategoryService;
import com.ishang.service.ProductInfoService;
import com.ishang.util.ResultVOUtil;
import com.ishang.vo.ResultVO;
import com.ishang.vo.ProductExcelVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author zml
 * @since 2022-04-09
 */
@RestController
@RequestMapping("/seller/product")
public class SellerProductController {

    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductInfoService productInfoService;

    @ApiOperation("查询所有商品分类")
    @GetMapping("/findAllProductCategory")
    public ResultVO findAllProductCategory(){
        Map<String, List> map = new HashMap<>();
        map.put("content",this.productCategoryService.SellerList());
        return ResultVOUtil.success(map);
    }

    @ApiOperation("添加商品")
    @PostMapping("/add")
    public ResultVO add(@RequestBody SellerProductInfoForm sellerProductInfoForm){
        ProductInfo productInfo = new ProductInfo();
        BeanUtils.copyProperties(sellerProductInfoForm,productInfo);
        this.productInfoService.save(productInfo);
        return ResultVOUtil.success(null);

    }

    @ApiOperation("查询商品")
    @GetMapping("/list/{page}/{size}")
    public ResultVO list(@PathVariable("page") Integer page,@PathVariable("size") Integer size){
         return ResultVOUtil.success(this.productInfoService.list(page,size));
    }

    @ApiOperation("商品模糊查询")
    @GetMapping("/like/{keyWord}/{page}/{size}")
    public ResultVO like(@PathVariable("keyWord") String keyWord,
                         @PathVariable("page") Integer page,
                         @PathVariable("size") Integer size){
         return ResultVOUtil.success(this.productInfoService.like(keyWord, page, size));
    }

    @ApiOperation("通过分类查询商品")
    @GetMapping("/findByCategory/{categoryType}/{page}/{size}")
    public ResultVO findByCategory(@PathVariable("categoryType") Integer categoryType,
                                   @PathVariable("page") Integer page,
                                   @PathVariable("size") Integer size){
        return ResultVOUtil.success(this.productInfoService.findByCategory(categoryType, page, size));

    }

    @ApiOperation("通过ID查询商品")
    @GetMapping("/findById/{id}")
    public ResultVO findById(@PathVariable("id") Integer id){
        return ResultVOUtil.success(this.productInfoService.findById(id));
    }

    @ApiOperation("通过ID删除商品")
    @DeleteMapping("/delete/{id}")
    public ResultVO delete(@PathVariable("id") Integer id){
         this.productInfoService.removeById(id);
         return ResultVOUtil.success(null);
    }

    @ApiOperation("修改商品状态")
    @PutMapping("/updateStatus/{id}/{status}")
    public ResultVO updateStatus(@PathVariable("id") Integer id,@PathVariable("status") Boolean status){
        ProductInfo productInfo = new ProductInfo();
        productInfo.setProductId(id);
        if (status==true){
            productInfo.setProductStatus(1);
        }
        else {
            productInfo.setProductStatus(0);
        }
        this.productInfoService.updateById(productInfo);
        return ResultVOUtil.success(null);
    }

    @ApiOperation("修改商品")
    @PutMapping("/update")
    public ResultVO update(@RequestBody SellerProductInfoUpdateForm sellerProductInfoUpdateForm){
        ProductInfo productInfo = new ProductInfo();
        BeanUtils.copyProperties(sellerProductInfoUpdateForm,productInfo);
        if (sellerProductInfoUpdateForm.getStatus()==true){
            productInfo.setProductStatus(1);
        }else{
            productInfo.setProductStatus(0);
        }
        productInfo.setCategoryType(sellerProductInfoUpdateForm.getCategory().getCategoryType());
        this.productInfoService.updateById(productInfo);
        return ResultVOUtil.success(null);
    }

    @PostMapping("/import")
    public ResultVO importData(@RequestParam("file")MultipartFile file){
        List<ProductInfo> productInfoList = null;
        try {
            productInfoList = this.productInfoService.excelToProductInfoList(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(productInfoList==null){
            return ResultVOUtil.fail("导入Excel失败！");
        }
//        将list集合保存
        boolean result = this.productInfoService.saveBatch(productInfoList);
        if (result){
            return ResultVOUtil.success(null);
        }
        return ResultVOUtil.fail("导入Excel失败!");

    }

    @ApiOperation("导出Excel")
    @GetMapping("/export")
    public void export(HttpServletResponse response){
        try {
//            设置数据接收类型
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("UTF-8");
            String fileName = URLEncoder.encode("商品信息","UTF-8");
            response.setHeader("Content-disposition","attachment;filename="+fileName+".xlsx");
//          获取ProductExcelVO类型的list
            List<ProductExcelVO> list = this.productInfoService.excelVOList();
            EasyExcel.write(response.getOutputStream(), ProductExcelVO.class)
                    .registerWriteHandler(new CustomCellWriteHandler())
                    .sheet("商品信息")
                    .doWrite(list);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

