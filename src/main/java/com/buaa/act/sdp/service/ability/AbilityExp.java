package com.buaa.act.sdp.service.ability;

import com.buaa.act.sdp.dao.ChallengeItemDao;
import com.buaa.act.sdp.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.model.challenge.ChallengeItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 实验。给定项目是否能给出合适的开发者
 * Created by YLT on 2017/3/1.
 */
@Service
public class AbilityExp {

    @Autowired
    public UserAbility userAbility;
    @Autowired
    ChallengeRegistrantDao challengeRegistrantDao;
    @Autowired
    ChallengeItemDao challengeItemDao;

    public List<String> getTech(int itemId) {
        ChallengeItem item = challengeItemDao.getChallengeItemById(itemId);
        String tech[] = item.getTechnology();
        String platform[] = item.getPlatforms();
        List<String> technology = new ArrayList<String>();
        for (int i = 0; i < tech.length; i++) {
            technology.add(tech[i]);
        }
        for (int j = 0; j < platform.length; j++) {
            if (!technology.contains(platform[j])) {
                technology.add(platform[j]);
            }
        }
        return technology;
    }
}
