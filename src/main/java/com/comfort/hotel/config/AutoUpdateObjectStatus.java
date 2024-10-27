package com.comfort.hotel.config;

import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.service.CanteenService;
import com.comfort.hotel.service.GoodsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalTime;
import java.util.List;

/**
 * @Author: ayuan
 * @PS: 根据时间，检测当前时间，并自动更新食堂的状态
 * @Date: 2024/9/19 15:07
 */
@Component
public class AutoUpdateObjectStatus {

    @Resource
    private CanteenService canteenService;

    // 检查食堂的状态，是否需要进行更新 没一个小时触发一次定时任务
    @Scheduled(cron = "0 0 */1 * * ?")
    public void checkCanteenStatus() {
        System.out.println("食堂定时任务启动中.........");
        int updateCount = doneUpdateCanteenStatus();
        if (updateCount == Constants.ZERO) {
            System.out.println("当前没有任何数据发生变更..........");
        } else {
            System.out.println("已变更数据：" + updateCount + "条");
        }
        System.out.println("食堂定时任务结束.........");
    }

    // 做更新的判断，符合这改变状态，不符合则停止
    private int doneUpdateCanteenStatus() {
        // 查找所有的食堂id
        List<String> ids = canteenService.selectAllCanteenIds();
        int count = 0;
        for (String id : ids) {
            // 如果食堂当前的状态是 0，也就是暂停营业下,则不进行判断
            if (!canteenService.selectCanteenStatusById(id).equals(Constants.ZERO)) {
                // 查询当前食堂的营业时间
                LocalTime openTime = canteenService.selectOpenTimeById(id);
                // 查询当前食堂的打烊时间
                LocalTime closedTime = canteenService.selectClosedTimeById(id);
                // 获取当前的系统时间
                LocalTime now = LocalTime.now();
                // 如果当前系统时间大于营业时间，并且小于打烊时间，那么将现在的状态设置为营业
                if (now.isAfter(openTime) && now.isBefore(closedTime)) {
                    // 更新食堂营业状态
                     count += canteenService.updateCanteenOpenStatus(id, Constants.TWO);

                } else {
                    // 更新食堂打烊状态
                    count += canteenService.updateCanteenOpenStatus(id, Constants.ONE);
                }
            }

        }
            return count;
    }

}
